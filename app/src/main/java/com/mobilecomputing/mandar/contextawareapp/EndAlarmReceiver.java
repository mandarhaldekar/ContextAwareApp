package com.mobilecomputing.mandar.contextawareapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class EndAlarmReceiver extends BroadcastReceiver {
    AudioManager audioManager;
    public EndAlarmReceiver() {
    }

    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.

        audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        changeRinger(Constants.RINGER_MODE_NORMAL);
        NotificationHandler.sendNotification(context,Constants.NOTIFICATION_TITLE,Constants.NOTIFICATION_NORMAL_MESSAGE,Constants.NOTIFICATION_ID_OF_RINGER);
        Log.d("On end Alarm receiver ", "phone put on normal");
        //Put phone on silent



        Intent weatherIntent = new Intent(context, WeatherDataFetcherService.class);

        context.startService(weatherIntent);

        NotificationHandler.sendMapNotification(context,"Traffic update","");




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
