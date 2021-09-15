package com.whitneygoodey.termtracker.Database;

import android.app.Application;

import com.whitneygoodey.termtracker.DAO.AssessmentDao;
import com.whitneygoodey.termtracker.DAO.CourseDao;
import com.whitneygoodey.termtracker.DAO.TermDao;
import com.whitneygoodey.termtracker.DAO.UserDao;
import com.whitneygoodey.termtracker.Entities.Assessment;
import com.whitneygoodey.termtracker.Entities.Course;
import com.whitneygoodey.termtracker.Entities.Term;
import com.whitneygoodey.termtracker.Entities.User;
import com.whitneygoodey.termtracker.UI.MainActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Repository {
    private final TermDao termDao;
    private final CourseDao courseDao;
    private final AssessmentDao assessmentDao;
    private final UserDao userDao;
    private List<Term> allTerms;
    private List<Course> allCourses;
    private List<Assessment> allAssessments;
    private List<User> allUsers;
    private static final int THREAD_COUNT = 4 ;
    static final ExecutorService databaseExecutor = Executors.newFixedThreadPool(THREAD_COUNT);

    public Repository(Application application) {
        DbBuilder db = DbBuilder.getDatabase(application);
        termDao = db.termDao();
        courseDao = db.courseDao();
        assessmentDao = db.assessmentDao();
        userDao = db.userDao();
    }



    //Terms
    public List<Term> getAllTerms(int ownerID) {
        if (MainActivity.getCurrentUserID() == MainActivity.ADMIN_ID) {
            databaseExecutor.execute( () -> allTerms = termDao.getAllTerms());
        } else {
            databaseExecutor.execute( () -> allTerms = termDao.getUserTerms(ownerID));
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return allTerms;
    }

    public Term getTerm(int termID) {
        for (Term term : getAllTerms(MainActivity.getCurrentUserID())) {
            if (termID == term.getID()) {
                return term;
            }
        }
        return null;
    }

    //TODO: figure out how to retrieve single term/course/assessment via DAO query
//    public Term getTerm(int termID, int ownerID) {
//        for (Term term : getAllTerms(ownerID)) {
//            if (termID == term.getID()) {
//                return term;
//            }
//        }
//        return null;
//    }

    public void insert(Term term) {
        databaseExecutor.execute(() -> termDao.insert(term));
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

    public void delete(Term term) {
        databaseExecutor.execute( () -> termDao.delete(term));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



    //Courses
    public List<Course> getAllCourses(int ownerID) {
        if (MainActivity.getCurrentUserID() == MainActivity.ADMIN_ID) {
            databaseExecutor.execute( () -> allCourses = courseDao.getAllCourses());
        } else {
            databaseExecutor.execute( () -> allCourses = courseDao.getUserCourses(ownerID));
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return allCourses;
    }

    public List<Course> getTermCourses(int termID) {
        databaseExecutor.execute( () -> allCourses = courseDao.getTermCourses(termID));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return allCourses;
    }

    public Course getCourse(int courseID) {
        for (Course course : getAllCourses(MainActivity.getCurrentUserID())) {
            if(courseID == course.getID()) {
                return course;
            }
        }
        return null;
    }

    public void insert(Course course) {
        databaseExecutor.execute( () -> courseDao.insert(course));
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

    public void delete(Course course) {
        databaseExecutor.execute( () -> courseDao.delete(course));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



    //Assessments
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

    public void insert(Assessment assessment) {
        databaseExecutor.execute( () -> assessmentDao.insert(assessment));
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

    public void delete(Assessment assessment) {
        databaseExecutor.execute( () -> assessmentDao.delete(assessment));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



    //Users
    public List<User> getAllUsers() {
        databaseExecutor.execute( () -> allUsers = userDao.getAllUsers());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return allUsers;
    }

    public void insert(User user) {
        databaseExecutor.execute( () -> userDao.insert(user));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void update(User user) {
        databaseExecutor.execute( () -> userDao.update(user));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void delete(User user) {
        databaseExecutor.execute( () -> userDao.delete(user));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}