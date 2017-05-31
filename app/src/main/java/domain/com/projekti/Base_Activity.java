package domain.com.projekti;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;


//action bar activity and its functionality

public class Base_Activity extends AppCompatActivity implements TAGS
{
    private SharedPreferences loginCredentials;
    private MenuItem loginBtn,registerBtn,logoutBtn,newTaskBtn;

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.action_bar, menu);
        Init(menu);
            if(loginCredentials.getBoolean(TAG_LOGGED,false))
            {
                loggedInBtns();
            }
            else
                {
                notLoggedInBtns();
            }
        return true;
    }
    public void Init(Menu menu)
    {
        loginBtn = menu.findItem(R.id.ab_login);
        registerBtn = menu.findItem(R.id.ab_register);
        logoutBtn = menu.findItem(R.id.ab_logout);
        newTaskBtn = menu.findItem(R.id.ab_new_task);
        loginCredentials = getSharedPreferences("domain.com.projekti.PREFERENCE_FILE_KEY", Context.MODE_PRIVATE);
    }
    public void loggedInBtns()
    {
        loginBtn.setVisible(false);
        registerBtn.setVisible(false);
        logoutBtn.setVisible(true);
        newTaskBtn.setVisible(true);
    }
    public void notLoggedInBtns()
    {
        loginBtn.setVisible(true);
        registerBtn.setVisible(true);
        logoutBtn.setVisible(false);
        newTaskBtn.setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.ab_register:
                if(!(this.getClass().getSimpleName().equals(SignUpActivity.class.getSimpleName())))
                {
                    Intent register = new Intent(this, SignUpActivity.class);
                    startActivity(register);
                    finish();
                }
                break;
            case R.id.ab_login:
                if(!(this.getClass().getSimpleName().equals(SignInActivity.class.getSimpleName())))
                {
                    Intent login = new Intent(this, SignInActivity.class);
                    startActivity(login);
                    finish();
                }
                break;
            case R.id.ab_home:
                if(!(this.getClass().getSimpleName().equals(MainActivity.class.getSimpleName())))
                {
                    goToMain();
                }
                break;
            case R.id.ab_logout:
                getSharedPreferences("domain.com.projekti.PREFERENCE_FILE_KEY", Context.MODE_PRIVATE).edit().clear().commit();
                goToMain();
                break;
            case R.id.ab_new_task:
                if(!(this.getClass().getSimpleName().equals(NewTaskActivity.class.getSimpleName())))
                {
                    askCredentials().show();
                }
                break;
            case R.id.ab_settings:
                    Intent settings = new Intent(this,SettingsActivity.class);
                    startActivity(settings);
                break;
            default:
                return false;
        }
        return true;
    }

    private AlertDialog.Builder askCredentials()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Admin");
        builder.setMessage("Give administrator username and password");

        //Set the input;
        final EditText username = new EditText(this);
        final EditText password = new EditText(this);

        //set input types
        username.setInputType(InputType.TYPE_CLASS_TEXT);
        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        //set hints to input boxes
        username.setHint("Username");
        password.setHint("Password");

        //set inputs to one layout
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(username);
        layout.addView(password);

        //add layout to alertdialog
        builder.setView(layout);
        builder.setPositiveButton("Login", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                if(username.getText().toString().equals("Admin")&& password.getText().toString().equals("123456"))
                {
                    Intent newTask = new Intent(Base_Activity.this,NewTaskActivity.class);
                    startActivity(newTask);

                }
                else
                    {
                    Toast.makeText(Base_Activity.this, "Wrong username or password!", Toast.LENGTH_SHORT).show();
                    askCredentials().show();
                }
            }
        });
        builder.setNegativeButton("Cancel",new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                Toast.makeText(Base_Activity.this, "You need admin credentials to create a new task!", Toast.LENGTH_SHORT).show();
            }
        });
        return builder;
    }
    public void goToMain()
    {
        Intent home = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(home);
        finish();
    }
    public boolean checkInternetConnection(Context ctx)
    {
        ConnectivityManager cm = (ConnectivityManager)ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();


        return netInfo != null && netInfo.isConnectedOrConnecting() && cm.getActiveNetworkInfo().isAvailable() && cm.getActiveNetworkInfo().isConnected();
    }
}
