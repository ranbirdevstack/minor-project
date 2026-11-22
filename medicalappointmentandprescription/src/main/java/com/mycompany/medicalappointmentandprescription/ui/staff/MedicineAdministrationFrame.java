/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.medicalappointmentandprescription.ui.staff;
import com.mycompany.medicalappointmentandprescription.dao.MedicineReminderDao;
import com.mycompany.medicalappointmentandprescription.dao.MedicineDoseHistoryDao;
import com.mycompany.medicalappointmentandprescription.dao.PatientDao;

import com.mycompany.medicalappointmentandprescription.models.MedicineReminder;
import com.mycompany.medicalappointmentandprescription.models.MedicineDoseHistory;
import com.mycompany.medicalappointmentandprescription.models.Patient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionListener;
 /*
 * @author ranbi
 */
public class MedicineAdministrationFrame extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(MedicineAdministrationFrame.class.getName());
    private final MedicineReminderDao mrd = new MedicineReminderDao();
    private final MedicineDoseHistoryDao mdhd =new MedicineDoseHistoryDao(mrd);
    private final PatientDao pd = new PatientDao();
   private AppointmentDashboardStaffFrame parent;
    private DefaultTableModel tableModel;

    private static final DateTimeFormatter DATE_TIME_FORMATTER =DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm a");
    /**
     * Creates new form MedicineAdministrationFrame
     */
    public MedicineAdministrationFrame(AppointmentDashboardStaffFrame parent) {
        initComponents();
       setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
       this.parent=parent;
       setLocationRelativeTo(null);

       initializeTable();
      loadMedicineReminderTable(); 
    }
    private void initializeTable() {

    tableModel = new DefaultTableModel(
            new String[]{
                "Reminder ID",
                "Patient ID",
                "Patient Name",
                "Medicine",
                "Dosage",
                "Scheduled Time",
                "Dose Status",
                "Action"
            }, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return column == 7;
        }
    };

    tblmedicinerminder.setModel(tableModel);

    tblmedicinerminder.setRowHeight(25);

    tblmedicinerminder.getColumnModel()
            .getColumn(7)
            .setCellRenderer(new ActionButtonRenderer());

    tblmedicinerminder.getColumnModel()
            .getColumn(7)
            .setCellEditor(new ActionButtonEditor());
}
    private void loadMedicineReminderTable() {

    tableModel.setRowCount(0);

    LocalDateTime now = LocalDateTime.now();

    String search = txtsearch.getText().trim().toLowerCase();

    String selectedStatus =
            cmbstatus.getSelectedItem().toString();

    for (MedicineReminder reminder : mrd.findAll()) {

        Patient patient =
                pd.findByPatientId(reminder.getPatientId());

        if (patient == null) {
            continue;
        }

        String patientId =
                String.valueOf(patient.getPatientId());

        String patientName =
                patient.getFullName();

        if (!search.isEmpty()
                && !patientId.contains(search)
                && !patientName.toLowerCase().contains(search)) {
            continue;
        }

        List<MedicineDoseHistory> histories =
                mdhd.findByReminderId(reminder.getReminderId());

        for (MedicineDoseHistory history : histories) {

            String status = history.getStatus();

            if (!selectedStatus.equals("All")
                    && !selectedStatus.equalsIgnoreCase(status)) {
                continue;
            }

            tableModel.addRow(new Object[]{
                reminder.getReminderId(),
                patientId,
                patientName,
                history.getMedicineName(),
                history.getDosage(),
                history.getScheduledAt().format(DATE_TIME_FORMATTER),
                status,
                "View"
            });
        }

        boolean historyExists = false;

        for (MedicineDoseHistory history : histories) {

            if (history.getScheduledAt() != null
                    && history.getScheduledAt().equals(
                            reminder.getScheduledAt())) {

                historyExists = true;
                break;
            }
        }

        if (!historyExists) {

            LocalDateTime displayTime =
                    reminder.getScheduledAt();

            if ("Snoozed".equalsIgnoreCase(reminder.getStatus())
                    && reminder.getSnoozeUntil() != null) {

                displayTime = reminder.getSnoozeUntil();
            }

            String doseStatus =
                    displayTime.isAfter(now) ? "Upcoming" : "Due";

            if (!"Pending".equalsIgnoreCase(reminder.getStatus())
                    && !"Snoozed".equalsIgnoreCase(reminder.getStatus())) {
                continue;
            }

            if (!selectedStatus.equals("All")
                    && !selectedStatus.equalsIgnoreCase(doseStatus)) {
                continue;
            }

            tableModel.addRow(new Object[]{
                reminder.getReminderId(),
                patientId,
                patientName,
                reminder.getMedicineName(),
                reminder.getDosage(),
                displayTime.format(DATE_TIME_FORMATTER),
                doseStatus,
                doseStatus.equals("Due") ? "Administer" : "View"
            });
        }
    }
}
    private void searchMedicineReminderTable() {

    tableModel.setRowCount(0);

    String search = txtsearch.getText().trim().toLowerCase();

    if (search.isEmpty()) {
        loadMedicineReminderTable();
        return;
    }

    LocalDateTime now = LocalDateTime.now();

    String selectedStatus =
            cmbstatus.getSelectedItem().toString();

    for (MedicineReminder reminder : mrd.findAll()) {

        Patient patient =
                pd.findByPatientId(reminder.getPatientId());

        if (patient == null) {
            continue;
        }

        String patientId =
                String.valueOf(patient.getPatientId());

        String patientName =
                patient.getFullName();

        if (!patientId.contains(search)
                && !patientName.toLowerCase().contains(search)) {
            continue;
        }

        loadMedicineReminderRow(reminder, patient, now, selectedStatus);
    }
}
    private void loadMedicineReminderRow(
        MedicineReminder reminder,
        Patient patient,
        LocalDateTime now,
        String selectedStatus) {

    List<MedicineDoseHistory> histories =
            mdhd.findByReminderId(reminder.getReminderId());

    String patientId =
            String.valueOf(patient.getPatientId());

    String patientName =
            patient.getFullName();

    for (MedicineDoseHistory history : histories) {

        String status = history.getStatus();

        if (!selectedStatus.equals("All")
                && !selectedStatus.equalsIgnoreCase(status)) {
            continue;
        }

        tableModel.addRow(new Object[]{
            reminder.getReminderId(),
            patientId,
            patientName,
            history.getMedicineName(),
            history.getDosage(),
            history.getScheduledAt().format(DATE_TIME_FORMATTER),
            status,
            "View"
        });
    }

    boolean historyExists = false;

    for (MedicineDoseHistory history : histories) {

        if (history.getScheduledAt() != null
                && history.getScheduledAt().equals(
                        reminder.getScheduledAt())) {

            historyExists = true;
            break;
        }
    }

    if (!historyExists) {

        LocalDateTime displayTime = reminder.getScheduledAt();

        if ("Snoozed".equalsIgnoreCase(reminder.getStatus())
                && reminder.getSnoozeUntil() != null) {

            displayTime = reminder.getSnoozeUntil();
        }

        String doseStatus =
                displayTime.isAfter(now) ? "Upcoming" : "Due";

        if (!"Pending".equalsIgnoreCase(reminder.getStatus())
                && !"Snoozed".equalsIgnoreCase(reminder.getStatus())) {
            return;
        }

        if (!selectedStatus.equals("All")
                && !selectedStatus.equalsIgnoreCase(doseStatus)) {
            return;
        }

        tableModel.addRow(new Object[]{
            reminder.getReminderId(),
            patientId,
            patientName,
            reminder.getMedicineName(),
            reminder.getDosage(),
            displayTime.format(DATE_TIME_FORMATTER),
            doseStatus,
            doseStatus.equals("Due") ? "Administer" : "View"
        });
    }
}
    private void refreshMedicineRecords() {

    btnrefresh.setEnabled(false);
    btnrefresh.setText("Refreshing...");

    SwingWorker<Void, Void> worker = new SwingWorker<>() {

        @Override
        protected Void doInBackground() throws Exception {

            Thread.sleep(300);

            return null;
        }

        @Override
        protected void done() {

            loadMedicineReminderTable();

            btnrefresh.setEnabled(true);
            btnrefresh.setText("Refresh");
        }
    };

    worker.execute();
}
    private void openAdministrationDialog(int reminderId) {

    MedicineAdministrationDialog dialog =
            new MedicineAdministrationDialog(this,true, reminderId);

    dialog.setLocationRelativeTo(this);
    dialog.setVisible(true);

    loadMedicineReminderTable();
}
    private void openHistoryDetails(int historyId) {

    MedicineAdministrationDialog dialog =
            new MedicineAdministrationDialog(this, historyId, true);

    dialog.setLocationRelativeTo(this);
    dialog.setVisible(true);
}
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblsubtitle = new javax.swing.JLabel();
        lbltitle = new javax.swing.JLabel();
        lblmedicalremindermanagement = new javax.swing.JLabel();
        lblsearch = new javax.swing.JLabel();
        txtsearch = new javax.swing.JTextField();
        btnrefresh = new javax.swing.JButton();
        btnback = new javax.swing.JButton();
        btnsearch = new javax.swing.JButton();
        lblstatus = new javax.swing.JLabel();
        cmbstatus = new javax.swing.JComboBox<>();
        spmedicinereminder = new javax.swing.JScrollPane();
        tblmedicinerminder = new javax.swing.JTable();
        lblmedicalreminderlist = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        lblsubtitle.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        lblsubtitle.setText("Manage Patient Medicine Doses ");

        lbltitle.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        lbltitle.setText("Medicine Adminstrator");

        lblmedicalremindermanagement.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        lblmedicalremindermanagement.setText("Medicine Reminder Management");

        lblsearch.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        lblsearch.setText("Search");

        txtsearch.addActionListener(this::txtsearchActionPerformed);

        btnrefresh.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        btnrefresh.setText("Refresh");
        btnrefresh.addActionListener(this::btnrefreshActionPerformed);

        btnback.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        btnback.setText("Back");
        btnback.addActionListener(this::btnbackActionPerformed);

        btnsearch.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        btnsearch.setText("Search");
        btnsearch.addActionListener(this::btnsearchActionPerformed);

        lblstatus.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        lblstatus.setText("Status:");

        cmbstatus.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        cmbstatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "Due", "Given", "Skipped" }));
        cmbstatus.addActionListener(this::cmbstatusActionPerformed);

        tblmedicinerminder.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        tblmedicinerminder.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        tblmedicinerminder.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Reminder ID ", "Patient ID ", "Patient Name", "Medicine", "Dosage", "Scheduled Time ", "Dose Status", "Action "
            }
        ));
        spmedicinereminder.setViewportView(tblmedicinerminder);

        lblmedicalreminderlist.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        lblmedicalreminderlist.setText("Medicine Reminder List");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lbltitle)
                .addGap(104, 104, 104)
                .addComponent(btnrefresh)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnback)
                .addGap(47, 47, 47))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(167, 167, 167)
                        .addComponent(lblsearch)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtsearch, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnsearch)
                        .addGap(35, 35, 35)
                        .addComponent(lblstatus)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmbstatus, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addComponent(spmedicinereminder, javax.swing.GroupLayout.PREFERRED_SIZE, 702, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(292, 292, 292)
                        .addComponent(lblsubtitle))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(31, 31, 31)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblmedicalreminderlist)
                            .addComponent(lblmedicalremindermanagement))))
                .addContainerGap(24, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbltitle)
                    .addComponent(btnrefresh)
                    .addComponent(btnback))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblsubtitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblmedicalremindermanagement)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtsearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblsearch)
                    .addComponent(btnsearch)
                    .addComponent(lblstatus)
                    .addComponent(cmbstatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(57, 57, 57)
                .addComponent(lblmedicalreminderlist)
                .addGap(18, 18, 18)
                .addComponent(spmedicinereminder, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(173, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtsearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtsearchActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtsearchActionPerformed

    private void btnbackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnbackActionPerformed
        parent.setVisible(true);
        dispose();
         
    }//GEN-LAST:event_btnbackActionPerformed

    private void btnrefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnrefreshActionPerformed
      refreshMedicineRecords();
    }//GEN-LAST:event_btnrefreshActionPerformed

    private void btnsearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnsearchActionPerformed
         searchMedicineReminderTable();
    }//GEN-LAST:event_btnsearchActionPerformed

    private void cmbstatusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbstatusActionPerformed
        searchMedicineReminderTable();
    }//GEN-LAST:event_cmbstatusActionPerformed

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
        java.awt.EventQueue.invokeLater(() -> new MedicineAdministrationFrame(new  AppointmentDashboardStaffFrame (2)).setVisible(true));
    }
   private class ActionButtonRenderer extends JButton
        implements TableCellRenderer {

    public ActionButtonRenderer() {
        setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(
            JTable table, Object value,
            boolean isSelected, boolean hasFocus,
            int row, int column) {

        setText(value == null ? "" : value.toString());

        return this;
    }
}
   private class ActionButtonEditor extends AbstractCellEditor
        implements TableCellEditor {

    private final JButton button = new JButton();

    private int reminderId;

    private String action;

    public ActionButtonEditor() {

        button.addActionListener(e -> {

            fireEditingStopped();

            if ("Administer".equals(action)) {
                openAdministrationDialog(reminderId);
            } else {
                openHistoryDetails(reminderId);
            }
        });
    }

    @Override
    public Component getTableCellEditorComponent(
            JTable table, Object value,
            boolean isSelected, int row, int column) {

        action = value.toString();

        reminderId = Integer.parseInt(
                table.getValueAt(row, 0).toString()
        );

        button.setText(action);

        return button;
    }

    @Override
    public Object getCellEditorValue() {
        return action;
    }
}
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnback;
    private javax.swing.JButton btnrefresh;
    private javax.swing.JButton btnsearch;
    private javax.swing.JComboBox<String> cmbstatus;
    private javax.swing.JLabel lblmedicalreminderlist;
    private javax.swing.JLabel lblmedicalremindermanagement;
    private javax.swing.JLabel lblsearch;
    private javax.swing.JLabel lblstatus;
    private javax.swing.JLabel lblsubtitle;
    private javax.swing.JLabel lbltitle;
    private javax.swing.JScrollPane spmedicinereminder;
    private javax.swing.JTable tblmedicinerminder;
    private javax.swing.JTextField txtsearch;
    // End of variables declaration//GEN-END:variables
}
