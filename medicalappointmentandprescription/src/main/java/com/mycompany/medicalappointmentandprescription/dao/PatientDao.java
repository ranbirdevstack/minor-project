/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.medicalappointmentandprescription.dao;
import com.mycompany.medicalappointmentandprescription.models.Patient;
import com.mycompany.medicalappointmentandprescription.models.User;
import com.mycompany.medicalappointmentandprescription.models.Appointment;
import com.mycompany.medicalappointmentandprescription.models.Prescription;
import com.mycompany.medicalappointmentandprescription.models.MedicalHistory;
import com.mycompany.medicalappointmentandprescription.models.MedicineReminder;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
/**
 *
 * @author ranbi
 */
public class PatientDao {
    private static final List<Patient>Patients = new ArrayList<>();
    private static int nextPatientId=4;
    
    static
    {
     Patients.add(new Patient(1,2,"Rahul Sharma",25,"Male","9876543210","Varanasi, UttarPradesh","O+",LocalDateTime.now()));
     Patients.add(new Patient(2,2,"Priya Singh",22,"Female","9876543211","Jaunpur, UttarPradesh","B+",LocalDateTime.now()));
     Patients.add(new Patient(3,2,"Ankit Verma",35,"Male","9876543212","Lucknow, UttarPradesh","A+",LocalDateTime.now()));
    }
    public boolean registerPatient(Patient p,int staffId)
    {
        UserDao ud = new UserDao();
     if(p.getFullName()==null||p.getFullName().trim().isEmpty()||p.getContactNumber()==null||p.getAge()<=0||p.getGender()== null || p.getGender().trim().isEmpty()||p.getContactNumber().trim().isEmpty()|| p.getAddress()==null||p.getAddress().trim().isEmpty()||p.getBloodGroup()== null||p.getBloodGroup().trim().isEmpty()) 
         return false;
     User u = ud.findByUserId(staffId);
     if(u==null)
         return false;
     if(!"Admin".equalsIgnoreCase(u.getRole())&&!"Receptionist".equalsIgnoreCase(u.getRole()))
         return false;
     p.setPatientId(nextPatientId++);
     p.setRegisteredBy(staffId);
     p.setRegistrationDate(LocalDateTime.now());
     Patients.add(p);
     return true;
    }
    public Patient findByPatientId(int patientId)
    {
        for(Patient p:Patients)
        {
            if(p.getPatientId()==patientId)
                return p;
        }
        return null;
    }
    public List<Patient> findAll()
    {
      return new ArrayList<>(Patients);
    }
    public List<Patient> findByName(String key)
    {
      List<Patient> result=new ArrayList<>();
      if(key==null||key.trim().isEmpty())
          return result;
      String search=key.trim().toLowerCase();
      for(Patient p:Patients)
      {
          if(p.getFullName()!=null&&p.getFullName().trim().toLowerCase().contains(search))
              result.add(p);
      }
      return result;
    }
    public List<Patient> findByContact(String contactNumber)
    {
      List<Patient> result=new ArrayList<>();
      if(contactNumber==null||contactNumber.trim().isEmpty())
          return result;
      String search=contactNumber.trim();
      for(Patient p:Patients)
      {
          if(p.getContactNumber()!=null&&p.getContactNumber().trim().contains(search))
              result.add(p);
      }
      return result;
    }
   public boolean updatePatient(Patient p)
   {
       if(p==null||p.getFullName()==null||p.getFullName().trim().isEmpty()||p.getContactNumber()==null||p.getAge()<=0||p.getGender()== null || p.getGender().trim().isEmpty()||p.getContactNumber().trim().isEmpty()|| p.getAddress()==null||p.getAddress().trim().isEmpty()||p.getBloodGroup()== null||p.getBloodGroup().trim().isEmpty()) 
         return false;
       Patient exist=findByPatientId(p.getPatientId());
       if(exist==null)
           return false;
       exist.setRegisteredBy(p.getRegisteredBy());
       exist.setRegistrationDate(p.getRegistrationDate());
       exist.setFullName(p.getFullName());
       exist.setContactNumber(p.getContactNumber());
       exist.setAddress(p.getAddress());
       exist.setAge(p.getAge());
       exist.setGender(p.getGender());
           exist.setBloodGroup(p.getBloodGroup());
       return true;
   }
   public boolean deletePatient(int patientId)
   {
       AppointmentDao ad = new AppointmentDao();
       PrescriptionDao pd = new PrescriptionDao();
       MedicalHistoryDao mhd = new MedicalHistoryDao();
       MedicineReminderDao mrd = new MedicineReminderDao();
       Patient p = findByPatientId(patientId);
       if(p==null)
           return false;
       for(Appointment a:ad.findAll())
       {
        if(a.getPatientId()== patientId)
            return false;
       }
       for(Prescription pr:pd.findAll())
       {
           if(pr.getPatientId()== patientId)
               return false;
       }
       for(MedicalHistory mh:mhd.findAll() )
       {
           if(mh.getPatientId()==patientId)
               return false;
       }
       for(MedicineReminder mr:mrd.findAll())
       {
           if(mr.getPatientId()==patientId)
               return false;
       }
       return Patients.remove(p);
   }
}