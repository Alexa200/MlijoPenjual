package com.example.aryaym.mlijopenjual.KelolaProduk;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.example.aryaym.mlijopenjual.Base.BaseActivity;
import com.example.aryaym.mlijopenjual.Base.ImageLoader;
import com.example.aryaym.mlijopenjual.KelolaProduk.service.UploadPhotoThread;
import com.example.aryaym.mlijopenjual.KelolaProduk.service.UploadPhotoThreadListener;
import com.example.aryaym.mlijopenjual.R;
import com.example.aryaym.mlijopenjual.Utils.Constants;
import com.example.aryaym.mlijopenjual.Utils.ShowAlertDialog;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.gun0912.tedpicker.Config;
import com.gun0912.tedpicker.ImagePickerActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TambahProdukActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "NewPostActivity";
    private static final int INTENT_REQUEST_GET_IMAGES = 13;
    @BindView(R.id.input_nama_produk)
    EditText inputNamaProduk;
    @BindView(R.id.input_kategori)
    Spinner spnKategoriProduk;
    @BindView(R.id.photoContainer)
    LinearLayout photoContainer;
    @BindView(R.id.btn_upload)
    ImageButton btnUpload;
    @BindView(R.id.imageContainer)
    HorizontalScrollView imageContainer;
    @BindView(R.id.input_harga_produk)
    EditText inputHargaProduk;
    @BindView(R.id.nominal_satuan)
    EditText inputNominalSatuan;
    @BindView(R.id.nama_satuan)
    Spinner spnNamaSatuan;
    @BindView(R.id.btn_simpan)
    Button btnSimpan;
    @BindView(R.id.btn_batal)
    Button btnBatal;
    @BindView(R.id.deskripsiProduk)
    EditText inputDeskripsiProduk;

    private ArrayAdapter<String> spinnerKategoriAdapter, spinnerSatuanAdapter;
    private String kategoriProduk, namaSatuan;
    private String namaProduk,  satuanProduk, deskripsiProduk;
    private Double hargaProduk;
    // [START declare_database_ref]
    private DatabaseReference mDatabase;
    // [END declare_database_ref

    private static final int REQUEST_CODE_READ_EXTERNAL_STORAGE = 1001;
    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private ArrayList<Uri> listImage = new ArrayList<>();
    private ProdukModel produk = new ProdukModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_produk);
        ButterKnife.bind(this);
        setTitle(R.string.title_activity_tambah_produk);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // [START initialize_database_ref]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END initialize_database_ref]

        btnUpload.setOnClickListener(this);
        btnSimpan.setOnClickListener(this);

        handleSpinner();
    }

    private void handleSpinner(){
        //spinnerKategori
        spinnerKategoriAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.arrKategori));
        spinnerKategoriAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        spnKategoriProduk.setAdapter(spinnerKategoriAdapter);
        spnKategoriProduk.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                String itemKategori = adapterView.getItemAtPosition(position).toString();
                kategoriProduk = itemKategori;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //spinnerSatuan
        spinnerSatuanAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.arrSatuan));
        spinnerSatuanAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        spnNamaSatuan.setAdapter(spinnerSatuanAdapter);
        spnNamaSatuan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                String itemSatuan = adapterView.getItemAtPosition(position).toString();
                namaSatuan = itemSatuan;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private boolean verifyStoragePermission() {
        int readPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (readPermission != PackageManager.PERMISSION_GRANTED) {
            //request permission
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_CODE_READ_EXTERNAL_STORAGE);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_READ_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    addPhoto();
                }
                break;
        }
    }

    private void addPhoto() {
        Config config = new Config();
        config.setSelectionMin(1);
        config.setSelectionLimit(10);

        ImagePickerActivity.setConfig(config);

        Intent myIntent = new Intent(TambahProdukActivity.this, ImagePickerActivity.class);
        myIntent.putParcelableArrayListExtra(ImagePickerActivity.EXTRA_IMAGE_URIS, listImage);
        startActivityForResult(myIntent, INTENT_REQUEST_GET_IMAGES);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == INTENT_REQUEST_GET_IMAGES && resultCode == RESULT_OK) {
            ArrayList<Uri> image_uris = data.getParcelableArrayListExtra(ImagePickerActivity.EXTRA_IMAGE_URIS);
            listImage.clear();
            for (Uri uri : image_uris) {
                //   String uriString = uri.toString();
                listImage.add(uri);
            }
            // mImage = data.getStringArrayListExtra(ImagePickerActivity.EXTRA_IMAGE_URIS);

            onPickImageSuccess();
        }
    }

    private void onPickImageSuccess() {
        int previewImageSize = getPixelValue(TambahProdukActivity.this, 150);
        photoContainer.removeAllViews();

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(previewImageSize, previewImageSize);
        params.setMargins(5, 0, 5, 0);

        for (Uri uri : listImage) {
            ImageView photo = new ImageView(this);
            photo.setScaleType(ImageView.ScaleType.CENTER_CROP);
            photo.setLayoutParams(params);
            //using Glide to load image
            ImageLoader.getInstance().loadImageOther(TambahProdukActivity.this, uri.toString(), photo);

            photoContainer.addView(photo);
        }
    }

    private int getPixelValue(Context context, int dimenId) {
        Resources resources = context.getResources();
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dimenId,
                resources.getDisplayMetrics()
        );
    }

    private boolean cekFillData(){
        boolean result = true;
        if (TextUtils.isEmpty(inputNamaProduk.getText()) || TextUtils.isEmpty(inputHargaProduk.getText())|| TextUtils.isEmpty(inputNominalSatuan.getText())||
                TextUtils.isEmpty(inputDeskripsiProduk.getText())){
            result = false;
            ShowAlertDialog.showAlert("Anda harus mengisi Form yang tersedia !", this);
        }
        return result;
    }

