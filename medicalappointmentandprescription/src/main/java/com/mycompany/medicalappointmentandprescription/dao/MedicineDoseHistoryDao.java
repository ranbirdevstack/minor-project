
package com.mycompany.medicalappointmentandprescription.dao;

import com.mycompany.medicalappointmentandprescription.models.MedicineDoseHistory;
import com.mycompany.medicalappointmentandprescription.models.Patient;
import com.mycompany.medicalappointmentandprescription.models.MedicineReminder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

public class MedicineDoseHistoryDao {

    private static final List<MedicineDoseHistory> doses = new ArrayList<>();

    private static int nextHistoryId = 4;

    private final MedicineReminderDao mrd;

    public MedicineDoseHistoryDao(MedicineReminderDao mrd) {

        if (mrd == null) {
            throw new IllegalArgumentException(
                    "MedicineDoseHistoryDao can't be null");
        }

        this.mrd = mrd;
    }

    // =========================================
    // IN-MEMORY FALLBACK DEMO RECORDS
    // =========================================

    static {

        LocalDateTime now = LocalDateTime.now();

        doses.add(new MedicineDoseHistory(
                1, 1, 1, "Paracetamol", "500mg",
                now.minusMinutes(1), now, "Given"));

        doses.add(new MedicineDoseHistory(
                2, 2, 2, "Cetraeizine", "10mg",
                now.plusHours(2),
                now.plusHours(2).plusMinutes(2), "Given"));

        doses.add(new MedicineDoseHistory(
                3, 3, 3, "Paracetamol", "500mg",
                now.plusHours(4),
                now.plusHours(4).plusMinutes(3), "Given"));
    }

