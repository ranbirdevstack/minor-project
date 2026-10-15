/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.medicalappointmentandprescription.models;
import java.time.LocalDateTime;
/**
 *
 * @author ranbi
 */
public class MedicineReminder {
   private int reminderId,patientId,prescriptionId;
   private String medicineName,dosage,status;
   private LocalDateTime scheduledAt,snoozeUntil;
   public MedicineReminder()
   {
       
   }
   public MedicineReminder(int reminderId,int patientId,int prescriptionId,String medicineName,String dosage,LocalDateTime scheduledAt,LocalDateTime snoozeUntil,String status )
   {
     this.reminderId=reminderId;
     this.patientId=patientId;
     this.prescriptionId=prescriptionId;
     this.medicineName=medicineName;
     this.dosage=dosage;
     this.scheduledAt=scheduledAt;
     this.snoozeUntil=snoozeUntil;
     this.status=status;
   }
    public int getReminderId()
    {
        return reminderId;
    }
    public void setReminderId(int reminderId)
    {
        this.reminderId=reminderId;
    }
    public int getPatientId()
    {
        return patientId;
    }
    public void setPatientId(int patientId)
    {
        this.patientId=patientId;
    }
    public int getPrecriptionId()
    {
        return prescriptionId;
    }
    public void setPrescriptionId(int prescriptionId)
    {
        this.prescriptionId=prescriptionId;
    }
    public String getMedicineName()
    {
        return medicineName;
    }
    public void setMedicineName(String medicineName)
    {
        this.medicineName=medicineName;
    }
    public String getDosage()
    {
        return dosage;
    }
    public void setDosage(String dosage)
    {
        this.dosage=dosage;
    }
    public LocalDateTime getScheduledAt()
    {
        return scheduledAt;
    }
    public void setScheduledAt(LocalDateTime scheduledAt)
    {
        this.scheduledAt=scheduledAt;
    }
    public LocalDateTime getSnoozeUntil()
    {
        return snoozeUntil;
    }
    public void setSnoozeUntil(LocalDateTime snoozeUntil)
    {
        this.snoozeUntil=snoozeUntil;
    }
    public String getStatus()
    {
        return status;
    }
    public void setStatus(String status)
    {
        this.status=status;
    }
    
}
