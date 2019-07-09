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
import com.mosmallowz.helloworldtalk.Group;
import com.mosmallowz.helloworldtalk.R;
import com.mosmallowz.helloworldtalk.activity.ChatGroupRoomActivity;
import com.mosmallowz.helloworldtalk.activity.CreateGroupsActivity;
import com.mosmallowz.helloworldtalk.adapter.GroupAdapter;
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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by mossi on 11/16/2014.
 */
@SuppressWarnings("unused")
public class GroupsListFragment extends Fragment {

    DatabaseReference myRef;
    FirebaseUser user;
    GroupAdapter adapter;
    SwipeMenuListView listView;
    ArrayList<Group> listGroups;
    ProgressBar progressBar;

    FrameLayout bgGray;
    TextView tvNoItem;


    public GroupsListFragment() {
        super();
    }

    @SuppressWarnings("unused")
    public static GroupsListFragment newInstance() {
        GroupsListFragment fragment = new GroupsListFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_groups_list, container, false);
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

        listView = rootView.findViewById(R.id.lv_groups_list);
        progressBar = rootView.findViewById(R.id.progress_bar);
        listGroups = new ArrayList<>();
        adapter = new GroupAdapter(listGroups);
        listView.setAdapter(adapter);

        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            testGetAllGroups();
        } else {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(getActivity(), "No internet connection.", Toast.LENGTH_SHORT).show();
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), ChatGroupRoomActivity.class);
                intent.putExtra("idGroup", listGroups.get(position).getIdGroup());
                intent.putExtra("nameGroup", listGroups.get(position).getNameGroup());
                intent.putExtra("photoUrlGroup", listGroups.get(position).getPhotoUrl());
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String nameGroup = listGroups.get(position).getNameGroup();
                myRef.child("Groups").child(user.getUid()).child(listGroups.get(position).getIdGroup()).removeValue();
                listGroups.remove(position);
                adapter.notifyDataSetChanged();
                Toast.makeText(getActivity(), "remove " + nameGroup + " success!!", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        swipeMenuCreator();
    }

    private void testGetAllGroups() {
        myRef.child("Groups").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    listGroups.clear();
                    progressBar.setVisibility(View.VISIBLE);
                    bgGray.setVisibility(View.GONE);
                    tvNoItem.setVisibility(View.GONE);

                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        Group group = child.getValue(Group.class);
                        listGroups.add(group);
                        adapter.notifyDataSetChanged();
                        Log.d("groups", dataSnapshot.getKey());
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
        listView.setMenuCreator(creator);

        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        // delete
                        AlertDialog.Builder builderLogOut = new AlertDialog.Builder(getActivity());
                        builderLogOut.setMessage("Are you sure to delete " + listGroups.get(position).getNameGroup() + " ?");
                        builderLogOut.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                myRef.child("Groups").child(user.getUid()).child(listGroups.get(position).getIdGroup()).removeValue();
                                Snackbar.make(getView(), "delete " + listGroups.get(position).getNameGroup() + " successfully.", Snackbar.LENGTH_LONG).show();
                                listGroups.remove(position);
                                adapter.notifyDataSetChanged();
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
        inflater.inflate(R.menu.menu_create_groups, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        Intent intent;
        switch (item.getItemId()) {
            case R.id.create_groups:
                intent = new Intent(getActivity(), CreateGroupsActivity.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
