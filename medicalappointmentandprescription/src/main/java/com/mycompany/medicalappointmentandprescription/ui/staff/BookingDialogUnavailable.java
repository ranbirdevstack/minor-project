/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package com.mycompany.medicalappointmentandprescription.ui.staff;
import com.mycompany.medicalappointmentandprescription.models.Doctor;
import com.mycompany.medicalappointmentandprescription.models.Patient;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.JOptionPane;
import java.time.format.DateTimeParseException;
/**
 *
 * @author ranbi
 */
public class BookingDialogUnavailable extends javax.swing.JDialog {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(BookingDialogUnavailable.class.getName());
    private Patient selectedPatient;
    private Doctor selectedDoctor;
    private BookAppointmentStaffFrame parentFrame;
    private LocalDate originalDate;
    private LocalTime originalTime;
    private LocalDate alternativeDate;
    private LocalTime alternativeTime;

    private boolean slotSelected = false;
    
    private static final DateTimeFormatter DATE_FORMATTER =DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private static final DateTimeFormatter TIME_FORMATTER =DateTimeFormatter.ofPattern("hh:mm a");
    private static Doctor doctor1 = null;
    private static Patient patient1 = null;
    private static LocalDate date1 = LocalDate.now();
    private static LocalTime time1 = LocalTime.of(10, 0);
    /**
     * Creates new form BookingUnavailableDialog
     */
    public BookingDialogUnavailable(BookAppointmentStaffFrame parent, boolean modal,Patient patient,Doctor doctor,LocalDate selectedDate,LocalTime selectedTime) {
        super(parent, modal);
        initComponents();
         this.selectedPatient = patient;
        this.selectedDoctor = doctor;
       this.parentFrame = parent;

        this.originalDate = selectedDate;
       this.originalTime = selectedTime;

    loadAppointmentDetails();
    addAlternativeSlotListeners();
    }
    private void loadAppointmentDetails() {

    if (selectedPatient == null || selectedDoctor == null) {
        return;
    }

    lblpatientidvalue.setText(
            String.valueOf(selectedPatient.getPatientId())
    );

    lblpatientnamevalue.setText(
            selectedPatient.getFullName()
    );

    lblspecializationvalue.setText(
            selectedDoctor.getSpecialization()
    );

    lbldoctornamevalue.setText(
            selectedDoctor.getDoctorName()
    );

    lblappointmentdatevalue.setText(
            originalDate.format(DATE_FORMATTER)
    );

    lblappointmenttimevalue.setText(
            originalTime.format(TIME_FORMATTER)
    );

    lbstatusvalue.setText("Unavailable");

    txtappointmentdate.setText(
            originalDate.format(DATE_FORMATTER)
    );

    cmbappointmentime.removeAllItems();

    lblstatusvalue.setText("Select an alternative slot");

    btnselectthisslot.setEnabled(false);
}
    private LocalDate getAlternativeDate() {

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
                "Please enter date in dd-MM-yyyy format.",
                "Invalid Date",
                JOptionPane.WARNING_MESSAGE
        );

