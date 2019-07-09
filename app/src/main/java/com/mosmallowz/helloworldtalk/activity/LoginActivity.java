package com.mosmallowz.helloworldtalk.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.mosmallowz.helloworldtalk.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import dmax.dialog.SpotsDialog;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class LoginActivity extends AppCompatActivity {

    EditText emailText;
    EditText passwordText;
    Button loginButton;
    TextView signLink;
    CheckBox rememberMe;
    TextView forgotPassword;

    private String email;
    private String password;
    private SpotsDialog dialog;

    private FirebaseAuth mAuth;
    FirebaseUser user;
    String emailIntent;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        emailText = findViewById(R.id.input_email);
        passwordText = findViewById(R.id.input_password);
        loginButton = findViewById(R.id.btn_login);
        signLink = findViewById(R.id.link_signup);
        rememberMe = findViewById(R.id.check_remember);
        forgotPassword = findViewById(R.id.forgot_password);

        try {
            emailIntent = getIntent().getExtras().get("email").toString();
            emailText.setText(emailIntent);
            passwordText.requestFocus();
        } catch (NullPointerException n){

        }

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                //        startActivity(intent);
                //        finish();
                ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                        connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                    //we are connected to a network
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    login();
                } else {
                    Toast.makeText(view.getContext(), "No internet connection.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        signLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ForgotPasswordActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.push_right_in,R.anim.push_right_out);
            }
        });
    }

    public void login() {

        if (!validate()) {
            return;
        }

        dialog = new SpotsDialog(this, "");
        dialog.show();

        signInWithEmailAndPassword(email, password);


    }

    public boolean validate() {
        boolean valid = true;

        email = emailText.getText().toString();
        password = passwordText.getText().toString();

        if ((email.isEmpty()) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailText.setError("Invalid email.");
            valid = false;
        } else {
            emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 6 || password.length() > 16) {
            passwordText.setError("between 4 and 16 alphanumeric characters.");
            valid = false;
        } else {
            passwordText.setError(null);
        }

        return valid;
    }

    public void signInWithEmailAndPassword(final String email, final String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(LoginActivity.this, "Log in successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, Main2Activity.class);
                            startActivity(intent);
                            finish();
                            overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
                            overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);

                            if (rememberMe.isChecked()) {
                                SharedPreferences prefs = getBaseContext().getSharedPreferences("RememberMe", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putString("email", email);
                                editor.putString("password", password);
                                editor.apply();
                            }

                        } else {
                            // If sign in fails, display a message to the user.
//                            if (task.getException().getMessage().equals("There is no user record corresponding to this identifier. The user may have been deleted")) {
//                                Toast.makeText(LoginActivity.this, "ไม่มีบัญชีนี้อยู่ในระบบ", Toast.LENGTH_SHORT).show();
//                            } else {
//
//                            }
                            Toast.makeText(LoginActivity.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                            passwordText.getText().clear();
                            rememberMe.setChecked(false);
                        }
                        dialog.dismiss();
                    }

                });
    }
}