package com.mosmallowz.helloworldtalk.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mosmallowz.helloworldtalk.R;

import java.util.ArrayList;

/**
 * Created by LeoMossi on 11/26/2017.
 */

public class PublicAdapter extends BaseAdapter {

    ArrayList<String> listPublic;

    public PublicAdapter(ArrayList<String> listPublic) {
        this.listPublic = listPublic;
    }

    @Override
    public int getCount() {
        return listPublic.size();
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
            view = inflater.inflate(R.layout.list_item_public, parent, false);
        }

        TextView groupName = view.findViewById(R.id.tv_public);
        ImageView imgGlobal = view.findViewById(R.id.img_public);

        groupName.setText(listPublic.get(position));
        imgGlobal.setImageResource(R.mipmap.ic_global);
        return view;
    }
}