        return null;
    }
}
    private void loadAlternativeTimeSlots() {

    cmbappointmentime.removeAllItems();

    cmbappointmentime.addItem("Select a Time");

    LocalDate date = getAlternativeDate();

    if (date == null) {
        return;
    }

    if (!selectedDoctor.getAvaliabilityStatus()) {

        lblstatusvalue.setText("Doctor Unavailable");
        btnselectthisslot.setEnabled(false);

        return;
    }

    if (!selectedDoctor.getWorkingDays().contains(
            date.getDayOfWeek())) {

        lblstatusvalue.setText(
                "Doctor does not work on this day"
        );

        btnselectthisslot.setEnabled(false);

        return;
    }

    List<LocalTime> timeSlots =
        parentFrame.findAlternativeSlots(selectedDoctor,date,originalTime);

    for (LocalTime time : timeSlots) {

        cmbappointmentime.addItem(
                time.format(TIME_FORMATTER)
        );
    }

    lblstatusvalue.setText("Select an alternative time");

    btnselectthisslot.setEnabled(false);
}
    private LocalTime getAlternativeTime() {

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
    private void checkAlternativeSlotAvailability() {

    alternativeDate = getAlternativeDate();

    alternativeTime = getAlternativeTime();

    btnselectthisslot.setEnabled(false);

    if (alternativeDate == null
            || alternativeTime == null) {

        lblstatusvalue.setText("Select a valid date and time");

        return;
    }

    BookAppointmentStaffFrame parentFrame =(BookAppointmentStaffFrame) getOwner();

    boolean available = parentFrame.isDoctorSlotAvailable(
            selectedDoctor,
            alternativeDate,
            alternativeTime
    );

    if (available) {

        lblstatusvalue.setText("Available");

        btnselectthisslot.setEnabled(true);

    } else {

        lblstatusvalue.setText("Unavailable");

        btnselectthisslot.setEnabled(false);
    }
}
    private void addAlternativeSlotListeners() {

    txtappointmentdate.getDocument().addDocumentListener(
            new DocumentListener() {

        @Override
        public void insertUpdate(DocumentEvent e) {
            loadAlternativeTimeSlots();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            loadAlternativeTimeSlots();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            loadAlternativeTimeSlots();
        }
    });

    cmbappointmentime.addActionListener(e -> {

        checkAlternativeSlotAvailability();

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
        lblsubtitle = new javax.swing.JLabel();
        btncancel = new javax.swing.JButton();
        lblpatientinformation = new javax.swing.JLabel();
        lblpatientid = new javax.swing.JLabel();
        lblpatientidvalue = new javax.swing.JLabel();
        lblpatientname = new javax.swing.JLabel();
        lblpatientnamevalue = new javax.swing.JLabel();
        lblspecialization = new javax.swing.JLabel();
        lblspecializationvalue = new javax.swing.JLabel();
        lbldoctorinformation = new javax.swing.JLabel();
        lbldoctorname = new javax.swing.JLabel();
        lbldoctornamevalue = new javax.swing.JLabel();
        lblselectedappointment = new javax.swing.JLabel();
        lblappointmentdate = new javax.swing.JLabel();
        lblappointmentdatevalue = new javax.swing.JLabel();
        lblstatus = new javax.swing.JLabel();
        lbstatusvalue = new javax.swing.JLabel();
        lblappointmenttime = new javax.swing.JLabel();
        lblappointmenttimevalue = new javax.swing.JLabel();
        txtappointmentdate = new javax.swing.JTextField();
        lblalternativeslot = new javax.swing.JLabel();
        lblappointmentdate1 = new javax.swing.JLabel();
        lblappointmenttime1 = new javax.swing.JLabel();
        cmbappointmentime = new javax.swing.JComboBox<>();
        lblstatus1 = new javax.swing.JLabel();
        lblstatusvalue = new javax.swing.JLabel();
        btnselectthisslot = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        lbltitle.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        lbltitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbltitle.setText("Slot Unavailable");

        lblsubtitle.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        lblsubtitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblsubtitle.setText("<html> The selected appointment slot is unavailable.<br> Please choose an alternative available slot. </html>");

        btncancel.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        btncancel.setText("Cancel");
        btncancel.addActionListener(this::btncancelActionPerformed);

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
        lbldoctornamevalue.setText("Doctor Name");

        lblselectedappointment.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        lblselectedappointment.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblselectedappointment.setText("Selected Appointment");

        lblappointmentdate.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        lblappointmentdate.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblappointmentdate.setText("Appointment Date");

        lblappointmentdatevalue.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        lblappointmentdatevalue.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblappointmentdatevalue.setText("Appointment Date");

        lblstatus.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        lblstatus.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblstatus.setText("Status");

        lbstatusvalue.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        lbstatusvalue.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbstatusvalue.setText("Status");

        lblappointmenttime.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        lblappointmenttime.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblappointmenttime.setText("Appointment Time");

        lblappointmenttimevalue.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        lblappointmenttimevalue.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblappointmenttimevalue.setText("Appointment Time");

        txtappointmentdate.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N

        lblalternativeslot.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        lblalternativeslot.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblalternativeslot.setText("Alternative Slot");

        lblappointmentdate1.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        lblappointmentdate1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblappointmentdate1.setText("Appointment Date");

        lblappointmenttime1.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        lblappointmenttime1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblappointmenttime1.setText("Appointment Time");

        cmbappointmentime.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        cmbappointmentime.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select a Time" }));

        lblstatus1.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        lblstatus1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblstatus1.setText("Status");

        lblstatusvalue.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        lblstatusvalue.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblstatusvalue.setText("Status");

        btnselectthisslot.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        btnselectthisslot.setText("Select this Slot");
        btnselectthisslot.addActionListener(this::btnselectthisslotActionPerformed);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(248, 248, 248)
                        .addComponent(lblsubtitle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(310, 310, 310)
                        .addComponent(lbltitle)
                        .addGap(215, 215, 215)
                        .addComponent(btncancel))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(306, 306, 306)
                        .addComponent(lblpatientinformation))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(32, 32, 32)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblappointmenttime, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(layout.createSequentialGroup()
                                                .addGap(99, 99, 99)
                                                .addComponent(lbldoctornamevalue))
                                            .addComponent(lbldoctorname)
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(lblpatientid)
                                                .addGap(52, 52, 52)
                                                .addComponent(lblpatientidvalue)))
                                        .addGap(328, 328, 328)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(lblpatientname)
                                            .addComponent(lblspecialization))
                                        .addGap(0, 0, Short.MAX_VALUE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(lblappointmentdate)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(lbstatusvalue)
                                            .addComponent(lblappointmentdatevalue))
                                        .addGap(396, 396, 396)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblpatientnamevalue)
                                    .addComponent(lblspecializationvalue)
                                    .addComponent(lblappointmenttimevalue)))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(274, 274, 274)
                                        .addComponent(lbldoctorinformation))
                                    .addComponent(lblstatus)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(lblappointmentdate1)
                                        .addGap(18, 18, 18)
                                        .addComponent(txtappointmentdate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(lblappointmenttime1)
                                            .addComponent(lblstatus1))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(lblstatusvalue)
                                            .addComponent(cmbappointmentime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(273, 273, 273)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(lblselectedappointment)
                                            .addGroup(layout.createSequentialGroup()
                                                .addGap(11, 11, 11)
                                                .addComponent(lblalternativeslot)))))
                                .addGap(0, 0, Short.MAX_VALUE)))))
                .addGap(196, 196, 196))
            .addGroup(layout.createSequentialGroup()
                .addGap(63, 63, 63)
                .addComponent(btnselectthisslot)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(9, 9, 9)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbltitle)
                    .addComponent(btncancel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblsubtitle, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblpatientinformation, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblpatientid)
                    .addComponent(lblpatientidvalue)
                    .addComponent(lblpatientname)
                    .addComponent(lblpatientnamevalue))
                .addGap(33, 33, 33)
                .addComponent(lbldoctorinformation, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbldoctorname, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbldoctornamevalue, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblspecialization, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblspecializationvalue, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(lblselectedappointment, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblappointmentdate, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblappointmentdatevalue, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblappointmenttime, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblappointmenttimevalue, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblstatus, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbstatusvalue, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(2, 2, 2)
                .addComponent(lblalternativeslot)
                .addGap(16, 16, 16)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblappointmentdate1)
                    .addComponent(txtappointmentdate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbappointmentime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblappointmenttime1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblstatus1)
                    .addComponent(lblstatusvalue))
                .addGap(18, 18, 18)
                .addComponent(btnselectthisslot)
                .addContainerGap(46, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btncancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btncancelActionPerformed
    slotSelected = false;

    dispose();
    }//GEN-LAST:event_btncancelActionPerformed

    private void btnselectthisslotActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnselectthisslotActionPerformed
          LocalDate date = getAlternativeDate();

    LocalTime time = getAlternativeTime();

    if (date == null || time == null) {
        return;
    }

    boolean available = parentFrame.isDoctorSlotAvailable(
            selectedDoctor,
            date,
            time
    );

    if (!available) {

        lblstatusvalue.setText("Unavailable");

        btnselectthisslot.setEnabled(false);

        JOptionPane.showMessageDialog(
                this,
                "This slot is no longer available. Please select another slot.",
                "Slot Unavailable",
                JOptionPane.WARNING_MESSAGE
        );

        return;
    }

    parentFrame.useAlternativeSlot(date, time);

    slotSelected = true;

    dispose();
    }//GEN-LAST:event_btnselectthisslotActionPerformed

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

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                BookingDialogUnavailable dialog;
                dialog = new BookingDialogUnavailable(new BookAppointmentStaffFrame(null), true,patient1,doctor1,date1,time1);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btncancel;
    private javax.swing.JButton btnselectthisslot;
    private javax.swing.JComboBox<String> cmbappointmentime;
    private javax.swing.JLabel lblalternativeslot;
    private javax.swing.JLabel lblappointmentdate;
    private javax.swing.JLabel lblappointmentdate1;
    private javax.swing.JLabel lblappointmentdatevalue;
    private javax.swing.JLabel lblappointmenttime;
    private javax.swing.JLabel lblappointmenttime1;
    private javax.swing.JLabel lblappointmenttimevalue;
    private javax.swing.JLabel lbldoctorinformation;
    private javax.swing.JLabel lbldoctorname;
    private javax.swing.JLabel lbldoctornamevalue;
    private javax.swing.JLabel lblpatientid;
    private javax.swing.JLabel lblpatientidvalue;
    private javax.swing.JLabel lblpatientinformation;
    private javax.swing.JLabel lblpatientname;
    private javax.swing.JLabel lblpatientnamevalue;
    private javax.swing.JLabel lblselectedappointment;
    private javax.swing.JLabel lblspecialization;
    private javax.swing.JLabel lblspecializationvalue;
    private javax.swing.JLabel lblstatus;
    private javax.swing.JLabel lblstatus1;
    private javax.swing.JLabel lblstatusvalue;
    private javax.swing.JLabel lblsubtitle;
    private javax.swing.JLabel lbltitle;
    private javax.swing.JLabel lbstatusvalue;
    private javax.swing.JTextField txtappointmentdate;
    // End of variables declaration//GEN-END:variables
}
