package com.example.chadyeo.animetv.api;


import java.io.Serializable;

public class Studio implements Serializable {
    private int id;
    private String studio_name;

    private int main_studio;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStudio_name() {
        return studio_name;
    }

    public void setStudio_name(String studio_name) {
        this.studio_name = studio_name;
    }

    public int getMain_studio() {
        return main_studio;
    }

    public void setMain_studio(int main_studio) {
        this.main_studio = main_studio;
    }
}
