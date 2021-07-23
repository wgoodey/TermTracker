package com.whitneygoodey.termtracker.Database;

import android.app.Application;

import com.whitneygoodey.termtracker.DAO.AssessmentDAO;
import com.whitneygoodey.termtracker.DAO.CourseDAO;
import com.whitneygoodey.termtracker.DAO.TermDAO;
import com.whitneygoodey.termtracker.Entities.Assessment;
import com.whitneygoodey.termtracker.Entities.Course;
import com.whitneygoodey.termtracker.Entities.Term;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Repository {
    private TermDAO termDAO;
    private CourseDAO courseDAO;
    private AssessmentDAO assessmentDAO;
    private List<Term> allTerms;
    private static final int THREADCOUNT = 4 ;
    static final ExecutorService databaseExecutor = Executors.newFixedThreadPool(THREADCOUNT);

    public Repository(Application application) {
        DbBuilder db = DbBuilder.getDatabase(application);
        termDAO = db.termDAO();
        courseDAO = db.courseDAO();
        assessmentDAO = db.assessmentDAO();
    }

    public List<Term> getAllTerms() {
        databaseExecutor.execute( () -> allTerms = termDAO.getAllTerms());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return allTerms;
    }

    public void insert(Term term) {
        databaseExecutor.execute(() -> termDAO.insert(term));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void insert(Course course) {
        databaseExecutor.execute( () -> courseDAO.insert(course));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void insert(Assessment assessment) {
        databaseExecutor.execute( () -> assessmentDAO.insert(assessment));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void delete(Term term) {
        databaseExecutor.execute( () -> termDAO.delete(term));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void update(Term term) {
        databaseExecutor.execute( () -> termDAO.update(term));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //TODO complete for Course and Assessment;
}
