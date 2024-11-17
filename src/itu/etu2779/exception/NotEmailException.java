package itu.etu2779.exception;

public class NotEmailException extends Exception{
    
    public NotEmailException (String message){
        super("La valeur de "+message+" doit representer un e-mail");
    }

}
