package com.example.newgenfoods.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.newgenfoods.Models.MenuItems;
import com.example.newgenfoods.R;

import java.util.ArrayList;

public class MenuAdapter extends ArrayAdapter<MenuItems> {

    private Context mContext;
    private int mResource;

    public MenuAdapter(@NonNull Context context, int resource, @NonNull ArrayList<MenuItems> objects) {
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
        TextView ingredients = (TextView) convertView.findViewById(R.id.ingredients);
        TextView price = (TextView) convertView.findViewById(R.id.price);

        title.setText(getItem(position).getTitle());
        ingredients.setText(getItem(position).getIngredients());
        price.setText(getItem(position).getPrice());

        return convertView;
    }

}
