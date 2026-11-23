package mg.nathafw.mapping;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.io.IOException;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import jakarta.servlet.http.HttpServletRequest;
import mg.nathafw.database.DatabaseManager;
import jakarta.servlet.http.Part;
import mg.nathafw.annotation.APIAnnotation;
import mg.nathafw.annotation.MyController;
import mg.nathafw.annotation.URLAnnotation;
import mg.nathafw.annotation.Param;
import mg.nathafw.err.URLAlreadyDefinedException;
import mg.nathafw.err.URLNotSupportedException;
import mg.nathafw.util.interfaces.AnnotatedClassesProcessor;

public class URLProcessor implements AnnotatedClassesProcessor {

    private final List<Class<?>> controllerClasses = new ArrayList<>();
    private final HashMap<URLKey, URLControllerMap> urlMaps = new HashMap<>();
    private final HashMap<URLKey, URLControllerMap> apiMaps = new HashMap<>();

    @Override
    public void processAnnotatedClass(Class<?> clazz) throws ReflectiveOperationException, URLAlreadyDefinedException {
        if (clazz.isAnnotationPresent(MyController.class)) {
            controllerClasses.add(clazz);
            for (Method method : clazz.getDeclaredMethods()) {
                // Process API annotations
                if (method.isAnnotationPresent(APIAnnotation.class)) {
                    APIAnnotation apiAnnotation = method.getAnnotation(APIAnnotation.class);
                    if (apiAnnotation.enabled()) {
                        URLKey key = new URLKey(apiAnnotation.value(), apiAnnotation.httpMethod());
                        if (apiMaps.containsKey(key)) {
                            throw new URLAlreadyDefinedException(key, apiMaps.get(key));
                        } else {
                            apiMaps.put(key, new URLControllerMap(method, clazz));
                        }
                    }
                }
                // Process URL annotations
                if (method.isAnnotationPresent(URLAnnotation.class)) {
                    URLAnnotation urlAnnotation = method.getAnnotation(URLAnnotation.class);
                    URLKey key = new URLKey(urlAnnotation.value(), urlAnnotation.httpMethod());
                    if (urlMaps.containsKey(key)) {
                        throw new URLAlreadyDefinedException(key, urlMaps.get(key));
                    } else {
                        urlMaps.put(key, new URLControllerMap(method, clazz));
                    }
                }
            }
        }
    }

    public Object executeRequest(URLKey url, HttpServletRequest request)
            throws URLNotSupportedException, ReflectiveOperationException {
        URLControllerMap map = null;
        if (this.apiMaps.containsKey(url)) {
            map = this.apiMaps.get(url);
        }
        if (this.urlMaps.containsKey(url)) {
            map = this.urlMaps.get(url);
        }

        if (map != null) {
            Object[] params = resolveMethodArgument(map.getReflectMethod(), request);
            return map.getReflectMethod().invoke(map.getPrototypeSeed(), params);
        }

        throw new URLNotSupportedException(url, urlMaps);
    }

    private Object[] resolveMethodArgument(Method method, HttpServletRequest request) {
        Parameter[] parameters = method.getParameters();
        Object[] args = new Object[parameters.length];

        for (int i = 0; i < parameters.length; i++) {
            args[i] = resolveArgument(parameters[i], request);
        }

        return args;
    }

    private Object resolveArgument(Parameter param, HttpServletRequest request) {
        if (param.getType().equals(HttpServletRequest.class)) {
            return request;
        }

        if (param.getType().equals(DatabaseManager.class)) {
            Object databaseManager = request.getAttribute(DatabaseManager.REQUEST_ATTRIBUTE);
            if (databaseManager == null) {
                throw new IllegalStateException("Base non configurée. Définissez DB_URL dans web.xml.");
            }
            return databaseManager;
        }

        String bindingName = getParameterName(param);

        if (List.class.isAssignableFrom(param.getType())) {
            return resolveListArgument(param, bindingName, request);
        }

        if (isSimpleType(param.getType())) {
            String value = request.getParameter(bindingName);
            if (value == null && param.getType().isPrimitive()) {
                return convertType(null, param.getType());
            }
            if (value != null) {
                return convertType(value, param.getType());
            }
            return null;
        }

        try {
            return fillObject(param.getType(), request, bindingName);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la population de l'objet", e);
        }
    }

