package itu.etu2779.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import itu.etu2779.mapping.LoadController;
import itu.etu2779.mapping.Mapper;
import itu.etu2779.servlet.ModelAndView;

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
                    Method met = clazz.getDeclaredMethod(map.getNomMethode());
                    Object objet = met.invoke(clazz.newInstance());
                    if (objet instanceof String) {
                        String resultat = (String) objet;
                        out.println(String.format("Resultat: %s", resultat));
                        return;
                    } else if(objet instanceof ModelAndView) {
                        ModelAndView model = (ModelAndView) objet;
                        for (Map.Entry<String, Object> entry : model.getData().entrySet()) {
                            req.setAttribute(entry.getKey(), entry.getValue());
                        }
                        RequestDispatcher dispatcher = req.getRequestDispatcher(model.getUrl());
                        dispatcher.forward(req, res);
                        return;
                    }
                    throw new ServletException("La valeur du type de retour de la fonction doit être de type String ou ModelAndView");
                } catch (ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException e) {
                    out.println(e);
                } 
            }
        }
    }
}
