package com.whitneygoodey.termtracker.UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.whitneygoodey.termtracker.Database.Repository;
import com.whitneygoodey.termtracker.Entities.Assessment;
import com.whitneygoodey.termtracker.Entities.Course;
import com.whitneygoodey.termtracker.Entities.Term;
import com.whitneygoodey.termtracker.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //create temporary data
        Term term = new Term("Term 1", "10/23/2021", "10/23/2021");
        Course course = new Course(1,1,"Fundamentals of Woodworking", Course.Status.ENROLLED, "10/23/2021", "10/23/2021","Bob Vila", "bob@vila.com", "123-456-7890", "This is a long note that has no real meaning other than to fill space in the view and hopefully demonstrate some wrapping.");
        Assessment assessment = new Assessment(1,1, "Assessment 1", "10/23/2021", "10/23/2021", Assessment.Type.PERFORMANCE);


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Repository repository = new Repository(getApplication());
        repository.insert(term);
        repository.insert(course);
        repository.insert(assessment);
    }

    public void loadTerms(View view) {
        Intent intent = new Intent(MainActivity.this, TermList.class);
        startActivity(intent);
    }

}