/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.medicalappointmentandprescription.dao;
import com.mycompany.medicalappointmentandprescription.models.User;
import java.util.List;

/**
 *
 * @author ranbi
 */
public class UserDao {
   
      private static final List <User> ACCOUNTS = List.of(new User(1,"admin","Admin","adm123"),new User(2,"staff","Receptionist","sta123"),new User(3,"doctor","Doctor","doc123"));
      public User validateCredentials(String username,String password)
      {
          for (User account:ACCOUNTS)
          {
              if(account.getUsername().equals(username)&& account.getPassword().equals(password))
                  return account;
          }
          return null;
      }
      public User findByUserId(int userId )
      {
        for (User account:ACCOUNTS)
          {
              if(account.getUserId()== userId)
              {
                  return account;
              }
          }
           return null;
      }
      public boolean updatePassword(int userId,String newPassword)
      {
          User u = findByUserId(userId);
          if(u != null)
          {
              u.setPassword(newPassword);
              return true;
          }
          return false;
      }
    }
    

