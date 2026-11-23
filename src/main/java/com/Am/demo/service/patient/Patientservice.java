package com.Am.demo.service.patient;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;
import java.util.UUID;

import com.Am.demo.model.Patient;
import com.Am.demo.model.PatientDTO;
import com.Am.demo.util.Genutil;

import mg.nathafw.database.DatabaseManager;

public class Patientservice {
    public List<PatientDTO> listPatients(DatabaseManager dm) throws SQLException {
        return searchPatients(dm, null, null);
    }

    public List<PatientDTO> searchPatients(DatabaseManager dm, String nom, String idVital) throws SQLException {
        if (dm == null) {
            throw new IllegalArgumentException("La base de données est obligatoire");
        }

        List<PatientDTO> patients = new ArrayList<>();
        String sql = "SELECT u.id AS idUtilisateur, u.nom, u.prenom, p.idVital "
                + "FROM utilisateur u JOIN role r ON r.id = u.idRole "
                + "LEFT JOIN patient p ON p.idUtilisateur = u.id "
                + "WHERE LOWER(r.libelle) = 'patient'";

        List<String> filters = new ArrayList<>();
        if (nom != null && !nom.trim().isEmpty()) {
            sql += " AND (LOWER(u.nom) LIKE ? OR LOWER(u.prenom) LIKE ?)";
            filters.add("%" + nom.trim().toLowerCase() + "%");
            filters.add("%" + nom.trim().toLowerCase() + "%");
        }
        if (idVital != null && !idVital.trim().isEmpty()) {
            sql += " AND LOWER(COALESCE(p.idVital, '')) LIKE ?";
            filters.add("%" + idVital.trim().toLowerCase() + "%");
        }
        sql += " ORDER BY u.nom, u.prenom";

        try (Connection connection = dm.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            for (int i = 0; i < filters.size(); i++) {
                statement.setString(i + 1, filters.get(i));
            }
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    PatientDTO patient = new PatientDTO();
                    patient.setIdUtilisateur(rs.getInt("idUtilisateur"));
                    patient.setNom(rs.getString("nom"));
                    patient.setPrenom(rs.getString("prenom"));
                    patient.setIdVital(rs.getString("idVital"));
                    patients.add(patient);
                }
            }
        }
        return patients;
    }

    public PatientDTO getPatientDetail(DatabaseManager dm, int idUtilisateur) throws SQLException {
        if (dm == null || idUtilisateur <= 0) {
            throw new IllegalArgumentException("La base et l'identifiant utilisateur sont obligatoires");
        }

        String sql = "SELECT u.id AS idUtilisateur, u.nom, u.prenom, r.libelle AS role, "
                + "p.idVital, p.dateNaissance, p.lieuNaissance, p.genre, p.marie "
                + "FROM utilisateur u JOIN role r ON r.id = u.idRole "
                + "LEFT JOIN patient p ON p.idUtilisateur = u.id "
                + "WHERE u.id = ? AND LOWER(r.libelle) = 'patient'";
        try (Connection connection = dm.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, idUtilisateur);
            try (ResultSet rs = statement.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                PatientDTO detail = new PatientDTO();
                detail.setIdUtilisateur(rs.getInt("idUtilisateur"));
                detail.setNom(rs.getString("nom"));
                detail.setPrenom(rs.getString("prenom"));
                detail.setRole(rs.getString("role"));
                detail.setIdVital(rs.getString("idVital"));
                detail.setDateNaissance(rs.getDate("dateNaissance"));
                detail.setLieuNaissance(rs.getString("lieuNaissance"));
                detail.setGenre(rs.getString("genre"));
                detail.setMarie((Boolean) rs.getObject("marie"));
                return detail;
            }
        }
    }

    public void saveData(DatabaseManager dm, Patient p) throws Exception {
        if (dm == null || p == null) {
            throw new IllegalArgumentException("La base et le patient sont obligatoires");
        }
        p.setIdVital(UUID.randomUUID().toString());

        Genutil.create(dm, "patient", p);
    }

    public void updateData(DatabaseManager dm, Patient p) throws Exception {
        if (dm == null || p == null || p.getIdVital() == null || p.getIdVital().isBlank()) {
            throw new IllegalArgumentException("La base et l'identifiant du patient sont obligatoires");
        }
        int updated = Genutil.update(dm, "patient", "idVital", p);
        if (updated == 0) {
            throw new SQLException("Aucun patient trouvé pour l'identifiant " + p.getIdVital());
        }
    }

    public Patient getData(DatabaseManager dm, String idVital) throws Exception {
        if (dm == null || idVital == null || idVital.isBlank()) {
            throw new IllegalArgumentException("La base et l'identifiant du patient sont obligatoires");
        }
        return Genutil.findById(dm, "patient", "idVital", idVital, Patient.class);
    }
}
