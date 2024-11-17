package itu.etu2779.exception;

public class OutOfRangeException extends Exception{

    public OutOfRangeException(String className, String errorLoc, double debut, double fin){
        super(handleError(className, errorLoc, debut, fin));
    }

    private static String handleError(String className, String errorLoc, double debut, double fin){
        if(errorLoc.equals("CLASS")) 
            return "L'attribut "+className+" doit etre de type int/double ou Integer/Double";
        return "La valeur de l'attribut "+className+" doit etre entre "+debut+" et "+fin;
    }
    
}
