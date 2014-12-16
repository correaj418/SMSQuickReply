package com.johnny.smsquickreply.sms;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.johnny.smsquickreply.R;
import com.johnny.smsquickreply.constants.QRConstants;

public class QRReceivedActivity extends Activity implements OnTouchListener
{
    private TextView obSenderTextView;
    private TextView obBodyTextView;

    private EditText obResponseEditText;

    private Button obCloseButton;
    private Button obLaterButton;
    private Button obCallButton;
    private Button obOpenButton;
    private Button obReplyButton;

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
        obSenderTextView = (TextView) findViewById(R.id.received_sender_tv);
        String loSender = arBundle.getString(QRConstants.SENDER);

        obSenderTextView.setText(loSender);

        obBodyTextView = (TextView) findViewById(R.id.received_message_body_tv);
        String loBody = arBundle.getString(QRConstants.BODY);

        obBodyTextView.setText(loBody);

        obBodyTextView.setOnTouchListener(this);

        obResponseEditText = (EditText) findViewById(R.id.response_body_et);

        obReplyButton = (Button) findViewById(R.id.received_reply_button);
        obReplyButton.setVisibility(View.GONE);
    }

    @Override
    public boolean onTouch(View arView, MotionEvent arMotionEvent)
    {
        if (arView.getId() == R.id.received_message_body_tv)
        {
            bodyTouched();
        }

        return false;
    }

    private void bodyTouched()
    {
        obResponseEditText.setVisibility(View.VISIBLE);
        obResponseEditText.requestFocus();

        obReplyButton.setVisibility(View.VISIBLE);

        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
    }

    public void closeClicked(View view)
    {
        finish();
    }

    public void callClicked(View view)
    {
        Intent loPhoneCallIntent = new Intent(Intent.ACTION_CALL);

        loPhoneCallIntent.setData(Uri.parse("tel:" + obSenderTextView.getText().toString()));
        startActivity(loPhoneCallIntent);

        finish();
    }

    public void laterClicked(View view)
    {
        finish();
    }

    public void openClicked(View view)
    {
    }

    public void replyClicked(View view)
    {
        SmsManager loSmsManager = SmsManager.getDefault();

        loSmsManager.sendTextMessage(
                obSenderTextView.getText().toString(), null, obResponseEditText.getText().toString(), null, null);

        finish();
    }

    @Override
    public void finish()
    {
        markMessageRead(obSenderTextView.getText().toString(), obBodyTextView.getText().toString());

        super.finish();
    }

    private void markMessageRead(String arNumber, String arBody)
    {
        Uri loUri = Uri.parse("content://sms/inbox");
        Cursor loCursor = getContentResolver().query(loUri, null, null, null, null);

        try
        {
            while (loCursor.moveToNext())
            {
                if (loCursor.getString(loCursor.getColumnIndex("address")).equals(arNumber) &&
                        loCursor.getInt(loCursor.getColumnIndex("read")) == 0)
                {
                    if (loCursor.getString(loCursor.getColumnIndex("body")).startsWith(arBody))
                    {
                        String loSmsMessageID = loCursor.getString(loCursor.getColumnIndex("_id"));

                        ContentValues loValues = new ContentValues();
                        loValues.put("read", true);

                        getContentResolver().update(Uri.parse("content://sms/inbox"), loValues, "_id=" + loSmsMessageID, null);

                        loCursor.close();

                        return;
                    }
                }
            }

            loCursor.close();
        }
        catch (Exception e)
        {
            Log.e("Mark Read", "Error in Read: " + e.toString());
        }
    }
}
