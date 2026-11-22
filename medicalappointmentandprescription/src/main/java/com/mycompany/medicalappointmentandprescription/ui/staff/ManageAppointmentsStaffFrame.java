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

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableCellEditor;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.time.LocalDate;

import javax.swing.table.DefaultTableModel;
/**
 *
 * @author ranbi
 */
public class ManageAppointmentsStaffFrame extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(ManageAppointmentsStaffFrame.class.getName());
    private final AppointmentDao ad = new AppointmentDao();
    private final PatientDao pd = new PatientDao();
    private final DoctorDao dd = new DoctorDao();
    private AppointmentDashboardStaffFrame parentFrame;
   private final DateTimeFormatter dateFormatter =DateTimeFormatter.ofPattern("dd-MM-yyyy");

   private final DateTimeFormatter timeFormatter =DateTimeFormatter.ofPattern("hh:mm a");
    /**
     * Creates new form ManageAppointmentsStaffFrame
     */
    public ManageAppointmentsStaffFrame(AppointmentDashboardStaffFrame parent) {
        initComponents();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
           this.parentFrame = parent;

       setAppointmentTableModel();
       setupAppointmentFilters();
       applyAppointmentFilters();
        setupActionColumn();

      loadAppointmentTable();
    }
    private void setAppointmentTableModel() {

    DefaultTableModel model = new DefaultTableModel(
        new Object[][]{},
        new String[]{
            "Appointment ID",
            "Patient Name",
            "Patient ID",
            "Date",
            "Doctor Name",
            "Time",
            "Status",
            "Action"
        }
    ) {

        @Override
        public boolean isCellEditable(int row, int column) {

            return column == 7;
        }
    };

    tblappointmentlist.setModel(model);

    tblappointmentlist.setRowHeight(32);
    tblappointmentlist.setSelectionMode(
            ListSelectionModel.SINGLE_SELECTION
    );
}
    private void setupAppointmentFilters() {

    cmbdate.setModel(new DefaultComboBoxModel<>(
        new String[]{
            "All Dates",
            "Today",
            "Tomorrow",
            "Next 7 Days",
            "Next 30 Days"
        }
    ));

    cmbstatus.setModel(new DefaultComboBoxModel<>(
        new String[]{
            "All Status",
            "Pending",
            "Confirmed",
            "Completed",
            "Cancelled"
        }
    ));

    cmbdoctors.removeAllItems();

    cmbdoctors.addItem("All Doctors");

    List<Doctor> doctors = dd.findAll();

    for (Doctor doctor : doctors) {

        cmbdoctors.addItem(doctor.getDoctorName());
    }

    cmbdate.addActionListener(e -> applyAppointmentFilters());

    cmbdoctors.addActionListener(e -> applyAppointmentFilters());

    cmbstatus.addActionListener(e -> applyAppointmentFilters());
}
    private void applyAppointmentFilters() {

    DefaultTableModel model =
            (DefaultTableModel) tblappointmentlist.getModel();

    model.setRowCount(0);

    List<Appointment> appointments = ad.findAll();

    String selectedDate = cmbdate.getSelectedItem() != null? cmbdate.getSelectedItem().toString(): "All Dates";

    String selectedDoctor = cmbdoctors.getSelectedItem() != null? cmbdoctors.getSelectedItem().toString(): "All Doctors";

    String selectedStatus = cmbstatus.getSelectedItem() != null
            ? cmbstatus.getSelectedItem().toString()
            : "All Status";

    LocalDate today = LocalDate.now();

    for (Appointment appointment : appointments) {

        Patient patient = pd.findByPatientId(
                appointment.getPatientId()
        );

        Doctor doctor = dd.findByDoctorId(
                appointment.getDoctorId()
        );

        if (patient == null || doctor == null) {
            continue;
        }


        LocalDate appointmentDate =
                appointment.getAppointmentDate();

        boolean dateMatches = true;

        switch (selectedDate) {

            case "Today":

                dateMatches = appointmentDate.equals(today);

                break;

            case "Tomorrow":

                dateMatches = appointmentDate.equals(today.plusDays(1));

                break;

            case "Next 7 Days":

                dateMatches =
                        !appointmentDate.isBefore(today)
                        && !appointmentDate.isAfter(today.plusDays(7));

                break;

            case "Next 30 Days":

                dateMatches =
                        !appointmentDate.isBefore(today)
                        && !appointmentDate.isAfter(today.plusDays(30));

                break;
        }

        if (!dateMatches) {
            continue;
        }


        if (!"All Doctors".equalsIgnoreCase(selectedDoctor)
                && !doctor.getDoctorName().equalsIgnoreCase(selectedDoctor)) {

            continue;
        }

        if (!"All Status".equalsIgnoreCase(selectedStatus)
                && !appointment.getStatus().equalsIgnoreCase(selectedStatus)) {

            continue;
        }


        model.addRow(new Object[]{

            appointment.getAppointmentId(),

            patient.getFullName(),

            patient.getPatientId(),

            appointment.getAppointmentDate(),

            doctor.getDoctorName(),

            appointment.getAppointmentTime(),

            appointment.getStatus(),

            "View Details"

        });

    }
}
    public void loadAppointmentTable() {

    DefaultTableModel model =
            (DefaultTableModel) tblappointmentlist.getModel();

    model.setRowCount(0);

    List<Appointment> appointments = ad.findAll();

    for (Appointment appointment : appointments) {

        Patient patient = pd.findByPatientId(
                appointment.getPatientId()
        );

        Doctor doctor = dd.findByDoctorId(
                appointment.getDoctorId()
        );

        if (patient == null || doctor == null) {
            continue;
        }

        model.addRow(new Object[]{

            appointment.getAppointmentId(),

            patient.getFullName(),

            patient.getPatientId(),

            appointment.getAppointmentDate(),

            doctor.getDoctorName(),

            appointment.getAppointmentTime(),

            appointment.getStatus(),

            "View Details"

        });
    }
}
    private void setupActionColumn() {

    tblappointmentlist.getColumnModel()
            .getColumn(7)
            .setCellRenderer(new ViewDetailsButtonRenderer());

    tblappointmentlist.getColumnModel()
            .getColumn(7)
            .setCellEditor(new ViewDetailsButtonEditor());
}

    /*
    
    **
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lbltitle = new javax.swing.JLabel();
        lblsubtitle = new javax.swing.JLabel();
        btnback = new javax.swing.JButton();
        cmbdoctors = new javax.swing.JComboBox<>();
        lblsearch = new javax.swing.JLabel();
        txtsearch = new javax.swing.JTextField();
        btnsearch = new javax.swing.JButton();
        lblappointmentmanagement = new javax.swing.JLabel();
        lbldate = new javax.swing.JLabel();
        cmbdate = new javax.swing.JComboBox<>();
        lbldoctor = new javax.swing.JLabel();
        lblstatus = new javax.swing.JLabel();
        cmbstatus = new javax.swing.JComboBox<>();
        btnclearfilter = new javax.swing.JButton();
        btnrefresh = new javax.swing.JButton();
        lblappointmentlist = new javax.swing.JLabel();
        spappointmentlist = new javax.swing.JScrollPane();
        tblappointmentlist = new javax.swing.JTable();
        btnbookappointment = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        lbltitle.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        lbltitle.setText("Manage Appointment");

        lblsubtitle.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        lblsubtitle.setText(" Manage patient appointments   ");

        btnback.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        btnback.setText("Back");
        btnback.addActionListener(this::btnbackActionPerformed);

        cmbdoctors.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        cmbdoctors.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All Doctor" }));

        lblsearch.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        lblsearch.setText("Search");

        txtsearch.addActionListener(this::txtsearchActionPerformed);

        btnsearch.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        btnsearch.setText("Search");
        btnsearch.addActionListener(this::btnsearchActionPerformed);

        lblappointmentmanagement.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        lblappointmentmanagement.setText("Appointment Management");

        lbldate.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        lbldate.setText("Date :");

        cmbdate.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        cmbdate.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All Dates", "Today", "Tomorrow", "Next 7 Days ", "Next 30 Days" }));

        lbldoctor.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        lbldoctor.setText("Doctor's name:");

        lblstatus.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        lblstatus.setText("Status:");

        cmbstatus.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        cmbstatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "Pending", "Confirmed", "Completed", "Cancelled" }));

        btnclearfilter.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        btnclearfilter.setText("Clear Filter");
        btnclearfilter.addActionListener(this::btnclearfilterActionPerformed);

        btnrefresh.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        btnrefresh.setText("↻ Refresh");
        btnrefresh.addActionListener(this::btnrefreshActionPerformed);

        lblappointmentlist.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        lblappointmentlist.setText("Appointment List");

        tblappointmentlist.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        tblappointmentlist.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        tblappointmentlist.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Id", "Patient Name ", "Patient Id", "Doctor Name", "Status", "Time", "Action"
            }
        ));
        spappointmentlist.setViewportView(tblappointmentlist);

        btnbookappointment.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        btnbookappointment.setText("Book Appointment");
        btnbookappointment.addActionListener(this::btnbookappointmentActionPerformed);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblappointmentlist)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lbldate)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cmbdate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(lbldoctor)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(cmbdoctors, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(lblstatus)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cmbstatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(lblsearch)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(txtsearch, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(btnsearch))
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                    .addGap(243, 243, 243)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(lblsubtitle)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                            .addComponent(lbltitle)
                                            .addGap(38, 38, 38))))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnclearfilter)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnrefresh)
                            .addComponent(btnback)))
                    .addComponent(lblappointmentmanagement))
                .addContainerGap(29, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(btnbookappointment)
                .addGap(73, 73, 73))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(spappointmentlist)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbltitle)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblsubtitle)
                    .addComponent(btnback))
                .addGap(8, 8, 8)
                .addComponent(lblappointmentmanagement)
                .addGap(4, 4, 4)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtsearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblsearch)
                    .addComponent(btnsearch))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbldate)
                    .addComponent(lblstatus)
                    .addComponent(cmbstatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbdoctors, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbldoctor)
                    .addComponent(cmbdate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnclearfilter)
                    .addComponent(btnrefresh))
                .addGap(18, 18, 18)
                .addComponent(lblappointmentlist)
                .addGap(18, 18, 18)
                .addComponent(spappointmentlist, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(67, 67, 67)
                .addComponent(btnbookappointment)
                .addContainerGap(92, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnbackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnbackActionPerformed
        parentFrame.setVisible(true);
        dispose();
    }//GEN-LAST:event_btnbackActionPerformed

    private void txtsearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtsearchActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtsearchActionPerformed

    private void btnsearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnsearchActionPerformed
          String key = txtsearch.getText().trim().toLowerCase();

    if (key.isEmpty()) {
        JOptionPane.showMessageDialog(
                this,
                "Please enter Appointment ID, Patient ID or Patient Name.",
                "Search Appointment",
                JOptionPane.WARNING_MESSAGE
        );
        return;
    }

    DefaultTableModel tableModel =
            (DefaultTableModel) tblappointmentlist.getModel();

    tableModel.setRowCount(0);

    for (Appointment a : ad.findAll()) {

        Patient p = pd.findByPatientId(a.getPatientId());

        Doctor d = dd.findByDoctorId(a.getDoctorId());

        if (p == null || d == null) {
            continue;
        }

        boolean matchAppointmentId =
                String.valueOf(a.getAppointmentId()).contains(key);

        boolean matchPatientId =
                String.valueOf(p.getPatientId()).contains(key);

        boolean matchPatientName =
                p.getFullName().toLowerCase().contains(key);

        if (matchAppointmentId || matchPatientId || matchPatientName) {

            tableModel.addRow(new Object[]{
                a.getAppointmentId(),
                p.getFullName(),
                p.getPatientId(),
                a.getAppointmentDate(),
                d.getDoctorName(),
                a.getAppointmentTime(),
                a.getStatus(),
                "View Details"
            });
        }
    }

    if (tableModel.getRowCount() == 0) {

        JOptionPane.showMessageDialog(
                this,
                "No Appointment Found.",
                "Search Result",
                JOptionPane.INFORMATION_MESSAGE
        );
    }
    }//GEN-LAST:event_btnsearchActionPerformed

    private void btnclearfilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnclearfilterActionPerformed
      txtsearch.setText("");

     cmbdate.setSelectedIndex(0);
     cmbdoctors.setSelectedIndex(0);
     cmbstatus.setSelectedIndex(0);

     loadAppointmentTable();
    }//GEN-LAST:event_btnclearfilterActionPerformed

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

                loadAppointmentTable();

            } finally {

                btnrefresh.setText("↻ Refresh");
                btnrefresh.setEnabled(true);

            }
        }
    };

    worker.execute();

    }//GEN-LAST:event_btnrefreshActionPerformed

    private void btnbookappointmentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnbookappointmentActionPerformed
        BookAppointmentStaffFrame frame =new BookAppointmentStaffFrame(this);

        frame.setLocationRelativeTo(this);
        frame.setVisible(true);

    }//GEN-LAST:event_btnbookappointmentActionPerformed

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
        java.awt.EventQueue.invokeLater(() -> new ManageAppointmentsStaffFrame(new  AppointmentDashboardStaffFrame (2)).setVisible(true));
    }
    private class ViewDetailsButtonRenderer extends JButton
        implements TableCellRenderer {

    public ViewDetailsButtonRenderer() {
        setText("View Details");
        setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(
            JTable table,
            Object value,
            boolean isSelected,
            boolean hasFocus,
            int row,
            int column) {

        setText(value == null ? "View Details" : value.toString());

        return this;
    }
}
    
    private class ViewDetailsButtonEditor extends AbstractCellEditor
        implements TableCellEditor {

    private final JButton button;
    private int appointmentId;

    public ViewDetailsButtonEditor() {

        button = new JButton("View Details");

        button.addActionListener(e -> {
            fireEditingStopped();
           AppointmentDetailsStaffFrame frame =new AppointmentDetailsStaffFrame(appointmentId,  ManageAppointmentsStaffFrame.this);

           frame.setLocationRelativeTo(null);
           frame.setVisible(true);
        });
    }

    @Override
    public Component getTableCellEditorComponent(
            JTable table,
            Object value,
            boolean isSelected,
            int row,
            int column) {

        int modelRow = table.convertRowIndexToModel(row);

        appointmentId = Integer.parseInt(
                table.getModel().getValueAt(modelRow, 0).toString()
        );

        return button;
    }

    @Override
    public Object getCellEditorValue() {
        return "View Details";
    }
}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnback;
    private javax.swing.JButton btnbookappointment;
    private javax.swing.JButton btnclearfilter;
    private javax.swing.JButton btnrefresh;
    private javax.swing.JButton btnsearch;
    private javax.swing.JComboBox<String> cmbdate;
    private javax.swing.JComboBox<String> cmbdoctors;
    private javax.swing.JComboBox<String> cmbstatus;
    private javax.swing.JLabel lblappointmentlist;
    private javax.swing.JLabel lblappointmentmanagement;
    private javax.swing.JLabel lbldate;
    private javax.swing.JLabel lbldoctor;
    private javax.swing.JLabel lblsearch;
    private javax.swing.JLabel lblstatus;
    private javax.swing.JLabel lblsubtitle;
    private javax.swing.JLabel lbltitle;
    private javax.swing.JScrollPane spappointmentlist;
    private javax.swing.JTable tblappointmentlist;
    private javax.swing.JTextField txtsearch;
    // End of variables declaration//GEN-END:variables
}
