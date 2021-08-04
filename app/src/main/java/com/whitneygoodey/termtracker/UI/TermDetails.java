package com.whitneygoodey.termtracker.UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.whitneygoodey.termtracker.Database.Repository;
import com.whitneygoodey.termtracker.Entities.Course;
import com.whitneygoodey.termtracker.Entities.Term;
import com.whitneygoodey.termtracker.R;

import java.util.List;
import java.util.Objects;

public class TermDetails extends AppCompatActivity {

    private Repository repository;
    private int termID;
    private int courseID;
    private Term term;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_details);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        try {
            termID = getIntent().getIntExtra("termID", -1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //get term and courses from the database
        repository = new Repository(getApplication());
        setTermDetailsOnScreen();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;

            case R.id.refresh:
                setTermDetailsOnScreen();
                return true;

            case R.id.notify:
                //TODO: add code for notifications
                return true;

            case R.id.share:
                //TODO: add code for sharing
                return true;

            case R.id.edit:
                editTerm(termID);
                return true;

            case R.id.delete:
                //TODO: add code for deleting
                repository.delete(term);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setTermDetailsOnScreen() {
        //get term from the database
        term = repository.getTerm(termID);

        //set term info on screen
        TextView title = findViewById(R.id.termTitle);
        TextView start = findViewById(R.id.textStartDate);
        TextView end = findViewById(R.id.textEndDate);
        title.setText(term.getTitle());
        start.setText(term.getStartDate());
        end.setText(term.getEndDate());

        //set RecyclerView and CourseAdapter
        List<Course> courseList = repository.getTermCourses(termID);
        RecyclerView recyclerView = findViewById(R.id.courseRecyclerView);
        final CourseAdapter courseAdapter = new CourseAdapter(this);
        recyclerView.setAdapter(courseAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        courseAdapter.setCourseList(courseList);
    }

    public void addCourse(View view) {
        Intent intent = new Intent(TermDetails.this, AddCourse.class);
        intent.putExtra("termID", termID);
        startActivity(intent);
    }

    public void editTerm(int termID) {
        Intent intent = new Intent(TermDetails.this, AddTerm.class);
        intent.putExtra("termID", termID);
        startActivity(intent);
    }


}