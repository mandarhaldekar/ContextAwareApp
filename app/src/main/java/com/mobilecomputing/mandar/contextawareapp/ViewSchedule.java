package com.mobilecomputing.mandar.contextawareapp;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.List;


public class ViewSchedule extends ActionBarActivity {

    ListView lv;
    ScheduleModel[] modelItems;
    DBManager db;
    private List<UserInfo> allScheduleList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_schedule);
        lv = (ListView) findViewById(R.id.listOfSchedule);

        db = new DBManager(this);
        allScheduleList = db.getAllUserInfo();



        modelItems = new ScheduleModel[allScheduleList.size()];
        for(int i=0;i<allScheduleList.size();i++){
            modelItems[i] = new ScheduleModel(allScheduleList.get(i).getDay()+" "+allScheduleList.get(i).getFromTimeStamp()+" : "+allScheduleList.get(i).getToTimeStamp(),0);
        }
//        modelItems[0] = new ScheduleModel("pizza", 0);
//        modelItems[1] = new ScheduleModel("burger", 1);
//        modelItems[2] = new ScheduleModel("olives", 1);
//        modelItems[3] = new ScheduleModel("orange", 0);
//        modelItems[4] = new ScheduleModel("tomato", 1);
        CustomAdapter adapter = new CustomAdapter(this, modelItems);
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


}
