package com.mycompany.medicalappointmentandprescription;

import com.mycompany.medicalappointmentandprescription.dao.DBConnection;
import com.mycompany.medicalappointmentandprescription.ui.auth.LoginFrame;

import java.sql.Connection;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class Main {

    private static MedicineReminderMonitor reminder;

    public static void main(String[] args) {

        try (Connection con = DBConnection.getConnection()) {

            System.out.println("Database Connected Successfully!");
            System.out.println("Database: " + con.getCatalog());

        } catch (SQLException e) {

            System.out.println("Database Connection Failed!");
            e.printStackTrace();

            SwingUtilities.invokeLater(() ->
                    JOptionPane.showMessageDialog(
                            null,
                            "Database connection failed!\nCheck db.properties.",
                            "Database Error",
                            JOptionPane.ERROR_MESSAGE
                    )
            );

            return;
        }

     
        reminder = new MedicineReminderMonitor();

        SwingUtilities.invokeLater(() -> {

            LoginFrame lo = new LoginFrame();

            lo.setLocationRelativeTo(null);
            lo.setVisible(true);

        });
    }

    public static MedicineReminderMonitor getReminderMonitor() {
        return reminder;
    }
}