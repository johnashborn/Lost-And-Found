
import java.sql.Date;
import java.sql.Timestamp;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author arant
 */
public class ActivityLog {
    private String eventType;
    private String details;
    private String itemName;
    private String studentName;

   
    private final Timestamp eventDate;

public ActivityLog(String eventType, String details, Timestamp eventDate, String itemName, String studentName) {
    this.eventType = eventType;
    this.details = details;
    this.eventDate = eventDate;
    this.itemName = itemName;
    this.studentName = studentName;
}

   

    public String getEventType() { return eventType; }
    public String getDetails() { return details; }
     public Timestamp getEventDate() { return eventDate; }
    public String getItemName() { return itemName; }
    public String getStudentName() { return studentName; }
}