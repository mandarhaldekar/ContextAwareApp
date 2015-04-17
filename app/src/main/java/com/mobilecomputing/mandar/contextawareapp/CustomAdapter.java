package com.mobilecomputing.mandar.contextawareapp;

/**
 * Created by Mandar on 4/17/2015.
 */
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
public class CustomAdapter extends ArrayAdapter{
        ScheduleModel[] modelItems = null;
        Context context;
public CustomAdapter(Context context, ScheduleModel[] resource) {
        super(context,R.layout.row,resource);
        // TODO Auto-generated constructor stub
        this.context = context;
        this.modelItems = resource;
        }
@Override
public View getView(int position, View convertView, ViewGroup parent)
{
        // TODO Auto-generated method stub
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.row, parent, false);
            TextView name = (TextView) convertView.findViewById(R.id.textView1);
            CheckBox cb = (CheckBox) convertView.findViewById(R.id.checkBox1);
            name.setText(modelItems[position].getContent());
            if(modelItems[position].getValue() == 1)
               cb.setChecked(true);
            else
                cb.setChecked(false);

            return convertView;
        }
 }
