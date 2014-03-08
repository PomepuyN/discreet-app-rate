package com.npi.discreetapprate.sample;

import android.app.Application;

import fr.nicolaspomepuy.discreetapprate.AppRate;

/**
 * Created by nicolas on 08/03/14.
 */
public class SampleApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AppRate.initExceptionHandler(this);
    }
}
