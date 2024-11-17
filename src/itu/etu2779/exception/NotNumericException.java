package itu.etu2779.exception;

public class NotNumericException extends Exception{
    
    public NotNumericException (String message){
        super("La valeur de "+message+" doit représenter une valeur numérique");
    }

}
