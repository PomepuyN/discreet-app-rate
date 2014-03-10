package com.npi.discreetapprate.sample;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import de.keyboardsurfer.android.widget.crouton.Configuration;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import fr.nicolaspomepuy.discreetapprate.AppRate;
import fr.nicolaspomepuy.discreetapprate.RetryPolicy;

public class MainActivity extends ActionBarActivity {

    private LinearLayout buttonBar;
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;
    private TextView currentCount;
    private TextView hasBeenClicked;
    private TextView isElpased;
    private EditText initialCount;
    private EditText text;
    private Spinner retryPolicy;
    private Button okButton;
    private EditText delay;
    private TextView lastCrash;
    private EditText installTime;
    private EditText pauseAfterCrash;
    private Spinner actionSpinner;
    private CheckBox onTop;
    private TextView monitoredTime;
    private EditText minimumMonitoringTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        manageViews();

        settings = getSharedPreferences(PreferencesConstants.PREFS_NAME, 0);
        editor = settings.edit();

        updateValueDisplay();


        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                switch (actionSpinner.getSelectedItemPosition()) {
                    case 0:
                        getAppRate().checkAndShow();
                        break;
                    case 1:
                        getAppRate().reset();
                        break;
                    case 2:
                        getAppRate().forceShow();
                        break;
                    case 3:
                        getAppRate().neverShowAgain();
                        break;
                    case 4:
                        editor.putLong(PreferencesConstants.KEY_LAST_CRASH, System.currentTimeMillis());
                        editor.commit();
                        Toast.makeText(MainActivity.this, "App has crashed just now", Toast.LENGTH_LONG).show();
                        break;
                }

                updateValueDisplay();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppRate.with(this).debug(true).endMonitoring();
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppRate.with(this).debug(true).startMonitoring();
    }

    private void manageViews() {
        buttonBar = (LinearLayout) findViewById(R.id.button_bar);

        okButton = (Button) findViewById(R.id.ok_button);
        currentCount = (TextView) findViewById(R.id.curent_count);
        hasBeenClicked = (TextView) findViewById(R.id.has_been_clicked);
        isElpased = (TextView) findViewById(R.id.is_elapsed);
        lastCrash = (TextView) findViewById(R.id.last_crash);
        monitoredTime = (TextView) findViewById(R.id.monitored_time);
        initialCount = (EditText) findViewById(R.id.initial_count);
        text = (EditText) findViewById(R.id.text);
        delay = (EditText) findViewById(R.id.delay);
        retryPolicy = (Spinner) findViewById(R.id.retry_policy);
        actionSpinner = (Spinner) findViewById(R.id.action_chooser);
        installTime = (EditText) findViewById(R.id.install_time);
        pauseAfterCrash = (EditText) findViewById(R.id.pause_after_crash);
        minimumMonitoringTime = (EditText) findViewById(R.id.minimum_monitoring_time);
        onTop = (CheckBox) findViewById(R.id.on_top);


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.retry_policies, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        retryPolicy.setAdapter(adapter);

        ArrayAdapter<CharSequence> actionAdapter = ArrayAdapter.createFromResource(this,
                R.array.actions, android.R.layout.simple_spinner_item);
        actionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        actionSpinner.setAdapter(actionAdapter);

    }


    private void updateValueDisplay() {
        currentCount.setText(String.valueOf(settings.getInt(PreferencesConstants.KEY_COUNT, 0)));
        hasBeenClicked.setText(String.valueOf(settings.getBoolean(PreferencesConstants.KEY_CLICKED, false)));
        isElpased.setText(String.valueOf(settings.getBoolean(PreferencesConstants.KEY_ELAPSED_TIME, false)));
        monitoredTime.setText(String.valueOf(settings.getLong(PreferencesConstants.KEY_MONITOR_TOTAL, 0L) / 1000) + " seconds");
        long lastCrashTime = settings.getLong(PreferencesConstants.KEY_LAST_CRASH, 0L);
        if (lastCrashTime == 0L) {
            lastCrash.setText("Never");
        } else {
            lastCrash.setText(String.valueOf((System.currentTimeMillis() - lastCrashTime) / 1000) + " seconds ago");
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Process process = Runtime.getRuntime().exec("logcat -d");
                    BufferedReader bufferedReader = new BufferedReader(
                            new InputStreamReader(process.getInputStream()));

                    final StringBuilder log = new StringBuilder();
                    String line = "";
                    while ((line = bufferedReader.readLine()) != null) {
                        if (line.contains("DicreetAppRate")) {
                            int index = line.indexOf(":");

                            if (!TextUtils.isEmpty(log.toString())) log.append("\n");
                            log.append(line.substring(index + 1));

                        }
                    }


                    final Style style = new Style.Builder(Style.INFO).setConfiguration(new Configuration.Builder()
                            .setDuration(Configuration.DURATION_LONG)
                            .build()).build();

                    runOnUiThread(new Runnable() {
                        public void run() {
                            if (!TextUtils.isEmpty(log.toString())) {
                                Crouton.clearCroutonsForActivity(MainActivity.this);
                                Crouton.makeText(MainActivity.this, log.toString(), style).show();

                            }
                        }
                    });
                    Runtime.getRuntime().exec("logcat -c");

                } catch (IOException e) {
                }
            }
        }).start();
    }

    private AppRate getAppRate() {
        RetryPolicy policy = RetryPolicy.EXPONENTIAL;
        switch (retryPolicy.getSelectedItemPosition()) {
            case 1:
                policy = RetryPolicy.INCREMENTAL;
                break;
            case 2:
                policy = RetryPolicy.NONE;
                break;
        }
        return AppRate.with(this)
                .initialLaunchCount(Integer.valueOf(initialCount.getText().toString()))
                .text(text.getText().toString())
                .retryPolicy(policy)
                .debug(true)
                .fromTop(onTop.isChecked())
                .pauseTimeAfterCrash(Integer.valueOf(pauseAfterCrash.getText().toString()))
                .atLeastInstalledSince(Integer.valueOf(installTime.getText().toString()))
                .delay(Integer.valueOf(delay.getText().toString()))
                .minimumMonitoringTime(Integer.valueOf(minimumMonitoringTime.getText().toString()))
                .listener(new AppRate.OnShowListener() {
                    @Override
                    public void onRateAppShowing() {
                        if (!onTop.isChecked()) {
                            // View is showing => hide the buttons
                            hideButtonBar();
                        }
                    }

                    @Override
                    public void onRateAppDismissed() {
                        if (!onTop.isChecked()) {
                            // User clicked the cross
                            showButtonBar();
                            Toast.makeText(MainActivity.this, "User has dismissed.", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onRateAppClicked() {
                        if (!onTop.isChecked()) {
                            // User launched the app rating
                            showButtonBar();
                            Toast.makeText(MainActivity.this, "User launched the Play Store.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void showButtonBar() {
        Animation showAnimation = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        buttonBar.startAnimation(showAnimation);
        showAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                buttonBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void hideButtonBar() {
        Animation hideAnimation = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
        buttonBar.startAnimation(hideAnimation);
        hideAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                buttonBar.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/PomepuyN/discreet-app-rate")));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
