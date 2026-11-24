package com.mycompany.medicalappointmentandprescription.dao;

import com.mycompany.medicalappointmentandprescription.models.Prescription;
import com.mycompany.medicalappointmentandprescription.models.Patient;
import com.mycompany.medicalappointmentandprescription.models.Doctor;
import com.mycompany.medicalappointmentandprescription.models.Appointment;
import com.mycompany.medicalappointmentandprescription.models.MedicineReminder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PrescriptionDao {

    private static final List<Prescription> Prescriptions = new ArrayList<>();
    private static int nextPrescriptionId = 4;

    // =========================
    // CONSTRUCTOR
    // =========================
    public PrescriptionDao() {
    }

    // =========================
    // ADD PRESCRIPTION
    // =========================
    public boolean addPrescription(Prescription p) {

        if (!isValidPrescription(p)) {
            return false;
        }

        if (!validateReferences(p)) {
            return false;
        }

        p.setIssueDate(LocalDate.now());

        try (Connection con = DBConnection.getConnection()) {

            if (con == null) {
                return addPrescriptionToMemory(p);
            }

            // One prescription per appointment
            if (findByAppointmentId(p.getAppointmentId()) != null) {
                return false;
            }

            String sql = "INSERT INTO prescriptions "
                    + "(appointment_id, patient_id, doctor_id, issue_date, "
                    + "medicine_and_dosage, clinical_notes) "
                    + "VALUES (?, ?, ?, ?, ?, ?)";

            try (PreparedStatement ps = con.prepareStatement(
                    sql, Statement.RETURN_GENERATED_KEYS)) {

                ps.setInt(1, p.getAppointmentId());
                ps.setInt(2, p.getPatientId());
                ps.setInt(3, p.getDoctorId());
                ps.setDate(4, java.sql.Date.valueOf(p.getIssueDate()));
                ps.setString(5, p.getMedicineAndDosage());
                ps.setString(6, p.getClinicalNotes());

                int rows = ps.executeUpdate();

                if (rows > 0) {

                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) {
                            p.setPrescriptionId(rs.getInt(1));
                        }
                    }

                    syncPrescriptionsFromDatabase();

                    return true;
                }
            }

        } catch (SQLException e) {

            if (isConnectionFailure(e)) {
                return addPrescriptionToMemory(p);
            }

            e.printStackTrace();
            return false;
        }

        return false;
    }

    // =========================
    // FIND BY PRESCRIPTION ID
    // =========================
    public Prescription findByPrescriptionId(int prescriptionId) {

        if (prescriptionId <= 0) {
            return null;
        }

        try (Connection con = DBConnection.getConnection()) {

            if (con == null) {
                return findByPrescriptionIdInMemory(prescriptionId);
            }

            String sql = "SELECT * FROM prescriptions WHERE prescription_id = ?";

            try (PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setInt(1, prescriptionId);

                try (ResultSet rs = ps.executeQuery()) {

                    if (rs.next()) {
                        return mapPrescription(rs);
                    }
                }
            }

        } catch (SQLException e) {

            if (isConnectionFailure(e)) {
                return findByPrescriptionIdInMemory(prescriptionId);
            }

            e.printStackTrace();
        }

        return null;
    }

    // =========================
    // FIND BY APPOINTMENT ID
    // =========================
    public Prescription findByAppointmentId(int appointmentId) {

        if (appointmentId <= 0) {
            return null;
        }

        try (Connection con = DBConnection.getConnection()) {

            if (con == null) {
                return findByAppointmentIdInMemory(appointmentId);
            }

            String sql = "SELECT * FROM prescriptions WHERE appointment_id = ?";

            try (PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setInt(1, appointmentId);

                try (ResultSet rs = ps.executeQuery()) {

                    if (rs.next()) {
                        return mapPrescription(rs);
                    }
                }
            }

        } catch (SQLException e) {

            if (isConnectionFailure(e)) {
                return findByAppointmentIdInMemory(appointmentId);
            }

            e.printStackTrace();
        }

        return null;
    }

    // =========================
    // FIND ALL
    // =========================
    public List<Prescription> findAll() {

        try (Connection con = DBConnection.getConnection()) {

            if (con == null) {
                return new ArrayList<>(Prescriptions);
            }

            List<Prescription> result = new ArrayList<>();

            String sql = "SELECT * FROM prescriptions ORDER BY prescription_id";

            try (PreparedStatement ps = con.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    result.add(mapPrescription(rs));
                }
            }

            return result;

        } catch (SQLException e) {

            if (isConnectionFailure(e)) {
                return new ArrayList<>(Prescriptions);
            }

            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    // =========================
    // FIND BY PATIENT ID
    // =========================
    public List<Prescription> findByPatientId(int patientId) {

        List<Prescription> result = new ArrayList<>();

        if (patientId <= 0) {
            return result;
        }

        try (Connection con = DBConnection.getConnection()) {

            if (con == null) {
                return findByPatientIdInMemory(patientId);
            }

            String sql = "SELECT * FROM prescriptions "
                    + "WHERE patient_id = ? ORDER BY issue_date";

            try (PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setInt(1, patientId);

                try (ResultSet rs = ps.executeQuery()) {

                    while (rs.next()) {
                        result.add(mapPrescription(rs));
                    }
                }
            }

            return result;

        } catch (SQLException e) {

            if (isConnectionFailure(e)) {
                return findByPatientIdInMemory(patientId);
            }

            e.printStackTrace();
        }

        return result;
    }

    // =========================
    // FIND BY DOCTOR ID
    // =========================
    public List<Prescription> findByDoctorId(int doctorId) {

        List<Prescription> result = new ArrayList<>();

        if (doctorId <= 0) {
            return result;
        }

        try (Connection con = DBConnection.getConnection()) {

            if (con == null) {
                return findByDoctorIdInMemory(doctorId);
            }

            String sql = "SELECT * FROM prescriptions "
                    + "WHERE doctor_id = ? ORDER BY issue_date";

            try (PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setInt(1, doctorId);

                try (ResultSet rs = ps.executeQuery()) {

                    while (rs.next()) {
                        result.add(mapPrescription(rs));
                    }
                }
            }

            return result;

        } catch (SQLException e) {

            if (isConnectionFailure(e)) {
                return findByDoctorIdInMemory(doctorId);
            }

            e.printStackTrace();
        }

        return result;
    }

    // =========================
    // FIND BY ISSUE DATE
    // =========================
    public List<Prescription> findByIssueDate(LocalDate issueDate) {

        List<Prescription> result = new ArrayList<>();

        if (issueDate == null) {
            return result;
        }

        try (Connection con = DBConnection.getConnection()) {

            if (con == null) {
                return findByIssueDateInMemory(issueDate);
            }

            String sql = "SELECT * FROM prescriptions WHERE issue_date = ?";

            try (PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setDate(1, java.sql.Date.valueOf(issueDate));

                try (ResultSet rs = ps.executeQuery()) {

                    while (rs.next()) {
                        result.add(mapPrescription(rs));
                    }
                }
            }

            return result;

        } catch (SQLException e) {

            if (isConnectionFailure(e)) {
                return findByIssueDateInMemory(issueDate);
            }

            e.printStackTrace();
        }

        return result;
    }

    // =========================
    // UPDATE PRESCRIPTION
    // =========================
    public boolean updatePrescription(Prescription p) {

        if (!isValidPrescription(p) || p.getPrescriptionId() <= 0) {
            return false;
        }

        if (!validateReferences(p)) {
            return false;
        }

        try (Connection con = DBConnection.getConnection()) {

            if (con == null) {
                return updatePrescriptionInMemory(p);
            }

            Prescription existing = findByPrescriptionId(p.getPrescriptionId());

            if (existing == null) {
                return false;
            }

            // Prevent another prescription for the same appointment
            Prescription other = findByAppointmentId(p.getAppointmentId());

            if (other != null
                    && other.getPrescriptionId() != p.getPrescriptionId()) {
                return false;
            }

            String sql = "UPDATE prescriptions SET "
                    + "appointment_id = ?, patient_id = ?, doctor_id = ?, "
                    + "medicine_and_dosage = ?, clinical_notes = ? "
                    + "WHERE prescription_id = ?";

            try (PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setInt(1, p.getAppointmentId());
                ps.setInt(2, p.getPatientId());
                ps.setInt(3, p.getDoctorId());
                ps.setString(4, p.getMedicineAndDosage());
                ps.setString(5, p.getClinicalNotes());
                ps.setInt(6, p.getPrescriptionId());

                boolean updated = ps.executeUpdate() > 0;

                if (updated) {
                    syncPrescriptionsFromDatabase();
                }

                return updated;
            }

        } catch (SQLException e) {

            if (isConnectionFailure(e)) {
                return updatePrescriptionInMemory(p);
            }

            e.printStackTrace();
            return false;
        }
    }

    // =========================
    // DELETE PRESCRIPTION
    // =========================
    public boolean deletePrescription(int prescriptionId) {

        if (prescriptionId <= 0) {
            return false;
        }

        try (Connection con = DBConnection.getConnection()) {

            if (con == null) {
                return deletePrescriptionFromMemory(prescriptionId);
            }

            Prescription existing = findByPrescriptionId(prescriptionId);

            if (existing == null) {
                return false;
            }

            // Check linked medicine reminders
            String checkSql = "SELECT COUNT(*) FROM medicine_reminders "
                    + "WHERE prescription_id = ?";

            try (PreparedStatement ps = con.prepareStatement(checkSql)) {

                ps.setInt(1, prescriptionId);

                try (ResultSet rs = ps.executeQuery()) {

                    if (rs.next() && rs.getInt(1) > 0) {
                        return false;
                    }
                }
            }

            String sql = "DELETE FROM prescriptions WHERE prescription_id = ?";

            try (PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setInt(1, prescriptionId);

                boolean deleted = ps.executeUpdate() > 0;

                if (deleted) {
                    syncPrescriptionsFromDatabase();
                }

                return deleted;
            }

        } catch (SQLException e) {

            if (isConnectionFailure(e)) {
                return deletePrescriptionFromMemory(prescriptionId);
            }

            e.printStackTrace();
            return false;
        }
    }

    // =========================
    // VALIDATE PRESCRIPTION
    // =========================
    private boolean isValidPrescription(Prescription p) {

        return p != null
                && p.getAppointmentId() > 0
                && p.getPatientId() > 0
                && p.getDoctorId() > 0
                && p.getMedicineAndDosage() != null
                && !p.getMedicineAndDosage().trim().isEmpty()
                && p.getClinicalNotes() != null
                && !p.getClinicalNotes().trim().isEmpty();
    }

    // =========================
    // VALIDATE REFERENCES
    // =========================
    private boolean validateReferences(Prescription p) {

        PatientDao pd = new PatientDao();
        DoctorDao dd = new DoctorDao();
        AppointmentDao ad = new AppointmentDao();

        Appointment a = ad.findByAppointmentId(p.getAppointmentId());
        Patient pt = pd.findByPatientId(p.getPatientId());
        Doctor d = dd.findByDoctorId(p.getDoctorId());

        if (a == null || pt == null || d == null) {
            return false;
        }

        if (a.getPatientId() != p.getPatientId()
                || a.getDoctorId() != p.getDoctorId()) {
            return false;
        }

        return a.getStatus() != null
                && !"Cancelled".equalsIgnoreCase(a.getStatus());
    }

    // =========================
    // DATABASE SYNC
    // =========================
    private void syncPrescriptionsFromDatabase() {

        List<Prescription> temp = new ArrayList<>();
        int maxId = 0;

        try (Connection con = DBConnection.getConnection()) {

            if (con == null) {
                return;
            }

            String sql = "SELECT * FROM prescriptions ORDER BY prescription_id";

            try (PreparedStatement ps = con.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    Prescription p = mapPrescription(rs);

                    temp.add(p);

                    if (p.getPrescriptionId() > maxId) {
                        maxId = p.getPrescriptionId();
                    }
                }
            }

            Prescriptions.clear();
            Prescriptions.addAll(temp);

            nextPrescriptionId = maxId + 1;

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // =========================
    // RESULT SET MAPPING
    // =========================
    private Prescription mapPrescription(ResultSet rs) throws SQLException {

        return new Prescription(
                rs.getInt("prescription_id"),
                rs.getInt("appointment_id"),
                rs.getInt("patient_id"),
                rs.getInt("doctor_id"),
                rs.getDate("issue_date").toLocalDate(),
                rs.getString("medicine_and_dosage"),
                rs.getString("clinical_notes")
        );
    }

    // =========================
    // CONNECTION FAILURE CHECK
    // =========================
    private boolean isConnectionFailure(SQLException e) {

        String state = e.getSQLState();

        return state == null || state.startsWith("08");
    }

    // =========================
    // IN-MEMORY ADD
    // =========================
    private boolean addPrescriptionToMemory(Prescription p) {

        for (Prescription existing : Prescriptions) {

            if (existing.getAppointmentId() == p.getAppointmentId()) {
                return false;
            }
        }

        p.setPrescriptionId(nextPrescriptionId++);
        p.setIssueDate(LocalDate.now());

        Prescriptions.add(p);

        return true;
    }

    // =========================
    // IN-MEMORY FIND METHODS
    // =========================
    private Prescription findByPrescriptionIdInMemory(int prescriptionId) {

        for (Prescription p : Prescriptions) {

            if (p.getPrescriptionId() == prescriptionId) {
                return p;
            }
        }

        return null;
    }

    private Prescription findByAppointmentIdInMemory(int appointmentId) {

        for (Prescription p : Prescriptions) {

            if (p.getAppointmentId() == appointmentId) {
                return p;
            }
        }

        return null;
    }

    private List<Prescription> findByPatientIdInMemory(int patientId) {

        List<Prescription> result = new ArrayList<>();

        for (Prescription p : Prescriptions) {

            if (p.getPatientId() == patientId) {
                result.add(p);
            }
        }

        return result;
    }

    private List<Prescription> findByDoctorIdInMemory(int doctorId) {

        List<Prescription> result = new ArrayList<>();

        for (Prescription p : Prescriptions) {

            if (p.getDoctorId() == doctorId) {
                result.add(p);
            }
        }

        return result;
    }

    private List<Prescription> findByIssueDateInMemory(LocalDate issueDate) {

        List<Prescription> result = new ArrayList<>();

        for (Prescription p : Prescriptions) {

            if (p.getIssueDate().equals(issueDate)) {
                result.add(p);
            }
        }

        return result;
    }

    // =========================
    // IN-MEMORY UPDATE
    // =========================
    private boolean updatePrescriptionInMemory(Prescription p) {

        Prescription existing =
                findByPrescriptionIdInMemory(p.getPrescriptionId());

        if (existing == null) {
            return false;
        }

        for (Prescription other : Prescriptions) {

            if (other.getPrescriptionId() != p.getPrescriptionId()
                    && other.getAppointmentId() == p.getAppointmentId()) {
                return false;
            }
        }

        if (!validateReferences(p)) {
            return false;
        }

        existing.setAppointmentId(p.getAppointmentId());
        existing.setPatientId(p.getPatientId());
        existing.setDoctorId(p.getDoctorId());
        existing.setMedicineAndDosage(p.getMedicineAndDosage());
        existing.setClinicalNotes(p.getClinicalNotes());

        return true;
    }

    // =========================
    // IN-MEMORY DELETE
    // =========================
    private boolean deletePrescriptionFromMemory(int prescriptionId) {

        Prescription p = findByPrescriptionIdInMemory(prescriptionId);

        if (p == null) {
            return false;
        }

        MedicineReminderDao mrd = new MedicineReminderDao();

        for (MedicineReminder mr : mrd.findAll()) {

            if (mr.getPrescriptionId() == prescriptionId) {
                return false;
            }
        }

        return Prescriptions.remove(p);
    }
}