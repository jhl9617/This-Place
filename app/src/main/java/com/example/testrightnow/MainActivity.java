package com.example.testrightnow;



import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.transition.Slide;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.util.FusedLocationSource;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.Button;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    DrawerLayout drawer;
    Toolbar toolbar;

    //지도
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private FusedLocationSource locationSource;
    private NaverMap naverMap;
    public static String place_result;
    public double dou_latitude;
    public double dou_longitude;
    public LatLng search_latlng;

    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //FireBaseTest
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()){
                            Log.w("FCM LOG", "getInstanceId failed", task.getException());
                            return ;
                        }
                        String token = task.getResult().getToken();
                        Log.d("FCM Log","FCM 토큰: "+token);
                        Toast.makeText(MainActivity.this, token, Toast.LENGTH_SHORT).show();
                    }

                });

        queue = Volley.newRequestQueue(this);

        //로그인 액티비티에서
        Intent intent = getIntent();
        String u_id = intent.getStringExtra("u_id");

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

//View nav_header_view = navigationView.inflateHeaderView(R.layout.nav_header_main);
        View nav_header_view = navigationView.getHeaderView(0);

        TextView nav_header_id_text = (TextView) nav_header_view.findViewById(R.id.login_textview);
        nav_header_id_text.setText(u_id+"님 반갑습니다!");

        showPermissionDialog();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView1 = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //NaverMap Initialize
        MapFragment mapFragment = (MapFragment)getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            getSupportFragmentManager().beginTransaction().add(R.id.map_fragment, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);
        locationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);

        //핀 위도,경도값 MainActivity로 전달
        Button button = findViewById(R.id.button6);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread() {
                    @Override
                    public void run() {
                        double camera_latitude = dou_latitude;
                        double camera_longitude = dou_longitude;
                        String latitude = Double.toString(camera_latitude);
                        String longtitude = Double.toString(camera_longitude);
                        String userid = u_id;
                        String serverUrl = "http://27.96.134.241/htdocs/how.php";
                        String token =FirebaseInstanceId.getInstance().getToken();
                        final String address = getCurrentAddress(dou_latitude,dou_longitude);
                        try {
                            URL url = new URL(serverUrl);

                            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                            connection.setRequestMethod("POST");
                            connection.setDoInput(true);
                            connection.setDoOutput(true);
                            connection.setUseCaches(false);

                            //보낼 데이터
                            String query = "userid=" + userid + "&latitude=" + latitude + "&longtitude=" + longtitude + "&address="+ address+"&token=" +token;

                            OutputStream os = connection.getOutputStream();
                            OutputStreamWriter writer = new OutputStreamWriter(os);



                            writer.write(query, 0, query.length());
                            writer.flush();
                            writer.close();


                            //php에서 echo결과 받기
                            InputStream is = connection.getInputStream();

                            InputStreamReader isr = new InputStreamReader(is);

                            BufferedReader reader = new BufferedReader(isr);

                            final StringBuffer buffer = new StringBuffer();

                            String line = reader.readLine();

                            while (line != null) {
                                buffer.append(line + "\n");
                                line = reader.readLine();
                            }

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), buffer.toString(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }




                /*
                //Intent intent = new Intent();
                double camera_latitude = dou_latitude;
                double camera_longitude = dou_longitude;
                //String으로 바꿔서 id,좌표 보내기
                final String latitude = Double.toString(camera_latitude);
                final String longtitude = Double.toString(camera_longitude);
                final String userid = u_id;
                String serverUrl="http://27.96.134.241/htdocs/howaboutthere.php";/*/
                        //여기다 다시 해보기

                /*
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            Toast.makeText(getApplicationContext(), latitude + "/" + longtitude + "에 대하여 요청합니다.", Toast.LENGTH_SHORT).show();
                            if (success) {
                                Toast.makeText(getApplicationContext(), "요청에 성공하였습니다.", Toast.LENGTH_SHORT).show();
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                builder.setMessage("요청에 실패하였습니다.")
                                        .setNegativeButton("다시시도", null)
                                        .create()
                                        .show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                howaboutthere howaboutthere = new howaboutthere(userid,latitude,longtitude,responseListener);
                RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                queue.add(howaboutthere);/*/

                        //Toast.makeText(getApplicationContext(),userid+"/"+latitude+"/"+longtitude,Toast.LENGTH_SHORT).show();
                        //intent.putExtra("pin_latitude", camera_latitude);
                        //intent.putExtra("pin_longitude", camera_longitude);
                    }
                }.start();
            }
        });
    }
    //요청하는 함수 만드는것

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu1) {
            Intent intent= new Intent(getApplicationContext(),CameraActivity.class);
            startActivity(intent);
        } else if (id == R.id.menu2) {
            Intent intent= new Intent(getApplicationContext(),TalkActivity.class);
            startActivity(intent);
        }

        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    private void showPermissionDialog(){
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(MainActivity.this,"Permission Granted.",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(MainActivity.this,"Permission Denied:"+deniedPermissions.get(0),Toast.LENGTH_LONG).show();
            }
        };
        new TedPermission(this)
                .setPermissionListener(permissionlistener)
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)
                .check();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (locationSource.onRequestPermissionsResult(
                requestCode, permissions, grantResults)) {
            if (!locationSource.isActivated()) { // 권한 거부됨
                naverMap.setLocationTrackingMode(LocationTrackingMode.None);
            }
            return;
        }
        super.onRequestPermissionsResult(
                requestCode, permissions, grantResults);
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        this.naverMap = naverMap;
        naverMap.setLocationSource(locationSource);
        naverMap.setLocationTrackingMode(LocationTrackingMode.Follow);
        naverMap.moveCamera(CameraUpdate.zoomTo(15));
        UiSettings uiSettings = naverMap.getUiSettings();
        uiSettings.setLocationButtonEnabled(true);

        naverMap.addOnCameraChangeListener(new NaverMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(int reason, boolean animated) {
                CameraPosition position = naverMap.getCameraPosition();
                dou_latitude = position.target.latitude;
                dou_longitude = position.target.longitude;
            }
        });

        //주소 검색 후 해당 위치로 카메라 이동
        // Initialize the Google Place SDK
        Places.initialize(getApplicationContext(), "AIzaSyBeXEK8thgp8yPLn01Ho-SHC31CzNavtUo");
        // Create a new PlacesClient instance
        PlacesClient placesClient = Places.createClient(this);
        // Initialize the AutocompleteSupportFragment
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.LAT_LNG, Place.Field.NAME));
        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NotNull Place place) {
                // TODO: Get info about the selected place.
                search_latlng = place.getLatLng();
                double dou_lat = search_latlng.latitude;
                double dou_lon = search_latlng.longitude;
                CameraUpdate cameraUpdate = CameraUpdate.scrollTo(new com.naver.maps.geometry.LatLng(dou_lat, dou_lon));
                naverMap.moveCamera(cameraUpdate);
                naverMap.moveCamera(CameraUpdate.zoomTo(15));
                //Toast.makeText(MapActivity.this,"장소명: " + place.getName() + ", 위도 : " + Double.toString(dou_lat) + ", 경도 : " + Double.toString(dou_lon), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(@NotNull Status status) {
                // TODO: Handle the error.
                Toast.makeText(MainActivity.this, "An error occurred: " + status,Toast.LENGTH_LONG).show();
            }
        });


    }
    //주소 가지고오는 함수
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
}