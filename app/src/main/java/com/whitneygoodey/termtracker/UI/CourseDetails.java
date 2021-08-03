package com.whitneygoodey.termtracker.UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.whitneygoodey.termtracker.Database.Repository;
import com.whitneygoodey.termtracker.Entities.Assessment;
import com.whitneygoodey.termtracker.Entities.Course;
import com.whitneygoodey.termtracker.R;

import java.util.List;
import java.util.Objects;

public class CourseDetails extends AppCompatActivity {

    private Repository repository;
    private int courseID;
    private int assessmentID;
    private Course course;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_details);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        try {
            courseID = getIntent().getIntExtra("courseID", -1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //get course and assessments from the database
        repository = new Repository(getApplication());
        course = repository.getCourse(courseID);
        List<Assessment> assessmentList = repository.getCourseAssessments(courseID);

        //set course info on screen
        TextView title = findViewById(R.id.editCourseTitle);
        title.setText(course.getTitle());
        TextView status = findViewById(R.id.statusSpinner);
        status.setText(course.getStatus().toString());
        TextView start = findViewById(R.id.textStartDate);
        start.setText(course.getStartDate());
        TextView end = findViewById(R.id.textEndDate);
        end.setText(course.getEndDate());

        TextView name = findViewById(R.id.editName);
        name.setText(course.getInstructorName());
        TextView email = findViewById(R.id.editEmail);
        email.setText(course.getInstructorEmail());
        TextView phone = findViewById(R.id.editPhone);
        phone.setText(course.getInstructorPhone());
        TextView note = findViewById(R.id.textNote);
        note.setText(course.getNote());

        //set RecyclerView and AssessmentAdapter
        RecyclerView recyclerView = findViewById(R.id.assessmentsRecyclerView);
        final AssessmentAdapter assessmentAdapter = new AssessmentAdapter(this);
        recyclerView.setAdapter(assessmentAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        assessmentAdapter.setAssessmentList(assessmentList);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void addAssessment(View view) {
        Intent intent = new Intent(CourseDetails.this, AddAssessment.class);
        intent.putExtra("courseID", courseID);
        startActivity(intent);
    }

    public void editAssessment(View view) {
        Intent intent = new Intent(CourseDetails.this, AddAssessment.class);
        intent.putExtra("assessmentID", assessmentID);
        startActivity(intent);
    }

}