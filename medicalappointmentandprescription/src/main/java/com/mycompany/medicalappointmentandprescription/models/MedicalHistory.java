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
public class MedicalHistory {
    private int historyId,patientId,doctorId;
    private LocalDateTime recordedAt;
    private String symptoms,diagnosis,treatmentNotes;
    public MedicalHistory()
    {
    }
    public MedicalHistory(int historyId,int patientId,int doctorId,LocalDateTime recordedAt,String symptoms,String diagnosis, String treatmentNotes)
    {
        this.historyId=historyId;
        this.patientId=patientId;
        this.doctorId=doctorId;
        this.recordedAt=recordedAt;
        this.symptoms=symptoms;
        this.diagnosis=diagnosis;
        this.treatmentNotes=treatmentNotes;
    }
    public int getHistoryId()
    {
        return historyId;
    }
    public void setHistoryId(int historyId)
    {
        this.historyId=historyId;
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
    public LocalDateTime getRecordedAt()
    {
        return recordedAt;
    }
    public void setRecordedAt(LocalDateTime recordedAt)
    {
        this.recordedAt=recordedAt;
    }
    public String getSymptoms()
    {
        return symptoms;
    }
    public void setSymptoms(String symptoms)
    {
        this.symptoms=symptoms;
    }
    public String getDiagnosis()
    {
        return diagnosis;
    }
    public void setDiagnosis(String diagnosis)
    {
        this.diagnosis=diagnosis;
    }
    public String getTreatmentNotes()
    {
        return treatmentNotes;
    }
    public void setTreatmentNotes(String treatmentNotes)
    {
        this.treatmentNotes=treatmentNotes;
    }
    
}
