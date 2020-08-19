package com.example.testrightnow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.util.FusedLocationSource;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Map;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private FusedLocationSource locationSource;
    private NaverMap naverMap;
    public static String place_result;
    public double dou_latitude;
    public double dou_longitude;
    public LatLng search_latlng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        //NaverMap Initialize
        MapFragment mapFragment = (MapFragment)getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            getSupportFragmentManager().beginTransaction().add(R.id.map_fragment, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);
        locationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);

        //핀 위도,경도값 MainActivity로 전달
        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                double camera_latitude = dou_latitude;
                double camera_longitude = dou_longitude;
                intent.putExtra("pin_latitude", camera_latitude);
                intent.putExtra("pin_longitude", camera_longitude);
                setResult(RESULT_OK, intent);
                finish();;
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,  @NonNull int[] grantResults) {
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
                Toast.makeText(MapActivity.this, "An error occurred: " + status,Toast.LENGTH_LONG).show();
            }
        });


    }

}
