package dailyalex.com.openthedoor;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, TcpObserver{

    //==============================================================================================

    private TextView navDrawerUsername;
    NavigationView navigationView;
    private static User mUser;
    AlertDialog dialog;
    boolean loginAdmin = false;

    //==============================================================================================

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mUser = new User();
        TcpClient.getInstance().Attach(this);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);

        navDrawerUsername = (TextView)headerView.findViewById(R.id.tvNavDrawerUsername);

        mUser.setUsername(getIntent().getStringExtra("username"));
        mUser.setPassword(getIntent().getStringExtra("password"));
        mUser.setFirstName(getIntent().getStringExtra("first_name")); //TODO Nume utilizatori din fisier sau alta modalitate
        mUser.setLastName(getIntent().getStringExtra("last_name"));
        mUser.setIsAdministrator(getIntent().getBooleanExtra("is_administrator",false));
        String userInfo = mUser.getFirstName() + " " + mUser.getLastName() + "\n";

        Menu menuNav = navigationView.getMenu();
        MenuItem administratorItem = menuNav.findItem(R.id.nav_administrator);
        MenuItem logItem = menuNav.findItem(R.id.nav_log);

        if(mUser.isAdministrator())
        {
            userInfo += "Administrator";
            administratorItem.setVisible(true);
            logItem.setVisible(true);
        }else
        {
            userInfo += "Standard user";
            administratorItem.setVisible(false);
            logItem.setVisible(false);
        }
        navDrawerUsername.setText(userInfo);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        tx.replace(R.id.screen_area, new HomeFragment(),HomeFragment.TAG);
        tx.commitAllowingStateLoss();

        navigationView.setCheckedItem(R.id.nav_home);

    }

    @Override
    protected void onStop() {
        super.onStop();
        TcpClient.getInstance().Detach(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //No call for super().
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        TcpClient.getInstance().Attach(this);
        navigationView.setCheckedItem(R.id.nav_home);
        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        tx.replace(R.id.screen_area, new HomeFragment(),HomeFragment.TAG);
        tx.commitAllowingStateLoss();
    }

    //==============================================================================================

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.exit_app) {
            finish();
            System.exit(0);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //==============================================================================================

    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment = null;

        if (id == R.id.nav_home) {

            fragment = new HomeFragment();
        } else if (id == R.id.nav_gallery) {
//            fragment = new GalleryFragment();
        } else if (id == R.id.nav_administrator) {
            loginAdmin = true;
            if(mUser.isAdministrator()) {
                showLoginDialog();
            }else{
//                Snackbar.make(findViewById(R.id.screen_area),"You are not an administrator!",Snackbar.LENGTH_SHORT);
                Toast.makeText(this,"You are not an administrator!",Toast.LENGTH_SHORT);
            }
        } else if (id == R.id.nav_settings) {
//            fragment = new SettingsFragment();
        } else if (id == R.id.nav_log) {
            loginAdmin = false;
            if(mUser.isAdministrator()) {
                showLoginDialog();
            }else{
//                Snackbar.make(findViewById(R.id.screen_area),"You are not an administrator!",Snackbar.LENGTH_SHORT);
                Toast.makeText(this,"You are not an administrator!",Toast.LENGTH_SHORT);
            }
        } else if (id == R.id.nav_logout) {
            Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
            setResult(RESULT_OK,intent);
            navDrawerUsername.setText("Not logged in");
            finish();
        }

        changeFragment(fragment);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //==============================================================================================

    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if(getFragmentManager().getBackStackEntryCount() > 0){
            getFragmentManager().popBackStack();
        }
        else if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    //==============================================================================================

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
        return false;
    }

    //==============================================================================================

    void showLoginDialog() {

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_login, null);


        final EditText etPassword = (EditText) mView.findViewById(R.id.etDialogPassword);
        final Button btnLogin = (Button) mView.findViewById(R.id.btnLogin);

        final EditText etUsername = (EditText) mView.findViewById(R.id.etDialogUsername) ;


        ImageView ivFingerprint = (ImageView)mView.findViewById(R.id.ivFingerprint);
        ivFingerprint.setVisibility(View.GONE);

        mBuilder.setView(mView);
        dialog = mBuilder.create();


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etPassword.getText().toString() != null && etUsername.getText().toString() != null && loginAdmin) {

                    TcpClient.getInstance().sendMessageToServer(Constants.ADMINISTRATOR_LOGIN_COMMAND
                                                                + " " + etUsername.getText().toString() + " "
                                                                + etPassword.getText().toString());
                }else if (etPassword.getText().toString() != null && etUsername.getText().toString() != null && !loginAdmin) {

                    TcpClient.getInstance().sendMessageToServer(Constants.LOGS_LOGIN_COMMAND
                            + " " + etUsername.getText().toString() + " "
                            + etPassword.getText().toString());
                }else{
                    Toast.makeText(MainActivity.this,"Username or password not provided!",Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.show();

    }


    public void updateTcp(String message){
        if(message.equals(Constants.ADMINISTRATOR_LOGIN_RESPONSE_OK)){
            dialog.dismiss();
            Fragment fragment = new AdministratorFragment();
            changeFragment(fragment);
            Toast.makeText(MainActivity.this,"Administrator login successful!",Toast.LENGTH_LONG).show();
        }else if(message.equals(Constants.LOGS_LOGIN_RESPONSE_OK)){
            dialog.dismiss();
            Fragment fragment = new LogsFragment();
            changeFragment(fragment);
            Toast.makeText(MainActivity.this,"Logs login successful!",Toast.LENGTH_LONG).show();
        }else if(message.equals(Constants.ADMINISTRATOR_LOGIN_RESPONSE_FAILED) || message.equals(Constants.LOGS_LOGIN_RESPONSE_FAILED)){
            Toast.makeText(MainActivity.this,"Wrong username or password!",Toast.LENGTH_LONG).show();
        }else if(message.equals(Constants.CONNECTION_ESTABLISHED))
        {
            //Toast.makeText(getApplicationContext(),"Server is unavailable!",Toast.LENGTH_SHORT).show();
            Snackbar.make(this.findViewById(R.id.screen_area),"Connection established!",Snackbar.LENGTH_SHORT).show();
        }else if(message.equals(Constants.SERVER_DOWN))
        {
            //Toast.makeText(getApplicationContext(),"Server is unavailable!",Toast.LENGTH_SHORT).show();
            Snackbar.make(this.findViewById(R.id.screen_area),"Server is down!",Snackbar.LENGTH_INDEFINITE)
                    .setAction("Retry connection?", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            TcpClient.getInstance().start();
                        }
                    }).show();
        }
        else if(message.equals(Constants.CONNECTION_LOST))
        {
            //Toast.makeText(this,"Connection lost! Restart the app!",Toast.LENGTH_SHORT).show();
            Snackbar.make(this.findViewById(R.id.screen_area),"Something went wrong and the connection is lost.",Snackbar.LENGTH_INDEFINITE)
                    .setAction("Retry connection?", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            TcpClient.getInstance().start();
                        }
                    }).show();
        }
    }

    void changeFragment(Fragment fragment){
        if(fragment != null) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.screen_area, fragment).addToBackStack(null);
            ft.commit();
        }
    }

    public static User getCurrentUser(){
        return mUser;
    }
}