    // =========================================
// ADD HISTORY USING EXISTING TRANSACTION
// =========================================

boolean addHistory(Connection con, MedicineDoseHistory h)
        throws SQLException {

    if (con == null || !isValidHistory(h)) {
        return false;
    }

    if (!validateReferences(h)) {
        return false;
    }

    String status = h.getStatus().trim();

    if (!status.equalsIgnoreCase("Given")
            && !status.equalsIgnoreCase("Skipped")) {
        return false;
    }

    if (status.equalsIgnoreCase("Given")
            && h.getGivenAt().isBefore(h.getScheduledAt())) {
        return false;
    }

    h.setMedicineName(h.getMedicineName().trim());
    h.setDosage(h.getDosage().trim());

    h.setStatus(
            status.equalsIgnoreCase("Given")
                    ? "Given" : "Skipped"
    );

    String sql = "INSERT INTO medicine_dose_history "
            + "(reminder_id, patient_id, medicine_name, dosage, "
            + "scheduled_at, given_at, status) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?)";

    try (PreparedStatement ps = con.prepareStatement(
            sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

        ps.setInt(1, h.getReminderId());
        ps.setInt(2, h.getPatientId());
        ps.setString(3, h.getMedicineName());
        ps.setString(4, h.getDosage());
        ps.setTimestamp(5,
                Timestamp.valueOf(h.getScheduledAt()));
        ps.setTimestamp(6,
                Timestamp.valueOf(h.getGivenAt()));
        ps.setString(7, h.getStatus());

        int rows = ps.executeUpdate();

        if (rows <= 0) {
            return false;
        }

        try (ResultSet rs = ps.getGeneratedKeys()) {

            if (rs.next()) {
                h.setHistoryId(rs.getInt(1));
            } else {
                throw new SQLException(
                        "Failed to generate dose history ID.");
            }
        }

        return true;
    }
}
   public boolean addHistory(MedicineDoseHistory h) {

    if (!isValidHistory(h)) {
        return false;
    }

    if (!validateReferences(h)) {
        return false;
    }

    String status = h.getStatus().trim();

    if (!status.equalsIgnoreCase("Given")
            && !status.equalsIgnoreCase("Skipped")) {
        return false;
    }

    if (status.equalsIgnoreCase("Given")
            && h.getGivenAt().isBefore(h.getScheduledAt())) {
        return false;
    }

    h.setMedicineName(h.getMedicineName().trim());
    h.setDosage(h.getDosage().trim());

    h.setStatus(
            status.equalsIgnoreCase("Given")
                    ? "Given" : "Skipped");

    String sql = "INSERT INTO medicine_dose_history "
            + "(reminder_id, patient_id, medicine_name, dosage, "
            + "scheduled_at, given_at, status) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?)";

    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(
                 sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

        ps.setInt(1, h.getReminderId());
        ps.setInt(2, h.getPatientId());
        ps.setString(3, h.getMedicineName());
        ps.setString(4, h.getDosage());
        ps.setTimestamp(5,
                Timestamp.valueOf(h.getScheduledAt()));
        ps.setTimestamp(6,
                Timestamp.valueOf(h.getGivenAt()));
        ps.setString(7, h.getStatus());

        int rows = ps.executeUpdate();

        if (rows > 0) {

            try (ResultSet rs = ps.getGeneratedKeys()) {

                if (rs.next()) {
                    h.setHistoryId(rs.getInt(1));
                }
            }

            syncHistoryFromDatabase();
            return true;
        }

    } catch (SQLException e) {

        if (!isConnectionFailure(e)) {

            System.err.println(
                    "Add Dose History Error: " + e.getMessage());

            return false;
        }

        System.err.println(
                "Database unavailable. Using in-memory fallback.");
    }

    h.setHistoryId(nextHistoryId++);
    doses.add(h);

    return true;
}

    // =========================================
    // FIND BY HISTORY ID
    // =========================================

    public MedicineDoseHistory findByHistoryId(int historyId) {

        syncHistoryFromDatabase();

        for (MedicineDoseHistory h : doses) {

            if (h.getHistoryId() == historyId) {
                return h;
            }
        }

        return null;
    }

    // =========================================
    // FIND ALL
    // =========================================

    public List<MedicineDoseHistory> findAll() {

        syncHistoryFromDatabase();

        return new ArrayList<>(doses);
    }

    // =========================================
    // FIND BY PATIENT ID
    // =========================================

    public List<MedicineDoseHistory> findByPatientId(int patientId) {

        syncHistoryFromDatabase();

        List<MedicineDoseHistory> result = new ArrayList<>();

        for (MedicineDoseHistory h : doses) {

            if (h.getPatientId() == patientId) {
                result.add(h);
            }
        }

        return result;
    }

    // =========================================
    // FIND BY REMINDER ID
    // =========================================

    public List<MedicineDoseHistory> findByReminderId(int reminderId) {

        syncHistoryFromDatabase();

        List<MedicineDoseHistory> result = new ArrayList<>();

        for (MedicineDoseHistory h : doses) {

            if (h.getReminderId() == reminderId) {
                result.add(h);
            }
        }

        return result;
    }

    // =========================================
    // UPDATE HISTORY
    // =========================================

    public boolean updateHistory(MedicineDoseHistory h) {

        if (!isValidHistory(h) || h.getHistoryId() <= 0) {
            return false;
        }

        MedicineDoseHistory exist =
                findByHistoryId(h.getHistoryId());

        if (exist == null || !validateReferences(h)) {
            return false;
        }

        String status = h.getStatus().trim();

        if (!status.equalsIgnoreCase("Given")
                && !status.equalsIgnoreCase("Skipped")) {
            return false;
        }

        if (status.equalsIgnoreCase("Given")
                && h.getGivenAt().isBefore(h.getScheduledAt())) {
            return false;
        }

        String finalStatus =
                status.equalsIgnoreCase("Given")
                        ? "Given" : "Skipped";

        String sql = "UPDATE medicine_dose_history SET "
                + "reminder_id=?, patient_id=?, medicine_name=?, "
                + "dosage=?, scheduled_at=?, given_at=?, status=? "
                + "WHERE history_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, h.getReminderId());
            ps.setInt(2, h.getPatientId());
            ps.setString(3, h.getMedicineName().trim());
            ps.setString(4, h.getDosage().trim());
            ps.setTimestamp(5,
                    Timestamp.valueOf(h.getScheduledAt()));
            ps.setTimestamp(6,
                    Timestamp.valueOf(h.getGivenAt()));
            ps.setString(7, finalStatus);
            ps.setInt(8, h.getHistoryId());

            boolean updated = ps.executeUpdate() > 0;

            if (updated) {
                syncHistoryFromDatabase();
            }

            return updated;

        } catch (SQLException e) {

            if (!isConnectionFailure(e)) {

                System.err.println(
                        "Update Dose History Error: " + e.getMessage());

                return false;
            }
        }

        exist.setReminderId(h.getReminderId());
        exist.setPatientId(h.getPatientId());
        exist.setMedicineName(h.getMedicineName().trim());
        exist.setDosage(h.getDosage().trim());
        exist.setScheduledAt(h.getScheduledAt());
        exist.setGivenAt(h.getGivenAt());
        exist.setStatus(finalStatus);

        return true;
    }

