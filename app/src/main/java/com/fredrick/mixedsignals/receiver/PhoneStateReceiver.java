package com.fredrick.mixedsignals.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.android.internal.telephony.ITelephony;
import com.fredrick.mixedsignals.MainActivity;
import com.fredrick.mixedsignals.model.BlockItem;

import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * Created by Administrator on 4/23/2017.
 */

public class PhoneStateReceiver extends BroadcastReceiver {
    public static String TAG="PhoneStateReceiver";
    public ArrayList<BlockItem> checkParam = new ArrayList<BlockItem>();
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.PHONE_STATE")) {
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            Log.d(TAG,"PhoneStateReceiver**Call State=" + state);

            if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                Log.d(TAG,"PhoneStateReceiver**Idle");
            } else if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                // Incoming call
                String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                String smsNumber = incomingNumber;
                incomingNumber = buildPhone(incomingNumber);
                String comparingNumber, comparingSMS;
                Log.d(TAG,"PhoneStateReceiver**Incoming call " + incomingNumber);

                if (MainActivity._instance.blockParam != null && MainActivity._instance.blockParam.size() > 0) {
                    checkParam = MainActivity._instance.blockParam;
                    for (int i = 0; i < checkParam.size(); i++) {
                        comparingNumber = buildPhone(checkParam.get(i).getPhone());
                        if (incomingNumber.contains(comparingNumber)) {
                            if (checkParam.get(i).getChecked()) {
                                comparingSMS = checkParam.get(i).getSMS();
                                if (!killCall(context)) { // Using the method defined earlier
                                    Log.d(TAG,"PhoneStateReceiver **Unable to kill incoming call");
                                    break;
                                } else {
                                    sendSMS(smsNumber, comparingSMS);
                                }
                            }
                            return;
                        }

                        if (comparingNumber.contains(incomingNumber)) {
                            if (checkParam.get(i).getChecked()) {
                                comparingSMS = checkParam.get(i).getSMS();
                                if (!killCall(context)) { // Using the method defined earlier
                                    Log.d(TAG,"PhoneStateReceiver **Unable to kill incoming call");
                                    break;
                                } else {
                                    sendSMS(smsNumber, comparingSMS);
                                }
                            }
                            return;
                        }
                    }
                }
            } else if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                Log.d(TAG,"PhoneStateReceiver **Offhook");
            }
        } else if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {
            // Outgoing call
            String outgoingNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            Log.d(TAG,"PhoneStateReceiver **Outgoing call " + outgoingNumber);

            setResultData(null); // Kills the outgoing call

        } else {
            Log.d(TAG,"PhoneStateReceiver **unexpected intent.action=" + intent.getAction());
        }
    }

    public boolean killCall(Context context) {
        try {
            // Get the boring old TelephonyManager
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

            // Get the getITelephony() method
            Class classTelephony = Class.forName(telephonyManager.getClass().getName());
            Method methodGetITelephony = classTelephony.getDeclaredMethod("getITelephony");

            // Ignore that the method is supposed to be private
            methodGetITelephony.setAccessible(true);

            com.android.internal.telephony.ITelephony telephonyService = (ITelephony) methodGetITelephony.invoke(telephonyManager);
            telephonyService.silenceRinger();
            telephonyService.endCall();
        } catch (Exception ex) { // Many things can go wrong with reflection calls
            Log.d(TAG,"PhoneStateReceiver **" + ex.toString());
            return false;
        }
        return true;
    }

    private void sendSMS(String phoneNumber, String message) {

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null,message, null, null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private String buildPhone(String strPhone) {
        String strRes = strPhone.replaceAll("[-+.^:,]","");;
        strRes = strRes.replaceAll("\\s+", "").trim();
        return strRes;
    }
}
