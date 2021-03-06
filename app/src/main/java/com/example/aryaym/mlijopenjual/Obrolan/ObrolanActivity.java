package com.example.aryaym.mlijopenjual.Obrolan;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.aryaym.mlijopenjual.Base.BaseActivity;
import com.example.aryaym.mlijopenjual.Base.InternetConnection;
import com.example.aryaym.mlijopenjual.R;
import com.example.aryaym.mlijopenjual.Utils.Constants;
import com.example.aryaym.mlijopenjual.Utils.EncodeImage;
import com.example.aryaym.mlijopenjual.Utils.ShowSnackbar;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ObrolanActivity extends BaseActivity implements View.OnClickListener{

    @BindView(R.id.recycler_chat)
    RecyclerView recyclerChat;
    @BindView(R.id.btn_tambah_img)
    ImageView btnTambahFoto;
    @BindView(R.id.input_pesan)
    EditText inputPesan;
    @BindView(R.id.btn_kirim)
    ImageView btnKirim;

    private ObrolanPresenter presenter;
    private ObrolanAdapter obrolanAdapter;
    private List<ObrolanModel> obrolanList = new ArrayList<>();
    private String penerimaId = "";
    private String deviceToken, namaPenerima, avatar;
    private DatabaseReference mDatabase;
   // private KonsumenModel konsumenModel;
    private Uri mUri;

    private static final int REQUEST_CODE_READ_EXTERNAL_STORAGE = 1001;
    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    private static final int REQUEST_CODE_CAMERA = 1002;
    private static final String[] PERMISSIONS_CAMERA = {
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static int RESULT_LOAD_IMAGE = 1;
    private StorageReference mStorage;
    private LinearLayoutManager linearLayoutManager;
    //private SessionManagerUser sessionManagerUser;
    private HashMap<String, String> hashDataUser = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_obrolan);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        namaPenerima = getIntent().getStringExtra(Constants.NAMA);
        getSupportActionBar().setTitle(namaPenerima);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance().getReference();
        //konsumenModel = (KonsumenModel) getIntent().getSerializableExtra(Constants.KONSUMEN_MODEL);
//        penerimaId = konsumenModel.getUid();
//        deviceToken = konsumenModel.getDeviceToken();
        penerimaId = getIntent().getStringExtra(Constants.UID);
        avatar = getIntent().getStringExtra(Constants.AVATAR);
    //    deviceToken = getIntent().getStringExtra(Constants.DEVICE_TOKEN);
        //init
        presenter = new ObrolanPresenter(this);
        obrolanAdapter = new ObrolanAdapter(this, obrolanList, avatar);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerChat.setLayoutManager(linearLayoutManager);
        recyclerChat.setAdapter(obrolanAdapter);
        //load data chat
        loadDataObrolan();

        btnKirim.setOnClickListener(this);
        btnTambahFoto.setOnClickListener(this);
        inputPesan.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0){
                    btnKirim.setVisibility(View.GONE);
                }else {
                    btnKirim.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void loadDataObrolan(){
        try {
            presenter.loadData(getUid(), penerimaId).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    try {
                        if (dataSnapshot != null) {
                            ObrolanModel message = dataSnapshot.getValue(ObrolanModel.class);
                            if (message != null) {
                                obrolanList.add(message);
                                recyclerChat.scrollToPosition(obrolanList.size() - 1);
                                obrolanAdapter.notifyDataSetChanged();

                            }
                        }
                    } catch (Exception e) {
                       // ShowSnackbar.showSnack(ObrolanActivity.this, getResources().getString(R.string.error));
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    try {
                        if (dataSnapshot != null) {
                            ObrolanModel messageUpdate = dataSnapshot.getValue(ObrolanModel.class);
                            if (messageUpdate != null) {
                                for (int i = obrolanList.size() - 1; i >= 0; i--) {
                                    if (messageUpdate.getTimestamp() == obrolanList.get(i).getTimestamp() && messageUpdate.getPengirim().equals(obrolanList.get(i).getPengirim()) &&
                                            messageUpdate.getPenerima().equals(obrolanList.get(i).getPenerima())) {
                                        obrolanList.set(i, messageUpdate);
                                        obrolanAdapter.notifyDataSetChanged();
                                        break;
                                    }
                                }
                            }

                        }
                    } catch (Exception e) {
                        //ShowSnackbar.showSnack(ChatActivity.this, getResources().getString(R.string.error));
                    }
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } catch (Exception e) {
          //  ShowSnackbar.showSnack(ChatActivity.this, getResources().getString(R.string.error));
        }
    }

    @Override
    public void onClick(View v) {
        if (v == btnTambahFoto){
            showMenuTambahFoto(v);
        }else if (v == btnKirim){
            if (InternetConnection.getInstance().isOnline(ObrolanActivity.this)){
                tambahDataObrolan();
                buatNotifikasiKeKonsumen();
            }else {
                ShowSnackbar.showSnack(ObrolanActivity.this, "Tidak ada koneksi");
            }
        }
    }

    @SuppressLint("RestrictedApi")
    private void showMenuTambahFoto(View v) {
        MenuBuilder menuBuilder = new MenuBuilder(this);
        MenuInflater menuInflater = new MenuInflater(this);
        menuInflater.inflate(R.menu.menu_tambah_foto_obrolan, menuBuilder);
        MenuPopupHelper menuPopupHelper = new MenuPopupHelper(this, menuBuilder, v);
        menuPopupHelper.setForceShowIcon(true);
        menuPopupHelper.show();
        //item click
        menuBuilder.setCallback(new MenuBuilder.Callback() {
            @Override
            public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {
                if (item.getItemId() == R.id.action_show_gallery) {
                    if (verifyStoragePermissions()) {
                        showGallery();
                    }
                } else if (item.getItemId() == R.id.action_open_camera) {
                    if (verifyOpenCamera()) {
                        openCamera();
                    }
                }
                return true;
            }

            @Override
            public void onMenuModeChange(MenuBuilder menu) {

            }
        });
        //show menu


    }

    private void showGallery() {
        Intent intent = new Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, RESULT_LOAD_IMAGE);
    }

    private boolean verifyStoragePermissions() {
        // Check if we have read or write permission
        int readPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (readPermission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    REQUEST_CODE_READ_EXTERNAL_STORAGE
            );

            return false;
        }

        return true;
    }

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

    private void openCamera() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "Image File Name");
        mUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mUri);
        startActivityForResult(cameraIntent, Constants.CAMERA_INTENT);
    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults) {
        switch (permsRequestCode) {
            case REQUEST_CODE_READ_EXTERNAL_STORAGE:
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK) {
            if (InternetConnection.getInstance().isOnline(ObrolanActivity.this)) {
                try {
                    if (data.getData() != null) {
                        //save data first
                        final ObrolanModel obrolanModel = new ObrolanModel("https://firebasestorage.googleapis.com/v0/b/mlijo-5640f.appspot.com/o/no_img.png?alt=media&token=b8abf803-1e79-4e4f-8c62-9b6cffc64e90", true, new Date().getTime(), getUid());
                        // final String key = String.format("%s%d", getUid(), new Date().getTime());
                        final String key = mDatabase.child(Constants.OBROLAN).child(getUid()).child(penerimaId).push().getKey();
                        tambahData(key, getUid(), penerimaId, obrolanModel, true);
                        tambahData(key, penerimaId, getUid(), obrolanModel, false);
                        //update last message
                        updateObrolanKonsumen(getUid(), penerimaId, obrolanModel);
                        updateObrolanPenjual(getUid(), penerimaId, obrolanModel);

                        byte[] arrImageBytes = EncodeImage.encodeImage(getRealPathFromURI(data.getData()));
                        String fileName = String.format("%d%s", new Date().getTime(), getUid());
                        StorageReference storageForUpFile = mStorage.child(Constants.OBROLAN).child(fileName);
                        UploadTask uploadTask = storageForUpFile.putBytes(arrImageBytes);
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("CHAT_IMAGE", e.getMessage());
                            }
                        });
                        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                //update message
                                updateMessage(taskSnapshot.getDownloadUrl().toString(), key, getUid(), penerimaId);
                                updateMessage(taskSnapshot.getDownloadUrl().toString(), key, penerimaId, getUid());
                            }
                        });
                    } else {
                        ShowSnackbar.showSnack(this, getResources().getString(R.string.msg_retry));
                    }
                } catch (Exception e) {
                    ShowSnackbar.showSnack(this, getResources().getString(R.string.msg_retry));
                }
            } else {
                ShowSnackbar.showSnack(ObrolanActivity.this, getResources().getString(R.string.msg_noInternet));
            }
        } else if (requestCode == Constants.CAMERA_INTENT && resultCode == RESULT_OK) {
            if (InternetConnection.getInstance().isOnline(ObrolanActivity.this)) {
                try {
                    if (mUri != null) {
                        //save data first
                        final ObrolanModel obrolanModel = new ObrolanModel("https://firebasestorage.googleapis.com/v0/b/mlijo-5640f.appspot.com/o/no_img.png?alt=media&token=b8abf803-1e79-4e4f-8c62-9b6cffc64e90", true, new Date().getTime(), getUid());
                        // final String key = String.format("%s%d", getUid(), new Date().getTime());
                        final String key = mDatabase.child(Constants.OBROLAN).child(getUid()).child(penerimaId).push().getKey();
                        tambahData(key, getUid(), penerimaId, obrolanModel, true);
                        tambahData(key, penerimaId, getUid(), obrolanModel, false);
                        //update last message
                        updateObrolanKonsumen(getUid(), penerimaId, obrolanModel);
                        updateObrolanPenjual(getUid(), penerimaId, obrolanModel);

                        byte[] arrImageBytes = EncodeImage.encodeImage(getRealPathFromURI(mUri));
                        String fileName = String.format("%d%s", new Date().getTime(), getUid());
                        StorageReference storageForUpFile = mStorage.child(Constants.OBROLAN).child(fileName);
                        UploadTask uploadTask = storageForUpFile.putBytes(arrImageBytes);
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("CHAT_IMAGE", e.getMessage());
                            }
                        });
                        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                //update message
                                updateMessage(taskSnapshot.getDownloadUrl().toString(), key, getUid(), penerimaId);
                                updateMessage(taskSnapshot.getDownloadUrl().toString(), key, penerimaId, getUid());
                            }
                        });
                    } else {
                        ShowSnackbar.showSnack(this, getResources().getString(R.string.msg_retry));
                    }

                } catch (Exception e) {
                    ShowSnackbar.showSnack(this, getResources().getString(R.string.msg_retry));
                }
            } else {
                ShowSnackbar.showSnack(ObrolanActivity.this, getResources().getString(R.string.msg_noInternet));
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    private void tambahData(String key, String pengirimId, String penerimaId, ObrolanModel obrolanModel, boolean kontenMilikku) {
        try {
            obrolanModel.setKontenMilikku(kontenMilikku);
            obrolanModel.setPenerima(penerimaId);
            Map<String, Object> data = obrolanModel.toMap();
            mDatabase.child(Constants.OBROLAN).child(pengirimId).child(penerimaId).child(key).setValue(data);
        } catch (Exception e) {
            Log.d("CHAT", e.getMessage());
        }
    }

    private void tambahDataObrolan() {
        try {
            String konten = inputPesan.getText().toString();
            long timestamp = new Date().getTime();
            ObrolanModel obrolan = new ObrolanModel(konten, false, timestamp, getUid());
            obrolan.setDisplayStatus(false);
            //add pesan
            presenter.tambahObrolan(penerimaId, obrolan);
            recyclerChat.smoothScrollToPosition(obrolanAdapter.getItemCount());
            //clear data
            inputPesan.setText("");
        }catch (Exception e){

        }
    }

    public void updateObrolanKonsumen(String pengirimId, String penerimaId, ObrolanModel obrolanModel){
        try {
            ObrolanTerakhir obrolanTerakhir = new ObrolanTerakhir(penerimaId, obrolanModel.getTimestamp()* -1);
            Map<String, Object> data = obrolanTerakhir.toMap();
            mDatabase.child(Constants.KONSUMEN).child(penerimaId).child(Constants.OBROLAN).child(pengirimId).updateChildren(data);
        }catch (Exception e){

        }
    }
    public void updateObrolanPenjual(String pengirimId, String penerimaId, ObrolanModel obrolanModel){
        try {
            ObrolanTerakhir obrolanTerakhir = new ObrolanTerakhir(penerimaId, obrolanModel.getTimestamp()* -1);
            Map<String, Object> data = obrolanTerakhir.toMap();
            mDatabase.child(Constants.PENJUAL).child(pengirimId).child(Constants.OBROLAN).child(penerimaId).updateChildren(data);
        }catch (Exception e){

        }
    }

    private void updateMessage(String data, String key, String currentId, String partnerId) {
        Map<String, Object> dataUpdate = new HashMap<>();
        dataUpdate.put(Constants.KONTEN, data);
        mDatabase.child(Constants.OBROLAN).child(currentId).child(partnerId).child(key).updateChildren(dataUpdate);
    }

    private void buatNotifikasiKeKonsumen() {
        String key = mDatabase.child(Constants.NOTIFIKASI).child("konsumen").child(Constants.OBROLAN).child(penerimaId).push().getKey();
        mDatabase.child(Constants.NOTIFIKASI).child("konsumen").child(Constants.OBROLAN).child(penerimaId).child(key).child("pengirimId").setValue(getUid());
    }
}
