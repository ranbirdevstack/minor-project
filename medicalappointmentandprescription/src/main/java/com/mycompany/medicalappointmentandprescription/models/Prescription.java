/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.medicalappointmentandprescription.models;
import java.time.LocalDate;

/**
 *
 * @author ranbi
 */
public class Prescription {
    private int prescriptionId,appointmentId,patientId,doctorId;
    private LocalDate issueDate;
    private String medicineAndDosage,clinicalNotes;
    public Prescription()
    {
    }
    public Prescription(int prescriptionId,int appointmentId,int patientId,int doctorId,LocalDate issueDate,String medicineAndDosage,String clinicalNotes)
    {
     this.prescriptionId=prescriptionId;
     this.appointmentId=appointmentId;
     this.patientId=patientId;
     this.doctorId=doctorId;
     this.issueDate=issueDate;
     this.medicineAndDosage=medicineAndDosage;
     this.clinicalNotes=clinicalNotes;
    }
    public int getPrescriptionId()
    {
        return prescriptionId;
    }
    public void setPrescriptionId(int prescriptionId)
    {
        this.prescriptionId=prescriptionId;
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
    public LocalDate getIssueDate()
    {
        return issueDate;
    }
    public void setIssueDate(LocalDate issueDate)
    {
        this.issueDate=issueDate;
    }
     public String getMedicineAndDosage()
    {
        return medicineAndDosage;
    }
    public void setMedicineAndDosage(String medicineAndDosage)
    {
        this.medicineAndDosage=medicineAndDosage;
    }
     public String getClinicalNotes()
    {
        return clinicalNotes;
    }
    public void setClinicalNotes(String clinicalNotes)
    {
        this.clinicalNotes=clinicalNotes;
    }
}
