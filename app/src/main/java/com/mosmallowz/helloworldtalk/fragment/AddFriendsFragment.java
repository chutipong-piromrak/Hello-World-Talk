package com.mosmallowz.helloworldtalk.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.mosmallowz.helloworldtalk.R;
import com.mosmallowz.helloworldtalk.Users;
import com.mosmallowz.helloworldtalk.activity.Main2Activity;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by mossi on 11/16/2014.
 */
@SuppressWarnings("unused")
public class AddFriendsFragment extends Fragment {

    EditText inputEmailFriend;
    DatabaseReference myRef;
    FirebaseAuth mAuth;
    FirebaseUser myUser;
    ImageView imgFriend;
    TextView emailFriend;
    FrameLayout btnSearch;
    String email;

    LinearLayout contentAddFriends;


    public AddFriendsFragment() {
        super();
    }

    @SuppressWarnings("unused")
    public static AddFriendsFragment newInstance() {
        AddFriendsFragment fragment = new AddFriendsFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_add_friends, container, false);
        initInstances(rootView, savedInstanceState);
        return rootView;
    }

    private void init(Bundle savedInstanceState) {
        // Init Fragment level's variable(s) here

        myRef = FirebaseDatabase.getInstance().getReference();
        myUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        // Init 'View' instance(s) with rootView.findViewById here
        inputEmailFriend = rootView.findViewById(R.id.search_email);
        btnSearch = rootView.findViewById(R.id.btn_search);
        emailFriend = rootView.findViewById(R.id.email_friend);
        contentAddFriends = rootView.findViewById(R.id.content_add_friends);
        imgFriend = rootView.findViewById(R.id.img_friend);


        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                   searchFriends();
//                    testSearch();
                }
            }
        });

    }

    public boolean validate() {
        boolean valid = true;
        email = inputEmailFriend.getText().toString();
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            inputEmailFriend.setError("Invalid email");
            valid = false;
        } else {
            inputEmailFriend.setError(null);
        }
        return valid;
    }

    public void searchFriends() {
        if (email.equals(myUser.getEmail())) {
            Toast.makeText(getActivity(), "this is your email.", Toast.LENGTH_SHORT).show();
        } else {
            myRef.child("Relationship").child(myUser.getUid()).orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    View view = getActivity().getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    if (!dataSnapshot.exists()) {
                        myRef.child("Users").orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getValue() == null) {
                                    Toast.makeText(getActivity(), "This email is not found", Toast.LENGTH_SHORT).show();
                                } else {
                                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                                        final Users users = child.getValue(Users.class);
                                        contentAddFriends.setVisibility(View.VISIBLE);
                                        emailFriend.setText(users.getEmail());
                                        if (users.getPhotoUrl().equals("")) {
                                            Glide.with(getActivity())
                                                    .load(R.mipmap.ic_launcher_round)
                                                    .into(imgFriend);
                                        } else {
                                            Glide.with(getActivity())
                                                    .load(users.getPhotoUrl())
                                                    .into(imgFriend);
                                        }
                                        contentAddFriends.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                addFriends(users);
                                            }
                                        });
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    } else {
                        Snackbar.make(getView(),email + " is already your friend", Snackbar.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }

    private void addFriends(final Users users) {
        AlertDialog.Builder builderAddFriends = new AlertDialog.Builder(getActivity());
        builderAddFriends.setMessage("Are you sure to add " + users.getEmail() + " to your friend list ?");
        builderAddFriends.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Users myinfo = new Users();
                String idRoom = myRef.push().getKey();
                users.setIdRoom(idRoom);
                myinfo.setEmail(myUser.getEmail());
                myinfo.setUid(myUser.getUid());
                myinfo.setIdRoom(idRoom);
                try {
                    myinfo.setPhotoUrl(myUser.getPhotoUrl().toString());
                } catch (NullPointerException ex) {
                    myinfo.setPhotoUrl("");
                }
                myinfo.setName(myUser.getDisplayName());

                myRef.child("Relationship").child(myUser.getUid()).child(users.getUid()).setValue(users);
                myRef.child("Relationship").child(users.getUid()).child(myUser.getUid()).setValue(myinfo);
                inputEmailFriend.getText().clear();
                Intent intent = new Intent(getActivity(), Main2Activity.class);
                startActivity(intent);
                getActivity().finish();
                Toast.makeText(getActivity(), "Add " + users.getEmail() + " success!", Toast.LENGTH_SHORT).show();
            }
        });

        builderAddFriends.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builderAddFriends.show();

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
