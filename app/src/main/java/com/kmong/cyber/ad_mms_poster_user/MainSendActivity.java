package com.kmong.cyber.ad_mms_poster_user;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.kmong.cyber.ad_mms_poster_user.SendMMS3.SendMMSActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainSendActivity extends AppCompatActivity {
    @Bind(R.id.edit_number) EditText e_number;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_send);

        init();

    }

    void init(){
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_mainsend_ad_post)
    void ad_post(){
        String content = e_number.getText().toString();
        Intent i = new Intent(MainSendActivity.this, SendMMSActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Log.i("ADPOST",content);
        i.putExtra("number",content);
        startActivity(i);
        finish();
    }

}
