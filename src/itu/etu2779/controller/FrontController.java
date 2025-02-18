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
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;

import itu.etu2779.annotation.RestAPI;
import itu.etu2779.annotation.auth.Auth;
import itu.etu2779.annotation.auth.RoleType;
import itu.etu2779.exception.NotEmailException;
import itu.etu2779.exception.NotNumericException;
import itu.etu2779.exception.OutOfLengthException;
import itu.etu2779.exception.OutOfRangeException;
import itu.etu2779.exception.RequiredException;
import itu.etu2779.mapping.LoadController;
import itu.etu2779.mapping.Mapper;
import itu.etu2779.mapping.VerbMethod;
import itu.etu2779.servlet.CustomSession;
import itu.etu2779.servlet.ModelAndView;
import itu.etu2779.utils.Utilitaire;

public class FrontController extends HttpServlet {

    protected List<Class<?>> nomController = new ArrayList<>();
    protected HashMap<String, Mapper> mapping = new HashMap<>();

    private String initError = "";

    @Override
    public void init(){
        try {
            String controllerPackage = getServletConfig().getInitParameter("controllerChecker");
            LoadController.load(controllerPackage, nomController, mapping);
        } catch (ServletException e) {
            initError += e.getLocalizedMessage();
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

        if(!initError.equals("")){
            res.setContentType("text/html");
            res.setStatus(500);
            out.println("<h1>SpringLike Error</h1>");
            out.println("");
            out.println("<h3>Status: "+res.getStatus()+"</h3>");
            out.println("");
            out.println("");
            out.println("<p>Description: "+initError+"</p>");
            return;
        }

        String path = req.getRequestURI();

        if (path.contains("/assets/")) {
            RequestDispatcher defaultDispatcher = req.getServletContext().getNamedDispatcher("default");
            try {
                defaultDispatcher.forward(req, res);
            } catch (ServletException | IOException e) {
                res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur lors du rendu des ressources statiques");
            }
            return;
        }

        String[] parts = path.split("/");
        String urlTyped = parts[parts.length - 1];

        String getVerb = req.getMethod();
        if (req.getAttribute("error")!=null) {
            getVerb = "GET";
        }
        
        for (String cle : mapping.keySet()) {
            if (cle.equals(urlTyped)) {
                try {
                    Mapper map = mapping.get(urlTyped);
                    int nbrNonCorrelant = 0;
                    boolean contientDeuxVerbMethod = map.getVerbMethod().size() > 1 ? true : false;
                    for (VerbMethod vm : map.getVerbMethod()) {
                        if (!vm.getVerb().equals(getVerb)) {
                            nbrNonCorrelant += 1;
                        }
                    }

                    if (nbrNonCorrelant == 1 && !contientDeuxVerbMethod && req.getAttribute("error")==null) throw new ServletException("Methodes non correlants");  
                    HttpSession session = req.getSession();
                    CustomSession[] cs = new CustomSession[1];
                    Class<?> clazz = Class.forName(map.getNomClasse());

                    Method[] methods = clazz.getDeclaredMethods();
                    for (int i = 0; i < methods.length; i++) {
                        for (VerbMethod vm : map.getVerbMethod()) {
                            if (Utilitaire.memeMethode(methods[i], vm.getMethod())) {
                                if(methods[i].isAnnotationPresent(Auth.class)){
                                    String roleKey = getServletConfig().getInitParameter("role");
                                    String authKey = getServletConfig().getInitParameter("authentication");
                                    RoleType roleUser = roleKey != null ? (RoleType) session.getAttribute(roleKey) : null;
                                    Boolean auth = authKey != null ? (Boolean) session.getAttribute(authKey) : null;
                                    Auth authAnnotation = methods[i].getAnnotation(Auth.class);
                                    if (auth == null || !auth) {
                                        throw new ServletException("Vous devez être authentifié(e) pour acceder à cette méthode");
                                    } 
                                    if (roleUser == null || authAnnotation.value().level > roleUser.level) {
                                        throw new ServletException("Vous n'avez pas les droits nécessaires pour accéder à cette méthode");
                                    }
                                }
                                Object objet = Utilitaire.invokeMethod(clazz, methods[i], req, res, session, cs);
                                if (methods[i].isAnnotationPresent(RestAPI.class)) {
                                    res.setContentType("application/json");
                                    Gson gson = new Gson();
                                    if (objet instanceof ModelAndView) {
                                        ModelAndView model = (ModelAndView) objet;
                                        String json = gson.toJson(model.getData());
                                        out.println(json);
                                    } else {
                                        String json = gson.toJson(objet);
                                        out.println(json);
                                    }
                                    return;
                                } else {
                                    if (objet instanceof String) {
                                        String resultat = (String) objet;
                                        out.println(String.format("Resultat: %s", resultat));
        
                                        if (cs[0] != null) {
                                            cs[0].customToHttpSession(session);
                                        }
        
                                        return;
                                    } else if(objet instanceof ModelAndView) {                    
                                        ModelAndView model = (ModelAndView) objet;
                                        
                                        for (Map.Entry<String, Object> entry : model.getData().entrySet()) {
                                            req.setAttribute(entry.getKey(), entry.getValue());
                                        }
        
                                        if (cs[0] != null) {
                                            cs[0].customToHttpSession(session);
                                        }
        
                                        RequestDispatcher dispatcher = req.getRequestDispatcher(model.getUrl());
                                        dispatcher.forward(req, res);
                                        return;
                                    }
                                }
                            }
                        }
                    }
                    throw new ServletException("La valeur du type de retour de la fonction doit être de type String ou ModelAndView");
                } catch (ClassNotFoundException | ServletException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException | NoSuchMethodException | NotNumericException | NotEmailException | OutOfLengthException | OutOfRangeException | RequiredException e) {
                    res.setContentType("text/html");
                    res.setStatus(500);
                    out.println("<h1>SpringLike Error</h1>");
                    out.println("");
                    out.println("<h3>Status: "+res.getStatus()+"</h3>");
                    out.println("");
                    out.println("");
                    out.println("<p>Description: "+e.getLocalizedMessage()+"</p>");
                    return;
                } 
            }
        }
        // res.sendError(HttpServletResponse.SC_NOT_FOUND, "Pas d'URL trouve");
        res.setContentType("text/html");
        res.setStatus(404);
        out.println("<h1>SpringLike Error</h1>");
        out.println("");
        out.println("<h3>Status: "+res.getStatus()+"</h3>");
        out.println("");
        out.println("");
        out.println("<p>Description: Pas d'URL trouvé</p>");
    }
}