    private Object resolveListArgument(Parameter param, String bindingName, HttpServletRequest request) {
        Class<?> elementType = getListElementType(param);

        if (elementType == null) {
            return Collections.emptyList();
        }

        if (isSimpleType(elementType)) {
            String[] values = request.getParameterValues(bindingName);
            if (values == null || values.length == 0) {
                return Collections.emptyList();
            }

            List<Object> list = new ArrayList<>();
            for (String value : values) {
                list.add(convertType(value, elementType));
            }
            return list;
        }

        Map<Integer, Object> orderedElements = new HashMap<>();
        Enumeration<String> parameterNames = request.getParameterNames();

        while (parameterNames.hasMoreElements()) {
            String parameterName = parameterNames.nextElement();
            IndexedField indexedField = IndexedField.parseIndexedField(parameterName);
            if (indexedField == null) {
                continue;
            }

            if (!indexedField.prefix.equals(bindingName)) {
                continue;
            }

            Object element = orderedElements.get(indexedField.index);
            if (element == null) {
                element = instantiateType(elementType);
                orderedElements.put(indexedField.index, element);
            }
            if (indexedField.fieldName != null) {
                applyFieldValue(element, indexedField.fieldName, request.getParameter(parameterName));
            }
        }

        if (orderedElements.isEmpty()) {
            return Collections.emptyList();
        }

        List<Object> list = new ArrayList<>();
        for (Integer index : new TreeSet<>(orderedElements.keySet())) {
            list.add(orderedElements.get(index));
        }
        return list;
    }

    private Object fillObject(Class<?> clazz, HttpServletRequest request, String bindingPrefix) throws Exception {
        Object dtoInstance = null;
        try {
            dtoInstance = clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'instanciation de l'objet : Vérifier que la classe a un constructeur par défaut", e);
        }
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);

            String fieldName = field.getName();
            if (field.getType().equals(FileUpload.class)) {
                Part part = getUploadPart(request, bindingPrefix, fieldName);
                if (part != null && part.getSubmittedFileName() != null && !part.getSubmittedFileName().isEmpty()) {
                    field.set(dtoInstance, toFileUpload(part));
                }
                continue;
            }

            String rawValue = request.getParameter(bindingPrefix + "." + fieldName);
            if (rawValue == null) {
                rawValue = request.getParameter(fieldName);
            }

