package itu.etu2779.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import itu.etu2779.annotation.Param;

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

    public static boolean isAnObject(Class<?> parameterClass){
        String classType = parameterClass.getName();
        if (classType.equals("java.lang.String") || classType.equals("int") || classType.equals("double") || classType.equals("java.util.Date") ) {
            return false;
        }
        return true;
    }

    public static String getSetter(String field) {
        String result = "set";
        String var3 = field.substring(0, 1).toUpperCase();
        result = result + var3;
        result = result + field.substring(1);
        return result;
     }

    public static Object invokeMethod(Class<?> clazz, Method method, HttpServletRequest req) throws ServletException, IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException, SecurityException{
        Parameter[] param = method.getParameters();
        Class<?>[] parameterTypes = method.getParameterTypes();
        String [] name = parameterTypes.length != 0 ? new String[param.length] : null;
        Object[] values = parameterTypes.length != 0 ? new Object[param.length] : null;
        for (int j = 0; j < param.length; j++) {
            name[j] = param[j].getName();
            if (isAnObject(param[j].getType())) {
                Class<?> objectClass = param[j].getType();
                Object obj = objectClass.newInstance();
                Field[] fields = objectClass.getDeclaredFields();
                Object[] objectValues = new Object[fields.length];
                for (int i = 0; i < fields.length; i++) {
                    fields[i].setAccessible(true);
                    Class<?> fieldClass = fields[i].getType();
                    if (param[j].isAnnotationPresent(Param.class)) {
                        String annotationValue = param[j].getAnnotation(Param.class).name()+"."+fields[i].getName();
                        String parameter = req.getParameter(annotationValue);
                        objectValues[i] = Utilitaire.getRealParameterType(fieldClass, parameter);
                    } else {
                        String parameter = req.getParameter(name[j]+"."+fields[i].getName());
                        objectValues[i] = Utilitaire.getRealParameterType(fieldClass, parameter);
                    }
                    String setter = getSetter(fields[i].getName());
                    Method met = objectClass.getDeclaredMethod(setter, fieldClass);
                    met.invoke(obj, objectValues[i]);
                }
                values[j] = obj;
            } else {
                if (param[j].isAnnotationPresent(Param.class)) {
                    String annotationValue = param[j].getAnnotation(Param.class).name();
                    String parameter = req.getParameter(annotationValue);
                    values[j] = Utilitaire.getRealParameterType(parameterTypes[j], parameter);
                } else {
                    String parameter = req.getParameter(name[j]);
                    values[j] = Utilitaire.getRealParameterType(parameterTypes[j], parameter);
                }
            }
        }
        Object objet = method.invoke(clazz.newInstance(), values);
        return objet;
    }
}
