package com.mobilecomputing.mandar.contextawareapp;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;

/**
 * Created by Mandar on 4/16/2015.
 */
public class Constants {

    private Constants() {
    }

    public static final String PACKAGE_NAME = "com.google.android.gms.location.Geofence";

    public static final String SHARED_PREFERENCES_NAME = PACKAGE_NAME + ".SHARED_PREFERENCES_NAME";

    public static final String GEOFENCES_ADDED_KEY = PACKAGE_NAME + ".GEOFENCES_ADDED_KEY";

    /**
     * Used to set an expiration time for a geofence. After this amount of time Location Services
     * stops tracking the geofence.
     */
    public static final long GEOFENCE_EXPIRATION_IN_HOURS = 12;

    /**
     * For this sample, geofences expire after twelve hours.
     */
    public static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS =
            GEOFENCE_EXPIRATION_IN_HOURS * 60 * 60 * 1000;
    public static final float GEOFENCE_RADIUS_IN_METERS = 600; // in meters
    public static final String HOME_GEOFENCE = "HOME";
    public static final String WORK_GEOFENCE = "WORK";
    public static final String GEOFENCE_TYPE = "TYPE";
    public static final String RINGER_MODE_SILENT = "silent";
    public static final String RINGER_MODE_NORMAL = "normal";
    public static final String RINGER_MODE_NONE = "none";

    //Notification title
    public static final String NOTIFICATION_TITLE = "Context Aware App";
    public static final String NOTIFICATION_SILENT_MESSAGE = "Your phone is put on silent mode";
    public static final String NOTIFICATION_NORMAL_MESSAGE = "Your phone is put on normal mode";

    //NotificationID
    public static final int NOTIFICATION_ID_OF_RINGER = 910;
    public static final int NOTIFICATION_ID_OF_HOME_APPLIANCES = 911;
    public static final int NOTIFICATION_ID_OF_WEATHER = 2;
    public static final int NOTIFICATION_ID_OF_TRAFFIC = 3;





    //Messages
    public static final String HOME_APPLIANCES_TURN_ON_MSG = "Please turn on the home appliances if needed";
    public static final String HOME_APPLIANCES_TURN_OFF_MSG = "Please turn off the home appliances if needed";





}
