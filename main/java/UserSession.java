/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author arant
 */
public class UserSession {
    private String email;
    private String fullName;
    private String contactNumber;
    private int studentID;

    public UserSession(String email, String fullName, String contactNumber, int studentID) {
        this.email = email;
        this.fullName = fullName;
        this.contactNumber = contactNumber;
        this.studentID = studentID;
    }

    public String getEmail() { return email; }
    public String getFullName() { return fullName; }
    public String getContactNumber() { return contactNumber; }
    public int getStudentID() { return studentID; }
}
