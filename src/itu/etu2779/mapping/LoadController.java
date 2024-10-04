package itu.etu2779.mapping;

import java.net.URL;
import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;

import itu.etu2779.annotation.Controller;
import itu.etu2779.annotation.Get;
import itu.etu2779.annotation.Post;
import itu.etu2779.annotation.Url;
import itu.etu2779.utils.Utilitaire;

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
                boolean isGetNotPresent = Utilitaire.isGetNotPresent(method);
                String path = "";
                if(method.isAnnotationPresent(Url.class)){
                    Url url = method.getAnnotation(Url.class);
                    path = url.path();
                    if (path.equals("")) {
                        throw new ServletException("Redirection d'URL non trouvée");
                    }
                } else {
                    throw new ServletException("Redirection d'URL non trouvée");
                }
                if(method.isAnnotationPresent(Post.class)){
                    Mapper map = new Mapper(method.getDeclaringClass().getName(), method.getName(), Post.class);
                    if (!mapping.containsKey(path)) {
                        mapping.put(path, map);
                    } else {
                        throw new ServletException("Duplication d'URL trouvée");
                    }
                } else if(method.isAnnotationPresent(Get.class) || isGetNotPresent){
                    Mapper map = new Mapper(method.getDeclaringClass().getName(), method.getName(), Get.class);
                    if (!mapping.containsKey(path)) {
                        mapping.put(path, map);
                    } else {
                        throw new ServletException("Duplication d'URL trouvée");
                    }
                }
            }
        }
    }

}
