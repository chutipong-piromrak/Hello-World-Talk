package com.mosmallowz.helloworldtalk.fragment;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mosmallowz.helloworldtalk.Message;
import com.mosmallowz.helloworldtalk.R;
import com.mosmallowz.helloworldtalk.adapter.FriendMessageAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by mossi on 11/16/2014.
 */
@SuppressWarnings("unused")
public class ChatRoomFragment extends Fragment {

    DatabaseReference myRef;
    String keyRoom;
    EditText inputMessage;
    FrameLayout btnSend;
    ArrayList<Message> listMessage;
    ListView listView;
    FriendMessageAdapter adapter;
    Date currentTime;
    String localTime;
    FirebaseUser user;


    public ChatRoomFragment() {
        super();
    }

    @SuppressWarnings("unused")
    public static ChatRoomFragment newInstance() {
        ChatRoomFragment fragment = new ChatRoomFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_chat_room, container, false);
        initInstances(rootView, savedInstanceState);
        return rootView;
    }

    private void init(Bundle savedInstanceState) {
        // Init Fragment level's variable(s) here
        user = FirebaseAuth.getInstance().getCurrentUser();

        keyRoom = getActivity().getIntent().getExtras().get("keyRoom").toString();

        myRef = FirebaseDatabase.getInstance().getReference()
                .child("AllRooms").child(keyRoom);

    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        // Init 'View' instance(s) with rootView.findViewById here
        listView = rootView.findViewById(R.id.gv_chat_room);
        inputMessage = rootView.findViewById(R.id.input_message);
        btnSend = rootView.findViewById(R.id.btn_send);

        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+7:00"));
        Date currentLocalTime = cal.getTime();
        DateFormat date = new SimpleDateFormat("HH:mm a");
        // you can get seconds by adding  "...:ss" to it
        date.setTimeZone(TimeZone.getTimeZone("GMT+7:00"));
        localTime = date.format(currentLocalTime);

        listMessage = new ArrayList<>();
        listView = rootView.findViewById(R.id.gv_chat_room);
        adapter = new FriendMessageAdapter(listMessage);
        listView.setAdapter(adapter);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager connectivityManager = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                        connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                    //we are connected to a network
                    sendMessage();
                } else {
                    Toast.makeText(getActivity(), "No internet connection.", Toast.LENGTH_SHORT).show();
                }

            }
        });


        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Message message = dataSnapshot.getValue(Message.class);
                listMessage.add(message);
                adapter.notifyDataSetChanged();
                listView.setSelection(listMessage.size()-1);

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

    }

    private void sendMessage() {
        Message message = new Message();
        message.setName(user.getDisplayName());
        message.setEmail(user.getEmail());
        try {
            message.setPhotoUrl(user.getPhotoUrl().toString());
        }
        catch (NullPointerException ex) {
            message.setPhotoUrl("");
        }
        message.setTime(localTime);
        if (inputMessage.getText().toString().length() != 0) {
            message.setTextMessage(inputMessage.getText().toString());
            myRef.push().setValue(message);
            inputMessage.getText().clear();
        }
        Log.d("keyRoom",keyRoom+"#####");
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
