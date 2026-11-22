/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.medicalappointmentandprescription.ui.staff;
import com.mycompany.medicalappointmentandprescription.dao.AppointmentDao;
import com.mycompany.medicalappointmentandprescription.dao.PatientDao;
import com.mycompany.medicalappointmentandprescription.dao.DoctorDao;
import com.mycompany.medicalappointmentandprescription.dao.FollowUpRecommendationDao;
import com.mycompany.medicalappointmentandprescription.dao.UserDao;

import com.mycompany.medicalappointmentandprescription.models.Appointment;
import com.mycompany.medicalappointmentandprescription.models.Patient;
import com.mycompany.medicalappointmentandprescription.models.Doctor;
import com.mycompany.medicalappointmentandprescription.models.FollowUpRecommendation;
import com.mycompany.medicalappointmentandprescription.models.User;

import com.mycompany.medicalappointmentandprescription.ui.auth.LoginFrame;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
/**
 *
 * @author ranbi
 */
public class AppointmentDashboardStaffFrame extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(AppointmentDashboardStaffFrame.class.getName());
    private final UserDao ud = new UserDao();
    private final AppointmentDao ad = new AppointmentDao();
    private final PatientDao pd = new PatientDao();
    private final DoctorDao dd = new DoctorDao();
    private final FollowUpRecommendationDao fd =new FollowUpRecommendationDao();
    private User u;
    private int staffId;
    private final DateTimeFormatter timeFormatter =DateTimeFormatter.ofPattern("hh:mm a");
    private final DateTimeFormatter dateFormatter =DateTimeFormatter.ofPattern("dd-MM-yyyy");
    /**
     * Creates new form AppointmentDashboardStaffFrame
     */
    public AppointmentDashboardStaffFrame(int staffId) {
        initComponents();
        setLocationRelativeTo(null);
        this.staffId = staffId;
        loadStaffDetails();
        configureTables();
        loadDashboardData();
    }
    private void loadStaffDetails() {

    u = ud.findByUserId(staffId);

    if (u != null
            && "Staff".equalsIgnoreCase(u.getRole())
            && u.getIsActive()) {

        lblstaffname.setText(u.getFullName());

    } else {

        lblstaffname.setText("Staff");

    }
     btnrefresh.setText("↻ Refresh");
}
    private void configureTables() {

    // Today's Appointments Table
    DefaultTableModel todayModel = new DefaultTableModel(
            new Object[]{
                "Appointment ID",
                "Patient Name",
                "Doctor Name",
                "Time",
                "Status"
            }, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    tbltodayappointment.setModel(todayModel);


    // Upcoming Appointments Table
    DefaultTableModel upcomingModel = new DefaultTableModel(
            new Object[]{
                "Appointment ID",
                "Patient Name",
                "Doctor Name",
                "Date",
                "Time",
                "Status"
            }, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    tblupcomingappointment.setModel(upcomingModel);

}
    private void loadTodayAppointments() {

    DefaultTableModel model =
            (DefaultTableModel) tbltodayappointment.getModel();

    model.setRowCount(0);

    List<Appointment> appointments =ad.findByDate(LocalDate.now());

    for (Appointment a : appointments) {

        if ("Cancelled".equalsIgnoreCase(a.getStatus())) {
            continue;
        }

        Patient patient =pd.findByPatientId(a.getPatientId());

        Doctor doctor =dd.findByDoctorId(a.getDoctorId());

        String patientName = patient != null? patient.getFullName() : "Unknown";

        String doctorName = doctor != null? doctor.getDoctorName() : "Unknown";

        String time = a.getAppointmentTime() != null? a.getAppointmentTime().format(timeFormatter): "N/A";

        model.addRow(new Object[]{
            a.getAppointmentId(),
            patientName,
            doctorName,
            time,
            a.getStatus()
        });
    }
}
    private void loadUpcomingAppointments() {

    DefaultTableModel model =
            (DefaultTableModel) tblupcomingappointment.getModel();

    model.setRowCount(0);

    List<Appointment> appointments = ad.findAll();

    for (Appointment a : appointments) {

        if (a.getAppointmentDate() == null
                || !a.getAppointmentDate().isAfter(LocalDate.now())) {
            continue;
        }

        
        if ("Cancelled".equalsIgnoreCase(a.getStatus())) {
            continue;
        }

        Patient patient =pd.findByPatientId(a.getPatientId());

        Doctor doctor =dd.findByDoctorId(a.getDoctorId());

        String patientName = patient != null? patient.getFullName() : "Unknown";

        String doctorName = doctor != null? doctor.getDoctorName() : "Unknown";

        String date = a.getAppointmentDate().format(dateFormatter);

        String time = a.getAppointmentTime() != null? a.getAppointmentTime().format(timeFormatter): "N/A";

        model.addRow(new Object[]{
            a.getAppointmentId(),
            patientName,
            doctorName,
            date,
            time,
            a.getStatus()
        });
    }
}
    private void loadAppointmentTables() {

    loadTodayAppointments();

    loadUpcomingAppointments();
}
    private void loadDashboardData() {

    List<Appointment> appointments = ad.findAll();

    LocalDate today = LocalDate.now();

    int totalAppointments = appointments.size();
    int todayAppointments = 0;
    int upcomingAppointments = 0;
    int pendingFollowUps = 0;

    for (Appointment a : appointments) {

        if ("Cancelled".equalsIgnoreCase(a.getStatus())) {
            continue;
        }

        if (a.getAppointmentDate() == null) {
            continue;
        }

        if (a.getAppointmentDate().isEqual(today)) {
            todayAppointments++;
        }
        
        else if (a.getAppointmentDate().isAfter(today)) {
            upcomingAppointments++;
        }
    }

    List<FollowUpRecommendation> followUps =fd.findAll();

    for (FollowUpRecommendation f : followUps) {

        if ("Pending".equalsIgnoreCase(f.getStatus())) {
            pendingFollowUps++;
        }
    }

    lbltotalappointmentnumber.setText(String.valueOf(totalAppointments));
    lbltodayappointmentnumber.setText(String.valueOf(todayAppointments));
    lblupcomingappointmentnumber.setText(String.valueOf(upcomingAppointments));
    lblpendingfollowsupsnumber.setText(String.valueOf(pendingFollowUps));

    loadAppointmentTables();
}    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblsystemtitle = new javax.swing.JLabel();
        btnlogout = new javax.swing.JButton();
        lbltitle = new javax.swing.JLabel();
        lblwelcome = new javax.swing.JLabel();
        lblstaffname = new javax.swing.JLabel();
        lblsubtitle = new javax.swing.JLabel();
        btnrefresh = new javax.swing.JButton();
        lblappointmentoverview = new javax.swing.JLabel();
        totalappointment = new javax.swing.JPanel();
        lbltotalappointment = new javax.swing.JLabel();
        lbltotalappointmentnumber = new javax.swing.JLabel();
        todayappointment = new javax.swing.JPanel();
        lbltodayappointment = new javax.swing.JLabel();
        lbltodayappointmentnumber = new javax.swing.JLabel();
        upcomingappointment = new javax.swing.JPanel();
        lblupcomingappointment = new javax.swing.JLabel();
        lblupcomingappointmentnumber = new javax.swing.JLabel();
        pendingfollowsups = new javax.swing.JPanel();
        lblpendingfollowsups = new javax.swing.JLabel();
        lblpendingfollowsupsnumber = new javax.swing.JLabel();
        lbltodayappointmentheading = new javax.swing.JLabel();
        sptodayappointment = new javax.swing.JScrollPane();
        tbltodayappointment = new javax.swing.JTable();
        lblupcomingappointmentheading = new javax.swing.JLabel();
        spupcomingappointment = new javax.swing.JScrollPane();
        tblupcomingappointment = new javax.swing.JTable();
        lblquickaction = new javax.swing.JLabel();
        btnmanagepatient = new javax.swing.JButton();
        btnmanageappointment = new javax.swing.JButton();
        btnmedicaladminstrator = new javax.swing.JButton();
        btnreports = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        lblsystemtitle.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        lblsystemtitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblsystemtitle.setText("Medical Appointment & Prescription System");

        btnlogout.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        btnlogout.setText("Logout");
        btnlogout.addActionListener(this::btnlogoutActionPerformed);

        lbltitle.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        lbltitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbltitle.setText("Staff Dashboard");

        lblwelcome.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        lblwelcome.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblwelcome.setText("Welcome,");

        lblstaffname.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        lblstaffname.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblstaffname.setText("Staff's Name");

        lblsubtitle.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        lblsubtitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblsubtitle.setText("Overview of appointments, patients and daily activities ");

        btnrefresh.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        btnrefresh.setText("Refresh");
        btnrefresh.addActionListener(this::btnrefreshActionPerformed);

        lblappointmentoverview.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        lblappointmentoverview.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblappointmentoverview.setText("Appointment Overview");

        totalappointment.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        lbltotalappointment.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        lbltotalappointment.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbltotalappointment.setText("Total Appointment ");

        lbltotalappointmentnumber.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        lbltotalappointmentnumber.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbltotalappointmentnumber.setText("00");

        javax.swing.GroupLayout totalappointmentLayout = new javax.swing.GroupLayout(totalappointment);
        totalappointment.setLayout(totalappointmentLayout);
        totalappointmentLayout.setHorizontalGroup(
            totalappointmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(totalappointmentLayout.createSequentialGroup()
                .addContainerGap(16, Short.MAX_VALUE)
                .addGroup(totalappointmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbltotalappointment)
                    .addGroup(totalappointmentLayout.createSequentialGroup()
                        .addGap(38, 38, 38)
                        .addComponent(lbltotalappointmentnumber)))
                .addContainerGap())
        );
        totalappointmentLayout.setVerticalGroup(
            totalappointmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(totalappointmentLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(lbltotalappointment)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lbltotalappointmentnumber)
                .addContainerGap(40, Short.MAX_VALUE))
        );

        todayappointment.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        lbltodayappointment.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        lbltodayappointment.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbltodayappointment.setText("Today's Appointment ");

        lbltodayappointmentnumber.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        lbltodayappointmentnumber.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbltodayappointmentnumber.setText("00");

        javax.swing.GroupLayout todayappointmentLayout = new javax.swing.GroupLayout(todayappointment);
        todayappointment.setLayout(todayappointmentLayout);
        todayappointmentLayout.setHorizontalGroup(
            todayappointmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(todayappointmentLayout.createSequentialGroup()
                .addContainerGap(16, Short.MAX_VALUE)
                .addGroup(todayappointmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbltodayappointment)
                    .addGroup(todayappointmentLayout.createSequentialGroup()
                        .addGap(38, 38, 38)
                        .addComponent(lbltodayappointmentnumber)))
                .addContainerGap())
        );
        todayappointmentLayout.setVerticalGroup(
            todayappointmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(todayappointmentLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(lbltodayappointment)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lbltodayappointmentnumber)
                .addContainerGap(40, Short.MAX_VALUE))
        );

        upcomingappointment.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        lblupcomingappointment.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        lblupcomingappointment.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblupcomingappointment.setText("Upcoming Appointment ");

        lblupcomingappointmentnumber.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        lblupcomingappointmentnumber.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblupcomingappointmentnumber.setText("00");

        javax.swing.GroupLayout upcomingappointmentLayout = new javax.swing.GroupLayout(upcomingappointment);
        upcomingappointment.setLayout(upcomingappointmentLayout);
        upcomingappointmentLayout.setHorizontalGroup(
            upcomingappointmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(upcomingappointmentLayout.createSequentialGroup()
                .addContainerGap(16, Short.MAX_VALUE)
                .addComponent(lblupcomingappointment)
                .addContainerGap())
            .addGroup(upcomingappointmentLayout.createSequentialGroup()
                .addGap(56, 56, 56)
                .addComponent(lblupcomingappointmentnumber)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        upcomingappointmentLayout.setVerticalGroup(
            upcomingappointmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(upcomingappointmentLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(lblupcomingappointment)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblupcomingappointmentnumber)
                .addContainerGap(40, Short.MAX_VALUE))
        );

        pendingfollowsups.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        lblpendingfollowsups.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        lblpendingfollowsups.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblpendingfollowsups.setText("Pending Follows-Ups");

        lblpendingfollowsupsnumber.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        lblpendingfollowsupsnumber.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblpendingfollowsupsnumber.setText("00");

        javax.swing.GroupLayout pendingfollowsupsLayout = new javax.swing.GroupLayout(pendingfollowsups);
        pendingfollowsups.setLayout(pendingfollowsupsLayout);
        pendingfollowsupsLayout.setHorizontalGroup(
            pendingfollowsupsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pendingfollowsupsLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblpendingfollowsups)
                .addContainerGap())
            .addGroup(pendingfollowsupsLayout.createSequentialGroup()
                .addGap(56, 56, 56)
                .addComponent(lblpendingfollowsupsnumber)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pendingfollowsupsLayout.setVerticalGroup(
            pendingfollowsupsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pendingfollowsupsLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(lblpendingfollowsups)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblpendingfollowsupsnumber)
                .addContainerGap(40, Short.MAX_VALUE))
        );

        lbltodayappointmentheading.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        lbltodayappointmentheading.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbltodayappointmentheading.setText("Today's Appointment ");

        tbltodayappointment.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        tbltodayappointment.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        tbltodayappointment.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Appointment Id", "Patient Name", "Doctor Name", "Time", "Status"
            }
        ));
        sptodayappointment.setViewportView(tbltodayappointment);

        lblupcomingappointmentheading.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        lblupcomingappointmentheading.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblupcomingappointmentheading.setText("Upcoming Appointment ");

        tblupcomingappointment.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        tblupcomingappointment.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        tblupcomingappointment.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Appointment Id", "Patient Name", "Doctor Name", "Date", "Time", "Status"
            }
        ));
        spupcomingappointment.setViewportView(tblupcomingappointment);

        lblquickaction.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        lblquickaction.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblquickaction.setText("Quick Action");

        btnmanagepatient.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        btnmanagepatient.setText("Manage Patient");
        btnmanagepatient.addActionListener(this::btnmanagepatientActionPerformed);

        btnmanageappointment.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        btnmanageappointment.setText("Manage Appointment");
        btnmanageappointment.addActionListener(this::btnmanageappointmentActionPerformed);

        btnmedicaladminstrator.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        btnmedicaladminstrator.setText("Medical Adminstrator");
        btnmedicaladminstrator.addActionListener(this::btnmedicaladminstratorActionPerformed);

        btnreports.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        btnreports.setText("Reports");
        btnreports.addActionListener(this::btnreportsActionPerformed);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(167, 167, 167)
                        .addComponent(lblsystemtitle)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 115, Short.MAX_VALUE)
                        .addComponent(btnlogout))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblappointmentoverview)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(lblwelcome)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(lblstaffname))
                                    .addComponent(lbltodayappointmentheading))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnrefresh))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblupcomingappointmentheading)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(spupcomingappointment, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 630, Short.MAX_VALUE)
                                        .addComponent(sptodayappointment, javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                            .addGap(35, 35, 35)
                                            .addComponent(totalappointment, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                            .addComponent(todayappointment, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGap(18, 18, 18)
                                            .addComponent(upcomingappointment, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGap(18, 18, 18)
                                            .addComponent(pendingfollowsups, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addComponent(lblquickaction))
                                .addGap(0, 0, Short.MAX_VALUE)))))
                .addGap(24, 24, 24))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(204, 204, 204)
                        .addComponent(lblsubtitle))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(300, 300, 300)
                        .addComponent(lbltitle))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(101, 101, 101)
                        .addComponent(btnmanagepatient)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnmanageappointment)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnmedicaladminstrator)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnreports)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblsystemtitle)
                    .addComponent(btnlogout))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbltitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblwelcome)
                    .addComponent(lblstaffname)
                    .addComponent(btnrefresh))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblsubtitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblappointmentoverview)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(totalappointment, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(todayappointment, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(upcomingappointment, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pendingfollowsups, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lbltodayappointmentheading)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sptodayappointment, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblupcomingappointmentheading)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(spupcomingappointment, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lblquickaction)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnmanagepatient)
                    .addComponent(btnmanageappointment)
                    .addComponent(btnmedicaladminstrator)
                    .addComponent(btnreports))
                .addContainerGap(77, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnlogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnlogoutActionPerformed
        
    int choice = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to logout?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
    );

    if (choice == JOptionPane.YES_OPTION) {

        LoginFrame loginFrame = new LoginFrame();
        loginFrame.setLocationRelativeTo(null);
        loginFrame.setVisible(true);

        this.dispose();
    }
    }//GEN-LAST:event_btnlogoutActionPerformed

    private void btnrefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnrefreshActionPerformed
       
    btnrefresh.setText("Refreshing...");
    btnrefresh.setEnabled(false);

    SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

        @Override
        protected Void doInBackground() throws Exception {
            Thread.sleep(300);
            return null;
        }

        @Override
        protected void done() {
            try {
                loadStaffDetails();
                loadDashboardData();

            } finally {
                btnrefresh.setText("↻ Refresh");
                btnrefresh.setEnabled(true);
            }
        }
    };

    worker.execute();

    }//GEN-LAST:event_btnrefreshActionPerformed

    private void btnmanagepatientActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnmanagepatientActionPerformed
        ManagePatientFrame frame = new ManagePatientFrame(u.getUserId());
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        this.dispose();
    }//GEN-LAST:event_btnmanagepatientActionPerformed

    private void btnmanageappointmentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnmanageappointmentActionPerformed
        ManageAppointmentsStaffFrame frame = new ManageAppointmentsStaffFrame(this);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        this.dispose();
    }//GEN-LAST:event_btnmanageappointmentActionPerformed

    private void btnmedicaladminstratorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnmedicaladminstratorActionPerformed
        MedicineAdministrationFrame frame = new MedicineAdministrationFrame(this);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        this.dispose();
    }//GEN-LAST:event_btnmedicaladminstratorActionPerformed

    private void btnreportsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnreportsActionPerformed
        ReportStaffFrame frame = new ReportStaffFrame(this);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        this.dispose();
    }//GEN-LAST:event_btnreportsActionPerformed

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
        java.awt.EventQueue.invokeLater(() -> new AppointmentDashboardStaffFrame(2).setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnlogout;
    private javax.swing.JButton btnmanageappointment;
    private javax.swing.JButton btnmanagepatient;
    private javax.swing.JButton btnmedicaladminstrator;
    private javax.swing.JButton btnrefresh;
    private javax.swing.JButton btnreports;
    private javax.swing.JLabel lblappointmentoverview;
    private javax.swing.JLabel lblpendingfollowsups;
    private javax.swing.JLabel lblpendingfollowsupsnumber;
    private javax.swing.JLabel lblquickaction;
    private javax.swing.JLabel lblstaffname;
    private javax.swing.JLabel lblsubtitle;
    private javax.swing.JLabel lblsystemtitle;
    private javax.swing.JLabel lbltitle;
    private javax.swing.JLabel lbltodayappointment;
    private javax.swing.JLabel lbltodayappointmentheading;
    private javax.swing.JLabel lbltodayappointmentnumber;
    private javax.swing.JLabel lbltotalappointment;
    private javax.swing.JLabel lbltotalappointmentnumber;
    private javax.swing.JLabel lblupcomingappointment;
    private javax.swing.JLabel lblupcomingappointmentheading;
    private javax.swing.JLabel lblupcomingappointmentnumber;
    private javax.swing.JLabel lblwelcome;
    private javax.swing.JPanel pendingfollowsups;
    private javax.swing.JScrollPane sptodayappointment;
    private javax.swing.JScrollPane spupcomingappointment;
    private javax.swing.JTable tbltodayappointment;
    private javax.swing.JTable tblupcomingappointment;
    private javax.swing.JPanel todayappointment;
    private javax.swing.JPanel totalappointment;
    private javax.swing.JPanel upcomingappointment;
    // End of variables declaration//GEN-END:variables
}
