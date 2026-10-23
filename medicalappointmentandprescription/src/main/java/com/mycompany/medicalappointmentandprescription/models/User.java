/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.medicalappointmentandprescription.models;

/**
 *
 * @author ranbi
 */
public class User {
  private int userId;
  private String username,fullName,contactNumber, role,password;
  private boolean isActive;
  public User()
  {
  }
public User(int userId,String username,String fullName,String contactNumber,String role,String password,boolean isActive)
{
    this.userId=userId;
    this.username=username;
    this.fullName=fullName;
    this.contactNumber=contactNumber;
    this.role=role;
    this.password=password;
    this.isActive=isActive;
}
public int getUserId()
{
    return userId;
}
public void setUserId(int userId)
{
    this.userId=userId;
}
public String getUsername()
{
    return username;
}
public void setUsername(String username)
{
    this.username=username;
}
public String getFullName()
    {
        return fullName;
    }
public void setFullName(String fullName)
    {
        this.fullName=fullName;
    }
public String getContactNumber()
    {
        return contactNumber;
    }
public void setContactNumber(String contactNumber)
    {
        this.contactNumber=contactNumber;
    }
public String getRole()
{
    return role;
}
public void setRole(String role )
{
    this.role=role;
}
public String getPassword()
{
    return password;
}
public void setPassword(String password )
{
    this.password=password;
}
public boolean getIsActive()
{
    return isActive;
}
public void setIsActive(boolean isActive )
{
    this.isActive=isActive;
}
}
