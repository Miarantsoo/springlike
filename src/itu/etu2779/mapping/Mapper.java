package itu.etu2779.mapping;

import java.util.HashSet;
import java.util.Set;

public class Mapper {
    
    private String nomClasse;
    private Set<VerbMethod> verbMethod; 

    public Mapper(String nomClasse) {
        setNomClasse(nomClasse);
        setVerbMethod(new HashSet<>());
    }

    public void addVerbMethod(VerbMethod vm) {
        verbMethod.add(vm);
    }

    public String getNomClasse() {
        return this.nomClasse;
    }

    public void setNomClasse(String nomClasse) {
        this.nomClasse = nomClasse;
    }

    public Set<VerbMethod> getVerbMethod() {
        return verbMethod;
    }

    public void setVerbMethod(Set<VerbMethod> verbMethod) {
        this.verbMethod = verbMethod;
    }
 
}
