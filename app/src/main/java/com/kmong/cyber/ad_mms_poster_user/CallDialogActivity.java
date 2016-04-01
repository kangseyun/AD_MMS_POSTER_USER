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

import com.kmong.cyber.ad_mms_poster_user.SendMMS3.SendMMSActivity;

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
    public  String number = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new DBController(this);
        Intent intent = getIntent();
        number = intent.getExtras().getString("number"); // 수신자 번호 가져오기

        new AlertDialog.Builder(CallDialogActivity.this)
                .setTitle("저장된 메세지를 전송하시겠습니까?")
                .setPositiveButton("전송", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //sendMMS(); 인텐트 형식의 방법 구현되어있음
                        Intent i = new Intent(CallDialogActivity.this, SendMMSActivity.class); // 라이브러리를 사용하여 보내는 방법 아직 테스트 안해봄 에뮬에선  APN문제때문에 테스트 불가능
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        i.putExtra("number", "" + number);
                        startActivity(i);
                        finish();
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                }).setNeutralButton("차단",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String query = String.format("insert into block values (null,'%s')", number);
                        //String query_img = String.format("insert into img values (null,'%s')", content);
                        try {
                            db.update(query);
                        } catch (Exception e) {
                            Log.i("ERROR", e.toString());
                        }
                    }
                })
                .show();
    }


}
