package com.kmong.cyber.ad_mms_poster_user.SendMMS3;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.provider.MediaStore.Images.Media;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.kmong.cyber.ad_mms_poster_user.DBController;
import com.kmong.cyber.ad_mms_poster_user.R;
import com.kmong.cyber.ad_mms_poster_user.SendMMS3.APNHelper.APN;
import com.kmong.cyber.ad_mms_poster_user.nokia.IMMConstants;
import com.kmong.cyber.ad_mms_poster_user.nokia.MMContent;
import com.kmong.cyber.ad_mms_poster_user.nokia.MMEncoder;
import com.kmong.cyber.ad_mms_poster_user.nokia.MMMessage;
import com.kmong.cyber.ad_mms_poster_user.nokia.MMResponse;
import com.kmong.cyber.ad_mms_poster_user.nokia.MMSender;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

public class SendMMSActivity extends Activity {
    ProgressDialog mProgressDialog;
    private DBController db;
    private static final String TAG = "SendMMSActivity";
    private ConnectivityManager mConnMgr;
    private PowerManager.WakeLock mWakeLock;
    private ConnectivityBroadcastReceiver mReceiver;

    private String number =null;
    public ArrayList<String> url = new ArrayList<String>();
    private NetworkInfo mNetworkInfo;
    private NetworkInfo mOtherNetworkInfo;

    public enum State {
        UNKNOWN,
        CONNECTED,
        NOT_CONNECTED
    }

