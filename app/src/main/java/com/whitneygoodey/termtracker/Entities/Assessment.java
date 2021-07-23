package com.whitneygoodey.termtracker.Entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "assessment_table")
public class Assessment {

    @PrimaryKey(autoGenerate = true)
    private int ID;
    private int courseID;
    private String title;
    private String startDate;
    private String endDate;
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

    public Assessment(int ID, int courseID, String title, String startDate, String endDate, Type type) {
        this.ID = ID;
        this.courseID = courseID;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.type = type;
    }

    @Override
    public String toString() {
        return "Assessment{" +
                "ID='" + ID + '\'' +
                "Title='" + title + '\'' +
                "Start Date='" + startDate + '\'' +
                "End Date='" + endDate + '\'' +
                "type='" + type + '\'' +
                '}';
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
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

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
