package com.mosmallowz.helloworldtalk.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mosmallowz.helloworldtalk.Group;
import com.mosmallowz.helloworldtalk.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by LeoMossi on 11/20/2017.
 */

public class GroupAdapter extends BaseAdapter {

    ArrayList<Group> listGroup;

    public GroupAdapter(ArrayList<Group> listGroup) {
        this.listGroup = listGroup;
    }

    @Override
    public int getCount() {
        return listGroup.size();
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
            view = inflater.inflate(R.layout.list_item_groups, parent, false);
        }

        TextView groupName = view.findViewById(R.id.tv_group);
        ImageView imgGroup = view.findViewById(R.id.img_group);

        if (!listGroup.get(position).getPhotoUrl().equals("")) {
            Glide.with(parent.getContext())
                    .load(listGroup.get(position).getPhotoUrl())
                    .into(imgGroup);
        } else {
            Glide.with(parent.getContext())
                    .load(R.mipmap.ic_default)
                    .into(imgGroup);
        }

        groupName.setText(listGroup.get(position).getNameGroup());

        return view;
    }
}
