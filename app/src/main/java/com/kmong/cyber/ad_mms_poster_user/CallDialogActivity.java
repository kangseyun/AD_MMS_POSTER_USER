package com.kmong.cyber.ad_mms_poster_user;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;

import java.util.List;

/**
 * Copyright 2016 JSpiner. All rights reserved.
 *
 * @author JSpiner (jspiner@naver.com)
 * @project Allio
 * @since 2016. 1. 31.
 */
public class CallDialogActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        new AlertDialog.Builder(CallDialogActivity.this)
                .setTitle("Allio 메세지를 전송하시겠습니까?")
                .setPositiveButton("전송", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                })
                .show();
    }


    String getText(){
        return "tmp";
       // db read
    }

    private void sendMMS(String phoneNumber, String subject) {
        Intent it = new Intent(Intent.ACTION_SEND);
        it.putExtra("sms_body", "내용");
        it.putExtra("subject", "Test");
        it.putExtra("sms_body", "Body");
        startActivity(it);
    }
}
