package com.spawn.ai.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;

import java.util.Locale;

public class AppUtils {

    private static AppUtils appUtils;

    public static AppUtils getInstance() {
        if (appUtils == null) {
            appUtils = new AppUtils();
        }

        return appUtils;
    }

    public static String getStringRes(int resourceId, Context context, String lang) {
        String result = "";

        Locale requestedLocale = new Locale(lang);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Configuration config = new Configuration(context.getResources().getConfiguration());
            config.setLocale(requestedLocale);
            result = context.createConfigurationContext(config).getText(resourceId).toString();
        }

        return result;
    }

}
