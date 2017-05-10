package domain.com.projekti;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class SignInActivity extends Base_Activity implements TAGS{
    private EditText et_uname, et_pword;
    //private String TAG_ID = "ID",
    //        TAG_NAME = "Name",
    //        TAG_LOGGED = "LogStatus",
    //        TAG_PASSWORD = "Password";
    private String uname, pword,jsonData;
    private User currentUser;
    private MyASyncHandler myASyncHandler;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        Init();
    }

    private void Init() {
        et_uname = (EditText) findViewById(R.id.si_et_uname);
        et_pword = (EditText) findViewById(R.id.si_et_pword);
        currentUser = new User();
    }

    public void tryLogin(View v) {
        uname = et_uname.getText().toString();
        pword = et_pword.getText().toString();
        String urlAddress = "https://codez.savonia.fi/jukka/project/user.php?Name="+uname+"&Password="+pword;
        myASyncHandler = new MyASyncHandler(true,this);
        try{
            jsonData=myASyncHandler.execute("1",urlAddress).get();
            if(TextUtils.isEmpty(jsonData)){
                Toast.makeText(this, "Incorrect username or password!", Toast.LENGTH_SHORT).show();
            }else{
                currentUser = ParseJSON(jsonData);
                if(currentUser!=null){
                    saveCredentials();
                    Toast.makeText(this, "Logged in!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(this, "Incorrect username or password!", Toast.LENGTH_SHORT).show();
                }
            }
        }catch(Exception e){
            Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show();
        }
    }

    private User ParseJSON(String json) {
        if (json != null) {
            try {

                JSONArray jsonArray  = new JSONArray(json);
                Log.d("JSON to list: ", "> " + json);

                JSONObject jsonObj = jsonArray.getJSONObject(0);
                String id = jsonObj.getString(TAG_ID);
                String name = jsonObj.getString(TAG_NAME);
                String password = jsonObj.getString(TAG_PWORD);

                User user = new User();

                user.ID = Integer.parseInt(id);
                user.Name = name;
                user.Password = password;
                return user;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            Log.e("ServiceHandler", "No data received from HTTP Request");
            return null;
        }
    }
    private void saveCredentials(){
        SharedPreferences loginCredentials = getSharedPreferences("domain.com.projekti.PREFERENCE_FILE_KEY", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = loginCredentials.edit();
        editor.putString(TAG_NAME,currentUser.Name);
        editor.putString(TAG_PWORD,currentUser.Password);
        editor.putInt(TAG_ID,currentUser.ID);
        editor.putBoolean(TAG_LOGGED,true);
        editor.commit();
    }
}