//    private void submitPost() {
//        namaProduk = inputNamaProduk.getText().toString();
//        hargaProduk = Double.parseDouble(inputHargaProduk.getText().toString());
//        satuanProduk = inputNominalSatuan.getText().toString() ;
//        deskripsiProduk = inputDeskripsiProduk.getText().toString();
//
//        //mDatabase.
//        final String userId = getUid();
//        mDatabase.child(Constants.PENJUAL).child(userId).addListenerForSingleValueEvent(
//                new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        // Get user value
//                        User user = dataSnapshot.getValue(User.class);
//
//                        // [START_EXCLUDE]
//                        if (user == null) {
//                            // User is null, error out
//                            Log.e(TAG, "User " + userId + " is unexpectedly null");
//                            Toast.makeText(TambahProdukActivity.this,
//                                    "Error: could not fetch user.",
//                                    Toast.LENGTH_SHORT).show();
//                        } else {
//                            // Write new post
//                            buatProdukBaru(userId, namaProduk, kategoriProduk, hargaProduk, satuanProduk, namaSatuan, deskripsiProduk);
//                          //  tambahPemilikProduk(idProduk, kategoriProduk);
//                        }
//
//                        // Finish this Activity, back to the stream
//                       // setEditingEnabled(true);
//                        finish();
//                        // [END_EXCLUDE]
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
//                        // [START_EXCLUDE]
//                       // setEditingEnabled(true);
//                        // [END_EXCLUDE]
//                    }
//                });
//        // [END single_value_read]
//    }
//
//    private void buatProdukBaru(final String userId, final String namaProduk, final String kategoriProduk, final Double hargaProduk, final String satuanProduk, final String namaSatuan, final String deskripsiProduk) {
//        final String key = mDatabase.child(Constants.PRODUK).push().getKey();
//        final String idProduk = key;
//        final ArrayList<String> imgURL = new ArrayList<String>();
//        final CountDownLatch uploadDone = new CountDownLatch(listImage.size()); //Multi thread sync
//        totalImage = 0;
//        for (int img=0; img<listImage.size(); img++){
//            String filepath = String.valueOf(listImage.get(img));
//            Uri file = Uri.fromFile(new File(filepath));
//            StorageReference imageRef = mFirebaseStorage.child(Constants.IMAGES).child(idProduk);
//            UploadTask uploadTask = imageRef.child(System.currentTimeMillis() + "." +
//                    MimeTypeMap.getFileExtensionFromUrl(file.getLastPathSegment())).putFile(file);
//
//            uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
//                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
////                    System.out.println("Upload is " + progress + "% done");
//                        mProgressDialog.setMessage("" + (totalImage + 1) + "/" +
//                        listImage.size() + " " + progress + "%");
//                }
//            }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
//                    System.out.println("Upload is paused");
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    uploadDone.countDown();
//                }
//            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                    // Handle successful uploads on complete
//                    uploadDone.countDown();
//                    imgURL.add(taskSnapshot.getDownloadUrl().toString());
//
//                    if (totalImage ==(listImage.size() - 1)){
//                        ArrayList<String> imgProduk = imgURL;
//                        ProdukModel produkModel = new ProdukModel(userId, namaProduk, kategoriProduk, imgProduk, hargaProduk, satuanProduk, namaSatuan, deskripsiProduk, idProduk);
//                        Map<String, Object> postValues = produkModel.toProduk();
//
//                        mDatabase.child(Constants.PRODUK).child(kategoriProduk).child(key).setValue(postValues);
//                    }
//                    totalImage +=1;
//                }
//            });
//        }
//        //Wait until upload images done!
//      //  uploadDone.await();
//        ProdukModel produkModel = new ProdukModel(idProduk, kategoriProduk);
//        Map<String, Object> postValues = produkModel.toPenjual();
//
//        mDatabase.child(Constants.PENJUAL).child(getUid()).child(Constants.PRODUK).child(idProduk).setValue(postValues);
//
//    }

    private void submitPost(){
        final ProdukModel produkModel = new ProdukModel();
        namaProduk = inputNamaProduk.getText().toString();
        hargaProduk = Double.parseDouble(inputHargaProduk.getText().toString());
        satuanProduk = inputNominalSatuan.getText().toString() ;
        deskripsiProduk = inputDeskripsiProduk.getText().toString();

        buatProdukBaru(namaProduk, hargaProduk, satuanProduk, deskripsiProduk);
//        UploadPhotoThreadListener uploadPhotoThreadListener = new UploadPhotoThreadListener() {
//            @Override
//            public void onUploadPhotoSuccess(ArrayList<String> photoUrls) {
//                Map<String, Object> updateImage = new HashMap<>();
//                updateImage.put(Constants.IMAGES, photoUrls);
//                mDatabase.child(Constants.PRODUK).child(kategoriProduk).child(ProdukModel).updateChildren(updateImage);
//
//                tambahProdukPenjual();
//            }
//        };
//        new UploadPhotoThread(produk.getIdProduk(), listImage, uploadPhotoThreadListener).execute();

        // Finish this Activity, back to the stream

//        for (int img=0; img<listImage.size(); img++){
//            String filepath = String.valueOf(listImage.get(img));
//            Uri file = Uri.fromFile(new File(filepath));
//            StorageReference imageRef = mFirebaseStorage.child(Constants.IMAGES).child(produk.getIdProduk());
//            UploadTask uploadTask = imageRef.child(System.currentTimeMillis() + "." +
//                    MimeTypeMap.getFileExtensionFromUrl(file.getLastPathSegment())).putFile(file);
//
//            uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
//                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
////                    System.out.println("Upload is " + progress + "% done");
//                        mProgressDialog.setMessage("" + (totalImage + 1) + "/" +
//                        listImage.size() + " " + progress + "%");
//                }
//            }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
//                    System.out.println("Upload is paused");
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    uploadDone.countDown();
//                }
//            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                    // Handle successful uploads on complete
//                    uploadDone.countDown();
//                    imgURL.add(taskSnapshot.getDownloadUrl().toString());
//
//                    if (totalImage ==(listImage.size() - 1)){
//                        ArrayList<String> imgUri = imgURL;
//                        //ProdukModel produkModel = new ProdukModel(userId, namaProduk, kategoriProduk, imgProduk, hargaProduk, satuanProduk, namaSatuan, deskripsiProduk, idProduk);
//                        Map<String, Object> postValues = new HashMap<>();
//                        postValues.put(Constants.IMAGES, imgUri);
//                        mDatabase.child(Constants.PRODUK).child(kategoriProduk).child(produk.getIdProduk()).setValue(postValues);
//                    }
//                    totalImage +=1;
//                }
//            });
//        }

    }

    private void buatProdukBaru(String namaProduk, Double hargaProduk, String satuanProduk, String deskripsiProduk){
        String pushId = mDatabase.child(Constants.PRODUK).child(kategoriProduk).push().getKey();
        final String idProduk = pushId;
        HashMap<String, Object> dataProduk = new HashMap<>();
        dataProduk.put(Constants.ID_PENJUAL, getUid());
        dataProduk.put(Constants.NAMAPRODUK, namaProduk);
        dataProduk.put(Constants.ID_KATEGORI, kategoriProduk);
        dataProduk.put(Constants.HARGAPRODUK, hargaProduk);
        dataProduk.put(Constants.DIGITSATUAN, satuanProduk);
        dataProduk.put(Constants.NAMASATUAN, namaSatuan);
        dataProduk.put(Constants.ID_PRODUK,idProduk);
        dataProduk.put(Constants.IMAGES, produk.getImgProduk());
        dataProduk.put(Constants.DESKRIPSI, deskripsiProduk);
        mDatabase.child(Constants.PRODUK).child(kategoriProduk).child(idProduk).setValue(dataProduk);
        showProgessDialog();

        UploadPhotoThreadListener uploadPhotoThreadListener = new UploadPhotoThreadListener() {
            @Override
            public void onUploadPhotoSuccess(ArrayList<String> photoUrls) {
                Map<String, Object> updateImage = new HashMap<>();
                updateImage.put(Constants.IMAGES, photoUrls);
                mDatabase.child(Constants.PRODUK).child(kategoriProduk).child(idProduk).updateChildren(updateImage);

                showProgessDialog();
                HashMap<String, Object> data = new HashMap<>();
                data.put(Constants.ID_KATEGORI, kategoriProduk);
                data.put(Constants.ID_PRODUK, idProduk);
                mDatabase.child(Constants.PENJUAL).child(getUid()).child(Constants.PRODUK).child(idProduk).setValue(data);
            }
        };
        new UploadPhotoThread(idProduk, listImage, uploadPhotoThreadListener).execute();

        hideProgressDialog();
        finish();
    }

    // [END write_fan_out]
    @Override
    public void onClick(View view) {
        if (view == btnUpload) {
            if (verifyStoragePermission()) {
                addPhoto();
            }
        } else if (view == btnSimpan) {
            if (cekFillData()){
                showProgessDialog();
                submitPost();
            }
        }
    }
}
