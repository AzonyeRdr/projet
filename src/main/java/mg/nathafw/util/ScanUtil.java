package mg.nathafw.util;

import java.io.File;
import java.net.URL;
import java.util.Enumeration;

import mg.nathafw.mapping.URLProcessor;
import mg.nathafw.util.interfaces.AnnotatedClassesProcessor;

public class ScanUtil {

    public static void handleAnnotatedClasses(
            String packageName,
            AnnotatedClassesProcessor processor) throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = packageName == null ? "" : packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);

        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            if (!"file".equals(resource.getProtocol())) {
                continue;
            }
            File directory = new File(resource.toURI());
            scanDirectory(directory,
                    packageName == null ? "" : packageName,
                    classLoader,
                    processor);
        }
    }

    public static void fillURLProcessor(String packageName, URLProcessor processor) throws Exception {
        handleAnnotatedClasses(packageName, processor);
    }

    private static void scanDirectory(
            File directory,
            String packageName,
            ClassLoader classLoader,
            AnnotatedClassesProcessor processor) throws Exception {

        File[] files = directory.listFiles();
        if (files == null) {
            return;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                String subPackage = packageName.isEmpty() 
                    ? file.getName() 
                    : packageName + "." + file.getName();
                scanDirectory(file, subPackage, classLoader, processor);
            } else if (file.getName().endsWith(".class")) {
                String className = packageName.isEmpty()
                    ? file.getName().substring(0, file.getName().length() - 6)
                    : packageName + "." + file.getName().substring(0, file.getName().length() - 6);
                
                try {
                    Class<?> clazz = Class.forName(className);
                    processor.processAnnotatedClass(clazz);
                } catch (ClassNotFoundException e) {
                }
            }
        }
    }
}
