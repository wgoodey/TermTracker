package com.whitneygoodey.termtracker.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.whitneygoodey.termtracker.Entities.Term;

import java.util.List;

@Dao
public interface TermDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Term term);

    @Update
    void update(Term term);

    @Delete
    void delete(Term term);

    @Query("SELECT * FROM term_table ORDER BY ID ASC")
    List<Term> getAllTerms();

    @Query("SELECT * FROM term_table WHERE ownerID = :owner ORDER BY ID ASC")
    List<Term> getUserTerms(int owner);

    @Query("SELECT * FROM term_table WHERE ID = :termID")
    Term getTerm(int termID);
}