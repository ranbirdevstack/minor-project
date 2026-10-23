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
    private List<DayOfWeek>workingDay;
    public Doctor()
    {
    }
    public Doctor(int doctorId,int userId,String doctorName,String specialization,String contactNumber,double consultationFee,String workingHours,List<DayOfWeek>workingDay,boolean availabilityStatus)
    {
        this.doctorId=doctorId;
        this.userId=userId;
        this.doctorName=doctorName;
        this.specialization=specialization;
        this.contactNumber=contactNumber;
        this.consultationFee=consultationFee;
        this.workingHours=workingHours;
        this.workingDay= workingDay==null?new ArrayList<>():new ArrayList<>(workingDay);
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
    public List getWorkingDay()
    {
        return new ArrayList<>(workingDay);
    }
    public void setWorkingDay(List<DayOfWeek>workingDay)
    {
        this.workingDay= workingDay==null?new ArrayList<>():new ArrayList<>(workingDay);
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
