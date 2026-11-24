package com.mycompany.medicalappointmentandprescription.dao;

import com.mycompany.medicalappointmentandprescription.models.MedicineReminder;
import com.mycompany.medicalappointmentandprescription.models.Patient;
import com.mycompany.medicalappointmentandprescription.models.Prescription;
import com.mycompany.medicalappointmentandprescription.models.MedicineDoseHistory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MedicineReminderDao {

    private static final List<MedicineReminder> Reminders = new ArrayList<>();
    private static int nextReminderId = 4;

    private final MedicineDoseHistoryDao mdhd =
            new MedicineDoseHistoryDao(this);

    // In-memory fallback demo records
    static {
        Reminders.add(new MedicineReminder(
                1, 1, 1, "Paracetamol", "500mg",
                LocalDateTime.now().minusMinutes(1),
                8, null, "Pending"
        ));

        Reminders.add(new MedicineReminder(
                2, 2, 2, "Cetirizine", "10mg",
                LocalDateTime.now().plusHours(2),
                24, null, "Pending"
        ));

        Reminders.add(new MedicineReminder(
                3, 3, 3, "Amoxicillin", "500mg",
                LocalDateTime.now().plusHours(4),
                8, null, "Pending"
        ));
    }

    // =========================================
    // ADD REMINDER
    // =========================================

    public boolean addReminder(MedicineReminder r) {

        if (!isValidReminder(r)) {
            return false;
        }

        if (!validateReferences(r)) {
            return false;
        }

        r.setMedicineName(r.getMedicineName().trim());
        r.setDosage(r.getDosage().trim());
        r.setStatus("Pending");
        r.setSnoozeUntil(null);

        String sql = "INSERT INTO medicine_reminders "
                + "(patient_id, prescription_id, medicine_name, dosage, "
                + "scheduled_at, interval_hours, snooze_until, status) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, r.getPatientId());
            ps.setInt(2, r.getPrescriptionId());
            ps.setString(3, r.getMedicineName());
            ps.setString(4, r.getDosage());
            ps.setTimestamp(5, Timestamp.valueOf(r.getScheduledAt()));
            ps.setInt(6, r.getIntervalHours());

            if (r.getSnoozeUntil() == null) {
                ps.setNull(7, java.sql.Types.TIMESTAMP);
            } else {
                ps.setTimestamp(7,
                        Timestamp.valueOf(r.getSnoozeUntil()));
            }

            ps.setString(8, r.getStatus());

            int rows = ps.executeUpdate();

            if (rows > 0) {

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        r.setReminderId(rs.getInt(1));
                    }
                }

                syncRemindersFromDatabase();
                return true;
            }

        } catch (SQLException e) {

            if (!isConnectionFailure(e)) {
                System.err.println(
                        "Add Reminder Error: " + e.getMessage());
                return false;
            }

            System.err.println(
                    "Database unavailable. Using in-memory fallback.");
        }

        r.setReminderId(nextReminderId++);
        Reminders.add(r);

        return true;
    }

    // =========================================
    // FIND BY REMINDER ID
    // =========================================

    public MedicineReminder findByReminderId(int reminderId) {

        if (syncRemindersFromDatabase()) {

            for (MedicineReminder r : Reminders) {
                if (r.getReminderId() == reminderId) {
                    return r;
                }
            }

            return null;
        }

        for (MedicineReminder r : Reminders) {
            if (r.getReminderId() == reminderId) {
                return r;
            }
        }

        return null;
    }

    // =========================================
    // FIND ALL
    // =========================================

    public List<MedicineReminder> findAll() {

        syncRemindersFromDatabase();

        return new ArrayList<>(Reminders);
    }

    // =========================================
    // FIND BY PATIENT ID
    // =========================================

    public List<MedicineReminder> findByPatientId(int patientId) {

        syncRemindersFromDatabase();

        List<MedicineReminder> result = new ArrayList<>();

        for (MedicineReminder r : Reminders) {

            if (r.getPatientId() == patientId) {
                result.add(r);
            }
        }

        return result;
    }

    // =========================================
    // FIND BY PRESCRIPTION ID
    // =========================================

    public List<MedicineReminder> findByPrescriptionId(int prescriptionId) {

        syncRemindersFromDatabase();

        List<MedicineReminder> result = new ArrayList<>();

        for (MedicineReminder r : Reminders) {

            if (r.getPrescriptionId() == prescriptionId) {
                result.add(r);
            }
        }

        return result;
    }

    // =========================================
    // FIND BY STATUS
    // =========================================

    public List<MedicineReminder> findByStatus(String status) {

        syncRemindersFromDatabase();

        List<MedicineReminder> result = new ArrayList<>();

        if (status == null) {
            return result;
        }

        for (MedicineReminder r : Reminders) {

            if (r.getStatus() != null
                    && r.getStatus().equalsIgnoreCase(status)) {

                result.add(r);
            }
        }

        return result;
    }

    // =========================================
    // FIND DUE REMINDERS
    // =========================================

    public List<MedicineReminder> findDueReminders() {

        syncRemindersFromDatabase();

        List<MedicineReminder> result = new ArrayList<>();

        LocalDateTime now = LocalDateTime.now();

        for (MedicineReminder r : Reminders) {

            boolean pending =
                    "Pending".equalsIgnoreCase(r.getStatus());

            boolean snoozed =
                    "Snoozed".equalsIgnoreCase(r.getStatus());

            boolean scheduledTime =
                    r.getScheduledAt() != null
                    && !r.getScheduledAt().isAfter(now);

            boolean snoozedTime =
                    r.getSnoozeUntil() != null
                    && !r.getSnoozeUntil().isAfter(now);

            if (pending && scheduledTime) {
                result.add(r);
            } else if (snoozed && snoozedTime) {
                result.add(r);
            }
        }

        return result;
    }

    // =========================================
    // UPDATE REMINDER
    // =========================================

    public boolean updateReminder(MedicineReminder r) {

        if (r == null || r.getReminderId() <= 0
                || !isValidReminder(r)
                || r.getStatus() == null
                || r.getStatus().trim().isEmpty()) {

            return false;
        }

        MedicineReminder exist =
                findByReminderId(r.getReminderId());

        if (exist == null || !validateReferences(r)) {
            return false;
        }

        String sql = "UPDATE medicine_reminders SET "
                + "patient_id=?, prescription_id=?, medicine_name=?, "
                + "dosage=?, scheduled_at=?, interval_hours=?, "
                + "snooze_until=?, status=? "
                + "WHERE reminder_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, r.getPatientId());
            ps.setInt(2, r.getPrescriptionId());
            ps.setString(3, r.getMedicineName().trim());
            ps.setString(4, r.getDosage().trim());
            ps.setTimestamp(5,
                    Timestamp.valueOf(r.getScheduledAt()));
            ps.setInt(6, r.getIntervalHours());

            if (r.getSnoozeUntil() == null) {
                ps.setNull(7, java.sql.Types.TIMESTAMP);
            } else {
                ps.setTimestamp(7,
                        Timestamp.valueOf(r.getSnoozeUntil()));
            }

            ps.setString(8, r.getStatus());
            ps.setInt(9, r.getReminderId());

            boolean updated = ps.executeUpdate() > 0;

            if (updated) {
                syncRemindersFromDatabase();
            }

            return updated;

        } catch (SQLException e) {

            if (!isConnectionFailure(e)) {
                System.err.println(
                        "Update Reminder Error: " + e.getMessage());
                return false;
            }
        }

        exist.setPatientId(r.getPatientId());
        exist.setPrescriptionId(r.getPrescriptionId());
        exist.setMedicineName(r.getMedicineName().trim());
        exist.setDosage(r.getDosage().trim());
        exist.setScheduledAt(r.getScheduledAt());
        exist.setIntervalHours(r.getIntervalHours());
        exist.setSnoozeUntil(r.getSnoozeUntil());
        exist.setStatus(r.getStatus());

        return true;
    }

    // =========================================
    // SNOOZE REMINDER
    // =========================================

    public boolean snoozedReminder(int reminderId, int minutes) {

        if (minutes != 5 && minutes != 10) {
            return false;
        }

        MedicineReminder r = findByReminderId(reminderId);

        if (r == null
                || (!"Pending".equalsIgnoreCase(r.getStatus())
                && !"Snoozed".equalsIgnoreCase(r.getStatus()))) {

            return false;
        }

        LocalDateTime now = LocalDateTime.now();

        LocalDateTime baseTime = r.getSnoozeUntil();

        if (baseTime == null || !baseTime.isAfter(now)) {
            baseTime = now;
        }

        LocalDateTime newSnoozeTime =
                baseTime.plusMinutes(minutes);

        String sql = "UPDATE medicine_reminders "
                + "SET snooze_until=?, status='Snoozed' "
                + "WHERE reminder_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setTimestamp(1,
                    Timestamp.valueOf(newSnoozeTime));

            ps.setInt(2, reminderId);

            boolean updated = ps.executeUpdate() > 0;

            if (updated) {
                syncRemindersFromDatabase();
            }

            return updated;

        } catch (SQLException e) {

            if (!isConnectionFailure(e)) {
                System.err.println(
                        "Snooze Reminder Error: " + e.getMessage());
                return false;
            }
        }

        r.setSnoozeUntil(newSnoozeTime);
        r.setStatus("Snoozed");

        return true;
    }

    // =========================================
