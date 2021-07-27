package com.whitneygoodey.termtracker.Database;

import android.app.Application;

import com.whitneygoodey.termtracker.DAO.AssessmentDao;
import com.whitneygoodey.termtracker.DAO.CourseDao;
import com.whitneygoodey.termtracker.DAO.TermDao;
import com.whitneygoodey.termtracker.Entities.Assessment;
import com.whitneygoodey.termtracker.Entities.Course;
import com.whitneygoodey.termtracker.Entities.Term;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Repository {
    private TermDao termDao;
    private CourseDao courseDao;
    private AssessmentDao assessmentDao;
    private List<Term> allTerms;
    private List<Course> allCourses;
    private List<Assessment> allAssessments;
    private static final int THREADCOUNT = 4 ;
    static final ExecutorService databaseExecutor = Executors.newFixedThreadPool(THREADCOUNT);

    public Repository(Application application) {
        DbBuilder db = DbBuilder.getDatabase(application);
        termDao = db.termDao();
        courseDao = db.courseDao();
        assessmentDao = db.assessmentDao();
    }

    public List<Term> getAllTerms() {
        databaseExecutor.execute( () -> allTerms = termDao.getAllTerms());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return allTerms;
    }

    public List<Course> getAllCourses() {
        databaseExecutor.execute( () -> allCourses = courseDao.getAllCourses());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return allCourses;
    }

    public List<Assessment> getAllAssessments() {
        databaseExecutor.execute( () -> allAssessments = assessmentDao.getAllAssessments());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return allAssessments;
    }

    public void insert(Term term) {
        databaseExecutor.execute(() -> termDao.insert(term));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void insert(Course course) {
        databaseExecutor.execute( () -> courseDao.insert(course));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void insert(Assessment assessment) {
        databaseExecutor.execute( () -> assessmentDao.insert(assessment));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void delete(Term term) {
        databaseExecutor.execute( () -> termDao.delete(term));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void update(Term term) {
        databaseExecutor.execute( () -> termDao.update(term));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //TODO complete for Course and Assessment;
}
