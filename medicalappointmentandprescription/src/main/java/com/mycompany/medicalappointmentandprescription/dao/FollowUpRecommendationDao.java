
package com.mycompany.medicalappointmentandprescription.dao;

import com.mycompany.medicalappointmentandprescription.models.FollowUpRecommendation;
import com.mycompany.medicalappointmentandprescription.models.Patient;
import com.mycompany.medicalappointmentandprescription.models.Doctor;
import com.mycompany.medicalappointmentandprescription.models.Appointment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Date;
import java.sql.Timestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

public class FollowUpRecommendationDao {

    private static final List<FollowUpRecommendation> Follows = new ArrayList<>();
    private static int nextFollowUpId = 4;

    static {
        Follows.add(new FollowUpRecommendation(
                1, 1, 1, 1,
                LocalDate.now().plusDays(7),
                "Follow the prescribed medication and return for a follow-up checkup",
                "Pending",
                LocalDateTime.now().minusDays(2)
        ));

        Follows.add(new FollowUpRecommendation(
                2, 2, 2, 1,
                LocalDate.now().plusDays(14),
                "Monitor symptoms and bring previous medical report during next visit",
                "Completed",
                LocalDateTime.now().minusDays(10)
        ));

        Follows.add(new FollowUpRecommendation(
                3, 3, 3, 1,
                LocalDate.now().plusDays(2),
                "Return for a follow-up examination and review the treatment progress",
                "Pending",
                LocalDateTime.now().minusDays(1)
        ));
    }

    private boolean isValidFollowUp(FollowUpRecommendation fp) {

        if (fp == null) {
            return false;
        }

        if (fp.getDoctorId() <= 0
                || fp.getPatientId() <= 0
                || fp.getAppointmentId() <= 0
                || fp.getRecommendedDate() == null
                || fp.getInstruction() == null
                || fp.getInstruction().trim().isEmpty()) {
            return false;
        }

        if (fp.getRecommendedDate().isBefore(LocalDate.now())) {
            return false;
        }

        return true;
    }

    private boolean isValidStatus(String status) {

        return status != null
                && ("Pending".equalsIgnoreCase(status)
                || "Completed".equalsIgnoreCase(status)
                || "Cancelled".equalsIgnoreCase(status)
                || "Scheduled".equalsIgnoreCase(status));
    }

    private boolean isValidPatientDoctorAppointment(FollowUpRecommendation fp) {

        AppointmentDao ap = new AppointmentDao();
        PatientDao pd = new PatientDao();
        DoctorDao dd = new DoctorDao();

        Patient p = pd.findByPatientId(fp.getPatientId());
        Doctor d = dd.findByDoctorId(fp.getDoctorId());
        Appointment a = ap.findByAppointmentId(fp.getAppointmentId());

        return a != null
                && p != null
                && d != null
                && a.getDoctorId() == fp.getDoctorId()
                && a.getPatientId() == fp.getPatientId();
    }

    private FollowUpRecommendation mapFollowUp(ResultSet rs) throws SQLException {

        FollowUpRecommendation fp = new FollowUpRecommendation();

        fp.setFollowUpId(rs.getInt("follow_up_id"));
        fp.setAppointmentId(rs.getInt("appointment_id"));
        fp.setPatientId(rs.getInt("patient_id"));
        fp.setDoctorId(rs.getInt("doctor_id"));

        Date recommendedDate = rs.getDate("recommended_date");

        if (recommendedDate != null) {
            fp.setRecommendedDate(recommendedDate.toLocalDate());
        }

        fp.setInstruction(rs.getString("instruction"));
        fp.setStatus(rs.getString("status"));

        Timestamp createdAt = rs.getTimestamp("created_at");

        if (createdAt != null) {
            fp.setCreatedAt(createdAt.toLocalDateTime());
        }

        return fp;
    }

    // ADD FOLLOW-UP

