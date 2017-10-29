package com.example.aryaym.mlijopenjual.Profil;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.codetroopers.betterpickers.radialtimepicker.RadialTimePickerDialogFragment;
import com.example.aryaym.mlijopenjual.Base.BaseActivity;
import com.example.aryaym.mlijopenjual.BuildConfig;
import com.example.aryaym.mlijopenjual.Profil.Service.LocationUpdatesService;
import com.example.aryaym.mlijopenjual.Profil.Service.LocationUtils;
import com.example.aryaym.mlijopenjual.R;
import com.example.aryaym.mlijopenjual.Utils.Constants;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;


public class KelolaLokasiActivity extends BaseActivity
        implements View.OnClickListener, RadialTimePickerDialogFragment.OnTimeSetListener{



    @BindView(R.id.switch_lokasi)
    SwitchCompat switchLokasi;
    @BindView(R.id.txt_status)
    TextView txtStatus;
    @BindView(R.id.btn_submit_lokasi)
    Button btnSubmit;
    @BindView(R.id.txt_kecamatan)
    TextView txtKecamatan;
    @BindView(R.id.hari_mulai)
    TextView txtHariMulai;
    @BindView(R.id.hari_selesai)
    TextView txtHariSelesai;
    @BindView(R.id.jam_mulai)
    TextView txtJamMulai;
    @BindView(R.id.jam_selesai)
    TextView txtJamSelesai;

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
    private int mSelectedDay;
    private ArrayList<Integer> mSelectKecamatan;
    private static final String FRAG_TAG_TIME_PICKER_OPEN = "timePickerDialogFragmentOpen";
    private static final String FRAG_TAG_TIME_PICKER_CLOSE = "timePickerDialogFragmentClose";
    private long timeOpenInMinute = 0, timeCloseInMinute = 0;

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
        ambilData();

        txtKecamatan.setOnClickListener(this);
        txtHariMulai.setOnClickListener(this);
        txtHariSelesai.setOnClickListener(this);
        txtJamMulai.setOnClickListener(this);
        txtJamSelesai.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);

        switchLokasi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    mService.removeLocationUpdates();
                    disableStatusLokasiPenjual();
                    txtStatus.setText("non-Aktif");
                } else if (isChecked) {
                    if (!checkPermissions()) {
                        requestPermissions();
                    } else {
                        mService.requestLocationUpdates();
                        enableStatusLokasiPenjual(lat, lon);
                    }
                    txtStatus.setText("Aktif");
                }
            }
        });
        bindService(new Intent(this, LocationUpdatesService.class), mServiceConnection,
                Context.BIND_AUTO_CREATE);
    }

    private void ambilData(){
        try {
            mDatabase.child(Constants.PENJUAL).child(getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot != null) {
                        PenjualModel penjualModel = dataSnapshot.getValue(PenjualModel.class);
                        if (penjualModel != null) {
                            try {
                                txtKecamatan.setText(penjualModel.getInfoLokasi().get(Constants.KECAMATAN).toString());
                                txtHariMulai.setText(penjualModel.getInfoLokasi().get(Constants.HARI_MULAI).toString());
                                txtHariSelesai.setText(penjualModel.getInfoLokasi().get(Constants.HARI_SELESAI).toString());
                                txtJamMulai.setText(penjualModel.getInfoLokasi().get(Constants.JAM_MULAI).toString());
                                txtJamSelesai.setText(penjualModel.getInfoLokasi().get(Constants.JAM_SELESAI).toString());
                                if (penjualModel.isStatusLokasi()){
                                    //switchLokasi.setChecked(true);
                                    txtStatus.setText("Aktif");
                                }else {
                                    //switchLokasi.setChecked(false);
                                    txtStatus.setText("non-Aktif");
                                }
                            }catch (Exception e){

                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }catch (Exception e){

        }
    }

    private void submitData(Map<String, Object> infoLokasi){
        Map<String, Object> updateDataPenjual = new HashMap<>();
        updateDataPenjual.put(Constants.INFO_LOKASI, infoLokasi);
        mDatabase.child(Constants.PENJUAL).child(getUid()).updateChildren(updateDataPenjual);
    }

    private void perbaruiData(){
        try {
            String kecamatan = txtKecamatan.getText().toString();
            String startDay = txtHariMulai.getText().toString();
            String endDay = txtHariSelesai.getText().toString();
            String startTime = txtJamMulai.getText().toString();
            String endTime = txtJamSelesai.getText().toString();
            //getData
            Map<String, Object> infoLokasi = new HashMap<>();
            infoLokasi.put(Constants.KECAMATAN, kecamatan);
            infoLokasi.put(Constants.HARI_MULAI, startDay);
            infoLokasi.put(Constants.HARI_SELESAI, endDay);
            infoLokasi.put(Constants.JAM_MULAI, startTime);
            infoLokasi.put(Constants.JAM_SELESAI, endTime);

            submitData(infoLokasi);
        }catch (Exception e){

        }
    }

    @Override
    public void onClick(View v) {
        if (v == txtKecamatan){
            shownAlertPilihKecamatan();
        }else if (v == txtHariMulai){
            showAlertHariMulai();
        }else if (v == txtHariSelesai){
            showAlertHariSelesai();
        }else if (v == txtJamMulai){
            RadialTimePickerDialogFragment time = new RadialTimePickerDialogFragment()
                    .setOnTimeSetListener(KelolaLokasiActivity.this)
                    .setStartTime(12,00)
                    .setDoneText("OK")
                    .setCancelText("Batal");
            time.show(getSupportFragmentManager(), FRAG_TAG_TIME_PICKER_OPEN );
        }else if (v == txtJamSelesai){
            RadialTimePickerDialogFragment time = new RadialTimePickerDialogFragment()
                    .setOnTimeSetListener(KelolaLokasiActivity.this)
                    .setStartTime(12,00)
                    .setDoneText("OK")
                    .setCancelText("Batal");
            time.show(getSupportFragmentManager(), FRAG_TAG_TIME_PICKER_CLOSE );
        }else if (v == btnSubmit){
          //  if (cekUpdateData()){
                perbaruiData();
                finish();
            }
        //}
    }

    //Select Jam
    @Override
    public void onTimeSet(RadialTimePickerDialogFragment dialogFragment, int jam, int menit){
        if (dialogFragment.getTag().equals(FRAG_TAG_TIME_PICKER_OPEN)){
            txtJamMulai.setText(String.format("%d : %d", jam, menit));
            timeOpenInMinute = jam * 60 + menit;
        }else {
            txtJamSelesai.setText(String.format("%d : %d", jam, menit));
            timeCloseInMinute = jam * 60 + menit;
        }
    }

    //Select Hari
    private void showAlertHariMulai(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pilih Hari")
                .setSingleChoiceItems(R.array.hari, 0,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mSelectedDay = which;
                            }
                        })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        txtHariMulai.setText(getResources().getStringArray(R.array.hari)[mSelectedDay]);
                    }
                })
                .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        //show dialog
        builder.show();
    }

    private void showAlertHariSelesai(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pilih Hari")
                .setSingleChoiceItems(R.array.hari, 0,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mSelectedDay = which;
                            }
                        })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        txtHariSelesai.setText(getResources().getStringArray(R.array.hari)[mSelectedDay]);
                    }
                })
                .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        //show dialog
        builder.show();
    }

    //Pilih Kecamatan
    private void shownAlertPilihKecamatan(){
        mSelectKecamatan = new ArrayList<>();
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pilih Area Berdasarkan Kecamatan")
                .setMultiChoiceItems(R.array.arrKecamatan, null, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if (isChecked){
                            //select item
                            mSelectKecamatan.add(which);
                        }else if (mSelectKecamatan.contains(which)){
                            mSelectKecamatan.remove(Integer.valueOf(which));
                        }
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String kecText = "";
                        for (int i:mSelectKecamatan){
                            kecText += getResources().getStringArray(R.array.arrKecamatan)[i];
                            kecText += ",";
                        }
                        txtKecamatan.setText(kecText);
                    }
                })
                .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.show();
    }


    private void enableStatusLokasiPenjual(double latitude, double longtitude) {
        mDatabase.child(Constants.PENJUAL).child(getUid()).child(Constants.STATUS_LOKASI).setValue(true);

        geoFire.setLocation(getUid(), new GeoLocation(latitude, longtitude), new GeoFire.CompletionListener() {

            @Override
            public void onComplete(String key, DatabaseError error) {
                if (error != null) {
                    System.err.println("There was an error saving the location to GeoFire: " + error);
                } else {
                    Log.d("##**##", "Location saved successfully ");
                }
            }
        });
    }

    private void disableStatusLokasiPenjual() {
        mDatabase.child(Constants.PENJUAL).child(getUid()).child(Constants.STATUS_LOKASI).setValue(false);
        geoFire.removeLocation(getUid());
    }


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
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this,
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
                Log.i(TAG, "PenjualModel interaction was cancelled.");
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

                double getLatitude = lokasi.getLatitude();
                double getLongitude = lokasi.getLongitude();
                enableStatusLokasiPenjual(getLatitude, getLongitude);
            }
        }
    }
}
