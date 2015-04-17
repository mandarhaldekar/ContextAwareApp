package com.mobilecomputing.mandar.contextawareapp;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Geocoder;
import android.media.AudioManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import android.location.Address;


public class MainActivity extends ActionBarActivity implements View.OnClickListener,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<Status> {
    private static final String TAG = "Context Aware App" ;
    TimePicker tp;
    DBManager db;
    TimePicker tp1;
    Button addrecord;
    EditText worklocation;
    EditText homelocation;
    CheckBox Sun;
    CheckBox Mon;
    CheckBox Tues;
    CheckBox Wednes;
    CheckBox Thurs;
    CheckBox Fri;
    CheckBox Sat;
    ArrayList<String> days;
    String fromstrDateTime;
    String tostrDateTime;
    String home;
    String work;
    AudioManager audioManager;

    /////

    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;

    /**
     * The list of geofences used in this sample.
     */
    protected ArrayList<Geofence> mGeofenceList;

    /**
     * Used to keep track of whether geofences were added.
     */
    private boolean mGeofencesAdded;

    /**
     * Used when requesting to add or remove geofences.
     */
    private PendingIntent mGeofencePendingIntent;

    /**
     * Used to persist application state about whether geofences were added.
     */
    private SharedPreferences mSharedPreferences;


    /////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        homelocation=(EditText)findViewById(R.id.editText);
        worklocation=(EditText)findViewById(R.id.editText2);
        addrecord=(Button)findViewById(R.id.button);
        addrecord.setOnClickListener(this);
        tp=(TimePicker)findViewById(R.id.timePicker);
        tp1=(TimePicker)findViewById(R.id.timePicker1);
        Sun=(CheckBox)findViewById(R.id.sun);
        Mon=(CheckBox)findViewById(R.id.mon);
        Tues=(CheckBox)findViewById(R.id.tues);
        Wednes=(CheckBox)findViewById(R.id.wednes);
        Thurs=(CheckBox)findViewById(R.id.thurs);
        Fri=(CheckBox)findViewById(R.id.fri);
        Sat=(CheckBox)findViewById(R.id.sat);
        days=new ArrayList<String>();

