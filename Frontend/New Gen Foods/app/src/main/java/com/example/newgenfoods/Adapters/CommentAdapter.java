package com.example.newgenfoods.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.newgenfoods.Models.CommentItems;
import com.example.newgenfoods.R;

import java.util.ArrayList;

public class CommentAdapter extends ArrayAdapter<CommentItems> {

    private Context mContext;
    private int mResource;

    public CommentAdapter(@NonNull Context context, int resource, @NonNull ArrayList<CommentItems> objects) {
        super(context, resource, objects);

        this.mContext = context;
        this.mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        convertView = layoutInflater.inflate(mResource, parent, false);

        TextView name = (TextView) convertView.findViewById(R.id.name);
        TextView comment = (TextView) convertView.findViewById(R.id.comment);

        name.setText(getItem(position).getName());
        comment.setText(getItem(position).getComment());

        return convertView;
    }

}