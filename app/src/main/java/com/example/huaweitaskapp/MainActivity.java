package com.example.huaweitaskapp;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.huaweitaskapp.Login.LoginActivity;
import com.example.huaweitaskapp.POJOClasses.CityArrayClass;
import com.example.huaweitaskapp.POJOClasses.GeneralCallClass;
import com.example.huaweitaskapp.Network.RetrofitClientInstance;
import com.example.huaweitaskapp.Network.RippleAPIService;
import com.example.huaweitaskapp.databinding.ActivityMainBinding;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1252;
    private FusedLocationProviderClient mFusedLocationClient;

    ActivityMainBinding binding;
    boolean doubleBackToExitPressedOnce = false;

    GeneralCallClass enclosingObject;
    MainAdapter myMainAdapter;

    //Define a request code to send to Google Play services
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    LocationCallback mLocationCallback;
    private double currentLatitude;
    private double currentLongitude;

    private FirebaseAuth mAuth;
    List<CityArrayClass> cityArray = new ArrayList<>();
    LocationSettingsResponse response;

    private boolean isContinue = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        mAuth = FirebaseAuth.getInstance();

        binding.logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(i);
                Toast.makeText(MainActivity.this, getString(R.string.success_logout), Toast.LENGTH_SHORT).show();
            }
        });

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                // The next two lines tell the new client that “this” current class will handle connection stuff
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                //fourth line adds the LocationServices API endpoint from GooglePlayServices
                .addApi(LocationServices.API)
                .build();

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(40 * 1000)        // 40 seconds, in milliseconds
                .setFastestInterval(6 * 1000); // 5 second, in milliseconds

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        currentLatitude = location.getLatitude();
                        currentLongitude = location.getLongitude();

                        drawPicture(currentLatitude, currentLongitude);
                        callRequestForStatistics(currentLatitude, currentLongitude);

                        if (!isContinue && mFusedLocationClient != null) {
                            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
                        }
                    }
                }
            }
        };

        binding.mySearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // do something on text submit
                List<CityArrayClass> dataArray = getData();
                int i = 0;

                //Toast.makeText(MainActivity.this, "Your search started, this make take a while.", Toast.LENGTH_SHORT).show();
                for(; i < dataArray.size(); i++){

                    CityArrayClass tmp = dataArray.get(i);
                    if(tmp.getFindname().equals(query.toUpperCase())){
                        double searchLatitude = tmp.getLat();
                        double searchLongtitude = tmp.getLon();

                        drawPicture(searchLatitude, searchLongtitude);
                        callRequestForStatistics(searchLatitude, searchLongtitude);

                        Toast.makeText(MainActivity.this, "Map and statistics are re-loaded as per your search.", Toast.LENGTH_LONG).show();
                        return true;
                    }
                }
                if(i >= dataArray.size()){
                    Toast.makeText(MainActivity.this, "We could not find it.", Toast.LENGTH_LONG).show();
                    return false;
                }
                Toast.makeText(MainActivity.this, "Something went terribly wrong.", Toast.LENGTH_LONG).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // do something when text changes
                return false;
            }
        });
    }

    public void requestLocationEnabling() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(this).checkLocationSettings(builder.build());

        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try {
                    response = task.getResult(ApiException.class);
                    getEverythingOnScreen();
                    // All location settings are satisfied. The client can initialize location
                    // requests here.
                } catch (ApiException exception) {
                    switch (exception.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be fixed by showing the
                            // user a dialog.
                            try {
                                // Cast to a resolvable exception.
                                ResolvableApiException resolvable = (ResolvableApiException) exception;
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                resolvable.startResolutionForResult(MainActivity.this, LocationRequest.PRIORITY_HIGH_ACCURACY);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            } catch (ClassCastException e) {
                                // Ignore, should be an impossible error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have no way to fix the
                            // settings so we won't show the dialog.
                            break;
                    }
                }
            }
        });
    }

    public void drawPicture(double lat, double lon){
        String layer = "temp_new";
        String z = "10";
        if (lat < 0) {
            lat = lat * -1; //API WANTS IT THIS WAY
        }
        if(lon < 0){
            lon = lon * -1; //API WANTS IT THIS WAY
        }
        String imageUri = "https://tile.openweathermap.org/map/"+ layer +"/" + z + "/" + (int) lat +"/" + (int) lon + ".png?appid=9794a2c6d81483b63a264d80fd5f68db";
        Picasso.with(this).load(imageUri).fit().centerCrop()
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_foreground)
                .into(binding.serverImageView);
    }

    @SuppressLint("MissingPermission")
    public void getEverythingOnScreen(){
        if (isContinue) {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
        } else {
            mFusedLocationClient.getLastLocation().addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        currentLatitude = location.getLatitude();
                        currentLongitude = location.getLongitude();

                        drawPicture(currentLatitude, currentLongitude);
                        callRequestForStatistics(currentLatitude, currentLongitude);
                    } else {
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
                    }
                }
            });
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LocationRequest.PRIORITY_HIGH_ACCURACY) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    getEverythingOnScreen();
                    break;
                case Activity.RESULT_CANCELED:
                    // The user was asked to change settings, but chose not to
                    Toast.makeText(this, "You denied to open Location Services", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    }

    public void callRequestForStatistics(double lat, double lon){
        RippleAPIService service = RetrofitClientInstance.getRetrofitInstance().create(RippleAPIService.class);

        Call<GeneralCallClass> call = service.weatherStatisticsByLatAndLon(lat, lon, "metric", "9794a2c6d81483b63a264d80fd5f68db");
        call.enqueue(new Callback<GeneralCallClass>() {
            @Override
            public void onResponse(Call<GeneralCallClass> call, Response<GeneralCallClass> response) {
                if (response.isSuccessful()) {
                    enclosingObject = response.body();

                    myMainAdapter = new MainAdapter(enclosingObject.getDailyClassList());
                    binding.statisticsRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    binding.statisticsRecyclerView.setAdapter(myMainAdapter);

                } else {
                    Toast.makeText(MainActivity.this, getString(R.string.unsuccessful_response), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<GeneralCallClass> call, Throwable t) {
                Toast.makeText(MainActivity.this, getString(R.string.call_failed), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Now lets connect to the API
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(!isNetworkAvailable()){
            AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
            builder1.setMessage("You are not connected to internet. Please connect and try again.");
            builder1.setCancelable(false);

            builder1.setPositiveButton(
                    "Exit the App",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            finish();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();
        }
        else{
            requestLocationEnabling();

            if (mGoogleApiClient != null) {
                mGoogleApiClient.connect();
            }

            FirebaseUser currentUser = mAuth.getCurrentUser();
            if(currentUser == null){
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(i);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);

        //Disconnect from API onPause()
        if (mGoogleApiClient.isConnected()) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
            mGoogleApiClient.disconnect();
        }
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onConnected(Bundle bundle) {
        if(isNetworkAvailable()){
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
                }
            }
            else {
                getEverythingOnScreen();
            }
        }
    }

    /**/
    public String getJson(){
        String json = null;
        try {
            // Opening data.json file
            InputStream inputStream = getAssets().open("history_city_list.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            // read values in the byte array
            inputStream.read(buffer);
            inputStream.close();
            // convert byte to string
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return json;
        }
        return json;
    }

    public List<CityArrayClass> getData(){
        try {
            JSONArray array = new JSONArray(getJson());
            //JSONArray array = object.getJSONArray("history_city_list");
            for (int i = 0; i < array.length(); i++) {

                JSONObject jsonObject = array.getJSONObject(i);

                JSONObject city = jsonObject.getJSONObject("city");
                JSONObject coordinates = city.getJSONObject("coord");

                int id = jsonObject.getInt("id");
                String findname = city.getString("findname");
                double lat = (double) coordinates.get("lat");
                double lon = (double) coordinates.get("lon");

                CityArrayClass cityArrayObject = new CityArrayClass(id, lat, lon, findname);

                cityArray.add(cityArrayObject);
            }
            return cityArray;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getEverythingOnScreen();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
            Toast.makeText(this, "We just cannot solve the problem.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        currentLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();

        drawPicture(currentLatitude, currentLongitude);
        callRequestForStatistics(currentLatitude, currentLongitude);
        Toast.makeText(this, currentLatitude + " and " + currentLongitude + " is updated. Map and the statistics will reload.", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finish();
            moveTaskToBack(true);
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, getString(R.string.click_back_twice), Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }
}