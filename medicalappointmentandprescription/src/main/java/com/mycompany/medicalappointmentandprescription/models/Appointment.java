/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.medicalappointmentandprescription.models;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
/**
 *
 * @author ranbi
 */
public class Appointment {
    private int appointmentId,patientId,doctorId;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private String status;
    private LocalDateTime createdAt;
    public Appointment()
    {
    }
    public Appointment(int appointmentId,int patientId,int doctorId,LocalDate appointmentDate,LocalTime appointmentTime,String status,LocalDateTime createdAt)
    {
      this.appointmentId=appointmentId;
      this.doctorId=doctorId;
      this.patientId=patientId;
      this.appointmentDate=appointmentDate;
      this.appointmentTime=appointmentTime;
      this.status=status;
      this.createdAt=createdAt;
    }
    public int getAppointmentId()
    {
        return appointmentId;
    }
    public void setAppointmentId(int appointmentId)
    {
        this.appointmentId= appointmentId;
    }
    public int getDoctorId()
    {
        return doctorId;
    }
    public void setDoctorId(int doctorId)
    {
        this.doctorId= doctorId;
    }
    public int getPatientId()
    {
        return patientId;
    }
    public void setPatientId(int patientId)
    {
        this.patientId= patientId;
    }
    public LocalDate getAppointmentDate()
    {
        return appointmentDate;
    }
    public void setAppointmentDate(LocalDate appointmentDate)
    {
        this.appointmentDate= appointmentDate;
    }
    public LocalTime getAppointmentTime()
    {
        return appointmentTime;
    }
    public void setAppointmentTime(LocalTime appointmentTime)
    {
        this.appointmentTime= appointmentTime;
    }
    public String getStatus()
    {
        return status;
    }
    public void setStatus(String status)
    {
        this.status= status;
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
