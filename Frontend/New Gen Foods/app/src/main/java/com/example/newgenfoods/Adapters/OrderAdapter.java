package com.example.newgenfoods.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.newgenfoods.Models.OrderItems;
import com.example.newgenfoods.R;

import java.util.ArrayList;

public class OrderAdapter extends ArrayAdapter<OrderItems> {

    private Context mContext;
    private int mResource;

    public OrderAdapter(@NonNull Context context, int resource, @NonNull ArrayList<OrderItems> objects) {
        super(context, resource, objects);

        this.mContext = context;
        this.mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        convertView = layoutInflater.inflate(mResource, parent, false);

        TextView id = (TextView) convertView.findViewById(R.id.id);
        TextView status = (TextView) convertView.findViewById(R.id.status);

        id.setText(getItem(position).getOrderNo());
        status.setText(getItem(position).getStatus());

        return convertView;
    }

}
