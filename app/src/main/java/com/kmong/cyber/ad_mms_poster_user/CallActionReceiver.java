package com.kmong.cyber.ad_mms_poster_user;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by JSpiner on 2015. 2. 19..
 */
public class CallActionReceiver extends BroadcastReceiver {
    public static boolean idleCheck = false;
    public static boolean ringCheck = false;
    public static boolean offCheck = false;
    public static String number = "";
    public Context context;
    String TAG = "Call State catch";

    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;

        //onoff
//        if (!Util.sharedPreferences.getBoolean("sms_onoff", false)) return;


        //발신전화번호
        String tmp_num = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
        if (tmp_num != null)
            if (!tmp_num.equals("")) number = tmp_num;


        //수신전화번호 감지를 윈한 리스너
        TelephonyManager telManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        telManager.listen(new PhoneStateListener() {
            public void onCallStateChanged(int state, String incomingNumber) {
                //수신
                if (incomingNumber != null)
                    if (!incomingNumber.equals("")) number = incomingNumber;
            }
        }, PhoneStateListener.LISTEN_CALL_STATE);

        /*
        발신 : IDLE -> OFFHOOK -> IDLE
        수신 : IDLE -> RINGING -> OFFHOOK -> IDLE
         */
        if (intent.getAction().equals("android.intent.action.PHONE_STATE")) {
            Log.d(TAG, intent.getStringExtra(TelephonyManager.EXTRA_STATE));
            switch (intent.getStringExtra(TelephonyManager.EXTRA_STATE)) {
                case "IDLE":
                    Log.d(TAG, "CALL_STATE_IDLE ");

                    if (idleCheck && offCheck) {

                        /*
                        수신 0
                        발신 1
                        둘다 2
                        가 되서 꼬이는 문제 생겼는데 일단
                        동작은 문제없고 코드만 더러워지
                        기획서 대로 그대로
                        일단짜놓고 나중에 수정

                        TODO - 팝업만들기
                         */
                        int callType = 2;
                        if (ringCheck) {
                            Log.d(TAG, "수신" + number);
                            callType = 0;

                        } else {
                            Log.d(TAG, "발신" + number);
                            callType = 1;

                        }

                        idleCheck = false;
                        ringCheck = false;
                        offCheck = false;
                    } else {
                        idleCheck = true;
                        ringCheck = false;
                        offCheck = false;
                    }
                    break;

                case "OFFHOOK":
                    Log.d(TAG, "CALL_STATE_OFFHOOK");
                /* 가끔 idle 없이 offhook으로 넘어가는 현상 방지용*/
                    idleCheck = true;
                    offCheck = true;
                    break;

                case "RINGING":
                    Log.d(TAG, "CALL_STATE_RINGING ");
                    ringCheck = true;
                    break;

                default:
                    Log.d(TAG, "default");
                    break;
            }
        }

    }




    private void sendSMS(String phoneNumber, String message) {
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, null, null);
    }


}
