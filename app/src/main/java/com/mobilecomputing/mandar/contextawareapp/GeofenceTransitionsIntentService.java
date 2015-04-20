package com.mobilecomputing.mandar.contextawareapp;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
import java.util.List;

public class GeofenceTransitionsIntentService extends IntentService {

    protected static final String TAG = "geofence-transitions-service";
    AudioManager audioManager;

    /**
     * This constructor is required, and calls the super IntentService(String)
     * constructor with the name for a worker thread.
     */
    public GeofenceTransitionsIntentService() {
        // Use the TAG to name the worker thread.
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    /**
     * Handles incoming intents.
     * @param intent sent by Location Services. This Intent is provided to Location
     *               Services (inside a PendingIntent) when addGeofences() is called.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        Log.i(TAG, "here");

        String geoFenceType = intent.getStringExtra(Constants.GEOFENCE_TYPE);
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        if (geofencingEvent.hasError()) {
            String errorMessage = GeofenceErrorMessages.getErrorString(this,
                    geofencingEvent.getErrorCode());
            Log.e(TAG, errorMessage);
            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL)
           {

               //Check which location
               if(geoFenceType.equalsIgnoreCase(Constants.WORK_GEOFENCE))
               {
                   Log.i(TAG, "Work Location Enter of Dwell event");
                   processGeoFenceEvent(geofencingEvent,Constants.RINGER_MODE_SILENT,"");

               }
               else if(geoFenceType.equalsIgnoreCase(Constants.HOME_GEOFENCE))
               {
                   Log.i(TAG, "Home Location Enter of Dwell event");
                   //While entering home location, put phone on normal and show appliances turning on message
                   processGeoFenceEvent(geofencingEvent,Constants.RINGER_MODE_NORMAL,Constants.HOME_APPLIANCES_TURN_ON_MSG);


               }



        }
        else if(geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT)
        {
            //Check which location
            if(geoFenceType.equalsIgnoreCase(Constants.WORK_GEOFENCE))
            {
                Log.i(TAG, "Work Location Exit event");


                processGeoFenceEvent(geofencingEvent,Constants.RINGER_MODE_NORMAL,"");


            }
            else if(geoFenceType.equalsIgnoreCase(Constants.HOME_GEOFENCE))
            {
                Log.i(TAG, "Home Location Exit event");
                processGeoFenceEvent(geofencingEvent,Constants.RINGER_MODE_NORMAL,Constants.HOME_APPLIANCES_TURN_OFF_MSG);


            }
        }

        else {
            // Log the error.
            Log.e(TAG, getString(R.string.geofence_transition_invalid_type, geofenceTransition));
        }
    }

    public void processGeoFenceEvent(GeofencingEvent geofencingEvent,String ringerMode, String msg)
    {

        int geofenceTransition = geofencingEvent.getGeofenceTransition();
        //Put phone on silent
        changeRinger(ringerMode);
        // Get the geofences that were triggered. A single event can trigger multiple geofences.
        List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();


        // Get the transition details as a String.
        String geofenceTransitionDetails = getGeofenceTransitionDetails(
                this,
                geofenceTransition,
                triggeringGeofences
        );

        // Send notification and log the transition details.
        int notification_ID= 0;
        if(msg.equalsIgnoreCase(""))
            sendNotification(geofenceTransitionDetails,msg,Constants.NOTIFICATION_ID_OF_RINGER);
        else
            sendNotification(geofenceTransitionDetails,msg,Constants.NOTIFICATION_ID_OF_HOME_APPLIANCES);

        //Put phone on silent here


        Log.i(TAG, geofenceTransitionDetails);



    }

    /**
     * Gets transition details and returns them as a formatted string.
     *
     * @param context               The app context.
     * @param geofenceTransition    The ID of the geofence transition.
     * @param triggeringGeofences   The geofence(s) triggered.
     * @return                      The transition details formatted as String.
     */
    private String getGeofenceTransitionDetails(
            Context context,
            int geofenceTransition,
            List<Geofence> triggeringGeofences) {

        String geofenceTransitionString = getTransitionString(geofenceTransition);

        // Get the Ids of each geofence that was triggered.
        ArrayList triggeringGeofencesIdsList = new ArrayList();
        for (Geofence geofence : triggeringGeofences) {
            triggeringGeofencesIdsList.add(geofence.getRequestId());
        }
        String triggeringGeofencesIdsString = TextUtils.join(", ", triggeringGeofencesIdsList);

        return geofenceTransitionString + ": " + triggeringGeofencesIdsString;
    }

    /**
     * Posts a notification in the notification bar when a transition is detected.
     * If the user clicks the notification, control goes to the MainActivity.
     */
    private void sendNotification(String notificationDetails, String msg,int notification_ID) {
        // Create an explicit content Intent that starts the main Activity.
        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);

        // Construct a task stack.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        // Add the main Activity to the task stack as the parent.
        stackBuilder.addParentStack(MainActivity.class);

        // Push the content Intent onto the stack.
        stackBuilder.addNextIntent(notificationIntent);

        // Get a PendingIntent containing the entire back stack.
        PendingIntent notificationPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // Get a notification builder that's compatible with platform versions >= 4
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        // Define the notification settings.
        builder.setSmallIcon(R.drawable.ic_launcher)
                // In a real app, you may want to use a library like Volley
                // to decode the Bitmap.
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                        R.drawable.ic_launcher))
                .setColor(Color.RED)
                .setContentTitle(notificationDetails)
                .setContentText(msg)
                .setContentIntent(notificationPendingIntent);

        // Dismiss notification once the user touches it.
        builder.setAutoCancel(true);

        // Get an instance of the Notification manager
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Issue the notification
        mNotificationManager.notify(notification_ID, builder.build());
    }

    /**
     * Maps geofence transition types to their human-readable equivalents.
     *
     * @param transitionType    A transition type constant defined in Geofence
     * @return                  A String indicating the type of transition
     */
    private String getTransitionString(int transitionType) {
        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return getString(R.string.geofence_transition_entered);
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return getString(R.string.geofence_transition_exited);
            case Geofence.GEOFENCE_TRANSITION_DWELL:
                return getString(R.string.geofence_transition_entered);
            default:
                return getString(R.string.unknown_geofence_transition);
        }
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