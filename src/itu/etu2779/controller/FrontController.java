package itu.etu2779.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import itu.etu2779.annotation.Param;
import itu.etu2779.mapping.LoadController;
import itu.etu2779.mapping.Mapper;
import itu.etu2779.servlet.ModelAndView;
import itu.etu2779.utils.Utilitaire;

public class FrontController extends HttpServlet {

    List<Class<?>> nomController = new ArrayList<>();
    HashMap<String, Mapper> mapping = new HashMap<>();

    @Override
    public void init() throws ServletException{
        String controllerPackage = getServletConfig().getInitParameter("controllerChecker");
        LoadController.load(controllerPackage, nomController, mapping);
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

        String path = req.getRequestURI();
        String[] parts = path.split("/");
        String urlTyped = parts[parts.length - 1];
        for (String cle : mapping.keySet()) {
            if (cle.equals(urlTyped)) {
                try {
                    Mapper map = mapping.get(urlTyped);
                    Class<?> clazz = Class.forName(map.getNomClasse());
                    Method[] methods = clazz.getDeclaredMethods();
                    for (int i = 0; i < methods.length; i++) {
                        if (methods[i].getName().equals(map.getNomMethode())) {
                            Parameter[] param = methods[i].getParameters();
                            Class<?>[] parameterTypes = methods[i].getParameterTypes();
                            String [] name = parameterTypes.length != 0 ? new String[param.length] : null;
                            Object[] values = parameterTypes.length != 0 ? new Object[param.length] : null;
                            for (int j = 0; j < param.length; j++) {
                                name[j] = param[j].getName();
                                if (param[j].isAnnotationPresent(Param.class)) {
                                    String annotationValue = param[j].getAnnotation(Param.class).name();
                                    String parameter = req.getParameter(annotationValue);
                                    values[j] = Utilitaire.getRealParameterType(parameterTypes[j], parameter);
                                } else {
                                    String parameter = req.getParameter(name[j]);
                                    values[j] = Utilitaire.getRealParameterType(parameterTypes[j], parameter);
                                }
                            }
        
                            Object objet = methods[i].invoke(clazz.newInstance(), values);
                            if (objet instanceof String) {
                                String resultat = (String) objet;
                                out.println(String.format("Resultat: %s", resultat));
                                return;
                            } else if(objet instanceof ModelAndView) {                    
                                out.print("foru");
                                ModelAndView model = (ModelAndView) objet;
                                
                                for (Map.Entry<String, Object> entry : model.getData().entrySet()) {
                                    req.setAttribute(entry.getKey(), entry.getValue());
                                }
                                RequestDispatcher dispatcher = req.getRequestDispatcher(model.getUrl());
                                dispatcher.forward(req, res);
                                return;
                            }
                        }
                    }
                    throw new ServletException("La valeur du type de retour de la fonction doit être de type String ou ModelAndView");
                } catch (ClassNotFoundException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException e) {
                    out.println(e);
                } 
            }
        }
        res.sendError(HttpServletResponse.SC_NOT_FOUND, "Pas d'URL trouve");
    }
}
