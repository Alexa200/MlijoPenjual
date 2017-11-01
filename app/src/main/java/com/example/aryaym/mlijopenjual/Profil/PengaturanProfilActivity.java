package com.example.aryaym.mlijopenjual.Profil;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.aryaym.mlijopenjual.Base.BaseActivity;
import com.example.aryaym.mlijopenjual.Base.ImageLoader;
import com.example.aryaym.mlijopenjual.Base.InternetConnection;
import com.example.aryaym.mlijopenjual.R;
import com.example.aryaym.mlijopenjual.Utils.Constants;
import com.example.aryaym.mlijopenjual.Utils.EncodeImage;
import com.example.aryaym.mlijopenjual.Utils.ShowAlertDialog;
import com.example.aryaym.mlijopenjual.Utils.ShowSnackbar;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PengaturanProfilActivity extends BaseActivity
        implements View.OnClickListener {

    @BindView(R.id.imgPenjual)
    ImageView imgPenjual;
    @BindView(R.id.input_alamat_penjual)
    EditText inputAlamatPenjual;
    @BindView(R.id.input_telepon_penjual)
    EditText inputTeleponPenjual;
    @BindView(R.id.chk_sayuran)
    CheckBox chkSayuran;
    @BindView(R.id.chk_buah)
    CheckBox chkBuah;
    @BindView(R.id.chk_daging)
    CheckBox chkDaging;
    @BindView(R.id.chk_ikan)
    CheckBox chkIkan;
    @BindView(R.id.chk_palawija)
    CheckBox chkPalawija;
    @BindView(R.id.chk_bumbu)
    CheckBox chkBumbu;
    @BindView(R.id.chk_peralatan)
    CheckBox chkPeralatan;
    @BindView(R.id.chk_lain)
    CheckBox chkLain;
    @BindView(R.id.btn_edit_profil)
    Button btnSimpanProfil;

    private static final int REQUEST_CODE_READ_STORAGE = 1001;
    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    private static final int REQUEST_CODE_CAMERA = 1002;
    private static final String[] PERMISSIONS_CAMERA = {
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private Uri mUri;
    private DatabaseReference mDatabase;
    private StorageReference mStorage;
    private byte[] bitmapDataUser = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pengaturan_profil);
        setTitle(R.string.title_activity_kelola_profil);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance().getReference();
        ambilData();

        imgPenjual.setOnClickListener(this);

        btnSimpanProfil.setOnClickListener(this);
    }

    private void ambilData(){
        try {
            mDatabase.child(Constants.PENJUAL).child(getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot != null){
                        PenjualModel penjualModel = dataSnapshot.getValue(PenjualModel.class);
                        if (penjualModel != null){
                            try {
                                //ImageLoader.getInstance().loadImageAvatar(PengaturanProfilActivity.this, penjualModel.getDetailPenjual().get(Constants.AVATAR).toString(), imgPenjual);
                                inputAlamatPenjual.setText(penjualModel.getDetailPenjual().get(Constants.ALAMAT).toString());
                                inputTeleponPenjual.setText(penjualModel.getDetailPenjual().get(Constants.TELPON).toString());
                                //get kategori
                                if (Boolean.parseBoolean(penjualModel.getInfoKategori().get(Constants.KATEGORI_SAYURAN).toString())){
                                    chkSayuran.setChecked(true);
                                }else {
                                    chkSayuran.setChecked(false);
                                }
                                if (Boolean.parseBoolean(penjualModel.getInfoKategori().get(Constants.KATEGORI_BUAH).toString())){
                                    chkBuah.setChecked(true);
                                }else {
                                    chkBuah.setChecked(false);
                                }
                                if (Boolean.parseBoolean(penjualModel.getInfoKategori().get(Constants.KATEGORI_DAGING).toString())){
                                    chkDaging.setChecked(true);
                                }else {
                                    chkDaging.setChecked(false);
                                }
                                if (Boolean.parseBoolean(penjualModel.getInfoKategori().get(Constants.KATEGORI_IKAN).toString())){
                                    chkIkan.setChecked(true);
                                }else {
                                    chkIkan.setChecked(false);
                                }
                                if (Boolean.parseBoolean(penjualModel.getInfoKategori().get(Constants.KATEGORI_PALAWIJA).toString())){
                                    chkPalawija.setChecked(true);
                                }else {
                                    chkPalawija.setChecked(false);
                                }
                                if (Boolean.parseBoolean(penjualModel.getInfoKategori().get(Constants.KATEGORI_BUMBU).toString())){
                                    chkBumbu.setChecked(true);
                                }else {
                                    chkBumbu.setChecked(false);
                                }
                                if (Boolean.parseBoolean(penjualModel.getInfoKategori().get(Constants.KATEGORI_PERALATAN).toString())){
                                    chkPeralatan.setChecked(true);
                                }else {
                                    chkPeralatan.setChecked(false);
                                }
                                if (Boolean.parseBoolean(penjualModel.getInfoKategori().get(Constants.KATEGORI_LAIN).toString())){
                                    chkLain.setChecked(true);
                                }else {
                                    chkLain.setChecked(false);
                                }
                                ImageLoader.getInstance().loadImageAvatar(PengaturanProfilActivity.this, penjualModel.getDetailPenjual().get(Constants.AVATAR).toString(), imgPenjual);
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

    private boolean cekUpdateData(){
        boolean sukses = true;
        if (inputAlamatPenjual.getText() == null || inputTeleponPenjual.getText() == null && (inputTeleponPenjual.getText().length() >12 || inputTeleponPenjual.getText().length() <10) ){
            sukses = false;
            ShowAlertDialog.showAlert("Mohon diisi", this);
        }
        return sukses;
    }

    private void submitData(String alamat, String notelp,  Map<String, Object> detailInfo){
        mDatabase.child(Constants.PENJUAL).child(getUid()).child(Constants.INFO_KATEGORI).updateChildren(detailInfo);
        mDatabase.child(Constants.PENJUAL).child(getUid()).child(Constants.DETAIL_PENJUAL).child(Constants.ALAMAT).setValue(alamat);
        mDatabase.child(Constants.PENJUAL).child(getUid()).child(Constants.DETAIL_PENJUAL).child(Constants.TELPON).setValue(notelp);
    }

    private void perbaruiData(){
        try {
            String alamat = inputAlamatPenjual.getText().toString();
            String notelp = inputTeleponPenjual.getText().toString();
            boolean katSayur = chkSayuran.isChecked();
            boolean katBuah = chkBuah.isChecked();
            boolean katDaging = chkDaging.isChecked();
            boolean katIkan = chkIkan.isChecked();
            boolean katPala = chkPalawija.isChecked();
            boolean katBumbu = chkBumbu.isChecked();
            boolean katAlat = chkPeralatan.isChecked();
            boolean katLain = chkLain.isChecked();
            //getData
            Map<String, Object> detailInfo = new HashMap<>();
            detailInfo.put(Constants.KATEGORI_SAYURAN, katSayur);
            detailInfo.put(Constants.KATEGORI_BUAH, katBuah);
            detailInfo.put(Constants.KATEGORI_DAGING, katDaging);
            detailInfo.put(Constants.KATEGORI_IKAN, katIkan);
            detailInfo.put(Constants.KATEGORI_PALAWIJA, katPala);
            detailInfo.put(Constants.KATEGORI_BUMBU, katBumbu);
            detailInfo.put(Constants.KATEGORI_PERALATAN, katAlat);
            detailInfo.put(Constants.KATEGORI_LAIN, katLain);

            submitData(alamat, notelp, detailInfo);
        }catch (Exception e){

        }
    }

    @Override
    public void onClick(View v) {
        if (v == imgPenjual){
            showAlertForCamera();
        }else if (v == btnSimpanProfil){
            if (cekUpdateData()){
                perbaruiData();
                finish();
            }
        }
    }

    //Upload Foto penjual
    private void showAlertForCamera() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater = getLayoutInflater();
        View v = layoutInflater.inflate(R.layout.custom_dialog_up_image, null);
        builder.setView(v);
        //components in custom view
        TextView txtGallery = (TextView) v.findViewById(R.id.txt_open_gallery);
        TextView txtCamera = (TextView) v.findViewById(R.id.txt_open_camera);
        //show dialog
        final AlertDialog alertDialog = builder.show();
        //event click
        txtGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verifyStoragePermissions()) {
                    showGallery();
                }
                alertDialog.dismiss();
            }
        });
        txtCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verifyOpenCamera()) {
                    openCamera();
                }
                alertDialog.dismiss();
            }
        });

    }

    //open gallery to choosing image
    private void showGallery() {
        Intent intent = new Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, Constants.GALLERY_INTENT);
    }

    //open gallery to taking a picture
    private void openCamera() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "Image File Name");
        mUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mUri);
        startActivityForResult(cameraIntent, Constants.CAMERA_INTENT);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_READ_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showGallery();
                }
                break;
            case REQUEST_CODE_CAMERA:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                }
                break;
        }
    }

    //confirm request persmission
    private boolean verifyOpenCamera() {
        int camera = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (camera != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_CAMERA, REQUEST_CODE_CAMERA
            );

            return false;
        }
        return true;
    }

    //confirm request persmission
    private boolean verifyStoragePermissions() {
        // Check if we have read or write permission
        int readPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (readPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE, REQUEST_CODE_READ_STORAGE
            );

            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.GALLERY_INTENT && resultCode == RESULT_OK) {
            if (InternetConnection.getInstance().isOnline(PengaturanProfilActivity.this)) {
                try {
                    //load image into imageview
                    ImageLoader.getInstance().loadImageAvatar(PengaturanProfilActivity.this, data.getData().toString(), imgPenjual);
                    Constants.USER_FILE_PATH = getRealPathFromURI(data.getData());
                    addImageUser(getUid(), new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            editUserPhotoURL(getUid(), taskSnapshot.getDownloadUrl().toString());
                        }
                    });
                } catch (Exception e) {
                    ShowSnackbar.showSnack(this, getResources().getString(R.string.msg_error));
                }
            } else {
                ShowSnackbar.showSnack(PengaturanProfilActivity.this, getResources().getString(R.string.msg_noInternet));
            }
        } else if (requestCode == Constants.CAMERA_INTENT && resultCode == RESULT_OK) {
            if (InternetConnection.getInstance().isOnline(PengaturanProfilActivity.this)) {
                try {
                    if (mUri != null) {
                        //load image into imageview
                        ImageLoader.getInstance().loadImageAvatar(PengaturanProfilActivity.this, mUri.toString(), imgPenjual);
                        Constants.USER_FILE_PATH = getRealPathFromURI(mUri);
                        addImageUser(getUid(), new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                editUserPhotoURL(getUid(), taskSnapshot.getDownloadUrl().toString());
                            }
                        });
                    } else {
                        ShowSnackbar.showSnack(this, getResources().getString(R.string.msg_error));
                    }
                } catch (Exception e) {
                    ShowSnackbar.showSnack(this, getResources().getString(R.string.msg_error));
                }
            } else {
                ShowSnackbar.showSnack(PengaturanProfilActivity.this, getResources().getString(R.string.msg_noInternet));
            }
        }
    }

    private String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    public void editUserPhotoURL(String uid, String photoURL) {
        Map<String, Object> myMap = new HashMap<>();
        myMap.put(Constants.AVATAR, photoURL);
        mDatabase.child(Constants.PENJUAL).child(uid).child(Constants.DETAIL_PENJUAL).updateChildren(myMap);
    }

    public void addImageUser(String uid, OnSuccessListener<UploadTask.TaskSnapshot> listener) {
        if (Constants.USER_FILE_PATH != null) {
            bitmapDataUser = EncodeImage.encodeImage(Constants.USER_FILE_PATH);
        }
        if (bitmapDataUser != null) {
            StorageReference filePathAvatar = mStorage.child(Constants.USER_AVATAR).child(Constants.PENJUAL).child(uid).child(Constants.AVATAR);
            UploadTask uploadTask = filePathAvatar.putBytes(bitmapDataUser);
            uploadTask.addOnSuccessListener(listener);

            //restart bitmap
            Constants.USER_FILE_PATH = null;
        }
    }

}

