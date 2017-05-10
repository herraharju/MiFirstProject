package domain.com.projekti;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MrKohvi on 14.4.2017.
 */

public class MyAdapter extends ArrayAdapter<Task> {
    private final Context context;
    private final ArrayList<Task> tasks;
    private final int loggedId;

    public MyAdapter(Context ctx, ArrayList<Task> objects,int logId) {
        super(ctx, R.layout.activity_main, objects);
        this.context=ctx;
        this.tasks = objects;
        this.loggedId=logId;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_item,parent,false);
        //initialize textview-element items in a row
        TextView desc = (TextView) rowView.findViewById(R.id.tv_task_desc);
        TextView lon = (TextView) rowView.findViewById(R.id.tv_task_lon);
        TextView lat = (TextView) rowView.findViewById(R.id.tv_task_lat);
        TextView place = (TextView) rowView.findViewById(R.id.tv_task_place);
        //check for task color set
        if((!(getItem(position).Start.equals("null")))&&(!(getItem(position).Stop.equals("null")))){
            rowView.setBackgroundColor(ContextCompat.getColor(context,R.color.GRAY));
        }else if(((getItem(position).UserID==(loggedId)))&&(getItem(position).Start.equals("null"))&&(getItem(position).Stop.equals("null"))){
            rowView.setBackgroundColor(ContextCompat.getColor(context,R.color.YELLOW));
        }else if(!(getItem(position).Start.equals("null"))){
            rowView.setBackgroundColor(ContextCompat.getColor(context,R.color.TURQOISE));
        }else if ((getItem(position).Start.equals("null"))&&(getItem(position).Stop.equals("null"))){
            rowView.setBackgroundColor(ContextCompat.getColor(context,R.color.GREEN));
        }

        //set texts to items in a row
        desc.setText("Description: "+tasks.get(position).Description);
        place.setText("Place: "+(tasks.get(position).Place));
        lon.setText("Longitude: "+Double.toString(tasks.get(position).Lon));
        lat.setText("Latitude: "+Double.toString(tasks.get(position).Lat));
        return rowView;
    }

}
