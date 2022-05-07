package com.example.newgenfoods.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.newgenfoods.Models.OrderDetails;
import com.example.newgenfoods.R;

import java.util.ArrayList;

public class OrderDetailsAdapter extends ArrayAdapter<OrderDetails> {

    private Context mContext;
    private int mResource;

    public OrderDetailsAdapter(@NonNull Context context, int resource, @NonNull ArrayList<OrderDetails> objects) {
        super(context, resource, objects);

        this.mContext = context;
        this.mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        convertView = layoutInflater.inflate(mResource, parent, false);

        TextView title = (TextView) convertView.findViewById(R.id.title);
        TextView addon = (TextView) convertView.findViewById(R.id.addon);
        TextView notice = (TextView) convertView.findViewById(R.id.notice);
        TextView qty = (TextView) convertView.findViewById(R.id.qty);

        String addonName = getItem(position).getAddon();
        if (!addonName.equals("")) {
            addonName = "with " + addonName;
        }

        title.setText(getItem(position).getTitle());
        addon.setText(addonName);
        notice.setText(getItem(position).getNotice());
        qty.setText(getItem(position).getQty() + " Items");

        return convertView;
    }

}