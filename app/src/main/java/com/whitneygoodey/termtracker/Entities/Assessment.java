package com.whitneygoodey.termtracker.Entities;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "assessment_table")
public class Assessment {

    @PrimaryKey(autoGenerate = true)
    private int ID;
    private int ownerID;
    private int courseID;
    private String title;
    private String startDate;
    private String endDate;
    private String description;
    private Type type;

    public enum Type {
        PERFORMANCE("Performance Assessment"),
        OBJECTIVE("Objective Assessment");

        private final String stringValue;

        Type(String s) {
            this.stringValue = s;
        }

        @Override
        public String toString() {
            return stringValue;
        }
    }

    @Ignore
    public Assessment(int ID, int ownerID, int courseID, String title, String startDate, String endDate, String description, Type type) {
        this.ID = ID;
        this.ownerID = ownerID;
        this.courseID = courseID;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
        this.type = type;
    }

    public Assessment(int ownerID, int courseID, String title, String startDate, String endDate, String description, Type type) {
        this.ownerID = ownerID;
        this.courseID = courseID;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
        this.type = type;
    }

    @Override
    public String toString() {
        return "Assessment{" +
                "ID='" + ID + '\'' +
                "Title='" + title + '\'' +
                "Start Date='" + startDate + '\'' +
                "End Date='" + endDate + '\'' +
                "Description='" + description + '\'' +
                "type='" + type + '\'' +
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

    public int getCourseID() {
        return courseID;
    }

    public void setCourseID(int courseID) {
        this.courseID = courseID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
