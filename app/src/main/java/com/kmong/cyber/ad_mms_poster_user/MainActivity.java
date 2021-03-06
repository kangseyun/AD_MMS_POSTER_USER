package com.kmong.cyber.ad_mms_poster_user;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends AppCompatActivity {
    @Bind(R.id.ButtonLoad) Button btn_load;
    @Bind(R.id.ButtonSave) Button btn_save;
    @Bind(R.id.edit_content) EditText e_content;
    public DBController db;
    public Context mcontext;
    public ArrayList<String> url = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new DBController(this);
        init();
        usercheck();
    }



    void init() {
        ButterKnife.bind(this);
        e_content.setText(db.PrintData());
    }


    public String phoneNumberLoad() { // MY PHONE NUMBER LOAD
        TelephonyManager tMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String mPhoneNumber = tMgr.getLine1Number();
        return mPhoneNumber;
    }

    Thread thread = new Thread(){
        @Override
        public void run() {
            try {
                Thread.sleep(3500); // As I am using LENGTH_LONG in Toast
                MainActivity.this.finish();
                System.exit(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public void usercheck() {
        String myPhoneNumber = phoneNumberLoad();
        String API_URL = "http://qwebmomo.cafe24.com/api/check_userable.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, API_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject result = new JSONObject(response);
                            int code = result.getInt("code");
                            if (code != 1) {
                                Toast.makeText(MainActivity.this, "비활성화된 계정입니다. 관리자에게 문의하세요.", Toast.LENGTH_LONG).show();
                                thread.start();
                            }
                        } catch (Exception e) {
                            Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                            thread.start();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                        thread.start();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("id", phoneNumberLoad());
                return map;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @OnClick(R.id.ButtonSave)
    void save() {
        String content = e_content.getText().toString();
        String query = String.format("insert into content values (null,'%s')", content);
        //String query_img = String.format("insert into img values (null,'%s')", content);
        try {
            db.update(query);
            db.PrintData(); //log
        } catch (Exception e) {
            Log.i("ERROR", e.toString());
        }

        Toast.makeText(MainActivity.this, "SAVE Complete", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.ButtonLoad)
    void imgLoad() {
        Intent i = new Intent(MainActivity.this, ImageActivity.class);
        startActivity(i);
        finish();
    }

    @OnClick(R.id.sendMMS)
    void MainSend(){
        Intent i = new Intent(MainActivity.this, MainSendActivity.class);
        startActivity(i);
    }

}
