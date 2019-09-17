package com.example.application;


import android.util.Log;

import java.util.Date;

public class TimeString {
    public static final int SEC = 60;
    public static final int MIN = 60;
    public static final int HOUR = 24;
    public static final int DAY = 30;
    public static final int MONTH = 12;

    public static String formatTimeString(Date date) {


        long curTime = System.currentTimeMillis();
        Log.d("formatTimeString", "formatTimeString: curTime : " + curTime);
        long regTime = date.getTime();
        Log.d("formatTimeString", "formatTimeString: curTime : " + regTime);
        long diffTime = (curTime - regTime) / 1000;
        Log.d("formatTimeString", "formatTimeString: diffTime : " + diffTime);
        String msg = null;

        if (diffTime < TimeString.SEC) {
            msg = diffTime + "초 전";
        } else if ((diffTime /= TimeString.SEC) < TimeString.MIN) {
            msg = diffTime + "분전";
        } else if ((diffTime /= TimeString.MIN) < TimeString.HOUR) {
            msg = (diffTime) + "시간전";
        } else if ((diffTime /= TimeString.HOUR) < TimeString.DAY) {
            msg = (diffTime) + "일전";
        } else if ((diffTime /= TimeString.DAY) < TimeString.MONTH) {
            msg = (diffTime) + "달전";
        } else {
            msg = (diffTime) + "년전";
        }
        return msg;
    }
}