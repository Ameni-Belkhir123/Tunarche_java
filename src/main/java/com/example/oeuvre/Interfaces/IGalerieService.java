package com.example.oeuvre.Interfaces;

import com.example.oeuvre.Entities.Galerie;

import java.util.List;

public interface IGalerieService {
    void addGalerie(Galerie galerie);

    void updateGalerie(Galerie galerie);

    void deleteGalerie(int galerieId);

    List<Galerie> getAllGaleries();

    Galerie getGalerieById(int galerieId);
}
