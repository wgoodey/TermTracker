package com.whitneygoodey.termtracker.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.whitneygoodey.termtracker.Entities.Course;

import java.util.List;

@Dao
public interface CourseDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Course course);

    @Update
    void update(Course course);

    @Delete
    void delete(Course course);

    @Query("SELECT * FROM course_table ORDER BY ID ASC")
    List<Course> getAllCourses();

    @Query("SELECT * FROM course_table WHERE ownerID = :owner ORDER BY ID ASC")
    List<Course> getUserCourses(int owner);

    @Query("SELECT * FROM course_table WHERE termID = :term ORDER BY ID ASC")
    List<Course> getTermCourses(int term);

    @Query("SELECT * FROM course_table WHERE ID = :courseID")
    Course getCourse(int courseID);

}