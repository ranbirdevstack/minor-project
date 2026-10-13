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
public class Patient {
    private int patientId,registeredBy,age;
    private String fullName,gender,contactNumber,address,bloodGroup;
    private LocalDateTime registrationDate;
    public Patient()
    {
    }
    public Patient(int patientId,int registeredBy,String fullName,int age,String gender,String contactNumber,String address,String bloodGroup,LocalDateTime registrationDate  )
    {
      this.patientId=patientId;
      this.registeredBy=registeredBy;
      this.fullName=fullName;
      this.age=age;
      this.gender=gender;
      this.contactNumber=contactNumber;
      this.address=address;
      this.bloodGroup=bloodGroup;
      this.registrationDate= registrationDate;
    }
    public int getPatientId()
    {
        return patientId;
    }
    public void setPatientId(int patientId)
    {
        this.patientId=patientId;
    }
    public int getRegisteredBy()
    {
        return registeredBy;
    }
    public void setRegisteredBy(int registeredBy)
    {
        this.registeredBy=registeredBy;
    }
    
    public String getFullName()
    {
        return fullName;
    }
    public void setFullName(String fullName)
    {
        this.fullName=fullName;
    }
    public int getAge()
    {
        return age;
    }
    public void setAge(int age)
    {
        this.age=age;
    }
    public String getGender()
    {
        return gender;
    }
    public void setGender(String gender)
    {
        this.gender=gender;
    }
    public String getContactNumber()
    {
        return contactNumber;
    }
    public void setContactNumber(String contactNumber)
    {
        this.contactNumber=contactNumber;
    }
    public String getAddress()
    {
        return address;
    }
    public void setAddress(String address)
    {
        this.address=address;
    }
    public String getBloodGroup()
    {
        return bloodGroup;
    }
    public void setBloodGroup(String bloodGroup)
    {
        this.bloodGroup=bloodGroup;
    }
       public LocalDateTime getRegistrationDate()
    {
        return registrationDate;
    }
    public void setRegistrationDate(LocalDateTime registrationDate)
    {
        this.registrationDate=registrationDate;
    }   
}