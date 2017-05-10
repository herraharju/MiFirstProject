package domain.com.projekti;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class NewTaskActivity
        extends Base_Activity
        implements View.OnClickListener{

    private Button btn_map_loc, btn_create;
    private EditText et_task_desc, et_task_place;
    private TextView tv_task_lon, tv_task_lat, tv_task_addr;

    private static final int PLACE_PICKER_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);
        Init();
        setListeners();
    }

    private void Init() {
        btn_map_loc = (Button) findViewById(R.id.btn_new_task_loc_map);
        btn_create = (Button) findViewById(R.id.btn_new_task_create);
        et_task_desc = (EditText) findViewById(R.id.et_new_task_desc);
        et_task_place = (EditText) findViewById(R.id.et_new_task_place);
        tv_task_lat = (TextView) findViewById(R.id.tv_new_task_lat);
        tv_task_lon = (TextView) findViewById(R.id.tv_new_task_lon);
        tv_task_addr = (TextView) findViewById(R.id.tv_new_task_address);
    }

    private void setListeners() {
        btn_map_loc.setOnClickListener(this);
        btn_create.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_new_task_loc_map:
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_new_task_create:
                //add code here
                boolean check = true;
                String descript = et_task_desc.getText().toString();
                String place = et_task_place.getText().toString();
                String lon = tv_task_lon.getText().toString();
                String lat = tv_task_lat.getText().toString();
                Double lonVal = scanDoublesFromString(lon);
                Double latVal = scanDoublesFromString(lat);
                //input check
                if(descript.isEmpty()){
                    Toast.makeText(this, "Task description required", Toast.LENGTH_SHORT).show();
                    check = false;
                }
                //address/place check
                if(place.isEmpty()){
                    Toast.makeText(this, "Place description or address required", Toast.LENGTH_SHORT).show();
                    check = false;
                }
                if(lonVal<=0.0 || latVal<=0.0){
                    Toast.makeText(this, "You need to give location for task", Toast.LENGTH_SHORT).show();
                    check = false;
                }
                if(check){
                    //create new task to db
                    String urlAddress = "https://codez.savonia.fi/jukka/project/inserttask.php?Description="+descript+"&Lon="+lonVal+"&Lat="+latVal+"&Place="+place;
                    try {
                        new MyASyncHandler(false,getApplicationContext()).execute("0",urlAddress).get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }finally {
                        Intent backToMain = new Intent(this,MainActivity.class);
                        startActivity(backToMain);

                        finish();
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                tv_task_addr.setText("Address: "+place.getAddress());
                tv_task_lon.setText("Longitude: "+Double.toString(place.getLatLng().longitude));
                tv_task_lat.setText("Latitude: "+Double.toString(place.getLatLng().latitude));
            }else{
                super.onActivityResult(requestCode,resultCode,data);
            }
        }
    }
    private Double scanDoublesFromString(String s){
        Double result=0.0;
        String toParse[] = s.split(" ");

        //get last double from string
        for(String item : toParse){
            try{
                result = Double.parseDouble(item);
            }catch(NumberFormatException e){
                result = 0.0;
            }
        }

        return result;
    }
}
