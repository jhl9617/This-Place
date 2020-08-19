package com.example.testrightnow;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.CursorLoader;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.BreakIterator;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CameraActivity extends AppCompatActivity {


    ImageView imgVwSelected;
    Button btnImageSend, btnImageSelection;
    File tempSelectFile;
    String imgPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        imgVwSelected = findViewById(R.id.imgVwSelected);
        Intent intent = getIntent();
        final String token = intent.getStringExtra("token");
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            int permissionResult = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if(permissionResult==PackageManager.PERMISSION_DENIED){
                String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permissions,10);
            }
        }
        btnImageSend = findViewById(R.id.btnImageSend);
        btnImageSend.setEnabled(false);
        btnImageSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                FileUploadUtils.send2Server(tempSelectFile,token);
                Toast.makeText(getApplicationContext(),"send",Toast.LENGTH_SHORT).show();
            }
        });

        btnImageSelection = findViewById(R.id.btnImageSelection);
        btnImageSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //이미지 경로 얻어오는거 합치기
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                //intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 10);

            }
        });

        //imgVwSelected = findViewById(R.id.imgVwSelected);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        switch(requestCode){
            case 10:
                if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this,"외부 메모리 읽기/쓰기 사용 가능",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this,"외부 메모리 읽기/쓰기 제한",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 10:
                if(resultCode==RESULT_OK){
                    Toast.makeText(this,"RESULT_OK",Toast.LENGTH_SHORT).show();
                    Uri uri = data.getData();
                    if(uri!=null){
                        imgVwSelected.setImageURI(uri);
                        imgPath = getRealPathFromUri(uri);

                        new AlertDialog.Builder(this).setMessage(uri.toString()+"\n"+"\n"+imgPath).create().show();
                    }
                }else{
                    Toast.makeText(this,"이미지를 선택하지 않았습니다.",Toast.LENGTH_SHORT).show();
                }
                tempSelectFile = new File(imgPath);
                btnImageSend.setEnabled(true);
                break;
        }

        /*
        if (requestCode != 1 || resultCode != RESULT_OK) {
            return;
        }
        Uri dataUri = data.getData();
        imgVwSelected.setImageURI(dataUri);
        try {  //ImageView 에 이미지 출력
            InputStream in = getContentResolver().openInputStream(dataUri);
            Bitmap image = BitmapFactory.decodeStream(in);
            imgVwSelected.setImageBitmap(image);
            in.close();
            // 선택한 이미지 임시 저장
            String date = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss").format(new Date());
            tempSelectFile = new File(Environment.getExternalStorageDirectory() + "/Pictures/Test/", "temp_" + date + ".jpeg");
            OutputStream out = new FileOutputStream(tempSelectFile);
            image.compress(Bitmap.CompressFormat.JPEG, 100, out);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        btnImageSend.setEnabled(true);

         /*/
    }
    String getRealPathFromUri(Uri uri){
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(this,uri,proj,null,null,null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }



}