        db = new DBManager(this);


        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);


        //////////////////////////

        // Empty list for storing geofences.
        mGeofenceList = new ArrayList<Geofence>();

        // Initially set the PendingIntent used in addGeofences() and removeGeofences() to null.
        mGeofencePendingIntent = null;

        // Retrieve an instance of the SharedPreferences object.
        mSharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES_NAME,
                MODE_PRIVATE);

        // Get the value of mGeofencesAdded from SharedPreferences. Set to false as a default.
        mGeofencesAdded = mSharedPreferences.getBoolean(Constants.GEOFENCES_ADDED_KEY, false);



        // Kick off the request to build GoogleApiClient.
        buildGoogleApiClient();


        ///

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

    public LatLng getLocationFromAddress(String strAddress) {


        Geocoder coder = new Geocoder(this);
        List<Address> address;
        LatLng p1 = null;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new LatLng(location.getLatitude(), location.getLongitude() );

        } catch (Exception ex) {

            ex.printStackTrace();
        }

        return p1;
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
    public void addWorkLocationButtonHandler(View v){
        //Get string

        String workAddress = worklocation.getText().toString();

        //Get lat and long
        LatLng workLocation = getLocationFromAddress(workAddress);

        if(workLocation == null){
            Toast.makeText(this, "Could not find this place, please enter proper address", Toast.LENGTH_LONG).show();
            return;
        }

        Log.d(TAG,"Lat Long found"+Double.toString(workLocation.latitude)+","+Double.toString(workLocation.longitude));
        //Else Add lat and long to geofencelist
        addToGeoFenceList(workAddress,workLocation);

        //Register for location
        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(this, getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
            return;
        }


        try {
            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient,
                    // The GeofenceRequest object.
                    getGeofencingRequest(),
                    // A pending intent that that is reused when calling removeGeofences(). This
                    // pending intent is used to generate an intent when a matched geofence
                    // transition is observed.
                    getGeofencePendingIntent(Constants.WORK_GEOFENCE)
            ).setResultCallback(this); // Result processed in onResult().
        } catch (SecurityException securityException) {
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
            logSecurityException(securityException);
        }

    }


    public void addHomeLocationButtonHandler(View v){
        //Get string

        String homeAddress = homelocation.getText().toString();

        //Get lat and long
        LatLng homeLocation = getLocationFromAddress(homeAddress);

        if(homeLocation == null){
            Toast.makeText(this, "Could not find this place, please enter proper address", Toast.LENGTH_LONG).show();
            return;
        }

        Log.d(TAG,"Lat Long found"+Double.toString(homeLocation.latitude)+","+Double.toString(homeLocation.longitude));
        //Else Add lat and long to geofencelist
        addToGeoFenceList(homeAddress,homeLocation);

        //Register for location
        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(this, getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
            return;
        }


        try {
            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient,
                    // The GeofenceRequest object.
                    getGeofencingRequest(),
                    // A pending intent that that is reused when calling removeGeofences(). This
                    // pending intent is used to generate an intent when a matched geofence
                    // transition is observed.
                    getGeofencePendingIntent(Constants.HOME_GEOFENCE)
            ).setResultCallback(this); // Result processed in onResult().
        } catch (SecurityException securityException) {
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
            logSecurityException(securityException);
        }

    }


    /**
     * Removes geofences, which stops further notifications when the device enters or exits
     * previously registered geofences.
     */
    public void removeGeofencesButtonHandler(View view) {
        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(this, getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            // Remove geofences.
            LocationServices.GeofencingApi.removeGeofences(
                    mGoogleApiClient,
                    // This is the same pending intent that was used in addGeofences().
                    getGeofencePendingIntent("")
            ).setResultCallback(this); // Result processed in onResult().
        } catch (SecurityException securityException) {
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
            logSecurityException(securityException);
        }
    }

    private void logSecurityException(SecurityException securityException) {
        Log.e(TAG, "Invalid location permission. " +
                "You need to use ACCESS_FINE_LOCATION with geofences", securityException);
    }
    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.button) {
            days.clear();
            fromstrDateTime = tp.getCurrentHour() + ":" + tp.getCurrentMinute();
            tostrDateTime = tp1.getCurrentHour() + ":" + tp1.getCurrentMinute();
            if (Sun.isChecked() == true) {
                days.add("Sunday");
            }
            if (Mon.isChecked() == true) {
                days.add("Monday");
            }
            if (Tues.isChecked() == true) {
                days.add("Tuesday");
            }
            if (Wednes.isChecked() == true) {
                days.add("Wednesday");
            }
            if (Thurs.isChecked() == true) {
                days.add("Thursday");
            }
            if (Fri.isChecked() == true) {
                days.add("Friday");
            }
            if (Sat.isChecked() == true) {
                days.add("Saturday");
            }
            home=homelocation.getText().toString();
            work=worklocation.getText().toString();
            //get Lan and Long from this address and put into database
            Double homeLocationLat = 0.0;
            Double homeLocationLong = 0.0;
            Double workLocationLat = 0.0;
            Double workLocationLong = 0.0;

            //Inserting into database
            for(int i=0;i<days.size();i++){
                //Record Id is zero by default
                UserInfo userInfo = new UserInfo(0,fromstrDateTime,tostrDateTime,days.get(i),workLocationLat,workLocationLong,homeLocationLat,homeLocationLong);
                db.addUserInfo(userInfo);

            }






            //set phone to silent

            audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            audioManager.setStreamVolume(AudioManager.STREAM_RING,0,0);
            audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION,0,0);

            String mode="regular";
            if (audioManager.getRingerMode()==AudioManager.RINGER_MODE_SILENT)
            {
                mode="silent";
            }
            Toast.makeText(this, "Record added"+ days.toString()+" "+fromstrDateTime+"Ringer mode:"+mode,Toast.LENGTH_LONG).show();
            audioManager.setStreamMute(AudioManager.STREAM_RING,false);
            audioManager.setStreamMute(AudioManager.STREAM_NOTIFICATION,false);

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

    /**
     * Builds and returns a GeofencingRequest. Specifies the list of geofences to be monitored.
     * Also specifies how the geofence notifications are initially triggered.
     */
    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();

        // The INITIAL_TRIGGER_ENTER flag indicates that geofencing service should trigger a
        // GEOFENCE_TRANSITION_ENTER notification when the geofence is added and if the device
        // is already inside that geofence.
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_DWELL);

        // Add the geofences to be monitored by geofencing service.
        builder.addGeofences(mGeofenceList);

        // Return a GeofencingRequest.
        return builder.build();
    }

    @Override
    public void onResult(Status status) {

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
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);

        intent.putExtra(Constants.GEOFENCE_TYPE,geoFencetype);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }


}
