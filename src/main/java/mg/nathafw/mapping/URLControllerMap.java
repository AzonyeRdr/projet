package mg.nathafw.mapping;

import java.lang.reflect.Method;

public class URLControllerMap {

    private Class<?> controllerClasses;
    private Method reflectMethod;

    public URLControllerMap(Method reflectMethod, Class<?> controllerClasses) {
        this.reflectMethod = reflectMethod;
        this.controllerClasses = controllerClasses;
    }

    public Class<?> getControllerClasses() {
        return controllerClasses;
    }

    public void setControllerClasses(Class<?> controllerClasses) {
        this.controllerClasses = controllerClasses;
    }

    public Method getReflectMethod() {
        return reflectMethod;
    }

    public void setReflectMethod(Method reflectMethod) {
        this.reflectMethod = reflectMethod;
    }

    public Object getPrototypeSeed() throws ReflectiveOperationException {
        return controllerClasses.getConstructor().newInstance();
    }

    @Override
    public String toString() {
        return "URLControllerMap [method=" + reflectMethod + ", controllerClasses=" + controllerClasses + "]";
    }
}
