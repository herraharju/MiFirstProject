package domain.com.projekti;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by MrKohvi on 12.4.2017.
 */

public class MyASyncHandler extends AsyncTask<String,Void,String> {
    public MyASyncHandler(boolean showProgressDialog,Context activityContext){
        super();
        this.showLoading = showProgressDialog;
        this.context = activityContext;
    }
    private boolean showLoading;
    private ProgressDialog proDialog;
    private Context context;
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if(showLoading){
           // Showing progress loading dialog
            proDialog = new ProgressDialog(context);
            proDialog.setMessage("Please wait…");
            proDialog.setCancelable(false);
            proDialog.show();
        }
    }
    @Override
    protected String doInBackground(String... params) {
        String ret="";
        if(params[0].equals("0")) {//execute url and send data to server
            try {
                URL url = new URL(params[1]);
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url(url).build();
                client.newCall(request).execute();
            } catch (Exception e) {
                Log.d("Response: ", "> " + e.getMessage());
            }
            ret= "Account created succesfully!";
        }else if(params[0].equals("1")){//execute url and receive return JSON value from server
            HttpURLConnection urlConnection=null;
            try{
                URL url = new URL(params[1]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream stream = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
                StringBuilder builder = new StringBuilder();

                String inputString;
                while ((inputString = bufferedReader.readLine()) != null) {
                    builder.append(inputString);
                }
                Log.d("Response: ", "> " + builder.toString());

                ret = builder.toString();

            }catch(Exception e){
                Log.d("Response: ", "> " + e.getMessage());
            }finally{
                if(urlConnection!=null){
                    urlConnection.disconnect();
                }
            }
        }
        return ret;
    }
    @Override
    protected void onPostExecute(String requestresult) {
        //Dismiss progress dialog
        super.onPostExecute(requestresult);
        if(showLoading){
            if (proDialog.isShowing()){
                proDialog.dismiss();
            }
        }

    }

}

