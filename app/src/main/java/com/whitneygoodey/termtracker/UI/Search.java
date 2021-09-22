package com.whitneygoodey.termtracker.UI;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.whitneygoodey.termtracker.Database.Repository;
import com.whitneygoodey.termtracker.Entities.Assessment;
import com.whitneygoodey.termtracker.Entities.Course;
import com.whitneygoodey.termtracker.Entities.Term;
import com.whitneygoodey.termtracker.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Search extends AppCompatActivity {

    private Repository repository;
    List<Term> termResultsList;
    List<Course> courseResultsList;
    List<Assessment> assessmentResultsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        repository = new Repository(getApplication());

        EditText searchBar = findViewById(R.id.editSearch);
        RadioGroup searchOptions = findViewById(R.id.searchRadios);
        RadioButton termsOption = findViewById(R.id.radioTerms);
        RadioButton coursesOption = findViewById(R.id.radioCourses);
        RadioButton assessmentsOption = findViewById(R.id.radioAssessments);
        Button searchButton = findViewById(R.id.searchButton);
        RecyclerView searchResultsRecyclerView = findViewById(R.id.searchResultsRecyclerView);

        try {
            String searchType = getIntent().getStringExtra("searchType");
            if (searchType.equals("course")) {
                coursesOption.setChecked(true);
            } else if (searchType.equals("assessment")) {
                assessmentsOption.setChecked(true);
            } else {
                termsOption.setChecked(true);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        searchButton.setOnClickListener(v -> {
            String searchKey = searchBar.getText().toString();

            if (termsOption.isChecked()) {
                termResultsList = searchTerms(searchKey);

                //set recyclerView to load terms
                final TermAdapter termAdapter = new TermAdapter(this);
                searchResultsRecyclerView.setAdapter(termAdapter);
                searchResultsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                termAdapter.setTermList(termResultsList);

            } else if (coursesOption.isChecked()) {
                courseResultsList = searchCourses(searchKey);

                //set recyclerview to load courses
                final CourseAdapter courseAdapter = new CourseAdapter(this);
                searchResultsRecyclerView.setAdapter(courseAdapter);
                searchResultsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                courseAdapter.setCourseList(courseResultsList);

            } else {
                assessmentResultsList = searchAssessments(searchKey);

                //set recyclerView to load assessments
                final AssessmentAdapter assessmentAdapter = new AssessmentAdapter(this);
                searchResultsRecyclerView.setAdapter(assessmentAdapter);
                searchResultsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                assessmentAdapter.setAssessmentList(assessmentResultsList);

            }
        });

    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.menu, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private List<Term> searchTerms(String key) {
        key = key.toLowerCase().trim();
        List<Term> results = new ArrayList();
        for (Term term : repository.getAllTerms(MainActivity.getCurrentUserID())) {
            String title = term.getTitle().toLowerCase().trim();
            if (title.contains(key)) {
                results.add(term);
            }
        }
        return results;
    }

    private List<Course> searchCourses(String key) {
        key = key.toLowerCase().trim();
        List<Course> results = new ArrayList<>();
        for (Course course : repository.getAllCourses(MainActivity.getCurrentUserID())) {
            String courseTitle = course.getTitle().toLowerCase().trim();
            String courseNote = course.getNote().toLowerCase().trim();
            if (courseTitle.contains(key)) {
                results.add(course);
            } else if (courseNote.contains(key)) {
                results.add(course);
            } else {
                List<Assessment> courseAssessments = repository.getCourseAssessments(course.getID());
                for (Assessment assessment : courseAssessments) {
                    String assessmentTitle = assessment.getTitle().toLowerCase().trim();
                    String assessmentDescription = assessment.getDescription().toLowerCase().trim();
                    if (assessmentTitle.contains(key) || assessmentDescription.contains(key)) {
                        results.add(course);
                        break;
                    }
                }
            }
        }
        return results;
    }

    private List<Assessment> searchAssessments(String key) {
        key = key.toLowerCase().trim();
        List<Assessment> results = new ArrayList<>();
        for (Assessment assessment : repository.getAllAssessments(MainActivity.getCurrentUserID())) {
            String assessmentTitle = assessment.getTitle().toLowerCase().trim();
            String assessmentDescription = assessment.getDescription().toLowerCase().trim();
            if (assessmentTitle.contains(key) || assessmentDescription.contains(key)) {
                results.add(assessment);
            }
        }
        return results;
    }

}