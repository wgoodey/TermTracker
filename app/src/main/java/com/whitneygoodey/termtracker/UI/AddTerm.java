package com.whitneygoodey.termtracker.UI;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.whitneygoodey.termtracker.Database.Repository;
import com.whitneygoodey.termtracker.Entities.Term;
import com.whitneygoodey.termtracker.R;

import java.util.Objects;

public class AddTerm extends AppCompatActivity {

    int id = -1;
    EditText titleEdit;
    EditText startEdit;
    EditText endEdit;
    Repository repository;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_term);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        repository = new Repository(getApplication());


        titleEdit = findViewById(R.id.titleEditText);
        startEdit = findViewById(R.id.startDateEditText);
        endEdit = findViewById(R.id.endDateEditText);

        try {
            id = getIntent().getIntExtra("id", -1);
        } catch (Exception e) {
            e.printStackTrace();
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

    //TODO: test editing Term
    public void saveTerm(View view) {
        String title = titleEdit.getText().toString();
        String startDate = startEdit.getText().toString();
        String endDate = endEdit.getText().toString();

        if (id == -1) {
            Term newTerm = new Term(title, startDate, endDate);
            repository.insert(newTerm);
        } else {
            Term oldTerm = new Term(id, title, startDate, endDate);
            repository.update(oldTerm);
        }
        //TODO: figure out how to update termRecycleView automatically
        finish();
    }

    //TODO: change return so date is entered in dateEditText
    public void showDatePicker(View view) {
        //TODO: configure screen to show DatePicker with click on calendar icon

    }
}