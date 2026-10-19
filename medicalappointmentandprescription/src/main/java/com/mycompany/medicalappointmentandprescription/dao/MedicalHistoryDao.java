/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.medicalappointmentandprescription.dao;
import com.mycompany.medicalappointmentandprescription.models.MedicalHistory;
import com.mycompany.medicalappointmentandprescription.models.Doctor;
import com.mycompany.medicalappointmentandprescription.models.Patient;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author ranbi
 */
public class MedicalHistoryDao {
    private static final List<MedicalHistory> Histories=new ArrayList<>();
    private static int nextHistoryId=4;
    static
    {
        LocalDateTime now=LocalDateTime.now();
        Histories.add(new MedicalHistory(1,1,1,now.minusDays(10),"Fever and Headache","Viral Fever","Rest, fluids and  prescribed  medation"));
        Histories.add(new MedicalHistory(2,2,1,now.minusDays(5),"Sneezing and runny nose","Allergic rhintics","Avoid allergens and take prescribed medation"));
        Histories.add(new MedicalHistory(3,3,1,now.minusDays(2),"Sore throat and cough","Upper respiratory tract infection","Rest and follow prescribed medation"));    
    }
    public boolean addHistory(MedicalHistory h)
    {
        if(h==null||h.getPatientId()<=0||h.getDoctorId()<=0||h.getSymptoms()==null||h.getSymptoms().trim().isEmpty()||h.getTreatmentNotes()==null||h.getTreatmentNotes().trim().isEmpty()||h.getDiagnosis()==null||h.getDiagnosis().trim().isEmpty())
            return false;
        PatientDao pd=new PatientDao();
        DoctorDao dd=new DoctorDao();
        Patient p=pd.findByPatientId(h.getPatientId());
        Doctor d=dd.findByDoctorId(h.getDoctorId());
        if(p == null||d == null)
            return false;
        h.setHistoryId(nextHistoryId++);
        h.setRecordedAt(LocalDateTime.now());
        h.setDiagnosis(h.getDiagnosis().trim());
        h.setSymptoms(h.getSymptoms());
        h.setTreatmentNotes(h.getTreatmentNotes());
        Histories.add(h);
        return true;
    }
    public MedicalHistory findByHistoryId(int historyId)
    {
        for(MedicalHistory h:Histories)
        {
            if(h.getHistoryId()==historyId)
                return h;
        }
        return null;
    }
    public List<MedicalHistory>findAll()
    {
        return new ArrayList<>(Histories);
    }
    public List<MedicalHistory>findByPatientId(int patientId)
    {
        List<MedicalHistory> result=new ArrayList<>();
        for(MedicalHistory h:Histories)
        {
            if(h.getPatientId()==patientId)
                result.add(h);
        }
        return result;
    }
    public List<MedicalHistory>findByDoctorId(int doctorId)
    {
        List<MedicalHistory> result=new ArrayList<>();
        for(MedicalHistory h:Histories)
        {
            if(h.getDoctorId()==doctorId)
                result.add(h);
        }
        return result;
    }
    public boolean updateHistory(MedicalHistory h)
    {
        if(h==null||h.getHistoryId()<=0||h.getPatientId()<=0||h.getDoctorId()<=0||h.getSymptoms()==null||h.getSymptoms().trim().isEmpty()||h.getTreatmentNotes()==null||h.getTreatmentNotes().trim().isEmpty()||h.getDiagnosis()==null||h.getDiagnosis().trim().isEmpty())
            return false;
        MedicalHistory exist=findByHistoryId(h.getHistoryId());
        if(exist==null)
            return false;
        PatientDao pd=new PatientDao();
        DoctorDao dd=new DoctorDao();
        Patient p=pd.findByPatientId(h.getPatientId());
        Doctor d=dd.findByDoctorId(h.getDoctorId());
        if(p == null||d == null)
            return false;
        exist.setPatientId(h.getPatientId());
        exist.setDoctorId(h.getDoctorId());
        exist.setDiagnosis(h.getDiagnosis().trim());
        exist.setSymptoms(h.getSymptoms().trim());
        exist.setTreatmentNotes(h.getTreatmentNotes().trim());
        return true;
    }
    public boolean deleteHistory(int historyId)
    {
        MedicalHistory h=findByHistoryId(historyId);
          if(h==null)
           return false;
       return Histories.remove(h);
    }
    
}
