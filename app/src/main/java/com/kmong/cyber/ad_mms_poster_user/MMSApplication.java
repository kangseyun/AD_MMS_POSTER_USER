package com.kmong.cyber.ad_mms_poster_user;

import android.app.Application;
import android.content.Context;

/**
 * Copyright 2016 JSpiner. All rights reserved.
 *
 * @author JSpiner (jspiner@naver.com)
 * @project AD_MMS_POSTER_USER
 * @since 2016. 4. 9.
 */
public class MMSApplication extends Application {

    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getBaseContext();
    }
}
