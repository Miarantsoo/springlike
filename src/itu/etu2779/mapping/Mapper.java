package itu.etu2779.mapping;

public class Mapper {
    
    String nomClasse;
    String nomMethode;
    Class<?> verb;

    public Mapper() {}

    public Mapper(String nomClasse, String nomMethode, Class<?> verb){
        setNomClasse(nomClasse);
        setNomMethode(nomMethode);
        setVerb(verb);
    }

    public String getNomClasse() {
        return this.nomClasse;
    }

    public void setNomClasse(String nomClasse) {
        this.nomClasse = nomClasse;
    }

    public String getNomMethode() {
        return this.nomMethode;
    }

    public void setNomMethode(String nomMethode) {
        this.nomMethode = nomMethode;
    }

    public Class<?> getVerb(){
        return this.verb;
    }

    public void setVerb(Class<?> verb){
        this.verb = verb;
    }
 
}
