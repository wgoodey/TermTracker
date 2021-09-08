package com.whitneygoodey.termtracker.UI;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.whitneygoodey.termtracker.Database.Repository;
import com.whitneygoodey.termtracker.Entities.Course;
import com.whitneygoodey.termtracker.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class AddCourse extends AppCompatActivity {

    int id;
    int termID;
    Course course;
    Repository repository;
    EditText titleEdit;
    EditText creditsEdit;
    EditText startEdit;
    EditText endEdit;
    EditText nameEdit;
    EditText emailEdit;
    EditText phoneEdit;
    EditText noteEdit;
    Spinner statusSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        repository = new Repository(getApplication());

        titleEdit = findViewById(R.id.editCourseTitle);
        creditsEdit = findViewById(R.id.editCredits);
        startEdit = findViewById(R.id.editStartDate);
        endEdit = findViewById(R.id.editEndDate);
        nameEdit = findViewById(R.id.editName);
        emailEdit = findViewById(R.id.editEmail);
        phoneEdit = findViewById(R.id.editPhone);
        noteEdit = findViewById(R.id.editNote);
        statusSpinner = findViewById(R.id.statusSpinner);

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
            new DatePickerDialog(AddCourse.this, startDate, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            updateEditText(myCalendar, startEdit);
        });

        endEdit.setOnClickListener(v -> {
            new DatePickerDialog(AddCourse.this, endDate, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        //set status spinner list
        statusSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Course.Status.values()));

        try {
            id = getIntent().getIntExtra("courseID", -1);
            termID = getIntent().getIntExtra("termID", -1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //if editing course, get details and fill them in on the screen.
        if (id != -1) {
            course = repository.getCourse(id);

            titleEdit.setText(course.getTitle());
            creditsEdit.setText(String.valueOf(course.getCredits()));
            startEdit.setText(course.getStartDate());
            endEdit.setText(course.getEndDate());
            nameEdit.setText(course.getInstructorName());
            emailEdit.setText(course.getInstructorEmail());
            phoneEdit.setText(course.getInstructorPhone());
            noteEdit.setText(course.getNote());

            //set status spinner
            switch (course.getStatus()) {
                case PLANNED:
                    statusSpinner.setSelection(0);
                    break;
                case ENROLLED:
                    statusSpinner.setSelection(1);
                    break;
                case PROGRESS:
                    statusSpinner.setSelection(2);
                    break;
                case COMPLETED:
                    statusSpinner.setSelection(3);
                    break;
                default:
                    statusSpinner.setSelection(4);
                    break;
            }

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
                    saveCourse();
                    finish();
                    return true;
                }
        }
        return super.onOptionsItemSelected(item);
    }

    public void saveCourse() {
        String title;
        Course.Status status;
        int credits;
        String startDate;
        String endDate;
        String name;
        String email;
        String phone;
        String note;

        //get details from the screen
        title = titleEdit.getText().toString();
        credits = Integer.parseInt(creditsEdit.getText().toString());
        startDate = startEdit.getText().toString();
        endDate = endEdit.getText().toString();
        name = nameEdit.getText().toString();
        email = emailEdit.getText().toString();
        phone = phoneEdit.getText().toString();
        note = noteEdit.getText().toString();

        //get status from spinner
        switch (statusSpinner.getSelectedItem().toString()) {
            case "Enrolled":
                status = Course.Status.ENROLLED;
                break;
            case "In Progress":
                status = Course.Status.PROGRESS;
                break;
            case "Completed":
                status = Course.Status.COMPLETED;
                break;
            case "Dropped":
                status = Course.Status.DROPPED;
                break;
            default:
                status = Course.Status.PLANNED;
        }

        //check if new course or not
        if (id == -1) {
            //create new course without id and insert into database
            course = new Course(termID, title, status, credits, startDate, endDate, name, email, phone, note);
            repository.insert(course);
        } else {
            //create new course with existing id and update in database
            course = new Course(id, termID, title, status, credits, startDate, endDate, name, email, phone, note);
            repository.update(course);
        }
    }

    private void updateEditText(Calendar myCalendar, EditText editText) {
        String myFormat = "MM/dd/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        editText.setText(sdf.format(myCalendar.getTime()));
    }
}