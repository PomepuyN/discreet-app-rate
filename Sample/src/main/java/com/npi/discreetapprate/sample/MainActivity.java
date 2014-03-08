package com.npi.discreetapprate.sample;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
    private Button buttonLaunch;
    private Button buttonReset;
    private Button buttonForce;
    private EditText delay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        manageViews();

        settings = getSharedPreferences(PreferencesConstants.PREFS_NAME, 0);
        editor = settings.edit();

        updateValueDisplay();


        buttonForce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getAppRate().forceShow();
                updateValueDisplay();
            }
        });

        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getAppRate().reset();
                updateValueDisplay();
            }
        });

        buttonLaunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getAppRate().checkAndShow();
                updateValueDisplay();
            }
        });

    }

    private void manageViews() {
        buttonBar = (LinearLayout) findViewById(R.id.button_bar);

        buttonForce = (Button) findViewById(R.id.button_force);
        buttonReset = (Button) findViewById(R.id.button_reset);
        buttonLaunch = (Button) findViewById(R.id.button_launch);
        currentCount = (TextView) findViewById(R.id.curent_count);
        hasBeenClicked = (TextView) findViewById(R.id.has_been_clicked);
        isElpased = (TextView) findViewById(R.id.is_elapsed);
        initialCount = (EditText) findViewById(R.id.initial_count);
        text = (EditText) findViewById(R.id.text);
        delay = (EditText) findViewById(R.id.delay);
        retryPolicy = (Spinner) findViewById(R.id.retry_policy);


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.retry_policies, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        retryPolicy.setAdapter(adapter);

    }


    private void updateValueDisplay() {
        currentCount.setText(String.valueOf(settings.getInt(PreferencesConstants.KEY_COUNT, 0)));
        hasBeenClicked.setText(String.valueOf(settings.getBoolean(PreferencesConstants.KEY_CLICKED, false)));
        isElpased.setText(String.valueOf(settings.getBoolean(PreferencesConstants.KEY_ELAPSED_TIME, false)));
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
                .delay(Integer.valueOf(delay.getText().toString()))
                .listener(new AppRate.OnShowListener() {
                    @Override
                    public void onRateAppShowing() {
                        // View is showing => hide the buttons
                        hideButtonBar();
                    }

                    @Override
                    public void onRateAppDismissed() {
                        // User clicked the cross
                        showButtonBar();
                        Toast.makeText(MainActivity.this, "User has dismissed.", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onRateAppClicked() {
                        // User launched the app rating
                        showButtonBar();
                        Toast.makeText(MainActivity.this, "User launched the Play Store.", Toast.LENGTH_LONG).show();
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
