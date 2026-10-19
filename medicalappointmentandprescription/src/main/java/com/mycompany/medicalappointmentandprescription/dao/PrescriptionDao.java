/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.medicalappointmentandprescription.dao;
import com.mycompany.medicalappointmentandprescription.models.Prescription;
import com.mycompany.medicalappointmentandprescription.models.Patient;
import com.mycompany.medicalappointmentandprescription.models.Doctor;
import com.mycompany.medicalappointmentandprescription.models.Appointment;
import com.mycompany.medicalappointmentandprescription.models.MedicineReminder;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author ranbi
 */
public class PrescriptionDao {
  private static final List<Prescription>Prescriptions=new ArrayList<>();
  private static int nextPrescriptionId=4;
  static
  {
      Prescriptions.add(new Prescription(1,1,1,1,LocalDate.now().plusDays(1),"Paracetamol 500 mg - 1 tablet twice daily","Fever and Headache"));
      Prescriptions.add(new Prescription(2,2,2,1,LocalDate.now().plusDays(2),"Cetrizine 10 mg - 1 tablet at night","Allergy symptoms"));
      Prescriptions.add(new Prescription(3,3,3,1,LocalDate.now().plusDays(3),"Amoxicillin 500 mg - 1 capsule twice daily","Bacterial infection symptoms"));
  }
  public boolean addPrescription(Prescription p)
  {
      if(p==null||p.getDoctorId()<=0||p.getPatientId()<=0||p.getMedicineAndDosage()==null||p.getMedicineAndDosage().trim().isEmpty()||p.getClinicalNotes()==null||p.getClinicalNotes().trim().isEmpty())
          return false;
      PatientDao pd=new PatientDao();
      DoctorDao dd=new DoctorDao();
      AppointmentDao ad=new AppointmentDao();
      Appointment a= ad.findByAppointmentId(p.getAppointmentId());
      Patient pt=pd.findByPatientId(p.getPatientId());
      Doctor d=dd.findByDoctorId(p.getDoctorId());
      if(a==null)
        return false;
      if(pt==null)
          return false;
      if(d==null)
          return false;
      if(a.getPatientId()!=p.getPatientId()||a.getDoctorId()!=p.getDoctorId())
          return false;
      if(a.getStatus()==null||"Cancelled".equalsIgnoreCase(a.getStatus()))
          return false;
      for(Prescription exist:Prescriptions)
      {
          if(exist.getAppointmentId()==p.getAppointmentId())
              return false;
      }
      p.setIssueDate(LocalDate.now());
      p.setPrescriptionId(nextPrescriptionId++);
      Prescriptions.add(p);
      return true;
  }
  public Prescription findByPrescriptionId(int prescriptionId)
  {
      for(Prescription p:Prescriptions)
      {
          if(p.getPrescriptionId()== prescriptionId)
              return p;
      }
      return null;
  }
  public Prescription findByAppointmentId(int appointmentId)
  {
      for(Prescription p:Prescriptions)
      {
          if(p.getAppointmentId()== appointmentId)
              return p;
      }
      return null;
  }
  public List<Prescription>findAll()
  {
          return new ArrayList<>(Prescriptions);
  }
  public List<Prescription>findByPatientId(int patientId)
  {
      List <Prescription>result=new ArrayList<>();
      for(Prescription p:Prescriptions)
      {
          if(p.getPatientId()==patientId)
              result.add(p);
      }
      return result;
  }
  public List<Prescription>findByDoctorId(int doctorId)
  {
      List <Prescription>result=new ArrayList<>();
      for(Prescription p:Prescriptions)
      {
          if(p.getDoctorId()==doctorId)
              result.add(p);
      }
      return result;
  }
  public List<Prescription>findByIssueDate(LocalDate issueDate)
  {
      List <Prescription>result=new ArrayList<>();
      if(issueDate==null)
          return result;
      for(Prescription p:Prescriptions)
      {
          if(p.getIssueDate().equals(issueDate))
              result.add(p);
      }
      return result;
  }
  public boolean updatePrescription(Prescription p)
  {
      if(p==null||p.getAppointmentId()<=0||p.getDoctorId()<=0||p.getPatientId()<=0||p.getMedicineAndDosage()==null||p.getMedicineAndDosage().trim().isEmpty()||p.getClinicalNotes()==null||p.getClinicalNotes().trim().isEmpty())
          return false;
      
      Prescription exist=findByPrescriptionId(p.getPrescriptionId());
      if(exist==null)
          return false;
      PatientDao pd=new PatientDao();
      DoctorDao dd=new DoctorDao();
      AppointmentDao ad=new AppointmentDao();
      Appointment a= ad.findByAppointmentId(p.getAppointmentId());
      Patient pt=pd.findByPatientId(p.getPatientId());
      Doctor d=dd.findByDoctorId(p.getDoctorId());
      if(a==null)
        return false;
      if(pt==null)
          return false;
      if(d==null)
          return false;
      if(a.getPatientId()!=p.getPatientId()||a.getDoctorId()!=p.getDoctorId())
          return false;
      if(a.getStatus()==null||"Cancelled".equalsIgnoreCase(a.getStatus()))
          return false;
      for(Prescription other:Prescriptions)
      {
          if(other.getPrescriptionId()!=p.getPrescriptionId()&&other.getAppointmentId()==p.getAppointmentId())
              return false;
      }
      exist.setAppointmentId(p.getAppointmentId());
      exist.setPatientId(p.getPatientId());
      exist.setDoctorId(p.getDoctorId());
      exist.setMedicineAndDosage(p.getMedicineAndDosage());
      exist.setClinicalNotes(p.getClinicalNotes());
      return true;
  }
  public boolean deletePrescription(int prescriptionId)
  {
      Prescription p=findByPrescriptionId(prescriptionId);
      if(p==null)
          return false;
      MedicineReminderDao mrd=new MedicineReminderDao();
      for(MedicineReminder mr:mrd.findAll())
      {
          if(mr.getPrescriptionId()== prescriptionId)
             return false;
      }
      return Prescriptions.remove(p);
  }
}
