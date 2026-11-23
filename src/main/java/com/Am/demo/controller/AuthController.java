package com.Am.demo.controller;

import com.Am.demo.model.Role;
import com.Am.demo.model.Utilisateur;
import com.Am.demo.service.auth.Authservice;
import com.Am.demo.service.patient.Patientservice;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import mg.nathafw.annotation.MyController;
import mg.nathafw.annotation.Param;
import mg.nathafw.annotation.URLAnnotation;
import mg.nathafw.database.DatabaseManager;
import mg.nathafw.mapping.HTTPMethod;
import mg.nathafw.util.ModelView;

@MyController
public class AuthController {
    private final Authservice authservice = new Authservice();
    private final Patientservice patientservice = new Patientservice();

    @URLAnnotation(value = "/", httpMethod = HTTPMethod.GET)
    public ModelView home() {
        return new ModelView("home.jsp");
    }

    @URLAnnotation(value = "/auth/login", httpMethod = HTTPMethod.GET)
    public ModelView loginForm(DatabaseManager dm, HttpServletRequest request) throws Exception {
        ModelView view = new ModelView("auth/login.jsp");
        HttpSession session = request.getSession(false);
        if (session != null) {
            Object registrationMessage = session.getAttribute("registrationMessage");
            if (registrationMessage != null) {
                view.add("message", registrationMessage);
                session.removeAttribute("registrationMessage");
            }
        }
        return view;
    }

    @URLAnnotation(value = "/auth/login", httpMethod = HTTPMethod.POST)
    public ModelView login(@Param("id") int id,
            DatabaseManager dm, HttpServletRequest request) throws Exception {
        if (id <= 0) {
            return loginForm(dm, request).add("error", "Saisissez un identifiant valide.");
        }
        Utilisateur utilisateur = authservice.login(dm, id);
        if (utilisateur == null) {
            return loginForm(dm, request).add("error", "Identifiant introuvable.").add("id", id);
        }
        request.getSession(true).setAttribute("utilisateur", utilisateur);
        return profileView(dm, utilisateur);
    }

    @URLAnnotation(value = "/auth/register", httpMethod = HTTPMethod.GET)
    public ModelView registerForm(DatabaseManager dm) throws Exception {
        return new ModelView("auth/register.jsp").add("roles", authservice.listRoles(dm));
    }

    @URLAnnotation(value = "/auth/register", httpMethod = HTTPMethod.POST)
    public ModelView register(@Param("utilisateur") Utilisateur utilisateur,
            @Param("idRole") int idRole, DatabaseManager dm, HttpServletRequest request) throws Exception {
        String nom = utilisateur == null ? null : utilisateur.getNom();
        String prenom = utilisateur == null ? null : utilisateur.getPrenom();
        if (nom == null || nom.isBlank() || prenom == null || prenom.isBlank()) {
            ModelView view = registerForm(dm);
            return view.add("error", "Le nom et le prénom sont obligatoires.").add("nom", nom).add("prenom", prenom);
        }
        Role role = null;
        for (Role candidate : authservice.listRoles(dm)) {
            if (candidate.getId() == idRole)
                role = candidate;
        }
        if (role == null) {
            ModelView view = registerForm(dm);
            return view.add("error", "Choisissez un rôle valide.").add("nom", nom).add("prenom", prenom);
        }
        utilisateur.setNom(nom.trim());
        utilisateur.setPrenom(prenom.trim());
        utilisateur.setRole(role);
        authservice.signIn(dm, utilisateur);
        request.getSession(true).setAttribute("registrationMessage",
                "Compte créé. Votre identifiant est " + utilisateur.getId() + ".");
        return loginForm(dm, request);
    }

    @URLAnnotation(value = "/auth/profile", httpMethod = HTTPMethod.GET)
    public ModelView profile(HttpServletRequest request, DatabaseManager dm) throws Exception {
        Utilisateur utilisateur = currentUser(request);
        if (utilisateur == null)
            return loginForm(dm, request);
        return profileView(dm, utilisateur);
    }

    @URLAnnotation(value = "/auth/profile/edit", httpMethod = HTTPMethod.GET)
    public ModelView editForm(HttpServletRequest request, DatabaseManager dm) throws Exception {
        Utilisateur utilisateur = currentUser(request);
        return utilisateur == null ? loginForm(dm, request)
                : new ModelView("auth/edit-profile.jsp").add("utilisateur", utilisateur);
    }

    @URLAnnotation(value = "/auth/profile/edit", httpMethod = HTTPMethod.POST)
    public ModelView updateProfile(@Param("nom") String nom, @Param("prenom") String prenom,
            HttpServletRequest request, DatabaseManager dm) throws Exception {
        Utilisateur utilisateur = currentUser(request);
        if (utilisateur == null)
            return loginForm(dm, request);
        if (nom == null || nom.isBlank() || prenom == null || prenom.isBlank()) {
            return new ModelView("auth/edit-profile.jsp").add("utilisateur", utilisateur)
                    .add("error", "Le nom et le prénom sont obligatoires.").add("nom", nom).add("prenom", prenom);
        }
        utilisateur.setNom(nom.trim());
        utilisateur.setPrenom(prenom.trim());
        authservice.update(dm, utilisateur);
        return profileView(dm, utilisateur);
    }

    @URLAnnotation(value = "/auth/logout", httpMethod = HTTPMethod.GET)
    public ModelView logout(HttpServletRequest request, DatabaseManager dm) throws Exception {
        HttpSession session = request.getSession(false);
        if (session != null)
            session.invalidate();
        return loginForm(dm, request);
    }

    private Utilisateur currentUser(HttpServletRequest request) {
        Object value = request.getSession(false) == null ? null : request.getSession(false).getAttribute("utilisateur");
        return value instanceof Utilisateur utilisateur ? utilisateur : null;
    }

    private ModelView profileView(DatabaseManager dm, Utilisateur utilisateur) throws Exception {
        boolean isPatient = utilisateur.getRole() != null
                && "patient".equalsIgnoreCase(utilisateur.getRole().getLibelle());
        ModelView view = new ModelView("auth/profile.jsp")
                .add("utilisateur", utilisateur)
                .add("isPatient", isPatient)
                .add("isStaff", !isPatient);
        if (isPatient) {
            view.add("patient", authservice.getPatient(dm, utilisateur.getId()));
        } else {
            view.add("patients", patientservice.listPatients(dm));
        }
        return view;
    }

}
