package com.spawn.ai.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public final class DateTimeUtils {

    private DateTimeUtils() {

    }

    public static String getDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm");
        return simpleDateFormat.format(new Date().getTime());
    }
}
