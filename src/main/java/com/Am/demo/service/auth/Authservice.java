package com.Am.demo.service.auth;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.Am.demo.model.Patient;
import com.Am.demo.model.Role;
import com.Am.demo.model.Utilisateur;
import com.Am.demo.service.patient.Patientservice;
import com.Am.demo.util.Genutil;

import mg.nathafw.database.DatabaseManager;

public class Authservice {
    Patientservice ps = new Patientservice();

    /** Crée un utilisateur et renseigne son identifiant généré. */
    public Utilisateur signIn(DatabaseManager dm, Utilisateur utilisateur) throws Exception {
        if (dm == null || utilisateur == null) {
            throw new IllegalArgumentException("La base et l'utilisateur sont obligatoires");
        }
        if (utilisateur.getRole() == null || utilisateur.getRole().getId() <= 0) {
            throw new IllegalArgumentException("Un rôle valide est obligatoire");
        }
        utilisateur.setIdRole(utilisateur.getRole().getId());
        int id = Genutil.create(dm, "utilisateur", utilisateur, "id");
        utilisateur.setId(id);

        if ("patient".equalsIgnoreCase(utilisateur.getRole().getLibelle())) {
            Patient p = new Patient();
            p.setIdUtilisateur(id);

            ps.saveData(dm, p);
        }
        return utilisateur;
    }

    /** Recherche un utilisateur par identifiant. Retourne null s'il est absent. */
    public Utilisateur login(DatabaseManager dm, int id) throws SQLException, ReflectiveOperationException {
        if (dm == null) {
            throw new IllegalArgumentException("La base de données est obligatoire");
        }
        Utilisateur u = Genutil.findById(dm, "utilisateur", "id", id, Utilisateur.class);
        if (u != null) {
            loadRole(dm, u);
        }
        return u;

    }

    /** Enregistre les modifications du nom et du prénom. */
    public Utilisateur update(DatabaseManager dm, Utilisateur utilisateur) throws Exception {
        if (dm == null || utilisateur == null || utilisateur.getId() <= 0) {
            throw new IllegalArgumentException("La base et un utilisateur valide sont obligatoires");
        }
        int updated = Genutil.update(dm, "utilisateur", "id", utilisateur);
        return updated == 0 ? null : utilisateur;
    }

    public List<Role> listRoles(DatabaseManager dm) throws Exception {
        return Genutil.findAll(dm, "role", Role.class);
    }

    public Patient getPatient(DatabaseManager dm, int userId) throws Exception {
        return Genutil.findByColumn(dm, "patient", "idUtilisateur", userId, Patient.class);
    }

    private void loadRole(DatabaseManager dm, Utilisateur utilisateur) throws SQLException {
        String sql = "SELECT r.id, r.libelle FROM role r JOIN utilisateur u ON u.idRole = r.id WHERE u.id = ?";
        try (Connection connection = dm.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, utilisateur.getId());
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    Role role = new Role();
                    role.setId(rs.getInt("id"));
                    role.setLibelle(rs.getString("libelle"));
                    utilisateur.setRole(role);
                }
            }
        }
    }
}
