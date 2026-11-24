package com.mycompany.medicalappointmentandprescription.dao;

import com.mycompany.medicalappointmentandprescription.models.Appointment;
import com.mycompany.medicalappointmentandprescription.models.Doctor;
import com.mycompany.medicalappointmentandprescription.models.Patient;
import com.mycompany.medicalappointmentandprescription.models.Prescription;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

public class AppointmentDao {

    private static final List<Appointment> Appointments = new ArrayList<>();
    private static int nextAppointmentId = 4;

    private static final String SELECT_ALL =
            "SELECT * FROM appointments ORDER BY appointment_id";

    // Constructor
    public AppointmentDao() {
    }

    // =========================
    // ADD APPOINTMENT
    // =========================
    public boolean addAppointment(Appointment a) {

        if (!isValidAppointment(a)) {
            return false;
        }

        DoctorDao dd = new DoctorDao();
        PatientDao ptd = new PatientDao();

        Patient p = ptd.findByPatientId(a.getPatientId());

        if (p == null) {
            return false;
        }

        Doctor d = dd.findByDoctorId(a.getDoctorId());

        if (d == null || !d.getAvaliabilityStatus()) {
            return false;
        }

        if (a.getAppointmentDate().isBefore(LocalDate.now())) {
            return false;
        }

        try (Connection con = DBConnection.getConnection()) {

            if (con == null) {
                return addAppointmentToMemory(a);
            }

            if (isSlotBooked(
                    con,
                    a.getDoctorId(),
                    a.getAppointmentDate(),
                    a.getAppointmentTime(),
                    0)) {
                return false;
            }

            String sql = "INSERT INTO appointments "
                    + "(patient_id, doctor_id, appointment_date, "
                    + "appointment_time, status) "
                    + "VALUES (?, ?, ?, ?, ?)";

            try (PreparedStatement ps = con.prepareStatement(
                    sql, Statement.RETURN_GENERATED_KEYS)) {

                ps.setInt(1, a.getPatientId());
                ps.setInt(2, a.getDoctorId());
                ps.setDate(3, java.sql.Date.valueOf(a.getAppointmentDate()));
                ps.setTime(4, java.sql.Time.valueOf(a.getAppointmentTime()));
                ps.setString(5, "Pending");

                int rows = ps.executeUpdate();

                if (rows > 0) {

                    try (ResultSet rs = ps.getGeneratedKeys()) {

                        if (rs.next()) {
                            a.setAppointmentId(rs.getInt(1));
                        }
                    }

                    a.setStatus("Pending");
                    a.setCreatedAt(LocalDateTime.now());

                    syncAppointmentsFromDatabase();

                    return true;
                }
            }

        } catch (SQLException e) {

            if (isConnectionFailure(e)) {
                return addAppointmentToMemory(a);
            }

            e.printStackTrace();
            return false;
        }

        return false;
    }

