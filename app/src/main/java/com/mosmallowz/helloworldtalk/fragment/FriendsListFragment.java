package com.mosmallowz.helloworldtalk.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.mosmallowz.helloworldtalk.R;
import com.mosmallowz.helloworldtalk.Users;
import com.mosmallowz.helloworldtalk.activity.AddFriendsActivity;
import com.mosmallowz.helloworldtalk.activity.ChatRoomActivity;
import com.mosmallowz.helloworldtalk.adapter.FriendsAdapter;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by mossi on 11/16/2014.
 */
@SuppressWarnings("unused")
public class FriendsListFragment extends Fragment {

    DatabaseReference myRef;
    ArrayList<Users> listFriends;
    SwipeMenuListView listViewFriends;
    FriendsAdapter friendsAdapter;

    FirebaseUser user;
    String myId;

    ProgressBar progressBar;

    FrameLayout bgGray;
    TextView tvNoItem;


    public FriendsListFragment() {
        super();
    }

    @SuppressWarnings("unused")
    public static FriendsListFragment newInstance() {
        FriendsListFragment fragment = new FriendsListFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_friends_list, container, false);
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
        setHasOptionsMenu(true);

        bgGray = rootView.findViewById(R.id.bg_gray);
        tvNoItem = rootView.findViewById(R.id.tv_no_item);

        //Friends
        listViewFriends = rootView.findViewById(R.id.lv_friends_list);
        progressBar = rootView.findViewById(R.id.progress_bar);
        listFriends = new ArrayList<>();
        friendsAdapter = new FriendsAdapter(listFriends);
        listViewFriends.setAdapter(friendsAdapter);


        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            testGetAllFriends();
        } else {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(getActivity(), "No internet connection.", Toast.LENGTH_SHORT).show();
        }
//        getAllFriends();
        //Friends
        listViewFriends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), ChatRoomActivity.class);
                intent.putExtra("keyRoom", listFriends.get(position).getIdRoom());
                intent.putExtra("nameFriend", listFriends.get(position).getName());
                intent.putExtra("photoUrlFriend", listFriends.get(position).getPhotoUrl());
                Log.d("keyRoom", listFriends.get(position).getIdRoom() + "@@@");
                startActivity(intent);
            }
        });

        listViewFriends.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String email = listFriends.get(position).getEmail();
                myRef.child("Relationship").child(user.getUid()).child(listFriends.get(position).getUid()).removeValue();
                listFriends.remove(position);
                friendsAdapter.notifyDataSetChanged();
                Toast.makeText(getActivity(), "remove " + email + " success!!", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        swipeMenuCreator();
    }

    private void testGetAllFriends() {

        myRef.child("Relationship").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                Log.d("checkFriends","have friends");
                listFriends.clear();
                if (dataSnapshot.exists()) {
                    progressBar.setVisibility(View.VISIBLE);
                    bgGray.setVisibility(View.GONE);
                    tvNoItem.setVisibility(View.GONE);

                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        final Users relUsers = child.getValue(Users.class);
                        Log.d("checkFriends", "have friends " + relUsers.getEmail());
                        Query query = myRef.child("Users").orderByChild("uid").equalTo(relUsers.getUid());
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot child : dataSnapshot.getChildren()) {
                                    Users uerUser = child.getValue(Users.class);
                                    relUsers.setPhotoUrl(uerUser.getPhotoUrl());
                                    relUsers.setName(uerUser.getName());
                                    listFriends.add(relUsers);
                                    friendsAdapter.notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                    progressBar.setVisibility(View.GONE);
                } else {
                    bgGray.setVisibility(View.VISIBLE);
                    tvNoItem.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void swipeMenuCreator() {

        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getActivity());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(170);
                // set a icon
                deleteItem.setIcon(R.drawable.ic_delete);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };

        // set creator
        listViewFriends.setMenuCreator(creator);

        listViewFriends.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        // delete
                        AlertDialog.Builder builderLogOut = new AlertDialog.Builder(getActivity());
                        builderLogOut.setMessage("Are you sure to delete " + listFriends.get(position).getName() + " ?");
                        builderLogOut.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String email = listFriends.get(position).getEmail();
                                myRef.child("Relationship").child(user.getUid()).child(listFriends.get(position).getUid()).removeValue();
                                myRef.child("Relationship").child(listFriends.get(position).getUid()).child(user.getUid()).removeValue();
                                Snackbar.make(getView(), "ลบ " + listFriends.get(position).getName() + " delete successfully.", Snackbar.LENGTH_LONG).show();
                                listFriends.remove(position);
                                friendsAdapter.notifyDataSetChanged();
                            }
                        });
                        builderLogOut.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builderLogOut.show();
                        break;
                }
                // false : close the menu; true : not close the menu
                return false;
            }
        });
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_add_friends, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        Intent intent;
        switch (item.getItemId()) {
            case R.id.add_friends:
                intent = new Intent(getActivity(), AddFriendsActivity.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
