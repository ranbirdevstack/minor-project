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
    private record DummyAccount(String username,String password,User user)
    {
    }
      private static final List <DummyAccount> ACCOUNTS = List.of(new DummyAccount("admin","ad123",new User(1,"admin","Admin","ad123")),new DummyAccount("staff","st123",new User(2,"staff","Receptionist","st123")),new DummyAccount("doctor","dr123",new User(3,"doctor","Doctor","dr123")));
      public User validateCreditials(String username,String password)
      {
          for (DummyAccount account:ACCOUNTS)
          {
              if(account.username().equals(username)&& account.password().equals(password))
                  return account.user();
          }
          return null;
      }
    }
    

