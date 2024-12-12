package itu.etu2779.utils;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import itu.etu2779.annotation.validation.Date;
import itu.etu2779.annotation.validation.Email;
import itu.etu2779.annotation.validation.Length;
import itu.etu2779.annotation.validation.Numeric;
import itu.etu2779.annotation.validation.Range;
import itu.etu2779.annotation.validation.Required;
import itu.etu2779.exception.NotDateException;
import itu.etu2779.exception.NotEmailException;
import itu.etu2779.exception.NotNumericException;
import itu.etu2779.exception.OutOfLengthException;
import itu.etu2779.exception.OutOfRangeException;
import itu.etu2779.exception.RequiredException;

public class ValidationUtils {

    private static boolean isEmail(String pseudoEmail) {
        String regex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(pseudoEmail);

        return matcher.matches();
    }

    private static boolean isNumericType(Object obj) {
        Class<?> clazz = obj.getClass();
        switch (clazz.getSimpleName()) {
            case "Integer":
                return true;
            case "Double":
                return true;
            case "int":
                return true;
            case "double":
                return true;
            default:
                return false;
        }
    }

    private static boolean isFieldNumericType(Field f) {
        Class<?> clazz = f.getType();
        switch (clazz.getSimpleName()) {
            case "Integer":
                return true;
            case "Double":
                return true;
            case "int":
                return true;
            case "double":
                return true;
            default:
                return false;
        }
    }


    public static void verifyRequired(Field field, Object obj) throws RequiredException {
        if (field.isAnnotationPresent(Required.class)) {
            field.setAccessible(true); 
            Object value = obj;

            if (value == null) {
                throw new RequiredException(field.getName());
            }

            if(field.getType().getName().equals("java.lang.String")) {
                if (value.equals("null")) {
                    throw new RequiredException(field.getName());
                }
            }

            if (field.getType().isPrimitive()) {
                if (field.getType() == int.class && (Integer)value == 0) {
                    throw new RequiredException(field.getName());
                } else if (field.getType() == boolean.class && (Boolean)value == false) {
                    throw new RequiredException(field.getName());
                } else if (field.getType() == double.class && (Double)value == 0.0) {
                    throw new RequiredException(field.getName());
                }
            }
        }
    }
    
    public static void verifyLength(Field field, Object obj) throws OutOfLengthException {
        if (field.isAnnotationPresent(Length.class)) {
            field.setAccessible(true); 
            Object value = obj;
            Length len = field.getAnnotation(Length.class);

            if (!value.getClass().equals(String.class)) {
                throw new OutOfLengthException(field.getName(), "CLASS", len.x());
            }

            if (((String) value).length() > len.x()) {
                throw new OutOfLengthException(field.getName(), "LENGTH", len.x());
            }
        }
    }

    public static void verifyRange(Field field, Object obj) throws OutOfRangeException {
        if (field.isAnnotationPresent(Range.class)) {
            field.setAccessible(true); 
            Object value = obj;
            Range ran = field.getAnnotation(Range.class);

            if (!isNumericType(value)) {
                throw new OutOfRangeException(field.getName(), "CLASS", ran.begin(), ran.end());
            }

            if (!((Double.valueOf((Integer) value)) > ran.begin() && ((Double.valueOf((Integer) value)) < ran.end()))) {
                throw new OutOfRangeException(field.getName(), "RANGE", ran.begin(), ran.end());
            }
        }
    }

    public static void verifyEmail(Field field, Object obj) throws NotEmailException {
        if (field.isAnnotationPresent(Email.class)) {
            field.setAccessible(true); 
                Object value = obj;

                if (!isEmail((String) value)) {
                    throw new NotEmailException(field.getName());
                }
        }
    }

    public static void verifyNumeric(Field field) throws NotNumericException {
        if (field.isAnnotationPresent(Numeric.class)) {
            field.setAccessible(true); 
            if (!isFieldNumericType(field)) {
                throw new NotNumericException(field.getName());
            }
        }
    }

    // public static void verifyDate(Object obj) throws NotDateException {
    //     Class<?> clazz = obj.getClass();
        
    //     for (Field field : clazz.getDeclaredFields()) {
    //         if (field.isAnnotationPresent(Date.class)) {
    //             field.setAccessible(true); 
    //             try {
    //                 Object value = field.get(obj);
    //                 Date format = field.getAnnotation(Date.class);
    //                 SimpleDateFormat sdf = new SimpleDateFormat(format.pattern());
    //                 java.util.Date date = sdf.parse((String) value);
    //             } catch (IllegalAccessException | ParseException e) {
    //                 throw new NotDateException(field.getName());
    //             }
    //         }
    //     }
    // }

    public static void verifyPreValidations(Field field) throws 
        NotNumericException
    {
        verifyNumeric(field);
    }
    
    public static void verifyValidations(Field f, Object obj) throws 
    NotEmailException, 
    OutOfLengthException, 
    OutOfRangeException, RequiredException
    {
        verifyRequired(f, obj);
        verifyEmail(f, obj);
        verifyLength(f, obj);
        verifyRange(f, obj);
    }

}
