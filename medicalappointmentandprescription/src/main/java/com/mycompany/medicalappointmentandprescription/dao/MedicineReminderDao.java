/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.medicalappointmentandprescription.dao;
import com.mycompany.medicalappointmentandprescription.models.MedicineReminder;
import com.mycompany.medicalappointmentandprescription.models.Patient;
import com.mycompany.medicalappointmentandprescription.models.Prescription;
import com.mycompany.medicalappointmentandprescription.models.MedicineDoseHistory;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
        
/**
 *
 * @author ranbi
 */
public class MedicineReminderDao {
    private static final List<MedicineReminder> Reminders =new ArrayList<>();
    private static int nextReminderId=4;
    static
    {
        Reminders.add(new MedicineReminder(1,1,1,"Paracetmol","500mg",LocalDateTime.now().minusMinutes(1),8,null,"Pending"));
        Reminders.add(new MedicineReminder(2,2,2,"Cetirizine","10mg",LocalDateTime.now().plusHours(2),24,null,"Pending"));
        Reminders.add(new MedicineReminder(3,3,3,"Amoxicilin","500mg",LocalDateTime.now().plusHours(4),8,null,"Pending"));
    }
    private final    MedicineDoseHistoryDao mdhd=new MedicineDoseHistoryDao(this);
    public boolean addReminder(MedicineReminder r)
    {
        if(r==null||r.getPatientId()<=0||r.getPrescriptionId()<=0||r.getMedicineName()==null||r.getMedicineName().trim().isEmpty()||r.getDosage()==null||r.getDosage().trim().isEmpty()||r.getIntervalHours()<=0)
            return false;
        PatientDao ptd=new PatientDao();
        PrescriptionDao pd=new PrescriptionDao();
        Patient pt=ptd.findByPatientId(r.getPatientId());
        Prescription p=pd.findByPrescriptionId(r.getPrescriptionId());
        if(pt==null||p==null||p.getPatientId()!=r.getPatientId())  
            return false;
        r.setReminderId(nextReminderId++);
        r.setMedicineName(r.getMedicineName().trim());
        r.setDosage(r.getDosage().trim());
        r.setStatus("Pending");
        r.setSnoozeUntil(null);
        Reminders.add(r);
        return true;
    }
    public MedicineReminder findByReminderId(int reminderId)
    {
        for(MedicineReminder mr:Reminders)
        {
            if(mr.getReminderId()==reminderId)
                return mr;
        }
        return null;
    }
    public List<MedicineReminder> findAll()
    {
        return new ArrayList<>(Reminders);
    }
    public List<MedicineReminder> findByPatientId(int patientId)
    {
        List<MedicineReminder> result=new ArrayList<>();
        for(MedicineReminder r:Reminders)
        {
            if(r.getPatientId()==patientId)
                result.add(r);
        }
        return result;
    }
    public List<MedicineReminder> findByPrescriptionId(int prescriptionId)
    {
        List<MedicineReminder> result=new ArrayList<>();
        for(MedicineReminder r:Reminders)
        {
            if(r.getPrescriptionId()==prescriptionId)
                result.add(r);
        }
        return result;
    }
    public List<MedicineReminder> findByStatus(String status)
    {
        List<MedicineReminder> result=new ArrayList<>();
        for(MedicineReminder r:Reminders)
        {
            if(r.getStatus().equalsIgnoreCase(status))
                result.add(r);
        }
        return result;
    }
    public List<MedicineReminder> findDueReminders()
    {
        List<MedicineReminder>result=new ArrayList<>();
        LocalDateTime now= LocalDateTime.now();
        for(MedicineReminder r:Reminders)
        {
         boolean pending="Pending".equalsIgnoreCase(r.getStatus());
         boolean snoozed="Snoozed".equalsIgnoreCase(r.getStatus());
         boolean scheduledTime=r.getScheduledAt()!=null&&!r.getScheduledAt().isAfter(now);
         boolean snoozedTime=r.getSnoozeUntil()!=null&&!r.getSnoozeUntil().isAfter(now);
         if(pending&&scheduledTime)
             result.add(r);
         else if(snoozed&&snoozedTime)
             result.add(r);
        }
        return result;
    }
    public boolean updateReminder(MedicineReminder r)
    {
        if(r==null)
            return false;
        MedicineReminder exist=findByReminderId(r.getReminderId());
        if(exist==null||r.getPatientId()<=0||r.getPrescriptionId()<=0||r.getMedicineName()==null||r.getMedicineName().trim().isEmpty()||r.getDosage()==null||r.getDosage().trim().isEmpty()||r.getIntervalHours()<=0||r.getStatus()==null||r.getStatus().trim().isEmpty())
            return false;
         PatientDao ptd=new PatientDao();
         PrescriptionDao pd=new PrescriptionDao();
         Patient pt=ptd.findByPatientId(r.getPatientId());
        Prescription p=pd.findByPrescriptionId(r.getPrescriptionId());
        if(pt==null||p==null||p.getPatientId()!=r.getPatientId())  
            return false;
        exist.setPatientId(r.getPatientId());
        exist.setPrescriptionId(r.getPrescriptionId());
        exist.setMedicineName(r.getMedicineName().trim());
        exist.setDosage(r.getDosage().trim());
        exist.setScheduledAt(r.getScheduledAt());
        exist.setIntervalHours(r.getIntervalHours());
        exist.setSnoozeUntil(r.getSnoozeUntil());
        exist.setStatus(r.getStatus());
        return true;
    }
    public boolean snoozedReminder(int reminderId,int minutes)
    {
        if(minutes!=5 && minutes!=10)
            return false;
        MedicineReminder r = findByReminderId(reminderId);
        if(r == null||!"pending".equalsIgnoreCase(r.getStatus())&&!"Snoozed".equalsIgnoreCase(r.getStatus()))
            return false;
        r.setSnoozeUntil(r.getSnoozeUntil().plusMinutes(minutes));
        r.setStatus("Snoozed");
        return true;
    }
    public boolean completedReminder(int reminderId)
    {
        MedicineReminder r = findByReminderId(reminderId);
        if(r==null||(!"Pending".equalsIgnoreCase(r.getStatus())&&!"Snoozed".equalsIgnoreCase(r.getStatus())))
            return false;
        LocalDateTime now= LocalDateTime.now();
        MedicineDoseHistory mdh=new MedicineDoseHistory(r.getReminderId(),r.getPatientId(),0,r.getMedicineName(),r.getDosage(),r.getScheduledAt(),now,"Given");
        if(!mdhd.addHistory(mdh))
            return false;
        r.setScheduledAt(now.plusHours(r.getIntervalHours()));
        r.setSnoozeUntil(null);
        r.setStatus("Pending");
        return true;
    }
    public boolean deleteReminder(int reminderId)
    {
        MedicineReminder r=findByReminderId(reminderId);
        if(r==null)
            return false;
      
         for( MedicineDoseHistory mdh:mdhd.findAll())
         {
             if(mdh.getReminderId()==reminderId)
                 return false;
         }
         return Reminders.remove(r);
    }
}