    public boolean addFollowUp(FollowUpRecommendation fp) {

        if (!isValidFollowUp(fp)) {
            return false;
        }

        if (!isValidPatientDoctorAppointment(fp)) {
            return false;
        }

        String sql = "INSERT INTO follow_up_recommendations "
                + "(appointment_id, patient_id, doctor_id, recommended_date, instruction, status) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection()) {

            if (con != null) {

                try (PreparedStatement ps = con.prepareStatement(
                        sql, Statement.RETURN_GENERATED_KEYS)) {

                    ps.setInt(1, fp.getAppointmentId());
                    ps.setInt(2, fp.getPatientId());
                    ps.setInt(3, fp.getDoctorId());
                    ps.setDate(4, Date.valueOf(fp.getRecommendedDate()));
                    ps.setString(5, fp.getInstruction());
                    ps.setString(6, "Pending");

                    int rows = ps.executeUpdate();

                    if (rows > 0) {

                        try (ResultSet rs = ps.getGeneratedKeys()) {

                            if (rs.next()) {
                                fp.setFollowUpId(rs.getInt(1));
                            } else {
                                return false;
                            }
                        }

                        try (PreparedStatement ps2 = con.prepareStatement(
                                "SELECT created_at FROM follow_up_recommendations WHERE follow_up_id = ?")) {

                            ps2.setInt(1, fp.getFollowUpId());

                            try (ResultSet rs = ps2.executeQuery()) {
                                if (rs.next()) {
                                    fp.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                                }
                            }
                        }

                        fp.setStatus("Pending");
                        return true;
                    }
                }
            }

        } catch (SQLException e) {
            System.out.println("Follow-up Add DB Error: " + e.getMessage());
        }

        // IN-MEMORY FALLBACK

        fp.setFollowUpId(nextFollowUpId++);
        fp.setStatus("Pending");
        fp.setCreatedAt(LocalDateTime.now());

        Follows.add(fp);

