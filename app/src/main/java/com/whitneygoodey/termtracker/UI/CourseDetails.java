package com.whitneygoodey.termtracker.UI;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
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

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;

public class CourseDetails extends AppCompatActivity {

    private Repository repository;
    private Course course;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_details);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        repository = new Repository(getApplication());

        try {
            int courseID = getIntent().getIntExtra("courseID", -1);
            course = repository.getCourse(courseID);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //get course and assessments from the database
        setCourseDetailsOnScreen(course);
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
                course = repository.getCourse(course.getID());
                setCourseDetailsOnScreen(course);
                return true;

            case R.id.notify:
                //create notification contents
                String startContent = course.getTitle() + " is starting today.";
                String endContent = course.getTitle() + " is ending today.";
                String content = course.getTitle() + " is today.";
                String toastMessage = course.getTitle() + " notification(s) set.";

                //get dates from term and convert to ZonedDateTime
                ZonedDateTime start = MainActivity.getZonedDateTime(course.getStartDate());
                ZonedDateTime end = MainActivity.getZonedDateTime(course.getEndDate());
                Long startTrigger = start.toInstant().toEpochMilli();
                Long endTrigger = end.toInstant().toEpochMilli();

                //set flags to true if the dates are in the future
//                ZonedDateTime currentDate = ZonedDateTime.now().withHour(00).withMinute(0).withSecond(0).withNano(0).minusNanos(1);
                boolean startFuture = start.isAfter(ZonedDateTime.now());
                boolean endFuture = end.isAfter(ZonedDateTime.now());

                //set notifications only for events that are in the future
                if (startFuture) {
                    if (start.equals(end)) {
                        //register a single notification for both
                        createNotification(content, startTrigger);
                    } else {
                        //register start notification
                        createNotification(startContent, startTrigger);
                        createNotification(endContent, endTrigger);
                    }
                } else if (endFuture) {
                    //register end notification
                    createNotification(endContent, endTrigger);
                } else {
                    //warn that no notifications were made
                    Toast.makeText(getApplicationContext(), "Notifications cannot be made for past events.", Toast.LENGTH_LONG).show();
                    break;
                }

                Toast.makeText(getApplicationContext(), toastMessage, Toast.LENGTH_LONG).show();
                return true;

            case R.id.share:
                //get course title with details
                StringBuilder courseDetails = new StringBuilder();
                courseDetails.append(course.getTitle());
                //fill in the rest of the details
                courseDetails.append("\nStatus: ").append(course.getStatus())
                        .append("\nCredits: ").append(course.getCredits())
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
                List<Assessment> courseAssessments = repository.getCourseAssessments(course.getID());
                if (courseAssessments.size() > 0) {
                    //get assessment details
                    for (Assessment assessment : repository.getCourseAssessments(course.getID())) {
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
                editCourse(course);
                return true;

            case R.id.search:
                Intent intent = new Intent(CourseDetails.this, Search.class);
                intent.putExtra("searchType", "assessment");
                startActivity(intent);
                return true;

            case R.id.delete:
                //if course has associated assessments, delete those.
                List<Assessment> allAssessments = repository.getAllAssessments(MainActivity.getCurrentUserID());
                for (Assessment assessment : allAssessments) {
                    if (assessment.getCourseID() == course.getID()) {
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

            case R.id.reports:
                Intent reportsIntent = new Intent(CourseDetails.this, Reports.class);
                startActivity(reportsIntent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setCourseDetailsOnScreen(Course course) {
        //get views and set text values
        //set course title, status, credits, start and end info on screen
        TextView title = findViewById(R.id.editCourseTitle);
        title.setText(course.getTitle());
        TextView status = findViewById(R.id.statusSpinner);
        status.setText(course.getStatus().toString());
        TextView credits = findViewById(R.id.textCredits);
        credits.setText(String.valueOf(course.getCredits()));
        TextView start = findViewById(R.id.textStartDate);
        start.setText(course.getStartDate());
        TextView end = findViewById(R.id.textEndDate);
        end.setText(course.getEndDate());
        TextView termTitle = findViewById(R.id.termTitle);
        termTitle.setText(getString(R.string.in_parentheses, repository.getTerm(course.getTermID()).getTitle()));


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
        List<Assessment> assessmentList = repository.getCourseAssessments(course.getID());
        RecyclerView recyclerView = findViewById(R.id.assessmentsRecyclerView);
        final AssessmentAdapter assessmentAdapter = new AssessmentAdapter(this);
        recyclerView.setAdapter(assessmentAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        assessmentAdapter.setAssessmentList(assessmentList);
    }


    public void addAssessment(View view) {
        Intent intent = new Intent(CourseDetails.this, AddAssessment.class);
        intent.putExtra("courseID", course.getID());
        startActivity(intent);
    }

    private void editCourse(Course course) {
        Intent intent = new Intent(CourseDetails.this, AddCourse.class);
        intent.putExtra("courseID", course.getID());
        intent.putExtra("termID", course.getTermID());
        startActivity(intent);
    }

    private void createNotification(String content, Long trigger) {
        Intent startIntent = new Intent(CourseDetails.this, MyReceiver.class);
        startIntent.putExtra("type", "Course");
        startIntent.putExtra("content", content);

        PendingIntent sender = PendingIntent.getBroadcast(CourseDetails.this, ++MainActivity.numAlert, startIntent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, trigger, sender);
    }

}