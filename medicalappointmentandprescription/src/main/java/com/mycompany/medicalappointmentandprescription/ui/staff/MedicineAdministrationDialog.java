/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package com.mycompany.medicalappointmentandprescription.ui.staff;
import com.mycompany.medicalappointmentandprescription.dao.MedicineReminderDao;
import com.mycompany.medicalappointmentandprescription.dao.MedicineDoseHistoryDao;
import com.mycompany.medicalappointmentandprescription.dao.PatientDao;

import com.mycompany.medicalappointmentandprescription.models.MedicineReminder;
import com.mycompany.medicalappointmentandprescription.models.MedicineDoseHistory;
import com.mycompany.medicalappointmentandprescription.models.Patient;

import java.time.format.DateTimeFormatter;
import javax.swing.JOptionPane;
/**
 *
 * @author ranbi
 */
public class MedicineAdministrationDialog extends javax.swing.JDialog {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(MedicineAdministrationDialog.class.getName());
    private final MedicineReminderDao mrd = new MedicineReminderDao();
    private final MedicineDoseHistoryDao mdhd =new MedicineDoseHistoryDao(mrd);
    private final PatientDao pd = new PatientDao();

    private static final DateTimeFormatter FORMATTER =DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm a");

    private int reminderId;
    private int historyId;
    private boolean viewOnly;
    /**
     * Creates new form MedicineAdministrationDialog
     */
    public MedicineAdministrationDialog(java.awt.Frame parent, boolean modal,int reminderId) {
        super(parent, modal);
        initComponents();
        this.reminderId = reminderId;
        this.viewOnly = false;

       setTitle("Medicine Administration");
       setLocationRelativeTo(parent);

       loadReminderDetails();
    }
   public MedicineAdministrationDialog(java.awt.Frame parent,int historyId,  boolean viewOnly) {

    initComponents();
    this.historyId = historyId;
    this.viewOnly = viewOnly;

    setTitle("Medicine Dose History");
    setLocationRelativeTo(parent);

    loadHistoryDetails();
    

}
   private void loadReminderDetails() {

    MedicineReminder reminder =
            mrd.findByReminderId(reminderId);

    if (reminder == null) {
        JOptionPane.showMessageDialog(
                this,
                "Medicine reminder not found."
        );
        dispose();
        return;
    }

    Patient patient =
            pd.findByPatientId(reminder.getPatientId());

    if (patient == null) {
        JOptionPane.showMessageDialog(
                this,
                "Patient not found."
        );
        dispose();
        return;
    }

    lbltitle.setText("Medicine Administration");

    lblpatientidvalue.setText(
            String.valueOf(patient.getPatientId()));

    lblpatientnamevalue.setText(
            patient.getFullName());

    lblreminderidvalue.setText(
            String.valueOf(reminder.getReminderId()));

    lblmedicineinformation.setText("Medicine Information");

    lbldosagenamevalue.setText(
            reminder.getMedicineName());

    lbldosagevalue.setText(
            reminder.getDosage());

    lblscheduleddatevalue.setText(
            reminder.getScheduledAt().format(FORMATTER));

    lblactiondate.setVisible(false);
    lblactiondatevalue.setVisible(false);

    String status = reminder.getStatus();

    if ("Snoozed".equalsIgnoreCase(status)
            && reminder.getSnoozeUntil() != null) {
        status = reminder.getSnoozeUntil().isAfter(
                java.time.LocalDateTime.now())
                ? "Upcoming" : "Due";
    } else if ("Pending".equalsIgnoreCase(status)) {
        status = reminder.getScheduledAt().isAfter(
                java.time.LocalDateTime.now())
                ? "Upcoming" : "Due";
    }

    lblstatusvalue.setText(status);

    lbladminstrator.setVisible(true);
    btngiven.setVisible(true);
    btnskipped.setVisible(true);

    if (!"Due".equalsIgnoreCase(status)) {
        btngiven.setEnabled(false);
        btnskipped.setEnabled(false);
    }
}
   private void loadHistoryDetails() {

    MedicineDoseHistory history =
            mdhd.findByHistoryId(historyId);

    if (history == null) {
        JOptionPane.showMessageDialog(
                this,
                "Medicine dose history not found."
        );
        dispose();
        return;
    }

    Patient patient =
            pd.findByPatientId(history.getPatientId());

    if (patient == null) {
        JOptionPane.showMessageDialog(
                this,
                "Patient not found."
        );
        dispose();
        return;
    }

    lbltitle.setText("Medicine Dose History");

    lblpatientidvalue.setText(
            String.valueOf(patient.getPatientId()));

    lblpatientnamevalue.setText(
            patient.getFullName());

    lblreminderidvalue.setText(
            String.valueOf(history.getReminderId()));

    lbldosagenamevalue.setText(
            history.getMedicineName());

    lbldosagevalue.setText(
            history.getDosage());

    lblscheduleddatevalue.setText(
            history.getScheduledAt().format(FORMATTER));

    lblactiondatevalue.setText(
            history.getGivenAt().format(FORMATTER));

    lblstatusvalue.setText(history.getStatus());

    lbladminstrator.setVisible(false);
    btngiven.setVisible(false);
    btnskipped.setVisible(false);

    lblactiondate.setVisible(true);
    lblactiondatevalue.setVisible(true);
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
        lblpatientinformation = new javax.swing.JLabel();
        lblpatientid = new javax.swing.JLabel();
        lblpatientidvalue = new javax.swing.JLabel();
        lblpatientname = new javax.swing.JLabel();
        lblpatientnamevalue = new javax.swing.JLabel();
        lblmedicineinformation = new javax.swing.JLabel();
        lblreminderid = new javax.swing.JLabel();
        lblreminderidvalue = new javax.swing.JLabel();
        lbldosagename = new javax.swing.JLabel();
        lbldosagenamevalue = new javax.swing.JLabel();
        lblscheduleddate = new javax.swing.JLabel();
        lblscheduleddatevalue = new javax.swing.JLabel();
        lblactiondate = new javax.swing.JLabel();
        lblactiondatevalue = new javax.swing.JLabel();
        lblstatus = new javax.swing.JLabel();
        lblstatusvalue = new javax.swing.JLabel();
        lbladminstrator = new javax.swing.JLabel();
        btnback = new javax.swing.JButton();
        btngiven = new javax.swing.JButton();
        btnskipped = new javax.swing.JButton();
        lbldosage = new javax.swing.JLabel();
        lbldosagevalue = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        lbltitle.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        lbltitle.setText("Medicine Dose History");

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

        lblmedicineinformation.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        lblmedicineinformation.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblmedicineinformation.setText("Medicine Information");

        lblreminderid.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        lblreminderid.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblreminderid.setText("Reminder Id");

        lblreminderidvalue.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        lblreminderidvalue.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblreminderidvalue.setText("Reminder Id");

        lbldosagename.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        lbldosagename.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbldosagename.setText("Dosage Name");

        lbldosagenamevalue.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        lbldosagenamevalue.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbldosagenamevalue.setText("Dosage Name");

        lblscheduleddate.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        lblscheduleddate.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblscheduleddate.setText("Scheduled Date");

        lblscheduleddatevalue.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        lblscheduleddatevalue.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblscheduleddatevalue.setText("-");

        lblactiondate.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        lblactiondate.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblactiondate.setText("Action Date");

        lblactiondatevalue.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        lblactiondatevalue.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblactiondatevalue.setText("-");

        lblstatus.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        lblstatus.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblstatus.setText("Status");

        lblstatusvalue.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        lblstatusvalue.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblstatusvalue.setText("-");

        lbladminstrator.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        lbladminstrator.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbladminstrator.setText("Adminstrator");

        btnback.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        btnback.setText("Back");
        btnback.addActionListener(this::btnbackActionPerformed);

        btngiven.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        btngiven.setText("Mark as Given");
        btngiven.addActionListener(this::btngivenActionPerformed);

        btnskipped.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        btnskipped.setText("Mark as Skipped");
        btnskipped.addActionListener(this::btnskippedActionPerformed);

        lbldosage.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        lbldosage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbldosage.setText("Dosage ");

        lbldosagevalue.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        lbldosagevalue.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbldosagevalue.setText("Dosage ");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lbltitle)
                .addGap(278, 278, 278))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(34, 34, 34)
                        .addComponent(lblpatientid))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblreminderid)
                            .addComponent(lblactiondate)
                            .addComponent(lbldosage))
                        .addGap(60, 60, 60)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lbldosagevalue)
                            .addComponent(lblactiondatevalue)
                            .addComponent(lblpatientidvalue)
                            .addComponent(lblreminderidvalue)
                            .addComponent(btngiven))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 159, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblpatientname)
                        .addGap(57, 57, 57)
                        .addComponent(lblpatientnamevalue))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(122, 122, 122)
                        .addComponent(btnback))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(lbldosagename)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(btnskipped)
                                    .addGap(7, 7, 7)))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(54, 54, 54)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblstatus)
                                    .addComponent(lblscheduleddate))))
                        .addGap(22, 22, 22)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblstatusvalue)
                            .addComponent(lbldosagenamevalue)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(lblscheduleddatevalue)))))
                .addGap(66, 66, 66))
            .addGroup(layout.createSequentialGroup()
                .addGap(295, 295, 295)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblmedicineinformation)
                    .addComponent(lblpatientinformation)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(lbladminstrator)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbltitle)
                    .addComponent(btnback))
                .addGap(18, 18, 18)
                .addComponent(lblpatientinformation, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblpatientid, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblpatientidvalue, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblpatientname, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblpatientnamevalue, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(37, 37, 37)
                .addComponent(lblmedicineinformation, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblreminderid, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblreminderidvalue, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbldosagename, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbldosagenamevalue, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblscheduleddate, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblscheduleddatevalue, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lbldosage, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lbldosagevalue, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblstatus, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblstatusvalue, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblactiondate, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblactiondatevalue, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(28, 28, 28)
                .addComponent(lbladminstrator, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(35, 35, 35)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btngiven)
                    .addComponent(btnskipped))
                .addContainerGap(192, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnbackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnbackActionPerformed
           dispose();
    }//GEN-LAST:event_btnbackActionPerformed

    private void btngivenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btngivenActionPerformed
       int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure the medicine was given?",
            "Confirm Medicine Administration",
            JOptionPane.YES_NO_OPTION
    );

    if (confirm != JOptionPane.YES_OPTION) {
        return;
    }

    if (mrd.completedReminder(reminderId)) {

        JOptionPane.showMessageDialog(
                this,
                "Medicine marked as Given successfully."
        );

        dispose();

    } else {

        JOptionPane.showMessageDialog(
                this,
                "Unable to update medicine status."
        );
    }
    }//GEN-LAST:event_btngivenActionPerformed

    private void btnskippedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnskippedActionPerformed
         int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure the medicine was skipped?",
            "Confirm Medicine Status",
            JOptionPane.YES_NO_OPTION
    );

    if (confirm != JOptionPane.YES_OPTION) {
        return;
    }

    if (mrd.skippedReminder(reminderId)) {

        JOptionPane.showMessageDialog(
                this,
                "Medicine marked as Skipped successfully."
        );

        dispose();

    } else {

        JOptionPane.showMessageDialog(
                this,
                "Unable to update medicine status."
        );
    }
    }//GEN-LAST:event_btnskippedActionPerformed

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
                MedicineAdministrationDialog dialog = new MedicineAdministrationDialog(new javax.swing.JFrame(), true,1);
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
    private javax.swing.JButton btnback;
    private javax.swing.JButton btngiven;
    private javax.swing.JButton btnskipped;
    private javax.swing.JLabel lblactiondate;
    private javax.swing.JLabel lblactiondatevalue;
    private javax.swing.JLabel lbladminstrator;
    private javax.swing.JLabel lbldosage;
    private javax.swing.JLabel lbldosagename;
    private javax.swing.JLabel lbldosagenamevalue;
    private javax.swing.JLabel lbldosagevalue;
    private javax.swing.JLabel lblmedicineinformation;
    private javax.swing.JLabel lblpatientid;
    private javax.swing.JLabel lblpatientidvalue;
    private javax.swing.JLabel lblpatientinformation;
    private javax.swing.JLabel lblpatientname;
    private javax.swing.JLabel lblpatientnamevalue;
    private javax.swing.JLabel lblreminderid;
    private javax.swing.JLabel lblreminderidvalue;
    private javax.swing.JLabel lblscheduleddate;
    private javax.swing.JLabel lblscheduleddatevalue;
    private javax.swing.JLabel lblstatus;
    private javax.swing.JLabel lblstatusvalue;
    private javax.swing.JLabel lbltitle;
    // End of variables declaration//GEN-END:variables
}