            if (rawValue != null) {
                Object convertedValue = convertType(rawValue, field.getType());
                field.set(dtoInstance, convertedValue);
            }
        }

        return dtoInstance;
    }

    private Part getUploadPart(HttpServletRequest request, String bindingPrefix, String fieldName) {
        try {
            Part part = request.getPart(bindingPrefix + "." + fieldName);
            if (part == null) {
                part = request.getPart(fieldName);
            }
            return part;
        } catch (IOException | jakarta.servlet.ServletException e) {
            throw new RuntimeException("Erreur lors de la récupération du fichier " + fieldName, e);
        }
    }

    private FileUpload toFileUpload(Part part) {
        try (var input = part.getInputStream()) {
            byte[] bytes = input.readAllBytes();
            Byte[] contenu = new Byte[bytes.length];
            for (int i = 0; i < bytes.length; i++) {
                contenu[i] = bytes[i];
            }
            return new FileUpload(part.getSubmittedFileName(), contenu);
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la lecture du fichier " + part.getSubmittedFileName(), e);
        }
    }

    private boolean isSimpleType(Class<?> type) {
        return type.isPrimitive()
                || type.equals(String.class)
                || Number.class.isAssignableFrom(type)
                || type.equals(Boolean.class)
                || type.equals(Character.class)
                || type.equals(Date.class)
                || type.equals(Timestamp.class)
                || type.equals(LocalDate.class)
                || type.equals(LocalDateTime.class);
    }

    private Class<?> getListElementType(Parameter param) {
        if (!(param.getParameterizedType() instanceof ParameterizedType)) {
            return null;
        }
        ParameterizedType parameterizedType = (ParameterizedType) param.getParameterizedType();

        if (parameterizedType.getActualTypeArguments().length != 1) {
            return null;
        }

        java.lang.reflect.Type elementType = parameterizedType.getActualTypeArguments()[0];
        if (elementType instanceof Class<?>) {
            return (Class<?>) elementType;
        }

        return null;
    }

    private Object instantiateType(Class<?> type) {
        try {
            return type.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'instanciation de la liste : vérifier que la classe a un constructeur par défaut", e);
        }
    }

    private void applyFieldValue(Object target, String fieldName, String rawValue) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);

            if (rawValue == null) {
                if (field.getType().isPrimitive()) {
                    field.set(target, convertType(null, field.getType()));
                }
                return;
            }

            field.set(target, convertType(rawValue, field.getType()));
        } catch (NoSuchFieldException ignored) {
            // Unknown field: ignore to keep binding permissive
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Erreur lors du binding d'un champ de liste", e);
        }
    }

    private String getParameterName(Parameter param) {
        if (param.isAnnotationPresent(Param.class)) {
            return param.getAnnotation(Param.class).value();
        }
        return param.getName();
    }

    

    private Object convertType(String value, Class<?> type) {
        if (value == null || value.isEmpty()) {
            if (type.isPrimitive()) {
                if (type == boolean.class) {
                    return false;
                }
                if (type == char.class) {
                    return '\0';
                }
                if (type == byte.class) {
                    return (byte) 0;
                }
                if (type == short.class) {
                    return (short) 0;
                }
                if (type == int.class) {
                    return 0;
                }
                if (type == long.class) {
                    return 0L;
                }
                if (type == float.class) {
                    return 0f;
                }
                if (type == double.class) {
                    return 0d;
                }
            }
            return null;
        }

        if (type == String.class) {
            return value;
        }

        if (type == char.class || type == Character.class) {
            return value.charAt(0);
        }

        if (type == byte.class || type == Byte.class) {
            return Byte.parseByte(value);
        }

        if (type == short.class || type == Short.class) {
            return Short.parseShort(value);
        }

        if (type == int.class || type == Integer.class) {
            return Integer.parseInt(value);
        }

        if (type == long.class || type == Long.class) {
            return Long.parseLong(value);
        }

        if (type == float.class || type == Float.class) {
            return Float.parseFloat(value);
        }

        if (type == double.class || type == Double.class) {
            return Double.parseDouble(value);
        }

        if (type == boolean.class || type == Boolean.class) {
            return Boolean.parseBoolean(value);
        }

        if (type == Date.class) {
            try {
                return Date.valueOf(value.trim());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Format de date invalide. Format attendu : yyyy-MM-dd", e);
            }
        }

        if (type == Timestamp.class) {
            try {
                return Timestamp.valueOf(value.trim().replace('T', ' '));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Format de date invalide. Format attendu : yyyy-MM-dd HH:mm:ss", e);
            }
        }

        if (type == LocalDate.class) {
            try {
                return LocalDate.parse(value.trim());
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Format de date invalide. Format attendu : yyyy-MM-dd", e);
            }
        }

        if (type == LocalDateTime.class) {
            try {
                return LocalDateTime.parse(value.trim().replace(' ', 'T'));
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException(
                        "Format de date invalide. Format attendu : yyyy-MM-ddTHH:mm:ss",
                        e);
            }
        }

        throw new IllegalArgumentException("Type de conversion non supporté: " + type.getName());
    }

    public List<Class<?>> getControllerClasses() {
        return controllerClasses;
    }

    public HashMap<URLKey, URLControllerMap> getUrlMaps() {
        return urlMaps;
    }

    public HashMap<URLKey, URLControllerMap> getApiMaps() {
        return apiMaps;
    }

    public boolean isAPIRequest(URLKey key) {
        return apiMaps.containsKey(key);
    }
}
