package com.whitneygoodey.termtracker.Database;

import android.app.Application;

import com.whitneygoodey.termtracker.DAO.AssessmentDao;
import com.whitneygoodey.termtracker.DAO.CourseDao;
import com.whitneygoodey.termtracker.DAO.TermDao;
import com.whitneygoodey.termtracker.Entities.Assessment;
import com.whitneygoodey.termtracker.Entities.Course;
import com.whitneygoodey.termtracker.Entities.Term;

import java.util.ArrayList;
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

    public Term getTerm(int termID) {
        for (Term term : getAllTerms()) {
            if (termID == term.getID()) {
                return term;
            }
        }
        return null;
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

    public List<Course> getTermCourses(int termID) {
        List<Course> termCourses = new ArrayList<>();
        for (Course course : getAllCourses()) {
            if (termID == course.getTermID()) {
                termCourses.add(course);
            }
        }
        return termCourses;
    }

    public Course getCourse(int courseID) {
        for (Course course : getAllCourses()) {
            if(courseID == course.getID()) {
                return course;
            }
        }
        return null;
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

    public List<Assessment> getCourseAssessments(int courseID) {
        List <Assessment> courseAssessments = new ArrayList<>();
        for (Assessment assessment : getAllAssessments()) {
            if (courseID == assessment.getCourseID()) {
                courseAssessments.add(assessment);
            }
        }
        return courseAssessments;
    }

    public Assessment getAssessment(int assessmentID) {
        for (Assessment assessment : getAllAssessments()) {
            if(assessmentID == assessment.getID()) {
                return assessment;
            }
        }
        return null;
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

    public void delete(Course course) {
        databaseExecutor.execute( () -> courseDao.delete(course));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void delete(Assessment assessment) {
        databaseExecutor.execute( () -> assessmentDao.delete(assessment));
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

    public void update(Course course) {
        databaseExecutor.execute( () -> courseDao.update(course));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void update(Assessment assessment) {
        databaseExecutor.execute( () -> assessmentDao.update(assessment));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void deleteAllTerms() {
        databaseExecutor.execute( () -> termDao.deleteAllTerms());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void deleteAllCourses() {
        databaseExecutor.execute( () -> courseDao.deleteAllCourses());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void deleteAllAssessments() {
        databaseExecutor.execute( () -> assessmentDao.deleteAllAssessments());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
