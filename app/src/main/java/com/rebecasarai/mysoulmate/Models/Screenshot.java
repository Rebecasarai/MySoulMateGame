package com.rebecasarai.mysoulmate.Models;

/**
 * Created by rebecagonzalez on 8/2/18.
 */

public class Screenshot {

    public String imageName;

    public String imageURL;

    public Screenshot() {

    }

    public Screenshot(String name, String url) {

        this.imageName = name;
        this.imageURL= url;
    }

    public Screenshot(String name) {

        this.imageName = name;
    }

    public String getImageName() {
        return imageName;
    }

    public String getImageURL() {
        return imageURL;
    }

}


