package com.Am.demo.model;

import java.sql.Date;

/** Données d'identification d'un patient destinées aux vues de gestion. */
public class PatientDTO {
    private int idUtilisateur;
    private String nom;
    private String prenom;
    private String role;
    private String idVital;
    private Date dateNaissance;
    private String lieuNaissance;
    private String genre;
    private Boolean marie;

    public PatientDTO() {
    }

    public int getIdUtilisateur() {
        return idUtilisateur;
    }

    public void setIdUtilisateur(int idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getIdVital() {
        return idVital;
    }

    public void setIdVital(String idVital) {
        this.idVital = idVital;
    }

    public Date getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(Date dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public String getLieuNaissance() {
        return lieuNaissance;
    }

    public void setLieuNaissance(String lieuNaissance) {
        this.lieuNaissance = lieuNaissance;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public Boolean getMarie() {
        return marie;
    }

    public void setMarie(Boolean marie) {
        this.marie = marie;
    }
}
