package com.rebecasarai.mysoulmate.Models;

/**
 * Created by rebecagonzalez on 8/2/18.
 */

public class Screenshot {

    public String imageURL;

    public Screenshot() {
    }

    public Screenshot(String url) {
        this.imageURL = url;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}


