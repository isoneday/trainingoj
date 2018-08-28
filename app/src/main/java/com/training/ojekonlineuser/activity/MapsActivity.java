package com.training.ojekonlineuser.activity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.training.ojekonlineuser.R;
import com.training.ojekonlineuser.helper.DirectionMapsV2;
import com.training.ojekonlineuser.helper.GPStrack;
import com.training.ojekonlineuser.helper.HeroHelper;
import com.training.ojekonlineuser.helper.MyConstants;
import com.training.ojekonlineuser.helper.SessionManager;
import com.training.ojekonlineuser.model.Distance;
import com.training.ojekonlineuser.model.Duration;
import com.training.ojekonlineuser.model.LegsItem;
import com.training.ojekonlineuser.model.ResponseWaypoints;
import com.training.ojekonlineuser.model.RoutesItem;
import com.training.ojekonlineuser.network.InitRetrofit;
import com.training.ojekonlineuser.network.RestApi;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    @BindView(R.id.imgpick)
    ImageView imgpick;
    @BindView(R.id.lokasiawal)
    TextView lokasiawal;
    @BindView(R.id.lokasitujuan)
    TextView lokasitujuan;
    @BindView(R.id.edtcatatan)
    EditText edtcatatan;
    @BindView(R.id.txtharga)
    TextView txtharga;
    @BindView(R.id.txtjarak)
    TextView txtjarak;
    @BindView(R.id.txtdurasi)
    TextView txtdurasi;
    @BindView(R.id.requestorder)
    Button requestorder;
    @BindView(R.id.rootlayout)
    RelativeLayout rootlayout;
    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    private GPStrack gpstrack;
    private double lat;
    private double lon;
    private String name_location;
    private LatLng lokasiku;
    private Intent intent;
    private double latawal;
    private double lonawal;
    private double latakhir;
    private double lonakhir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ButterKnife.bind(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        cekstatusgps();
    }

    private void cekstatusgps() {

        final LocationManager manager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "Gps already enabled", Toast.LENGTH_SHORT).show();
            //     finish();
        }
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "Gps not enabled", Toast.LENGTH_SHORT).show();
            enableLoc();
        }

    }

    private void enableLoc() {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(Bundle bundle) {

                        }

                        @Override
                        public void onConnectionSuspended(int i) {
                            googleApiClient.connect();
                        }
                    })
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(ConnectionResult connectionResult) {

                            Log.d("Location error", "Location error " + connectionResult.getErrorCode());
                        }
                    }).build();
            googleApiClient.connect();

            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(30 * 1000);
            locationRequest.setFastestInterval(5 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);

            builder.setAlwaysShow(true);

            PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(MapsActivity.this, MyConstants.REQUEST_LOCATION);

                                finish();
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                    }
                }
            });
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                    && checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
                                android.Manifest.permission.ACCESS_FINE_LOCATION},
                        110);


            }
            return;
        }
        akseslokasiku();
    }

    private void akseslokasiku() {
        gpstrack = new GPStrack(MapsActivity.this);
        if (gpstrack.canGetLocation() && mMap != null) {
            lat = gpstrack.getLatitude();
            lon = gpstrack.getLongitude();
            mMap.clear();

            name_location = convertlocation(lat, lon);

            Toast.makeText(this, "lat : " + lat + "\n lon :" + lon, Toast.LENGTH_SHORT).show();
            lokasiku = new LatLng(lat, lon);
            mMap.addMarker(new MarkerOptions().position(lokasiku).title(name_location).icon(
                    BitmapDescriptorFactory.fromResource(R.mipmap.ic_marker)));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lokasiku, 18));
            mMap.getUiSettings().setCompassEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }
    }

    private String convertlocation(double lat, double lon) {
        name_location = null;
        Geocoder geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());
        try {
            List<Address> list = geocoder.getFromLocation(lat, lon, 1);
            if (list != null && list.size() > 0) {
                name_location = list.get(0).getAddressLine(0) + "" + list.get(0).getCountryName();

                //fetch data from addresses
            } else {
                Toast.makeText(this, "kosong", Toast.LENGTH_SHORT).show();
                //display Toast message
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return name_location;

    }


    @OnClick({R.id.lokasiawal, R.id.lokasitujuan, R.id.requestorder})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.lokasiawal:
                try {
                    intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                            .build(MapsActivity.this);
                    startActivityForResult(intent, MyConstants.REQAWAL);
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.lokasitujuan:
                try {
                    intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                            .build(MapsActivity.this);
                    startActivityForResult(intent, MyConstants.REQTUJUAN);
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.requestorder:
                prosesorder();
                break;
        }
    }

    private void prosesorder() {
        SessionManager manager = new SessionManager(this);
        int iduser = Integer.parseInt(manager.getIdUser());
        String ltawal = String.valueOf(latawal);
        String lnawal = String.valueOf(latawal);
        String ltakhir = String.valueOf(latawal);
        String lnakhir = String.valueOf(latawal);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Place place = PlaceAutocomplete.getPlace(this, data);

        if (requestCode == MyConstants.REQAWAL && resultCode == RESULT_OK) {
            latawal = place.getLatLng().latitude;
            lonawal = place.getLatLng().longitude;
            name_location = place.getName().toString();
            lokasiawal.setText(name_location);
            mMap.clear();
            addmarker(lat, lon);
        } else if (requestCode == MyConstants.REQTUJUAN && resultCode == RESULT_OK) {
            latakhir = place.getLatLng().latitude;
            lonakhir = place.getLatLng().longitude;
            name_location = place.getName().toString();
            lokasitujuan.setText(name_location);
            mMap.clear();
            addmarker(lat, lon);
            aksesrute();
        }
    }

    private void aksesrute() {
        RestApi api = InitRetrofit.getintance2();
//        String origin = String.valueOf(latawal) + "," + String.valueOf(lonawal);
//        String destination = String.valueOf(latakhir) + "," + String.valueOf(lonakhir);

        Call<ResponseWaypoints> waypointsCall = api.getrute(
                lokasiawal.getText().toString(),
                lokasitujuan.getText().toString()
        );

        waypointsCall.enqueue(new Callback<ResponseWaypoints>() {
            @Override
            public void onResponse(Call<ResponseWaypoints> call, Response<ResponseWaypoints> response) {
                if (response.isSuccessful()) {
                    String status = response.body().getStatus();
                    if (status.equals("OK")) {
                        List<RoutesItem> routes = response.body().getRoutes();
                        List<LegsItem> legs = routes.get(0).getLegs();
                        Distance distance = legs.get(0).getDistance();
                        Duration duration = legs.get(0).getDuration();
                        txtdurasi.setText(duration.getText().toString());
                        String j = HeroHelper.removeLastChar(distance.getText().toString());
                        txtjarak.setText(j);
                        //hitung harga
                        double nilaijarak = Double.valueOf(distance.getValue());
                        double harga = Math.ceil(nilaijarak / 1000);
                        double total = harga * 10000;
                        txtharga.setText("RP." + HeroHelper.toRupiahFormat2(String.valueOf(total)));
                        String points = response.body().getRoutes().get(0).getOverviewPolyline().getPoints();
                        DirectionMapsV2 mapsV2 = new DirectionMapsV2(MapsActivity.this);
                        mapsV2.gambarRoute(mMap, points);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseWaypoints> call, Throwable t) {

            }
        });
    }

    private void addmarker(double lat, double lon) {
        lokasiku = new LatLng(lat, lon);
        name_location = convertlocation(lat, lon);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lokasiku, 18));
        mMap.addMarker(new MarkerOptions().position(lokasiku).title(name_location).
                icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_marker)));

    }
}
