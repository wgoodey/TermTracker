package com.whitneygoodey.termtracker.UI;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

import com.whitneygoodey.termtracker.Database.Repository;
import com.whitneygoodey.termtracker.Entities.Assessment;
import com.whitneygoodey.termtracker.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddAssessment extends AppCompatActivity {

    int id;
    int courseID;
    Assessment assessment;
    Repository repository;
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

        //set date hints
        startEdit.setHint(MainActivity.formatDateHints());
        endEdit.setHint(MainActivity.formatDateHints());

        //set datePickers for startEdit and endEdit
        final Calendar myCalendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener startDate = (view, year, month, dayOfMonth) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateEditText(myCalendar, startEdit);
        };

        DatePickerDialog.OnDateSetListener endDate = (view, year, month, dayOfMonth) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateEditText(myCalendar, endEdit);
        };

        startEdit.setOnClickListener(v -> {
            new DatePickerDialog(AddAssessment.this, startDate, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            updateEditText(myCalendar, startEdit);
        });

        endEdit.setOnClickListener(v -> {
            new DatePickerDialog(AddAssessment.this, endDate, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();
        });

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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.save_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;

            case R.id.save:
                String title = titleEdit.getText().toString();
                String startDate = startEdit.getText().toString();
                String endDate = endEdit.getText().toString();
                if (MainActivity.isValid(getApplicationContext(), title, startDate, endDate)) {
                    saveAssessment();
                    finish();
                    return true;
                }
        }
        return super.onOptionsItemSelected(item);
    }

    public void saveAssessment() {

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

        if (id == -1) {
            //create new assessment without id and insert into database
            assessment = new Assessment(courseID, title, startDate, endDate, description, type);
            repository.insert(assessment);
        } else {
            //create new assessment with existing id and update in database
            assessment = new Assessment(id, courseID, title, startDate, endDate, description, type);
            repository.update(assessment);
        }
    }

    private void updateEditText(Calendar myCalendar, EditText editText) {
        String myFormat = "MM/dd/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        editText.setText(sdf.format(myCalendar.getTime()));
    }
}