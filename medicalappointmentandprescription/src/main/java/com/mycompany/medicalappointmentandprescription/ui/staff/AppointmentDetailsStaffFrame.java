/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.medicalappointmentandprescription.ui.staff;
import com.mycompany.medicalappointmentandprescription.dao.AppointmentDao;
import com.mycompany.medicalappointmentandprescription.dao.PatientDao;
import com.mycompany.medicalappointmentandprescription.dao.DoctorDao;

import com.mycompany.medicalappointmentandprescription.models.Appointment;
import com.mycompany.medicalappointmentandprescription.models.Patient;
import com.mycompany.medicalappointmentandprescription.models.Doctor;
import com.mycompany.medicalappointmentandprescription.models.FollowUpRecommendation;
import com.mycompany.medicalappointmentandprescription.dao.FollowUpRecommendationDao;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
/**
 *
 * @author ranbi
 */
public class AppointmentDetailsStaffFrame extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(AppointmentDetailsStaffFrame.class.getName());
    private final AppointmentDao ad = new AppointmentDao();
    private final PatientDao pd = new PatientDao();
    private final DoctorDao dd = new DoctorDao();
    private final FollowUpRecommendationDao fd =new FollowUpRecommendationDao();

   private int appointmentId;

   private Appointment appointment;
   private Patient patient;
   private Doctor doctor;
   private FollowUpRecommendation followUp;

   private ManageAppointmentsStaffFrame parentFrame;

   private static final DateTimeFormatter DATE_FORMATTER =DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private static final DateTimeFormatter TIME_FORMATTER =DateTimeFormatter.ofPattern("hh:mm a");
    /**
     * Creates new form AppointmentDetailsStaffFrame
     */
    public AppointmentDetailsStaffFrame(int appointmentId,ManageAppointmentsStaffFrame parent) {
        initComponents();
         this.appointmentId = appointmentId;
         this.parentFrame = parent;

       setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
       setLocationRelativeTo(null);

      initializeFollowUpSection();
      loadAppointmentDetails();
        
    }
    private void initializeFollowUpSection() {

    lblfollowid.setVisible(false);
    lblfollowidvalue.setVisible(false);

    lblrecommendatedate.setVisible(false);
    lblrecommendatedatevalue.setVisible(false);

    lblstatus1.setVisible(false);
    lblstatusvalue.setVisible(false);

    btnbookfollowups.setVisible(false);

    lblnofollowsups.setVisible(false);
}
    private void loadAppointmentDetails() {

    appointment = ad.findByAppointmentId(appointmentId);

    if (appointment == null) {
        JOptionPane.showMessageDialog(
                this,
                "Appointment not found."
        );
        dispose();
        return;
    }

    patient = pd.findByPatientId(appointment.getPatientId());
    doctor = dd.findByDoctorId(appointment.getDoctorId());

    if (patient == null || doctor == null) {
        JOptionPane.showMessageDialog(
                this,
                "Patient or Doctor details not found."
        );
        dispose();
        return;
    }

    displayAppointmentDetails();
    displayPatientDetails();
    displayDoctorDetails();

    loadFollowUpDetails();
}
    private void displayAppointmentDetails() {

    lblappointmentidvalue.setText(
            String.valueOf(appointment.getAppointmentId())
    );

    txtappointmentdate.setText(
            appointment.getAppointmentDate().format(DATE_FORMATTER)
    );

    cmbappointmentime.removeAllItems();

    cmbappointmentime.addItem(
            appointment.getAppointmentTime().format(TIME_FORMATTER)
    );

    cmbstatus.setSelectedItem(appointment.getStatus());
    btnrefresh.setText("↺ Refresh");
}
    private void displayPatientDetails() {

    lblpatientidvalue.setText(
            String.valueOf(patient.getPatientId())
    );

    lblpatientnamevalue.setText(patient.getFullName());

    lblagevalue.setText(
            String.valueOf(patient.getAge())
    );

    lblgendervalue.setText(patient.getGender());

    lblcontactvalue.setText(patient.getContactNumber());
}
    private void displayDoctorDetails() {

    lbldoctoridvalue.setText(
            String.valueOf(doctor.getDoctorId())
    );

    lbldoctornamevalue.setText(doctor.getDoctorName());

    lblspecializationvalue.setText(
            doctor.getSpecialization()
    );

    lblconsultationfeesvalue.setText(
            "₹" + doctor.getConsultationFee()
    );
}
    private void loadFollowUpDetails() {

    followUp = fd.findByAppointmentId(appointmentId);

    if (followUp == null) {

        lblfollowid.setVisible(false);
        lblfollowidvalue.setVisible(false);

        lblrecommendatedate.setVisible(false);
        lblrecommendatedatevalue.setVisible(false);

        lblstatus1.setVisible(false);
        lblstatusvalue.setVisible(false);

        btnbookfollowups.setVisible(false);

        lblnofollowsups.setVisible(true);

        return;
    }

    lblnofollowsups.setVisible(false);

    lblfollowid.setVisible(true);
    lblfollowidvalue.setVisible(true);

    lblrecommendatedate.setVisible(true);
    lblrecommendatedatevalue.setVisible(true);

    lblstatus1.setVisible(true);
    lblstatusvalue.setVisible(true);

    lblfollowidvalue.setText(
            String.valueOf(followUp.getFollowUpId())
    );

    lblrecommendatedatevalue.setText(
            followUp.getRecommendedDate().format(DATE_FORMATTER)
    );

    lblstatusvalue.setText(followUp.getStatus());

    btnbookfollowups.setVisible(
            "Pending".equalsIgnoreCase(followUp.getStatus())
    );
}
    private void bookFollowUp() {

    if (followUp == null) {
        JOptionPane.showMessageDialog(
                this,
                "No follow-up recommendation found."
        );
        return;
    }

    if (!"Pending".equalsIgnoreCase(followUp.getStatus())) {
        JOptionPane.showMessageDialog(
                this,
                "This follow-up is not pending."
        );
        return;
    }

    BookAppointmentStaffFrame frame =
            new BookAppointmentStaffFrame(
                    this,
                    patient,
                    doctor,
                    followUp.getRecommendedDate()
            );

    frame.setVisible(true);
    setVisible(false);
}
    public void onFollowUpAppointmentBooked() {

    if (followUp == null) {
        return;
    }

    followUp.setStatus("Scheduled");

    if (fd.updateFollowUp(followUp)) {
        loadFollowUpDetails();
    } else {
        JOptionPane.showMessageDialog(
                this,
                "Appointment booked, but follow-up status could not be updated."
        );
    }
}
    private void saveChanges() {

    LocalDate date;
    LocalTime time;

    try {
        date = LocalDate.parse(
                txtappointmentdate.getText().trim(),
                DATE_FORMATTER
        );

        time = LocalTime.parse(
                (String) cmbappointmentime.getSelectedItem(),
                TIME_FORMATTER
        );

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Invalid date or time.");
        return;
    }

    String status = (String) cmbstatus.getSelectedItem();

    appointment.setAppointmentDate(date);
    appointment.setAppointmentTime(time);
    appointment.setStatus(status);

    if (ad.updateAppointment(appointment)) {

        JOptionPane.showMessageDialog(
                this,
                "Appointment updated successfully."
        );

        loadAppointmentDetails();

        if (parentFrame != null) {
            parentFrame.loadAppointmentTable();
        }

    } else {
        JOptionPane.showMessageDialog(
                this,
                "Unable to update appointment."
        );
    }
}
    private void deleteAppointment() {

    int choice = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete this appointment?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION
    );

    if (choice == JOptionPane.YES_OPTION) {

        if (ad.deleteAppointment(appointmentId)) {

            JOptionPane.showMessageDialog(
                    this,
                    "Appointment deleted successfully."
            );

            if (parentFrame != null) {
                parentFrame.loadAppointmentTable();
            }

            dispose();

        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Appointment cannot be deleted because a related prescription exists."
            );
        }
    }
}
    
 
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblappointmentid = new javax.swing.JLabel();
        lblappointmentidvalue = new javax.swing.JLabel();
        lblappointmentdetail = new javax.swing.JLabel();
        lblappointmentinformation = new javax.swing.JLabel();
        txtappointmentdate = new javax.swing.JTextField();
        lblappointmentdate = new javax.swing.JLabel();
        lblappointmenttime = new javax.swing.JLabel();
        cmbappointmentime = new javax.swing.JComboBox<>();
        lblstatus = new javax.swing.JLabel();
        cmbstatus = new javax.swing.JComboBox<>();
        lblpatientinformation = new javax.swing.JLabel();
        lblpatientid = new javax.swing.JLabel();
        lblpatientidvalue = new javax.swing.JLabel();
        lblpatientname = new javax.swing.JLabel();
        lblpatientnamevalue = new javax.swing.JLabel();
        lblage = new javax.swing.JLabel();
        lblagevalue = new javax.swing.JLabel();
        lblgender = new javax.swing.JLabel();
        lblgendervalue = new javax.swing.JLabel();
        lblcontact = new javax.swing.JLabel();
        lblcontactvalue = new javax.swing.JLabel();
        lblspecialization = new javax.swing.JLabel();
        lblspecializationvalue = new javax.swing.JLabel();
        lbldoctorinformation = new javax.swing.JLabel();
        lbldoctorname = new javax.swing.JLabel();
        lbldoctornamevalue = new javax.swing.JLabel();
        lbldoctoridvalue = new javax.swing.JLabel();
        lbldoctorid = new javax.swing.JLabel();
        lblconsultationfees = new javax.swing.JLabel();
        lblconsultationfeesvalue = new javax.swing.JLabel();
        lblfollowsups = new javax.swing.JLabel();
        lblfollowidvalue = new javax.swing.JLabel();
        lblfollowid = new javax.swing.JLabel();
        lblrecommendatedate = new javax.swing.JLabel();
        lblrecommendatedatevalue = new javax.swing.JLabel();
        lblstatus1 = new javax.swing.JLabel();
        lblstatusvalue = new javax.swing.JLabel();
        lblnofollowsups = new javax.swing.JLabel();
        btnrefresh = new javax.swing.JButton();
        btnback = new javax.swing.JButton();
        btnbookfollowups = new javax.swing.JButton();
        btnsavechanges = new javax.swing.JButton();
        btndelete = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        lblappointmentid.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        lblappointmentid.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblappointmentid.setText("Appointment Id");

        lblappointmentidvalue.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        lblappointmentidvalue.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblappointmentidvalue.setText("-");

        lblappointmentdetail.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        lblappointmentdetail.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblappointmentdetail.setText("Appointment Detail");

        lblappointmentinformation.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        lblappointmentinformation.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblappointmentinformation.setText("Appointment Information");

        txtappointmentdate.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N

        lblappointmentdate.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        lblappointmentdate.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblappointmentdate.setText("Appointment Date");

        lblappointmenttime.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        lblappointmenttime.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblappointmenttime.setText("Appointment Time");

        cmbappointmentime.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        cmbappointmentime.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select a Time" }));

        lblstatus.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        lblstatus.setText("Status:");

        cmbstatus.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        cmbstatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select a Staus", "Pending", "Confirmed", "Completed", "Cancelled" }));

        lblpatientinformation.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        lblpatientinformation.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblpatientinformation.setText("Patient Information");

        lblpatientid.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        lblpatientid.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblpatientid.setText("Patient Id");

        lblpatientidvalue.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        lblpatientidvalue.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblpatientidvalue.setText("Patient Id");

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

        lblspecialization.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        lblspecialization.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblspecialization.setText("Specialization");

        lblspecializationvalue.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        lblspecializationvalue.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblspecializationvalue.setText("Specialization");

        lbldoctorinformation.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        lbldoctorinformation.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbldoctorinformation.setText("Doctor Information");

        lbldoctorname.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        lbldoctorname.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbldoctorname.setText("Doctor Name");

        lbldoctornamevalue.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        lbldoctornamevalue.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbldoctornamevalue.setText("-");

        lbldoctoridvalue.setText("Doctor id");

        lbldoctorid.setText("Doctor id:");

        lblconsultationfees.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        lblconsultationfees.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblconsultationfees.setText("Consultation Fees");

        lblconsultationfeesvalue.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        lblconsultationfeesvalue.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblconsultationfeesvalue.setText("Consultation Fees");

        lblfollowsups.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        lblfollowsups.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblfollowsups.setText("Follow Ups Recommdation");

        lblfollowidvalue.setText("Follows id");

        lblfollowid.setText("Follows id:");

        lblrecommendatedate.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        lblrecommendatedate.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblrecommendatedate.setText("Recommendate Date");

        lblrecommendatedatevalue.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        lblrecommendatedatevalue.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblrecommendatedatevalue.setText("Recommendate Date");

        lblstatus1.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        lblstatus1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblstatus1.setText("Status");

        lblstatusvalue.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        lblstatusvalue.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblstatusvalue.setText("Status");

        lblnofollowsups.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        lblnofollowsups.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblnofollowsups.setText("  No follow-up recommendation found.");

        btnrefresh.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        btnrefresh.setText("Refresh");
        btnrefresh.addActionListener(this::btnrefreshActionPerformed);

        btnback.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        btnback.setText("Back");
        btnback.addActionListener(this::btnbackActionPerformed);

        btnbookfollowups.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        btnbookfollowups.setText("Book Follows Ups");
        btnbookfollowups.addActionListener(this::btnbookfollowupsActionPerformed);

        btnsavechanges.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        btnsavechanges.setText("Save Changes");
        btnsavechanges.addActionListener(this::btnsavechangesActionPerformed);

        btndelete.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        btndelete.setText("Delete");
        btndelete.addActionListener(this::btndeleteActionPerformed);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(296, 296, 296)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblappointmentinformation)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblappointmentdetail)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 126, Short.MAX_VALUE)
                                .addComponent(btnrefresh))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(288, 288, 288)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btnbookfollowups)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(lbldoctorinformation)
                                .addComponent(lblpatientinformation)
                                .addComponent(lblfollowsups)))))
                .addGap(18, 18, 18)
                .addComponent(btnback)
                .addGap(29, 29, 29))
            .addGroup(layout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblstatus1)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblappointmentid)
                                    .addComponent(lblappointmenttime))
                                .addGap(34, 34, 34)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblappointmentidvalue)
                                    .addComponent(cmbappointmentime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(lblage)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(lblagevalue, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                    .addComponent(lblpatientid)
                                    .addGap(81, 81, 81)
                                    .addComponent(lblpatientidvalue)))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblcontact)
                                .addGap(18, 18, 18)
                                .addComponent(lblcontactvalue, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblspecialization)
                                    .addComponent(lbldoctorid))
                                .addGap(61, 61, 61)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lbldoctoridvalue)
                                    .addComponent(lblspecializationvalue)
                                    .addComponent(lblfollowidvalue)
                                    .addComponent(lblstatusvalue)))
                            .addComponent(lblfollowid))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lbldoctorname)
                                    .addComponent(lblconsultationfees))
                                .addGap(23, 23, 23)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblconsultationfeesvalue)
                                    .addComponent(lbldoctornamevalue))
                                .addGap(96, 96, 96))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(lblrecommendatedate)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(lblrecommendatedatevalue))
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                            .addComponent(lblpatientname)
                                            .addGap(45, 45, 45)
                                            .addComponent(lblpatientnamevalue))
                                        .addGroup(layout.createSequentialGroup()
                                            .addComponent(lblappointmentdate)
                                            .addGap(18, 18, 18)
                                            .addComponent(txtappointmentdate, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(layout.createSequentialGroup()
                                            .addComponent(lblstatus)
                                            .addGap(72, 72, 72)
                                            .addComponent(cmbstatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(layout.createSequentialGroup()
                                            .addComponent(lblgender)
                                            .addGap(75, 75, 75)
                                            .addComponent(lblgendervalue, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addGap(85, 85, 85))))))
            .addGroup(layout.createSequentialGroup()
                .addGap(251, 251, 251)
                .addComponent(lblnofollowsups)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGap(147, 147, 147)
                .addComponent(btnsavechanges)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btndelete)
                .addGap(183, 183, 183))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblappointmentdetail)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnrefresh)
                        .addComponent(btnback)))
                .addGap(18, 18, 18)
                .addComponent(lblappointmentinformation, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblappointmentid, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblappointmentidvalue, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblappointmentdate)
                    .addComponent(txtappointmentdate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbappointmentime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblappointmenttime)
                    .addComponent(lblstatus)
                    .addComponent(cmbstatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(lblpatientinformation, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblpatientid, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblpatientidvalue, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblage)
                            .addComponent(lblagevalue)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblpatientname, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblpatientnamevalue, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblgender)
                            .addComponent(lblgendervalue))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblcontact)
                    .addComponent(lblcontactvalue))
                .addGap(22, 22, 22)
                .addComponent(lbldoctorinformation, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lbldoctoridvalue)
                            .addComponent(lbldoctorid))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblspecialization, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblspecializationvalue, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lbldoctorname, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lbldoctornamevalue, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblconsultationfees, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblconsultationfeesvalue, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(18, 18, 18)
                .addComponent(lblfollowsups, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblfollowidvalue)
                    .addComponent(lblfollowid)
                    .addComponent(lblrecommendatedate, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblrecommendatedatevalue, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblstatus1)
                    .addComponent(lblstatusvalue))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnbookfollowups)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblnofollowsups, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnsavechanges)
                    .addComponent(btndelete))
                .addGap(31, 31, 31))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnbackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnbackActionPerformed
         if (parentFrame != null) {
        parentFrame.setVisible(true);
    }

    dispose();
    }//GEN-LAST:event_btnbackActionPerformed

    private void btnrefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnrefreshActionPerformed
       btnrefresh.setText("Refreshing...");
    btnrefresh.setEnabled(false);

    SwingWorker<Void, Void> worker = new SwingWorker<>() {

        @Override
        protected Void doInBackground() throws Exception {

            Thread.sleep(300);

            return null;
        }

        @Override
        protected void done() {

            try {

                loadAppointmentDetails();

            } finally {

                btnrefresh.setText("↺ Refresh");
                btnrefresh.setEnabled(true);
            }
        }
    };

    worker.execute();
    }//GEN-LAST:event_btnrefreshActionPerformed

    private void btnbookfollowupsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnbookfollowupsActionPerformed
        bookFollowUp();
    }//GEN-LAST:event_btnbookfollowupsActionPerformed

    private void btnsavechangesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnsavechangesActionPerformed
        saveChanges();
    }//GEN-LAST:event_btnsavechangesActionPerformed

    private void btndeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btndeleteActionPerformed
       deleteAppointment();
    }//GEN-LAST:event_btndeleteActionPerformed

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
        java.awt.EventQueue.invokeLater(() -> new AppointmentDetailsStaffFrame(1,new  ManageAppointmentsStaffFrame(null)).setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnback;
    private javax.swing.JButton btnbookfollowups;
    private javax.swing.JButton btndelete;
    private javax.swing.JButton btnrefresh;
    private javax.swing.JButton btnsavechanges;
    private javax.swing.JComboBox<String> cmbappointmentime;
    private javax.swing.JComboBox<String> cmbstatus;
    private javax.swing.JLabel lblage;
    private javax.swing.JLabel lblagevalue;
    private javax.swing.JLabel lblappointmentdate;
    private javax.swing.JLabel lblappointmentdetail;
    private javax.swing.JLabel lblappointmentid;
    private javax.swing.JLabel lblappointmentidvalue;
    private javax.swing.JLabel lblappointmentinformation;
    private javax.swing.JLabel lblappointmenttime;
    private javax.swing.JLabel lblconsultationfees;
    private javax.swing.JLabel lblconsultationfeesvalue;
    private javax.swing.JLabel lblcontact;
    private javax.swing.JLabel lblcontactvalue;
    private javax.swing.JLabel lbldoctorid;
    private javax.swing.JLabel lbldoctoridvalue;
    private javax.swing.JLabel lbldoctorinformation;
    private javax.swing.JLabel lbldoctorname;
    private javax.swing.JLabel lbldoctornamevalue;
    private javax.swing.JLabel lblfollowid;
    private javax.swing.JLabel lblfollowidvalue;
    private javax.swing.JLabel lblfollowsups;
    private javax.swing.JLabel lblgender;
    private javax.swing.JLabel lblgendervalue;
    private javax.swing.JLabel lblnofollowsups;
    private javax.swing.JLabel lblpatientid;
    private javax.swing.JLabel lblpatientidvalue;
    private javax.swing.JLabel lblpatientinformation;
    private javax.swing.JLabel lblpatientname;
    private javax.swing.JLabel lblpatientnamevalue;
    private javax.swing.JLabel lblrecommendatedate;
    private javax.swing.JLabel lblrecommendatedatevalue;
    private javax.swing.JLabel lblspecialization;
    private javax.swing.JLabel lblspecializationvalue;
    private javax.swing.JLabel lblstatus;
    private javax.swing.JLabel lblstatus1;
    private javax.swing.JLabel lblstatusvalue;
    private javax.swing.JTextField txtappointmentdate;
    // End of variables declaration//GEN-END:variables
}
