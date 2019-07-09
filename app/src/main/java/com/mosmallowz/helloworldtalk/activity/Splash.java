package com.mosmallowz.helloworldtalk.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.mosmallowz.helloworldtalk.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class Splash extends AppCompatActivity {

    Handler handler;
    Runnable runnable;
    long delay_time;
    long time = 3000L;

    SharedPreferences prefs;
    FirebaseAuth mAuth;
    ProgressBar progressBar;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(base));
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progress_bar);
        prefs = getBaseContext().getSharedPreferences("RememberMe", Context.MODE_PRIVATE);
        final String emailRemember = prefs.getString("email", null);
        final String passwordRemember = prefs.getString("password", null);
        Log.d("emailpassword", emailRemember+""+passwordRemember);
        if (emailRemember != null && passwordRemember != null) {
            handler = new Handler();
            runnable = new Runnable() {
                public void run() {
                    ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                    if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                            connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                        //we are connected to a network
                        signInWithEmailAndPassword(emailRemember, passwordRemember);

                    } else {
                        Intent intent = new Intent(Splash.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                        Toast.makeText(Splash.this, "ไม่มีการเชื่อมต่ออินเทอร์เน็ต", Toast.LENGTH_SHORT).show();
                    }

                }
            };
        } else {
            handler = new Handler();
            runnable = new Runnable() {
                public void run() {
                    Intent intent = new Intent(Splash.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            };
        }
    }

    public void signInWithEmailAndPassword(final String email, final String password) {
        Log.d("email", email+""+password);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Toast.makeText(LoginActivity.this, "เข้าสู่ระบบสำเร็จ", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Splash.this, Main2Activity.class);
                            startActivity(intent);
                            finish();
                            overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
                            overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);

                        } else {
                            // If sign in fails, display a message to the user.
//                            Toast.makeText(Splash.this, "เข้าสู่ระบบไม่สำเร็จ" + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void onResume() {
        super.onResume();
        delay_time = time;
        handler.postDelayed(runnable, delay_time);
        time = System.currentTimeMillis();
    }

    public void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
        time = delay_time - (System.currentTimeMillis() - time);
    }
}