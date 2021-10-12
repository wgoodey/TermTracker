package com.whitneygoodey.termtracker.UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.whitneygoodey.termtracker.Database.Repository;
import com.whitneygoodey.termtracker.Entities.Term;
import com.whitneygoodey.termtracker.R;

import java.util.List;
import java.util.Objects;

public class TermList extends AppCompatActivity {

    private Repository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_list);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //get terms from the database
        setTermList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.term_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;

            case R.id.refresh:
                setTermList();
                return true;

            case R.id.search:
                Intent searchIntent = new Intent(TermList.this, Search.class);
                searchIntent.putExtra("searchType", "term");
                startActivity(searchIntent);
                return true;

            case R.id.reports:
                Intent reportsIntent = new Intent(TermList.this, Reports.class);
                startActivity(reportsIntent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setTermList() {
        repository = new Repository(getApplication());
        //TODO: change currentUser so that it's passed as intent extra instead?
        List<Term> allTerms = repository.getAllTerms(MainActivity.getCurrentUserID());
        RecyclerView recyclerView = findViewById(R.id.termRecyclerView);

        final TermAdapter termAdapter = new TermAdapter(this);
        recyclerView.setAdapter(termAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        termAdapter.setTermList(allTerms);
    }

    public void addTerm(View view) {
        Intent intent = new Intent(TermList.this, AddTerm.class);
        startActivity(intent);
    }

}