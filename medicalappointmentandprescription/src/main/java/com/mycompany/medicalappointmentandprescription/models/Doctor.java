/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.medicalappointmentandprescription.models;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author ranbi
 */
public class Doctor {
    private int doctorId,userId;
    private String doctorName,specialization,contactNumber,workingHours;
    private double consultationFee;
    private boolean availabilityStatus;
    private List<DayOfWeek>workingDays;
    public Doctor()
    {
    }
    public Doctor(int doctorId,int userId,String doctorName,String specialization,String contactNumber,double consultationFee,String workingHours,List<DayOfWeek>workingDays,boolean availabilityStatus)
    {
        this.doctorId=doctorId;
        this.userId=userId;
        this.doctorName=doctorName;
        this.specialization=specialization;
        this.contactNumber=contactNumber;
        this.consultationFee=consultationFee;
        this.workingHours=workingHours;
        this.workingDays= workingDays==null?new ArrayList<>():new ArrayList<>(workingDays);
        this.availabilityStatus=availabilityStatus;
    }
    public int getDoctorId()
    {
        return doctorId;
    }
    public void setDoctorId(int doctorId)
    {
        this.doctorId=doctorId;
    }
    public int getUserId()
    {
        return userId;
    }
    public void setUserId(int userId)
    {
        this.userId=userId;
    }
    public String getDoctorName()
    {
        return doctorName;
    }
    public void setDoctorName(String doctorName)
    {
        this.doctorName=doctorName;
    }
    public String getSpecialization()
    {
        return specialization;
    }
    public void setSpecialization(String specialization)
    {
        this.specialization=specialization;
    }
    public String getContactNumber()
    {
        return contactNumber;
    }
    public void setContactNumber(String contactNumber)
    {
        this.contactNumber=contactNumber;
    }
    public double getConsultationFee()
    {
        return consultationFee;
    }
    public void setConsultationFee(double consultationFee)
    {
        this.consultationFee=consultationFee;
    }
    public String getWorkingHours()
    {
        return workingHours;
    }
    public void setWorkingHours(String workingHours )
    {
        this.workingHours=workingHours;
    }
    public List getWorkingDays()
    {
        return new ArrayList<>(workingDays);
    }
    public void setWorkingDays(List<DayOfWeek>workingDays)
    {
        this.workingDays= workingDays==null?new ArrayList<>():new ArrayList<>(workingDays);
    }
    public boolean getAvaliabilityStatus()
    {
        return availabilityStatus;
    }
    public void setAvaliabilityStatus(boolean availabilityStatus)
    {
        this.availabilityStatus=availabilityStatus;
    }
}
