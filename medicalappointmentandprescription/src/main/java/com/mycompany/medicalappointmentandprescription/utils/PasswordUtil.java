/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.medicalappointmentandprescription.utils;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {

    private PasswordUtil() {
    }

    public static String hashPassword(String password) {

        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Password cannot be empty."
            );
        }

        return BCrypt.hashpw(password, BCrypt.gensalt(12));
    }

    public static boolean verifyPassword(
            String password, String hashedPassword) {

        if (password == null || hashedPassword == null
                || password.isEmpty() || hashedPassword.isEmpty()) {
            return false;
        }

        try {
            return BCrypt.checkpw(password, hashedPassword);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
