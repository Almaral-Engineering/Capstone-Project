package com.almareng.earthquakemonitor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils {
    public static String getFormattedDateTime(final String timestamp, final String format) {
        final Long unixSeconds = Long.parseLong(timestamp);
        final Date date = new Date(unixSeconds);
        final SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.getDefault());

        return dateFormat.format(date);
    }
}
