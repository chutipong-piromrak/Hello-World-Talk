package com.mosmallowz.helloworldtalk.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mosmallowz.helloworldtalk.Message;
import com.mosmallowz.helloworldtalk.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

/**
 * Created by LeoMossi on 11/10/2017.
 */

public class MessageAdapter extends BaseAdapter {

    private ArrayList<Message> listMessage;
    public MessageAdapter(ArrayList<Message> listMessage) {
        this.listMessage = listMessage;
    }
    private FirebaseAuth mAuth;

    @Override

    public int getCount() {
        if (listMessage != null) {
            return listMessage.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_item_message, parent, false);
        }
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        TextView textTimeStamp = (TextView) view.findViewById(R.id.tv_time_stamp);
        TextView textIMessage = (TextView) view.findViewById(R.id.i_message);
        TextView textUMessage = (TextView) view.findViewById(R.id.u_message);
        TextView textUUsername = (TextView) view.findViewById(R.id.u_username);
        ImageView imgProfile = (ImageView) view.findViewById(R.id.u_img);

        if (user.getEmail().equals(listMessage.get(position).getEmail())) {
            textIMessage.setVisibility(View.VISIBLE);
            textIMessage.setText(listMessage.get(position).getTextMessage());
            textUMessage.setVisibility(View.GONE);
            textUUsername.setVisibility(View.GONE);
            imgProfile.setVisibility(View.GONE);
        } else {
            textUMessage.setVisibility(View.VISIBLE);
            textUUsername.setVisibility(View.VISIBLE);
            imgProfile.setVisibility(View.VISIBLE);
            textUMessage.setText(listMessage.get(position).getTextMessage());
            textUUsername.setText(listMessage.get(position).getEmail());
            textIMessage.setVisibility(View.GONE);
        }

        textTimeStamp.setText(listMessage.get(position).getTime());

        Log.d("timeMos", listMessage.get(position).getTime() + " " + textTimeStamp.getText().toString());

        return view;
    }
}
