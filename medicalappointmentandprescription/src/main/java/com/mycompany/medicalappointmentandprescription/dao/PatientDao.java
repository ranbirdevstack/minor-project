package com.mycompany.medicalappointmentandprescription.dao;

import com.mycompany.medicalappointmentandprescription.models.Patient;
import com.mycompany.medicalappointmentandprescription.models.User;
import com.mycompany.medicalappointmentandprescription.models.Appointment;
import com.mycompany.medicalappointmentandprescription.models.Prescription;
import com.mycompany.medicalappointmentandprescription.models.MedicalHistory;
import com.mycompany.medicalappointmentandprescription.models.MedicineReminder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PatientDao {

    private static final List<Patient> Patients = new ArrayList<>();
    private static int nextPatientId = 4;

    // ================= IN-MEMORY FALLBACK DATA =================

    static {
        Patients.add(new Patient(
                1, 2, "Rahul Sharma", 25,
                "Male", "9876543210",
                "Varanasi, UttarPradesh", "O+",
                LocalDateTime.now()
        ));

        Patients.add(new Patient(
                2, 2, "Priya Singh", 22,
                "Female", "9876543211",
                "Jaunpur, UttarPradesh", "B+",
                LocalDateTime.now()
        ));

        Patients.add(new Patient(
                3, 2, "Ankit Verma", 35,
                "Male", "9876543212",
                "Lucknow, UttarPradesh", "A+",
                LocalDateTime.now()
        ));
    }

    // ================= VALIDATION =================

    private boolean isValidPatient(Patient p) {

        return p != null
                && p.getFullName() != null
                && !p.getFullName().trim().isEmpty()
                && p.getAge() > 0
                && p.getGender() != null
                && !p.getGender().trim().isEmpty()
                && p.getContactNumber() != null
                && !p.getContactNumber().trim().isEmpty()
                && p.getAddress() != null
                && !p.getAddress().trim().isEmpty()
                && p.getBloodGroup() != null
                && !p.getBloodGroup().trim().isEmpty();
    }

    // ================= DATABASE SYNC =================

    private boolean syncPatientsFromDatabase() {

        List<Patient> databasePatients = new ArrayList<>();
        int maximumPatientId = 0;

        String sql = "SELECT * FROM patients ORDER BY patient_id";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Patient p = new Patient();

                p.setPatientId(rs.getInt("patient_id"));
                p.setRegisteredBy(rs.getInt("registered_by"));
                p.setFullName(rs.getString("full_name"));
                p.setAge(rs.getInt("age"));
                p.setGender(rs.getString("gender"));
                p.setContactNumber(rs.getString("contact_number"));
                p.setAddress(rs.getString("address"));
                p.setBloodGroup(rs.getString("blood_group"));

                Timestamp timestamp =
                        rs.getTimestamp("registration_date");

                if (timestamp != null) {
                    p.setRegistrationDate(timestamp.toLocalDateTime());
                }

                databasePatients.add(p);

                if (p.getPatientId() > maximumPatientId) {
                    maximumPatientId = p.getPatientId();
                }
            }

            synchronized (Patients) {

                Patients.clear();
                Patients.addAll(databasePatients);

                nextPatientId = maximumPatientId + 1;
            }

            return true;

        } catch (SQLException | RuntimeException e) {

            System.err.println(
                    "PatientDao Database Sync Error: " + e.getMessage()
            );

            return false;
        }
    }

    // ================= IN-MEMORY SEARCH =================

    private Patient findByPatientIdInMemory(int patientId) {

        synchronized (Patients) {

            for (Patient p : Patients) {

                if (p.getPatientId() == patientId) {
                    return p;
                }
            }
        }

        return null;
    }

    // ================= REGISTER PATIENT =================

    public boolean registerPatient(Patient p, int staffId) {

        if (!isValidPatient(p)) {
            return false;
        }

        UserDao ud = new UserDao();

        User u = ud.findByUserId(staffId);

        if (u == null) {
            return false;
        }

        if (!"Admin".equalsIgnoreCase(u.getRole())
                && !"Staff".equalsIgnoreCase(u.getRole())) {
            return false;
        }

        p.setRegisteredBy(staffId);
        p.setRegistrationDate(LocalDateTime.now());

        boolean databaseAvailable = syncPatientsFromDatabase();

        // In-memory fallback
        if (!databaseAvailable) {

            synchronized (Patients) {

                p.setPatientId(nextPatientId++);
                Patients.add(p);
            }

            return true;
        }

        String sql = "INSERT INTO patients "
                + "(registered_by, full_name, age, gender, contact_number, "
                + "address, blood_group, registration_date) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, p.getRegisteredBy());
            ps.setString(2, p.getFullName());
            ps.setInt(3, p.getAge());
            ps.setString(4, p.getGender());
            ps.setString(5, p.getContactNumber());
            ps.setString(6, p.getAddress());
            ps.setString(7, p.getBloodGroup());

            ps.setTimestamp(
                    8,
                    Timestamp.valueOf(p.getRegistrationDate())
            );

            int rows = ps.executeUpdate();

            if (rows > 0) {

                try (ResultSet keys = ps.getGeneratedKeys()) {

                    if (keys.next()) {
                        p.setPatientId(keys.getInt(1));
                    }
                }

                syncPatientsFromDatabase();

                return true;
            }

        } catch (SQLException e) {

            System.err.println("Register Patient Error: " + e.getMessage());

            if (isConnectionFailure(e)) {

                synchronized (Patients) {

                    p.setPatientId(nextPatientId++);
                    Patients.add(p);
                }

                return true;
            }
        }

        return false;
    }

    // ================= FIND BY PATIENT ID =================

    public Patient findByPatientId(int patientId) {

        syncPatientsFromDatabase();

        return findByPatientIdInMemory(patientId);
    }

    // ================= FIND ALL =================

    public List<Patient> findAll() {

        syncPatientsFromDatabase();

        synchronized (Patients) {
            return new ArrayList<>(Patients);
        }
    }

    // ================= FIND BY NAME =================

    public List<Patient> findByName(String key) {

        List<Patient> result = new ArrayList<>();

        if (key == null || key.trim().isEmpty()) {
            return result;
        }

        String search = key.trim().toLowerCase();

        for (Patient p : findAll()) {

            if (p.getFullName() != null
                    && p.getFullName().trim()
                            .toLowerCase().contains(search)) {

                result.add(p);
            }
        }

        return result;
    }

    // ================= FIND BY CONTACT =================

    public List<Patient> findByContact(String contactNumber) {

        List<Patient> result = new ArrayList<>();

        if (contactNumber == null || contactNumber.trim().isEmpty()) {
            return result;
        }

        String search = contactNumber.trim();

        for (Patient p : findAll()) {

            if (p.getContactNumber() != null
                    && p.getContactNumber().trim().contains(search)) {

                result.add(p);
            }
        }

        return result;
    }

    // ================= UPDATE PATIENT =================

    public boolean updatePatient(Patient p) {

        if (!isValidPatient(p)) {
            return false;
        }

        boolean databaseAvailable = syncPatientsFromDatabase();

        Patient exist = findByPatientIdInMemory(p.getPatientId());

        if (exist == null) {
            return false;
        }

        // In-memory fallback
        if (!databaseAvailable) {

            updatePatientInMemory(exist, p);

            return true;
        }

        LocalDateTime registrationDate =
                p.getRegistrationDate() != null
                        ? p.getRegistrationDate()
                        : exist.getRegistrationDate();

        String sql = "UPDATE patients SET "
                + "registered_by=?, "
                + "full_name=?, "
                + "age=?, "
                + "gender=?, "
                + "contact_number=?, "
                + "address=?, "
                + "blood_group=?, "
                + "registration_date=? "
                + "WHERE patient_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, p.getRegisteredBy());
            ps.setString(2, p.getFullName());
            ps.setInt(3, p.getAge());
            ps.setString(4, p.getGender());
            ps.setString(5, p.getContactNumber());
            ps.setString(6, p.getAddress());
            ps.setString(7, p.getBloodGroup());

            ps.setTimestamp(
                    8,
                    Timestamp.valueOf(registrationDate)
            );

            ps.setInt(9, p.getPatientId());

            int rows = ps.executeUpdate();

            if (rows > 0) {

                syncPatientsFromDatabase();

                return true;
            }

        } catch (SQLException e) {

            System.err.println("Update Patient Error: " + e.getMessage());

            if (isConnectionFailure(e)) {

                updatePatientInMemory(exist, p);

                return true;
            }
        }

        return false;
    }

    private void updatePatientInMemory(Patient exist, Patient p) {

        exist.setRegisteredBy(p.getRegisteredBy());
        exist.setRegistrationDate(p.getRegistrationDate());
        exist.setFullName(p.getFullName());
        exist.setContactNumber(p.getContactNumber());
        exist.setAddress(p.getAddress());
        exist.setAge(p.getAge());
        exist.setGender(p.getGender());
        exist.setBloodGroup(p.getBloodGroup());
    }

    // ================= DELETE PATIENT =================

    public boolean deletePatient(int patientId) {

        boolean databaseAvailable = syncPatientsFromDatabase();

        Patient p = findByPatientIdInMemory(patientId);

        if (p == null) {
            return false;
        }

        // In-memory fallback
        if (!databaseAvailable) {
            return deletePatientFromMemory(patientId, p);
        }

        Connection con = null;

        try {

            con = DBConnection.getConnection();
            con.setAutoCommit(false);

            // Check dependent records
            String checkSql =
                    "SELECT "
                    + "(SELECT COUNT(*) FROM appointments WHERE patient_id=?) + "
                    + "(SELECT COUNT(*) FROM prescriptions WHERE patient_id=?) + "
                    + "(SELECT COUNT(*) FROM medical_history WHERE patient_id=?) + "
                    + "(SELECT COUNT(*) FROM follow_up_recommendations WHERE patient_id=?) + "
                    + "(SELECT COUNT(*) FROM medicine_reminders WHERE patient_id=?) + "
                    + "(SELECT COUNT(*) FROM medicine_dose_history WHERE patient_id=?) "
                    + "AS total_records";

            try (PreparedStatement ps = con.prepareStatement(checkSql)) {

                for (int i = 1; i <= 6; i++) {
                    ps.setInt(i, patientId);
                }

                try (ResultSet rs = ps.executeQuery()) {

                    if (rs.next() && rs.getInt("total_records") > 0) {

                        con.rollback();

                        return false;
                    }
                }
            }

            // Delete patient
            String deleteSql =
                    "DELETE FROM patients WHERE patient_id=?";

            try (PreparedStatement ps = con.prepareStatement(deleteSql)) {

                ps.setInt(1, patientId);

                int rows = ps.executeUpdate();

                if (rows > 0) {

                    con.commit();

                    syncPatientsFromDatabase();

                    return true;
                }
            }

            con.rollback();

        } catch (SQLException e) {

            System.err.println("Delete Patient Error: " + e.getMessage());

            if (con != null) {

                try {
                    con.rollback();
                } catch (SQLException ignored) {
                }
            }

            return false;

        } finally {

            if (con != null) {

                try {
                    con.setAutoCommit(true);
                    con.close();
                } catch (SQLException ignored) {
                }
            }
        }

        return false;
    }

    // ================= DELETE FROM MEMORY =================

    private boolean deletePatientFromMemory(int patientId, Patient p) {

        AppointmentDao ad = new AppointmentDao();
        PrescriptionDao pd = new PrescriptionDao();
        MedicalHistoryDao mhd = new MedicalHistoryDao();
        MedicineReminderDao mrd = new MedicineReminderDao();

        for (Appointment a : ad.findAll()) {

            if (a.getPatientId() == patientId) {
                return false;
            }
        }

        for (Prescription pr : pd.findAll()) {

            if (pr.getPatientId() == patientId) {
                return false;
            }
        }

        for (MedicalHistory mh : mhd.findAll()) {

            if (mh.getPatientId() == patientId) {
                return false;
            }
        }

        for (MedicineReminder mr : mrd.findAll()) {

            if (mr.getPatientId() == patientId) {
                return false;
            }
        }

        synchronized (Patients) {
            return Patients.remove(p);
        }
    }

    // ================= CONNECTION ERROR CHECK =================

    private boolean isConnectionFailure(SQLException e) {

        String state = e.getSQLState();

        return state == null || state.startsWith("08");
    }
}