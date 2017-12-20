package com.yzh.androidquickdevlib.gui.views;

import android.text.TextUtils;
import android.widget.TextView;

import com.yzh.androidquickdevlib.preference.impls.PreferenceUtil;

import java.lang.ref.WeakReference;
import java.util.Calendar;

public class AuthCodeTimerHelper implements Runnable {
    private static final String START_TIME_KEY = "AuthCodeTimerHelper.time";
    private WeakReference<TextView> mTextViewRef = null;
    private String originalStr = "";
    private String mSuffixTextFormatter = "%d秒后重新发送";
    private long countDownTime = 1000 * 60;

    public AuthCodeTimerHelper(TextView textView, String formatter, long countDownInMiliseconds, boolean reset) {
        this.mTextViewRef = new WeakReference<>(textView);
        this.mSuffixTextFormatter = TextUtils.isEmpty(formatter) ? mSuffixTextFormatter : formatter;
        this.countDownTime = countDownInMiliseconds > 0 ? countDownInMiliseconds : this.countDownTime;

        if (reset || PreferenceUtil.getHelper()
                .getLong(START_TIME_KEY, -1) < 0) {
            final long now = Calendar.getInstance()
                    .getTimeInMillis();
            PreferenceUtil.getHelper()
                    .put(START_TIME_KEY, now);
        }

        if (textView != null) {
            this.originalStr = textView.getText()
                    .toString();
            textView.postDelayed(this, 1000);
        }
    }

    @Override
    public void run() {
        final long startTime = PreferenceUtil.getHelper()
                .getLong(START_TIME_KEY, 0);
        final long now = Calendar.getInstance()
                .getTimeInMillis();
        final long leftTime = now - startTime;

        TextView tv = mTextViewRef.get();
        if (tv != null) {
            if (leftTime > this.countDownTime) {
                tv.setEnabled(true);
                tv.setText(this.originalStr);
                PreferenceUtil.getHelper()
                        .del(START_TIME_KEY);
            }
            else {
                tv.setEnabled(false);
                tv.setText(String.format(mSuffixTextFormatter, (this.countDownTime - leftTime) / 1000));
                tv.postDelayed(this, 1000);
            }
        }
    }

}