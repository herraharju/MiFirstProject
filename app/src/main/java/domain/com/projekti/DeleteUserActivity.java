package domain.com.projekti;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class DeleteUserActivity extends AppCompatActivity implements
        View.OnClickListener,
        TAGS
{
    //private String TAG_LOGGED = "LogStatus",
    //        TAG_ID = "ID",
    //        TAG_PWORD="Password";
    private SharedPreferences loginCredentials;
    private EditText et_pword,et_cpword;
    private Button del_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_user);
        Init();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        del_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input_pword,input_cpword;
                input_pword = et_pword.getText().toString();
                input_cpword = et_cpword.getText().toString();
                if(input_pword.equals(loginCredentials.getString(TAG_PWORD,""))&& input_pword.equals(input_cpword)){
                    deleteUser().show();
                }else{
                    Toast.makeText(getApplicationContext(), "Wrong password!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    private void Init(){
        et_pword = (EditText)findViewById(R.id.du_et_old_pword);
        et_cpword = (EditText)findViewById(R.id.du_et_cpword);
        del_btn = (Button)findViewById(R.id.du_del_btn);
        loginCredentials = getSharedPreferences("domain.com.projekti.PREFERENCE_FILE_KEY", Context.MODE_PRIVATE);

    }
    private AlertDialog.Builder deleteUser(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete current user");
        builder.setMessage("Are you sure you want to delete this user?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int deleteId=loginCredentials.getInt(TAG_ID,-1);
                String urlAddress="https://codez.savonia.fi/jukka/project/deleteuser.php?ID="+deleteId;
                //MyASyncHandler myASyncHandler =  new MyASyncHandler(false,context);
                try{
                    new MyASyncHandler(false,getApplicationContext()).execute("0",urlAddress).get();
                    getSharedPreferences("domain.com.projekti.PREFERENCE_FILE_KEY", Context.MODE_PRIVATE).edit().clear().commit();
                    finish();
                }catch(Exception e){
                    Log.d("Response: ", "> " + e.getMessage());
                }
                Toast.makeText(DeleteUserActivity.this, "User was deleted succesfully", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("NO",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(DeleteUserActivity.this, "User wasn't deleted", Toast.LENGTH_SHORT).show();
            }
        });
        return builder;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.du_del_btn:

        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return false;
        }
    }
}
