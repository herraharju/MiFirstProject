package domain.com.projekti;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EditUserActivity extends AppCompatActivity implements TAGS
{
    private EditText et_pword,et_cpword,et_desc,et_old_pword;
    private String pword,cpword,desc,old_pword;
    private Button btn_editDetails;
    private Context context;
    private SharedPreferences loginCredentials;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);
        Init();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        btn_editDetails.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // get variables from edittext fields
                old_pword = et_old_pword.getText().toString();
                pword=et_pword.getText().toString();
                cpword=et_cpword.getText().toString();
                desc=et_desc.getText().toString();
                //check if input is correct
                if(pword.isEmpty()||cpword.isEmpty()||old_pword.isEmpty())
                {
                    Toast.makeText(EditUserActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                }
                else if(!(old_pword.equals(loginCredentials.getString(TAG_PWORD,""))))
                {
                    Toast.makeText(EditUserActivity.this, "Current password was wrong!", Toast.LENGTH_SHORT).show();
                }
                else if(!(pword.equals(cpword)))
                {
                    Toast.makeText(EditUserActivity.this, "Passwords didn't match!", Toast.LENGTH_SHORT).show();
                }
                else
                    {
                    int userId=loginCredentials.getInt(TAG_ID,-1);
                    String urlAddress = "https://codez.savonia.fi/jukka/project/updateuser.php?ID="+userId+"&Password="+pword+"&Description="+desc;
                    try
                    {
                        new MyASyncHandler(true,context).execute("0", urlAddress).get();
                        SharedPreferences loginCredentials = getSharedPreferences("domain.com.projekti.PREFERENCE_FILE_KEY", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = loginCredentials.edit();
                        editor.putString(TAG_PWORD,pword);
                        editor.commit();
                        Toast.makeText(EditUserActivity.this, "Account was edited succesfully!", Toast.LENGTH_SHORT).show();
                    }
                    catch(Exception e)
                    {
                        Log.d("Response: ", "> " + e.getMessage());
                    }
                    Intent intent = new Intent(context,MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    private void Init()
    {
        btn_editDetails= (Button)findViewById(R.id.eu_btn_edit);
        et_old_pword = (EditText)findViewById(R.id.eu_et_old_pword);
        et_pword= (EditText)findViewById(R.id.eu_et_pword);
        et_cpword= (EditText)findViewById(R.id.eu_et_cpword);
        et_desc = (EditText)findViewById(R.id.eu_et_desc);
        desc="";
        context = this;
        loginCredentials = getSharedPreferences("domain.com.projekti.PREFERENCE_FILE_KEY", Context.MODE_PRIVATE);
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
