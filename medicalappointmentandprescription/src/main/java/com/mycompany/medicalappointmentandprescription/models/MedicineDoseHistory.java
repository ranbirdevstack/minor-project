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
public class MedicineDoseHistory {
   private int reminderId,patientId,historyId;
   private String medicineName,dosage,status;
   private LocalDateTime scheduledAt,givenAt;
   public MedicineDoseHistory()
   {
       
   }
   public MedicineDoseHistory(int reminderId,int patientId,int historyId,String medicineName,String dosage,LocalDateTime scheduledAt,LocalDateTime givenAt,String status )
   {
     this.reminderId=reminderId;
     this.patientId=patientId;
     this.historyId=historyId;
     this.medicineName=medicineName;
     this.dosage=dosage;
     this.scheduledAt=scheduledAt;
     this.givenAt=givenAt;
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
    public int getHistoryId()
    {
        return historyId;
    }
    public void setHistoryId(int historyId)
    {
        this.historyId=historyId;
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
    
    public LocalDateTime getGivenAt()
    {
        return givenAt;
    }
    public void setGivenAt(LocalDateTime givenAt)
    {
        this.givenAt=givenAt;
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
