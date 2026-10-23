/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.medicalappointmentandprescription.dao;
import com.mycompany.medicalappointmentandprescription.models.FollowUpRecommendation;
import com.mycompany.medicalappointmentandprescription.models.Patient;
import com.mycompany.medicalappointmentandprescription.models.Doctor;
import com.mycompany.medicalappointmentandprescription.models.Appointment;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author ranbi
 */
public class FollowUpRecommendationDao {
   private static final List<FollowUpRecommendation>Follows=new ArrayList<>();
   private static int nextFollowUpId=4;
   static
   {
       Follows.add(new FollowUpRecommendation(1,1,1,1,LocalDate.now().plusDays(7),"Follow the prescribed medication and return for a follow-up checkup","Pending",LocalDateTime.now().minusDays(2)) );
       Follows.add(new FollowUpRecommendation(2,2,2,1,LocalDate.now().plusDays(14),"Monitor symptoms and bring previos medical report during next visit","Completed",LocalDateTime.now().minusDays(10)) );
       Follows.add(new FollowUpRecommendation(3,3,3,1,LocalDate.now().plusDays(5),"Return for a follow-up examination and review the treatment progress","Pending",LocalDateTime.now().minusDays(1)) );
   }
   public boolean addFollowUp(FollowUpRecommendation fp)
   {
       if(fp==null)
            return false;
       if(fp.getDoctorId()<=0||fp.getPatientId()<=0||fp.getAppointmentId()<=0||fp.getRecommendedDate()==null||fp.getInstruction()==null||fp.getInstruction().trim().isEmpty())
           return false;
       if(fp.getRecommendedDate().isBefore(LocalDate.now()))
           return false;
       AppointmentDao ap=new AppointmentDao();
       PatientDao pd= new PatientDao();
       Patient p=pd.findByPatientId(fp.getPatientId());
       DoctorDao dd= new  DoctorDao();
       Doctor d=dd.findByDoctorId(fp.getDoctorId());
       Appointment a=ap.findByAppointmentId(fp.getAppointmentId());
       if(a==null||p==null||d==null||a.getDoctorId()!=fp.getDoctorId()||a.getPatientId()!=fp.getPatientId())
           return false;
       fp.setFollowUpId(nextFollowUpId++);
       fp.setStatus("Pending");
       fp.setCreatedAt(LocalDateTime.now());
       Follows.add(fp);
       return true;
   }
   public FollowUpRecommendation findByFollowUpId(int followUpId)
   {
       if(followUpId<=0)
           return null;
       for(FollowUpRecommendation fp:Follows)
       {
           if(fp.getFollowUpId()==followUpId)
               return fp;
       }
       return null;
   }
    public List<FollowUpRecommendation> findAll()
    {
        return new ArrayList<>(Follows);
    }
    public List<FollowUpRecommendation>findByPatientId(int patientId)
    {
        List <FollowUpRecommendation>result=new ArrayList<>();
        for(FollowUpRecommendation a:Follows)
        {
            if(a.getPatientId()==patientId)
                result.add(a);
        }
        return result;
    }
    public List<FollowUpRecommendation>findByDoctorId(int doctorId)
    {
        List <FollowUpRecommendation>result=new ArrayList<>();
        for(FollowUpRecommendation a:Follows)
        {
            if(a.getDoctorId()==doctorId)
                result.add(a);
        }
        return result;
    }
    public boolean updateFollowUp(FollowUpRecommendation fp)
    {
          if(fp==null)
            return false;
          if(fp.getDoctorId()<=0||fp.getPatientId()<=0||fp.getAppointmentId()<=0||fp.getRecommendedDate()==null||fp.getInstruction()==null||fp.getInstruction().trim().isEmpty()||fp.getStatus()==null||fp.getStatus().trim().isEmpty())
            return false;
          FollowUpRecommendation exist = findByFollowUpId(fp.getFollowUpId());
          if(exist==null)
             return false;
           if(fp.getRecommendedDate().isBefore(LocalDate.now()))
           return false;
       AppointmentDao ap=new AppointmentDao();
       PatientDao pd= new PatientDao();
       Patient p=pd.findByPatientId(fp.getPatientId());
       DoctorDao dd= new  DoctorDao();
       Doctor d=dd.findByDoctorId(fp.getDoctorId());
       Appointment a=ap.findByAppointmentId(fp.getAppointmentId());
       if(a==null||p==null||d==null||a.getDoctorId()!=fp.getDoctorId()||a.getPatientId()!=fp.getPatientId())
           return false;
        String status=fp.getStatus();
        if(!"Pending".equalsIgnoreCase(status)&&!"Completed".equalsIgnoreCase(status)&&!"Cancelled".equalsIgnoreCase(status))
         return false;
        exist.setAppointmentId(fp.getAppointmentId());
        exist.setPatientId(fp.getPatientId());
        exist.setDoctorId(fp.getDoctorId());
        exist.setStatus(status);
        exist.setRecommendedDate(fp.getRecommendedDate());
        exist.setInstruction(fp.getInstruction());
        return true;
    }
    public boolean deleteFollowUp(int followUpId)
    {
         FollowUpRecommendation fp=findByFollowUpId(followUpId);
         if(fp==null)
             return false;
         return Follows.remove(fp);
    }
       
}
