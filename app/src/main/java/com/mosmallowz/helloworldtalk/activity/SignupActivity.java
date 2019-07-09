package com.mosmallowz.helloworldtalk.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.mosmallowz.helloworldtalk.R;
import com.mosmallowz.helloworldtalk.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import dmax.dialog.SpotsDialog;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SignupActivity extends AppCompatActivity {

    DatabaseReference myRef;

    TextView linkLogin;
    EditText inputName;
    EditText inputEmail;
    EditText inputPassword;
    EditText inputReEnterPassword;
    Button signupButton;

    String name;
    String email;
    String password;
    private SpotsDialog dialog;

    private FirebaseAuth mAuth;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();
        myRef = FirebaseDatabase.getInstance().getReference();

        linkLogin = findViewById(R.id.link_login);
        inputName = findViewById(R.id.input_name);
        inputEmail = findViewById(R.id.input_email);
        inputPassword = findViewById(R.id.input_password);
        inputReEnterPassword = findViewById(R.id.input_reEnterPassword);
        signupButton = findViewById(R.id.btn_signup);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp();
            }
        });

        linkLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
            }
        });
    }

    private void signUp() {
        if (validate()) {
            dialog = new SpotsDialog(this, "Registering..");
            dialog.show();
            createUserWithEmailAndPassword();
        }
    }

    private boolean validate() {
        boolean valid = true;

        name = inputName.getText().toString();
        email = inputEmail.getText().toString();
        password = inputPassword.getText().toString();
        String reEnterPassword = inputReEnterPassword.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            inputName.setError("at least 3 characters.");
            valid = false;
        } else {
            inputName.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            inputEmail.setError("enter a valid email address.");
            valid = false;
        } else {
            inputEmail.setError(null);
        }

        if (password.isEmpty() || password.length() < 6 || password.length() > 16) {
            inputPassword.setError("between 4 and 16 alphanumeric characters.");
            valid = false;
        } else {
            inputPassword.setError(null);
        }

        if (reEnterPassword.isEmpty() || reEnterPassword.length() < 6 || reEnterPassword.length() > 16 || !(reEnterPassword.equals(password))) {
            inputReEnterPassword.setError("Password Do not match.");
            valid = false;
        } else {
            inputReEnterPassword.setError(null);
        }

        return valid;
    }

    private void createUserWithEmailAndPassword() {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            //Update User profile in authen
                            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            Log.d("testuser", user.getEmail());
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name)
                                    .build();

                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                //Create user profile in database
                                                Users users = new Users();
                                                users.setName(name);
                                                users.setPhotoUrl("");
                                                users.setUid(user.getUid());
                                                users.setEmail(user.getEmail());
                                                users.setIdRoom("");
                                                myRef.child("Users").child(user.getUid()).setValue(users);

                                                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                                                intent.putExtra("email", user.getEmail());
                                                startActivity(intent);
                                                finish();
                                                Toast.makeText(SignupActivity.this, "Register successfully.", Toast.LENGTH_SHORT).show();
                                                dialog.dismiss();
                                            }
                                        }
                                    });


                        } else {
                            Toast.makeText(SignupActivity.this, "\n" +
                                    "Registration failed. Please try again.", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            // If sign in fails, display a message to the user.
                        }
                    }
                });
    }


}
