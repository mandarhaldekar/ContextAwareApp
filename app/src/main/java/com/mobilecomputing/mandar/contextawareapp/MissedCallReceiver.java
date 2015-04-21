package com.mobilecomputing.mandar.contextawareapp;

/**
 * Created by Mandar on 4/17/2015.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MissedCallReceiver extends BroadcastReceiver {

    static boolean isRinging=false;
    static boolean isReceived=false;
    static boolean isMessageSent = false;
    static String callerPhoneNumber;
    static final String message = "I am at work location. I am busy right now. I will get back to you as soon as I can";

    @Override
    public void onReceive(Context mContext, Intent intent){

        Log.e("Call detection","In Receive");
        // Get current phone state
        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);



        if(state==null)
            return;

        //phone is ringing
        if(state.equals(TelephonyManager.EXTRA_STATE_RINGING)){
            isRinging =true;
            //get caller's number
            Bundle bundle = intent.getExtras();
            callerPhoneNumber= bundle.getString("incoming_number");
            return;
        }

        //phone is received
        if(state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)){
            isReceived=true;
        }

        // phone is idle
        if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)){
            // detect missed call
            if(isRinging==true && isReceived==false && isMessageSent == false)
            {

                int flag = isWithinScheduleOrWorkLocation(mContext);
                if(flag == 1)
                {
                    Toast.makeText(mContext, "Got a missed call from : " + callerPhoneNumber, Toast.LENGTH_LONG).show();
                    Log.e("Call detection", "Got a missed call from : " + callerPhoneNumber);

                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(callerPhoneNumber, null, message, null, null);
                    Toast.makeText(mContext, "SMS sent.",
                            Toast.LENGTH_LONG).show();
                    isMessageSent = true;
                    Log.e("Call detection", "SMS Sent");
                }

            }
        }
    }

    private int isWithinScheduleOrWorkLocation(Context context) {

        //Get all records and check timings with current time
        DBManager db = new DBManager(context);
        List<UserInfo> allScheduleList = db.getAllUserInfo();


        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE");
        String dayOfWeek = dateFormat.format(date);


        for(int i=0;i<allScheduleList.size();i++)
        {
            UserInfo userInfo = allScheduleList.get(i);

            Log.e("Schedule check","day of the week is :"+dayOfWeek+" day in DB is "+userInfo.getDay());
            if(userInfo.getDay()!= null && userInfo.getDay().equalsIgnoreCase(dayOfWeek) )
            {
//                Log.e("Schedule Check","Day match found");
                //Compare time
                if(CommonUtil.isWithin(userInfo.getFromTimeStamp(),userInfo.getToTimeStamp()))
                {
                    Log.e("Schedule Check","Within schedule found");

                    return 1;
                }

            }
        }

        return 0;
    }
}
