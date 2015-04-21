package com.mobilecomputing.mandar.contextawareapp;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Mandar on 4/20/2015.
 */
public class CommonUtil {

    public static boolean isWithin(String from, String to)
    {


        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm"); //like "HH:mm" or just "mm", whatever you want
        String cur = sdf.format(date);
        Log.e("Schedule","Current time is :"+cur);
        if(from != null && to!=null && cur != null) {


            if(cur.compareTo(from) >=0 && cur.compareTo(to) <=0){
                Log.e("Schedule","Current time is in between ");
                return true;
            }

        }
        return false;


    }



}
