package com.Am.demo.model;

import java.sql.Date;

public class Patient {
    String idVital, lieuNaissance, genre;
    int idUtilisateur;
    Date dateNaissance;
    Boolean marie;

    public Patient() {
    }

    public Patient(Date dateNaissance, int idUtilisateur, String idVital, String lieuNaissance, Boolean marie,
            String genre) {
        this.dateNaissance = dateNaissance;
        this.genre = genre;
        this.idUtilisateur = idUtilisateur;
        this.idVital = idVital;
        this.lieuNaissance = lieuNaissance;
        this.marie = marie;
    }

    public String getIdVital() {
        return idVital;
    }

    public void setIdVital(String idVital) {
        this.idVital = idVital;
    }

    public int getIdUtilisateur() {
        return idUtilisateur;
    }

    public void setIdUtilisateur(int idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
    }

    public String getLieuNaissance() {
        return lieuNaissance;
    }

    public void setLieuNaissance(String lieuNaissance) {
        this.lieuNaissance = lieuNaissance;
    }

    public Date getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(Date dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public Boolean getMarie() {
        return marie;
    }

    public void setMarie(Boolean marie) {
        this.marie = marie;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }
}
