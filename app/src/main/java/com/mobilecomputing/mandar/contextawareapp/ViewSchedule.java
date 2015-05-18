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
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This activity shows list of schedule to the user and also allows him/her to delete them
 */
public class ViewSchedule extends ActionBarActivity implements View.OnClickListener,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<Status> {
    private static final String TAG = "Context Aware App" ;
    Button deleteButton;
    ListView lv;
    ArrayList<ScheduleModel> modelItems;
    DBManager db;
    private List<UserInfo> allScheduleList;
    CustomAdapter adapter;

    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;

    /**
     * The list of geofences used in this sample.
     */
    protected ArrayList<Geofence> mGeofenceList;
    ArrayList<String> workLocationList = new ArrayList<String>();
    ArrayList<LatLng> workLocationLatLngList = new ArrayList<LatLng>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_schedule);
        lv = (ListView) findViewById(R.id.listOfSchedule);

        deleteButton = (Button)findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(this);


        db = new DBManager(this);
        allScheduleList = db.getAllUserInfo();
        mGeofenceList = new ArrayList<Geofence>();

        //Add distinct locations to GeoFence list
        buildDistinctGeoFenceList(allScheduleList);




        modelItems = new ArrayList<ScheduleModel>();
        for(int i=0;i<allScheduleList.size();i++){

            modelItems.add( new ScheduleModel("Location : "+allScheduleList.get(i).getWorkLocationAddr()+". Time: "+allScheduleList.get(i).getDay()+" "+allScheduleList.get(i).getFromTimeStamp()+" : "+allScheduleList.get(i).getToTimeStamp(),0));
        }

        adapter = new CustomAdapter(this, modelItems);
        lv.setAdapter(adapter);

        /**
         * TO-DO : Read schedule from database and display in the list
         */

        buildGoogleApiClient();
    }

    private void buildDistinctGeoFenceList(List<UserInfo> allScheduleList) {


        for(int i=0;i<allScheduleList.size();i++)
        {
            String workLocation = allScheduleList.get(i).getWorkLocationAddr();
            if(!workLocationList.contains(workLocation)){
                workLocationList.add(workLocation);

                workLocationLatLngList.add(new LatLng(allScheduleList.get(i).getWorkLocationLat(),allScheduleList.get(i).getWorkLocationLon()));
            }

        }
    }


    /**
     * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }


    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
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
            mGeofenceList = new ArrayList<Geofence>();
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

                    //Is this last record with this location
                    if(db.isZeroRecordWithThisLocation(userInfoItem.getWorkLocationAddr())){
                        //Remove GEOFENCE
                        //Find index in workLocationList
                        LatLng latLng = new LatLng(userInfoItem.getWorkLocationLat(),userInfoItem.getWorkLocationLon());

                        addToGeoFenceList(userInfoItem.getWorkLocationAddr(),latLng);

                        if (!mGoogleApiClient.isConnected()) {
                            Toast.makeText(this, getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        try {
                            // Remove geofences.
                            LocationServices.GeofencingApi.removeGeofences(
                                    mGoogleApiClient,
                                    // This is the same pending intent that was used in addGeofences().
                                    getGeofencePendingIntent(Constants.WORK_GEOFENCE)
                            ).setResultCallback(this); // Result processed in onResult().
                        } catch (SecurityException securityException) {
                            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
                            logSecurityException(securityException);
                        }

                        Log.e(TAG,"GEOFENCE REMOVED");
                    }

                    //Cancel alarm too
                    cancelCorrespondingAlarm(userInfoItem);
                    checkBox.setChecked(false);


                }


            }



            Iterator<UserInfo> iter = allScheduleList.iterator();
            ArrayList<UserInfo> itemstobedeleted=new ArrayList<UserInfo>();
            for(int j=0;j<posToBeDeleted.size();j++)
            {
                itemstobedeleted.add(allScheduleList.get(posToBeDeleted.get(j)));
            }
            while (iter.hasNext()) {
                UserInfo u = iter.next();

                if (itemstobedeleted.contains(u))  {
                    iter.remove();


                }
            }

            Iterator<ScheduleModel> iter1 = modelItems.iterator();
            ArrayList<ScheduleModel> itemstobedeleted1=new ArrayList<ScheduleModel>();
            for(int j=0;j<posToBeDeleted.size();j++)
            {
                itemstobedeleted1.add(modelItems.get(posToBeDeleted.get(j)));
            }
            while (iter1.hasNext()) {
                ScheduleModel sc = iter1.next();

                if (itemstobedeleted1.contains(sc)) {
                    iter1.remove();
                }
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

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "Connected to GoogleApiClient");
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason.
        Log.i(TAG, "Connection suspended");

        // onConnected() will be called again automatically when the service reconnects
    }

    @Override
    public void onResult(Status status) {

    }


    private void logSecurityException(SecurityException securityException) {
        Log.e(TAG, "Invalid location permission. " +
                "You need to use ACCESS_FINE_LOCATION with geofences", securityException);
    }

    /**
     * Gets a PendingIntent to send with the request to add or remove Geofences. Location Services
     * issues the Intent inside this PendingIntent whenever a geofence transition occurs for the
     * current list of geofences.
     *
     * @return A PendingIntent for the IntentService that handles geofence transitions.
     */
    private PendingIntent getGeofencePendingIntent(String geoFencetype) {
        // Reuse the PendingIntent if we already have it.
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);

        intent.putExtra(Constants.GEOFENCE_TYPE,geoFencetype);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }


    public void addToGeoFenceList(String address,LatLng latlng){
        mGeofenceList.add(new Geofence.Builder()
                // Set the request ID of the geofence. This is a string to identify this
                // geofence.
                .setRequestId(address)

                        // Set the circular region of this geofence.
                .setCircularRegion(
                        latlng.latitude,
                        latlng.longitude,
                        Constants.GEOFENCE_RADIUS_IN_METERS
                )

                        // Set the expiration duration of the geofence. This geofence gets automatically
                        // removed after this period of time.
                .setExpirationDuration(Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)
                .setLoiteringDelay(10000)

                        // Set the transition types of interest. Alerts are only generated for these
                        // transition. We track entry and exit transitions in this sample.
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT|Geofence.GEOFENCE_TRANSITION_DWELL)

                        // Create the geofence.
                .build());
    }

}
