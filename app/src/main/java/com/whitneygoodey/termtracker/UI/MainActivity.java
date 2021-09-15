package com.whitneygoodey.termtracker.UI;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.whitneygoodey.termtracker.Database.Repository;
import com.whitneygoodey.termtracker.Entities.Assessment;
import com.whitneygoodey.termtracker.Entities.Course;
import com.whitneygoodey.termtracker.Entities.Term;
import com.whitneygoodey.termtracker.Entities.User;
import com.whitneygoodey.termtracker.R;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    public static final int ADMIN_ID = 1;
    private static int userID = -1;
    public static int numAlert;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Repository repository = new Repository(getApplication());
//        setTempData(repository);

        List<User> userList = repository.getAllUsers();

        EditText userEdit = findViewById(R.id.editUser);
        EditText passEdit = findViewById(R.id.editPassword);
        Button button = findViewById(R.id.button);

        button.setOnClickListener(v -> {
            String email = userEdit.getText().toString();
            String password = passEdit.getText().toString();

            for (User currentUser : userList) {
                if (email.equals(currentUser.getEmail())) {
                    if (password.equals(currentUser.getPassword())) {
                        //if credentials match set currentUser and load terms
                        setCurrentUserID(currentUser.getID());
                        loadTerms();
                    } else {
                        //else warn of mismatch
                        Toast.makeText(getApplicationContext(), "Invalid credentials.", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

    }

    //TODO: add userID parameter
    public void loadTerms() {
        Intent intent = new Intent(MainActivity.this, TermList.class);
        startActivity(intent);
    }

    private void setTempData(Repository repository) {

        //create temporary data
        User admin = new User (1, "admin", "admin");
        User testUser = new User (2, "test", "test");

        Term term1 = new Term(1, "Term 1", "10/01/2021", "03/31/2022");
        Term term2 = new Term(1, "Term 2", "04/01/2022", "09/30/2021");
        Course course1 = new Course(1, 1,"Fundamentals of Woodworking", Course.Status.ENROLLED, 3,"04/01/2021", "05/23/2021", "Bob Vila", "bob@vila.com", "123-456-7890", "This is a long note that has no meaning other than to fill space in the view and hopefully demonstrate some wrapping.");
        Course course2 = new Course(2, 2,"Fundamentals of Scuba", Course.Status.PLANNED, 5,"10/01/2022", "10/23/2022", "Jacques Cousteau", "jacques@cousteau.com", "123-456-7890", "This is a long note that has no meaning other than to fill space in the view and hopefully demonstrate some wrapping.");
        Assessment assessment1 = new Assessment(1, 1, "Assessment 1", "10/23/2021", "10/23/2021", "Students will show competence in the basics of woodworking while demonstrating proper safety and technique.", Assessment.Type.PERFORMANCE);
        Assessment assessment2 = new Assessment(1, 2, "Assessment 2", "10/23/2021", "10/23/2021", "Students will show competence in the basics of scuba diving while demonstrating proper safety and technique.", Assessment.Type.OBJECTIVE);

        //insert temporary data
        repository.insert(testUser);
        repository.insert(admin);

        repository.insert(term1);
        repository.insert(term2);
        repository.insert(course1);
        repository.insert(course2);
        repository.insert(assessment1);
        repository.insert(assessment2);
    }

    public static ZonedDateTime getZonedDateTime(String date) {
        ZonedDateTime zdt = null;
        LocalDate ld = LocalDate.parse(date, formatter);
        LocalDateTime ldt = ld.atStartOfDay();
        ZoneId zone = ZoneId.systemDefault();
        zdt = ZonedDateTime.of(ldt, zone);

        return zdt;
    }

    public static String formatDateHints() {
        return LocalDate.now().format(formatter);
    }

    public static boolean isValid(Context context, String title, String start, String end) {
        boolean validity = true;

        try {
            ZonedDateTime startDate = getZonedDateTime(start);
            ZonedDateTime endDate = getZonedDateTime(end);

            if (title.equals("") || start.equals("") || end.equals("")) {
                Toast.makeText(context, "Title and dates are required.", Toast.LENGTH_LONG).show();
                validity = false;
            } else if (endDate.isBefore(startDate)) {
                Toast.makeText(context, "The end date cannot be earlier than the start date.", Toast.LENGTH_LONG).show();
                validity = false;
            }
        } catch (Exception e) {
            Toast.makeText(context, "Dates must be in the format mm/dd/yyyy.", Toast.LENGTH_LONG).show();
            validity = false;
        }

        return validity;
    }

    public static int getCurrentUserID() {
        return userID;
    }

    private void setCurrentUserID(int ID) {
        userID = ID;
    }
}