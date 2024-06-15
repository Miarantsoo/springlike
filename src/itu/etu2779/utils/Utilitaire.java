package itu.etu2779.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;

public class Utilitaire {
    
    public static Object getRealParameterType(Class<?> clazz, String parameter) throws ServletException{
        Object result = null;
        try {
            switch (clazz.getName()) {
                case "java.lang.String":
                    result = parameter;
                    break;
                case "int":
                    result = Integer.parseInt(parameter);
                    break;
                case "double":
                    result = Double.parseDouble(parameter);
                    break;
                case "java.util.Date":
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date date = sdf.parse(parameter);
                    result = date;
                    break;
            }
            return result;
        } catch (ClassCastException | ParseException e) {
            throw new ServletException("Type de parametre incoherent avec sa valeur");
        }
    }
}
