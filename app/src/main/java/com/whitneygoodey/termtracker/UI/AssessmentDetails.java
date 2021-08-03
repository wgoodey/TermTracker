package com.whitneygoodey.termtracker.UI;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

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
        assessment = repository.getAssessment(assessmentID);

        //TODO: set assessment details on screen
        TextView title = findViewById(R.id.assessmentTitle);
        TextView type = findViewById(R.id.assessmentType);
        TextView start = findViewById(R.id.textStartDate);
        TextView end = findViewById(R.id.textEndDate);
        TextView description = findViewById(R.id.textDescription);

        title.setText(assessment.getTitle());
        type.setText(assessment.getType().toString());
        start.setText(assessment.getStartDate());
        end.setText(assessment.getEndDate());
        description.setText(assessment.getDescription());

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
}