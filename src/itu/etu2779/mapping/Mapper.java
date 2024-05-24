package itu.etu2779.mapping;

public class Mapper {
    
    String nomClasse;
    String nomMethode;

    public Mapper() {}

    public Mapper(String nomClasse, String nomMethode){
        setNomClasse(nomClasse);
        setNomMethode(nomMethode);
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

}
