package com.whitneygoodey.termtracker.UI;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.whitneygoodey.termtracker.Database.Repository;
import com.whitneygoodey.termtracker.Entities.Assessment;
import com.whitneygoodey.termtracker.Entities.Course;
import com.whitneygoodey.termtracker.Entities.Term;
import com.whitneygoodey.termtracker.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Reports extends AppCompatActivity implements AdapterView.OnItemSelectedListener {


    private List<Term> allTerms;
    private List<Term> reportTerms;
    private List<Course> allCourses;
    private List<Course> reportCourses;
    private List<Assessment> allAssessments;
    private List<Assessment> reportAssessments;

    int currentUserID = MainActivity.getCurrentUserID();
    private Boolean reportAllTerms;
    private Boolean reportAllStatuses;
    private int termID;
    private Course.Status status;


    private Spinner scopeSpinner;
    private Spinner statusSpinner;
    private TextView title;
    private TableLayout reportTable;

    //TODO: match view spacing in reports screen to the other screens.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Context context = getApplicationContext();
        Repository repository = new Repository(getApplication());

        reportAllTerms = true;
        reportAllStatuses = true;
        termID = -1;
        status = null;

        //TODO: figure out how to run in the background
        allTerms = repository.getAllTerms(currentUserID);
        allCourses = repository.getAllCourses(currentUserID);
        allAssessments = repository.getAllAssessments(currentUserID);

        scopeSpinner = findViewById(R.id.scopeSpinner);
        statusSpinner = findViewById(R.id.statusSpinner);
        title = findViewById(R.id.reportTitle);
        reportTable = findViewById(R.id.reportTable);

        //set scope spinner list
        List<String> termList = new ArrayList<>();
        termList.add("All terms");
        for (Term term : repository.getAllTerms(currentUserID)) {
            termList.add(term.getTitle());
        }
        scopeSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, termList));
        scopeSpinner.setSelection(0);


        //set status spinner list
        List<String> statusList = new ArrayList<>();
        statusList.add("All");
        for(Course.Status status : Course.Status.values()) {
            statusList.add(status.toString());
        }
        statusSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, statusList));
        statusSpinner.setSelection(0);

        //setOnItemSelectedListener to spinners
        scopeSpinner.setOnItemSelectedListener(this);
        statusSpinner.setOnItemSelectedListener(this);

        filterLists();
        buildTable(reportTerms, reportCourses, reportAssessments);
        title.setText(buildTitle());
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
                filterLists();
                buildTable(reportTerms, reportCourses, reportAssessments);
                title.setText(buildTitle());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {

        String scopeFilter = scopeSpinner.getSelectedItem().toString();
        String statusFilter = statusSpinner.getSelectedItem().toString();

        if (scopeFilter.equals("All terms")) {
            reportAllTerms = true;
        } else {
            reportAllTerms = false;
            for (Term term : allTerms) {
                if (term.getTitle().equals(scopeFilter)) {
                    termID = term.getID();
                    break;
                }
            }
        }

        if (statusFilter.equals("All")) {
            reportAllStatuses = true;
            status = null;
        } else {
            reportAllStatuses = false;
            switch (statusFilter) {
                case "Planned":
                    status = Course.Status.PLANNED;
                    break;
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
                    reportAllStatuses = true;
            }
        }
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    private void filterLists() {

        //clear all report lists
        reportTerms = new ArrayList<>();
        reportCourses = new ArrayList<>();
        reportAssessments = new ArrayList<>();

        Predicate<Term> byTerm = term -> term.getID() == termID;
        Predicate<Course> byCourseTerm = course -> course.getTermID() == termID;
        Predicate<Course> byStatus = course -> course.getStatus().equals(status);


        if (reportAllTerms) {
            //add all Terms, Courses, and Assessments
            reportTerms.addAll(allTerms);
            reportCourses.addAll(allCourses);
        } else {
            //add only selected Term and associated Courses
            reportTerms = allTerms.stream().filter(byTerm).collect(Collectors.toList());
            reportCourses = allCourses.stream().filter(byCourseTerm).collect(Collectors.toList());
        }

        if (!reportAllStatuses) {
            //filter to selected course status
            reportCourses = reportCourses.stream().filter(byStatus).collect(Collectors.toList());
        }

        //add Assessments for each Course still in the list
        for (Course course : reportCourses) {
            Predicate<Assessment> byCourse = assessment -> assessment.getCourseID() == course.getID();
            reportAssessments.addAll(allAssessments.stream().filter(byCourse).collect(Collectors.toList()));
        }
    }

    private void buildTable(List<Term> reportTerms, List<Course> reportCourses, List<Assessment> reportAssessments) {
        //TODO: figure out how to keep it from clearing the table when switching orientation

        Context context = getApplicationContext();

        reportTable.removeViews(1, reportTable.getChildCount() - 1);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);

        for (Term currentTerm : reportTerms) {
            TextView tTerm = new TextView(this);
            tTerm.setLayoutParams(lp);
            tTerm.setText(currentTerm.getTitle());

            TextView tCourse = new TextView(this);
            tCourse.setLayoutParams(lp);
            tCourse.setText("");

            TextView tDates = new TextView(this);
            tDates.setLayoutParams(lp);
            String tDateString = context.getString(R.string.start_and_end_dates, currentTerm.getStartDate(), currentTerm.getEndDate());
            tDates.setText(tDateString);

            TextView tCredits = new TextView(this);
            tCredits.setLayoutParams(lp);
            tCredits.setText("");

            TextView tTotal = new TextView(this);
            tTotal.setLayoutParams(lp);
            int totalCredits = 0;
            for (Course currentCourse : reportCourses) {
                if (currentCourse.getTermID() == currentTerm.getID()) {
                    totalCredits += currentCourse.getCredits();
                }
            }
            tTotal.setText(String.valueOf(totalCredits));

            TextView tAssessments = new TextView(this);
            tAssessments.setLayoutParams(lp);
            tAssessments.setText("");

            TextView tStatus = new TextView(this);
            tStatus.setLayoutParams(lp);
            tStatus.setText("");

            TableRow termRow = new TableRow(this);
            termRow.setLayoutParams(lp);
            termRow.addView(tTerm);
            termRow.addView(tCourse);
            termRow.addView(tDates);
            termRow.addView(tCredits);
            termRow.addView(tTotal);
            termRow.addView(tAssessments);
            termRow.addView(tStatus);
            termRow.setBackgroundColor(getResources().getColor(R.color.teal_700));

            reportTable.addView(termRow);

            for (Course currentCourse : reportCourses) {
                if (currentCourse.getTermID() == currentTerm.getID()) {
                    TextView cTerm = new TextView(this);
                    cTerm.setLayoutParams(lp);
                    cTerm.setText("");

                    TextView cCourse = new TextView(this);
                    cCourse.setLayoutParams(lp);
                    cCourse.setText(currentCourse.getTitle());

                    TextView cDates = new TextView(this);
                    cDates.setLayoutParams(lp);
                    String cDateString = context.getString(R.string.start_and_end_dates, currentCourse.getStartDate(), currentCourse.getEndDate());
                    cDates.setText(cDateString);

                    TextView cCredits = new TextView(this);
                    cCredits.setLayoutParams(lp);
                    cCredits.setText(String.valueOf(currentCourse.getCredits()));

                    TextView cTotal = new TextView(this);
                    cTotal.setLayoutParams(lp);
                    cTotal.setText("");

                    TextView cAssessments = new TextView(this);
                    cAssessments.setLayoutParams(lp);
                    String assessments = "";
                    for (Assessment currentAssessment : reportAssessments) {
                        if (currentAssessment.getCourseID() == currentCourse.getID()) {
                            if (assessments.equals("")) {
                                if (currentAssessment.getType() == Assessment.Type.OBJECTIVE) {
                                    assessments += "OA";
                                } else {
                                    assessments += "PA";
                                }
                            } else {
                                if (currentAssessment.getType() == Assessment.Type.OBJECTIVE) {
                                    assessments += ", OA";
                                } else {
                                    assessments += ", PA";
                                }
                            }
                        }
                        cAssessments.setText(assessments);
                    }


                    TextView cStatus = new TextView(this);
                    cStatus.setLayoutParams(lp);
                    cStatus.setText(currentCourse.getStatus().toString());

                    TableRow courseRow = new TableRow(this);
                    courseRow.setLayoutParams(lp);
                    courseRow.addView(cTerm);
                    courseRow.addView(cCourse);
                    courseRow.addView(cDates);
                    courseRow.addView(cCredits);
                    courseRow.addView(cTotal);
                    courseRow.addView(cAssessments);
                    courseRow.addView(cStatus);
                    courseRow.setBackgroundColor(getResources().getColor(R.color.teal_200));

                    reportTable.addView(courseRow);
                }
            }
        }

    }

    private String buildTitle() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date date = new Date();

        return scopeSpinner.getSelectedItem().toString() + ": " +
                statusSpinner.getSelectedItem().toString() + " courses (" +
                formatter.format(date) + ")";
    }

}