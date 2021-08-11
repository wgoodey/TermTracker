package com.whitneygoodey.termtracker.UI;

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

import java.util.Objects;

public class AssessmentDetails extends AppCompatActivity {

    private Repository repository;
    private int assessmentID;
    private Assessment assessment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_details);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        try {
            assessmentID = getIntent().getIntExtra("assessmentID", -1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        repository = new Repository(getApplication());
        setAssessmentDetailsOnScreen();
    }

    private void setAssessmentDetailsOnScreen() {
        //get assessment from the database
        assessment = repository.getAssessment(assessmentID);

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
                setAssessmentDetailsOnScreen();
                return true;

            case R.id.notify:
                //TODO: add code for notifications
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
                editAssessment(assessmentID);
                return true;

            case R.id.delete:
                //TODO: add alert for confirmation?
                repository.delete(assessment);
                String message = getApplicationContext().getString(R.string.assessment_deleted, assessment.getTitle());
                Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
                toast.show();
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void editAssessment(int assessmentID) {
        Intent intent = new Intent(AssessmentDetails.this, AddAssessment.class);
        intent.putExtra("assessmentID", assessmentID);
        startActivity(intent);
    }

}