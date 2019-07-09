package com.mosmallowz.helloworldtalk.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.mosmallowz.helloworldtalk.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import dmax.dialog.SpotsDialog;

/**
 * Created by mossi on 11/16/2014.
 */
@SuppressWarnings("unused")
public class ForgotPasswordFragment extends Fragment {

    DatabaseReference myRef;
    FirebaseUser user;
    FirebaseAuth auth;

    EditText emailRegistered;
    Button btnSubmit;
    TextView backToLogin;
    String emailAddress;
    SpotsDialog dialog;


    public ForgotPasswordFragment() {
        super();
    }

    @SuppressWarnings("unused")
    public static ForgotPasswordFragment newInstance() {
        ForgotPasswordFragment fragment = new ForgotPasswordFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(savedInstanceState);

        if (savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_forgot_password, container, false);
        initInstances(rootView, savedInstanceState);
        return rootView;
    }

    private void init(Bundle savedInstanceState) {
        // Init Fragment level's variable(s) here
        user = FirebaseAuth.getInstance().getCurrentUser();
        auth = FirebaseAuth.getInstance();
        myRef = FirebaseDatabase.getInstance().getReference();
    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        // Init 'View' instance(s) with rootView.findViewById here

        emailRegistered = (EditText) rootView.findViewById(R.id.email_registered);
        btnSubmit = (Button) rootView.findViewById(R.id.btn_submit);
        backToLogin = (TextView) rootView.findViewById(R.id.back_to_login);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    dialog = new SpotsDialog(getActivity(), "Sending reset password email to your email.");
                    dialog.show();

                    auth.sendPasswordResetEmail(emailAddress)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    dialog.dismiss();
                                    Toast.makeText(getActivity(), "Email has been sent.", Toast.LENGTH_SHORT).show();
//                                    getActivity().finish();
//                                    getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                                }
                                else {
                                    dialog.dismiss();
                                    Toast.makeText(getActivity(), "Fail", Toast.LENGTH_SHORT).show();
//                                    getActivity().finish();
//                                    getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                                }
                            }
                        });
//                    myRef.child("Users").orderByChild("email").equalTo(emailAddress).addChildEventListener(new ChildEventListener() {
//                        @Override
//                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//
//                            auth.sendPasswordResetEmail(emailAddress)
//                                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<Void> task) {
//                                        if (task.isSuccessful()) {
//                                            dialog.dismiss();
//                                            Toast.makeText(getActivity(), "Email has been sent.", Toast.LENGTH_SHORT).show();
//                                            getActivity().finish();
//                                            getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
//                                        }
//                                    }
//                                });
//                        }
//                        @Override
//                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//                        }
//
//                        @Override
//                        public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//                        }
//
//                        @Override
//                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//                        }
//
//                        @Override
//                        public void onCancelled(DatabaseError databaseError) {
//
//                        }
//                    });
//                    Log.d("emailsent", ""+isRegistered);
//                    Toast.makeText(getActivity(), "This email is not found", Toast.LENGTH_SHORT).show();

                }
            }
        });


        backToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
                getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }

    public boolean validate() {
        boolean valid = true;
        emailAddress = emailRegistered.getText().toString();
        if (emailAddress.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()) {
            emailRegistered.setError("Invalid email.");
            valid = false;
        }
        return valid;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    /*
     * Save Instance State Here
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save Instance State here
    }

    /*
     * Restore Instance State Here
     */
    @SuppressWarnings("UnusedParameters")
    private void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore Instance State here
    }

}
