package itu.etu2779.servlet;

import java.util.Enumeration;
import java.util.HashMap;

import javax.servlet.http.HttpSession;

public class CustomSession {
    
    private HashMap<String, Object> values = new HashMap<>();

    public HashMap<String,Object> getValues() {
        return this.values;
    }

    public void setValues(HashMap<String,Object> values) {
        this.values = values;
    }

    public void add(String key, Object value){
        getValues().put(key, value);
    }

    public Object get(String key) {
        return getValues().get(key);
    }

    public void update(String key, Object value){
        getValues().replace(key, value);
    }

    public void delete(String key){
        getValues().remove(key);
    }

    public void customToHttpSession(HttpSession session){
        Enumeration<String> attributeNames = session.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            String attribute = attributeNames.nextElement();
            session.removeAttribute(attribute);
        }        
        getValues().forEach((k, v) -> {
            session.setAttribute(k, v);
        });
    }

    public void httpSessionToCustom(HttpSession session){
        Enumeration<String> attributeNames = session.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            String attribute = attributeNames.nextElement();
            Object value = session.getAttribute(attribute);
            this.add(attribute, value);
        }
    }

}
