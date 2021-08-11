package com.whitneygoodey.termtracker.UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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
        setCourseDetailsOnScreen();
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
                setCourseDetailsOnScreen();
                return true;

            case R.id.notify:
                //TODO: add code for notifications
                return true;

            case R.id.share:
                //get course title with details
                StringBuilder courseDetails = new StringBuilder();
                courseDetails.append(course.getTitle());
                //TODO: fill in the rest of the details
                courseDetails.append("\nStatus: ").append(course.getStatus())
                        .append("\nStart Date: ").append(course.getStartDate())
                        .append("\nEnd Date: ").append(course.getEndDate())
                        .append("\n\nInstructor")
                        .append("\nName: ").append(course.getInstructorName())
                        .append("\nEmail: ").append(course.getInstructorEmail())
                        .append("\nPhone: ").append(course.getInstructorPhone());
                if (course.getNote().length() > 0) {
                    courseDetails.append("\n\nNote: ")
                            .append(course.getNote());
                }
                courseDetails.append("\n\nAssessments:\n");
                List<Assessment> courseAssessments = repository.getCourseAssessments(courseID);
                if (courseAssessments.size() > 0) {
                    //get assessment details
                    for (Assessment assessment : repository.getCourseAssessments(courseID)) {
                        courseDetails.append(assessment.getTitle());
                        if (assessment.getType() == Assessment.Type.OBJECTIVE) {
                            courseDetails.append(" (OA)");
                        } else {
                            courseDetails.append(" (PA)");

                        }

                        //TODO: add check for identical start and end dates and if so, only append one
                        courseDetails.append("\nStart Date: ").append(assessment.getStartDate())
                                .append("\nEnd Date: ").append(assessment.getEndDate()).append("\n");
                    }
                }else {
                    courseDetails.append("None specified for this course.");
                }


                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, courseDetails.toString());
//                sendIntent.putExtra(Intent.EXTRA_TITLE, "Message Title");
                sendIntent.setType("text/plain");

                Intent shareIntent = Intent.createChooser(sendIntent, null);
                startActivity(shareIntent);
                return true;

            case R.id.edit:
                editCourse(courseID);
                return true;

            case R.id.delete:
                //if course has associated assessments, delete those.
                List<Assessment> allAssessments = repository.getAllAssessments();
                for (Assessment assessment : allAssessments) {
                    if (assessment.getCourseID() == courseID) {
                        repository.delete(assessment);
                    }
                }
                //delete course once its assessments are deleted
                repository.delete(course);
                String message = getApplicationContext().getString(R.string.course_deleted, course.getTitle());
                Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
                toast.show();
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setCourseDetailsOnScreen() {
        //get course from the database
        course = repository.getCourse(courseID);

        //get views
        TextView title = findViewById(R.id.editCourseTitle);
        title.setText(course.getTitle());
        TextView status = findViewById(R.id.statusSpinner);
        status.setText(course.getStatus().toString());
        TextView start = findViewById(R.id.textStartDate);
        start.setText(course.getStartDate());
        TextView end = findViewById(R.id.textEndDate);
        end.setText(course.getEndDate());

        //set course info on screen
        TextView name = findViewById(R.id.editName);
        name.setText(course.getInstructorName());
        TextView email = findViewById(R.id.editEmail);
        email.setText(course.getInstructorEmail());
        TextView phone = findViewById(R.id.editPhone);
        phone.setText(course.getInstructorPhone());
        TextView note = findViewById(R.id.textNote);
        note.setText(course.getNote());

        //set RecyclerView and AssessmentAdapter
        List<Assessment> assessmentList = repository.getCourseAssessments(courseID);
        RecyclerView recyclerView = findViewById(R.id.assessmentsRecyclerView);
        final AssessmentAdapter assessmentAdapter = new AssessmentAdapter(this);
        recyclerView.setAdapter(assessmentAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        assessmentAdapter.setAssessmentList(assessmentList);
    }


    public void addAssessment(View view) {
        Intent intent = new Intent(CourseDetails.this, AddAssessment.class);
        intent.putExtra("courseID", courseID);
        startActivity(intent);
    }

    public void editCourse(int courseID) {
        Intent intent = new Intent(CourseDetails.this, AddCourse.class);
        intent.putExtra("courseID", courseID);
        startActivity(intent);
    }

}