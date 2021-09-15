package com.whitneygoodey.termtracker.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.whitneygoodey.termtracker.Entities.Assessment;

import java.util.List;

@Dao
public interface AssessmentDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Assessment assessment);

    @Update
    void update(Assessment assessment);

    @Delete
    void delete(Assessment assessment);

    @Query("SELECT * FROM assessment_table ORDER BY ID ASC")
    List<Assessment> getAllAssessments();

    @Query("SELECT * FROM assessment_table WHERE ownerID = :owner ORDER BY ID ASC")
    List<Assessment> getUserAssessments(int owner);

    @Query("SELECT * FROM assessment_table WHERE courseID = :course ORDER BY ID ASC")
    List<Assessment> getTermCourses(int course);

    @Query("SELECT * FROM assessment_table WHERE ID = :assessmentID")
    Assessment getAssessment(int assessmentID);
}