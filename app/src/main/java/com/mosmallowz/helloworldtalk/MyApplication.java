package com.mosmallowz.helloworldtalk;

import android.app.Application;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by LeoMossi on 11/23/2017.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initFont();

    }
    private void initFont() {
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Athiti-Medium.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }

}
