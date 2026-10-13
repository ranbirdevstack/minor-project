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
  private String username, role,password;
  public User()
  {
  }
public User(int userId,String username,String role,String password)
{
    this.userId=userId;
    this.username=username;
    this.role=role;
    this.password=password;
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
public String getPassword()
{
    return password;
}
public void setPassword(String password )
{
    this.password=password;
}
}
