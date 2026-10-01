package mg.nathafw.mapping;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mg.nathafw.annotation.APIAnnotation;
import mg.nathafw.annotation.MyController;
import mg.nathafw.annotation.URLAnnotation;
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

    public Object executeRequest(URLKey url)
            throws URLNotSupportedException, ReflectiveOperationException {
        if (this.apiMaps.containsKey(url)) {
            URLControllerMap map = this.apiMaps.get(url);
            return map.getReflectMethod().invoke(map.getPrototypeSeed());
        }
        if (this.urlMaps.containsKey(url)) {
            URLControllerMap map = this.urlMaps.get(url);
            return map.getReflectMethod().invoke(map.getPrototypeSeed());
        }
        throw new URLNotSupportedException(url, urlMaps);
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
