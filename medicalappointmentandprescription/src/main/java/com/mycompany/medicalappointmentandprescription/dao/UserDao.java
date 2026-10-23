/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.medicalappointmentandprescription.dao;
import com.mycompany.medicalappointmentandprescription.models.User;
import com.mycompany.medicalappointmentandprescription.models.Patient;
import com.mycompany.medicalappointmentandprescription.models.Doctor;
import java.util.List;
import java.util.ArrayList;

/**
 *
 * @author ranbi
 */
public class UserDao {
   
      private static final List <User> ACCOUNTS = new ArrayList<>();
      private static int nextUserId=4;
      static {
          ACCOUNTS.add(new User(1,"admin","Rahul Singh","9876543214","Admin","adm123",true));
          ACCOUNTS.add(new User(2,"staff","Priya Sharma","9876543215","Staff","rec123",true));
          ACCOUNTS.add(new User(3,"doctor","Dr Amit Verma","9876543213","Doctor","doc123",true));
      }
      public User validateCredentials(String username,String password)
      {
          if(username==null||password==null)
              return null;
          for (User account:ACCOUNTS)
          {
              if(account.getUsername().equals(username)&& account.getPassword().equals(password)&&account.getIsActive())
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
      public User findByUsername(String username)
      {
          if(username == null || username.trim().isEmpty() )
          {
              return null;
          }
          for(User u : ACCOUNTS)
          {
              if(u.getUsername().equalsIgnoreCase(username.trim()))
              {
                  return u;
              }
          }
          return null;
      }
      public List<User> findAll()
      {
          return new ArrayList<>(ACCOUNTS);
      }
      public List<User> findStaffAccount()
      {
          List <User> staff=new ArrayList<>();
          for(User u : ACCOUNTS)
          {
              if("Staff".equals(u.getRole()))
              {
                  staff.add(u);
              }
          }
         return staff;
      }
      public boolean addUser(User u)
      {
       if(u == null || u.getUsername()==null||u.getUsername().trim().isEmpty()||u.getFullName()== null||u.getFullName().trim().isEmpty()|| u.getContactNumber()==null||u.getContactNumber().trim().isEmpty()|| u.getPassword()== null|| u.getPassword().trim().isEmpty()|| u.getRole()== null || u.getRole().trim().isEmpty())  
           return false;
       if(findByUsername(u.getUsername())!= null)
           return false;
       u.setUserId(nextUserId++);
       u.setIsActive(true);
       ACCOUNTS.add(u);
       return true;
      }
      public boolean addStaff(User u)
      {
          if(u==null||!"Receptionist".equals(u.getRole()))
              return false;
          return addUser(u);
      }
      public boolean updateUser(User u)
      {
          if(u == null || u.getUsername()==null||u.getUsername().trim().isEmpty()||u.getFullName()== null||u.getFullName().trim().isEmpty()|| u.getContactNumber()==null||u.getContactNumber().trim().isEmpty()|| u.getPassword()== null|| u.getPassword().trim().isEmpty()|| u.getRole()== null || u.getRole().trim().isEmpty())  
           return false;
          User exist= findByUserId(u.getUserId());
          if(exist==null)
              return false;
          exist.setContactNumber(u.getContactNumber());
          exist.setFullName(u.getFullName());
          exist.setIsActive(u.getIsActive());
          return true;
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
      public boolean deleteAccount(int userId)  
        {
            User u = findByUserId(userId);
            if(u==null)
                return false;
            if("Admin".equals(u.getRole()))
                return false;
            PatientDao pd = new PatientDao();
            for(Patient p:pd.findAll())
            {
                if(p.getRegisteredBy()== userId)
                    return false;
            }
            DoctorDao dd = new DoctorDao();
            for(Doctor d: dd.findAll())
            {
                if(d.getUserId()==userId)
                    return false;
            }
            return ACCOUNTS.remove(u);
        }
    }
    

