package com.kmong.cyber.ad_mms_poster_user;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Copyright 2016 JSpiner. All rights reserved.
 *
 * @author JSpiner (jspiner@naver.com)
 * @project Allio
 * @since 2016. 1. 31.
 */
public class CallDialogActivity extends AppCompatActivity {
    public DBController db;
    public ArrayList<String> url = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new DBController(this);

        new AlertDialog.Builder(CallDialogActivity.this)
                .setTitle("Allio 메세지를 전송하시겠습니까?")
                .setPositiveButton("전송", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        sendMMS();
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

    private void sendMMS() {
        url =  db.PrintData2(); //저장 텍스트 가져오기
        Intent intent = getIntent();
        String number = intent.getExtras().getString("number"); // 수신자 번호 가져오기

        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.putExtra("address",number);
        //sendIntent.putExtra("subject", "");
        sendIntent.putExtra("sms_body", db.PrintData()); //
        sendIntent.setType("image/*");

        for(int i=0;i<url.size();i++) {
            sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(url.get(i)))); // 이미지 저장 주소 로드해서 이미지 불러오게 하기
        }
        startActivity(Intent.createChooser(sendIntent, "send"));


    }
}
