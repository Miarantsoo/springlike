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
import javax.servlet.http.HttpSession;

import itu.etu2779.annotation.Param;
import itu.etu2779.servlet.CustomSession;

public class Utilitaire {
    
    public static Object getRealParameterType(Class<?> clazz, String parameter) throws ServletException{
        Object result = null;
        try {
            switch (clazz.getName()) {
                case "java.lang.String":
                    if (parameter.equals("")) {
                        result = "null";
                    } else {
                        result = parameter;
                    }
                    break;
                case "int":
                    if (parameter.equals("")) {
                        result = 0;
                    } else {
                        result = Integer.parseInt(parameter);
                    }
                    break;
                case "double":
                    if (parameter.equals("")) {
                        result = 0.0;
                    } else {
                        result = Double.parseDouble(parameter);
                    }
                    break;
                case "java.util.Date":
                    if(parameter.equals("")){
                        result = new Date();
                    } else {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        Date date = sdf.parse(parameter);
                        result = date;
                    }
                    break;
            }
            return result;
        } catch (ClassCastException | ParseException | NumberFormatException e) {
            throw new ServletException("Type de parametre incoherent avec sa valeur");
        }
    }

    public static boolean isAnObject(Class<?> parameterClass){
        if (parameterClass.equals(String.class) || parameterClass.equals(int.class) || parameterClass.equals(double.class) || parameterClass.equals(Date.class) || parameterClass.equals(CustomSession.class)) {
            return false;
        }
        return true;
    }

    public static boolean isASession(Class<?> parameterClass) {
        if (parameterClass.equals(CustomSession.class)) {
            return true;
        }
        return false;
    }

    public static String getSetter(String field) {
        String result = "set";
        String var3 = field.substring(0, 1).toUpperCase();
        result = result + var3;
        result = result + field.substring(1);
        return result;
     }

    public static boolean hasFieldOfType(Class<?> clazz, Class<?> fieldType) {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.getType().equals(fieldType)) {
                return true;
            }
        }
        return false;
    }

    public static Field getFieldOfType(Class<?> clazz, Class<?> fieldType) {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.getType().equals(fieldType)) {
                return field;
            }
        }
        return null;
    }

    public static Object invokeMethod(Class<?> clazz, Method method, HttpServletRequest req, HttpSession session, CustomSession[] cs) throws ServletException, IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException, SecurityException{
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
                        throw new ServletException("ETU002779 - Annotation @Param manquante");
                        // String parameter = req.getParameter(name[j]+"."+fields[i].getName());
                        // objectValues[i] = Utilitaire.getRealParameterType(fieldClass, parameter);
                    }
                    String setter = getSetter(fields[i].getName());
                    Method met = objectClass.getDeclaredMethod(setter, fieldClass);
                    met.invoke(obj, objectValues[i]);
                }
                values[j] = obj;
            } else if(isASession(param[j].getType())) {
                Class<?> objectClass = param[j].getType();
                CustomSession obj = (CustomSession) objectClass.newInstance();
                obj.httpSessionToCustom(session);
                cs[0] = obj;
                values[j] = obj;
            } else {
                if (param[j].isAnnotationPresent(Param.class)) {
                    String annotationValue = param[j].getAnnotation(Param.class).name();
                    String parameter = req.getParameter(annotationValue);
                    values[j] = Utilitaire.getRealParameterType(parameterTypes[j], parameter);
                } else {
                    throw new ServletException("ETU002779 - Annotation @Param manquante");
                    // String parameter = req.getParameter(name[j]);
                    // values[j] = Utilitaire.getRealParameterType(parameterTypes[j], parameter);
                }
            }
        }
        Object objet = method.invoke(clazz.newInstance(), values);
        return objet;
    }
}
