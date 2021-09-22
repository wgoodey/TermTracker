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
import com.whitneygoodey.termtracker.Entities.Course;
import com.whitneygoodey.termtracker.Entities.Term;
import com.whitneygoodey.termtracker.R;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;

public class TermDetails extends AppCompatActivity {

    private Repository repository;
    private Term term;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_details);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        repository = new Repository(getApplication());

        try {
            int termID = getIntent().getIntExtra("termID", -1);
            term = repository.getTerm(termID);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //get term and courses from the database
        setTermDetailsOnScreen(term);
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
                term = repository.getTerm(term.getID());
                setTermDetailsOnScreen(term);
                return true;

            case R.id.notify:
                //create notification contents
                String startContent = term.getTitle() + " is starting today.";
                String endContent = term.getTitle() + " is ending today.";
                String content = term.getTitle() + " is today.";
                String toastMessage = term.getTitle() + " notification(s) set.";

                //get dates from term and convert to ZonedDateTime
                ZonedDateTime start = MainActivity.getZonedDateTime(term.getStartDate());
                ZonedDateTime end = MainActivity.getZonedDateTime(term.getEndDate());
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
                //get term title with course titles and dates
                StringBuilder termDetails = new StringBuilder();
                termDetails.append(term.getTitle()).append("course list:\n");
                for (Course course : repository.getTermCourses(term.getID())) {
                    termDetails.append(course.getTitle())
                            .append(" (")
                            .append(getApplicationContext().getString(R.string.start_and_end_dates, course.getStartDate(), course.getEndDate()))
                            .append(")\n");
                }

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, termDetails.toString());
//                sendIntent.putExtra(Intent.EXTRA_TITLE, "Message Title");
                sendIntent.setType("text/plain");

                Intent shareIntent = Intent.createChooser(sendIntent, null);
                startActivity(shareIntent);
                return true;

            case R.id.edit:
                editTerm(term.getID());
                return true;

            case R.id.search:
                Intent intent = new Intent(TermDetails.this, Search.class);
                intent.putExtra("searchType", "course");
                startActivity(intent);
                return true;

            case R.id.delete:
                boolean flag = false;
                List<Course> allCourses = repository.getAllCourses(MainActivity.getCurrentUserID());

                for (Course course : allCourses) {
                    if (course.getTermID() == term.getID()) {
                        flag = true;
                        break;
                    }
                }

                if (flag) {
                    //display toast notifying user that term cannot be deleted
                    String message = getApplicationContext().getString(R.string.cannot_delete_term, term.getTitle());
                    Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
                    toast.show();

                    //TODO display confirmation dialog to delete a term with courses instead of just a warning toast?

                } else {
                    repository.delete(term);
                    String message = getApplicationContext().getString(R.string.term_deleted, term.getTitle());
                    Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
                    toast.show();
                    this.finish();
                    return true;
                }

                return false;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setTermDetailsOnScreen(Term term) {
        //set term info on screen
        TextView title = findViewById(R.id.termTitle);
        TextView start = findViewById(R.id.textStartDate);
        TextView end = findViewById(R.id.textEndDate);
        title.setText(term.getTitle());
        start.setText(term.getStartDate());
        end.setText(term.getEndDate());

        List<Course> courseList = repository.getTermCourses(term.getID());
        //count credits and set on screen
        int totalCredits = 0;
        int completedCredits = 0;
        for (Course course : courseList) {
            totalCredits += course.getCredits();
            if (course.getStatus().equals(Course.Status.COMPLETED)) {
                completedCredits+=course.getCredits();
            }
        }
        TextView credits = findViewById(R.id.creditsMessage);
        String creditsMessage = getApplicationContext().getString(R.string.completedCredits, completedCredits, totalCredits);
        credits.setText(creditsMessage);

        //set RecyclerView and CourseAdapter
        RecyclerView recyclerView = findViewById(R.id.courseRecyclerView);
        final CourseAdapter courseAdapter = new CourseAdapter(this);
        recyclerView.setAdapter(courseAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        courseAdapter.setCourseList(courseList);
    }

    public void addCourse(View view) {
        Intent intent = new Intent(TermDetails.this, AddCourse.class);
        intent.putExtra("termID", term.getID());
        startActivity(intent);
    }

    private void editTerm(int termID) {
        Intent intent = new Intent(TermDetails.this, AddTerm.class);
        intent.putExtra("termID", termID);
        startActivity(intent);
    }

    private void createNotification(String content, Long trigger) {
        Intent startIntent = new Intent(TermDetails.this, MyReceiver.class);
        startIntent.putExtra("type", "Term");
        startIntent.putExtra("content", content);

        PendingIntent sender = PendingIntent.getBroadcast(TermDetails.this, ++MainActivity.numAlert, startIntent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, trigger, sender);
    }
}