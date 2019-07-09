package com.mosmallowz.helloworldtalk.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mosmallowz.helloworldtalk.Message;
import com.mosmallowz.helloworldtalk.R;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

/**
 * Created by LeoMossi on 11/26/2017.
 */

public class FriendMessageAdapter extends BaseAdapter{
    private ArrayList<Message> listMessage;

    public FriendMessageAdapter(ArrayList<Message> listMessage) {
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
            view = inflater.inflate(R.layout.list_item_friends_message, parent, false);
        }
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        TextView textMyDate = (TextView) view.findViewById(R.id.tv_my_date);
        TextView textUDate = (TextView) view.findViewById(R.id.tv_u_date);
        TextView textIMessage = (TextView) view.findViewById(R.id.i_message);
        TextView textUMessage = (TextView) view.findViewById(R.id.u_message);
        ImageView imgProfile = (ImageView) view.findViewById(R.id.u_img);

        if (user.getEmail().equals(listMessage.get(position).getEmail())) {
            textIMessage.setVisibility(View.VISIBLE);
            textMyDate.setVisibility(View.VISIBLE);
            textIMessage.setText(listMessage.get(position).getTextMessage());
            textMyDate.setText(listMessage.get(position).getTime());
            textUMessage.setVisibility(View.GONE);
            imgProfile.setVisibility(View.GONE);
            textUDate.setVisibility(View.GONE);
        } else {
            textUMessage.setVisibility(View.VISIBLE);
            imgProfile.setVisibility(View.VISIBLE);
            textUDate.setVisibility(View.VISIBLE);
            textUMessage.setText(listMessage.get(position).getTextMessage());
            textUDate.setText(listMessage.get(position).getTime());
            textIMessage.setVisibility(View.GONE);
            textMyDate.setVisibility(View.GONE);


            if (listMessage.get(position).getPhotoUrl().equals("")) {
                Glide.with(parent.getContext())
                        .load(R.mipmap.ic_launcher_round)
                        .into(imgProfile);
            } else {
                Glide.with(parent.getContext())
                        .load(listMessage.get(position).getPhotoUrl())
                        .into(imgProfile);
            }
        }

        return view;
    }
}
