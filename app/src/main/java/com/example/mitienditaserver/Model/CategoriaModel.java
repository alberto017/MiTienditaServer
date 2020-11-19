package com.example.mitienditaserver.Model;

public class CategoriaModel {

    private String Name;
    private String Image;
    private String Status;

    public CategoriaModel() {
    }

    public CategoriaModel(String name, String image) {
        Name = name;
        Image = image;
    }

    public CategoriaModel(String name, String image, String status) {
        Name = name;
        Image = image;
        Status = status;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }
}//CategoriaModel

