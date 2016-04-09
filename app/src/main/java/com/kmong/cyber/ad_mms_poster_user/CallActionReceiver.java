package com.kmong.cyber.ad_mms_poster_user;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by JSpiner on 2015. 2. 19..
 */
public class CallActionReceiver extends BroadcastReceiver {
    public static boolean idleCheck = false;
    public static boolean ringCheck = false;
    public static boolean offCheck = false;
    public static String number = "";
    public Context context;
    public static final String TAG = "PHONE STATE";
    private static String mLastState;
    public DBController db;

    @Override
    public void onReceive(Context context1, Intent intent) {
        context = MMSApplication.context;

        db = new DBController(context);
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
        Log.i("number", number);
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
                        int callType = 2;
                        if (ringCheck) {
                            Log.d(TAG, "수신" + number);
                            callType = 0;

                        } else {
                            Log.d(TAG, "발신" + number);
                            callType = 1;

                        }
                        usercheck();
                    }

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

    public String phoneNumberLoad() { // MY PHONE NUMBER LOAD
        TelephonyManager tMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String mPhoneNumber = tMgr.getLine1Number();
        return mPhoneNumber;
    }

    public void usercheck() {
        String myPhoneNumber = phoneNumberLoad();
        String API_URL = "http://qwebmomo.cafe24.com/api/check_userable.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, API_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG,"response : "+response);
                        try {
                            JSONObject result = new JSONObject(response);
                            int code = result.getInt("code");
                            if (code == 1) {
                                if(db.CheckBlock(number) == 0){  //차단된 번호 검색해서 없으면 팝업창 뜨도록 만듬
                                    Intent i = new Intent(context, CallDialogActivity.class);
                                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    i.putExtra("number", "" + number);
                                    context.startActivity(i);
                                    Log.d(TAG,"started");
                                }
                                else{
                                    Log.d(TAG,"blocked");
                                    Toast.makeText(context,"차단된 전화번호입니다.",Toast.LENGTH_LONG).show();
                                }
                            }
                            else {
                                Toast.makeText(context,"비활성화된 계정입니다. 관리자에게 문의하세요.",Toast.LENGTH_LONG).show();

                            }
                        } catch (Exception e) {
                            Log.d(TAG,"response error : " +e.getMessage());
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Log.e(TAG,"VOLLY ERROR : "+error.getMessage());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("id", phoneNumberLoad());
                Log.d(TAG,"PARAMS : "+map);
                return map;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }


}

