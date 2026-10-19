/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.medicalappointmentandprescription.dao;
import com.mycompany.medicalappointmentandprescription.models.MedicineDoseHistory;
import com.mycompany.medicalappointmentandprescription.models.Patient;
import com.mycompany.medicalappointmentandprescription.models.MedicineReminder;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
/**
 *
 * @author ranbi
 */
public class MedicineDoseHistoryDao {
   private static final List<MedicineDoseHistory>doses=new ArrayList<>();
   private static int nextHistoryId=4;
   private final MedicineReminderDao mrd;
    public MedicineDoseHistoryDao(MedicineReminderDao mrd)
    {
        if(mrd==null)
            throw new IllegalArgumentException("MedicineDoseHistoryDao can't be null");
        this.mrd=mrd;
    }
    static
    {
        LocalDateTime now= LocalDateTime.now();
        doses.add(new MedicineDoseHistory(1,1,1,"Paracetamol","500mg",now.minusMinutes(1),now,"Given"));
        doses.add(new MedicineDoseHistory(2,2,2,"Cetraeizine","10mg",now.plusHours(2),now.plusHours(2).plusMinutes(2),"Given"));
        doses.add(new MedicineDoseHistory(3,3,3,"Paracetamol","500mg",now.plusHours(4),now.plusHours(4).plusMinutes(3),"Given"));
    }
    public boolean addHistory( MedicineDoseHistory h)
    {
        if(h==null||h.getReminderId()<=0||h.getPatientId()<=0||h.getMedicineName()==null||h.getMedicineName().trim().isEmpty()||h.getDosage()==null||h.getDosage().trim().isEmpty()||h.getGivenAt()==null||h.getScheduledAt()==null||h.getStatus()==null||h.getStatus().trim().isEmpty())
            return false;
        MedicineReminder mr=mrd.findByReminderId(h.getReminderId());
        if(mr==null||mr.getPatientId()!=h.getPatientId()||!mr.getMedicineName().trim().equalsIgnoreCase(h.getMedicineName())||!mr.getDosage().trim().equalsIgnoreCase(h.getDosage()))
            return false;
        PatientDao pd=new PatientDao();
        Patient p=pd.findByPatientId(h.getPatientId());
        if(p == null)
            return false;
         String status=h.getStatus().trim();
         if(!status.equalsIgnoreCase("Given")&&!status.equalsIgnoreCase("Skipped"))
             return false;
         if(status.equalsIgnoreCase("Given")&&h.getGivenAt().isBefore(h.getScheduledAt()))
             return false;
         h.setHistoryId(nextHistoryId++);
         h.setMedicineName(h.getMedicineName().trim());
         h.setDosage(h.getDosage().trim());
         h.setStatus(status.equalsIgnoreCase("Given")?"Given":"Skipped");
         doses.add(h);
         return true;
    }  
    public MedicineDoseHistory findByHistoryId(int historyId)
    {
      for(MedicineDoseHistory h:doses)
      {
          if(h.getHistoryId()==historyId)
              return h;
      }
      return null;
    }
    public List<MedicineDoseHistory> findAll()
    {
        return  new ArrayList<>(doses);
    }
    
    public List<MedicineDoseHistory> findByPatientId(int patientId)
    {
        List<MedicineDoseHistory>result=new ArrayList<>();
        for(MedicineDoseHistory h:doses)
        {
            if(h.getPatientId()==patientId)
                result.add(h);
        }
        return result;
    }
    public List<MedicineDoseHistory> findByReminderId(int reminderId)
    {
        List<MedicineDoseHistory>result=new ArrayList<>();
        for(MedicineDoseHistory h:doses)
        {
            if(h.getReminderId()==reminderId)
                result.add(h);
        }
        return result;
    }
    public boolean updateHistory(MedicineDoseHistory h)
    {
         if(h==null||h.getHistoryId()<=0||h.getReminderId()<=0||h.getPatientId()<=0||h.getMedicineName()==null||h.getMedicineName().trim().isEmpty()||h.getDosage()==null||h.getDosage().trim().isEmpty()||h.getGivenAt()==null||h.getScheduledAt()==null||h.getStatus()==null||h.getStatus().trim().isEmpty())
            return false;
         MedicineDoseHistory exist=findByHistoryId(h.getHistoryId());
         if(exist==null)
             return false;
         MedicineReminder mr=mrd.findByReminderId(h.getReminderId());
         if(mr==null||mr.getPatientId()!=h.getPatientId()||!mr.getMedicineName().trim().equalsIgnoreCase(h.getMedicineName())||!mr.getDosage().trim().equalsIgnoreCase(h.getDosage()))
            return false;
        PatientDao pd=new PatientDao();
        Patient p=pd.findByPatientId(h.getPatientId());
        if(p == null)
            return false;
        String status=h.getStatus().trim();
         if(!status.equalsIgnoreCase("Given")&&!status.equalsIgnoreCase("Skipped"))
             return false;
         if(status.equalsIgnoreCase("Given")&&h.getGivenAt().isBefore(h.getScheduledAt()))
             return false;
         exist.setReminderId(h.getReminderId());
         exist.setPatientId(h.getPatientId());
         exist.setMedicineName(h.getMedicineName().trim());
         exist.setDosage(h.getDosage().trim());
         exist.setScheduledAt(h.getScheduledAt());
         exist.setGivenAt(h.getGivenAt());
         exist.setStatus(status.equalsIgnoreCase("Given")?"Given":"Skipped");
         return true;
    }
    public boolean deleteHistory(int historyId)
    {
       MedicineDoseHistory h=findByHistoryId(historyId); 
       if(h==null)
           return false;
       return doses.remove(h);
    }
}
