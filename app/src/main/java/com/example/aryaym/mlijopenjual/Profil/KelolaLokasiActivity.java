package com.example.aryaym.mlijopenjual.Profil;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aryaym.mlijopenjual.Base.BaseActivity;
import com.example.aryaym.mlijopenjual.BuildConfig;
import com.example.aryaym.mlijopenjual.Profil.Service.LocationUpdatesService;
import com.example.aryaym.mlijopenjual.Profil.Service.LocationUtils;
import com.example.aryaym.mlijopenjual.R;
import com.example.aryaym.mlijopenjual.Utils.Constants;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;



public class KelolaLokasiActivity extends BaseActivity{

    @BindView(R.id.btn_edit)
    TextView btnEdit;
    @BindView(R.id.spn_kecamatan)
    Spinner spnKecamatan;
    @BindView(R.id.input_waktu_layout)
    LinearLayout inputWaktuLayout;
    @BindView(R.id.txt_waktu_layout)
    LinearLayout txtWaktuLayout;
    @BindView(R.id.switch_lokasi)
    SwitchCompat switchLokasi;
    @BindView(R.id.txt_status)
    TextView txtStatus;
    @BindView(R.id.btn_submit_lokasi)
    Button btnSubmitLokasi;

    private ArrayAdapter<String> spinnerKecamatanAdapter;

    DatabaseReference mDatabase, mDatabaseGeofire;
    GeoFire geoFire;
    // The BroadcastReceiver used to listen from broadcasts from the service.
    private MyReceiver myReceiver;
    // A reference to the service used to get location updates.
    private LocationUpdatesService mService = null;
    // Tracks the bound state of the service.
    private boolean mBound = false;
    // Used in checking for runtime permissions.
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private static final String TAG = KelolaLokasiActivity.class.getSimpleName();
    // Monitors the state of the connection to the service.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LocationUpdatesService.LocalBinder binder = (LocationUpdatesService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            mBound = false;
        }
    };

    double lat, lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kelola_lokasi);
        ButterKnife.bind(this);
        setTitle(R.string.title_activity_kelola_lokasi);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabaseGeofire = FirebaseDatabase.getInstance().getReference("geofire");
        geoFire = new GeoFire(mDatabaseGeofire);
        if (LocationUtils.requestingLocationUpdates(this)) {
            if (!checkPermissions()) {
                requestPermissions();
            }
        }
        myReceiver = new MyReceiver();
        //disableInput();


        enableInput();

        switchLokasi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (! isChecked){
                    mService.removeLocationUpdates();
                    disableStatusLokasiPenjual();
                }else if (isChecked){
                    if (!checkPermissions()) {
                        requestPermissions();
                    } else {
                        mService.requestLocationUpdates();
                        enableStatusLokasiPenjual(lat, lon);
                    }

                }
            }
        });
        bindService(new Intent(this, LocationUpdatesService.class), mServiceConnection,
                Context.BIND_AUTO_CREATE);
    }

    private void disableInput(){
        spnKecamatan.setEnabled(false);
        inputWaktuLayout.setVisibility(View.GONE);
        btnSubmitLokasi.setVisibility(View.GONE);
    }
    private void enableInput(){
        handleSpinner();
        spnKecamatan.setEnabled(true);
        txtWaktuLayout.setVisibility(View.GONE);
        btnSubmitLokasi.setVisibility(View.VISIBLE);
    }

    private void handleSpinner() {
        spinnerKecamatanAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.arrKecamatan));
        spinnerKecamatanAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        spnKecamatan.setAdapter(spinnerKecamatanAdapter);
        spnKecamatan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String itemKecamatan = parent.getItemAtPosition(position).toString();
                // kecamatan = itemKecamatan;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void enableStatusLokasiPenjual(double latitude, double longtitude){
        mDatabase.child(Constants.PENJUAL).child(getUid()).child(Constants.STATUS_LOKASI).setValue(1);

        geoFire.setLocation(getUid(), new GeoLocation(latitude,longtitude), new GeoFire.CompletionListener(){

            @Override
            public void onComplete(String key, DatabaseError error) {
                if (error != null) {
                    System.err.println("There was an error saving the location to GeoFire: " + error);
                } else {
                    Log.d("##**##" , "Location saved successfully ");
                }
            }
        });
    }
    private void disableStatusLokasiPenjual(){
        mDatabase.child(Constants.PENJUAL).child(getUid()).child(Constants.STATUS_LOKASI).setValue(2);
    }
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_tambah_alamat, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        if (item.getItemId() == R.id.btn_tambah_alamat){
//            Intent intent = new Intent(KelolaLokasiActivity.this, TambahAlamatActivity.class);
//            startActivity(intent);
//        }
//        return super.onOptionsItemSelected(item);
//    }


    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(myReceiver,
                new IntentFilter(LocationUpdatesService.ACTION_BROADCAST));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(myReceiver);
        super.onPause();
    }

    @Override
    public void onStop() {
        if (mBound) {
            // Unbind from the service. This signals to the service that this activity is no longer
            // in the foreground, and the service can respond by promoting itself to a foreground
            // service.
            unbindService(mServiceConnection);
            mBound = false;
        }
        super.onStop();
    }

    /**
     * Returns the current state of the permissions needed.
     */
    private boolean checkPermissions() {
        return  PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            Snackbar.make(
                    findViewById(R.id.activity_kelola_lokasi),
                    R.string.permission_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(KelolaLokasiActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    })
                    .show();
        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(KelolaLokasiActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted.
                mService.requestLocationUpdates();
            } else {
                // Permission denied.
                Snackbar.make(
                        findViewById(R.id.activity_kelola_lokasi),
                        R.string.permission_denied_explanation,
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.settings, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        })
                        .show();
            }
        }
    }

    /**
     * Receiver for broadcasts sent by {@link LocationUpdatesService}.
     */
    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Location lokasi = intent.getParcelableExtra(LocationUpdatesService.EXTRA_LOCATION);
            if (lokasi != null) {
                Toast.makeText(KelolaLokasiActivity.this, LocationUtils.getLocationText(lokasi),
                        Toast.LENGTH_LONG).show();

             double   getLatitude = lokasi.getLatitude();
             double   getLongitude = lokasi.getLongitude();
             enableStatusLokasiPenjual(getLatitude, getLongitude);
            }
        }
    }
}
