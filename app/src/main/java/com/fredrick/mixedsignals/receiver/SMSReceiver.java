package com.fredrick.mixedsignals.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.fredrick.mixedsignals.MainActivity;
import com.fredrick.mixedsignals.model.BlockItem;

import java.util.ArrayList;

/**
 * Created by Administrator on 4/26/2017.
 */

public class SMSReceiver extends BroadcastReceiver {
    public ArrayList<BlockItem> checkParam = new ArrayList<BlockItem>();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){
            Bundle bundle = intent.getExtras();
            Object messages[] = (Object[]) bundle.get("pdus");
            SmsMessage smsMessage[] = new SmsMessage[messages.length];

            for (int n = 0; n < messages.length; n++) {
                smsMessage[n] = SmsMessage.createFromPdu((byte[]) messages[n]);
            }

            String incomingNumber = smsMessage[0].getOriginatingAddress();
            String smsNumber = incomingNumber;
            incomingNumber = buildPhone(incomingNumber);
            final String messageSms = smsMessage[0].getDisplayMessageBody();
            long dateTimeSms = smsMessage[0].getTimestampMillis();

            String comparingNumber, comparingSMS;
            Uri deleteUri = Uri.parse("content://sms");

            //block sms if number is matched to our blocking number
            if (MainActivity._instance.blockParam != null && MainActivity._instance.blockParam.size() > 0) {
                checkParam = MainActivity._instance.blockParam;
                for (int i = 0; i < checkParam.size(); i++) {
                    comparingNumber = buildPhone(checkParam.get(i).getPhone());
                    if (incomingNumber.contains(comparingNumber)) {
                        if (checkParam.get(i).getChecked()) {
                            comparingSMS = checkParam.get(i).getSMS();
                            abortBroadcast();
                            deleteSMS(context, messageSms, smsNumber);
                            sendSMS(smsNumber, comparingSMS);
                        }
                        return;
                    }

                    if (comparingNumber.contains(incomingNumber)) {
                        if (checkParam.get(i).getChecked()) {
                            comparingSMS = checkParam.get(i).getSMS();
                            abortBroadcast();
                            deleteSMS(context, messageSms, smsNumber);
                            sendSMS(smsNumber, comparingSMS);
                        }
                        return;
                    }
                }
            }
        }
    }

    private void sendSMS(String phoneNumber, String message) {

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null,message, null, null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void deleteSMS(Context context, String message, String number) {
        try {
            Uri uriSms = Uri.parse("content://sms/inbox");
            Cursor c = context.getContentResolver().query(uriSms, new String[] { "_id", "thread_id", "address", "person", "date", "body" }, null, null, null);
            if (c != null && c.moveToFirst()) {
                do {
                    long id = c.getLong(0);
                    long threadId = c.getLong(1);
                    String address = c.getString(2);
                    String body = c.getString(5);

                    if (message.equals(body) && address.equals(number)) {
                        int iRes = context.getContentResolver().delete(Uri.parse("content://sms/" + id), null, null);
                        int iCon = context.getContentResolver().delete(Uri.parse("content://sms/conversations/" + threadId), null, null);
                        String uri = "content://sms/" + id;
                        int iSucc = context.getContentResolver().delete(Uri.parse(uri), null, null);
                    }
                } while (c.moveToNext());
            }
        } catch (Exception e) {
           e.printStackTrace();
        }
    }

    private String buildPhone(String strPhone) {
        String strRes = strPhone.replaceAll("[-+.^:,]","");;
        strRes = strRes.replaceAll("\\s+", "").trim();
        return strRes;
    }
}
