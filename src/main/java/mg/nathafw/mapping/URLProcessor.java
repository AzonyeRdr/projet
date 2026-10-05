package mg.nathafw.mapping;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
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
            Parameter param = parameters[i];

            if (param.getType().equals(HttpServletRequest.class)) {
                args[i] = request;
                continue;
            }

            String paramName = getParameterName(param);
            String requestParamName = request.getParameter(paramName) != null ? request.getParameter(paramName) : null;

            if (isSimpleType(param.getType()) && requestParamName != null) {
                args[i] = convertType(requestParamName, param.getType());
                continue;
            }
            
            try {
                args[i] = fillObject(param.getType(), request);
            } catch (Exception e) {
                throw new RuntimeException("Erreur lors de la population de l'objet", e);
            }
        }

        return args;
    }

    private Object fillObject(Class<?> clazz, HttpServletRequest request) throws Exception {
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
            String rawValue = request.getParameter(fieldName); 

            if (rawValue != null) {
                Object convertedValue = convertType(rawValue, field.getType());
                field.set(dtoInstance, convertedValue);
            }
        }

        return dtoInstance;
    }

    private boolean isSimpleType(Class<?> type) {
        return type.isPrimitive()
                || type.equals(String.class)
                || Number.class.isAssignableFrom(type)
                || type.equals(Boolean.class)
                || type.equals(Character.class);
    }

    private String getParameterName(Parameter param) {
        if (param.isAnnotationPresent(Param.class)) {
            return param.getAnnotation(Param.class).value();
        }
        return param.getName();
    }

    private Object convertType(String value, Class<?> type) {
        if (value == null || value.isEmpty()) {
            return null;
        }

        if (type == String.class) {
            return value;
        }

        if (type == int.class || type == Integer.class) {
            return Integer.parseInt(value);
        }

        if (type == double.class || type == Double.class) {
            return Double.parseDouble(value);
        }

        if (type == boolean.class || type == Boolean.class) {
            return Boolean.parseBoolean(value);
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
