package com.Am.demo.controller;

import java.sql.Date;

import com.Am.demo.model.Utilisateur;
import com.Am.demo.model.Patient;
import com.Am.demo.model.PatientDTO;
import com.Am.demo.service.auth.Authservice;
import com.Am.demo.service.patient.Patientservice;
import jakarta.servlet.http.HttpServletRequest;
import mg.nathafw.annotation.MyController;
import mg.nathafw.annotation.Param;
import mg.nathafw.annotation.URLAnnotation;
import mg.nathafw.database.DatabaseManager;
import mg.nathafw.mapping.HTTPMethod;
import mg.nathafw.util.ModelView;

@MyController
public class PatientController {
    private final Patientservice patientservice = new Patientservice();
    private final Authservice authservice = new Authservice();

    /** Ajoute au profil la liste des dossiers destinés aux rôles de gestion. */
    @URLAnnotation(value = "/patients", httpMethod = HTTPMethod.GET)
    public ModelView listPatients(@Param("nom") String nom, @Param("idVital") String idVital,
            HttpServletRequest request, DatabaseManager dm) throws Exception {
        Object value = request.getSession(false) == null ? null : request.getSession(false).getAttribute("utilisateur");
        if (!(value instanceof Utilisateur utilisateur)) {
            return new ModelView("auth/login.jsp");
        }
        if (utilisateur.getRole() == null || "patient".equalsIgnoreCase(utilisateur.getRole().getLibelle())) {
            return new ModelView("auth/profile.jsp").add("utilisateur", utilisateur)
                    .add("isPatient", true).add("isStaff", false)
                    .add("patient", authservice.getPatient(dm, utilisateur.getId()));
        }

        var patients = patientservice.searchPatients(dm, nom, idVital);
        return new ModelView("auth/profile.jsp").add("utilisateur", utilisateur)
                .add("isPatient", false).add("isStaff", true).add("patients", patients)
                .add("searchNom", nom == null ? "" : nom)
                .add("searchIdVital", idVital == null ? "" : idVital);
    }

    @URLAnnotation(value = "/patient/detail", httpMethod = HTTPMethod.GET)
    public ModelView patientDetail(@Param("idUtilisateur") int idUtilisateur,
            HttpServletRequest request, DatabaseManager dm) throws Exception {
        Object value = request.getSession(false) == null ? null : request.getSession(false).getAttribute("utilisateur");
        if (!(value instanceof Utilisateur connectedUser) || connectedUser.getRole() == null
                || "patient".equalsIgnoreCase(connectedUser.getRole().getLibelle())) {
            return new ModelView("auth/login.jsp");
        }

        PatientDTO detail = patientservice.getPatientDetail(dm, idUtilisateur);
        if (detail == null) {
            return new ModelView("patient/detail.jsp")
                    .add("utilisateur", connectedUser)
                    .add("patientDetail", null)
                    .add("error", "Patient introuvable.");
        }
        return new ModelView("patient/detail.jsp")
                .add("utilisateur", connectedUser)
                .add("patientDetail", detail);
    }

    @URLAnnotation(value = "/patient/profile/edit", httpMethod = HTTPMethod.GET)
    public ModelView editPatientProfile(HttpServletRequest request, DatabaseManager dm) throws Exception {
        Object value = request.getSession(false) == null ? null : request.getSession(false).getAttribute("utilisateur");
        if (!(value instanceof Utilisateur utilisateur) || utilisateur.getRole() == null
                || !"patient".equalsIgnoreCase(utilisateur.getRole().getLibelle())) {
            return new ModelView("auth/login.jsp");
        }
        Patient patient = authservice.getPatient(dm, utilisateur.getId());
        if (patient == null) {
            patient = new Patient();
            patient.setIdUtilisateur(utilisateur.getId());
        }
        return new ModelView("patient/edit.jsp").add("utilisateur", utilisateur).add("patient", patient)
                .add("formAction", request.getContextPath() + (patient.getIdVital() == null
                        ? "/patient/create" : "/patient/profile/edit"));
    }

    @URLAnnotation(value = "/patient/profile/edit", httpMethod = HTTPMethod.POST)
    public ModelView updatePatientProfile(@Param("idVital") String idVital,
            @Param("dateNaissance") String dateNaissance, @Param("lieuNaissance") String lieuNaissance,
            @Param("genre") String genre, @Param("marie") boolean marie,
            HttpServletRequest request, DatabaseManager dm) throws Exception {
        Object value = request.getSession(false) == null ? null : request.getSession(false).getAttribute("utilisateur");
        if (!(value instanceof Utilisateur utilisateur) || utilisateur.getRole() == null
                || !"patient".equalsIgnoreCase(utilisateur.getRole().getLibelle())) {
            return new ModelView("auth/login.jsp");
        }
        Patient patient = new Patient();
        patient.setIdVital(idVital);
        patient.setIdUtilisateur(utilisateur.getId());
        patient.setDateNaissance(Date.valueOf(dateNaissance));
        patient.setLieuNaissance(lieuNaissance);
        patient.setGenre(genre);
        patient.setMarie(marie);
        patientservice.updateData(dm, patient);
        request.getSession(true).setAttribute("patientMessage", "Votre dossier a été mis à jour.");
        return new ModelView("auth/profile.jsp").add("utilisateur", utilisateur)
                .add("isPatient", true).add("isStaff", false)
                .add("patient", authservice.getPatient(dm, utilisateur.getId()));
    }

    @URLAnnotation(value = "/patient/create", httpMethod = HTTPMethod.GET)
    public ModelView createPatientForm(HttpServletRequest request) {
        Object value = request.getSession(false) == null ? null : request.getSession(false).getAttribute("utilisateur");
        if (!(value instanceof Utilisateur utilisateur)) {
            return new ModelView("auth/login.jsp");
        }
        Patient patient = new Patient();
        patient.setIdUtilisateur(utilisateur.getId());
        return new ModelView("patient/edit.jsp").add("utilisateur", utilisateur).add("patient", patient)
                .add("formAction", request.getContextPath() + "/patient/create");
    }

    @URLAnnotation(value = "/patient/create", httpMethod = HTTPMethod.POST)
    public ModelView createPatient(@Param("dateNaissance") String dateNaissance,
            @Param("lieuNaissance") String lieuNaissance, @Param("genre") String genre,
            @Param("marie") boolean marie,
            HttpServletRequest request, DatabaseManager dm) throws Exception {
        Object value = request.getSession(false) == null ? null : request.getSession(false).getAttribute("utilisateur");
        if (!(value instanceof Utilisateur utilisateur)) {
            return new ModelView("auth/login.jsp");
        }
        Patient patient = new Patient();
        patient.setIdUtilisateur(utilisateur.getId());
        patient.setDateNaissance(Date.valueOf(dateNaissance));
        patient.setLieuNaissance(lieuNaissance);
        patient.setGenre(genre);
        patient.setMarie(marie);
        patientservice.saveData(dm, patient);
        request.getSession(true).setAttribute("patientMessage", "Votre dossier a été créé.");
        return new ModelView("auth/profile.jsp").add("utilisateur", utilisateur)
                .add("isPatient", true).add("isStaff", false)
                .add("patient", patient);
    }
}