// COMPLETE REMINDER
// =========================================

public boolean completedReminder(int reminderId) {

    return recordDoseAndReschedule(reminderId, "Given");
}


// =========================================
// SKIP REMINDER
// =========================================

public boolean skippedReminder(int reminderId) {

    return recordDoseAndReschedule(reminderId, "Skipped");
}


// =========================================
// RECORD DOSE AND RESCHEDULE
// USING SINGLE TRANSACTION
// =========================================

private boolean recordDoseAndReschedule(
        int reminderId, String doseStatus) {

    MedicineReminder r = findByReminderId(reminderId);

    if (r == null
            || (!"Pending".equalsIgnoreCase(r.getStatus())
            && !"Snoozed".equalsIgnoreCase(r.getStatus()))) {

        return false;
    }

    LocalDateTime now = LocalDateTime.now();

    LocalDateTime nextTime =
            now.plusHours(r.getIntervalHours());

    MedicineDoseHistory mdh = new MedicineDoseHistory(
            r.getReminderId(),
            r.getPatientId(),
            0,
            r.getMedicineName(),
            r.getDosage(),
            r.getScheduledAt(),
            now,
            doseStatus
    );

    String sql = "UPDATE medicine_reminders SET "
            + "scheduled_at=?, snooze_until=NULL, status='Pending' "
            + "WHERE reminder_id=? "
            + "AND status IN ('Pending', 'Snoozed')";

    Connection con = null;

    try {

        con = DBConnection.getConnection();

        con.setAutoCommit(false);

        // STEP 1: Save dose history using same connection

        boolean historySaved = mdhd.addHistory(con, mdh);

        if (!historySaved) {

            con.rollback();
            return false;
        }

        // STEP 2: Update reminder using same connection

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setTimestamp(1, Timestamp.valueOf(nextTime));
            ps.setInt(2, reminderId);

            int rows = ps.executeUpdate();

            if (rows != 1) {

                con.rollback();
                return false;
            }
        }

        // STEP 3: Both operations successful

        con.commit();

        // Refresh reminder data after commit

        syncRemindersFromDatabase();

        return true;

    } catch (SQLException e) {

        if (con != null) {

            try {
                con.rollback();
            } catch (SQLException rollbackError) {
                System.err.println(
                        "Transaction Rollback Error: "
                        + rollbackError.getMessage());
            }
        }

        if (!isConnectionFailure(e)) {

            System.err.println(
                    "Dose Transaction Error: " + e.getMessage());

            return false;
        }

        // Database unavailable: in-memory fallback

        System.err.println(
                "Database unavailable. Using in-memory fallback.");

        if (!mdhd.addHistory(mdh)) {
            return false;
        }

        r.setScheduledAt(nextTime);
        r.setSnoozeUntil(null);
        r.setStatus("Pending");

        return true;

    } finally {

        if (con != null) {

            try {
                con.setAutoCommit(true);
                con.close();

            } catch (SQLException e) {
                System.err.println(
                        "Connection Close Error: " + e.getMessage());
            }
        }
    }
}

    // =========================================
    // DELETE REMINDER
    // =========================================

    public boolean deleteReminder(int reminderId) {

        MedicineReminder r = findByReminderId(reminderId);

        if (r == null) {
            return false;
        }

        // Check dose history
        for (MedicineDoseHistory mdh : mdhd.findAll()) {

            if (mdh.getReminderId() == reminderId) {
                return false;
            }
        }

        String sql =
                "DELETE FROM medicine_reminders WHERE reminder_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, reminderId);

            boolean deleted = ps.executeUpdate() > 0;

            if (deleted) {
                syncRemindersFromDatabase();
            }

            return deleted;

        } catch (SQLException e) {

            if (!isConnectionFailure(e)) {
                System.err.println(
                        "Delete Reminder Error: " + e.getMessage());
                return false;
            }
        }

        return Reminders.remove(r);
    }

    // =========================================
    // VALIDATION
    // =========================================

    private boolean isValidReminder(MedicineReminder r) {

        return r != null
                && r.getPatientId() > 0
                && r.getPrescriptionId() > 0
                && r.getMedicineName() != null
                && !r.getMedicineName().trim().isEmpty()
                && r.getDosage() != null
                && !r.getDosage().trim().isEmpty()
                && r.getScheduledAt() != null
                && r.getIntervalHours() > 0;
    }

    private boolean validateReferences(MedicineReminder r) {

        PatientDao ptd = new PatientDao();
        PrescriptionDao pd = new PrescriptionDao();

        Patient pt =
                ptd.findByPatientId(r.getPatientId());

        Prescription p =
                pd.findByPrescriptionId(r.getPrescriptionId());

        return pt != null
                && p != null
                && p.getPatientId() == r.getPatientId();
    }

    // =========================================
    // DATABASE SYNC
    // =========================================

    private boolean syncRemindersFromDatabase() {

        List<MedicineReminder> temp = new ArrayList<>();

        String sql = "SELECT * FROM medicine_reminders "
                + "ORDER BY reminder_id";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                temp.add(mapReminder(rs));
            }

            Reminders.clear();
            Reminders.addAll(temp);

            nextReminderId = 1;

            for (MedicineReminder r : Reminders) {
                if (r.getReminderId() >= nextReminderId) {
                    nextReminderId = r.getReminderId() + 1;
                }
            }

            return true;

        } catch (SQLException e) {

            if (!isConnectionFailure(e)) {
                System.err.println(
                        "Sync Reminder Error: " + e.getMessage());
            }

            return false;
        }
    }

    // =========================================
    // RESULT SET MAPPING
    // =========================================

    private MedicineReminder mapReminder(ResultSet rs)
            throws SQLException {

        Timestamp scheduledTimestamp =
                rs.getTimestamp("scheduled_at");

        Timestamp snoozeTimestamp =
                rs.getTimestamp("snooze_until");

        LocalDateTime scheduledAt =
                scheduledTimestamp == null
                ? null : scheduledTimestamp.toLocalDateTime();

        LocalDateTime snoozeUntil =
                snoozeTimestamp == null
                ? null : snoozeTimestamp.toLocalDateTime();

        return new MedicineReminder(
                rs.getInt("reminder_id"),
                rs.getInt("patient_id"),
                rs.getInt("prescription_id"),
                rs.getString("medicine_name"),
                rs.getString("dosage"),
                scheduledAt,
                rs.getInt("interval_hours"),
                snoozeUntil,
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