package itu.etu2779.exception;

public class RequiredException extends Exception{
    public RequiredException(String message) {
        super("La valeur de l'attribut "+message+" est requise");
    }
}
