package com.mosmallowz.helloworldtalk.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.mosmallowz.helloworldtalk.R;
import com.mosmallowz.helloworldtalk.Users;
import com.mosmallowz.helloworldtalk.activity.LoginActivity;
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
public class SettingFragment extends Fragment {

    DatabaseReference myRef;
    FirebaseUser user;

    ImageView imgProfile;
    TextView nameProfile;
    TextView emailProfile;

    Button btnLogOut;
    Button btnDelAcc;

    SpotsDialog spotsDialog;

    public SettingFragment() {
        super();
    }

    @SuppressWarnings("unused")
    public static SettingFragment newInstance() {
        SettingFragment fragment = new SettingFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_setting, container, false);
        initInstances(rootView, savedInstanceState);
        return rootView;
    }

    private void init(Bundle savedInstanceState) {
        // Init Fragment level's variable(s) here
        user = FirebaseAuth.getInstance().getCurrentUser();
        myRef = FirebaseDatabase.getInstance().getReference();
    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        // Init 'View' instance(s) with rootView.findViewById here

        imgProfile = (ImageView) rootView.findViewById(R.id.img_profile);
        nameProfile = (TextView) rootView.findViewById(R.id.name_profile);
        emailProfile = (TextView) rootView.findViewById(R.id.email_profile);
        btnLogOut = (Button) rootView.findViewById(R.id.btn_log_out);
        btnDelAcc = (Button) rootView.findViewById(R.id.btn_del_acc);

        nameProfile.setText(user.getDisplayName());
        emailProfile.setText(user.getEmail());

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOut();
            }
        });

        btnDelAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delAccount();
            }
        });
    }

    public void logOut() {
        AlertDialog.Builder builderLogOut = new AlertDialog.Builder(getActivity());
        builderLogOut.setMessage("Do you want to sign out ?");
        builderLogOut.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                spotsDialog = new SpotsDialog(getActivity(), "Signing out..");
                spotsDialog.show();
                SharedPreferences prefs = getActivity().getBaseContext()
                        .getSharedPreferences("RememberMe", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.clear();
                editor.apply();
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
                spotsDialog.dismiss();
            }
        });
        builderLogOut.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builderLogOut.show();
    }

    public void delAccount() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Do you want to delete your account ?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                spotsDialog = new SpotsDialog(getActivity(), "Deleting account..");
                spotsDialog.show();
                myRef.child("Relationship").child(user.getUid()).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Users friends = dataSnapshot.getValue(Users.class);
                        myRef.child("Relationship").child(friends.getUid()).child(user.getUid()).removeValue();
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                user.delete()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    myRef.child("Relationship").child(user.getUid()).removeValue();
                                    myRef.child("Users").child(user.getUid()).removeValue();
                                    SharedPreferences prefs = getActivity().getBaseContext()
                                            .getSharedPreferences("RememberMe", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = prefs.edit();
                                    editor.clear();
                                    editor.apply();
                                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                                    startActivity(intent);
                                    getActivity().finish();
                                    Toast.makeText(getActivity(), "delete account successfully.", Toast.LENGTH_SHORT).show();
                                    Log.d("deleteUser", "User account deleted.");
                                    spotsDialog.dismiss();
                                }
                            }
                        });
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
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
