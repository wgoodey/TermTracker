package com.whitneygoodey.termtracker.UI;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
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
    private FirebaseUser firebaseUser;
    public static int numAlert;
    private Repository repository;

    private final View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.googleSignInButton:
                    signIn();
                    break;
                case R.id.signOutButton:
                    signOut();
                    break;
                case R.id.userTextView:
                    loadTerms();
                    break;
                case R.id.enterArrow:
                    loadTerms();
                    break;
                case R.id.deleteUserText:
                    deleteUser();
                    break;
            }
        }
    };

    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;

    private FirebaseAuth firebaseAuth;
    private GoogleSignInClient googleSignInClient;

    private TextView userTextView;
    private TextView deleteLink;
    private SignInButton signInButton;
    private Button signOutButton;
    private ImageView enterArrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        repository = new Repository(getApplication());
//        setTempData(repository);

        List<User> userList = repository.getAllUsers();

        getSupportActionBar().hide();

        userTextView = findViewById(R.id.userTextView);
        enterArrow = findViewById(R.id.enterArrow);
        signInButton = findViewById(R.id.googleSignInButton);
        signOutButton = findViewById(R.id.signOutButton);
        deleteLink = findViewById(R.id.deleteUserText);

        signInButton.setOnClickListener(clickListener);
        signOutButton.setOnClickListener(clickListener);
        userTextView.setOnClickListener(clickListener);
        enterArrow.setOnClickListener(clickListener);
        deleteLink.setOnClickListener(clickListener);



        //initialize firebaseAuth and add authStateListener
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser listenerUser = firebaseAuth.getCurrentUser();
                if (listenerUser != null) {
                    firebaseUser = listenerUser;
                    updateUI(firebaseUser);
                    boolean found = false;
                    for (User user : userList) {
                        if (firebaseUser.getEmail().equals(user.getEmail())) {
                            found = true;
                            setCurrentUserID(user.getID());
                            break;
                        }
                    }
                    if (found && userID == -1) {
                        User newUser = new User(firebaseUser.getEmail(), "pass");
                        repository.insert(newUser);
                    }
                } else {
                    firebaseUser = null;
                    userID = -1;
                    updateUI(null);
                }
            }
        });

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);


    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        firebaseUser = firebaseAuth.getCurrentUser();
        updateUI(firebaseUser);
    }

    public void loadTerms() {
        Intent intent = new Intent(MainActivity.this, TermList.class);
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                updateUI(firebaseUser);
            }
        }
    }


    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            firebaseUser = firebaseAuth.getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                        }
                    }
                });
    }

    private void setTempData(Repository repository) {

        //create temporary data
        User admin = new User (1, "admin", "admin");
        User testUser = new User (2, "wgoodey@wgu.edu", "test");

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
            Toast.makeText(context, "Dates are required.", Toast.LENGTH_LONG).show();
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

    private void signIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signOut() {

        //Firebase sign out
        FirebaseAuth.getInstance().signOut();
        // Google sign out
        googleSignInClient.signOut();
//        updateUI(null);
    }

    private void updateUI(FirebaseUser user) {
        try {
            if (user == null) {
                userTextView.setText(getString(R.string.app_name));
                signInButton.setVisibility(View.VISIBLE);
                signOutButton.setVisibility(View.GONE);
                deleteLink.setVisibility(View.INVISIBLE);
                enterArrow.setVisibility(View.GONE);

            } else {
                userTextView.setText(user.getEmail());
                signInButton.setVisibility(View.GONE);
                signOutButton.setVisibility(View.VISIBLE);
                deleteLink.setVisibility(View.VISIBLE);
                enterArrow.setVisibility(View.VISIBLE);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void deleteUser() {
        int deleteKey = userID;

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//        builder.setCancelable(true);
        builder.setTitle("Confirm account deletion");
        builder.setMessage("Are you sure you want to delete this account and remove all associated data from the database?");
        builder.setIcon(R.drawable.ic_delete);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                String deleteMessage = firebaseUser.getEmail() + " successfully deleted";

                //TODO: re-authenticate user?

                // delete user
                firebaseUser.delete()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "User account deleted.");
                                    googleSignInClient.signOut();
                                    googleSignInClient.revokeAccess();
                                    //delete user's coursework
                                    deleteData(deleteKey);
                                    Toast.makeText(getApplicationContext(), deleteMessage, Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog deleteDialog = builder.create();
        deleteDialog.show();



    }

    private void deleteData(int owner) {
        List<User> users = repository.getAllUsers();
        List<Term> terms = repository.getAllTerms(owner);
        List<Course> courses = repository.getAllCourses(owner);
        List<Assessment> assessments = repository.getAllAssessments(owner);

        for (Assessment assessment : assessments) {
            repository.delete(assessment);
        }
        for (Course course : courses) {
            repository.delete(course);
        }
        for (Term term : terms) {
            repository.delete(term);
        }

        for (User user : users) {
            if (user.getID() == owner) {
                repository.delete(user);
            }
        }
    }

}