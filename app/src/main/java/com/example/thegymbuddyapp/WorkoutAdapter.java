package com.example.thegymbuddyapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class WorkoutAdapter extends BaseAdapter {
    Context context;
    ArrayList<workoutModel> arrayList;

    public WorkoutAdapter(Context context, ArrayList<workoutModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.workout, parent, false);
        }
        TextView name;
        TextView description;

        name = convertView.findViewById(R.id.workoutName);
        description = convertView.findViewById(R.id.workoutDescription);

        name.setText(arrayList.get(position).getName());
        description.setText(arrayList.get(position).getDescription());

        return convertView;
    }
}
