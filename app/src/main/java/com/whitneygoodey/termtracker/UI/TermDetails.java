package com.whitneygoodey.termtracker.UI;

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

import java.util.List;
import java.util.Objects;

public class TermDetails extends AppCompatActivity {

    private Repository repository;
    private int termID;
    private Term term;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_details);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        try {
            termID = getIntent().getIntExtra("termID", -1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //get term and courses from the database
        repository = new Repository(getApplication());
        setTermDetailsOnScreen();
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
                setTermDetailsOnScreen();
                return true;

            case R.id.notify:
                //TODO: add code for notifications

                return true;

            case R.id.share:
                //get term title with course titles and dates
                StringBuilder termDetails = new StringBuilder();
                termDetails.append(term.getTitle()).append("course list:\n");
                for (Course course : repository.getTermCourses(termID)) {
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
                editTerm(termID);
                return true;

            case R.id.delete:
                boolean flag = false;
                List<Course> allCourses = repository.getAllCourses();

                for (Course course : allCourses) {
                    if (course.getTermID() == termID) {
                        flag = true;
                        break;
                    }
                }

                if (flag) {
                    //display toast notifying user that term cannot be deleted
                    String message = getApplicationContext().getString(R.string.cannot_delete_term, term.getTitle());
                    Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
                    toast.show();

                    //TODO display confirmation dialog to delete a term with courses?


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

    private void setTermDetailsOnScreen() {
        //get term from the database
        term = repository.getTerm(termID);

        //set term info on screen
        TextView title = findViewById(R.id.termTitle);
        TextView start = findViewById(R.id.textStartDate);
        TextView end = findViewById(R.id.textEndDate);
        title.setText(term.getTitle());
        start.setText(term.getStartDate());
        end.setText(term.getEndDate());

        //set RecyclerView and CourseAdapter
        List<Course> courseList = repository.getTermCourses(termID);
        RecyclerView recyclerView = findViewById(R.id.courseRecyclerView);
        final CourseAdapter courseAdapter = new CourseAdapter(this);
        recyclerView.setAdapter(courseAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        courseAdapter.setCourseList(courseList);
    }

    public void addCourse(View view) {
        Intent intent = new Intent(TermDetails.this, AddCourse.class);
        intent.putExtra("termID", termID);
        startActivity(intent);
    }

    public void editTerm(int termID) {
        Intent intent = new Intent(TermDetails.this, AddTerm.class);
        intent.putExtra("termID", termID);
        startActivity(intent);
    }


}