    private State mState;
    private boolean mListening;
    private boolean mSending;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        db = new DBController(this);
        Intent intent = getIntent();
        number = intent.getExtras().getString("number"); // 수신자 번호 가져오기
        Log.i("from number",number);
        mListening = true;
        mSending = false;
        mConnMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        mReceiver = new ConnectivityBroadcastReceiver();

        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mReceiver, filter);

        try {

            // Ask to start the connection to the APN. Pulled from Android source code.
            int result = beginMmsConnectivity();

            if (result != PhoneEx.APN_ALREADY_ACTIVE) {
                Log.v(TAG, "Extending MMS connectivity returned " + result + " instead of APN_ALREADY_ACTIVE");
                // Just wait for connectivity startup without
                // any new request of APN switch.
                return;
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    protected void endMmsConnectivity() {
        // End the connectivity
        try {
            Log.v(TAG, "endMmsConnectivity");
            if (mConnMgr != null) {
                mConnMgr.stopUsingNetworkFeature(
                        ConnectivityManager.TYPE_MOBILE,
                        PhoneEx.FEATURE_ENABLE_MMS);
            }
        } finally {
            releaseWakeLock();
        }
    }

    protected int beginMmsConnectivity() throws IOException {
        // Take a wake lock so we don't fall asleep before the message is downloaded.
        createWakeLock();

        int result = mConnMgr.startUsingNetworkFeature(ConnectivityManager.TYPE_MOBILE, PhoneEx.FEATURE_ENABLE_MMS);

        Log.v(TAG, "beginMmsConnectivity: result=" + result);

        switch (result) {
            case PhoneEx.APN_ALREADY_ACTIVE:
            case PhoneEx.APN_REQUEST_STARTED:
                acquireWakeLock();
                return result;
        }

        throw new IOException("Cannot establish MMS connectivity");
    }

    private synchronized void createWakeLock() {
        // Create a new wake lock if we haven't made one yet.
        if (mWakeLock == null) {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MMS Connectivity");
            mWakeLock.setReferenceCounted(false);
        }
    }

    private void acquireWakeLock() {
        // It's okay to double-acquire this because we are not using it
        // in reference-counted mode.
        mWakeLock.acquire();
    }

    private void releaseWakeLock() {
        // Don't release the wake lock if it hasn't been created and acquired.
        if (mWakeLock != null && mWakeLock.isHeld()) {
            mWakeLock.release();
        }
    }

    private void sendMMSUsingNokiaAPI() {
        // Magic happens here.

        Log.d(TAG,"sendMMSUsingNokiaAPI");

        MMMessage mm = new MMMessage();
        SetMessage(mm);
        AddContents(mm);

        MMEncoder encoder = new MMEncoder();
        encoder.setMessage(mm);

        try {
            Log.d(TAG, "send mms ");
            encoder.encodeMessage();
            byte[] out = encoder.getMessage();

            Log.d(TAG," make MMSender");
            MMSender sender = new MMSender();
            Log.d(TAG, " make APNHelper");
            APNHelper apnHelper = new APNHelper(this);
            List<APN> results = apnHelper.getMMSApns();
            String MMSCenterUrl = "http://omms.nate.com:9082/oma_mms";
            String MMSProxy = "lteoma.nate.com";
            int MMSPort = 9093;

            if (results.size() > 0) {
                TelephonyManager telephonyManager =((TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE));
                String operatorName = telephonyManager.getNetworkOperatorName();

                Log.i("Telephony info", operatorName);
                if(operatorName == null){
                    Log.i("Telephony","NULL");
                } else if(operatorName.equals("SKTelecom")){
                     MMSCenterUrl = "http://omms.nate.com:9082/oma_mms";
                     MMSProxy = "lteoma.nate.com";
                     MMSPort = 9093;
                } else if(operatorName.equals("KT") || operatorName.equals("olleh")) {
                    MMSCenterUrl = "http://mmsc.ktfwing.com:9082";
                    MMSProxy = null;
                    MMSPort = 9093;
                } else if(operatorName.equals("LG U+")){
                    MMSCenterUrl = "http://omammsc.uplus.co.kr:9084";
                    MMSProxy = "";
                    MMSPort = 9084;
                } else {
                    MMSCenterUrl = "http://omms.nate.com:9082/oma_mms";
                    MMSProxy = "lteoma.nate.com";
                    MMSPort = 9093;
                }

                Log.d(TAG," make MMS info");

                final Boolean isProxySet = (MMSProxy != null) && (MMSProxy.trim().length() != 0);

                sender.setMMSCURL(MMSCenterUrl);
                sender.addHeader("X-NOKIA-MMSC-Charging", "100");

                Log.d(TAG,"send request");
                MMResponse mmResponse = sender.send(out, isProxySet, MMSProxy, MMSPort);
                Log.d(TAG, "Message sent to " + sender.getMMSCURL());
                Log.d(TAG, "Response code: " + mmResponse.getResponseCode() + " " + mmResponse.getResponseMessage());

                Enumeration keys = mmResponse.getHeadersList();
                Log.d(TAG,"print responses");
                while (keys.hasMoreElements()) {
                    String key = (String) keys.nextElement();
                    String value = (String) mmResponse.getHeaderValue(key);
                    Log.d(TAG, (key + ": " + value));
                }

                if (mmResponse.getResponseCode() == 200) {
                    // 200 Successful, disconnect and reset.
                    endMmsConnectivity();
                    mSending = false;
                    mListening = false;
                } else {
                    // kill dew :D hhaha
                }
            }
        } catch (Exception e) {
            System.out.println("error : "+e.getMessage());
            Log.e(TAG, "error : " + e.getMessage());
            e.printStackTrace();
        }
        Log.d(TAG,"FINISH SEND func");
    }

    public String phoneNumberLoad() { // MY PHONE NUMBER LOAD
        TelephonyManager tMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String mPhoneNumber = tMgr.getLine1Number();
        return mPhoneNumber;
    }

    public static File resizeImage(File file){
        if(file==null) return file;

        Log.d(TAG, "resize start");

        File dir= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        Bitmap bmpPic= BitmapFactory.decodeFile(file.getPath());

        Log.d(TAG, "resize loaded");

        int MAX_IMAGE_SIZE = 100 * 512; // max final file size
        File finalPath = new File(dir+"/resize.png");
        if ((bmpPic.getWidth() >= 1024) && (bmpPic.getHeight() >= 1024)) {
            BitmapFactory.Options bmpOptions = new BitmapFactory.Options();
            bmpOptions.inSampleSize = 1;
            while ((bmpPic.getWidth() >= 1024) && (bmpPic.getHeight() >= 1024)) {
                bmpOptions.inSampleSize++;
                bmpPic = BitmapFactory.decodeFile(file.getPath(), bmpOptions);
            }
            Log.d(TAG, "Resize: " + bmpOptions.inSampleSize);
        }

        int compressQuality = 104; // quality decreasing by 5 every loop. (start from 99)
        int streamLength = MAX_IMAGE_SIZE;

        while (streamLength >= MAX_IMAGE_SIZE) {
            ByteArrayOutputStream bmpStream = new ByteArrayOutputStream();
            compressQuality -= 5;
            Log.d(TAG, "Quality: " + compressQuality);
            bmpPic.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream);
            byte[] bmpPicByteArray = bmpStream.toByteArray();
            streamLength = bmpPicByteArray.length;
            Log.d(TAG, "Size: " + streamLength);
        }
        Matrix matrix = new Matrix();
        matrix.preRotate(90,0,0);
        bmpPic = Bitmap.createBitmap(bmpPic, 0, 0, bmpPic.getWidth(), bmpPic.getHeight(), matrix, true);
        try {
            Log.d(TAG, "path : " + finalPath);
            FileOutputStream bmpFile = new FileOutputStream(finalPath);
            bmpPic.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpFile);
            bmpFile.flush();
            bmpFile.close();
        } catch (Exception e) {
            Log.e(TAG, "Error on saving file" + e.getMessage());
        }

        return finalPath;
    }

    private void SetMessage(MMMessage mm) {
        mm.setVersion(IMMConstants.MMS_VERSION_10);
        mm.setMessageType(IMMConstants.MESSAGE_TYPE_M_SEND_REQ);
        mm.setTransactionId("0000000066");
        mm.setDate(new Date(System.currentTimeMillis()));
//        mm.setFrom("01082251070/TYPE=PLMN"); // doesnt work, i wish this worked as it should be
//        mm.addToAddress("01082251070/TYPE=PLMN");

        Log.d(TAG, "setfrom : " + phoneNumberLoad() + "/TYPE=PLMN");
        Log.d(TAG, "setto : " + number + "/TYPE=PLMN");
        mm.setFrom(phoneNumberLoad() + "/TYPE=PLMN"); // doesnt work, i wish this worked as it should be
        mm.addToAddress(number + "/TYPE=PLMN");
        mm.setDeliveryReport(true);
        mm.setReadReply(false);
        mm.setSenderVisibility(IMMConstants.SENDER_VISIBILITY_SHOW);

            /*
        String subject  ="광고입니다";
        Log.i("Content",subject);
        byte[] buf2 = new byte[]{};
        try {
            buf2 = subject.getBytes("euc-kr"); // 저장된 광고 내용을 인토딩해서 버퍼에 넣은다
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }*/
       // subject  = new String(buf2);
        mm.setSubject("AD");
        mm.setMessageClass(IMMConstants.MESSAGE_CLASS_PERSONAL);
        mm.setPriority(IMMConstants.PRIORITY_LOW);
        mm.setContentType(IMMConstants.CT_APPLICATION_MULTIPART_MIXED);

//	    In case of multipart related message and a smil presentation available
//	    mm.setContentType(IMMConstants.CT_APPLICATION_MULTIPART_RELATED);
//	    mm.setMultipartRelatedType(IMMConstants.CT_APPLICATION_SMIL);
//	    mm.setPresentationId("<A0>"); // where <A0> is the id of the content containing the SMIL presentation

    }

    private void AddContents(MMMessage mm) {
        Log.d(TAG,"AddContents");
        /*Path where contents are stored*/

        // You need to have this file in your SD. Otherwise error../*
            url = db.PrintData2();
            int size = url.size();
            ArrayList<MMContent> part = new ArrayList<MMContent>();

            for(int i=0;i<size;i++) {
                File file = resizeImage(new File(url.get(i)));
                Uri outputFileUri = Uri.fromFile(file);
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                Bitmap b;

                try {
                    b = Media.getBitmap(getContentResolver(), outputFileUri);
                    b.compress(CompressFormat.PNG, 90, os);
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                // Adds text content
                MMContent part1 = new MMContent();
                byte[] buf1 = os.toByteArray();
                part1.setContent(buf1, 0, buf1.length);
                part1.setContentId("<" + i + ">");
                part1.setType(IMMConstants.CT_IMAGE_PNG);
                part.add(part1);
                mm.addContent(part.get(i));
            }


        size = size + 1;

        MMContent part2 = new MMContent();
        // 디비에서 저장된 광고 내용 불러오기
        // 이미지로드는 printData2()에서 이미지 주소 불러오서 첨부하면 될듯 구현은 안해써 아직 기본 MMS가 보내지는지 테스트를 안해서
        String contnet  =db.PrintData();
        Log.i("Content",contnet);
        byte[] buf2 = new byte[]{};
        try {
            buf2 = contnet.getBytes("euc-kr"); // 저장된 광고 내용을 인토딩해서 버퍼에 넣은다
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        part2.setContent(buf2, 0, buf2.length);
        part2.setContentId("<"+size+">");

        TelephonyManager telephonyManager =((TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE));
        String operatorName = telephonyManager.getNetworkOperatorName();

        Log.i("Telephony info", operatorName);
        if(operatorName.equals("KT") || operatorName.equals("olleh")) { // kt
            part2.setType(IMMConstants.CT_TEXT_HTML);
        } else {
            part2.setType(IMMConstants.CT_TEXT_PLAIN + "; charset=\"euc-kr\";");  //sk, lg
        }
        mm.addContent(part2);
    }

    private class ConnectivityBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG,"onReceive");
            String action = intent.getAction();

            if (!action.equals(ConnectivityManager.CONNECTIVITY_ACTION) || mListening == false) {
                Log.w(TAG, "onReceived() called with " + mState.toString() + " and " + intent);
                return;
            }

            boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);

            if (noConnectivity) {
                mState = State.NOT_CONNECTED;
            } else {
                mState = State.CONNECTED;
            }

            mNetworkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
            mOtherNetworkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_OTHER_NETWORK_INFO);

