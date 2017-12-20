package com.yzh.androidquickdevlib.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DateUtility {
    public final static String SIMPLE_WEEK_DAY_CN[] = new String[]{"日", "一", "二", "三", "四", "五", "六"};
    public final static String SIMPLE_WEEK_DAY_EN[] = new String[]{"Sun", "Mon", "Tues", "Wed", "Thur", "Fri", "Sat"};

    public final static String WEEK_DAY_CN[] = new String[]{"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
    public final static String WEEK_DAY_EN[] = new String[]{"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

    public final static String SIMPLE_MONTH_DAY_CN[] = new String[]{"1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月"};
    public final static String MONTH_DAY_CN[] = new String[]{"一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "十二月"};
    public final static String SIMPLE_MONTH_DAY_EN[] = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "June", "July", "Aug", "Sept", "Oct", "Nov", "Dec"};
    public final static String MONTH_DAY_EN[] = new String[]{"January", "February", "March", "April", "May", "June", "July", "Aguest", "September", "October", "November", "December"};

    public static String formatTimeAsServerFormat(Calendar time) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return df.format(time.getTime());
    }

    public static Calendar parseTimeFromServerFormat(String timeStr) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Calendar ret = Calendar.getInstance(Locale.getDefault());
        try {
            ret.setTime(df.parse(timeStr));
        }
        catch (Exception ignored) {
            // 不作处理, 默认返回当前日期
            ignored.printStackTrace();
        }
        return ret;
    }

    public static String getWeekDayString(Calendar time, Locale locale) {
        return getWeekDayString(time, locale, true);
    }

    public static String getWeekDayString(Calendar time, Locale locale, boolean isSimple) {
        if (locale == Locale.ENGLISH) {
            if (isSimple) {
                return SIMPLE_WEEK_DAY_EN[time.get(Calendar.DAY_OF_WEEK) - 1];
            }
            else {
                return WEEK_DAY_EN[time.get(Calendar.DAY_OF_WEEK) - 1];
            }
        }
        else {
            if (isSimple) {
                return SIMPLE_WEEK_DAY_CN[time.get(Calendar.DAY_OF_WEEK) - 1];
            }
            else {
                return WEEK_DAY_CN[time.get(Calendar.DAY_OF_WEEK) - 1];
            }
        }
    }

    public static String getMonthString(Calendar time, Locale locale) {
        return getMonthString(time, locale, true);
    }

    public static String getMonthString(Calendar time, Locale locale, boolean isSimple) {
        if (locale == Locale.ENGLISH) {
            if (isSimple) {
                return SIMPLE_MONTH_DAY_EN[time.get(Calendar.MONTH)];
            }
            else {
                return MONTH_DAY_EN[time.get(Calendar.MONTH)];
            }
        }
        else {
            if (isSimple) {
                return SIMPLE_MONTH_DAY_CN[time.get(Calendar.MONTH)];
            }
            else {
                return MONTH_DAY_CN[time.get(Calendar.MONTH)];
            }
        }
    }

    public static String getYearMonthDateString(Calendar time) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return df.format(time.getTime());
    }

    public static String getMonthDateString(Calendar time) {
        SimpleDateFormat df = new SimpleDateFormat("MM-dd", Locale.getDefault());
        return df.format(time.getTime());
    }

    public static String getHourMinueString(Calendar time) {
        SimpleDateFormat df = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return df.format(time.getTime());
    }

    public static String getYearMonthDateHourMinuteString(Calendar time) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        return df.format(time.getTime());
    }


    public static String getYearMonthDateDotSeperatorString(Calendar time) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd", Locale.getDefault());
        return df.format(time.getTime());
    }

    public static String getYearMonthDateWeekdayHourMinuteString(Calendar time) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd E HH:mm", Locale.getDefault());
        return df.format(time.getTime());
    }

    public static boolean isSameDay(String startDate, String endDate) {
        Calendar startCalendar = parseTimeFromServerFormat(startDate);
        Calendar endCalendar = parseTimeFromServerFormat(endDate);

        return isSameDay(startCalendar, endCalendar);
    }

    public static boolean isSameDay(Calendar start, Calendar end) {
        if (start.get(Calendar.DAY_OF_YEAR) == end.get(Calendar.DAY_OF_YEAR) && (start.get(Calendar.YEAR) == end.get(Calendar.YEAR))) {
            return true;
        }

        return false;
    }
}
