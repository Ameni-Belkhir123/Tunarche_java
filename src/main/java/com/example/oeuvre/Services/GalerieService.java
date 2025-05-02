package com.example.oeuvre.Services;

import com.example.oeuvre.DAOs.GalerieDAO;
import com.example.oeuvre.Entities.Galerie;
import com.example.oeuvre.Interfaces.IGalerieService;

import java.util.List;

public class GalerieService implements IGalerieService {
    private GalerieDAO galerieDAO = new GalerieDAO();

    @Override
    public void addGalerie(Galerie galerie) {
        galerieDAO.save(galerie);
    }

    @Override
    public void updateGalerie(Galerie galerie) {
        galerieDAO.update(galerie);
    }

    @Override
    public void deleteGalerie(int galerieId) {
        galerieDAO.delete(galerieId);
    }

    @Override
    public List<Galerie> getAllGaleries() {
        return galerieDAO.findAll();
    }

    @Override
    public Galerie getGalerieById(int galerieId) {
        return galerieDAO.findById(galerieId);
    }
}