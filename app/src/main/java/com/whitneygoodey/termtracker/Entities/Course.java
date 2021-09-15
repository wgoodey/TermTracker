package com.whitneygoodey.termtracker.Entities;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "course_table")
public class Course {
    @PrimaryKey(autoGenerate = true)
    private int ID;
    private int ownerID;
    private int termID;
    private int credits;
    private String title;
    private Status status;
    private String startDate;
    private String endDate;
    private String instructorName;
    private String instructorEmail;
    private String instructorPhone;
    //TODO: convert this to a list of notes?
    private String note;

    public enum Status {
        PLANNED("Planned"),
        ENROLLED("Enrolled"),
        PROGRESS("In Progress"),
        COMPLETED("Completed"),
        DROPPED("Dropped");

        private final String stringValue;

        Status(String s) {
            this.stringValue = s;
        }

        @Override
        public String toString() {
            return stringValue;
        }
    }

    @Ignore
    public Course(int ID, int ownerID, int termID, String title, Status status, int credits, String startDate, String endDate, String instructorName, String instructorEmail, String instructorPhone, String note) {
        this.ID = ID;
        this.ownerID = ownerID;
        this.termID = termID;
        this.title = title;
        this.status = status;
        this.credits = credits;
        this.startDate = startDate;
        this.endDate = endDate;
        this.instructorName = instructorName;
        this.instructorEmail = instructorEmail;
        this.instructorPhone = instructorPhone;
        this.note = note;
    }

    public Course(int ownerID, int termID, String title, Status status, int credits, String startDate, String endDate, String instructorName, String instructorEmail, String instructorPhone, String note) {
        this.ownerID = ownerID;
        this.termID = termID;
        this.title = title;
        this.status = status;
        this.credits = credits;
        this.startDate = startDate;
        this.endDate = endDate;
        this.instructorName = instructorName;
        this.instructorEmail = instructorEmail;
        this.instructorPhone = instructorPhone;
        this.note = note;
    }

    @Override
    public String toString() {
        return "Course{" +
                "ID='" + ID + '\'' +
                "Title='" + title + '\'' +
                "Status='" + status + '\'' +
                "Start Date='" + startDate + '\'' +
                "Instructor Name='" + instructorName + '\'' +
                "Instructor Email='" + instructorEmail + '\'' +
                "Instructor Phone='" + instructorPhone + '\'' +
                "Note='" + note + '\'' +
                '}';
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(int ownerID) {
        this.ownerID = ownerID;
    }

    public int getTermID() {
        return termID;
    }

    public void setTermID(int termID) {
        this.termID = termID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public String getInstructorName() {
        return instructorName;
    }

    public void setInstructorName(String instructorName) {
        this.instructorName = instructorName;
    }

    public String getInstructorEmail() {
        return instructorEmail;
    }

    public void setInstructorEmail(String instructorEmail) {
        this.instructorEmail = instructorEmail;
    }

    public String getInstructorPhone() {
        return instructorPhone;
    }

    public void setInstructorPhone(String instructorPhone) {
        this.instructorPhone = instructorPhone;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }


}