//			mReason = intent.getStringExtra(ConnectivityManager.EXTRA_REASON);
//			mIsFailover = intent.getBooleanExtra(ConnectivityManager.EXTRA_IS_FAILOVER, false);


            // Check availability of the mobile network.
            if ((mNetworkInfo == null) || (mNetworkInfo.getType() != ConnectivityManager.TYPE_MOBILE_MMS)) {
                Log.v(TAG, "   type is not TYPE_MOBILE_MMS, bail");
                return;
            }
            Log.i("GETTYPE",""+mNetworkInfo.getType());
            Log.i("GETTYPE",mNetworkInfo.toString());
            Log.i("GETTYPE",""+ConnectivityManager.TYPE_MOBILE_MMS);

            if (!mNetworkInfo.isConnected()) {
                Log.v(TAG, "   TYPE_MOBILE_MMS not connected, bail");
                return;
            } else {
                Log.v(TAG, "connected..");
                if (mSending == false) {
                    mSending = true;

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "thread run");

                            dialogStart.sendEmptyMessageDelayed(0, 0);

                            sendMMSUsingNokiaAPI();
                            dialogEnd.sendEmptyMessageDelayed(0, 100);
                            Log.d(TAG,"thread end");
                        }
                    }).start();
                }
            }
        }
    };


    Handler dialogStart = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mProgressDialog = ProgressDialog.show(SendMMSActivity.this, "",
                    "광고를 전송하는 중입니다. \n1~3분정도 소요됩니다.", true);
            mProgressDialog.show();
        }
    };
    Handler dialogEnd = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Toast.makeText(getBaseContext(), "광고를 성공적으로 발송했습니다.", Toast.LENGTH_LONG);
            mProgressDialog.dismiss();
            finish();
        }
    };
}