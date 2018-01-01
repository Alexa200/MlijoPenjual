package com.example.aryaym.mlijopenjual.KelolaProduk;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
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
import com.example.aryaym.mlijopenjual.Base.InternetConnection;
import com.example.aryaym.mlijopenjual.KelolaProduk.service.UploadPhotoThread;
import com.example.aryaym.mlijopenjual.KelolaProduk.service.UploadPhotoThreadListener;
import com.example.aryaym.mlijopenjual.R;
import com.example.aryaym.mlijopenjual.Utils.Constants;
import com.example.aryaym.mlijopenjual.Utils.ShowAlertDialog;
import com.example.aryaym.mlijopenjual.Utils.ShowSnackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.gun0912.tedpicker.Config;
import com.gun0912.tedpicker.ImagePickerActivity;

import java.util.ArrayList;
import java.util.Date;
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
    @BindView(R.id.activity)
    LinearLayout activity;

    private ArrayAdapter<String> spinnerKategoriAdapter, spinnerSatuanAdapter;

    private String namaProduk, kategoriProduk, namaSatuan, satuanProduk, deskripsiProduk;
    private Double hargaProduk, hargaDouble;
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

        handleDataType();
        handleSpinner();
    }

    public void handleDataType() {
        try {
            hargaDouble = Double.parseDouble(inputHargaProduk.getText().toString());
        } catch (NumberFormatException e) {
            hargaDouble = Double.valueOf(0);
        }
    }

    private void handleSpinner() {
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
        config.setSelectionLimit(4);
        config.setToolbarTitleRes(R.string.chooseImage);

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

    private boolean cekKolomIsian() {
        boolean hasil;
        if (TextUtils.isEmpty(inputNamaProduk.getText()) || TextUtils.isEmpty(inputHargaProduk.getText()) || TextUtils.isEmpty(inputNominalSatuan.getText()) ||
                TextUtils.isEmpty(inputDeskripsiProduk.getText())) {
            hasil = false;
        } else {
            hasil = true;
        }
        return hasil;
    }

    private void simpanProduk() {
        namaProduk = inputNamaProduk.getText().toString();
        hargaProduk = hargaDouble;
        satuanProduk = inputNominalSatuan.getText().toString();
        deskripsiProduk = inputDeskripsiProduk.getText().toString();

        if (cekKolomIsian() == true) {
            buatProdukBaru(namaProduk, hargaProduk, satuanProduk, deskripsiProduk);
        } else {
            ShowAlertDialog.showAlert("Anda harus mengisi semua Form yang tersedia !", this);
        }
    }

    private void buatProdukBaru(String namaProduk, Double hargaProduk, String satuanProduk, String deskripsiProduk) {
        if (InternetConnection.getInstance().isOnline(TambahProdukActivity.this)) {
            try {
                long waktuDibuat = new Date().getTime();
                String pushId = mDatabase.child(Constants.PRODUK_REGULER).child(kategoriProduk).push().getKey();
                final String idProduk = pushId;
                HashMap<String, Object> dataProduk = new HashMap<>();
                dataProduk.put(Constants.ID_PENJUAL, getUid());
                dataProduk.put(Constants.WAKTU_DIBUAT, waktuDibuat);
                dataProduk.put(Constants.NAMAPRODUK, namaProduk);
                dataProduk.put(Constants.ID_KATEGORI, kategoriProduk);
                dataProduk.put(Constants.HARGAPRODUK, hargaProduk);
                dataProduk.put(Constants.DIGITSATUAN, satuanProduk);
                dataProduk.put(Constants.NAMASATUAN, namaSatuan);
                dataProduk.put(Constants.ID_PRODUK, idProduk);
                dataProduk.put(Constants.GAMBARPRODUK, produk.getGambarProduk());
                dataProduk.put(Constants.DESKRIPSI, deskripsiProduk);
                mDatabase.child(Constants.PRODUK_REGULER).child(kategoriProduk).child(idProduk).setValue(dataProduk);
                showProgessDialog();

                if (listImage.size() == 0) {
                    ShowAlertDialog.showAlert("Anda harus memilih gambar produk minimal (1)!", this);
                } else {

                    UploadPhotoThreadListener uploadPhotoThreadListener = new UploadPhotoThreadListener() {
                        @Override
                        public void onUploadPhotoSuccess(ArrayList<String> photoUrls) {
                            Map<String, Object> updateImage = new HashMap<>();
                            updateImage.put(Constants.GAMBARPRODUK, photoUrls);
                            mDatabase.child(Constants.PRODUK_REGULER).child(kategoriProduk).child(idProduk).updateChildren(updateImage);

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
            } catch (Exception e) {
                ShowSnackbar.showSnack(this, getResources().getString(R.string.msg_error));
            }
        } else {
            final Snackbar snackbar = Snackbar.make(activity, getResources().getString(R.string.msg_noInternet), Snackbar.LENGTH_INDEFINITE);
                    snackbar.setAction(getResources().getString(R.string.ok), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            snackbar.dismiss();
                        }
                    });
            snackbar.show();
        }
    }

    // [END write_fan_out]
    @Override
    public void onClick(View view) {
        if (view == btnUpload) {
            if (verifyStoragePermission()) {
                addPhoto();
            }
        } else if (view == btnSimpan) {
            simpanProduk();
        }
    }
}
