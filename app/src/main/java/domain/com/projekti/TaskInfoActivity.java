package domain.com.projekti;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.ArrayList;

public class TaskInfoActivity extends AppCompatActivity implements TAGS
{
    private TextView tv_user,tv_desc,tv_exp,tv_id,tv_place,tv_lat,tv_lon,tv_start;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_info);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Task info");
        Init();
        Intent intent = getIntent();

        if(intent.hasExtra("test"))
        {
            ArrayList<String> list =  intent.getExtras().getStringArrayList("test");
            tv_user.setText("User: "+list.get(0));
            tv_desc.setText("Description:" +list.get(1));
            tv_exp.setText("Explanation:" +list.get(2));
            tv_id.setText("Task ID:" +list.get(3));
            tv_place.setText("Place:" +list.get(4));
            tv_lat.setText("Latitude:" +list.get(5));
            tv_lon.setText("Longitude:" +list.get(6));
            tv_start.setText("Started:" +list.get(7));
        }
    }

    public void Init()
    {
        tv_user = (TextView)findViewById(R.id.ti_tv_user);
        tv_desc = (TextView)findViewById(R.id.ti_tv_task_desc);
        tv_exp = (TextView)findViewById(R.id.ti_tv_task_explanation);
        tv_id = (TextView)findViewById(R.id.ti_tv_task_id);
        tv_place = (TextView)findViewById(R.id.ti_tv_task_place);
        tv_lat = (TextView)findViewById(R.id.ti_tv_lat);
        tv_lon = (TextView)findViewById(R.id.ti_tv_lon);
        tv_start = (TextView)findViewById(R.id.ti_tv_start);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return false;
        }
    }
}
