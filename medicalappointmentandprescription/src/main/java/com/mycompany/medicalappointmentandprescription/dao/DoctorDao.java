/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.medicalappointmentandprescription.dao;
import com.mycompany.medicalappointmentandprescription.models.Doctor;
import com.mycompany.medicalappointmentandprescription.models.User;
import com.mycompany.medicalappointmentandprescription.models.Appointment; 
import com.mycompany.medicalappointmentandprescription.models.Prescription;
import com.mycompany.medicalappointmentandprescription.models.MedicalHistory;
import java.time.DayOfWeek;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author ranbi
 */
public class DoctorDao {
    private static final List<Doctor>Doctors=new ArrayList<>();
    private static int nextDoctorId=2;
   
 
    static
    {
     Doctors.add(new Doctor(1,3,"Dr Amit Verma","General Physician","9876543213",500.00,"09:00 AM -02:00 PM",Arrays.asList(DayOfWeek.MONDAY,DayOfWeek.WEDNESDAY,DayOfWeek.FRIDAY),true));
    }
    public boolean addDoctor(Doctor d)
    {
         UserDao ud = new UserDao();
      if(d==null|| d.getDoctorName()==null||d.getDoctorName().trim().isEmpty()||d.getSpecialization()==null||d.getSpecialization().trim().isEmpty()||d.getContactNumber()==null||d.getContactNumber().trim().isEmpty()||!Double.isFinite(d.getConsultationFee())||d.getConsultationFee()<=0.00||d.getWorkingHours()==null||d.getWorkingHours().trim().isEmpty())  
          return false;
      User u=ud.findByUserId(d.getUserId());
      if(u==null)
          return false;
      if(!"Doctor".equalsIgnoreCase(u.getRole()))
          return false;
      if(findByUserId(d.getUserId())!=null)
          return false;
      d.setDoctorId(nextDoctorId++ );
      Doctors.add(d);
      return true;
    }
    public Doctor findByDoctorId(int doctorId)
    {
        for(Doctor d:Doctors)
        {
            if(d.getDoctorId()==doctorId)
                return d;
        }
        return null;
    }
    public Doctor findByUserId(int userId)
    {
        for(Doctor d:Doctors)
        {
            if(d.getUserId()==userId)
                return d;
        }
        return null;
    }
    public List<Doctor>findAll()
    {
        return new ArrayList<>(Doctors);
    }
    public List<Doctor>findAvailableDoctor()
    {
        List<Doctor>available=new ArrayList<>();
        for(Doctor d:Doctors)
        {
            if(d.getAvaliabilityStatus())
                available.add(d);
        }
        return available;
    }
    public boolean updateDoctor(Doctor d)
    {
      if(d==null|| d.getDoctorName()==null||d.getDoctorName().trim().isEmpty()||d.getSpecialization()==null||d.getSpecialization().trim().isEmpty()||d.getContactNumber()==null||d.getContactNumber().trim().isEmpty()||!Double.isFinite(d.getConsultationFee())||d.getConsultationFee()<=0.00||d.getWorkingHours()==null||d.getWorkingHours().trim().isEmpty())  
          return false;
      Doctor exist = findByDoctorId(d.getDoctorId());
      if(exist==null)
          return false;
      if(exist.getUserId()!=d.getUserId())
          return false;
      exist.setDoctorName(d.getDoctorName());
      exist.setSpecialization(d.getSpecialization());
      exist.setContactNumber(d.getContactNumber());
      exist.setWorkingHours(d.getWorkingHours());
      exist.setConsultationFee(d.getConsultationFee());
      exist.setAvaliabilityStatus(d.getAvaliabilityStatus());
      exist.setWorkingDays(d.getWorkingDays());
      return true;
    }
    public boolean deleteDoctor(int doctorId)
    {
     Doctor d= findByDoctorId(doctorId);
        AppointmentDao ad =new AppointmentDao();
      PrescriptionDao pd = new PrescriptionDao();
      MedicalHistoryDao mhd= new MedicalHistoryDao();
     if(d==null)
         return false;
     for(Appointment a:ad.findAll())
     {
         if(a.getDoctorId()==doctorId)
             return false;
     }
     for(Prescription p:pd.findAll())
     {
         if(p.getDoctorId()==doctorId)
             return false;
     }
     for(MedicalHistory mh:mhd.findAll())
     {
         if(mh.getDoctorId()==doctorId)
             return false;
     }
     return Doctors.remove(d);
    }
    }
  
