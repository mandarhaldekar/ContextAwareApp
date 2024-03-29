package com.mobilecomputing.mandar.contextawareapp;


import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * It receives weather data from push notification and shows it on UI
 */
public class WeatherActivity extends Activity {



    TextView title, tempText, dateText, conditionText, windText, humidityText,day1,day2, day3, day4;
    ImageView image;
    ArrayList<String> weather = new ArrayList<String>();
    ProgressDialog dialog;

    public static Context context;

    public static Context getAppContext(){
        return WeatherActivity.context;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        image = (ImageView)findViewById(R.id.icon);
        title = (TextView) findViewById(R.id.weather_title);
        dateText = (TextView) findViewById(R.id.dateText);
        tempText = (TextView) findViewById(R.id.tempText);
        conditionText = (TextView) findViewById(R.id.conditionText);
        humidityText = (TextView) findViewById(R.id.humidityText);
        windText = (TextView) findViewById(R.id.windText);

        getDataFromIntentAndSetIntoUI(getIntent());
        Log.d("Weather tag","In on create");




    }

    public void getDataFromIntentAndSetIntoUI(Intent intent){


        Bundle extras = intent.getExtras();
        if(intent == null){
            Log.e("Weather Activity","Null intent received");
            return;
        }
        if(extras != null)
        {
            if(extras.containsKey("Temperature"))
            {

                String temperature = extras.getString("Temperature");
                tempText.setText("Temperature: " + temperature);
            }
            if(extras.containsKey("Humidity"))
            {

                String humidity = extras.getString("Humidity");
                humidityText.setText("Humidity: " + humidity);
            }
            if(extras.containsKey("Condition"))
            {

                String condition = extras.getString("Condition");
                conditionText.setText("Condition: " + condition);
            }
            if(extras.containsKey("Wind"))
            {

                String wind = extras.getString("Wind");
                windText.setText("Wind: " + wind);
            }
            if(extras.containsKey("Date"))
            {

                String date = extras.getString("Date");
                dateText.setText("Date: " + date);
            }
            if(extras.containsKey("BitmapImage"))
            {
                Log.d("BitmapImage","Image received..setting image in image view");
                Bitmap bitMapImage = (Bitmap) extras.getParcelable("BitmapImage");
                if(bitMapImage == null){
                    Log.d("BitmapImage","Null Image !!");
                }
                image.setImageBitmap(bitMapImage);
            }

        }
    }
    @Override
    public void onNewIntent(Intent intent)
    {
        Log.d("Weather tag","In on new Intent");

        Bundle extras = intent.getExtras();
        if(extras != null)
        {
            if(extras.containsKey("Temperature"))
            {

                String temperature = extras.getString("Temperature");
                tempText.setText("Temperature: " + temperature);
            }
            if(extras.containsKey("Humidity"))
            {

                String humidity = extras.getString("Humidity");
                humidityText.setText("Humidity: " + humidity);
            }
            if(extras.containsKey("Condition"))
            {

                String condition = extras.getString("Condition");
                conditionText.setText("Condition: " + condition);
            }
            if(extras.containsKey("Wind"))
            {

                String wind = extras.getString("Wind");
                windText.setText("Wind: " + wind);
            }
            if(extras.containsKey("Date"))
            {

                String date = extras.getString("Date");
                dateText.setText("Wind: " + date);
            }

        }
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
// Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.weather, menu);
        return true;
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        getDataFromIntentAndSetIntoUI(getIntent());

    }
}

