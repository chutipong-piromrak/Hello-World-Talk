package com.mosmallowz.helloworldtalk.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mosmallowz.helloworldtalk.R;
import com.mosmallowz.helloworldtalk.Users;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by LeoMossi on 11/10/2017.
 */

public class FriendsAdapter extends BaseAdapter {

    private ArrayList<Users> listFriends;

    public FriendsAdapter(ArrayList<Users> listFriends) {
        this.listFriends = listFriends;
    }

    @Override
    public int getCount() {
        if (listFriends != null) {
            return listFriends.size();
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
            view = inflater.inflate(R.layout.list_item_friends, parent, false);
        }

        TextView friendName = view.findViewById(R.id.tv_friend_name);
        ImageView imgFriend = view.findViewById(R.id.img_friend);

        if (!listFriends.get(position).getPhotoUrl().equals("")) {
            Glide.with(parent.getContext())
                    .load(listFriends.get(position).getPhotoUrl())
                    .into(imgFriend);
        } else {
            Glide.with(parent.getContext())
                    .load(R.mipmap.ic_default)
                    .into(imgFriend);
        }
        friendName.setText(listFriends.get(position).getName());

        return view;
    }
}
