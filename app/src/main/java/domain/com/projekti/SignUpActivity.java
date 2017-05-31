package domain.com.projekti;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


//sign up activity
public class SignUpActivity extends Base_Activity
{
    private EditText et_uname,et_pword,et_cpword,et_desc;
    private String uname,pword,cpword,desc;
    private Button btn_create;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Init();
        btn_create.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // get variables from edittext fields
                uname=et_uname.getText().toString();
                pword=et_pword.getText().toString();
                cpword=et_cpword.getText().toString();
                desc=et_desc.getText().toString();
                //check if they are correct
                if(uname.isEmpty()||pword.isEmpty()||cpword.isEmpty())
                {
                    Toast.makeText(SignUpActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                }
                else if(!(pword.equals(cpword)))
                {
                    Toast.makeText(SignUpActivity.this, "Passwords didn't match!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if(checkInternetConnection(context))
                    {
                        String urlAddress = "https://codez.savonia.fi/jukka/project/insertuser.php?Name="+uname+"&Password="+pword+"&Description="+desc;
                        //my = new MyASyncHandler(true,context);
                        try
                        {
                            new MyASyncHandler(true,context).execute("0", urlAddress).get();
                            Toast.makeText(SignUpActivity.this, "Account was created!", Toast.LENGTH_SHORT).show();
                        }
                        catch(Exception e)
                        {
                            Log.d("Response: ", "> " + e.getMessage());
                        }
                        Intent intent = new Intent (getApplicationContext(),MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else
                    {
                        Toast.makeText(context, "There's no internet connection", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });
    }
    private void Init()
    {
        btn_create= (Button)findViewById(R.id.su_btn_create);
        et_uname= (EditText)findViewById(R.id.su_et_uname);
        et_pword= (EditText)findViewById(R.id.su_et_pword);
        et_cpword= (EditText)findViewById(R.id.su_et_cpword);
        et_desc = (EditText)findViewById(R.id.su_et_desc);
        desc="";
        context = this;
    }
}
