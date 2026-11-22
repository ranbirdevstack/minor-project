/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.medicalappointmentandprescription.ui.staff;
import com.mycompany.medicalappointmentandprescription.dao.PatientDao;
import com.mycompany.medicalappointmentandprescription.dao.DoctorDao;
import com.mycompany.medicalappointmentandprescription.dao.AppointmentDao;
import com.mycompany.medicalappointmentandprescription.dao.FollowUpRecommendationDao;
import com.mycompany.medicalappointmentandprescription.models.Patient;
import com.mycompany.medicalappointmentandprescription.models.Doctor;
import com.mycompany.medicalappointmentandprescription.models.Appointment;
import javax.swing.JFrame;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.DayOfWeek;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.TextStyle;

import java.util.List;
import java.util.Locale;
import java.util.ArrayList;
import java.util.stream.Collectors;

import javax.swing.JOptionPane;
import javax.swing.DefaultComboBoxModel;
/**
 *
 * @author ranbi
 */
public class BookAppointmentStaffFrame extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(BookAppointmentStaffFrame.class.getName());
    private final PatientDao pd = new PatientDao();
    private final DoctorDao dd = new DoctorDao();
    private final AppointmentDao ad = new AppointmentDao();
    private final FollowUpRecommendationDao fd = new FollowUpRecommendationDao();
   private Patient selectedPatient;
   private Doctor selectedDoctor;

   private LocalDate selectedAppointmentDate;
   private LocalTime selectedAppointmentTime;

   private boolean isLoadingDoctors = false;
   private boolean isLoadingTimes = false;
   private boolean isCheckingAvailability = false;

   private boolean isProactiveDialogOpen = false;
   private boolean isUnavailableDialogOpen = false;

   private boolean isUpdatingForm = false;

   private boolean proactiveConfirmed = false;
   private JFrame parentFrame;
   private String lastCheckedSlot = "";

   private static final DateTimeFormatter DATE_FORMATTER =DateTimeFormatter.ofPattern("dd-MM-yyyy");

   private static final DateTimeFormatter TIME_FORMATTER =DateTimeFormatter.ofPattern("hh:mm a");

   private static final DateTimeFormatter WORKING_HOURS_FORMATTER =DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH);

