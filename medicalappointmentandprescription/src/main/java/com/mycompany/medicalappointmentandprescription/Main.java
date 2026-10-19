/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.medicalappointmentandprescription;
import com.mycompany.medicalappointmentandprescription.ui.auth.LoginFrame;
import javax.swing.SwingUtilities;
/**
 *
 * @author ranbi
 */
public class Main {
    private static MedicineReminderMonitor reminder;
    public static void main(String[] args) {
        reminder=new MedicineReminderMonitor();
       SwingUtilities.invokeLater(()->{
        LoginFrame lo=new LoginFrame();
        lo.setLocationRelativeTo(null);
        lo.setVisible(true);
    });
    }
    public static MedicineReminderMonitor getReminderMonitor()
    {
        return  reminder; 
    }
}
