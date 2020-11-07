package com.spawn.ai.utils.task_utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeUtils {

    public DateTimeUtils() {
    }

    public String getDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm");
        return simpleDateFormat.format(new Date().getTime());
    }
}
