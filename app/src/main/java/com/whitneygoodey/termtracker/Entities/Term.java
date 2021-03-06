package com.whitneygoodey.termtracker.Entities;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "term_table")
public class Term {

    @PrimaryKey(autoGenerate = true)
    private int ID;
    private String title;
    private String startDate;
    private String endDate;
    private int ownerID;

    @Ignore
    public Term(int ID, int ownerID, String title, String startDate, String endDate) {
        this.ID = ID;
        this.ownerID = ownerID;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
    }


    public Term(int ownerID, String title, String startDate, String endDate) {
        this.ownerID = ownerID;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return "Term{" +
                "ID='" + ID + '\'' +
                "Title='" + title + '\'' +
                "Start Date='" + startDate + '\'' +
                "End Date='" + endDate + '\'' +
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
}
