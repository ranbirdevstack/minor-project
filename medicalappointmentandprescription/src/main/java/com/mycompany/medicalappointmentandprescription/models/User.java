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
  private String username, role;
public User(int userId,String username,String role)
{
    this.userId=userId;
    this.username=username;
    this.role=role;
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
public String getRole()
{
    return role;
}
public void setRole(String role )
{
    this.role=role;
}

}
