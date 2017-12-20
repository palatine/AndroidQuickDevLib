package com.yzh.androidquickdevlib.gui.views;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.yzh.androidquickdevlib.R;
import com.yzh.androidquickdevlib.utils.ResUtils;
import com.yzh.modaldialog.dialog.impl.BaseDialog;

public class BlockMessageDialog extends BaseDialog {
    protected String mMessage;
    protected String[] mButtonTexts = null;
    View mRootView = null;

    protected int mViewLayoutId = R.layout.dialog_message;

    public BlockMessageDialog(String message, String... buttonTexts) {
        this(null, message, buttonTexts);
    }

    public BlockMessageDialog(String message, boolean query) {
        this(null, message, query);
    }

    public BlockMessageDialog(Context parent, String message, String... buttonTexts) {
        super(parent, 0);
        this.mMessage = message;
        this.mButtonTexts = buttonTexts;
    }

    public BlockMessageDialog(Context parent, String message, boolean query) {
        super(parent, 0);
        this.mMessage = message;
        if (query == false) {
            this.mButtonTexts = new String[]{parent == null ? ResUtils.getString(R.string.confirm) : parent.getString(R.string.confirm)};
        }
    }

    public BlockMessageDialog(Context parent, int message, boolean query) {
        this(parent, parent == null ? ResUtils.getString(message) : parent.getString(message), query);
    }

    public BlockMessageDialog(Context parent, int strId, int[] buttonTextIds) {
        super(parent, 0);
        this.mMessage = ResUtils.getString(strId);
        if (buttonTextIds == null || buttonTextIds.length < 1) {
            return;
        }

        mButtonTexts = new String[3];
        for (int i = 0; i < buttonTextIds.length; i++) {
            if (buttonTextIds[i] == 0) {
                continue;
            }
            mButtonTexts[i] = ResUtils.getString(buttonTextIds[i]);
        }
    }

    @Override
    protected void onDialogCreate(Dialog dialog, Bundle bundle) {
        mRootView = LayoutInflater.from(mContext)
                .inflate(mViewLayoutId, null);
        TextView messageView = (TextView) mRootView.findViewById(R.id.msg_dlg_text_view);
        messageView.setText(mMessage);

        View.OnClickListener l = arg0 -> {
            int ret = AlertDialog.BUTTON_NEUTRAL;
            if (arg0.getId() == R.id.msg_dlg_positive_button) {
                ret = AlertDialog.BUTTON_POSITIVE;
            }
            else if (arg0.getId() == R.id.msg_dlg_negative_button) {
                ret = AlertDialog.BUTTON_NEGATIVE;
            }
            endModal(ret);
        };

        if (mButtonTexts == null) {
            Resources resources = this.mContext.getResources();
            mButtonTexts = new String[]{resources.getString(R.string.str_positive), null, resources.getString(R.string.str_negative)};
        }

        Button button = (Button) mRootView.findViewById(R.id.msg_dlg_positive_button);
        if (mButtonTexts.length > 0 && mButtonTexts[0] != null) {
            button.setText(mButtonTexts[0]);
            button.setVisibility(View.VISIBLE);
            button.setOnClickListener(l);
        }
        else {
            button.setVisibility(View.GONE);
        }

        button = (Button) mRootView.findViewById(R.id.msg_dlg_neutral_button);
        if (mButtonTexts.length > 1 && mButtonTexts[1] != null) {
            button.setText(mButtonTexts[1]);
            button.setVisibility(View.VISIBLE);
            button.setOnClickListener(l);
        }
        else {
            button.setVisibility(View.GONE);
        }


        button = (Button) mRootView.findViewById(R.id.msg_dlg_negative_button);
        if (mButtonTexts.length > 2 && mButtonTexts[2] != null) {
            button.setText(mButtonTexts[2]);
            button.setVisibility(View.VISIBLE);
            button.setOnClickListener(l);
        }
        else {
            button.setVisibility(View.GONE);
        }

        dialog.setContentView(mRootView);
    }
}
