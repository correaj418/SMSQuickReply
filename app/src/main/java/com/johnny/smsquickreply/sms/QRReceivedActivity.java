package com.johnny.smsquickreply.sms;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.johnny.smsquickreply.R;
import com.johnny.smsquickreply.constants.QRConstants;

public class QRReceivedActivity extends Activity
{
    @Override
    public void onCreate(Bundle arSavedInstanceState)
    {
        super.onCreate(arSavedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

        setContentView(R.layout.received_layout);

        init(getIntent().getExtras());
    }

    private void init(Bundle arBundle)
    {
        TextView loSenderTV = (TextView) findViewById(R.id.received_sender_tv);
        String loSender = arBundle.getString(QRConstants.SENDER);

        loSenderTV.setText(loSender);

        EditText loBodyTV = (EditText) findViewById(R.id.received_message_body_tv);
        String loBody = arBundle.getString(QRConstants.BODY);

        loBodyTV.setText(loBody);
    }
}
