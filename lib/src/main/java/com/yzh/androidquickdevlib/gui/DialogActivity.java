package com.yzh.androidquickdevlib.gui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.yzh.androidquickdevlib.R;


public class DialogActivity extends Activity {
    public static final String MESSAGE_TEXT = "message_text";
    public static final String BUTTONS_TEXT = "buttons_text";

    protected int mContentViewResId = R.layout.dialog_message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTheme(android.R.style.Theme_Dialog);
        this.setContentView(mContentViewResId);

        if (this.getWindow() != null) {
            this.getWindow()
                    .setBackgroundDrawableResource(android.R.color.transparent);
        }

        TextView messageView = (TextView) this.findViewById(R.id.msg_dlg_text_view);
        messageView.setText(this.getIntent()
                .getStringExtra(DialogActivity.MESSAGE_TEXT));
        String buttonsText[] = this.getIntent()
                .getStringArrayExtra(DialogActivity.BUTTONS_TEXT);

        View.OnClickListener l = arg0 -> {
            int ret = AlertDialog.BUTTON_NEUTRAL;
            if (arg0.getId() == R.id.msg_dlg_positive_button) {
                ret = AlertDialog.BUTTON_POSITIVE;
            }
            else if (arg0.getId() == R.id.msg_dlg_negative_button) {
                ret = AlertDialog.BUTTON_NEGATIVE;
            }
            DialogActivity.this.setResult(ret);
            finish();
        };

        if (buttonsText == null) {
            Resources resources = this.getResources();
            buttonsText = new String[]{resources.getString(R.string.str_positive), null, resources.getString(R.string.str_negative)};
        }

        Button button = (Button) findViewById(R.id.msg_dlg_positive_button);
        if (buttonsText.length > 0 && buttonsText[0] != null) {
            button.setText(buttonsText[0]);
            button.setVisibility(View.VISIBLE);
            button.setOnClickListener(l);
        }
        else {
            button.setVisibility(View.GONE);
        }

        button = (Button) findViewById(R.id.msg_dlg_neutral_button);
        if (buttonsText.length > 1 && buttonsText[1] != null) {
            button.setText(buttonsText[1]);
            button.setVisibility(View.VISIBLE);
            button.setOnClickListener(l);
        }
        else {
            button.setVisibility(View.GONE);
        }

        button = (Button) findViewById(R.id.msg_dlg_negative_button);
        if (buttonsText.length > 2 && buttonsText[2] != null) {
            button.setText(buttonsText[2]);
            button.setVisibility(View.VISIBLE);
            button.setOnClickListener(l);
        }
        else {
            button.setVisibility(View.GONE);
        }
    }
}
