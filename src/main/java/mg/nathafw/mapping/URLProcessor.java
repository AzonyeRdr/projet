package mg.nathafw.mapping;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mg.nathafw.annotation.MyController;
import mg.nathafw.annotation.URLAnnotation;
import mg.nathafw.err.URLAlreadyDefinedException;
import mg.nathafw.err.URLNotSupportedException;
import mg.nathafw.util.interfaces.AnnotatedClassesProcessor;

public class URLProcessor implements AnnotatedClassesProcessor {

    private final List<Class<?>> controllerClasses = new ArrayList<>();
    private final HashMap<URLKey, URLControllerMap> urlMaps = new HashMap<>();

    @Override
    public void processAnnotatedClass(Class<?> clazz) throws ReflectiveOperationException, URLAlreadyDefinedException {
        if (clazz.isAnnotationPresent(MyController.class)) {
            controllerClasses.add(clazz);
            for (Method method : clazz.getDeclaredMethods()) {
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
        } else {
            return;
        }
    }

    public Object executeRequest(URLKey url)
            throws URLNotSupportedException, ReflectiveOperationException {
        if (!this.getUrlMaps().containsKey(url)) {
            throw new URLNotSupportedException(url, urlMaps);
        }
        URLControllerMap map = this.getUrlMaps().get(url);
        return map.getReflectMethod().invoke(map.getPrototypeSeed());
    }

    public List<Class<?>> getControllerClasses() {
        return controllerClasses;
    }

    public HashMap<URLKey, URLControllerMap> getUrlMaps() {
        return urlMaps;
    }
}
