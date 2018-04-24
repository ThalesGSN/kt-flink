package br.com.dti.kt.core.common;

import br.com.dti.kt.core.Constants;
import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
public class DateUtils {

    private DateUtils() {

    }

    public static Date toDate(String dateStr, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        try {
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            String message = String.format("Invalid date: %s, format valid: yyyy-MM-dd", dateStr);
            log.error(message, e);
            throw new IllegalArgumentException(message);
        }
    }

    public static Date toDate(String dateStr) {
        return toDate(dateStr, Constants.DEFAULT_DATE_FORMAT);
    }
}
