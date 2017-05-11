package domain.com.projekti;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;


public class MainActivity extends Base_Activity implements
        TAGS,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener
{
    private String m_urlAddress;
    private TextView m_tv_logged, m_tv_loggedAs;
    private ListView listView;
    private MyAdapter adapter;
    private SharedPreferences loginCredentials, settings;
    private int userId;
    private ArrayList<Task> tasks;
    private LinearLayout layoutLoggedOut, layoutLoggedIn, layoutTips;
    private GoogleApiClient m_GoogleApiClient;
    private Location m_LastLocation, m_CurrentLocation;
    private LocationRequest m_LocationRequest;
    private boolean m_RequestingLocationUpdates;
    private String m_LastUpdateTime;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Init();
        checkIfLoggedIn();
        updateValuesFromBundle(savedInstanceState);
        countDownTimer();
    }

    private void updateValuesFromBundle(Bundle savedInstanceState)
    {
        if (savedInstanceState != null) {
            // Update the value of mRequestingLocationUpdates from the Bundle, and
            // make sure that the Start Updates and Stop Updates buttons are
            // correctly enabled or disabled.
            if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
                m_RequestingLocationUpdates = savedInstanceState.getBoolean(
                        REQUESTING_LOCATION_UPDATES_KEY);
            }

            // Update the value of mCurrentLocation from the Bundle and update the
            // UI to show the correct latitude and longitude.
            if (savedInstanceState.keySet().contains(LOCATION_KEY)) {
                // Since LOCATION_KEY was found in the Bundle, we can be sure that
                // mCurrentLocationis not null.
                m_CurrentLocation = savedInstanceState.getParcelable(LOCATION_KEY);
            }

            // Update the value of mLastUpdateTime from the Bundle and update the UI.
            if (savedInstanceState.keySet().contains(LAST_UPDATED_TIME_STRING_KEY)) {
                m_LastUpdateTime = savedInstanceState.getString(
                        LAST_UPDATED_TIME_STRING_KEY);
            }
            getTasks();
        }
    }

    public void Init()
    {
        m_tv_logged = (TextView) findViewById(R.id.main_tv_no_tasks);
        m_tv_loggedAs = (TextView) findViewById(R.id.main_tv_login_info);
        listView = (ListView) findViewById(R.id.myListView);
        registerForContextMenu(listView);
        listView.setLongClickable(true);
        tasks = new <Task>ArrayList();
        layoutLoggedOut = (LinearLayout) findViewById(R.id.loggedOut);
        layoutLoggedIn = (LinearLayout) findViewById(R.id.loggedIn);
        layoutTips = (LinearLayout) findViewById(R.id.tipsLayout);
        adapter = null;
        m_urlAddress = "";
        m_GoogleApiClient = null;
        m_LastLocation = null;
        m_CurrentLocation = null;


        loginCredentials = getSharedPreferences("domain.com.projekti.PREFERENCE_FILE_KEY", Context.MODE_PRIVATE);
        settings = PreferenceManager.getDefaultSharedPreferences(this);
        //set location request
        m_LocationRequest = new LocationRequest();
        m_LocationRequest.setInterval(Long.parseLong(settings.getString("sync_frequency", "10000")));
        m_LocationRequest.setFastestInterval(10000);
        m_LocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        m_RequestingLocationUpdates=false;

    }

    public void checkIfLoggedIn()
    {
        if (loginCredentials.getBoolean(TAG_LOGGED, false))
        {
            if (m_GoogleApiClient == null)
            {
                m_GoogleApiClient = new GoogleApiClient.Builder(this)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(LocationServices.API)
                        .build();
            }
            //if logged in
            m_tv_loggedAs.setText("Logged as: " + loginCredentials.getString(TAG_NAME, ""));

            //get user id
            userId = loginCredentials.getInt(TAG_ID, -1);

            //set listview before setting it visible
            getTasks();

            //get user tasks and set layout components
            layoutLoggedIn.setVisibility(View.VISIBLE);
            layoutLoggedOut.setVisibility(View.GONE);
            layoutTips.setVisibility(View.VISIBLE);
        }
        else
        {//if not logged in

            //set layout components
            layoutLoggedOut.setVisibility(View.VISIBLE);
            layoutLoggedIn.setVisibility(View.GONE);
            layoutTips.setVisibility(View.GONE);
        }
    }

    private ArrayList<Task> ParseJSON(String json)
    {
        ArrayList<Task> tasks = new <Task>ArrayList();

        if (json != null)
        {
            try
            {
                JSONArray jsonArray = new JSONArray(json);
                Log.d("JSON to list: ", "> " + json);
                for (int i = 0; i < jsonArray.length(); i++)
                {

                    JSONObject jsonObj = jsonArray.getJSONObject(i);

                    String id = jsonObj.getString(TAGS.TAG_ID);
                    String userid_task = jsonObj.getString(TAG_USER_ID_TASK);
                    String start = jsonObj.getString(TAG_START);
                    String stop = jsonObj.getString(TAG_STOP);
                    String explanation = jsonObj.getString(TAG_EXPLANATION);
                    String description = jsonObj.getString(TAG_DESCRIPTION);
                    String place = jsonObj.getString(TAG_PLACE);
                    String lat = jsonObj.getString(TAG_LAT);
                    String lon = jsonObj.getString(TAG_LON);

                    //place Lon Lat Description Id
                    Task task = new Task();

                    //check for numerical values
                    if (lat.equals("null"))
                    {
                        task.Lat = 0.0;
                    }
                    else
                    {
                        task.Lat = Double.parseDouble(lat);
                    }
                    if (lon.equals("null"))
                    {
                        task.Lon = 0.0;
                    }
                    else
                    {
                        task.Lon = Double.parseDouble(lon);
                    }

                    task.ID = Integer.parseInt(id);
                    task.Start = start;
                    task.Stop = stop;
                    task.Explanation = explanation;
                    task.Description = description;
                    task.Place = place;

                    if (!(userid_task.equals("null")))
                    {
                        task.UserID = Integer.parseInt(userid_task);
                    }

                    tasks.add(task);
                }
                return tasks;
            }
            catch (JSONException e)
            {
                e.printStackTrace();
                return null;
            }
        }
        else
        {
            Log.e("ServiceHandler", "No data received from HTTP Request");
            return null;
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.popup_menu_task, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        int index = info.position;
        m_urlAddress = "";
        String desc = tasks.get(index).Description;
        int id = tasks.get(index).ID;

        switch (item.getItemId())
        {
            case R.id.task_delete:
                Toast.makeText(getApplicationContext(), "Delete " + desc, Toast.LENGTH_SHORT).show();
                m_urlAddress = "https://codez.savonia.fi/jukka/project/deletetask.php?Id=" + id;

                try
                {
                    new MyASyncHandler(true, this).execute("1", m_urlAddress).get();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                break;
            case R.id.task_reserve:
                if (tasks.get(index).UserID == 0)
                {
                    Toast.makeText(getApplicationContext(), "Reserve " + desc, Toast.LENGTH_SHORT).show();
                    m_urlAddress = "https://codez.savonia.fi/jukka/project/reservetask.php?Id=" + id + "&UserId=" + userId;
                    try
                    {
                        new MyASyncHandler(true, this).execute("1", m_urlAddress).get();
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
                else
                {
                    Toast.makeText(this, "Someone has already reserved this task!", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.task_start:
                if (tasks.get(index).Start.equals("null") && tasks.get(index).Stop.equals("null") && tasks.get(index).UserID > 0)
                {
                    Toast.makeText(getApplicationContext(), "Start " + desc, Toast.LENGTH_SHORT).show();
                    m_urlAddress = "https://codez.savonia.fi/jukka/project/starttask.php?Id=" + id;
                    try
                    {
                        new MyASyncHandler(true, this).execute("1", m_urlAddress).get();
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
                else if (!(tasks.get(index).Stop.equals("null")))
                {
                    Toast.makeText(this, "Task has already been stopped!", Toast.LENGTH_SHORT).show();
                }
                else if (!(tasks.get(index).Start.equals("null")))
                {
                    Toast.makeText(this, "Task has already been started!", Toast.LENGTH_SHORT).show();
                }
                else if(tasks.get(index).UserID <= 0 )
                {
                    Toast.makeText(this, "You need to reserve task first", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.task_stop:
                if (tasks.get(index).Start.equals("null") && tasks.get(index).Stop.equals("null"))
                {
                    Toast.makeText(this, "Task is not even started yet", Toast.LENGTH_SHORT).show();
                } else if (!(tasks.get(index).Start.equals("null")) && !(tasks.get(index).Stop.equals("null")))
                {
                    Toast.makeText(this, "Task has already been stopped!", Toast.LENGTH_SHORT).show();
                } else if (!(tasks.get(index).Start.equals("null")) && (tasks.get(index).Stop.equals("null")))
                {
                    Toast.makeText(getApplicationContext(), "Stop " + desc, Toast.LENGTH_SHORT).show();
                    m_urlAddress = "https://codez.savonia.fi/jukka/project/stoptask.php?Id=" + id + "&Explanation=Stopped";
                    try
                    {
                        new MyASyncHandler(true, this).execute("1", m_urlAddress).get();
                    } catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    } catch (ExecutionException e)
                    {
                        e.printStackTrace();
                    }
                } else
                {
                    Toast.makeText(this, "Task has already been stopped!", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
        getTasks();
        return true;
    }

    private void getTasks()
    {
        try
        {
            m_urlAddress = "https://codez.savonia.fi/jukka/project/UserAndFreeTasks.php?UserID=" + userId;
            String json_tasks = new MyASyncHandler(true, this).execute("1", m_urlAddress).get();
            if (!(json_tasks.equals("[]")) || !(json_tasks.isEmpty()))
            {
                tasks = ParseJSON(json_tasks);
                adapter = new MyAdapter(MainActivity.this, tasks, userId);
                listView.setAdapter(adapter);
                listView.setVisibility(View.VISIBLE);
                m_tv_logged.setVisibility(View.GONE);
            }
            else
            {
                listView.setVisibility(View.GONE);
                m_tv_logged.setVisibility(View.VISIBLE);
                layoutTips.setVisibility(View.VISIBLE);
            }
        } catch (Exception e)
        {
            Log.d("Response: ", "> " + e.getMessage());
        }
    }

    public void countDownTimer()
    {
        new CountDownTimer(Long.parseLong(settings.getString("sync_frequency", "10000")), 1000)
        {
            @Override
            public void onTick(long millisUntilFinished)
            {

            }

            @Override
            public void onFinish()
            {
                getTasks();
                countDownTimer();
            }
        }.start();
    }

    public boolean checkAlertAboutTask(Location myLocation, Location taskLocation)
    {
        boolean result = false;
        double alertDistance = Double.parseDouble(settings.getString("distance_alert", "100"));

        if (myLocation.distanceTo(taskLocation) <= alertDistance)
        {
            result = true;
        }

        return result;
    }

    @Override
    protected void onStart()
    {
        if (loginCredentials.getBoolean(TAG_LOGGED, false))
        {
            m_GoogleApiClient.connect();
        }
        super.onStart();
    }

    @Override
    protected void onStop()
    {
        if (m_GoogleApiClient != null)
        {
            m_GoogleApiClient.disconnect();
        }

        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle)
    {
        checkIfTasksNear();
        if (m_RequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    public String checkIfEmpty(String s)
    {
        if (s.isEmpty() || s.equals("null"))
        {
            s = "";
        }

        return s;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults)
    {
        switch (requestCode)
        {
            case MY_PERMISSIONS_REQUEST_LOCATION:
            {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    Toast.makeText(this, "Thanks, now you get information about tasks!", Toast.LENGTH_SHORT).show();

                } else
                {
                   //disable functionality about location based stuff

                }
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void checkIfTasksNear()
    {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            askPermissions();
        }
        m_LastLocation = LocationServices.FusedLocationApi.getLastLocation(m_GoogleApiClient);

        if (m_LastLocation != null)
        {
            for (Task item : tasks)
            {
                if (!item.Start.equals("null") || item.Stop.equals("null") || item.UserID == 0 || item.UserID == userId)
                {
                    Location location = new Location("");
                    location.setLatitude(item.Lat);
                    location.setLongitude(item.Lon);
                    if (checkAlertAboutTask(m_LastLocation, location))
                    {
                        NotificationManager m_notifyMgr = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
                        Notification m_Notification;

                        Intent intent = new Intent(this, TaskInfoActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

                        //get data from task
                        ArrayList<String> list = new ArrayList<String>();
                        list.add(checkIfEmpty(item.Description));
                        list.add(checkIfEmpty(item.Description));
                        list.add(checkIfEmpty(item.Explanation));
                        list.add(checkIfEmpty("" + item.ID));
                        list.add(checkIfEmpty(item.Place));
                        list.add(checkIfEmpty("" + item.Lat));
                        list.add(checkIfEmpty("" + item.Lon));
                        list.add(checkIfEmpty(item.Start));
                        list.add(checkIfEmpty(loginCredentials.getString(TAG_NAME, "")));

                        intent.putStringArrayListExtra("test", list);

                        int requestID = (int) System.currentTimeMillis();
                        PendingIntent contentIntent = PendingIntent.getActivity(this, requestID,
                                intent, 0);

                        //build notification
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

                        m_Notification = builder.setContentIntent(contentIntent)
                                .setSmallIcon(R.drawable.cast_ic_notification_small_icon)
                                .setContentTitle("Task is near!")
                                .setContentText("Task " + item.Description + " is near!")
                                .build();

                        m_notifyMgr.notify(requestID, m_Notification);

                    }
                }
            }
        }
    }

    @Override
    public void onLocationChanged(Location location)
    {
        m_CurrentLocation = location;
        m_LastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        getTasks();
    }

    protected void startLocationUpdates()
    {
        if (ActivityCompat.checkSelfPermission
                (this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            askPermissions();
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                m_GoogleApiClient, m_LocationRequest,this);
    }
    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private void stopLocationUpdates()
    {
        if(m_GoogleApiClient!=null && m_GoogleApiClient.isConnected())
        {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    m_GoogleApiClient,this);
        }

    }
    private void askPermissions()
    {
        // Should we show an explanation?
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION))
        {

            // Show an explanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.
            Toast.makeText(this, "Sorry, tasks notifications are unavailable without gps", Toast.LENGTH_SHORT).show();

        }
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                MY_PERMISSIONS_REQUEST_LOCATION);

    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if(m_GoogleApiClient!=null)
        {
            if(m_GoogleApiClient.isConnected() && !m_RequestingLocationUpdates)
            {
                startLocationUpdates();
            }
        }
    }
    public void onSavedInstanceState(Bundle savedInstanceState)
    {
        savedInstanceState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY,
                m_RequestingLocationUpdates);
        savedInstanceState.putParcelable(LOCATION_KEY, m_CurrentLocation);
        savedInstanceState.putString(LAST_UPDATED_TIME_STRING_KEY, m_LastUpdateTime);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onConnectionSuspended(int i)
    {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {
    }

}
