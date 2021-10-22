package com.whitneygoodey.termtracker.UI;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
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

    EditText searchBar;
    RadioGroup searchOptions;
    RadioButton termsOption;
    RadioButton coursesOption;
    RadioButton assessmentsOption;
    RecyclerView searchResultsRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        repository = new Repository(getApplication());

        searchBar = findViewById(R.id.editSearch);
        searchOptions = findViewById(R.id.searchRadios);
        termsOption = findViewById(R.id.radioTerms);
        coursesOption = findViewById(R.id.radioCourses);
        assessmentsOption = findViewById(R.id.radioAssessments);
        Button searchButton = findViewById(R.id.searchButton);
        searchResultsRecyclerView = findViewById(R.id.searchResultsRecyclerView);

        searchButton.setOnClickListener(v -> search(searchBar.getText().toString()));

        if (savedInstanceState == null) {
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

        } else {
            String searchTerm = savedInstanceState.getString("searchTerms");
            String searchType = savedInstanceState.getString("searchType");

            searchBar.setText(searchTerm);
            if (searchType.equals("course")) {
                coursesOption.setChecked(true);
            } else if (searchType.equals("assessment")) {
                assessmentsOption.setChecked(true);
            } else {
                termsOption.setChecked(true);
            }
            search(searchTerm);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        String key = searchBar.getText().toString();
        String type;

        if (coursesOption.isChecked()) {
            type = "course";
        } else if (assessmentsOption.isChecked()) {
            type = "assessment";
        } else {
            type = "term";
        }

        savedInstanceState.putString("searchTerms", key);
        savedInstanceState.putString("searchType", type);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void search(String searchKey) {

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

    }

    private List<Term> searchTerms(String key) {
        key = key.toLowerCase().trim();
        List<Term> results = new ArrayList<>();
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