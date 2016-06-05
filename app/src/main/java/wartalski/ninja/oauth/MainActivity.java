package wartalski.ninja.oauth;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import wartalski.ninja.oauth.GpsHandler.GPSTracker;
import wartalski.ninja.oauth.service.Callback;
import wartalski.ninja.oauth.service.LoginService;
import wartalski.ninja.oauth.service.LoginServiceComponentFactory;
import wartalski.ninja.oauth.service.TrackerService;
import wartalski.ninja.oauth.service.TrackerServiceComponentFactory;
import wartalski.ninja.oauth.vo.CoordinatesVO;

public class MainActivity extends AppCompatActivity {

    private EditText login;
    private EditText password;
    private Button button;
    private LoginService loginService;
    private Toolbar toolbar;
    private EditText lat;
    private EditText lng;
    private Button startGpsButton;
    private Button stopGpsButton;
    private TrackerService trackerService;
    private boolean userLogIn;
    GPSTracker gps;

    @Override
    public void supportStartPostponedEnterTransition() {
        super.supportStartPostponedEnterTransition();
    }

    private Timer timer;
    private MyTimerTask myTimerTask = new MyTimerTask();
    private long periodicity = 10000;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        login = (EditText) findViewById(R.id.login);
        password = (EditText) findViewById(R.id.password);
        button = (Button) findViewById(R.id.submit);
        lat = (EditText) findViewById(R.id.lat);
        lng = (EditText) findViewById(R.id.lng);
        startGpsButton = (Button) findViewById(R.id.start_gps_button);
        stopGpsButton = (Button) findViewById(R.id.stop_gps_button);
        gps = new GPSTracker(MainActivity.this);
        userLogIn = false;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        loginService = new LoginService.Builder()
                .setFactory(new LoginServiceComponentFactory(this))
                .setCallback(new Callback() {
                    @Override
                    public void onSuccess() {
                        SharedPreferences sharedPreferences = getSharedPreferences("token_store", Context.MODE_PRIVATE);
                        Log.d("...", "access token " + sharedPreferences.getString("access_token", ""));
                        Log.d("...", "refresh token " + sharedPreferences.getString("refresh_token", ""));
                        Log.d("...", "user token " + sharedPreferences.getString("user", ""));
                        Log.d("...", "password token " + sharedPreferences.getString("password", ""));
                        Log.d("...", "user id " + sharedPreferences.getLong("user_id", -1));
                        toolbar.setTitle(sharedPreferences.getString("user_name", ""));
                        userLogIn = true;
                    }

                    @Override
                    public void onFailure() {
                        Log.d("...", "Failed");
                        userLogIn = false;
                        createAlert("Hey...","Incorrect login or password ;((( ");
                    }
                })
                .build();

        trackerService = new TrackerService.Builder()
                .setFactory(new TrackerServiceComponentFactory(this))
                .build();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String loginS = login.getText().toString();
                String passwordS = password.getText().toString();
                loginService.login(loginS, passwordS);
            }
        });

        startGpsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!userLogIn) {
                    createAlert("Hey...","You have to log in first!!");
                    return;
                }
                //start gps measurements
                if (gps.startGpsPosition()) {
                    Log.d("...", "try Timer started");
                    //re-schedule timer here
                    //otherwise, IllegalStateException of
                    //"TimerTask is scheduled already"
                    //will be thrown
                    startTimer();
                }

            }
        });
        stopGpsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gps.stopPosition();
                stopTimer();
            }
        });

}

    private void startTimer() {
        if (timer!=null){
            timer.cancel();
            timer = null;
        }
        timer = new Timer();
        myTimerTask = new MyTimerTask();
        timer.schedule(myTimerTask, 2000, getPeriodicity());
        Log.d("...", "Timer started");
    }

    private void stopTimer() {
        if (timer!=null){
            timer.cancel();
            timer = null;
        }
        Log.d("...", "Timer stopped");
    }

    public void createAlert(String title,String msg) {
        //Toast.makeText(MainActivity.this, "You are not logged in", Toast.LENGTH_LONG).show();
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);

        // Setting Dialog Title
        alertDialog.setTitle(title);

        // Setting Dialog Message
        alertDialog.setMessage(msg);

        // on pressing cancel button
        alertDialog.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {

            // Creating alert Dialog with one Button
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);

            // Setting Dialog Title
            alertDialog.setTitle("Configuration");

            // Setting Dialog Message
            alertDialog.setMessage("Enter periodicity in seconds");
            final EditText input = new EditText(MainActivity.this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            input.setLayoutParams(lp);
            alertDialog.setView(input); // uncomment this line

            // Setting Positive "Yes" Button
            alertDialog.setPositiveButton("submit",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            if (!android.text.TextUtils.isDigitsOnly(input.getText().toString())) {
                                Toast.makeText(MainActivity.this, "Wrong format!", Toast.LENGTH_LONG).show();
                            } else {
                                setPeriodicity(Long.parseLong(input.getText().toString()));
                                //restart timer if exist
                                if(timer != null ) startTimer();
                                Toast.makeText(MainActivity.this, "New value set.", Toast.LENGTH_LONG).show();
                            }
                            dialog.cancel();
                        }
                    });
            // Setting Negative "NO" Button
            alertDialog.setNegativeButton("cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Write your code here to execute after dialog
                            dialog.cancel();
                        }
                    });

            // closed

            // Showing Alert Message
            alertDialog.show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public long getPeriodicity() {
        return periodicity;
    }

    public void setPeriodicity(long periodicity) {
        this.periodicity = periodicity * 1000;
    }

    public class MyTimerTask extends TimerTask {

        @Override
        public void run() {
            Log.d("...", "Timer timeout... report to be send");
            Log.d("...", "gps.getLatitude() " + gps.getLatitude());
            Log.d("...", "gps.getLongitude()" + gps.getLongitude());
            Double latValue = gps.getLatitude();
            Double lngValue = gps.getLongitude();
            Date date = new Date();

            CoordinatesVO coordinatesVO = new CoordinatesVO();
            coordinatesVO.setLng(latValue);
            coordinatesVO.setLat(lngValue);
            coordinatesVO.setCreationDate(date);
            //Toast.makeText(MainActivity.this, "send!", Toast.LENGTH_LONG).show();

            trackerService.sendCoordinates(coordinatesVO, new Callback() {

                @Override
                public void onSuccess() {
                    Log.i("...", "sent !");
                }

                @Override
                public void onFailure() {
                    Log.i("...", "error");
                }
            });

        }
    }
}
