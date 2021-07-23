package com.whitneygoodey.termtracker.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.whitneygoodey.termtracker.DAO.AssessmentDAO;
import com.whitneygoodey.termtracker.DAO.CourseDAO;
import com.whitneygoodey.termtracker.DAO.TermDAO;
import com.whitneygoodey.termtracker.Entities.Assessment;
import com.whitneygoodey.termtracker.Entities.Course;
import com.whitneygoodey.termtracker.Entities.Term;

@Database(
        entities = {Term.class, Course.class, Assessment.class}, version = 1, exportSchema = false)
public abstract class DbBuilder extends RoomDatabase {

    public abstract TermDAO termDAO();
    public abstract CourseDAO courseDAO();
    public abstract AssessmentDAO assessmentDAO();

    private static volatile DbBuilder INSTANCE;

    static DbBuilder getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (DbBuilder.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), DbBuilder.class, "TermTracker.db")
                            .fallbackToDestructiveMigrationOnDowngrade()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
