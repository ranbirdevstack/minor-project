package com.mycompany.medicalappointmentandprescription;

import com.mycompany.medicalappointmentandprescription.dao.DBConnection;
import com.mycompany.medicalappointmentandprescription.utils.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.DayOfWeek;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DatabaseSeeder {

    public static void main(String[] args) {

        try (Connection con = DBConnection.getConnection()) {

            con.setAutoCommit(false);

            try {

                if (isAlreadySeeded(con)) {

                    System.out.println("Database is already seeded.");
                    con.rollback();
                    return;
                }

                // =========================================
                // 1. USERS
                // 1 ADMIN + 3 STAFF + 3 DOCTORS
                // =========================================

                int adminId = insertUser(
                        con,
                        "admin.rahul",
                        "Rahul Singh",
                        "9876500001",
                        "Admin",
                        "Rahul@Admin26"
                );

                int staffAyeshaId = insertUser(
                        con,
                        "staff.ayesha",
                        "Ayesha Khan",
                        "9876500002",
                        "Staff",
                        "Ayesha@Staff26"
                );

                int staffNehaId = insertUser(
                        con,
                        "staff.neha",
                        "Neha Verma",
                        "9876500003",
                        "Staff",
                        "Neha@Staff26"
                );

                int staffRohanId = insertUser(
                        con,
                        "staff.rohan",
                        "Rohan Gupta",
                        "9876500004",
                        "Staff",
                        "Rohan@Staff26"
                );

                int doctorAmitUserId = insertUser(
                        con,
                        "doctor.amit",
                        "Amit Verma",
                        "9876500005",
                        "Doctor",
                        "Amit@Doctor26"
                );

                int doctorPriyaUserId = insertUser(
                        con,
                        "doctor.priya",
                        "Priya Sharma",
                        "9876500006",
                        "Doctor",
                        "Priya@Doctor26"
                );

                int doctorRahulUserId = insertUser(
                        con,
                        "doctor.rahul",
                        "Rahul Sharma",
                        "9876500007",
                        "Doctor",
                        "Rahul@Doctor26"
                );

                // =========================================
                // 2. DOCTORS
                // =========================================

                int doctorAmitId = insertDoctor(
                        con,
                        doctorAmitUserId,
                        "Dr. Amit Verma",
                        "General Physician",
                        "9876500005",
                        500.00,
                        "09:00 AM - 02:00 PM",
                        "MONDAY,WEDNESDAY,FRIDAY"
                );

                int doctorPriyaId = insertDoctor(
                        con,
                        doctorPriyaUserId,
                        "Dr. Priya Sharma",
                        "Cardiologist",
                        "9876500006",
                        800.00,
                        "10:00 AM - 04:00 PM",
                        "TUESDAY,THURSDAY,SATURDAY"
                );

                int doctorRahulId = insertDoctor(
                        con,
                        doctorRahulUserId,
                        "Dr. Rahul Sharma",
                        "Dermatologist",
                        "9876500007",
                        600.00,
                        "09:00 AM - 01:00 PM",
                        "MONDAY,TUESDAY,THURSDAY"
                );

                // =========================================
                // 3. PATIENTS
                // Each Staff registers 2 patients
                // =========================================

                int patient1 = insertPatient(
                        con, staffAyeshaId,
                        "Aarav Sharma", 24, "Male",
                        "9876510001", "Varanasi", "O+"
                );

                int patient2 = insertPatient(
                        con, staffAyeshaId,
                        "Ananya Gupta", 22, "Female",
                        "9876510002", "Jaunpur", "A+"
                );

                int patient3 = insertPatient(
                        con, staffNehaId,
                        "Kabir Singh", 30, "Male",
                        "9876510003", "Varanasi", "B+"
                );

                int patient4 = insertPatient(
                        con, staffNehaId,
                        "Saanvi Mishra", 27, "Female",
                        "9876510004", "Prayagraj", "AB+"
                );

                int patient5 = insertPatient(
                        con, staffRohanId,
                        "Arjun Patel", 35, "Male",
                        "9876510005", "Mirzapur", "O-"
                );

                int patient6 = insertPatient(
                        con, staffRohanId,
                        "Myra Verma", 21, "Female",
                        "9876510006", "Varanasi", "B-"
                );

                // =========================================
                // 4. APPOINTMENTS
                // =========================================

                // Get current timestamp from MySQL server.
                // One-minute buffer ensures generated slots
                // remain after the actual created_at time.

                LocalDateTime createdAt =
                        getDatabaseCurrentDateTime(con).plusMinutes(1);

                // Amit: Monday, Wednesday, Friday
                List<LocalDateTime> amitSlots = getNextAvailableSlots(
                        createdAt,
                        Set.of(
                                DayOfWeek.MONDAY,
                                DayOfWeek.WEDNESDAY,
                                DayOfWeek.FRIDAY
                        ),
                        LocalTime.of(9, 0),
                        LocalTime.of(14, 0)
                );

                // Priya: Tuesday, Thursday, Saturday
                List<LocalDateTime> priyaSlots = getNextAvailableSlots(
                        createdAt,
                        Set.of(
                                DayOfWeek.TUESDAY,
                                DayOfWeek.THURSDAY,
                                DayOfWeek.SATURDAY
                        ),
                        LocalTime.of(10, 0),
                        LocalTime.of(16, 0)
                );

                // Rahul: Monday, Tuesday, Thursday
                List<LocalDateTime> rahulSlots = getNextAvailableSlots(
                        createdAt,
                        Set.of(
                                DayOfWeek.MONDAY,
                                DayOfWeek.TUESDAY,
                                DayOfWeek.THURSDAY
                        ),
                        LocalTime.of(9, 0),
                        LocalTime.of(13, 0)
                );

                LocalDateTime appointment1Time = amitSlots.get(0);
                LocalDateTime appointment2Time = amitSlots.get(1);

                LocalDateTime appointment3Time = priyaSlots.get(0);
                LocalDateTime appointment4Time = priyaSlots.get(1);

                LocalDateTime appointment5Time = rahulSlots.get(0);
                LocalDateTime appointment6Time = rahulSlots.get(1);

                // Insert Appointments

                int appointment1 = insertAppointment(
                        con, patient1, doctorAmitId, appointment1Time
                );

                int appointment2 = insertAppointment(
                        con, patient2, doctorAmitId, appointment2Time
                );

                int appointment3 = insertAppointment(
                        con, patient3, doctorPriyaId, appointment3Time
                );

                int appointment4 = insertAppointment(
                        con, patient4, doctorPriyaId, appointment4Time
                );

                int appointment5 = insertAppointment(
                        con, patient5, doctorRahulId, appointment5Time
                );

                int appointment6 = insertAppointment(
                        con, patient6, doctorRahulId, appointment6Time
                );

                // =========================================
                // 5. PRESCRIPTIONS
                // =========================================

                int prescription1 = insertPrescription(
                        con, appointment1, patient1, doctorAmitId,
                        appointment1Time.toLocalDate(),
                        "Paracetamol 500mg - Twice daily",
                        "Fever and headache"
                );

                int prescription2 = insertPrescription(
                        con, appointment2, patient2, doctorAmitId,
                        appointment2Time.toLocalDate(),
                        "Cetirizine 10mg - Once daily",
                        "Allergy symptoms"
                );

                int prescription3 = insertPrescription(
                        con, appointment3, patient3, doctorPriyaId,
                        appointment3Time.toLocalDate(),
                        "Amlodipine 5mg - Once daily",
                        "Blood pressure management"
                );

                int prescription4 = insertPrescription(
                        con, appointment4, patient4, doctorPriyaId,
                        appointment4Time.toLocalDate(),
                        "Metoprolol 25mg - Once daily",
                        "Cardiac consultation"
                );

                int prescription5 = insertPrescription(
                        con, appointment5, patient5, doctorRahulId,
                        appointment5Time.toLocalDate(),
                        "Cetirizine 10mg - Once daily",
                        "Skin allergy"
                );

                int prescription6 = insertPrescription(
                        con, appointment6, patient6, doctorRahulId,
                        appointment6Time.toLocalDate(),
                        "Calamine lotion - As directed",
                        "Skin irritation"
                );

                // =========================================
                // 6. MEDICAL HISTORY
                // =========================================

                insertMedicalHistory(
                        con, patient1, doctorAmitId,
                        appointment1Time,
                        "Fever and headache",
                        "Viral fever",
                        "Rest and prescribed medication"
                );

                insertMedicalHistory(
                        con, patient2, doctorAmitId,
                        appointment2Time,
                        "Sneezing and itching",
                        "Allergic rhinitis",
                        "Prescribed allergy medication"
                );

                insertMedicalHistory(
                        con, patient3, doctorPriyaId,
                        appointment3Time,
                        "Headache and dizziness",
                        "Hypertension",
                        "Regular blood pressure monitoring"
                );

                insertMedicalHistory(
                        con, patient4, doctorPriyaId,
                        appointment4Time,
                        "Palpitations",
                        "Cardiac symptoms under evaluation",
                        "Follow-up and monitoring advised"
                );

                insertMedicalHistory(
                        con, patient5, doctorRahulId,
                        appointment5Time,
                        "Skin itching",
                        "Skin allergy",
                        "Prescribed medication"
                );

                insertMedicalHistory(
                        con, patient6, doctorRahulId,
                        appointment6Time,
                        "Skin irritation",
                        "Dermatitis",
                        "Skin care and prescribed treatment"
                );

                // =========================================
                // 7. FOLLOW-UP RECOMMENDATIONS
                // =========================================

                insertFollowUp(
                        con, appointment1, patient1, doctorAmitId,
                        appointment1Time
                );

                insertFollowUp(
                        con, appointment2, patient2, doctorAmitId,
                        appointment2Time
                );

                insertFollowUp(
                        con, appointment3, patient3, doctorPriyaId,
                        appointment3Time
                );

                insertFollowUp(
                        con, appointment4, patient4, doctorPriyaId,
                        appointment4Time
                );

                insertFollowUp(
                        con, appointment5, patient5, doctorRahulId,
                        appointment5Time
                );

                insertFollowUp(
                        con, appointment6, patient6, doctorRahulId,
                        appointment6Time
                );

                // =========================================
                // 8. MEDICINE REMINDERS
                // =========================================

                int reminder1 = insertReminder(
                        con, patient1, prescription1,
                        "Paracetamol", "500mg",
                        appointment1Time, "Completed"
                );

                int reminder2 = insertReminder(
                        con, patient2, prescription2,
                        "Cetirizine", "10mg",
                        appointment2Time, "Pending"
                );

                int reminder3 = insertReminder(
                        con, patient3, prescription3,
                        "Amlodipine", "5mg",
                        appointment3Time, "Completed"
                );

                int reminder4 = insertReminder(
                        con, patient4, prescription4,
                        "Metoprolol", "25mg",
                        appointment4Time, "Skipped"
                );

                int reminder5 = insertReminder(
                        con, patient5, prescription5,
                        "Cetirizine", "10mg",
                        appointment5Time, "Completed"
                );

                int reminder6 = insertReminder(
                        con, patient6, prescription6,
                        "Calamine lotion", "As directed",
                        appointment6Time, "Pending"
                );

                // =========================================
                // 9. MEDICINE DOSE HISTORY
                // =========================================

                insertDoseHistory(
                        con, reminder1, patient1,
                        "Paracetamol", "500mg",
                        appointment1Time, "Given"
                );

                insertDoseHistory(
                        con, reminder3, patient3,
                        "Amlodipine", "5mg",
                        appointment3Time, "Given"
                );

                insertDoseHistory(
                        con, reminder4, patient4,
                        "Metoprolol", "25mg",
                        appointment4Time, "Skipped"
                );

                insertDoseHistory(
                        con, reminder5, patient5,
                        "Cetirizine", "10mg",
                        appointment5Time, "Given"
                );

                // =========================================
                // COMMIT
                // =========================================

                con.commit();

                System.out.println("\n==================================");
                System.out.println("DATABASE SEEDING SUCCESSFUL");
                System.out.println("==================================");
                System.out.println("Users: 7");
                System.out.println("Admin: 1");
                System.out.println("Staff: 3");
                System.out.println("Doctors: 3");
                System.out.println("Patients: 6");
                System.out.println("Appointments: 6");
                System.out.println("Prescriptions: 6");
                System.out.println("Medical History: 6");
                System.out.println("Follow-up Recommendations: 6");
                System.out.println("Medicine Reminders: 6");
                System.out.println("Medicine Dose History: 4");
                System.out.println("==================================");

            } catch (Exception e) {

                con.rollback();

                System.out.println("SEEDING FAILED!");
                System.out.println("Transaction rolled back.");

                e.printStackTrace();

            } finally {

                con.setAutoCommit(true);
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }
    }

    // =========================================
    // GET DATABASE CURRENT TIMESTAMP
    // =========================================

    private static LocalDateTime getDatabaseCurrentDateTime(
            Connection con
    ) throws SQLException {

        String sql = "SELECT CURRENT_TIMESTAMP";

        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getTimestamp(1).toLocalDateTime();
            }

            throw new SQLException(
                    "Unable to retrieve database timestamp."
            );
        }
    }

    // =========================================
    // GET NEXT AVAILABLE APPOINTMENT SLOTS
    // =========================================

    private static List<LocalDateTime> getNextAvailableSlots(
            LocalDateTime createdAt,
            Set<DayOfWeek> workingDays,
            LocalTime openingTime,
            LocalTime closingTime
    ) {

        List<LocalDateTime> slots = new ArrayList<>();

        LocalDate date = createdAt.toLocalDate();

        while (slots.size() < 2) {

            if (workingDays.contains(date.getDayOfWeek())) {

                LocalTime slotTime = openingTime;

                while (!slotTime.plusHours(1).isAfter(closingTime)) {

                    LocalDateTime slot =
                            LocalDateTime.of(date, slotTime);

                    if (slot.isAfter(createdAt)) {
                        slots.add(slot);
                    }

                    slotTime = slotTime.plusHours(1);

                    if (slots.size() == 2) {
                        break;
                    }
                }
            }

            date = date.plusDays(1);
        }

        return slots;
    }

    // =========================================
    // CHECK ALREADY SEEDED
    // =========================================

    private static boolean isAlreadySeeded(Connection con)
            throws SQLException {

        String sql =
                "SELECT user_id FROM users WHERE username = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "admin.rahul");

            try (ResultSet rs = ps.executeQuery()) {

                return rs.next();
            }
        }
    }

    // =========================================
    // INSERT USER
    // =========================================

    private static int insertUser(
            Connection con,
            String username,
            String fullName,
            String contact,
            String role,
            String password
    ) throws SQLException {

        String hashedPassword =
                PasswordUtil.hashPassword(password);

        String sql =
                "INSERT INTO users "
                + "(username, full_name, contact_number, role, password) "
                + "VALUES (?, ?, ?, ?, ?)";

        return insertAndGetId(
                con, sql,
                username, fullName, contact, role, hashedPassword
        );
    }

    // =========================================
    // INSERT DOCTOR
    // =========================================

    private static int insertDoctor(
            Connection con,
            int userId,
            String doctorName,
            String specialization,
            String contact,
            double fee,
            String workingHours,
            String workingDays
    ) throws SQLException {

        String sql =
                "INSERT INTO doctors "
                + "(user_id, doctor_name, specialization, "
                + "contact_number, consultation_fee, working_hours, "
                + "working_days, availability_status) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        return insertAndGetId(
                con, sql,
                userId, doctorName, specialization,
                contact, fee, workingHours, workingDays, true
        );
    }

    // =========================================
    // INSERT PATIENT
    // =========================================

    private static int insertPatient(
            Connection con,
            int registeredBy,
            String name,
            int age,
            String gender,
            String contact,
            String address,
            String bloodGroup
    ) throws SQLException {

        String sql =
                "INSERT INTO patients "
                + "(registered_by, full_name, age, gender, "
                + "contact_number, address, blood_group) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        return insertAndGetId(
                con, sql,
                registeredBy, name, age, gender,
                contact, address, bloodGroup
        );
    }

    // =========================================
    // INSERT APPOINTMENT
    // =========================================

    private static int insertAppointment(
            Connection con,
            int patientId,
            int doctorId,
            LocalDateTime appointmentDateTime
    ) throws SQLException {

        String sql =
                "INSERT INTO appointments "
                + "(patient_id, doctor_id, appointment_date, "
                + "appointment_time, status) "
                + "VALUES (?, ?, ?, ?, ?)";

        return insertAndGetId(
                con,
                sql,
                patientId,
                doctorId,
                Date.valueOf(appointmentDateTime.toLocalDate()),
                Time.valueOf(appointmentDateTime.toLocalTime()),
                "Confirmed"
        );
    }

    // =========================================
    // INSERT PRESCRIPTION
    // =========================================

    private static int insertPrescription(
            Connection con,
            int appointmentId,
            int patientId,
            int doctorId,
            LocalDate date,
            String medicine,
            String notes
    ) throws SQLException {

        String sql =
                "INSERT INTO prescriptions "
                + "(appointment_id, patient_id, doctor_id, "
                + "issue_date, medicine_and_dosage, clinical_notes) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        return insertAndGetId(
                con, sql,
                appointmentId, patientId, doctorId,
                Date.valueOf(date), medicine, notes
        );
    }

    // =========================================
    // INSERT MEDICAL HISTORY
    // =========================================

    private static void insertMedicalHistory(
            Connection con,
            int patientId,
            int doctorId,
            LocalDateTime appointmentDateTime,
            String symptoms,
            String diagnosis,
            String treatmentNotes
    ) throws SQLException {

        String sql =
                "INSERT INTO medical_history "
                + "(patient_id, doctor_id, recorded_at, symptoms, "
                + "diagnosis, treatment_notes) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        insertAndGetId(
                con,
                sql,
                patientId,
                doctorId,
                Timestamp.valueOf(appointmentDateTime),
                symptoms,
                diagnosis,
                treatmentNotes
        );
    }

    // =========================================
    // INSERT FOLLOW-UP
    // =========================================

    private static void insertFollowUp(
            Connection con,
            int appointmentId,
            int patientId,
            int doctorId,
            LocalDateTime appointmentDateTime
    ) throws SQLException {

        String sql =
                "INSERT INTO follow_up_recommendations "
                + "(appointment_id, patient_id, doctor_id, "
                + "recommended_date, instruction, status) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        insertAndGetId(
                con, sql,
                appointmentId, patientId, doctorId,
                Date.valueOf(
                        appointmentDateTime.toLocalDate().plusDays(7)
                ),
                "Return for follow-up consultation",
                "Pending"
        );
    }

    // =========================================
    // INSERT MEDICINE REMINDER
    // =========================================

    private static int insertReminder(
            Connection con,
            int patientId,
            int prescriptionId,
            String medicine,
            String dosage,
            LocalDateTime appointmentDateTime,
            String status
    ) throws SQLException {

        String sql =
                "INSERT INTO medicine_reminders "
                + "(patient_id, prescription_id, medicine_name, "
                + "dosage, scheduled_at, interval_hours, status) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        LocalDateTime scheduledTime =
                appointmentDateTime.plusHours(2);

        return insertAndGetId(
                con,
                sql,
                patientId,
                prescriptionId,
                medicine,
                dosage,
                Timestamp.valueOf(scheduledTime),
                8,
                status
        );
    }

    // =========================================
    // INSERT MEDICINE DOSE HISTORY
    // =========================================

    private static void insertDoseHistory(
            Connection con,
            int reminderId,
            int patientId,
            String medicine,
            String dosage,
            LocalDateTime appointmentDateTime,
            String status
    ) throws SQLException {

        LocalDateTime scheduledTime =
                appointmentDateTime.plusHours(2);

        LocalDateTime actionTime =
                appointmentDateTime.plusHours(3);

        String sql =
                "INSERT INTO medicine_dose_history "
                + "(reminder_id, patient_id, medicine_name, dosage, "
                + "scheduled_at, given_at, status) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        insertAndGetId(
                con,
                sql,
                reminderId,
                patientId,
                medicine,
                dosage,
                Timestamp.valueOf(scheduledTime),
                Timestamp.valueOf(actionTime),
                status
        );
    }

    // =========================================
    // COMMON INSERT METHOD
    // =========================================

    private static int insertAndGetId(
            Connection con,
            String sql,
            Object... values
    ) throws SQLException {

        try (PreparedStatement ps = con.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {

            for (int i = 0; i < values.length; i++) {

                ps.setObject(i + 1, values[i]);
            }

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {

                if (rs.next()) {

                    return rs.getInt(1);
                }

                throw new SQLException(
                        "Unable to retrieve generated ID."
                );
            }
        }
    }
}