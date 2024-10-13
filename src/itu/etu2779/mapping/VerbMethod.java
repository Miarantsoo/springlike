package itu.etu2779.mapping;

import java.lang.reflect.Method;

public class VerbMethod {
    
    private String verb;
    private Method method;
    
    public VerbMethod() {
    }

    public VerbMethod(String verb, Method method) {
        this.setVerb(verb);
        this.setMethod(method);
    }
    
    public String getVerb() {
        return verb;
    }

    public void setVerb(String verb) {
        this.verb = verb;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }
    
}
