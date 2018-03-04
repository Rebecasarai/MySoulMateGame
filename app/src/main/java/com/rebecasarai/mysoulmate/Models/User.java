package com.rebecasarai.mysoulmate.Models;


import java.util.List;

/**
 * Esta clase representa Usuarios son almacenados en Firebase.
 */
public class User {

    private String uid;
    private String name;
    private String email;
    private List<String> screenshots;



    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getScreenshots() {
        return screenshots;
    }

    public void setScreenshots(List<String> screenshots) {
        this.screenshots = screenshots;
    }
}