    // =========================================
    // DELETE HISTORY
    // =========================================

    public boolean deleteHistory(int historyId) {

        MedicineDoseHistory h = findByHistoryId(historyId);

        if (h == null) {
            return false;
        }

        String sql =
                "DELETE FROM medicine_dose_history WHERE history_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, historyId);

            boolean deleted = ps.executeUpdate() > 0;

            if (deleted) {
                syncHistoryFromDatabase();
            }

            return deleted;

        } catch (SQLException e) {

            if (!isConnectionFailure(e)) {

                System.err.println(
                        "Delete Dose History Error: " + e.getMessage());

                return false;
            }
        }

        return doses.remove(h);
    }

    // =========================================
    // VALIDATION
    // =========================================

    private boolean isValidHistory(MedicineDoseHistory h) {

        return h != null
                && h.getReminderId() > 0
                && h.getPatientId() > 0
                && h.getMedicineName() != null
                && !h.getMedicineName().trim().isEmpty()
                && h.getDosage() != null
                && !h.getDosage().trim().isEmpty()
                && h.getGivenAt() != null
                && h.getScheduledAt() != null
                && h.getStatus() != null
                && !h.getStatus().trim().isEmpty();
    }

    private boolean validateReferences(MedicineDoseHistory h) {

        MedicineReminder mr =
                mrd.findByReminderId(h.getReminderId());

        if (mr == null
                || mr.getPatientId() != h.getPatientId()
                || !mr.getMedicineName().trim()
                        .equalsIgnoreCase(h.getMedicineName())
                || !mr.getDosage().trim()
                        .equalsIgnoreCase(h.getDosage())) {

            return false;
        }

        PatientDao pd = new PatientDao();

        Patient p = pd.findByPatientId(h.getPatientId());

        return p != null;
    }

    // =========================================
    // DATABASE SYNC
    // =========================================

    private boolean syncHistoryFromDatabase() {

        List<MedicineDoseHistory> temp = new ArrayList<>();

        String sql = "SELECT * FROM medicine_dose_history "
                + "ORDER BY history_id";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                temp.add(mapHistory(rs));
            }

            doses.clear();
            doses.addAll(temp);

            nextHistoryId = 1;

            for (MedicineDoseHistory h : doses) {

                if (h.getHistoryId() >= nextHistoryId) {
                    nextHistoryId = h.getHistoryId() + 1;
                }
            }

            return true;

        } catch (SQLException e) {

            if (!isConnectionFailure(e)) {

                System.err.println(
                        "Sync Dose History Error: " + e.getMessage());
            }

            return false;
        }
    }

    // =========================================
    // RESULT SET MAPPING
    // =========================================

    private MedicineDoseHistory mapHistory(ResultSet rs)
            throws SQLException {

        Timestamp scheduledTimestamp =
                rs.getTimestamp("scheduled_at");

        Timestamp givenTimestamp =
                rs.getTimestamp("given_at");

        LocalDateTime scheduledAt =
                scheduledTimestamp.toLocalDateTime();

        LocalDateTime givenAt =
                givenTimestamp.toLocalDateTime();

        return new MedicineDoseHistory(
                rs.getInt("reminder_id"),
                rs.getInt("patient_id"),
                rs.getInt("history_id"),
                rs.getString("medicine_name"),
                rs.getString("dosage"),
                scheduledAt,
                givenAt,
                rs.getString("status")
        );
    }

    // =========================================
    // CONNECTION FAILURE CHECK
    // =========================================

    private boolean isConnectionFailure(SQLException e) {

        String state = e.getSQLState();

        return state == null || state.startsWith("08");
    }
}
