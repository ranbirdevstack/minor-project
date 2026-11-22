/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.medicalappointmentandprescription.utils;

import com.mycompany.medicalappointmentandprescription.models.MedicineReminder;
import java.time.LocalDateTime;

public class ReminderScheduler {

    private ReminderScheduler() {
    }

    // Calculate next scheduled time
    public static LocalDateTime calculateNextScheduledTime(
            LocalDateTime baseTime, int intervalHours) {

        if (baseTime == null) {
            throw new IllegalArgumentException(
                    "Base time cannot be null."
            );
        }

        if (intervalHours <= 0) {
            throw new IllegalArgumentException(
                    "Interval hours must be greater than zero."
            );
        }

        return baseTime.plusHours(intervalHours);
    }

    // Schedule next dose after administration action
    public static void scheduleNextDose(
            MedicineReminder reminder, LocalDateTime actionTime) {

        if (reminder == null) {
            throw new IllegalArgumentException(
                    "Reminder cannot be null."
            );
        }

        if (actionTime == null) {
            throw new IllegalArgumentException(
                    "Action time cannot be null."
            );
        }

        LocalDateTime nextTime =
                calculateNextScheduledTime(
                        actionTime,
                        reminder.getIntervalHours()
                );

        reminder.setScheduledAt(nextTime);
        reminder.setSnoozeUntil(null);
        reminder.setStatus("Pending");
    }
}
