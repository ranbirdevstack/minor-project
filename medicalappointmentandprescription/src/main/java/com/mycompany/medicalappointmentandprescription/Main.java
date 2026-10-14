/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.medicalappointmentandprescription;
import com.mycompany.medicalappointmentandprescription.ui.auth.LoginFrame;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 *
 * @author ranbi
 */
public class Main {

    public static void main(String[] args) {
       SwingUtilities.invokeLater(()->{
        LoginFrame lo=new LoginFrame();
        lo.setLocationRelativeTo(null);
        lo.setVisible(true);
    });
    }
}
