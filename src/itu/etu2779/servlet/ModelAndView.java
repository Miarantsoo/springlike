package itu.etu2779.servlet;

import java.util.HashMap;

public class ModelAndView {
    
    private String url;
    private HashMap<String, Object> data = new HashMap<>();

    public ModelAndView(String url) {
        setUrl(url);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public HashMap<String, Object> getData() {
        return data;
    }

    public void setData(HashMap<String, Object> data) {
        this.data = data;
    }

    public void addObject(String cle, Object valeur){
        getData().put(cle, valeur);
    }
    
}
