package itu.etu2779.mapping;

import java.net.URL;
import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;

import itu.etu2779.annotation.Controller;
import itu.etu2779.annotation.Get;

public class LoadController {
    
    public static void load(String controllerPackage, List<Class<?>> nomController, HashMap<String, Mapper> mapping) throws ServletException {
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            URL url = classLoader.getResource(controllerPackage);
            if (url == null) {
                throw new ServletException("Package not found");
            } 
            File directory = new File(url.getFile().replace("%20", " "));
            File[] files = directory.listFiles();
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".class")) {
                    String className = file.getName().substring(0, file.getName().lastIndexOf('.'));
                    Class<?> clazz = Class.forName(String.format("%s.%s", controllerPackage, className));
                    if (clazz.isAnnotationPresent(Controller.class)) {
                        nomController.add(clazz);
                    }
                }
            }
            if (nomController.size() == 0) {
                throw new ServletException("Contenu du package null");
            }
        } catch (NullPointerException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        for (Class<?> clazz : nomController) {
            Method [] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                if(method.isAnnotationPresent(Get.class)){
                    String url = method.getAnnotation(Get.class).path();
                    Mapper map = new Mapper(method.getDeclaringClass().getName(), method.getName());
                    if (!mapping.containsKey(url)) {
                        mapping.put(url, map);
                    } else {
                        throw new ServletException("Duplication d'URL trouvée");
                    }
                }
            }
        }
    }

}
