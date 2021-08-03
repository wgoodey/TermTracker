package com.whitneygoodey.termtracker.UI;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

import com.whitneygoodey.termtracker.Database.Repository;
import com.whitneygoodey.termtracker.Entities.Assessment;
import com.whitneygoodey.termtracker.R;

public class AddAssessment extends AppCompatActivity {

    Repository repository;
    Assessment assessment;
    int id = -1;
    int courseID;
    EditText titleEdit;
    EditText startEdit;
    EditText endEdit;
    RadioGroup typeGroup;
    RadioButton objective;
    RadioButton performance;
    EditText descriptionEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_assessment);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        repository = new Repository(getApplication());

        titleEdit = findViewById(R.id.titleEditText);
        startEdit = findViewById(R.id.editStartDate);
        endEdit = findViewById(R.id.editEndDate);
        typeGroup = findViewById(R.id.typeRadioGroup);
        objective = findViewById(R.id.objectiveRadio);
        objective.setChecked(true);
        performance = findViewById(R.id.performanceRadio);
        descriptionEdit = findViewById(R.id.editDescription);

        try {
            id = getIntent().getIntExtra("assessmentID", -1);
            courseID = getIntent().getIntExtra("courseID", -1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //if editing assessment, get details and fill them in on the screen.
        if (id != -1) {
            assessment = repository.getAssessment(id);

            titleEdit.setText(assessment.getTitle());
            startEdit.setText(assessment.getStartDate());
            endEdit.setText(assessment.getEndDate());
            if (assessment.getType().equals(Assessment.Type.PERFORMANCE)) {
                performance.setChecked(true);
            }
            descriptionEdit.setText(assessment.getDescription());
        }
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

    public void saveAssessment(View view) {

        String title;
        String startDate;
        String endDate;
        String description;
        Assessment.Type type;

        //get details from the screen
        title = titleEdit.getText().toString();
        startDate = startEdit.getText().toString();
        endDate = endEdit.getText().toString();
        description = descriptionEdit.getText().toString();
        if (performance.isChecked()) {
            type = Assessment.Type.PERFORMANCE;
        } else type = Assessment.Type.OBJECTIVE;

        //check if new assessment or not
        if (id == -1) {
            //create new assessment without id and insert into database
            assessment = new Assessment(courseID, title, startDate, endDate, description, type);
            repository.insert(assessment);
        } else {
            //create new assessment with existing id and update in database
            assessment = new Assessment(id, courseID, title, startDate, endDate, description, type);
            repository.update(assessment);
        }
        finish();
    }

    //TODO: change return so date is entered in dateEditText
    public void showDatePicker(View view) {
        //TODO: configure screen to show DatePicker with click on calendar icon

    }
}