package com.mobilecomputing.mandar.contextawareapp;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONException;
import org.json.JSONObject;
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
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class WeatherDataFetcherService extends IntentService {


    LocationManager locationManager;


    Bitmap icon = null;
    String temperature, date, condition, humidity, wind, link;
    ArrayList<String> weather = new ArrayList<String>();
    double lat, lon;


    public WeatherDataFetcherService() {
        super("WeatherDataFetcherService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.e("Weather Fecther service","OnHandle in service Intent called");
        //Get lat and lon of your current location

        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Location current_loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);


        if(current_loc != null) {
            lat = current_loc.getLatitude();
            lon = current_loc.getLongitude();
        }

        getWeatherData();

        //Set data in intent
        Bundle msg = new Bundle();

//        tempText.setText("Temperature: "+temperature);
//        conditionText.setText("Condition: "+condition);
//        dateText.setText("Date: "+date);
//        humidityText.setText("Humidity: "+humidity);
//        windText.setText("Wind: "+wind);
//        image.setImageBitmap(icon);
//        day1.setText(weather.get(3));
//        day2.setText(weather.get(4));
//        day3.setText(weather.get(5));
//        day4.setText(weather.get(6));
//

        msg.putString("Temperature",temperature);
        msg.putString("Condition",condition);
        msg.putString("Humidity",humidity);
        msg.putString("Date",date);
        msg.putString("Wind",wind);
        msg.putParcelable("BitmapImage",icon);



        NotificationHandler.sendWeatherNotification(getApplicationContext(),"Weather Update","",msg);


        stopSelf();
    }



    public int getWoeIDFromLatLong(double lat, double lon){

        Location nearestResultLocation = new Location("");
        int woeid = 0;

        String URL1  = "https://query.yahooapis.com/v1/public/yql?q=SELECT%20*%20FROM%20geo.placefinder%20WHERE%20text=%27"+Double.toString(lat)+","+Double.toString(lon)+"%27%20and%20gflags=%27R%27&format=json";
        HttpPost httppost = new HttpPost(URL1);

        HttpClient client = new DefaultHttpClient();
        HttpResponse response;
        StringBuilder stringBuilder = new StringBuilder();

        try {
            response = client.execute(httppost);
            HttpEntity entity = response.getEntity();
            InputStream stream = entity.getContent();
            int b;
            while ((b = stream.read()) != -1) {
                stringBuilder.append((char) b);
            }

            Log.e("Weather TAG", stringBuilder.toString());


        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(stringBuilder.toString());
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            Integer count = (Integer) jsonObject.getJSONObject("query").getInt("count");
            if (count >= 1) {
                woeid = jsonObject.getJSONObject("query").getJSONObject("results").getJSONObject("Result").getInt("woeid");

                Log.d("Weather", "Count is 1 here");
//                    woeid = ((JSONArray) queryJsonObj.getJSONObject("results")).getJSONObject(0).getInt("woeid");


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("Weather", "WOEID is " + Integer.toString(woeid));
        return woeid;
    }

    protected String getWeatherData() {
// TODO Auto-generated method stub
        String qResult = "";
        HttpClient httpClient = new DefaultHttpClient();
        HttpContext localContext = new BasicHttpContext();

        int woeid = getWoeIDFromLatLong(lat,lon);

        HttpGet httpGet = new HttpGet("http://weather.yahooapis.com/forecastrss?w="+Integer.toString(woeid)+"&u=c&#8221");

        try {
            HttpResponse response = httpClient.execute(httpGet,
                    localContext);
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                InputStream inputStream = entity.getContent();
                Reader in = new InputStreamReader(inputStream);
                BufferedReader bufferedreader = new BufferedReader(in);
                StringBuilder stringBuilder = new StringBuilder();
                String stringReadLine = null;
                while ((stringReadLine = bufferedreader.readLine()) != null) {
                    stringBuilder.append(stringReadLine + "\n");
                }
                qResult = stringBuilder.toString();
            }

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Document dest = null;
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory
                .newInstance();
        DocumentBuilder parser;
        try {
            parser = dbFactory.newDocumentBuilder();
            dest = parser
                    .parse(new ByteArrayInputStream(qResult.getBytes()));
        } catch (ParserConfigurationException e1) {
            e1.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Node temperatureNode = dest.getElementsByTagName(
                "yweather:condition").item(0);
        temperature = temperatureNode.getAttributes()
                .getNamedItem("temp").getNodeValue().toString();
        Node tempUnitNode = dest.getElementsByTagName("yweather:units").item(0);
        temperature = temperature + "°" +tempUnitNode.getAttributes().getNamedItem("temperature").getNodeValue().toString();

        Node dateNode = dest.getElementsByTagName("yweather:forecast")
                .item(0);
        date = dateNode.getAttributes().getNamedItem("date")
                .getNodeValue().toString();

        Node conditionNode = dest
                .getElementsByTagName("yweather:condition").item(0);
        condition = conditionNode.getAttributes()
                .getNamedItem("text").getNodeValue().toString();

        Node humidityNode = dest
                .getElementsByTagName("yweather:atmosphere").item(0);
        humidity = humidityNode.getAttributes()
                .getNamedItem("humidity").getNodeValue().toString();
        humidity = humidity + "%";

        Node windNode = dest.getElementsByTagName("yweather:wind").item(0);
        wind = windNode.getAttributes().getNamedItem("speed").getNodeValue().toString();
        Node windUnitNode = dest.getElementsByTagName("yweather:units").item(0);
        wind = wind + " "+windUnitNode.getAttributes().getNamedItem("speed")
                .getNodeValue().toString();

        String desc = dest.getElementsByTagName("item").item(0)
                .getChildNodes().item(13).getTextContent().toString();
        StringTokenizer str = new StringTokenizer(desc, "<=>");
        System.out.println("Tokens: " + str.nextToken("=>"));
        String src = str.nextToken();
        System.out.println("src: "+ src);

        String url1 = src.substring(1, src.length() - 2);
        Pattern TAG_REGEX = Pattern.compile("(.+?)<br />");
        Matcher matcher = TAG_REGEX.matcher(desc);
        while (matcher.find()) {
            weather.add(matcher.group(1));
        }

        Pattern links = Pattern.compile("(.+?)<BR/>");
        matcher = links.matcher(desc);
        while(matcher.find()){
            System.out.println("Match Links: "+ (matcher.group(1)));
            link = matcher.group(1);
        }

/* String test = (Html.fromHtml(desc)).toString();
System.out.println("test: "+ test);
StringTokenizer tkn = new StringTokenizer(test);
for(int i=0; i < tkn.countTokens(); i++){
System.out.println("Remaining: "+tkn.nextToken());
}*/

        InputStream in = null;
        try {
// in = OpenHttpConnection(url1);
            int response = -1;
            URL url = new URL(url1);
            URLConnection conn = url.openConnection();

            if (!(conn instanceof HttpURLConnection))
                throw new IOException("Not an HTTP connection");
            HttpURLConnection httpConn = (HttpURLConnection) conn;
            httpConn.setAllowUserInteraction(false);
            httpConn.setInstanceFollowRedirects(true);
            httpConn.setRequestMethod("GET");
            httpConn.connect();

            response = httpConn.getResponseCode();
            if (response == HttpURLConnection.HTTP_OK) {
                System.out.println("*********************");
                in = httpConn.getInputStream();
            }
            icon = BitmapFactory.decodeStream(in);
            in.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return qResult;

    }
//
//    protected void onPostExecute(String result) {
//        System.out.println("POST EXECUTE");
//
//        tempText.setText("Temperature: "+temperature);
//        conditionText.setText("Condition: "+condition);
//        dateText.setText("Date: "+date);
//        humidityText.setText("Humidity: "+humidity);
//        windText.setText("Wind: "+wind);
//        image.setImageBitmap(icon);
//        day1.setText(weather.get(3));
//        day2.setText(weather.get(4));
//        day3.setText(weather.get(5));
//        day4.setText(weather.get(6));
////            weatherLink.setText(Html.fromHtml(link));
//
//    }

}
