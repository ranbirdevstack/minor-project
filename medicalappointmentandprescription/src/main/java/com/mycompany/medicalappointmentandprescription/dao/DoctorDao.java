package com.mycompany.medicalappointmentandprescription.dao;

import com.mycompany.medicalappointmentandprescription.models.Doctor;
import com.mycompany.medicalappointmentandprescription.models.User;
import com.mycompany.medicalappointmentandprescription.models.Appointment;
import com.mycompany.medicalappointmentandprescription.models.Prescription;
import com.mycompany.medicalappointmentandprescription.models.MedicalHistory;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.time.DayOfWeek;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DoctorDao {

    private static final List<Doctor> Doctors = new ArrayList<>();
    private static int nextDoctorId = 2;


    static {
        Doctors.add(new Doctor(
                1,
                3,
                "Dr Amit Verma",
                "General Physician",
                "9876543213",
                500.00,
                "09:00 AM -02:00 PM",
                Arrays.asList(
                        DayOfWeek.MONDAY,
                        DayOfWeek.WEDNESDAY,
                        DayOfWeek.FRIDAY
                ),
                true
        ));
    }

    // ================= VALIDATION =================

    private boolean isValidDoctor(Doctor d) {

        return d != null
                && d.getDoctorName() != null
                && !d.getDoctorName().trim().isEmpty()
                && d.getSpecialization() != null
                && !d.getSpecialization().trim().isEmpty()
                && d.getContactNumber() != null
                && !d.getContactNumber().trim().isEmpty()
                && Double.isFinite(d.getConsultationFee())
                && d.getConsultationFee() > 0.00
                && d.getWorkingHours() != null
                && !d.getWorkingHours().trim().isEmpty();
    }

    // ================= WORKING DAYS CONVERSION =================

    private String convertWorkingDaysToString(Doctor d) {

        List<?> days = d.getWorkingDays();
        List<String> dayNames = new ArrayList<>();

        for (Object day : days) {

            if (day != null) {
                dayNames.add(day.toString().toUpperCase(Locale.ROOT));
            }
        }

        return String.join(",", dayNames);
    }

    private List<DayOfWeek> convertStringToWorkingDays(String days) {

        List<DayOfWeek> workingDays = new ArrayList<>();

        if (days == null || days.trim().isEmpty()) {
            return workingDays;
        }

        String[] dayArray = days.split(",");

        for (String day : dayArray) {

            try {
                workingDays.add(
                        DayOfWeek.valueOf(day.trim().toUpperCase(Locale.ROOT))
                );
            } catch (IllegalArgumentException ignored) {
                
            }
        }

        return workingDays;
    }

    // ================= DATABASE SYNC =================

    private boolean syncDoctorsFromDatabase() {

        List<Doctor> databaseDoctors = new ArrayList<>();
        int maximumDoctorId = 0;

        String sql = "SELECT * FROM doctors ORDER BY doctor_id";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Doctor d = new Doctor();

                d.setDoctorId(rs.getInt("doctor_id"));
                d.setUserId(rs.getInt("user_id"));
                d.setDoctorName(rs.getString("doctor_name"));
                d.setSpecialization(rs.getString("specialization"));
                d.setContactNumber(rs.getString("contact_number"));
                d.setConsultationFee(rs.getDouble("consultation_fee"));
                d.setWorkingHours(rs.getString("working_hours"));

                d.setWorkingDays(
                        convertStringToWorkingDays(
                                rs.getString("working_days")
                        )
                );

                d.setAvaliabilityStatus(
                        rs.getBoolean("availability_status")
                );

                databaseDoctors.add(d);

                if (d.getDoctorId() > maximumDoctorId) {
                    maximumDoctorId = d.getDoctorId();
                }
            }

            synchronized (Doctors) {

                Doctors.clear();
                Doctors.addAll(databaseDoctors);

                nextDoctorId = maximumDoctorId + 1;
            }

            return true;

        } catch (SQLException | RuntimeException e) {

            System.err.println(
                    "DoctorDao Database Sync Error: " + e.getMessage()
            );

            return false;
        }
    }

    // ================= IN-MEMORY SEARCH =================

    private Doctor findByDoctorIdInMemory(int doctorId) {

        synchronized (Doctors) {

            for (Doctor d : Doctors) {

                if (d.getDoctorId() == doctorId) {
                    return d;
                }
            }
        }

        return null;
    }

    private Doctor findByUserIdInMemory(int userId) {

        synchronized (Doctors) {

            for (Doctor d : Doctors) {

                if (d.getUserId() == userId) {
                    return d;
                }
            }
        }

        return null;
    }

    // ================= ADD DOCTOR =================

    public boolean addDoctor(Doctor d) {

        if (!isValidDoctor(d)) {
            return false;
        }

        UserDao ud = new UserDao();

        User u = ud.findByUserId(d.getUserId());

        if (u == null) {
            return false;
        }

        if (!"Doctor".equalsIgnoreCase(u.getRole())) {
            return false;
        }

        boolean databaseAvailable = syncDoctorsFromDatabase();

        if (findByUserIdInMemory(d.getUserId()) != null) {
            return false;
        }

        // Database unavailable: in-memory fallback
        if (!databaseAvailable) {

            synchronized (Doctors) {

                d.setDoctorId(nextDoctorId++);
                Doctors.add(d);
            }

            return true;
        }

        String sql = "INSERT INTO doctors "
                + "(user_id, doctor_name, specialization, contact_number, "
                + "consultation_fee, working_hours, working_days, availability_status) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, d.getUserId());
            ps.setString(2, d.getDoctorName());
            ps.setString(3, d.getSpecialization());
            ps.setString(4, d.getContactNumber());
            ps.setDouble(5, d.getConsultationFee());
            ps.setString(6, d.getWorkingHours());
            ps.setString(7, convertWorkingDaysToString(d));
            ps.setBoolean(8, d.getAvaliabilityStatus());

            int rows = ps.executeUpdate();

            if (rows > 0) {

                try (ResultSet keys = ps.getGeneratedKeys()) {

                    if (keys.next()) {
                        d.setDoctorId(keys.getInt(1));
                    }
                }

                syncDoctorsFromDatabase();

                return true;
            }

        } catch (SQLException e) {

            System.err.println("Add Doctor Error: " + e.getMessage());

            if (isConnectionFailure(e)) {

                synchronized (Doctors) {

                    d.setDoctorId(nextDoctorId++);
                    Doctors.add(d);
                }

                return true;
            }
        }

        return false;
    }

    // ================= FIND BY DOCTOR ID =================

    public Doctor findByDoctorId(int doctorId) {

        syncDoctorsFromDatabase();

        return findByDoctorIdInMemory(doctorId);
    }

    // ================= FIND BY USER ID =================

    public Doctor findByUserId(int userId) {

        syncDoctorsFromDatabase();

        return findByUserIdInMemory(userId);
    }

    // ================= FIND ALL DOCTORS =================

    public List<Doctor> findAll() {

        syncDoctorsFromDatabase();

        synchronized (Doctors) {
            return new ArrayList<>(Doctors);
        }
    }

    // ================= FIND AVAILABLE DOCTORS =================

    public List<Doctor> findAvailableDoctor() {

        List<Doctor> available = new ArrayList<>();

        for (Doctor d : findAll()) {

            if (d.getAvaliabilityStatus()) {
                available.add(d);
            }
        }

        return available;
    }

    // ================= UPDATE DOCTOR =================

    public boolean updateDoctor(Doctor d) {

        if (!isValidDoctor(d)) {
            return false;
        }

        boolean databaseAvailable = syncDoctorsFromDatabase();

        Doctor exist = findByDoctorIdInMemory(d.getDoctorId());

        if (exist == null) {
            return false;
        }

        if (exist.getUserId() != d.getUserId()) {
            return false;
        }

        // Database unavailable: in-memory fallback
        if (!databaseAvailable) {

            updateDoctorInMemory(exist, d);

            return true;
        }

        String sql = "UPDATE doctors SET "
                + "doctor_name=?, "
                + "specialization=?, "
                + "contact_number=?, "
                + "consultation_fee=?, "
                + "working_hours=?, "
                + "working_days=?, "
                + "availability_status=? "
                + "WHERE doctor_id=? AND user_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, d.getDoctorName());
            ps.setString(2, d.getSpecialization());
            ps.setString(3, d.getContactNumber());
            ps.setDouble(4, d.getConsultationFee());
            ps.setString(5, d.getWorkingHours());
            ps.setString(6, convertWorkingDaysToString(d));
            ps.setBoolean(7, d.getAvaliabilityStatus());
            ps.setInt(8, d.getDoctorId());
            ps.setInt(9, d.getUserId());

            int rows = ps.executeUpdate();

            if (rows > 0) {

                syncDoctorsFromDatabase();

                return true;
            }

        } catch (SQLException e) {

            System.err.println("Update Doctor Error: " + e.getMessage());

            if (isConnectionFailure(e)) {

                updateDoctorInMemory(exist, d);

                return true;
            }
        }

        return false;
    }

    private void updateDoctorInMemory(Doctor exist, Doctor d) {

        exist.setDoctorName(d.getDoctorName());
        exist.setSpecialization(d.getSpecialization());
        exist.setContactNumber(d.getContactNumber());
        exist.setWorkingHours(d.getWorkingHours());
        exist.setConsultationFee(d.getConsultationFee());
        exist.setAvaliabilityStatus(d.getAvaliabilityStatus());
        exist.setWorkingDays(d.getWorkingDays());
    }

    // ================= DELETE DOCTOR =================

    public boolean deleteDoctor(int doctorId) {

    boolean databaseAvailable = syncDoctorsFromDatabase();

    Doctor d = findByDoctorIdInMemory(doctorId);

    if (d == null) {
        return false;
    }

    // Database unavailable: in-memory fallback
    if (!databaseAvailable) {
        return deleteDoctorFromMemory(d);
    }

    Connection con = null;

    try {

        con = DBConnection.getConnection();
        con.setAutoCommit(false);

        // Check dependent records
        String checkSql =
                "SELECT "
                + "(SELECT COUNT(*) FROM appointments WHERE doctor_id=?) + "
                + "(SELECT COUNT(*) FROM prescriptions WHERE doctor_id=?) + "
                + "(SELECT COUNT(*) FROM medical_history WHERE doctor_id=?) + "
                + "(SELECT COUNT(*) FROM follow_up_recommendations WHERE doctor_id=?) + "
                + "(SELECT COUNT(*) FROM patients WHERE registered_by=?) "
                + "AS total_records";

        try (PreparedStatement ps = con.prepareStatement(checkSql)) {

            ps.setInt(1, doctorId);
            ps.setInt(2, doctorId);
            ps.setInt(3, doctorId);
            ps.setInt(4, doctorId);
            ps.setInt(5, d.getUserId());

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next() && rs.getInt("total_records") > 0) {

                    con.rollback();
                    return false;
                }
            }
        }

        // First delete doctor profile
        String deleteDoctorSql =
                "DELETE FROM doctors WHERE doctor_id=? AND user_id=?";

        try (PreparedStatement ps = con.prepareStatement(deleteDoctorSql)) {

            ps.setInt(1, doctorId);
            ps.setInt(2, d.getUserId());

            if (ps.executeUpdate() != 1) {

                con.rollback();
                return false;
            }
        }

        // Then delete linked user account
        String deleteUserSql =
                "DELETE FROM users WHERE user_id=? AND role='Doctor'";

        try (PreparedStatement ps = con.prepareStatement(deleteUserSql)) {

            ps.setInt(1, d.getUserId());

            if (ps.executeUpdate() != 1) {

                con.rollback();
                return false;
            }
        }

        con.commit();

        // Refresh in-memory data
        syncDoctorsFromDatabase();
        new UserDao().findAll();

        return true;

    } catch (SQLException e) {

        System.err.println("Delete Doctor Error: " + e.getMessage());

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
}

   private boolean deleteDoctorFromMemory(Doctor d) {

    AppointmentDao ad = new AppointmentDao();
    PrescriptionDao pd = new PrescriptionDao();
    MedicalHistoryDao mhd = new MedicalHistoryDao();

    int doctorId = d.getDoctorId();

    for (Appointment a : ad.findAll()) {
        if (a.getDoctorId() == doctorId) {
            return false;
        }
    }

    for (Prescription p : pd.findAll()) {
        if (p.getDoctorId() == doctorId) {
            return false;
        }
    }

    for (MedicalHistory mh : mhd.findAll()) {
        if (mh.getDoctorId() == doctorId) {
            return false;
        }
    }

    synchronized (Doctors) {
        if (!Doctors.remove(d)) {
            return false;
        }
    }

    
    UserDao ud = new UserDao();

    if (!ud.deleteAccount(d.getUserId())) {

        synchronized (Doctors) {
            Doctors.add(d);
        }

        return false;
    }

    return true;
}

    private boolean isConnectionFailure(SQLException e) {

        String state = e.getSQLState();

        return state == null || state.startsWith("08");
    }
}