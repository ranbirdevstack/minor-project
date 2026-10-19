/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.medicalappointmentandprescription.dao;
import com.mycompany.medicalappointmentandprescription.models.Appointment;
import com.mycompany.medicalappointmentandprescription.models.Doctor;
import com.mycompany.medicalappointmentandprescription.models.Patient;
import com.mycompany.medicalappointmentandprescription.models.Prescription;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author ranbi
 */
public class AppointmentDao {
    private static final List<Appointment>Appointments=new ArrayList<>();
    private static int nextAppointmentId=4;
   
   
    static
    {
        Appointments.add(new Appointment(1,1,1,LocalDate.now().plusDays(1),LocalTime.of(10,0),"Confirmed",LocalDateTime.now()));
        Appointments.add(new Appointment(2,2,1,LocalDate.now().plusDays(2),LocalTime.of(11,0),"Pending",LocalDateTime.now()));
        Appointments.add(new Appointment(3,3,1,LocalDate.now().plusDays(3),LocalTime.of(12,0),"Confirmed",LocalDateTime.now()));
    }
    public boolean addAppointment(Appointment a)
    {
         DoctorDao dd=new DoctorDao();
        PatientDao ptd=new PatientDao();
     if(a==null||a.getPatientId()<=0||a.getDoctorId()<=0||a.getAppointmentDate()==null||a.getAppointmentTime()==null)
         return false;
     Patient p=ptd.findByPatientId(a.getPatientId());
     if(p==null)
         return false;
     Doctor d= dd.findByDoctorId(a.getDoctorId());
     if(d==null||!d.getAvaliabilityStatus())
         return false;
          if(a.getAppointmentDate().isBefore(LocalDate.now()))
         return false;
       List<Appointment>existing=findByDoctorDateAndTime(a.getDoctorId(),a.getAppointmentTime(),a.getAppointmentDate());
      for(Appointment exist:existing) 
     {
         if(!"Cancelled".equalsIgnoreCase(exist.getStatus()))
             return false;   
     }
     a.setAppointmentId(nextAppointmentId++);
     a.setStatus("Pending");
     a.setCreatedAt(LocalDateTime.now());
     Appointments.add(a);
     return true;
    }
    public Appointment findByAppointmentId(int appointmentId)
    {
        for(Appointment a:Appointments)
        {
            if(a.getAppointmentId()==appointmentId)
                return a;
        }
        return null;
    }
    public List<Appointment> findAll()
    {
        return new ArrayList<>(Appointments);
    }
    public List<Appointment>findByPatientId(int patientId)
    {
        List <Appointment>result=new ArrayList<>();
        for(Appointment a:Appointments)
        {
            if(a.getPatientId()==patientId)
                result.add(a);
        }
        return result;
    }
    public List<Appointment>findByDoctorId(int doctorId)
    {
        List <Appointment>result=new ArrayList<>();
        for(Appointment a:Appointments)
        {
            if(a.getDoctorId()==doctorId)
                result.add(a);
        }
        return result;
    }
    public List<Appointment>findByDate(LocalDate Date)
    {
        List <Appointment>result=new ArrayList<>();
        if(Date==null)
            return result;
        for(Appointment a:Appointments)
        {
            if(a.getAppointmentDate().equals(Date))
                result.add(a);
        }
        return result;
    }
    public List<Appointment>findByTime(LocalTime Time)
    {
        List <Appointment>result=new ArrayList<>();
        if(Time==null)
            return result;
        for(Appointment a:Appointments)
        {
            if(a.getAppointmentTime().equals(Time))
                result.add(a);
        }
        return result;
    }
    public List<Appointment>findByDoctorDateAndTime(int doctorId,LocalTime Time,LocalDate Date)
    {
      List <Appointment>result=new ArrayList<>();
        if(doctorId<=0||Date==null||Time==null)
            return result;
        for(Appointment a:Appointments)
        {
            if(a.getDoctorId()==doctorId&&a.getAppointmentTime().equals(Time)&&a.getAppointmentDate().equals(Date))
                result.add(a);
        }
        return result;
    }
    public boolean updateAppointment(Appointment a)
    {
         DoctorDao dd=new DoctorDao();
        PatientDao ptd=new PatientDao();
        if(a==null||a.getPatientId()<=0||a.getDoctorId()<=0||a.getAppointmentDate()==null||a.getAppointmentTime()==null)
         return false;
        Appointment exist= findByAppointmentId(a.getAppointmentId());
        if(exist==null)
         return false;
        Patient p=ptd.findByPatientId(a.getPatientId());
        if(p==null)
         return false;
        Doctor d= dd.findByDoctorId(a.getDoctorId());
        if(d==null)
         return false;
        String status=a.getStatus();
        if(!"Pending".equalsIgnoreCase(status)&&!"Confirmed".equalsIgnoreCase(status)&&!"Completed".equalsIgnoreCase(status)&&!"Cancelled".equalsIgnoreCase(status)&&!d.getAvaliabilityStatus())
         return false;
        if(a.getAppointmentDate().isBefore(LocalDate.now()))
         return false;
        for(Appointment other:Appointments)
        {
            if(other.getAppointmentId()!=a.getAppointmentId()&&other.getDoctorId()==a.getDoctorId()&&other.getAppointmentDate().equals(a.getAppointmentDate())&&other.getAppointmentTime().equals(a.getAppointmentTime())&&!"Cancelled".equalsIgnoreCase(other.getStatus())&& !"Cancelled".equalsIgnoreCase(status))
             return false;
        }
        exist.setPatientId(a.getPatientId());
        exist.setDoctorId(a.getDoctorId());
        exist.setAppointmentDate(a.getAppointmentDate());
        exist.setAppointmentTime(a.getAppointmentTime());
        exist.setStatus(status);
        return true;
        }
    public boolean deleteAppointment(int appointmentId)
    {
        Appointment a=findByAppointmentId(appointmentId);
        PrescriptionDao pd=new PrescriptionDao();
        if(a==null)
         return false;
        for(Prescription p:pd.findAll())
        {
            if(p.getAppointmentId()==appointmentId)
                return false;
        }
        return Appointments.remove(a);
    }   
}
