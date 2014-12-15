package com.johnny.smsquickreply.sms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.telephony.SmsMessage;
import android.util.Log;

import com.johnny.smsquickreply.constants.QRConstants;

public class QRSMSReceiver extends BroadcastReceiver
{
    private static final String TAG = QRSMSReceiver.class.getSimpleName();

    public QRSMSReceiver()
    {
        Log.i(TAG, TAG + " created");
    }

    @Override
    public void onReceive(Context arContext, Intent arIntent)
    {
        Bundle loSMSExtras = arIntent.getExtras();

        if (loSMSExtras == null)
        {
            return;
        }

        Object[] loPDUs = (Object[]) loSMSExtras.get("pdus");

        for (Object lpMessage : loPDUs)
        {
            SmsMessage loSmsMessage = SmsMessage.createFromPdu((byte[]) lpMessage);
            Log.v(TAG, loSmsMessage.getMessageBody());

            Intent loIntent = new Intent();

            loIntent.setClass(arContext, QRReceivedActivity.class);
            loIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            loIntent.putExtra(QRConstants.SENDER, getSender(loSmsMessage.getOriginatingAddress(), arContext));
            loIntent.putExtra(QRConstants.BODY, loSmsMessage.getMessageBody());

            arContext.startActivity(loIntent);
        }
    }

    private String getSender(String arAddress, Context arContext)
    {
        String rvSender = null;

        Uri loSenderUri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(arAddress));
        String[] loProjection = new String[] { ContactsContract.PhoneLookup.DISPLAY_NAME };

        Cursor loCursor = arContext.getContentResolver()
                .query(loSenderUri, loProjection, null, null, null);

        if (loCursor != null)
        {
            if (loCursor.moveToFirst())
            {
                rvSender = loCursor.getString(0);
            }

            loCursor.close();
        }

        if (rvSender == null)
        {
            // the phone number is not in the user's contacts list
            rvSender = arAddress;
        }

        return rvSender;
    }
}