        return true;
    }

    // FIND BY FOLLOW-UP ID

    public FollowUpRecommendation findByFollowUpId(int followUpId) {

        if (followUpId <= 0) {
            return null;
        }

        String sql = "SELECT * FROM follow_up_recommendations WHERE follow_up_id = ?";

        try (Connection con = DBConnection.getConnection()) {

            if (con != null) {

                try (PreparedStatement ps = con.prepareStatement(sql)) {

                    ps.setInt(1, followUpId);

                    try (ResultSet rs = ps.executeQuery()) {

                        if (rs.next()) {
                            return mapFollowUp(rs);
                        }

                        return null;
                    }
                }
            }

        } catch (SQLException e) {
            System.out.println("Find Follow-up DB Error: " + e.getMessage());
        }

        for (FollowUpRecommendation fp : Follows) {

            if (fp.getFollowUpId() == followUpId) {
                return fp;
            }
        }

        return null;
    }

    // FIND BY APPOINTMENT ID

    public FollowUpRecommendation findByAppointmentId(int appointmentId) {

        if (appointmentId <= 0) {
            return null;
        }

        String sql = "SELECT * FROM follow_up_recommendations WHERE appointment_id = ? ORDER BY follow_up_id LIMIT 1";

        try (Connection con = DBConnection.getConnection()) {

            if (con != null) {

                try (PreparedStatement ps = con.prepareStatement(sql)) {

                    ps.setInt(1, appointmentId);

                    try (ResultSet rs = ps.executeQuery()) {

                        if (rs.next()) {
                            return mapFollowUp(rs);
                        }

                        return null;
                    }
                }
            }

        } catch (SQLException e) {
            System.out.println("Find Follow-up By Appointment DB Error: " + e.getMessage());
        }

        for (FollowUpRecommendation fp : Follows) {

            if (fp.getAppointmentId() == appointmentId) {
                return fp;
            }
        }

        return null;
    }

    // FIND ALL

    public List<FollowUpRecommendation> findAll() {

        List<FollowUpRecommendation> result = new ArrayList<>();

        String sql = "SELECT * FROM follow_up_recommendations ORDER BY follow_up_id";

        try (Connection con = DBConnection.getConnection()) {

            if (con != null) {

                try (PreparedStatement ps = con.prepareStatement(sql);
                     ResultSet rs = ps.executeQuery()) {

                    while (rs.next()) {
                        result.add(mapFollowUp(rs));
                    }

                    return result;
                }
            }

        } catch (SQLException e) {
            System.out.println("Find All Follow-ups DB Error: " + e.getMessage());
        }

        return new ArrayList<>(Follows);
    }

    // FIND BY PATIENT ID

    public List<FollowUpRecommendation> findByPatientId(int patientId) {

        List<FollowUpRecommendation> result = new ArrayList<>();

        if (patientId <= 0) {
            return result;
        }

        String sql = "SELECT * FROM follow_up_recommendations WHERE patient_id = ? ORDER BY follow_up_id";

        try (Connection con = DBConnection.getConnection()) {

            if (con != null) {

                try (PreparedStatement ps = con.prepareStatement(sql)) {

                    ps.setInt(1, patientId);

                    try (ResultSet rs = ps.executeQuery()) {

                        while (rs.next()) {
                            result.add(mapFollowUp(rs));
                        }

                        return result;
                    }
                }
            }

        } catch (SQLException e) {
            System.out.println("Find Follow-ups By Patient DB Error: " + e.getMessage());
        }

        for (FollowUpRecommendation fp : Follows) {

            if (fp.getPatientId() == patientId) {
                result.add(fp);
            }
        }

        return result;
    }

    // FIND BY DOCTOR ID

    public List<FollowUpRecommendation> findByDoctorId(int doctorId) {

        List<FollowUpRecommendation> result = new ArrayList<>();

        if (doctorId <= 0) {
            return result;
        }

        String sql = "SELECT * FROM follow_up_recommendations WHERE doctor_id = ? ORDER BY follow_up_id";

        try (Connection con = DBConnection.getConnection()) {

            if (con != null) {

                try (PreparedStatement ps = con.prepareStatement(sql)) {

                    ps.setInt(1, doctorId);

                    try (ResultSet rs = ps.executeQuery()) {

                        while (rs.next()) {
                            result.add(mapFollowUp(rs));
                        }

                        return result;
                    }
                }
            }

        } catch (SQLException e) {
            System.out.println("Find Follow-ups By Doctor DB Error: " + e.getMessage());
        }

        for (FollowUpRecommendation fp : Follows) {

            if (fp.getDoctorId() == doctorId) {
                result.add(fp);
            }
        }

        return result;
    }

    // UPDATE FOLLOW-UP

    public boolean updateFollowUp(FollowUpRecommendation fp) {

        if (!isValidFollowUp(fp)
                || fp.getFollowUpId() <= 0
                || !isValidStatus(fp.getStatus())) {
            return false;
        }

        if (!isValidPatientDoctorAppointment(fp)) {
            return false;
        }

        String sql = "UPDATE follow_up_recommendations SET "
                + "appointment_id = ?, patient_id = ?, doctor_id = ?, "
                + "recommended_date = ?, instruction = ?, status = ? "
                + "WHERE follow_up_id = ?";

        try (Connection con = DBConnection.getConnection()) {

            if (con != null) {

                try (PreparedStatement ps = con.prepareStatement(sql)) {

                    ps.setInt(1, fp.getAppointmentId());
                    ps.setInt(2, fp.getPatientId());
                    ps.setInt(3, fp.getDoctorId());
                    ps.setDate(4, Date.valueOf(fp.getRecommendedDate()));
                    ps.setString(5, fp.getInstruction());
                    ps.setString(6, fp.getStatus());
                    ps.setInt(7, fp.getFollowUpId());

                    return ps.executeUpdate() > 0;
                }
            }

        } catch (SQLException e) {
            System.out.println("Update Follow-up DB Error: " + e.getMessage());
        }

        // IN-MEMORY FALLBACK

        FollowUpRecommendation exist = findByFollowUpId(fp.getFollowUpId());

        if (exist == null) {
            return false;
        }

        exist.setAppointmentId(fp.getAppointmentId());
        exist.setPatientId(fp.getPatientId());
        exist.setDoctorId(fp.getDoctorId());
        exist.setStatus(fp.getStatus());
        exist.setRecommendedDate(fp.getRecommendedDate());
        exist.setInstruction(fp.getInstruction());

        return true;
    }

    // DELETE FOLLOW-UP

    public boolean deleteFollowUp(int followUpId) {

        if (followUpId <= 0) {
            return false;
        }

        String sql = "DELETE FROM follow_up_recommendations WHERE follow_up_id = ?";

        try (Connection con = DBConnection.getConnection()) {

            if (con != null) {

                try (PreparedStatement ps = con.prepareStatement(sql)) {

                    ps.setInt(1, followUpId);

                    return ps.executeUpdate() > 0;
                }
            }

        } catch (SQLException e) {
            System.out.println("Delete Follow-up DB Error: " + e.getMessage());
        }

        // IN-MEMORY FALLBACK

        FollowUpRecommendation fp = findByFollowUpId(followUpId);

        if (fp == null) {
            return false;
        }

        return Follows.remove(fp);
    }
}
