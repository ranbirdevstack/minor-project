package com.mycompany.medicalappointmentandprescription.dao;

import com.mycompany.medicalappointmentandprescription.models.MedicalHistory;
import com.mycompany.medicalappointmentandprescription.models.Patient;
import com.mycompany.medicalappointmentandprescription.models.Doctor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MedicalHistoryDao {

    private static final List<MedicalHistory> Histories =
            new ArrayList<>();

    private static int nextHistoryId = 4;

    static {
        LocalDateTime now = LocalDateTime.now();

        Histories.add(new MedicalHistory(
                1, 1, 1, now.minusDays(10),
                "Fever and Headache",
                "Viral Fever",
                "Rest, fluids and prescribed medication"));

        Histories.add(new MedicalHistory(
                2, 2, 1, now.minusDays(5),
                "Sneezing and runny nose",
                "Allergic rhinitis",
                "Avoid allergens and take prescribed medication"));

        Histories.add(new MedicalHistory(
                3, 3, 1, now.minusDays(2),
                "Sore throat and cough",
                "Upper respiratory tract infection",
                "Rest and follow prescribed medication"));
    }

    // Validate medical history
    private boolean isValidHistory(MedicalHistory h) {

        return h != null
                && h.getPatientId() > 0
                && h.getDoctorId() > 0
                && h.getSymptoms() != null
                && !h.getSymptoms().trim().isEmpty()
                && h.getDiagnosis() != null
                && !h.getDiagnosis().trim().isEmpty()
                && h.getTreatmentNotes() != null
                && !h.getTreatmentNotes().trim().isEmpty();
    }

    // Validate patient and doctor
    private boolean isValidPatientAndDoctor(MedicalHistory h) {

        PatientDao pd = new PatientDao();
        DoctorDao dd = new DoctorDao();

        Patient patient =
                pd.findByPatientId(h.getPatientId());

        Doctor doctor =
                dd.findByDoctorId(h.getDoctorId());

        return patient != null && doctor != null;
    }

    // Convert ResultSet into MedicalHistory
    private MedicalHistory mapHistory(ResultSet rs)
            throws SQLException {

        MedicalHistory h = new MedicalHistory();

        h.setHistoryId(rs.getInt("history_id"));
        h.setPatientId(rs.getInt("patient_id"));
        h.setDoctorId(rs.getInt("doctor_id"));

        h.setRecordedAt(
                rs.getTimestamp("recorded_at").toLocalDateTime());

        h.setSymptoms(rs.getString("symptoms"));
        h.setDiagnosis(rs.getString("diagnosis"));
        h.setTreatmentNotes(rs.getString("treatment_notes"));

        return h;
    }

    // Add Medical History
    public boolean addHistory(MedicalHistory h) {

        if (!isValidHistory(h)
                || !isValidPatientAndDoctor(h)) {
            return false;
        }

        String sql =
                "INSERT INTO medical_history "
                + "(patient_id, doctor_id, symptoms, diagnosis, treatment_notes) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection()) {

            if (con != null) {

                try (PreparedStatement ps =
                        con.prepareStatement(
                                sql,
                                PreparedStatement.RETURN_GENERATED_KEYS)) {

                    ps.setInt(1, h.getPatientId());
                    ps.setInt(2, h.getDoctorId());
                    ps.setString(3, h.getSymptoms().trim());
                    ps.setString(4, h.getDiagnosis().trim());
                    ps.setString(5, h.getTreatmentNotes().trim());

                    int rows = ps.executeUpdate();

                    if (rows > 0) {

                        try (ResultSet rs = ps.getGeneratedKeys()) {

                            if (rs.next()) {
                                h.setHistoryId(rs.getInt(1));
                            }
                        }

                        try (PreparedStatement query =
                                con.prepareStatement(
                                        "SELECT recorded_at FROM medical_history "
                                        + "WHERE history_id = ?")) {

                            query.setInt(1, h.getHistoryId());

                            try (ResultSet rs = query.executeQuery()) {

                                if (rs.next()) {
                                    h.setRecordedAt(
                                            rs.getTimestamp("recorded_at")
                                                    .toLocalDateTime());
                                }
                            }
                        }

                        return true;
                    }

                    return false;
                }
            }

        } catch (SQLException e) {

            System.err.println(
                    "MedicalHistoryDao addHistory Error: "
                    + e.getMessage());
        }

        // In-memory fallback

        synchronized (Histories) {

            h.setHistoryId(nextHistoryId++);

            h.setRecordedAt(LocalDateTime.now());

            h.setSymptoms(h.getSymptoms().trim());
            h.setDiagnosis(h.getDiagnosis().trim());
            h.setTreatmentNotes(h.getTreatmentNotes().trim());

            Histories.add(h);

            return true;
        }
    }

    // Find by History ID
    public MedicalHistory findByHistoryId(int historyId) {

        String sql =
                "SELECT * FROM medical_history WHERE history_id = ?";

        try (Connection con = DBConnection.getConnection()) {

            if (con != null) {

                try (PreparedStatement ps = con.prepareStatement(sql)) {

                    ps.setInt(1, historyId);

                    try (ResultSet rs = ps.executeQuery()) {

                        if (rs.next()) {
                            return mapHistory(rs);
                        }

                        return null;
                    }
                }
            }

        } catch (SQLException e) {

            System.err.println(
                    "MedicalHistoryDao findByHistoryId Error: "
                    + e.getMessage());
        }

        synchronized (Histories) {

            for (MedicalHistory h : Histories) {

                if (h.getHistoryId() == historyId) {
                    return h;
                }
            }
        }

        return null;
    }

    // Find All Medical Histories
    public List<MedicalHistory> findAll() {

        List<MedicalHistory> result = new ArrayList<>();

        String sql =
                "SELECT * FROM medical_history ORDER BY recorded_at DESC";

        try (Connection con = DBConnection.getConnection()) {

            if (con != null) {

                try (PreparedStatement ps =
                        con.prepareStatement(sql);
                     ResultSet rs = ps.executeQuery()) {

                    while (rs.next()) {
                        result.add(mapHistory(rs));
                    }

                    return result;
                }
            }

        } catch (SQLException e) {

            System.err.println(
                    "MedicalHistoryDao findAll Error: "
                    + e.getMessage());
        }

        synchronized (Histories) {
            return new ArrayList<>(Histories);
        }
    }

    // Find by Patient ID
    public List<MedicalHistory> findByPatientId(int patientId) {

        List<MedicalHistory> result = new ArrayList<>();

        String sql =
                "SELECT * FROM medical_history "
                + "WHERE patient_id = ? ORDER BY recorded_at DESC";

        try (Connection con = DBConnection.getConnection()) {

            if (con != null) {

                try (PreparedStatement ps = con.prepareStatement(sql)) {

                    ps.setInt(1, patientId);

                    try (ResultSet rs = ps.executeQuery()) {

                        while (rs.next()) {
                            result.add(mapHistory(rs));
                        }

                        return result;
                    }
                }
            }

        } catch (SQLException e) {

            System.err.println(
                    "MedicalHistoryDao findByPatientId Error: "
                    + e.getMessage());
        }

        synchronized (Histories) {

            for (MedicalHistory h : Histories) {

                if (h.getPatientId() == patientId) {
                    result.add(h);
                }
            }
        }

        return result;
    }

    // Find by Doctor ID
    public List<MedicalHistory> findByDoctorId(int doctorId) {

        List<MedicalHistory> result = new ArrayList<>();

        String sql =
                "SELECT * FROM medical_history "
                + "WHERE doctor_id = ? ORDER BY recorded_at DESC";

        try (Connection con = DBConnection.getConnection()) {

            if (con != null) {

                try (PreparedStatement ps = con.prepareStatement(sql)) {

                    ps.setInt(1, doctorId);

                    try (ResultSet rs = ps.executeQuery()) {

                        while (rs.next()) {
                            result.add(mapHistory(rs));
                        }

                        return result;
                    }
                }
            }

        } catch (SQLException e) {

            System.err.println(
                    "MedicalHistoryDao findByDoctorId Error: "
                    + e.getMessage());
        }

        synchronized (Histories) {

            for (MedicalHistory h : Histories) {

                if (h.getDoctorId() == doctorId) {
                    result.add(h);
                }
            }
        }

        return result;
    }

    // Update Medical History
    public boolean updateHistory(MedicalHistory h) {

        if (h == null
                || h.getHistoryId() <= 0
                || !isValidHistory(h)
                || !isValidPatientAndDoctor(h)) {

            return false;
        }

        String sql =
                "UPDATE medical_history SET "
                + "patient_id = ?, "
                + "doctor_id = ?, "
                + "symptoms = ?, "
                + "diagnosis = ?, "
                + "treatment_notes = ? "
                + "WHERE history_id = ?";

        try (Connection con = DBConnection.getConnection()) {

            if (con != null) {

                try (PreparedStatement ps = con.prepareStatement(sql)) {

                    ps.setInt(1, h.getPatientId());
                    ps.setInt(2, h.getDoctorId());
                    ps.setString(3, h.getSymptoms().trim());
                    ps.setString(4, h.getDiagnosis().trim());
                    ps.setString(5, h.getTreatmentNotes().trim());
                    ps.setInt(6, h.getHistoryId());

                    return ps.executeUpdate() > 0;
                }
            }

        } catch (SQLException e) {

            System.err.println(
                    "MedicalHistoryDao updateHistory Error: "
                    + e.getMessage());
        }

        // In-memory fallback

        synchronized (Histories) {

            for (MedicalHistory exist : Histories) {

                if (exist.getHistoryId() == h.getHistoryId()) {

                    exist.setPatientId(h.getPatientId());
                    exist.setDoctorId(h.getDoctorId());

                    exist.setSymptoms(h.getSymptoms().trim());
                    exist.setDiagnosis(h.getDiagnosis().trim());

                    exist.setTreatmentNotes(
                            h.getTreatmentNotes().trim());

                    return true;
                }
            }
        }

        return false;
    }

    // Delete Medical History
    public boolean deleteHistory(int historyId) {

        String sql =
                "DELETE FROM medical_history WHERE history_id = ?";

        try (Connection con = DBConnection.getConnection()) {

            if (con != null) {

                try (PreparedStatement ps = con.prepareStatement(sql)) {

                    ps.setInt(1, historyId);

                    return ps.executeUpdate() > 0;
                }
            }

        } catch (SQLException e) {

            System.err.println(
                    "MedicalHistoryDao deleteHistory Error: "
                    + e.getMessage());
        }

        // In-memory fallback

        synchronized (Histories) {

            MedicalHistory h = findByHistoryId(historyId);

            if (h == null) {
                return false;
            }

            return Histories.remove(h);
        }
    }
}