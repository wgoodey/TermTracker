package com.whitneygoodey.termtracker.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.whitneygoodey.termtracker.DAO.AssessmentDao;
import com.whitneygoodey.termtracker.DAO.CourseDao;
import com.whitneygoodey.termtracker.DAO.TermDao;
import com.whitneygoodey.termtracker.Entities.Assessment;
import com.whitneygoodey.termtracker.Entities.Course;
import com.whitneygoodey.termtracker.Entities.Term;

@Database(
        entities = {Term.class, Course.class, Assessment.class}, version = 2, exportSchema = false)
public abstract class DbBuilder extends RoomDatabase {

    public abstract TermDao termDao();
    public abstract CourseDao courseDao();
    public abstract AssessmentDao assessmentDao();

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
