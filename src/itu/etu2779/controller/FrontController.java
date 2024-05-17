package itu.etu2779.controller;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import itu.etu2779.annotation.Controller;

public class FrontController extends HttpServlet {

    boolean checked = false ;
    List<String> nomController = new ArrayList<>();

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

         if (!checked) {
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
                                        nomController.add(clazz.getName());
                                    }
                                }
                            }
                        }
                    }
                }
                checked = true;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        if (checked){
            for (int i = 0; i < nomController.size(); i++) {
                out.println(nomController.get(i));
            }
        }
    }
}
