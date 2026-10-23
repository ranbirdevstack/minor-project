package com.mycompany.medicalappointmentandprescription.models;
import java.time.LocalDate;
import java.time.LocalDateTime;
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author ranbi
 */
public class FollowUpRecommendation {
    private int followUpId,patientId,doctorId,appointmentId;
    private LocalDate recommendedDate;
    private String instruction,status;
    private LocalDateTime createdAt;
    public FollowUpRecommendation()
    {
    }
    public FollowUpRecommendation(int followUpId,int appointmentId,int patientId,int doctorId,LocalDate recommendedDate,String instruction,String status,LocalDateTime createdAt)
    {
     this.followUpId=followUpId;
     this.appointmentId=appointmentId;
     this.patientId=patientId;
     this.doctorId=doctorId;
     this.recommendedDate=recommendedDate;
     this.instruction=instruction;
     this.status=status;
     this.createdAt=createdAt;
    }
    public int getFollowUpId()
    {
        return followUpId;
    }
    public void setFollowUpId(int followUpId)
    {
        this.followUpId=followUpId;
    }
     public int getAppointmentId()
    {
        return appointmentId;
    }
    public void setAppointmentId(int appointmentId)
    {
        this.appointmentId=appointmentId;
    }
     public int getPatientId()
    {
        return patientId;
    }
    public void setPatientId(int patientId)
    {
        this.patientId=patientId;
    }
     public int getDoctorId()
    {
        return doctorId;
    }
    public void setDoctorId(int doctorId)
    {
        this.doctorId=doctorId;
    }
    public LocalDate getRecommendedDate()
    {
        return recommendedDate;
    }
    public void setRecommendedDate(LocalDate recommendedDate)
    {
        this.recommendedDate=recommendedDate;
    }
     public String getInstruction()
    {
        return instruction;
    }
    public void setInstruction(String instruction)
    {
        this.instruction=instruction;
    }
     public String getStatus()
    {
        return status;
    }
    public void setStatus(String status)
    {
        this.status=status;
    }
     public LocalDateTime getCreatedAt()
    {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt)
    {
        this.createdAt= createdAt;
    }
}
