package com.example.oeuvre.Entities;

import java.util.List;

public class Galerie {
    private int id;
    private String name;
    private String description;
    private String address;
    private List<Oeuvre> oeuvres; // Une galerie contient une liste d'oeuvres

    public Galerie(int id, String name, String description, String address, List<Oeuvre> oeuvres) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.address = address;
        this.oeuvres = oeuvres;
    }

    public Galerie() {

    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<Oeuvre> getOeuvres() {
        return oeuvres;
    }

    public void setOeuvres(List<Oeuvre> oeuvres) {
        this.oeuvres = oeuvres;
    }

    @Override
    public String toString() {
        return name;
    }


}