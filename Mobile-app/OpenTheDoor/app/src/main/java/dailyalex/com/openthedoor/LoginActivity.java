package dailyalex.com.openthedoor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity implements TcpObserver{

    Button btnLogin;
    EditText etUsername,
             etPassword;
    CheckBox cbRemember;
    private static User mUser;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    //boolean autoLogin;

    //==============================================================================================

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);


        sharedPreferences = getSharedPreferences("loginref",MODE_PRIVATE);
        editor = sharedPreferences.edit();
        btnLogin = (Button)findViewById(R.id.btnLogin);
        etUsername = (EditText)findViewById(R.id.etLoginUsername);
        etPassword = (EditText)findViewById(R.id.etLoginPassword);
        cbRemember = (CheckBox)findViewById(R.id.cbRemember);

        mUser = new User();

        TcpClient.getInstance().Attach(this);

        cbRemember.setChecked(false);
        mUser.setUsername(sharedPreferences.getString("username",null));
        mUser.setPassword(sharedPreferences.getString("password",null));


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mUser.setUsername(etUsername.getText().toString());
                mUser.setPassword(etPassword.getText().toString());
                login();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mUser.getUsername() != null && mUser.getPassword() != null) {
            login();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        TcpClient.getInstance().Detach(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        TcpClient.getInstance().Attach(this);
    }

    //==============================================================================================

    public void login(){
        if(cbRemember.isChecked()){
            editor.putString("username",mUser.getUsername());
            editor.putString("password",mUser.getPassword());
            editor.commit();
        }
        TcpClient.getInstance().sendMessageToServer(Constants.LOGIN_USER_COMMAND + " " +
                                                    mUser.getUsername() + " " + mUser.getPassword());
    }

    //==============================================================================================

    public void logout(){
        etPassword.setText("");
        etUsername.setText("");
        mUser.setPassword("");
        mUser.setUsername("");
        cbRemember.setChecked(false);
        editor.putString("username",null);
        editor.putString("password",null);
        editor.commit();
    }

    //==============================================================================================

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == Constants.REQUEST_CODE_LOGOUT){
            if(resultCode == RESULT_OK){
                logout();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void updateTcp(String message) {
        if(message.contains(Constants.LOGIN_USER_RESPONSE_OK)){
            int spatiu = message.indexOf(" ");
            message = message.substring(spatiu+1);
            spatiu = message.indexOf(" ");
            mUser.setFirstName(message.substring(0,spatiu));
            message = message.substring(spatiu+1);
            spatiu = message.indexOf(" ");
            mUser.setLastName(message.substring(0,spatiu));
            message = message.substring(spatiu+1);
            if(message.equals("true"))
            {
                mUser.setIsAdministrator(true);
            }else {
                mUser.setIsAdministrator(false);
            }

            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
            intent.putExtra("username",mUser.getUsername());
            intent.putExtra("password",mUser.getPassword());
            intent.putExtra("first_name",mUser.getFirstName());
            intent.putExtra("last_name",mUser.getLastName());
            intent.putExtra("is_administrator",mUser.isAdministrator());
            startActivityForResult(intent,Constants.REQUEST_CODE_LOGOUT);

        }else if(message.equals(Constants.LOGIN_USER_RESPONSE_FAILED)){
            Snackbar.make(this.findViewById(R.id.loginContent),"Wrong username or password!",Snackbar.LENGTH_SHORT).show();
        }else if(message.equals(Constants.CONNECTION_ESTABLISHED))
        {
            Snackbar.make(this.findViewById(R.id.loginContent),"Connection established!",Snackbar.LENGTH_SHORT).show();
        }else if(message.equals(Constants.SERVER_DOWN))
        {
            Snackbar.make(this.findViewById(R.id.loginContent),"Server is down!",Snackbar.LENGTH_INDEFINITE)
                    .setAction("Retry connection?", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TcpClient.getInstance().start();
                }
            }).show();
        }
        else if(message.equals(Constants.CONNECTION_LOST))
        {
            Snackbar.make(this.findViewById(R.id.loginContent),"Something went wrong and the connection is lost.",Snackbar.LENGTH_INDEFINITE)
                    .setAction("Retry connection?", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TcpClient.getInstance().start();
                }
            }).show();
        }
    }
}
