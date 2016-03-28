package com.kmong.cyber.ad_mms_poster_user;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;

import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kmong.cyber.ad_mms_poster_user.Adapter.ImageAdapter;
import com.kmong.cyber.ad_mms_poster_user.Model.ImageModel;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;


import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ImageActivity extends Activity {
    @Bind(R.id.image_insert) Button btn_insert;
    @Bind(R.id.listview) ListView list;
    final int REQ_CODE_SELECT_IMAGE=100;
    public DBController db;
    private ImageAdapter mAdapter;
    public String no;
    private ArrayList<String> result = new ArrayList<String>();
    private ArrayList<String> pri_no = new ArrayList<String>();
    private Uri mImageCaptureUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        init();
    }

    void init(){
        ButterKnife.bind(this);
        mAdapter = new ImageAdapter(getApplicationContext());
        list.setAdapter(mAdapter);
        list.setOnItemClickListener(itemClickListener);
        db = new DBController(this);

        loadimage();

    }


    void loadimage(){
        result =null;
        pri_no = null;

        result = db.PrintData2();
        pri_no = db.PrintData3();

        for(int i=0;i<result.size();i++){
            mAdapter.Additem(new ImageModel(pri_no.get(i),result.get(i)));
        }

    }

    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            no = ((TextView) view.findViewById(R.id.listview_number)).getText().toString();
            Log.i("get",no);
            AlertDialog.Builder alert_confirm = new AlertDialog.Builder(ImageActivity.this);
            alert_confirm.setMessage("사진 목록을 삭제하겠습니까?").setCancelable(false).setPositiveButton("삭제",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            db.delete(no);
                            Intent i = new Intent(ImageActivity.this, MainActivity.class);
                            startActivity(i);
                            finish();

                        }
                    }).setNegativeButton("취소",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            return;
                        }
                    });
            AlertDialog alert = alert_confirm.create();
            alert.show();
        }
    };



    @Override
     protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        if(requestCode == REQ_CODE_SELECT_IMAGE)
        {
            if(resultCode==RESULT_OK)
            {
                try {
                    mImageCaptureUri = data.getData();
                    File original_file = getImageFile(mImageCaptureUri);
                    mImageCaptureUri = createSaveCropFile();
                    File cpoy_file = new File(mImageCaptureUri.getPath());
                    // SD카드에 저장된 파일을 이미지 Crop을 위해 복사한다.
                    copyFile(original_file , cpoy_file);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

                Toast.makeText(getBaseContext(), "성공", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(ImageActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        }
    }

    private Uri createSaveCropFile(){
        Uri uri;
        String filename = "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
        uri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), filename));

        String query = String.format("insert into img values (null,'%s')",Environment.getExternalStorageDirectory().toString()+"/"+filename); // 여기다가 이미지 저장 주소 넣어야함
        try {
            db.update(query);
        } catch (Exception e){
            Log.i("ERROR",e.toString());
        }

        return uri;
    }


    private File getImageFile(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        if (uri == null) {
            uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }

        Cursor mCursor = getContentResolver().query(uri, projection, null, null,
                MediaStore.Images.Media.DATE_MODIFIED + " desc");
        if(mCursor == null || mCursor.getCount() < 1) {
            return null; // no cursor or no record
        }
        int column_index = mCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        mCursor.moveToFirst();

        String path = mCursor.getString(column_index);

        if (mCursor !=null ) {
            mCursor.close();
            mCursor = null;
        }

        return new File(path);
    }
    public static boolean copyFile(File srcFile, File destFile) {
        boolean result = false;
        try {
            InputStream in = new FileInputStream(srcFile);
            try {
                result = copyToFile(in, destFile);
            } finally  {
                in.close();
            }
        } catch (IOException e) {
            result = false;
        }
        return result;
    }

    /**
     * Copy data from a source stream to destFile.
     * Return true if succeed, return false if failed.
     */
    private static boolean copyToFile(InputStream inputStream, File destFile) {
        try {
            OutputStream out = new FileOutputStream(destFile);
            try {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) >= 0) {
                    out.write(buffer, 0, bytesRead);
                }
            } finally {
                out.close();
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }




    @OnClick(R.id.image_insert)
    void image_insert(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, REQ_CODE_SELECT_IMAGE);
    }



}
