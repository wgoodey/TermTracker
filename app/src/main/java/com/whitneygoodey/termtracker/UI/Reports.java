package com.whitneygoodey.termtracker.UI;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
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

import androidx.annotation.NonNull;
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
    private List<Course> allCourses;
    private List<Assessment> allAssessments;

    private List<Term> reportTerms;
    private List<Course> reportCourses;
    private List<Assessment> reportAssessments;

    int currentUserID = MainActivity.getCurrentUserID();
    private boolean reportAllTerms;
    private boolean reportAllStatuses;
    private int termID;
    private Course.Status status;


    private Spinner scopeSpinner;
    private Spinner statusSpinner;
    private TextView title;
    private TableLayout reportTable;

    private TableRow tableRow;
    private TextView tableTitle;
    private TextView tableDates;
    private TextView tableCredits;
    private TextView tableAssessments;
    private TextView tableStatus;


    //TODO: match view spacing in reports screen to the other screens.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Context context = getApplicationContext();
        Repository repository = new Repository(getApplication());

        //TODO: figure out how to run in the background
        allTerms = repository.getAllTerms(currentUserID);
        allCourses = repository.getAllCourses(currentUserID);
        allAssessments = repository.getAllAssessments(currentUserID);

        scopeSpinner = findViewById(R.id.scopeSpinner);
        statusSpinner = findViewById(R.id.statusSpinner);
        title = findViewById(R.id.reportTitle);
        reportTable = findViewById(R.id.reportTable);
        reportTable.addView(LayoutInflater.from(this).inflate(R.layout.table_header, null));


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

        if (savedInstanceState == null) {
            reportAllTerms = true;
            reportAllStatuses = true;
            termID = -1;
            status = null;
        } else {
            reportAllTerms = savedInstanceState.getBoolean("AllTerms");
            reportAllStatuses = savedInstanceState.getBoolean("AllStatuses");
            termID = savedInstanceState.getInt("TermID");
            switch (savedInstanceState.getString("Status")) {
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
                    status = null;
            }



//            filterLists();
//            buildTable(reportTerms, reportCourses, reportAssessments);
//            title.setText(buildTitle());
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        //get scope and status settings
        boolean allTerms = reportAllTerms;
        boolean allStatuses = reportAllStatuses;
        int term = termID;
        String statusSetting;
        if (status != null) {
            statusSetting = status.toString();
        } else {
            statusSetting = "All";
        }

        savedInstanceState.putBoolean("AllTerms", allTerms);
        savedInstanceState.putBoolean("AllStatuses", allStatuses);
        savedInstanceState.putInt("TermID", term);
        savedInstanceState.putString("Status", statusSetting);

        super.onSaveInstanceState(savedInstanceState);
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
                buildTable(reportTable, reportTerms, reportCourses, reportAssessments);
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

    @NonNull
    private String buildTitle() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date date = new Date();

        return scopeSpinner.getSelectedItem().toString() + ": " +
               statusSpinner.getSelectedItem().toString() + " courses (" +
               formatter.format(date) + ")";
    }

    private void buildTable(@NonNull TableLayout reportTable, List<Term> reportTerms, List<Course> reportCourses, List<Assessment> reportAssessments) {
        //TODO: figure out how to keep it from clearing the table when switching orientation

        Context context = getApplicationContext();

        reportTable.removeViews(1, reportTable.getChildCount()-1);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 100f);

        for (Term currentTerm : reportTerms) {
            TableRow termRow = getTermRow(reportCourses, context, lp, currentTerm);
            reportTable.addView(termRow);

            boolean termHasCourses = false;
            for (Course course : reportCourses) {
                if (course.getTermID() == currentTerm.getID()) {
                    termHasCourses = true;
                    break;
                }
            }

            if (termHasCourses) {
                for (Course currentCourse : reportCourses) {
                    if (currentCourse.getTermID() == currentTerm.getID()) {
                        TableRow courseRow = getCourseRow(reportAssessments, context, lp, currentCourse);
                        reportTable.addView(courseRow);
                    }
                }
            } else {
                //create filler tableRow to show there are no courses
                TableRow courseRow = getNoCourseRow(lp);
                reportTable.addView(courseRow);

            }
        }
    }

    @NonNull
    private TableRow getHeaderRow(Context context) {
        TableRow headerRow = (TableRow) LayoutInflater.from(context).inflate(R.layout.table_header, null);

        tableTitle = findViewById(R.id.templateTitle);
        tableTitle.setText("TEST");
        tableDates = findViewById(R.id.templateDates);
        tableCredits = findViewById(R.id.templateCredits);
        tableAssessments = findViewById(R.id.templateAssessments);
        tableStatus = findViewById(R.id.templateStatus);



        return headerRow;
    }

    @NonNull
    private TableRow getTermRow(List<Course> reportCourses, Context context, TableRow.LayoutParams lp, Term currentTerm) {
        lp.weight = 30f;
        TextView tTitle = new TextView(this);
        tTitle.setLayoutParams(lp);
        tTitle.setText(currentTerm.getTitle());

        lp.weight = 25f;
        TextView tDates = new TextView(this);
        tDates.setLayoutParams(lp);
        String tDateString = context.getString(R.string.start_and_end_dates, currentTerm.getStartDate(), currentTerm.getEndDate());
        tDates.setText(tDateString);

        lp.weight = 15f;
        TextView tCredits = new TextView(this);
        tCredits.setLayoutParams(lp);
        int totalCredits = 0;
        for (Course currentCourse : reportCourses) {
            if (currentCourse.getTermID() == currentTerm.getID()) {
                totalCredits += currentCourse.getCredits();
            }
        }
        tCredits.setText(String.valueOf(totalCredits));

        lp.weight = 15f;
        TextView tAssessments = new TextView(this);
        tAssessments.setLayoutParams(lp);
        tAssessments.setText("");

        lp.weight = 15f;
        TextView tStatus = new TextView(this);
        tStatus.setLayoutParams(lp);
        tStatus.setText("");

        TableRow termRow = new TableRow(this);
        termRow.setLayoutParams(lp);
        termRow.addView(tTitle);
        termRow.addView(tDates);
        termRow.addView(tCredits);
        termRow.addView(tAssessments);
        termRow.addView(tStatus);
        termRow.setBackgroundColor(getResources().getColor(R.color.teal_700));
        return termRow;
    }

    @NonNull
    private TableRow getCourseRow(List<Assessment> reportAssessments, Context context, TableRow.LayoutParams lp, Course currentCourse) {

        lp.weight = 30f;
        TextView cTitle = new TextView(this);
        cTitle.setLayoutParams(lp);
        cTitle.setText(currentCourse.getTitle());

        lp.weight = 25f;
        TextView cDates = new TextView(this);
        cDates.setLayoutParams(lp);
        String cDateString = context.getString(R.string.start_and_end_dates, currentCourse.getStartDate(), currentCourse.getEndDate());
        cDates.setText(cDateString);

        lp.weight = 15f;
        TextView cCredits = new TextView(this);
        cCredits.setLayoutParams(lp);
        cCredits.setText(String.valueOf(currentCourse.getCredits()));

        lp.weight = 15f;
        TextView cAssessments = new TextView(this);
        cAssessments.setLayoutParams(lp);
        String assessments = "";
        for (Assessment currentAssessment : reportAssessments) {
            if (currentAssessment.getCourseID() == currentCourse.getID()) {
                if (assessments.equals("")) {
                    if (currentAssessment.getType() == Assessment.Type.OBJECTIVE) {
                        assessments = "OA";
                    } else {
                        assessments = "PA";
                    }
                } else {
                    if (currentAssessment.getType() == Assessment.Type.OBJECTIVE) {
                        assessments += "/OA";
                    } else {
                        assessments += "/PA";
                    }
                }
            }
            cAssessments.setText(assessments);
        }


        lp.weight = 15f;
        TextView cStatus = new TextView(this);
        cStatus.setLayoutParams(lp);
        cStatus.setText(currentCourse.getStatus().toString());

        TableRow courseRow = new TableRow(this);
        courseRow.setLayoutParams(lp);
        courseRow.addView(cTitle);
        courseRow.addView(cDates);
        courseRow.addView(cCredits);
        courseRow.addView(cAssessments);
        courseRow.addView(cStatus);
        courseRow.setBackgroundColor(getResources().getColor(R.color.teal_200));
        return courseRow;
    }

    @NonNull
    private TableRow getNoCourseRow(TableRow.LayoutParams lp) {
        lp.weight = 30f;
        TextView cTitle = new TextView(this);
        cTitle.setLayoutParams(lp);
        cTitle.setText("No courses found");

        TableRow courseRow = new TableRow(this);
        courseRow.setLayoutParams(lp);
        courseRow.addView(cTitle);
        courseRow.setBackgroundColor(getResources().getColor(R.color.teal_200));
        return courseRow;
    }

}