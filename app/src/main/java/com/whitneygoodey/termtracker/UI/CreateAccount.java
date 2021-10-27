package com.whitneygoodey.termtracker.UI;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.whitneygoodey.termtracker.Database.Repository;
import com.whitneygoodey.termtracker.Entities.User;
import com.whitneygoodey.termtracker.R;

public class CreateAccount extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    private EditText emailField;
    private EditText passwordField;
    private EditText confirmPasswordField;
    private Button confirm;

    private TextView errorMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        firebaseAuth = FirebaseAuth.getInstance();

        errorMessage = findViewById(R.id.error);
        ImageView passCheck = findViewById(R.id.passCheck);
        ImageView confirmCheck = findViewById(R.id.confirmCheck);
        emailField = findViewById(R.id.emailEdit);

        passwordField = findViewById(R.id.passwordEdit);
        confirmPasswordField = findViewById(R.id.confirmPasswordEdit);

        passwordField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (Validator.isValidPassword(passwordField.getText().toString())) {
                    passCheck.setVisibility(View.VISIBLE);
                } else {
                    passCheck.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        confirmPasswordField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (Validator.isMatchingPassword(passwordField.getText().toString(),
                        confirmPasswordField.getText().toString())) {
                    confirmCheck.setVisibility(View.VISIBLE);
                } else {
                    confirmCheck.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        confirm = findViewById(R.id.confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = emailField.getText().toString().trim();
                String password = passwordField.getText().toString();

                if (!Validator.isValidEmail(email)) {
                    errorMessage.setText("Please enter a valid email address.");
                } else if (!Validator.isValidPassword(password) ||
                        !Validator.isMatchingPassword(password, confirmPasswordField.getText().toString())) {
                    errorMessage.setText("Password must be at least 8 characters\n " +
                            "with at least one symbol and one digit.");
                } else {
                    createAccount(email, password);
                    finish();
                }
            }
        });

    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if(currentUser != null){
            Toast.makeText(this, currentUser.getEmail(), Toast.LENGTH_LONG).show();
        } else {
            errorMessage.setText("");
        }
    }




    private void createAccount(String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
//                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            Toast.makeText(CreateAccount.this, "Authentication successful.",
                                    Toast.LENGTH_SHORT).show();
                            createDBUser(user);
                        } else {
                            // If sign in fails, display a message to the user.
//                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(CreateAccount.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private void createDBUser(FirebaseUser user) {
        Repository repository = new Repository(getApplication());
        User newUser = new User(user.getEmail());
        repository.insert(newUser);
    }




}