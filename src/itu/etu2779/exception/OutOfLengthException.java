package itu.etu2779.exception;

public class OutOfLengthException extends Exception {

    public OutOfLengthException(String className, String errorLoc, int length) {
        super(handleError(className, errorLoc, length)); 
    }

    private static String handleError(String className, String errorLoc, int length) {
        if (errorLoc.equals("CLASS")) {
            return "L'attribut " + className + " doit etre de type String";
        }
        return "La longueur de l'attribut " + className + " depasse les " + length + " lettres";
    }

}