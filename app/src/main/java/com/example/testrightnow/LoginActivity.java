package com.example.testrightnow;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Notification;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class LoginActivity extends AppCompatActivity{


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        showPermissionDialog();
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        setContentView(R.layout.activity_login);
        GpsTracker gpsTracker = new GpsTracker(LoginActivity.this);
        double longtitude =gpsTracker.getLongitude();
        double latitude = gpsTracker.getLatitude();


        final EditText new_id=(EditText)findViewById(R.id.new_id);
        final EditText new_pw=(EditText)findViewById(R.id.new_pw);
        final Button loginbtn = (Button)findViewById(R.id.button3);
        final TextView SignUpbtn=(TextView)findViewById(R.id.button4);

        SignUpbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent SignUpIntent = new Intent(LoginActivity.this,SignUpActivity.class);
                LoginActivity.this.startActivity(SignUpIntent);
            }
        });

        loginbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                final String u_id = new_id.getText().toString();
                final String u_pw = new_pw.getText().toString();
                final String longtitude1 = Double.toString(longtitude);
                final String latitude1 = Double.toString(latitude);
                final String Token = FirebaseInstanceId.getInstance().getToken();
                final String address=getCurrentAddress(latitude,longtitude);
                Response.Listener<String> responseListener=new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            Toast.makeText(getApplicationContext(),"success"+success,Toast.LENGTH_SHORT).show();

                            if(success){
                                String u_id = jsonResponse.getString("u_id");
                                String u_pw = jsonResponse.getString("u_pw");
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.putExtra("u_id",u_id);
                                LoginActivity.this.startActivity(intent);

                                //intent.putExtra("u_pw",u_pw);
                            }else{
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                builder.setMessage("Login failed")

                                        .setNegativeButton("retry",null)
                                        .create()
                                        .show();
                            }
                        }catch(JSONException e){
                            e.printStackTrace();
                        }
                    }
                };
                LoginRequest loginRequest = new LoginRequest(u_id,u_pw,longtitude1,latitude1,Token,address,responseListener);
                RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                queue.add(loginRequest);
            }
        });
    }
    //오늘 추가하고있는곳
    //지오커더 GPS를 주소로 변환
    public String getCurrentAddress(double latitude,double longtitude){
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(
                    latitude,
                    longtitude,
                    100);
        }catch(IOException ioException){
            Toast.makeText(this, "지오코더 서비스 사용불가",Toast.LENGTH_LONG).show();
            //showDialogForLocationServiceSetting();
            return "지오코더 서비스 사용불가";
        }catch (IllegalArgumentException illegalArgumentException){
            Toast.makeText(this,"잘못된 GPS좌표",Toast.LENGTH_LONG).show();
            //showDialogForLocationServiceSetting();
            return "주소 미발견";
        }
        Address address = addresses.get(0);
        return address.getAddressLine(0).toString()+"\n";
    }

    private void showPermissionDialog(){
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(LoginActivity.this,"Permission Granted.",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(LoginActivity.this,"Permission Denied:"+deniedPermissions.get(0),Toast.LENGTH_LONG).show();
            }
        };
        new TedPermission(this)
                .setPermissionListener(permissionlistener)
                .setPermissions(Manifest.permission.ACCESS_COARSE_LOCATION)
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
                .check();
    }
    /*
    //GPS활성화 위한 메소드
    private void showDialogForLocationServiceSetting(){
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"+"위치 설정을 수정하시겠습니까?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id){
                Intent callGPSSettingIntent= new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id){
                dialog.cancel();
            }
        });
        builder.create().show();
    }
    @Override
    protected  void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
            case GPS_ENABLE_REQUEST_CODE:
                if(checkLocationServicesStatus()){
                    if(checkLoationServicesStatus()){

                    }
                }
        }
    }

     */
}