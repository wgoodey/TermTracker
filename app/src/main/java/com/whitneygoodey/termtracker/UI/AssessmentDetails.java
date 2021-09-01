package com.whitneygoodey.termtracker.UI;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.whitneygoodey.termtracker.Database.Repository;
import com.whitneygoodey.termtracker.Entities.Assessment;
import com.whitneygoodey.termtracker.R;

import java.time.ZonedDateTime;
import java.util.Objects;

public class AssessmentDetails extends AppCompatActivity {

    private Repository repository;
    private Assessment assessment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_details);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        repository = new Repository(getApplication());

        try {
            int assessmentID = getIntent().getIntExtra("assessmentID", -1);
            assessment = repository.getAssessment(assessmentID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        setAssessmentDetailsOnScreen(assessment);
    }

    private void setAssessmentDetailsOnScreen(Assessment assessment) {
        //get views
        TextView title = findViewById(R.id.assessmentTitle);
        TextView type = findViewById(R.id.assessmentType);
        TextView start = findViewById(R.id.textStartDate);
        TextView end = findViewById(R.id.textEndDate);
        TextView description = findViewById(R.id.textDescription);

        //set assessment info on screen
        title.setText(assessment.getTitle());
        type.setText(assessment.getType().toString());
        start.setText(assessment.getStartDate());
        end.setText(assessment.getEndDate());
        description.setText(assessment.getDescription());
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
                assessment = repository.getAssessment(assessment.getID());
                setAssessmentDetailsOnScreen(assessment);
                return true;

            case R.id.notify:
                //create notification contents
                String startContent = assessment.getTitle() + " is starting today.";
                String endContent = assessment.getTitle() + " is ending today.";
                String content = assessment.getTitle() + " is today.";
                String toastMessage = assessment.getTitle() + " notification(s) set.";

                //get dates from term and convert to ZonedDateTime
                java.time.ZonedDateTime start = MainActivity.getZonedDateTime(assessment.getStartDate());
                ZonedDateTime end = MainActivity.getZonedDateTime(assessment.getEndDate());
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
                //get assessment details
                StringBuilder assessmentDetails = new StringBuilder();
                assessmentDetails.append(assessment.getTitle()).append("\n")
                        .append(assessment.getType())
                        .append("\nStart Date: ").append(assessment.getStartDate())
                        .append("\nEnd Date: ").append(assessment.getEndDate());
                if(assessment.getDescription().length() > 0)
                assessmentDetails.append("\n\nDescription:\n").append(assessment.getDescription());

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, assessmentDetails.toString());
//                sendIntent.putExtra(Intent.EXTRA_TITLE, "Message Title");
                sendIntent.setType("text/plain");

                Intent shareIntent = Intent.createChooser(sendIntent, null);
                startActivity(shareIntent);
                return true;

            case R.id.edit:
                editAssessment(assessment);
                return true;

            case R.id.delete:
                //TODO: change this to an alert for confirmation?
                repository.delete(assessment);
                String message = getApplicationContext().getString(R.string.assessment_deleted, assessment.getTitle());
                Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
                toast.show();
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void editAssessment(Assessment assessment) {
        Intent intent = new Intent(AssessmentDetails.this, AddAssessment.class);
        intent.putExtra("assessmentID", assessment.getID());
        intent.putExtra("courseID", assessment.getCourseID());
        startActivity(intent);
    }

    private void createNotification(String content, Long trigger) {
        Intent startIntent = new Intent(AssessmentDetails.this, MyReceiver.class);
        startIntent.putExtra("type", "Term");
        startIntent.putExtra("content", content);

        PendingIntent sender = PendingIntent.getBroadcast(AssessmentDetails.this, ++MainActivity.numAlert, startIntent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, trigger, sender);
    }

}