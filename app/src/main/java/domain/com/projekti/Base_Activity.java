package domain.com.projekti;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;


/**
 * Created by MrKohvi on 10.4.2017.
 */

public class Base_Activity extends AppCompatActivity {
    private String TAG_LOGGED = "LogStatus",
                   TAG_ID = "ID";
    private SharedPreferences loginCredentials;
    private MenuItem loginBtn,registerBtn,logoutBtn,newTaskBtn;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar, menu);
        Init(menu);
            if(loginCredentials.getBoolean(TAG_LOGGED,false)){
                loggedInBtns();
            }else{
                notLoggedInBtns();
            }
        return true;
    }
    public void Init(Menu menu){
        loginBtn = menu.findItem(R.id.ab_login);
        registerBtn = menu.findItem(R.id.ab_register);
        logoutBtn = menu.findItem(R.id.ab_logout);
        //deleteBtn = menu.findItem(R.id.ab_delete);
        newTaskBtn = menu.findItem(R.id.ab_new_task);
        loginCredentials = getSharedPreferences("domain.com.projekti.PREFERENCE_FILE_KEY", Context.MODE_PRIVATE);
    }
    public void loggedInBtns(){
        loginBtn.setVisible(false);
        registerBtn.setVisible(false);
        //deleteBtn.setVisible(true);
        logoutBtn.setVisible(true);
        newTaskBtn.setVisible(true);
    }
    public void notLoggedInBtns(){
        loginBtn.setVisible(true);
        registerBtn.setVisible(true);
        logoutBtn.setVisible(false);
        //deleteBtn.setVisible(false);
        newTaskBtn.setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ab_register://user register
                if(!(this.getClass().getSimpleName().equals(SignUpActivity.class.getSimpleName()))){
                    Intent register = new Intent(this, SignUpActivity.class);
                    startActivity(register);
                    finish();
                }
                break;
            /*case R.id.ab_delete:
                deleteUser().show();
                break;*/
            case R.id.ab_login://user login
                if(!(this.getClass().getSimpleName().equals(SignInActivity.class.getSimpleName()))) {
                    Intent login = new Intent(this, SignInActivity.class);
                    startActivity(login);
                    finish();
                }
                break;
            case R.id.ab_home://user login
                if(!(this.getClass().getSimpleName().equals(MainActivity.class.getSimpleName()))) {
                    goToMain();
                }
                break;
            case R.id.ab_logout://user logout
                getSharedPreferences("domain.com.projekti.PREFERENCE_FILE_KEY", Context.MODE_PRIVATE).edit().clear().commit();
                goToMain();
                break;
            /*case R.id.ab_details://edit user details
                if(!(this.getClass().getSimpleName().equals(EditUserActivity.class.getSimpleName()))){
                    Intent editDetails = new Intent(this,EditUserActivity.class);
                    startActivity(editDetails);
                }
                break;*/
            case R.id.ab_new_task:
                if(!(this.getClass().getSimpleName().equals(NewTaskActivity.class.getSimpleName()))){
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

    private AlertDialog.Builder askCredentials(){
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
        builder.setPositiveButton("Login", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(username.getText().toString().equals("Admin")&& password.getText().toString().equals("123456")){
                    Intent newTask = new Intent(Base_Activity.this,NewTaskActivity.class);
                    startActivity(newTask);

                }else{
                    Toast.makeText(Base_Activity.this, "Wrong username or password!", Toast.LENGTH_SHORT).show();
                    askCredentials().show();
                }
            }
        });
        builder.setNegativeButton("Cancel",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(Base_Activity.this, "You need admin credentials to create a new task!", Toast.LENGTH_SHORT).show();
            }
        });
        return builder;
    }
    public void goToMain(){
        Intent home = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(home);
        finish();
    }
}
