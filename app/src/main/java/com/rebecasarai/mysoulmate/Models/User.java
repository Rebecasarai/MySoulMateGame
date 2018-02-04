package com.rebecasarai.mysoulmate.Models;


/**
 * This class represents Users are they are stored in Firebase.
 */
public class User {

    private String uid;
    private String name;
    private String email;


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
}