private static final int SLOT_INTERVAL_MINUTES = 30;
    /**
     * Creates new form BookAppointmentStaffFrame
     */
    public BookAppointmentStaffFrame(ManageAppointmentsStaffFrame parent) {
        initComponents();
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        this.parentFrame = parent;
        initializeForm();
        setupDoctorComboBox();
        setupAvailabilityListeners();
    }
    public BookAppointmentStaffFrame(
        AppointmentDetailsStaffFrame parent, Patient patient, Doctor doctor,LocalDate date) {

    initComponents();

    this.parentFrame = parent;

    initializeForm();
    setupDoctorComboBox();
    autoFillFollowUpDetails(patient, doctor, date);

    addTimeListener();
    
}
    private void initializeForm() {

    selectedPatient = null;
    selectedDoctor = null;

    selectedAppointmentDate = null;
    selectedAppointmentTime = null;

    txtpatientid.setText("");

    txtappointmentdate.setText("");

    initializePatientDetails();

    initializeDoctorDetails();

    cmbappointmentime.removeAllItems();
    cmbappointmentime.addItem("Select a Time");

    lblstatusvalue.setText("Not Checked");

    btnbookappointment.setEnabled(false);
}
    private void initializePatientDetails() {

    lblpatientnamevalue.setText("-");
    lblagevalue.setText("-");
    lblgendervalue.setText("-");
    lblcontactvalue.setText("-");
}
    private void initializeDoctorDetails() {

    lbldoctornamevalue.setText("-");
    lblspecializationvalue.setText("-");
    lbldoccontactvalue.setText("-");
    lblconsultationfeevalue.setText("-");
    lblworkinghoursvalue.setText("-");
    lblworkingdaysvalue.setText("-");
    lblavailabiltyvalue.setText("-");
}
    private void setupDoctorComboBox() {

    isLoadingDoctors = true;

    cmbdoctors.removeAllItems();

    cmbdoctors.addItem("Select a Doctor");

    List<Doctor> doctors = dd.findAll();

    for (Doctor doctor : doctors) {

        cmbdoctors.addItem(doctor.getDoctorName());
    }

    cmbdoctors.setSelectedIndex(0);

    isLoadingDoctors = false;

    cmbdoctors.addActionListener(e -> {

        if (!isLoadingDoctors && !isUpdatingForm) {

            loadSelectedDoctor();
        }
    });
}
    private void loadSelectedDoctor() {

    String doctorName = (String) cmbdoctors.getSelectedItem();

    selectedDoctor = null;

    if (doctorName == null || doctorName.equals("Select a Doctor")) {

        initializeDoctorDetails();

        clearAppointmentSelection();

        return;
    }

    for (Doctor doctor : dd.findAll()) {

        if (doctor.getDoctorName().equals(doctorName)) {

            selectedDoctor = doctor;

            break;
        }
    }

    if (selectedDoctor == null) {

        initializeDoctorDetails();

        return;
    }

    lbldoctornamevalue.setText(selectedDoctor.getDoctorName());

    lblspecializationvalue.setText(
            selectedDoctor.getSpecialization()
    );

    lbldoccontactvalue.setText(
            selectedDoctor.getContactNumber()
    );

    lblconsultationfeevalue.setText(
            "₹" + selectedDoctor.getConsultationFee()
    );

    lblworkinghoursvalue.setText(
            selectedDoctor.getWorkingHours()
    );

     String days = selectedDoctor.getWorkingDays() == null ? "Not Specified" : ((java.util.Collection<?>) selectedDoctor.getWorkingDays()).stream().map(Object::toString).collect(java.util.stream.Collectors.joining(", "));
        lblworkingdaysvalue.setText("<html>" + days.replace(", ", ",<br>") + "</html>");

    lblavailabiltyvalue.setText(
            selectedDoctor.getAvaliabilityStatus()
                    ? "Available"
                    : "Unavailable"
    );

    clearAppointmentSelection();

    if (!selectedDoctor.getAvaliabilityStatus()) {

        JOptionPane.showMessageDialog(
                this,
                "This doctor is currently unavailable.",
                "Doctor Unavailable",
                JOptionPane.WARNING_MESSAGE
        );
    }
}
    private void clearAppointmentSelection() {

    selectedAppointmentDate = null;
    selectedAppointmentTime = null;

    proactiveConfirmed = false;
    lastCheckedSlot = "";

    txtappointmentdate.setText("");

    isLoadingTimes = true;

    cmbappointmentime.removeAllItems();
    cmbappointmentime.addItem("Select a Time");

    isLoadingTimes = false;

    lblstatusvalue.setText("Not Checked");

    btnbookappointment.setEnabled(false);
}
    private void searchPatient() {

    String patientIdText = txtpatientid.getText().trim();

    if (patientIdText.isEmpty()) {

        JOptionPane.showMessageDialog(
                this,
                "Please enter Patient ID.",
                "Validation",
                JOptionPane.WARNING_MESSAGE
        );

        return;
    }

    int patientId;

    try {

        patientId = Integer.parseInt(patientIdText);

    } catch (NumberFormatException e) {

        JOptionPane.showMessageDialog(
                this,
                "Patient ID must be a valid number.",
                "Invalid Patient ID",
                JOptionPane.ERROR_MESSAGE
        );

        return;
    }

    Patient patient = pd.findByPatientId(patientId);

    if (patient == null) {

        selectedPatient = null;

        initializePatientDetails();

        btnbookappointment.setEnabled(false);

        JOptionPane.showMessageDialog(
                this,
                "Patient not found.",
                "Search Result",
                JOptionPane.INFORMATION_MESSAGE
        );

        return;
    }

    selectedPatient = patient;

    lblpatientnamevalue.setText(patient.getFullName());

    lblagevalue.setText(String.valueOf(patient.getAge()));

    lblgendervalue.setText(patient.getGender());

    lblcontactvalue.setText(patient.getContactNumber());

    btnbookappointment.setEnabled(false);
}
    private void setupAvailabilityListeners() {

    txtappointmentdate.addActionListener(e -> {

        loadAppointmentTimes();

    });

    txtappointmentdate.addFocusListener(
            new java.awt.event.FocusAdapter() {

        @Override
        public void focusLost(java.awt.event.FocusEvent evt) {

            loadAppointmentTimes();
        }
    });

    cmbappointmentime.addActionListener(e -> {

        if (!isLoadingTimes && !isUpdatingForm) {

            checkAppointmentAvailability();
        }
    });
}
    private LocalDate getSelectedDate() {

    String dateText = txtappointmentdate.getText().trim();

    if (dateText.isEmpty()) {

        return null;
    }

    try {

        LocalDate date = LocalDate.parse(
                dateText,
                DATE_FORMATTER
        );

        if (date.isBefore(LocalDate.now())) {

            JOptionPane.showMessageDialog(
                    this,
                    "Appointment date cannot be in the past.",
                    "Invalid Date",
                    JOptionPane.WARNING_MESSAGE
            );

            return null;
        }

        return date;

    } catch (DateTimeParseException e) {

        JOptionPane.showMessageDialog(
                this,
                "Please enter date in dd-MM-yyyy format.\nExample: 25-11-2026",
                "Invalid Date",
                JOptionPane.WARNING_MESSAGE
        );

        return null;
    }
}
    private boolean isDoctorWorkingOnDate(Doctor doctor, LocalDate date) {

    if (doctor == null || date == null
            || doctor.getWorkingDays() == null) {
        return false;
    }

    return doctor.getWorkingDays().contains(date.getDayOfWeek());
}
    private LocalTime[] getDoctorWorkingHours(Doctor doctor) {

    if (doctor == null || doctor.getWorkingHours() == null) {
        return null;
    }

    try {
        String workingHours = doctor.getWorkingHours();

        String[] parts = workingHours.split("\\s*-\\s*");

        if (parts.length != 2) {
            System.out.println("Invalid Working Hours: " + workingHours);
            return null;
        }

        LocalTime startTime = LocalTime.parse(
                parts[0].trim(),
                WORKING_HOURS_FORMATTER
        );

        LocalTime endTime = LocalTime.parse(
                parts[1].trim(),
                WORKING_HOURS_FORMATTER
        );

        return new LocalTime[]{startTime, endTime};

    } catch (Exception e) {
        e.printStackTrace();
        return null;
    }
}
    List<LocalTime> generateTimeSlots(Doctor doctor) {

    List<LocalTime> timeSlots = new ArrayList<>();

    LocalTime[] workingHours = getDoctorWorkingHours(doctor);

    if (workingHours == null) {

        return timeSlots;
    }

    LocalTime startTime = workingHours[0];

    LocalTime endTime = workingHours[1];

    LocalTime currentTime = startTime;

    while (currentTime.isBefore(endTime)) {

        timeSlots.add(currentTime);

        currentTime = currentTime.plusMinutes(
                SLOT_INTERVAL_MINUTES
        );
    }

    return timeSlots;
}
    private void loadAppointmentTimes() {

    if (isUpdatingForm) {
        return;
    }

    selectedAppointmentDate = null;
    selectedAppointmentTime = null;

    proactiveConfirmed = false;
    lastCheckedSlot = "";

    btnbookappointment.setEnabled(false);
    lblstatusvalue.setText("Not Checked");

    isLoadingTimes = true;

    try {
        cmbappointmentime.removeAllItems();
        cmbappointmentime.addItem("Select a Time");
    } finally {
        isLoadingTimes = false;
    }

    if (selectedDoctor == null) {
        System.out.println("ERROR: No doctor selected.");
        return;
    }

    if (!selectedDoctor.getAvaliabilityStatus()) {
        System.out.println("ERROR: Doctor unavailable.");
        return;
    }

    LocalDate date = getSelectedDate();

    if (date == null) {
        System.out.println("ERROR: Invalid appointment date.");
        return;
    }

    if (!isDoctorWorkingOnDate(selectedDoctor, date)) {

        JOptionPane.showMessageDialog(
                this,
                "Doctor is not available on "
                + date.getDayOfWeek(),
                "Doctor Schedule",
                JOptionPane.INFORMATION_MESSAGE
        );

        return;
    }

    selectedAppointmentDate = date;

    List<LocalTime> timeSlots = generateTimeSlots(selectedDoctor);

    System.out.println("Generated Slots: " + timeSlots);

    isLoadingTimes = true;

    try {

        for (LocalTime time : timeSlots) {

            if (date.equals(LocalDate.now())
                    && !time.isAfter(LocalTime.now())) {
                continue;
            }

            cmbappointmentime.addItem(
                    time.format(TIME_FORMATTER)
            );
        }

    } finally {
        isLoadingTimes = false;
    }

    if (cmbappointmentime.getItemCount() == 1) {

        JOptionPane.showMessageDialog(
                this,
                "No future appointment slots are available.",
                "No Available Slots",
                JOptionPane.INFORMATION_MESSAGE
        );
    }
}
    private LocalTime getSelectedTime() {

    String timeText = (String) cmbappointmentime.getSelectedItem();

    if (timeText == null
            || timeText.equals("Select a Time")) {

        return null;
    }

    try {

        return LocalTime.parse(
                timeText,
                TIME_FORMATTER
        );

    } catch (DateTimeParseException e) {

        return null;
    }
}
     boolean isDoctorSlotAvailable(Doctor doctor,LocalDate date,LocalTime time) {

    if (doctor == null || date == null || time == null) {

        return false;
    }

    if (!doctor.getAvaliabilityStatus()) {

        return false;
    }

    if (!isDoctorWorkingOnDate(doctor, date)) {

        return false;
    }

    LocalTime[] workingHours = getDoctorWorkingHours(doctor);

    if (workingHours == null) {

        return false;
    }

    LocalTime startTime = workingHours[0];

    LocalTime endTime = workingHours[1];

    if (time.isBefore(startTime) || !time.isBefore(endTime)) {

        return false;
    }

    if (date.equals(LocalDate.now())
            && !time.isAfter(LocalTime.now())) {

        return false;
    }

    List<Appointment> appointments =
            ad.findByDoctorDateAndTime(
                    doctor.getDoctorId(),
                    time,
                    date
            );

    for (Appointment appointment : appointments) {

        if (!"Cancelled".equalsIgnoreCase(
                appointment.getStatus())) {

            return false;
        }
    }

    return true;
}
    private void checkAppointmentAvailability() {

    if (isCheckingAvailability
            || isProactiveDialogOpen
            || isUnavailableDialogOpen
            || isUpdatingForm) {

        return;
    }

    if (selectedPatient == null || selectedDoctor == null) {

        return;
    }

    LocalDate date = getSelectedDate();

    LocalTime time = getSelectedTime();

    if (date == null || time == null) {

        return;
    }

    selectedAppointmentDate = date;

    selectedAppointmentTime = time;

    String slotKey = selectedDoctor.getDoctorId()
            + "_" + date
            + "_" + time;

    if (slotKey.equals(lastCheckedSlot)
            && proactiveConfirmed) {

        return;
    }

    isCheckingAvailability = true;

    try {

        if (isDoctorSlotAvailable(
                selectedDoctor,
                date,
                time)) {

            lastCheckedSlot = slotKey;

            lblstatusvalue.setText("Available");

            openProactiveDialog();

        } else {

            lastCheckedSlot = slotKey;

            lblstatusvalue.setText("Unavailable");

           openUnavailableDialog();
        }

    } finally {

        isCheckingAvailability = false;
    }
}
     List<LocalTime> findAlternativeSlots(
        Doctor doctor,
        LocalDate date,
        LocalTime selectedTime) {

    List<LocalTime> alternatives = new ArrayList<>();

    List<LocalTime> allSlots = generateTimeSlots(doctor);

    for (LocalTime time : allSlots) {

        if (time.equals(selectedTime)) {

            continue;
        }

        if (isDoctorSlotAvailable(doctor, date, time)) {

            alternatives.add(time);
        }
    }

    return alternatives;
}
    public void useAlternativeSlot(
        LocalDate date,
        LocalTime time) {

    isUpdatingForm = true;

    try {

        txtappointmentdate.setText(
                date.format(DATE_FORMATTER)
        );

        cmbappointmentime.setSelectedItem(
                time.format(TIME_FORMATTER)
        );

        selectedAppointmentDate = date;
        selectedAppointmentTime = time;

        lastCheckedSlot = "";
        proactiveConfirmed = false;

    } finally {

        isUpdatingForm = false;
    }

    openProactiveDialog();
}
    private void openProactiveDialog() {

    if (isProactiveDialogOpen) {

        return;
    }

    isProactiveDialogOpen = true;

    try
    {

        BookingDialogProactive dialog = new BookingDialogProactive(
                        this,
                        true,
                        selectedPatient,
                        selectedDoctor,
                        selectedAppointmentDate,
                        selectedAppointmentTime
                );

        dialog.setLocationRelativeTo(this);

        dialog.setVisible(true);

        if (dialog.isConfirmed()) {

            proactiveConfirmed = true;

            lblstatusvalue.setText("Available");

            btnbookappointment.setEnabled(true);

        } else {

            proactiveConfirmed = false;

            btnbookappointment.setEnabled(false);
        }

    } 
   finally {

        isProactiveDialogOpen = false;
    }
}
    private void openUnavailableDialog() {

    if (isUnavailableDialogOpen) {
        return;
    }

    isUnavailableDialogOpen = true;

    try {

        BookingDialogUnavailable dialog =
                new BookingDialogUnavailable(
                        this,
                        true,
                        selectedPatient,
                        selectedDoctor,
                        selectedAppointmentDate,
                        selectedAppointmentTime
                );

        dialog.setLocationRelativeTo(this);

        dialog.setVisible(true);

    } finally {

        isUnavailableDialogOpen = false;
    }
}
    private void bookAppointment() {

    if (selectedPatient == null) {

        JOptionPane.showMessageDialog(
                this,
                "Please search and select a patient.",
                "Patient Required",
                JOptionPane.WARNING_MESSAGE
        );

        return;
    }

    if (selectedDoctor == null) {

        JOptionPane.showMessageDialog(
                this,
                "Please select a doctor.",
                "Doctor Required",
                JOptionPane.WARNING_MESSAGE
        );

        return;
    }

    LocalDate date = getSelectedDate();

    LocalTime time = getSelectedTime();

    if (date == null || time == null) {

        JOptionPane.showMessageDialog(
                this,
                "Please select a valid appointment date and time.",
                "Appointment Details Required",
                JOptionPane.WARNING_MESSAGE
        );

        return;
    }

    if (!proactiveConfirmed) {

        JOptionPane.showMessageDialog(
                this,
                "Please confirm the available slot first.",
                "Confirmation Required",
                JOptionPane.WARNING_MESSAGE
        );

        checkAppointmentAvailability();

        return;
    }

    if (!isDoctorSlotAvailable(selectedDoctor, date, time)) {

        proactiveConfirmed = false;

        btnbookappointment.setEnabled(false);

        lblstatusvalue.setText("Unavailable");

        JOptionPane.showMessageDialog(
                this,
                "This appointment slot is no longer available.",
                "Slot Unavailable",
                JOptionPane.WARNING_MESSAGE
        );

        lastCheckedSlot = "";

        checkAppointmentAvailability();

        return;
    }

    Appointment appointment = new Appointment();

    appointment.setPatientId(selectedPatient.getPatientId());

    appointment.setDoctorId(selectedDoctor.getDoctorId());

    appointment.setAppointmentDate(date);

    appointment.setAppointmentTime(time);

    appointment.setStatus("Pending");

    appointment.setCreatedAt(LocalDateTime.now());

    boolean saved = ad.addAppointment(appointment);

    if (saved) {

        if (parentFrame instanceof AppointmentDetailsStaffFrame) {

        ((AppointmentDetailsStaffFrame) parentFrame)
                .onFollowUpAppointmentBooked();

        openBookingConfirmation(appointment);

        parentFrame.setVisible(true);
        dispose();

    } else {

        openBookingConfirmation(appointment);
    }

    } else {

        proactiveConfirmed = false;

        btnbookappointment.setEnabled(false);

        lblstatusvalue.setText("Booking Failed");

        lastCheckedSlot = "";

        JOptionPane.showMessageDialog(
                this,
                "Unable to book this appointment. The slot may have been taken.",
                "Booking Failed",
                JOptionPane.ERROR_MESSAGE
        );

        checkAppointmentAvailability();
    }
}
    private void openBookingConfirmation(Appointment appointment) {

    BookingConfirmationDialog dialog =
            new BookingConfirmationDialog(
                    this,
                    true,
                    appointment,
                    selectedPatient,
                    selectedDoctor
            );

    dialog.setLocationRelativeTo(this);

    dialog.setVisible(true);

    clearBookingForm();
}
    public void clearBookingForm() {

    isUpdatingForm = true;

    try {

        txtpatientid.setText("");

        initializePatientDetails();

        selectedPatient = null;

        cmbdoctors.setSelectedIndex(0);

        selectedDoctor = null;

        initializeDoctorDetails();

        clearAppointmentSelection();

        lblstatusvalue.setText("Not Checked");

    } finally {

        isUpdatingForm = false;
    }
}
    private void clearForm() {

    isUpdatingForm = true;

    try {

        txtpatientid.setText("");

        initializePatientDetails();

        selectedPatient = null;

        cmbdoctors.setSelectedIndex(0);

        selectedDoctor = null;

        initializeDoctorDetails();

        txtappointmentdate.setText("");

        clearAppointmentSelection();

        lblstatusvalue.setText("Not Checked");

    } finally {

        isUpdatingForm = false;
    }
}
    private void autoFillFollowUpDetails(
        Patient patient, Doctor doctor,LocalDate date) {

   
    selectedPatient = patient;
    selectedDoctor = doctor;

    txtpatientid.setText(
            String.valueOf(patient.getPatientId())
    );

    lblpatientnamevalue.setText(patient.getFullName());
    lblagevalue.setText(String.valueOf(patient.getAge()));
    lblgendervalue.setText(patient.getGender());
    lblcontactvalue.setText(patient.getContactNumber());

    cmbdoctors.setSelectedItem(doctor.getDoctorName());

    txtappointmentdate.setText(
            date.format(DATE_FORMATTER)
    );

    
    loadAppointmentTimes();
}
 private void addTimeListener() {

    cmbappointmentime.addActionListener(e -> {

        if (!isLoadingTimes && !isUpdatingForm) {

            checkAppointmentAvailability();
        }
    });
}
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lbltitle = new javax.swing.JLabel();
        lblpatientinfomation = new javax.swing.JLabel();
        lblpatientid = new javax.swing.JLabel();
        txtpatientid = new javax.swing.JTextField();
        btnsearch = new javax.swing.JButton();
        lblpatientname = new javax.swing.JLabel();
        lblpatientnamevalue = new javax.swing.JLabel();
        lblage = new javax.swing.JLabel();
        lblagevalue = new javax.swing.JLabel();
        lblgender = new javax.swing.JLabel();
        lblgendervalue = new javax.swing.JLabel();
        lblcontact = new javax.swing.JLabel();
        lblcontactvalue = new javax.swing.JLabel();
        lbldoctorinfomation = new javax.swing.JLabel();
        lblselectdoctor = new javax.swing.JLabel();
        cmbdoctors = new javax.swing.JComboBox<>();
        lbldoctorname = new javax.swing.JLabel();
        lbldoctornamevalue = new javax.swing.JLabel();
        lblspecialization = new javax.swing.JLabel();
        lblspecializationvalue = new javax.swing.JLabel();
        lbldoccontact = new javax.swing.JLabel();
        lbldoccontactvalue = new javax.swing.JLabel();
        lblconsultationfee = new javax.swing.JLabel();
        lblconsultationfeevalue = new javax.swing.JLabel();
        lblworkinghours = new javax.swing.JLabel();
        lblworkinghoursvalue = new javax.swing.JLabel();
        lblworkingdays = new javax.swing.JLabel();
        lblworkingdaysvalue = new javax.swing.JLabel();
        lblavailabilty = new javax.swing.JLabel();
        lblavailabiltyvalue = new javax.swing.JLabel();
        lblappointmentdetail = new javax.swing.JLabel();
        lblappointmentdate = new javax.swing.JLabel();
        lblappointmenttime = new javax.swing.JLabel();
        cmbappointmentime = new javax.swing.JComboBox<>();
        lblstatus = new javax.swing.JLabel();
        lblstatusvalue = new javax.swing.JLabel();
        btnback = new javax.swing.JButton();
        btnbookappointment = new javax.swing.JButton();
        btnclear = new javax.swing.JButton();
        txtappointmentdate = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        lbltitle.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        lbltitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbltitle.setText("Book Appointment");

        lblpatientinfomation.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        lblpatientinfomation.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblpatientinfomation.setText("Patient Information");

        lblpatientid.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        lblpatientid.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblpatientid.setText("Patient Id");

        txtpatientid.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N

        btnsearch.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        btnsearch.setText("Search");
        btnsearch.addActionListener(this::btnsearchActionPerformed);

        lblpatientname.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        lblpatientname.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblpatientname.setText("Patient Name");

        lblpatientnamevalue.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        lblpatientnamevalue.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblpatientnamevalue.setText("Patient Name");

        lblage.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        lblage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblage.setText("Age");

        lblagevalue.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        lblagevalue.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblagevalue.setText("Age");

        lblgender.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        lblgender.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblgender.setText("Gender");

        lblgendervalue.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        lblgendervalue.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblgendervalue.setText("Gender");

        lblcontact.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        lblcontact.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblcontact.setText("Contact Number");

        lblcontactvalue.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        lblcontactvalue.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblcontactvalue.setText("Contact Number");

        lbldoctorinfomation.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        lbldoctorinfomation.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbldoctorinfomation.setText("Doctor Information");

        lblselectdoctor.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        lblselectdoctor.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblselectdoctor.setText("Select Doctor");

        cmbdoctors.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        cmbdoctors.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select a Doctor" }));

        lbldoctorname.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        lbldoctorname.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbldoctorname.setText("Doctor Name");

        lbldoctornamevalue.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        lbldoctornamevalue.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbldoctornamevalue.setText("Doctor Name");

        lblspecialization.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        lblspecialization.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblspecialization.setText("Specialization");

        lblspecializationvalue.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        lblspecializationvalue.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblspecializationvalue.setText("Specialization");

        lbldoccontact.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        lbldoccontact.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbldoccontact.setText("Contact Number");

        lbldoccontactvalue.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        lbldoccontactvalue.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbldoccontactvalue.setText("Contact Number");

        lblconsultationfee.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        lblconsultationfee.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblconsultationfee.setText("Consultation Fee");

        lblconsultationfeevalue.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        lblconsultationfeevalue.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblconsultationfeevalue.setText("Consultation Fee");

        lblworkinghours.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        lblworkinghours.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblworkinghours.setText("Working Hours");

        lblworkinghoursvalue.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        lblworkinghoursvalue.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblworkinghoursvalue.setText("Working Hours");

        lblworkingdays.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        lblworkingdays.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblworkingdays.setText("Working Days");

        lblworkingdaysvalue.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        lblworkingdaysvalue.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblworkingdaysvalue.setText("Working Days");

        lblavailabilty.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        lblavailabilty.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblavailabilty.setText("Availabilty");

        lblavailabiltyvalue.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        lblavailabiltyvalue.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblavailabiltyvalue.setText("Availabilty");

        lblappointmentdetail.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        lblappointmentdetail.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblappointmentdetail.setText("Appointment Detail");

        lblappointmentdate.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        lblappointmentdate.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblappointmentdate.setText("Appointment Date");

        lblappointmenttime.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        lblappointmenttime.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblappointmenttime.setText("Appointment Time");

        cmbappointmentime.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        cmbappointmentime.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select a Time" }));

        lblstatus.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        lblstatus.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblstatus.setText("Status");

        lblstatusvalue.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        lblstatusvalue.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblstatusvalue.setText("Status");

        btnback.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        btnback.setText("Back");
        btnback.addActionListener(this::btnbackActionPerformed);

        btnbookappointment.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        btnbookappointment.setText("Book Appointment");
        btnbookappointment.addActionListener(this::btnbookappointmentActionPerformed);

        btnclear.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        btnclear.setText("Clear");
        btnclear.addActionListener(this::btnclearActionPerformed);

        txtappointmentdate.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblpatientname)
                                    .addComponent(lblgender))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblgendervalue)
                                    .addComponent(lblpatientnamevalue)))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lbldoctorname)
                                    .addComponent(lbldoccontact)
                                    .addComponent(lblworkinghours)
                                    .addComponent(lblavailabilty))
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(18, 18, 18)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(lbldoccontactvalue, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGroup(layout.createSequentialGroup()
                                                .addGap(24, 24, 24)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(lblavailabiltyvalue)
                                                    .addComponent(lblworkinghoursvalue)))))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(27, 27, 27)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(lbldoctornamevalue)
                                            .addComponent(lblpatientid))))
                                .addGap(64, 64, 64)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(txtpatientid, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(25, 25, 25)
                                        .addComponent(btnsearch))
                                    .addComponent(lblpatientinfomation)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(123, 123, 123)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(lblconsultationfee)
                                            .addComponent(lblworkingdays)
                                            .addComponent(lblage)
                                            .addComponent(lblcontact)
                                            .addComponent(lblspecialization))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(lblworkingdaysvalue)
                                            .addComponent(lblspecializationvalue)
                                            .addComponent(lblconsultationfeevalue)
                                            .addComponent(lblagevalue)
                                            .addComponent(lblcontactvalue)))
                                    .addComponent(lbldoctorinfomation)
                                    .addComponent(lblappointmentdetail)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(lbltitle)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(btnback)))))
                        .addGap(36, 36, 36))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(lblselectdoctor)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cmbdoctors, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(235, 235, 235))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblappointmentdate)
                                .addGap(18, 18, 18)
                                .addComponent(txtappointmentdate, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(btnbookappointment)
                                .addGroup(layout.createSequentialGroup()
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(lblappointmenttime)
                                        .addComponent(lblstatus))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(lblstatusvalue)
                                        .addComponent(cmbappointmentime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 43, Short.MAX_VALUE)
                        .addComponent(btnclear)
                        .addGap(346, 346, 346))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbltitle)
                    .addComponent(btnback))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addComponent(btnsearch)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblpatientname)
                            .addComponent(lblpatientnamevalue)
                            .addComponent(lblage)
                            .addComponent(lblagevalue))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblgender)
                            .addComponent(lblgendervalue)
                            .addComponent(lblcontact)
                            .addComponent(lblcontactvalue)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblpatientinfomation)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtpatientid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblpatientid))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lbldoctorinfomation)
                .addGap(21, 21, 21)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbdoctors, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblselectdoctor))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lbldoctorname)
                        .addComponent(lbldoctornamevalue))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblspecialization)
                        .addComponent(lblspecializationvalue)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbldoccontactvalue, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lbldoccontact)
                        .addComponent(lblconsultationfee)
                        .addComponent(lblconsultationfeevalue)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblworkinghours)
                    .addComponent(lblworkinghoursvalue)
                    .addComponent(lblworkingdays)
                    .addComponent(lblworkingdaysvalue))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblavailabilty)
                    .addComponent(lblavailabiltyvalue))
                .addGap(18, 18, 18)
                .addComponent(lblappointmentdetail)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblappointmentdate)
                    .addComponent(txtappointmentdate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbappointmentime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblappointmenttime))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblstatus)
                    .addComponent(lblstatusvalue))
                .addGap(29, 29, 29)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnbookappointment)
                    .addComponent(btnclear))
                .addContainerGap(56, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnbackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnbackActionPerformed
        parentFrame.setVisible(true);
        dispose();
    }//GEN-LAST:event_btnbackActionPerformed

    private void btnsearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnsearchActionPerformed
         searchPatient();
    }//GEN-LAST:event_btnsearchActionPerformed

    private void btnbookappointmentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnbookappointmentActionPerformed
         bookAppointment();
    }//GEN-LAST:event_btnbookappointmentActionPerformed

    private void btnclearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnclearActionPerformed
        clearForm();
    }//GEN-LAST:event_btnclearActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new BookAppointmentStaffFrame(new ManageAppointmentsStaffFrame(null)).setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnback;
    private javax.swing.JButton btnbookappointment;
    private javax.swing.JButton btnclear;
    private javax.swing.JButton btnsearch;
    private javax.swing.JComboBox<String> cmbappointmentime;
    private javax.swing.JComboBox<String> cmbdoctors;
    private javax.swing.JLabel lblage;
    private javax.swing.JLabel lblagevalue;
    private javax.swing.JLabel lblappointmentdate;
    private javax.swing.JLabel lblappointmentdetail;
    private javax.swing.JLabel lblappointmenttime;
    private javax.swing.JLabel lblavailabilty;
    private javax.swing.JLabel lblavailabiltyvalue;
    private javax.swing.JLabel lblconsultationfee;
    private javax.swing.JLabel lblconsultationfeevalue;
    private javax.swing.JLabel lblcontact;
    private javax.swing.JLabel lblcontactvalue;
    private javax.swing.JLabel lbldoccontact;
    private javax.swing.JLabel lbldoccontactvalue;
    private javax.swing.JLabel lbldoctorinfomation;
    private javax.swing.JLabel lbldoctorname;
    private javax.swing.JLabel lbldoctornamevalue;
    private javax.swing.JLabel lblgender;
    private javax.swing.JLabel lblgendervalue;
    private javax.swing.JLabel lblpatientid;
    private javax.swing.JLabel lblpatientinfomation;
    private javax.swing.JLabel lblpatientname;
    private javax.swing.JLabel lblpatientnamevalue;
    private javax.swing.JLabel lblselectdoctor;
    private javax.swing.JLabel lblspecialization;
    private javax.swing.JLabel lblspecializationvalue;
    private javax.swing.JLabel lblstatus;
    private javax.swing.JLabel lblstatusvalue;
    private javax.swing.JLabel lbltitle;
    private javax.swing.JLabel lblworkingdays;
    private javax.swing.JLabel lblworkingdaysvalue;
    private javax.swing.JLabel lblworkinghours;
    private javax.swing.JLabel lblworkinghoursvalue;
    private javax.swing.JTextField txtappointmentdate;
    private javax.swing.JTextField txtpatientid;
    // End of variables declaration//GEN-END:variables
}
