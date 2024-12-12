package itu.etu2779.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import itu.etu2779.annotation.Get;
import itu.etu2779.annotation.Param;
import itu.etu2779.exception.NotEmailException;
import itu.etu2779.exception.NotNumericException;
import itu.etu2779.exception.OutOfLengthException;
import itu.etu2779.exception.OutOfRangeException;
import itu.etu2779.exception.RequiredException;
import itu.etu2779.mapping.VerbMethod;
import itu.etu2779.servlet.CustomSession;
import itu.etu2779.servlet.ModelAndView;

public class Utilitaire {

    public static boolean isGetNotPresent(Method m){
        Annotation [] annotations = m.getAnnotations();
        for(Annotation annotation: annotations){
            if (annotation.annotationType().equals(Get.class)) {
                return false;
            }
        }
        return true;
    }

    public static String methodNameFromSet(Set<VerbMethod> verbMethod) {
        String result = "";
        Iterator<VerbMethod> iterator = verbMethod.iterator();
        if (iterator.hasNext()) {
            VerbMethod element = iterator.next();
            result += element.getMethod();
        }
        return result;
    }
    
    public static Object getRealParameterType(Class<?> clazz, String parameter) throws ServletException, InstantiationException, IllegalAccessException{
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
                case "java.lang.Integer":
                case "int":
                    if (parameter.equals("")) {
                        result = 0;
                    } else {
                        result = Integer.parseInt(parameter);
                    }
                    break;
                case "java.lang.Double":
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
    
    public static Object getRealParameterType(Class<?> clazz, String annotationValue, String parameter, Field field, Object obj, HashMap<String, String[]> rTf) throws ServletException, InstantiationException, IllegalAccessException{
        Object result = null;
        try {
            try {
                ValidationUtils.verifyPreValidations(field);
            } catch (NotNumericException e) {
                String[] details = new String[2];
                details[0] = parameter;
                details[1] = e.getMessage();
                rTf.put(annotationValue, details);
            }
            
            switch (clazz.getName()) {
                case "java.lang.String":
                    if (parameter.equals("")) {
                        result = "null";
                    } else {
                        result = parameter;
                    }
                    break;
                case "java.lang.Integer":
                case "int":
                    if (parameter.equals("")) {
                        result = 0;
                    } else {
                        result = Integer.parseInt(parameter);
                    }
                    break;
                case "java.lang.Double":
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

            try {
                ValidationUtils.verifyValidations(field, result);
            } catch (NotEmailException | OutOfLengthException | OutOfRangeException | RequiredException e) {
                String[] details = new String[2];
                details[0] = parameter;
                details[1] = e.getMessage();
                rTf.put(annotationValue, details);
            }
            return result;
        } catch (ClassCastException | ParseException | NumberFormatException e) {
            throw new ServletException("Une erreur est survenue durant le procede");
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

    public static boolean memeMethode(Method method1, Method method2) {
        if (!method1.getName().equals(method2.getName())) {
            return false;
        }
        if (!method1.getReturnType().equals(method2.getReturnType())) {
            return false;
        }
        Class<?>[] paramTypes1 = method1.getParameterTypes();
        Class<?>[] paramTypes2 = method2.getParameterTypes();
        if (paramTypes1.length != paramTypes2.length) {
            return false;
        }
        for (int i = 0; i < paramTypes1.length; i++) {
            if (!paramTypes1[i].equals(paramTypes2[i])) {
                return false;
            }
        }
        return true;
    }

    private static String getFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        String[] tokens = contentDisp.split(";");
        for (String token : tokens) {
            if (token.trim().startsWith("filename")) {
                return token.substring(token.indexOf("=") + 2, token.length()-1);
            }
        }
        return "";
    }

    private static byte[] getFileBytes(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
        }
        return output.toByteArray();
    }

    public static Object invokeMethod(Class<?> clazz, Method method, HttpServletRequest req, HttpServletResponse resp, HttpSession session, CustomSession[] cs) throws 
        ServletException,
        IllegalAccessException, 
        InvocationTargetException, 
        InstantiationException, 
        NoSuchMethodException, 
        SecurityException, 
        IOException, 
        NotNumericException, 
        NotEmailException, 
        OutOfLengthException, 
        OutOfRangeException, 
        RequiredException
    {
        Parameter[] param = method.getParameters();
        Class<?>[] parameterTypes = method.getParameterTypes();
        String [] name = parameterTypes.length != 0 ? new String[param.length] : null;
        Object[] values = parameterTypes.length != 0 ? new Object[param.length] : null;
        HashMap<String, String[]> redirectToForm = new HashMap<>(); 
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
                        String annotationValue = param[j].getAnnotation(Param.class).name()+".";
                        if (fields[i].getName().contains("fileName")) {
                            annotationValue += fields[i].getName().split("fileName")[1];
                            Part filePart = req.getPart(annotationValue);
                            String fileName = Utilitaire.getFileName(filePart);
                            objectValues[i] = Utilitaire.getRealParameterType(fieldClass, annotationValue, fileName, fields[i], obj, redirectToForm);
                        } else if (fields[i].getName().contains("bytes")) {
                            annotationValue += fields[i].getName().split("bytes")[1];
                            Part filePart = req.getPart(annotationValue);
                            InputStream fileContent = filePart.getInputStream();
                            byte[] fileBytes = Utilitaire.getFileBytes(fileContent);
                            objectValues[i] = fileBytes;
                        } else {
                            annotationValue += fields[i].getName();
                            String parameter = req.getParameter(annotationValue);
                            objectValues[i] = Utilitaire.getRealParameterType(fieldClass, annotationValue, parameter, fields[i], obj, redirectToForm);
                        }
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
        if (redirectToForm.size() > 0) {
            String header = ((ModelAndView) objet).getError();
            RequestDispatcher dispatcher = req.getRequestDispatcher(header);
            
            req.setAttribute("error", redirectToForm);
            dispatcher.forward(req, resp);
            return null;
        }
        return objet;
    }
}
