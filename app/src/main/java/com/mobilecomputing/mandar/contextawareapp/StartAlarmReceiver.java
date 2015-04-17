package com.mobilecomputing.mandar.contextawareapp;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.util.Log;

public class StartAlarmReceiver extends BroadcastReceiver {
    AudioManager audioManager;
    public StartAlarmReceiver() {


    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.

        audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        changeRinger(Constants.RINGER_MODE_SILENT);
        Log.d("On star Alarm receiver ", "phone put on silent");
        //Put phone on silent


    }

    public void changeRinger(String ringerMode){

        if(ringerMode.equalsIgnoreCase(Constants.RINGER_MODE_SILENT)){
            //Put on silent
            audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            audioManager.setStreamVolume(AudioManager.STREAM_RING,0,0);
            audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION,0,0);
            audioManager.setStreamMute(AudioManager.STREAM_RING,false);
            audioManager.setStreamMute(AudioManager.STREAM_NOTIFICATION,false);
        }
        else if(ringerMode.equalsIgnoreCase(Constants.RINGER_MODE_NORMAL)){
            /* TO-DO Add code to increase volume*/
            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);


        }


    }
}
