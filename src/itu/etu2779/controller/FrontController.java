package itu.etu2779.controller;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import itu.etu2779.annotation.Controller;
import itu.etu2779.annotation.Get;
import itu.etu2779.mapping.Mapper;

public class FrontController extends HttpServlet {

    List<Class<?>> nomController = new ArrayList<>();
    HashMap<String, Mapper> mapping = new HashMap<>();

    @Override
    public void init() {
        try {
            String controllerPackage = getServletConfig().getInitParameter("controllerChecker");
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            URL url = classLoader.getResource(controllerPackage);
            if (url != null) {
                File directory = new File(url.getFile().replace("%20", " "));
                if (directory.exists() && directory.isDirectory()) {
                    File[] files = directory.listFiles();
                    if (files != null) {
                        for (File file : files) {
                            if (file.isFile() && file.getName().endsWith(".class")) {
                                String className = file.getName().substring(0, file.getName().lastIndexOf('.'));
                                Class<?> clazz = Class.forName(String.format("%s.%s", controllerPackage, className));
                                if (clazz.isAnnotationPresent(Controller.class)) {
                                    nomController.add(clazz);
                                }
                            }
                        }
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        for (Class<?> clazz : nomController) {
            Method [] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                if(method.isAnnotationPresent(Get.class)){
                    String url = method.getAnnotation(Get.class).path();
                    Mapper map = new Mapper(method.getDeclaringClass().getName(), method.getName());
                    mapping.put(url, map);
                }
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        processRequest(req, res);
    }

    @Override 
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException{
        processRequest(req, res);
    }

    protected void processRequest(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("text/plain");
        PrintWriter out = res.getWriter();

        Boolean present = false;
        String path = req.getRequestURI();
        String[] parts = path.split("/");
        String urlTyped = parts[parts.length - 1];
        for (String cle : mapping.keySet()) {
            if (cle.equals(urlTyped)) {
                out.println(String.format("url: %s", urlTyped));
                Mapper map = mapping.get(urlTyped);
                out.println(String.format("nom classe: %s", map.getNomClasse()));
                out.println(String.format("nom methode: %s", map.getNomMethode()));
                present = true;
            }
        }
        if (!present) {
            out.println("Pas de methode associé à ce chemin: "+ urlTyped);
        }
    }
}
