package domain.com.projekti;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;

import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;


public class MainActivity extends Base_Activity implements TAGS {
     //String TAG_LOGGED = "LogStatus",
     //       TAG_USER_ID_TASK = "UserID",
     //       TAG_USER_ID = "ID",
     //       TAG_TASK_ID="ID",
     //       TAG_LON="Lon",
     //       TAG_LAT="Lat",
     //       TAG_NAME = "Name",
     //       TAG_PASSWORD = "Password",
     //       TAG_START = "Start",
     //       TAG_STOP = "Stop",
     //       TAG_EXPLANATION = "Explanation",
     //       TAG_DESCRIPTION = "Description",
     //       TAG_PLACE = "Place";
    private String urlAddress;
    private TextView tv_logged,tv_loggedAs;
    private ListView listView;
    private MyAdapter adapter;
    SharedPreferences loginCredentials;
     int userId;
    private ArrayList<Task> tasks;
    private LinearLayout layoutLoggedOut,layoutLoggedIn,layoutTips;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Init();
        checkIfLoggedIn();
    }
    public void Init(){
        tv_logged=(TextView)findViewById(R.id.main_tv_no_tasks);
        tv_loggedAs = (TextView)findViewById(R.id.main_tv_login_info);
        listView = (ListView) findViewById(R.id.myListView);
        registerForContextMenu(listView);
        listView.setLongClickable(true);
        tasks =new <Task> ArrayList();
        layoutLoggedOut = (LinearLayout)findViewById(R.id.loggedOut);
        layoutLoggedIn = (LinearLayout)findViewById(R.id.loggedIn);
        layoutTips = (LinearLayout)findViewById(R.id.tipsLayout);
        adapter=null;
        urlAddress = "";
    }
    public void checkIfLoggedIn(){
        loginCredentials = getSharedPreferences("domain.com.projekti.PREFERENCE_FILE_KEY", Context.MODE_PRIVATE);
        if(loginCredentials.getBoolean(TAG_LOGGED,false)){
            //if logged in
            tv_loggedAs.setText("Logged as: "+loginCredentials.getString(TAG_NAME,""));
            //get user id
            SharedPreferences sharedPrefs = PreferenceManager
                    .getDefaultSharedPreferences(this);
            tv_loggedAs.setText(sharedPrefs.getString("sync_frequency",""));
            userId=loginCredentials.getInt(TAG_ID,-1);
            //set listview before setting it visible
            getTasks();
            //get user tasks and set layout components
            layoutLoggedIn.setVisibility(View.VISIBLE);
            layoutLoggedOut.setVisibility(View.GONE);
            layoutTips.setVisibility(View.VISIBLE);

        }else{//if not logged in
            //set layout components
            layoutLoggedOut.setVisibility(View.VISIBLE);
            layoutLoggedIn.setVisibility(View.GONE);
            layoutTips.setVisibility(View.GONE);
        }
    }
    private ArrayList<Task> ParseJSON(String json) {
        ArrayList<Task> tasks = new <Task>ArrayList();
        if (json != null) {
            try {

                JSONArray jsonArray  = new JSONArray(json);
                Log.d("JSON to list: ", "> " + json);
                for(int i = 0; i<jsonArray.length();i++){

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
                    if(lat.equals("null")){
                        task.Lat=0.0;
                    }else{
                        task.Lat = Double.parseDouble(lat);
                    }
                    if(lon.equals("null")){
                        task.Lon=0.0;
                    }else{
                        task.Lon = Double.parseDouble(lon);
                    }

                    task.ID = Integer.parseInt(id);
                    task.Start = start;
                    task.Stop = stop;
                    task.Explanation = explanation;
                    task.Description = description;
                    task.Place = place;
                    if(!(userid_task.equals("null"))){
                        task.UserID = Integer.parseInt(userid_task);
                    }


                    tasks.add(task);
                }

                return tasks;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            Log.e("ServiceHandler", "No data received from HTTP Request");
            return null;
        }
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.popup_menu_task,menu);
        /*
        menu.setHeaderTitle("Context Menu");
        menu.add(0, v.getId(), 0, "Action 1");
        menu.add(0, v.getId(), 0, "Action 2");*/
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo)item.getMenuInfo();
        int index = info.position;
        urlAddress = "";
        String desc = tasks.get(index).Description;
        int id = tasks.get(index).ID;
        switch(item.getItemId()){
            case R.id.task_delete:
                Toast.makeText(getApplicationContext(), "Delete "+desc, Toast.LENGTH_SHORT).show();
                urlAddress = "https://codez.savonia.fi/jukka/project/deletetask.php?Id="+id;

                try {
                    new MyASyncHandler(true,this).execute("1",urlAddress).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                break;
            case R.id.task_reserve:
                if(tasks.get(index).UserID==0){
                    Toast.makeText(getApplicationContext(), "Reserve "+desc, Toast.LENGTH_SHORT).show();

                    urlAddress = "https://codez.savonia.fi/jukka/project/reservetask.php?Id="+id+"&UserId="+userId;

                    try {
                        new MyASyncHandler(true,this).execute("1",urlAddress).get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }else{
                    Toast.makeText(this, "Someone has already reserved this task!", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.task_start:
                if(tasks.get(index).Start.equals("null")&& tasks.get(index).Stop.equals("null")){
                    Toast.makeText(getApplicationContext(), "Start "+desc, Toast.LENGTH_SHORT).show();
                    urlAddress = "https://codez.savonia.fi/jukka/project/starttask.php?Id="+id;
                    try {
                        new MyASyncHandler(true,this).execute("1",urlAddress).get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                } else if(!(tasks.get(index).Stop.equals("null"))){
                    Toast.makeText(this, "Task has already been stopped!", Toast.LENGTH_SHORT).show();
                }else if (!(tasks.get(index).Start.equals("null"))){
                    Toast.makeText(this, "Task has already been started!", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.task_stop:
                if(tasks.get(index).Start.equals("null")&&tasks.get(index).Stop.equals("null")) {
                    Toast.makeText(this, "Task is not even started yet", Toast.LENGTH_SHORT).show();
                }else if(!(tasks.get(index).Start.equals("null"))&&!(tasks.get(index).Stop.equals("null"))){
                    Toast.makeText(this, "Task has already been stopped!", Toast.LENGTH_SHORT).show();
                }else if(!(tasks.get(index).Start.equals("null"))&&(tasks.get(index).Stop.equals("null"))){
                    Toast.makeText(getApplicationContext(), "Stop "+desc, Toast.LENGTH_SHORT).show();
                    urlAddress = "https://codez.savonia.fi/jukka/project/stoptask.php?Id="+id+"&Explanation=Stopped";

                    try {
                        new MyASyncHandler(true,this).execute("1",urlAddress).get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }else{
                    Toast.makeText(this, "Task has already been stopped!", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
        getTasks();
        return true;
    }

    private void getTasks(){
        try{
            urlAddress= "https://codez.savonia.fi/jukka/project/UserAndFreeTasks.php?UserID="+userId;
            String json_tasks = new MyASyncHandler(true,this).execute("1",urlAddress).get();
            if(!(json_tasks.equals("[]") || json_tasks.isEmpty())){
                tasks=ParseJSON(json_tasks);
                adapter = new MyAdapter(MainActivity.this,tasks,userId);
                listView.setAdapter(adapter);
                listView.setVisibility(View.VISIBLE);
                tv_logged.setVisibility(View.GONE);
            }else{
                listView.setVisibility(View.GONE);
                tv_logged.setVisibility(View.VISIBLE);
                layoutTips.setVisibility(View.VISIBLE);
            }
        }catch(Exception e){
            Log.d("Response: ", "> " + e.getMessage());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkIfLoggedIn();
    }
}
