package com.mobilecomputing.mandar.contextawareapp;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class ViewSchedule extends ActionBarActivity implements View.OnClickListener {
    Button deleteButton;
    ListView lv;
    ArrayList<ScheduleModel> modelItems;
    DBManager db;
    private List<UserInfo> allScheduleList;
    CustomAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_schedule);
        lv = (ListView) findViewById(R.id.listOfSchedule);

        deleteButton = (Button)findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(this);


        db = new DBManager(this);
        allScheduleList = db.getAllUserInfo();



        modelItems = new ArrayList<ScheduleModel>();
        for(int i=0;i<allScheduleList.size();i++){

            modelItems.add( new ScheduleModel(allScheduleList.get(i).getDay()+" "+allScheduleList.get(i).getFromTimeStamp()+" : "+allScheduleList.get(i).getToTimeStamp(),0));
        }
//        modelItems[0] = new ScheduleModel("pizza", 0);
//        modelItems[1] = new ScheduleModel("burger", 1);
//        modelItems[2] = new ScheduleModel("olives", 1);
//        modelItems[3] = new ScheduleModel("orange", 0);
//        modelItems[4] = new ScheduleModel("tomato", 1);
        adapter = new CustomAdapter(this, modelItems);
        lv.setAdapter(adapter);

        /**
         * TO-DO : Read schedule from database and display in the list
         */
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {


        if(v.getId() == R.id.deleteButton)
        {
            ArrayList<UserInfo> listOfScheduleCopy = new ArrayList<UserInfo>(allScheduleList);

            ArrayList<Integer> posToBeDeleted = new ArrayList<Integer>();



            int totalItems = lv.getCount();
            for(int i=0;i<totalItems;i++)
            {

                UserInfo userInfoItem = listOfScheduleCopy.get(i);
                LinearLayout linearLayout = (LinearLayout)lv.getChildAt(i);
                if(linearLayout == null)
                {

                    Log.e("Removed","In break");
                    break;
                }

                CheckBox checkBox = (CheckBox) linearLayout.getChildAt(0);
                Log.e("Removed", "In the loop");
                if(checkBox.isChecked())
                {
                    posToBeDeleted.add(i);
                    Log.e("Removed","TO BE DELETED at pos "+Integer.toString(i));
                    //Delete record from database
                    db.deleteRecord(userInfoItem);

                    //Cancel alarm too
                    cancelCorrespondingAlarm(userInfoItem);



                }


            }



            Iterator<UserInfo> iter = allScheduleList.iterator();

            while (iter.hasNext()) {
                UserInfo u = iter.next();

                if (posToBeDeleted.contains(allScheduleList.indexOf(u)) )
                    iter.remove();
            }

            Iterator<ScheduleModel> iter1 = modelItems.iterator();
            while (iter1.hasNext()) {
                ScheduleModel sc = iter1.next();

                if (posToBeDeleted.contains(modelItems.indexOf(sc)) )
                    iter1.remove();
            }

            //remove item from modelList


            /*
                Update list and adapter when deleted
                 */
            CustomAdapter adapter = new CustomAdapter(this, modelItems);
            lv.setAdapter(adapter);


        }

    }

    public void cancelCorrespondingAlarm(UserInfo userInfo)
    {


        AlarmManager manager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(MainActivity.getAppContext(), StartAlarmReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(MainActivity.getAppContext(), userInfo.getRecordID(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Cancel alarms
        try {
            manager.cancel(alarmIntent);
            Log.e("Alarm Manager Cancellation", "AlarmManager update canceled. ");
        } catch (Exception e) {
            Log.e("Alarm Manager Cancellation", "AlarmManager update was not canceled. " + e.toString());
        }


        intent = new Intent(MainActivity.getAppContext(), EndAlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(MainActivity.getAppContext(), userInfo.getRecordID(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Cancel alarms
        try {
            manager.cancel(alarmIntent);
            Log.e("Alarm Manager Cancellation", "AlarmManager update canceled. ");
        } catch (Exception e) {
            Log.e("Alarm Manager Cancellation", "AlarmManager update was not canceled. " + e.toString());
        }


    }


}
