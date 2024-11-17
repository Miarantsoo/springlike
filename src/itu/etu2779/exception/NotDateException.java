package itu.etu2779.exception;

public class NotDateException extends Exception {

    public NotDateException(String message){
        super("L'attribut "+message+" n'est pas de type java.util.Date");
    }
    
}
