package domain.com.projekti;

/**
 * Created by MrKohvi on 10.5.2017.
 */

//tags used almost in every activity

public interface TAGS
{
    String  TAG_LOGGED = "LogStatus",
            TAG_USER_ID_TASK = "UserID",
            TAG_ID="ID",
            TAG_LON="Lon",
            TAG_LAT="Lat",
            TAG_NAME = "Name",
            TAG_PWORD = "Password",
            TAG_START = "Start",
            TAG_STOP = "Stop",
            TAG_EXPLANATION = "Explanation",
            TAG_DESCRIPTION = "Description",
            TAG_PLACE = "Place",
            REQUESTING_LOCATION_UPDATES_KEY = "requesting-location-updates",
            LOCATION_KEY ="location-key",
            LAST_UPDATED_TIME_STRING_KEY = "last-updated-time-string-key";
    int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    int MY_PERMISSIONS_REQUEST_VIBRATION = 100;

}