    // =========================
    // FIND BY APPOINTMENT ID
    // =========================
    public Appointment findByAppointmentId(int appointmentId) {

        if (appointmentId <= 0) {
            return null;
        }

        try (Connection con = DBConnection.getConnection()) {

            if (con == null) {
                return findByAppointmentIdInMemory(appointmentId);
            }

            String sql = "SELECT * FROM appointments WHERE appointment_id = ?";

            try (PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setInt(1, appointmentId);

                try (ResultSet rs = ps.executeQuery()) {

                    if (rs.next()) {
                        return mapAppointment(rs);
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
    public List<Appointment> findAll() {

        try (Connection con = DBConnection.getConnection()) {

            if (con == null) {
                return new ArrayList<>(Appointments);
            }

            List<Appointment> result = new ArrayList<>();

            try (PreparedStatement ps = con.prepareStatement(SELECT_ALL);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    result.add(mapAppointment(rs));
                }
            }

            return result;

        } catch (SQLException e) {

            if (isConnectionFailure(e)) {
                return new ArrayList<>(Appointments);
            }

            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    // =========================
    // FIND BY PATIENT ID
    // =========================
    public List<Appointment> findByPatientId(int patientId) {

        List<Appointment> result = new ArrayList<>();

        if (patientId <= 0) {
            return result;
        }

        try (Connection con = DBConnection.getConnection()) {

            if (con == null) {
                return findByPatientIdInMemory(patientId);
            }

            String sql = "SELECT * FROM appointments "
                    + "WHERE patient_id = ? ORDER BY appointment_date, appointment_time";

            try (PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setInt(1, patientId);

                try (ResultSet rs = ps.executeQuery()) {

                    while (rs.next()) {
                        result.add(mapAppointment(rs));
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
    public List<Appointment> findByDoctorId(int doctorId) {

        List<Appointment> result = new ArrayList<>();

        if (doctorId <= 0) {
            return result;
        }

        try (Connection con = DBConnection.getConnection()) {

            if (con == null) {
                return findByDoctorIdInMemory(doctorId);
            }

            String sql = "SELECT * FROM appointments "
                    + "WHERE doctor_id = ? ORDER BY appointment_date, appointment_time";

            try (PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setInt(1, doctorId);

                try (ResultSet rs = ps.executeQuery()) {

                    while (rs.next()) {
                        result.add(mapAppointment(rs));
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
    // FIND BY DATE
    // =========================
    public List<Appointment> findByDate(LocalDate Date) {

        List<Appointment> result = new ArrayList<>();

        if (Date == null) {
            return result;
        }

        try (Connection con = DBConnection.getConnection()) {

            if (con == null) {
                return findByDateInMemory(Date);
            }

            String sql = "SELECT * FROM appointments WHERE appointment_date = ?";

            try (PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setDate(1, java.sql.Date.valueOf(Date));

                try (ResultSet rs = ps.executeQuery()) {

                    while (rs.next()) {
                        result.add(mapAppointment(rs));
                    }
                }
            }

            return result;

        } catch (SQLException e) {

            if (isConnectionFailure(e)) {
                return findByDateInMemory(Date);
            }

            e.printStackTrace();
        }

        return result;
    }

    // =========================
    // FIND BY TIME
    // =========================
    public List<Appointment> findByTime(LocalTime Time) {

        List<Appointment> result = new ArrayList<>();

        if (Time == null) {
            return result;
        }

        try (Connection con = DBConnection.getConnection()) {

            if (con == null) {
                return findByTimeInMemory(Time);
            }

            String sql = "SELECT * FROM appointments WHERE appointment_time = ?";

            try (PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setTime(1, java.sql.Time.valueOf(Time));

                try (ResultSet rs = ps.executeQuery()) {

                    while (rs.next()) {
                        result.add(mapAppointment(rs));
                    }
                }
            }

            return result;

        } catch (SQLException e) {

            if (isConnectionFailure(e)) {
                return findByTimeInMemory(Time);
            }

            e.printStackTrace();
        }

        return result;
    }

    // =========================
    // FIND BY DOCTOR, DATE AND TIME
    // =========================
    public List<Appointment> findByDoctorDateAndTime(
            int doctorId, LocalTime Time, LocalDate Date) {

        List<Appointment> result = new ArrayList<>();

        if (doctorId <= 0 || Date == null || Time == null) {
            return result;
        }

        try (Connection con = DBConnection.getConnection()) {

            if (con == null) {
                return findByDoctorDateAndTimeInMemory(
                        doctorId, Time, Date);
            }

            String sql = "SELECT * FROM appointments "
                    + "WHERE doctor_id = ? "
                    + "AND appointment_time = ? "
                    + "AND appointment_date = ?";

            try (PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setInt(1, doctorId);
                ps.setTime(2, java.sql.Time.valueOf(Time));
                ps.setDate(3, java.sql.Date.valueOf(Date));

                try (ResultSet rs = ps.executeQuery()) {

                    while (rs.next()) {
                        result.add(mapAppointment(rs));
                    }
                }
            }

            return result;

        } catch (SQLException e) {

            if (isConnectionFailure(e)) {
                return findByDoctorDateAndTimeInMemory(
                        doctorId, Time, Date);
            }

            e.printStackTrace();
        }

        return result;
    }

    // =========================
    // UPDATE APPOINTMENT
    // =========================
    public boolean updateAppointment(Appointment a) {

        if (!isValidAppointment(a) || a.getAppointmentId() <= 0) {
            return false;
        }

        DoctorDao dd = new DoctorDao();
        PatientDao ptd = new PatientDao();

        Patient p = ptd.findByPatientId(a.getPatientId());

        if (p == null) {
            return false;
        }

        Doctor d = dd.findByDoctorId(a.getDoctorId());

        if (d == null) {
            return false;
        }

        String status = a.getStatus();

        if (!isValidStatus(status)) {
            return false;
        }

        if (a.getAppointmentDate().isBefore(LocalDate.now())) {
            return false;
        }

        try (Connection con = DBConnection.getConnection()) {

            if (con == null) {
                return updateAppointmentInMemory(a);
            }

            Appointment existing = findByAppointmentId(a.getAppointmentId());

            if (existing == null) {
                return false;
            }

            boolean slotChanged =
                    existing.getDoctorId() != a.getDoctorId()
                    || !existing.getAppointmentDate().equals(a.getAppointmentDate())
                    || !existing.getAppointmentTime().equals(a.getAppointmentTime());

            if (slotChanged && !d.getAvaliabilityStatus()) {
                return false;
            }

            if (isSlotBooked(
                    con,
                    a.getDoctorId(),
                    a.getAppointmentDate(),
                    a.getAppointmentTime(),
                    a.getAppointmentId())) {
                return false;
            }

            String sql = "UPDATE appointments SET "
                    + "patient_id = ?, doctor_id = ?, "
                    + "appointment_date = ?, appointment_time = ?, status = ? "
                    + "WHERE appointment_id = ?";

            try (PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setInt(1, a.getPatientId());
                ps.setInt(2, a.getDoctorId());
                ps.setDate(3, java.sql.Date.valueOf(a.getAppointmentDate()));
                ps.setTime(4, java.sql.Time.valueOf(a.getAppointmentTime()));
                ps.setString(5, status);
                ps.setInt(6, a.getAppointmentId());

                boolean updated = ps.executeUpdate() > 0;

                if (updated) {
                    syncAppointmentsFromDatabase();
                }

                return updated;
            }

        } catch (SQLException e) {

            if (isConnectionFailure(e)) {
                return updateAppointmentInMemory(a);
            }

            e.printStackTrace();
            return false;
        }
    }

    // =========================
    // DELETE APPOINTMENT
    // =========================
    public boolean deleteAppointment(int appointmentId) {

        if (appointmentId <= 0) {
            return false;
        }

        try (Connection con = DBConnection.getConnection()) {

            if (con == null) {
                return deleteAppointmentFromMemory(appointmentId);
            }

            Appointment existing = findByAppointmentId(appointmentId);

            if (existing == null) {
                return false;
            }

            // Check linked prescriptions
            String checkSql = "SELECT COUNT(*) FROM prescriptions "
                    + "WHERE appointment_id = ?";

            try (PreparedStatement ps = con.prepareStatement(checkSql)) {

                ps.setInt(1, appointmentId);

                try (ResultSet rs = ps.executeQuery()) {

                    if (rs.next() && rs.getInt(1) > 0) {
                        return false;
                    }
                }
            }

            String sql = "DELETE FROM appointments WHERE appointment_id = ?";

            try (PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setInt(1, appointmentId);

                boolean deleted = ps.executeUpdate() > 0;

                if (deleted) {
                    syncAppointmentsFromDatabase();
                }

                return deleted;
            }

        } catch (SQLException e) {

            // Other foreign-key references may also prevent deletion.
            if (isConnectionFailure(e)) {
                return deleteAppointmentFromMemory(appointmentId);
            }

            e.printStackTrace();
            return false;
        }
    }

    // =========================
    // DATABASE SYNC
    // =========================
    private void syncAppointmentsFromDatabase() {

        List<Appointment> temp = new ArrayList<>();
        int maxId = 0;

        try (Connection con = DBConnection.getConnection()) {

            if (con == null) {
                return;
            }

            try (PreparedStatement ps = con.prepareStatement(SELECT_ALL);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    Appointment a = mapAppointment(rs);

                    temp.add(a);

                    if (a.getAppointmentId() > maxId) {
                        maxId = a.getAppointmentId();
                    }
                }
            }

            Appointments.clear();
            Appointments.addAll(temp);

            nextAppointmentId = maxId + 1;

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // =========================
    // RESULT SET MAPPING
    // =========================
    private Appointment mapAppointment(ResultSet rs) throws SQLException {

        return new Appointment(
                rs.getInt("appointment_id"),
                rs.getInt("patient_id"),
                rs.getInt("doctor_id"),
                rs.getDate("appointment_date").toLocalDate(),
                rs.getTime("appointment_time").toLocalTime(),
                rs.getString("status"),
                rs.getTimestamp("created_at").toLocalDateTime()
        );
    }

    // =========================
    // SLOT CHECK
    // =========================
    private boolean isSlotBooked(
            Connection con,
            int doctorId,
            LocalDate date,
            LocalTime time,
            int excludeAppointmentId) throws SQLException {

        String sql = "SELECT COUNT(*) FROM appointments "
                + "WHERE doctor_id = ? "
                + "AND appointment_date = ? "
                + "AND appointment_time = ? "
                + "AND LOWER(status) <> 'cancelled' "
                + "AND appointment_id <> ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, doctorId);
            ps.setDate(2, java.sql.Date.valueOf(date));
            ps.setTime(3, java.sql.Time.valueOf(time));
            ps.setInt(4, excludeAppointmentId);

            try (ResultSet rs = ps.executeQuery()) {

                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    // =========================
    // VALIDATION
    // =========================
    private boolean isValidAppointment(Appointment a) {

        return a != null
                && a.getPatientId() > 0
                && a.getDoctorId() > 0
                && a.getAppointmentDate() != null
                && a.getAppointmentTime() != null;
    }

    private boolean isValidStatus(String status) {

        return "Pending".equalsIgnoreCase(status)
                || "Confirmed".equalsIgnoreCase(status)
                || "Completed".equalsIgnoreCase(status)
                || "Cancelled".equalsIgnoreCase(status);
    }

    private boolean isConnectionFailure(SQLException e) {

        String state = e.getSQLState();

        return state == null || state.startsWith("08");
    }

    // =========================
    // IN-MEMORY ADD
    // =========================
    private boolean addAppointmentToMemory(Appointment a) {

        for (Appointment existing : Appointments) {

            if (existing.getDoctorId() == a.getDoctorId()
                    && existing.getAppointmentDate().equals(a.getAppointmentDate())
                    && existing.getAppointmentTime().equals(a.getAppointmentTime())
                    && !"Cancelled".equalsIgnoreCase(existing.getStatus())) {

                return false;
            }
        }

        a.setAppointmentId(nextAppointmentId++);
        a.setStatus("Pending");
        a.setCreatedAt(LocalDateTime.now());

        Appointments.add(a);

        return true;
    }

    // =========================
    // IN-MEMORY FIND METHODS
    // =========================
    private Appointment findByAppointmentIdInMemory(int appointmentId) {

        for (Appointment a : Appointments) {

            if (a.getAppointmentId() == appointmentId) {
                return a;
            }
        }

        return null;
    }

    private List<Appointment> findByPatientIdInMemory(int patientId) {

        List<Appointment> result = new ArrayList<>();

        for (Appointment a : Appointments) {

            if (a.getPatientId() == patientId) {
                result.add(a);
            }
        }

        return result;
    }

    private List<Appointment> findByDoctorIdInMemory(int doctorId) {

        List<Appointment> result = new ArrayList<>();

        for (Appointment a : Appointments) {

            if (a.getDoctorId() == doctorId) {
                result.add(a);
            }
        }

        return result;
    }

    private List<Appointment> findByDateInMemory(LocalDate Date) {

        List<Appointment> result = new ArrayList<>();

        for (Appointment a : Appointments) {

            if (a.getAppointmentDate().equals(Date)) {
                result.add(a);
            }
        }

        return result;
    }

    private List<Appointment> findByTimeInMemory(LocalTime Time) {

        List<Appointment> result = new ArrayList<>();

        for (Appointment a : Appointments) {

            if (a.getAppointmentTime().equals(Time)) {
                result.add(a);
            }
        }

        return result;
    }

    private List<Appointment> findByDoctorDateAndTimeInMemory(
            int doctorId, LocalTime Time, LocalDate Date) {

        List<Appointment> result = new ArrayList<>();

        for (Appointment a : Appointments) {

            if (a.getDoctorId() == doctorId
                    && a.getAppointmentTime().equals(Time)
                    && a.getAppointmentDate().equals(Date)) {

                result.add(a);
            }
        }

        return result;
    }

    // =========================
    // IN-MEMORY UPDATE
    // =========================
    private boolean updateAppointmentInMemory(Appointment a) {

        Appointment existing =
                findByAppointmentIdInMemory(a.getAppointmentId());

        if (existing == null || !isValidStatus(a.getStatus())) {
            return false;
        }

        Doctor d = new DoctorDao().findByDoctorId(a.getDoctorId());

        if (d == null) {
            return false;
        }

        boolean slotChanged =
                existing.getDoctorId() != a.getDoctorId()
                || !existing.getAppointmentDate().equals(a.getAppointmentDate())
                || !existing.getAppointmentTime().equals(a.getAppointmentTime());

        if (slotChanged && !d.getAvaliabilityStatus()) {
            return false;
        }

        for (Appointment other : Appointments) {

            if (other.getAppointmentId() != a.getAppointmentId()
                    && other.getDoctorId() == a.getDoctorId()
                    && other.getAppointmentDate().equals(a.getAppointmentDate())
                    && other.getAppointmentTime().equals(a.getAppointmentTime())
                    && !"Cancelled".equalsIgnoreCase(other.getStatus())
                    && !"Cancelled".equalsIgnoreCase(a.getStatus())) {

                return false;
            }
        }

        existing.setPatientId(a.getPatientId());
        existing.setDoctorId(a.getDoctorId());
        existing.setAppointmentDate(a.getAppointmentDate());
        existing.setAppointmentTime(a.getAppointmentTime());
        existing.setStatus(a.getStatus());

        return true;
    }

    // =========================
    // IN-MEMORY DELETE
    // =========================
    private boolean deleteAppointmentFromMemory(int appointmentId) {

        Appointment a = findByAppointmentIdInMemory(appointmentId);

        if (a == null) {
            return false;
        }

        PrescriptionDao pd = new PrescriptionDao();

        for (Prescription p : pd.findAll()) {

            if (p.getAppointmentId() == appointmentId) {
                return false;
            }
        }

        return Appointments.remove(a);
    }
}
