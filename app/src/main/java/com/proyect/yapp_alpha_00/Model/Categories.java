package com.proyect.yapp_alpha_00.Model;

public class Categories {

    private String cName;
    private String cImage;
    private boolean isChecked;

    public Categories(String cName, String cImage, boolean isChecked) {
        this.cName = cName;
        this.cImage = cImage;
    }

    public Categories(){

    }

    public String getcName() {
        return cName;
    }

    public void setcName(String cName) {
        this.cName = cName;
    }

    public String getcImage() {
        return cImage;
    }

    public void setcImage(String cImage) {
        this.cImage = cImage;
    }
}
