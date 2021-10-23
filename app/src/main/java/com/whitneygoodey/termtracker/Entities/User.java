package com.whitneygoodey.termtracker.Entities;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_table")
public class User {

    @PrimaryKey(autoGenerate = true)
    int ID;
    private String email;

    @Ignore
    public User(int ID, String email) {
        this.ID = ID;
        this.email = email;
    }

    public User(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "User{" +
                "ID='" + ID + '\'' +
                "email='" + email + '\'' +
                '}';
    }

    public int getID() {
        return ID;
    }

    public String getEmail() {
        return email;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
