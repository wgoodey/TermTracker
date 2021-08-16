package com.whitneygoodey.termtracker.UI;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.whitneygoodey.termtracker.Database.Repository;
import com.whitneygoodey.termtracker.Entities.Term;
import com.whitneygoodey.termtracker.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class AddTerm extends AppCompatActivity {

    int id;
    Term term;
    Repository repository;
    EditText titleEdit;
    EditText startEdit;
    EditText endEdit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_term);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        repository = new Repository(getApplication());
        
        titleEdit = findViewById(R.id.titleEditText);
        startEdit = findViewById(R.id.editStartDate);
        endEdit = findViewById(R.id.editEndDate);

        //set date hints
        startEdit.setHint(MainActivity.formatDateHints());
        endEdit.setHint(MainActivity.formatDateHints());


        try {
            id = getIntent().getIntExtra("termID", -1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //if editing term, get details and fill them in on the screen.
        if (id != -1) {
            term = repository.getTerm(id);

            titleEdit.setText(term.getTitle());
            startEdit.setText(term.getStartDate());
            endEdit.setText(term.getEndDate());
        }

        //set datePickers for startEdit and endEdit
        final Calendar myCalendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener startDate = (view, year, month, dayOfMonth) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel(myCalendar, startEdit);
        };

        DatePickerDialog.OnDateSetListener endDate = (view, year, month, dayOfMonth) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel(myCalendar, endEdit);
        };

        startEdit.setOnClickListener(v -> {
            new DatePickerDialog(AddTerm.this, startDate, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            updateLabel(myCalendar, startEdit);
        });

        endEdit.setOnClickListener(v -> {
            new DatePickerDialog(AddTerm.this, endDate, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();
        });
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
                    saveTerm();
                    finish();
                    return true;
                }
        }
        return super.onOptionsItemSelected(item);
    }

    public void saveTerm() {
        String title = titleEdit.getText().toString();
        String startDate = startEdit.getText().toString();
        String endDate = endEdit.getText().toString();

        if (id == -1) {
            term = new Term(title, startDate, endDate);
            repository.insert(term);

        } else {
            term = new Term(id, title, startDate, endDate);
            repository.update(term);
        }
    }

    private void updateLabel(Calendar myCalendar, EditText editText) {
        String myFormat = "MM/dd/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        editText.setText(sdf.format(myCalendar.getTime()));
    }
}