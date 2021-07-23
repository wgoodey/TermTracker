package com.whitneygoodey.termtracker.Entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "course_table")
public class Course {
    @PrimaryKey(autoGenerate = true)
    private int ID;
    private int term;
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
        ENROLLED("Enrolled"),
        PROGRESS("In Progress"),
        PLANNED("Planned"),
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


    public Course(int ID, int term, String title, Status status, String startDate, String endDate, String instructorName, String instructorEmail, String instructorPhone, String note) {
        this.ID = ID;
        this.term = term;
        this.title = title;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
        this.instructorName = instructorName;
        this.instructorEmail = instructorEmail;
        this.instructorPhone = instructorPhone;
        this.note = note;
    }

    @Override
    //TODO: experiment with status.toString()
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

    public int getTerm() {
        return term;
    }

    public void setTerm(int term) {
        this.term = term;
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
