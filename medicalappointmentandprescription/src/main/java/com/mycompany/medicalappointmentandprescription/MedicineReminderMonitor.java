/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.medicalappointmentandprescription;
import com.mycompany.medicalappointmentandprescription.dao.MedicineReminderDao;
import com.mycompany.medicalappointmentandprescription.models.MedicineReminder;
import javax.swing.SwingUtilities;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import java.util.concurrent.TimeUnit;
/**
 *
 * @author ranbi
 */
public class MedicineReminderMonitor {
    private final MedicineReminderDao mrd = new MedicineReminderDao() ;
    private final ScheduledExecutorService scheduler=Executors.newSingleThreadScheduledExecutor(r -> {Thread th = new Thread(r,"MedicineReminderMonitor");th.setDaemon(true);return th;});
    private final Set<Integer> display=new HashSet<>();
    private Consumer<MedicineReminder> reminder;
    private boolean running,stopped;
    public MedicineReminderMonitor()
    {
        
    }
    public synchronized void setReminderCallback(Consumer<MedicineReminder>callback)
    {
        reminder=callback;
    }
    public synchronized void start()
    {
        if(running||stopped)
            return;
        running=true;
        scheduler.scheduleAtFixedRate(this::checkRemindersSafely, 0,15, TimeUnit.SECONDS);
    }
    private void checkRemindersSafely()
    {
        try{
            checkReminders();
        }
        catch(RuntimeException ex)
        {
         System.err.println("Error checking medicine reminders "+ex.getMessage());
         ex.printStackTrace();
        }
    }
    private void checkReminders()
    {
        Consumer<MedicineReminder>callback;
        synchronized(this)
                {
                    if(!running||reminder==null)
                        return;
                    callback=reminder;
                }
        for(MedicineReminder mr:mrd.findDueReminders())
        {
            if(mr==null||mr.getReminderId()<=0)
                continue;
            synchronized(display){
                if(!display.add(mr.getReminderId()))
                    continue;
            }
            SwingUtilities.invokeLater(()->{try{synchronized(MedicineReminderMonitor.this){if(!running){synchronized(display){display.remove(mr.getReminderId());}return;}}callback.accept(mr);}catch(RuntimeException ex){synchronized(display){display.remove(mr.getReminderId());}ex.printStackTrace();}});         
            }
        }
    public void reminderHandled(int reminderId)
    {
        synchronized(display){
            display.remove(reminderId);
        }
    }
    public synchronized void stop()
    {
        if(stopped)
            return;
        running=false;
        stopped=true;
        scheduler.shutdownNow();
        synchronized(display)
        {
            display.clear();
        }
     }
    }
