package com.example.aryaym.mlijopenjual.Autentifikasi;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.aryaym.mlijopenjual.Base.BaseActivity;
import com.example.aryaym.mlijopenjual.MainActivity;
import com.example.aryaym.mlijopenjual.R;
import com.example.aryaym.mlijopenjual.Utils.Constants;
import com.example.aryaym.mlijopenjual.Utils.ShowAlertDialog;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DataUserBaruActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.input_nik)
    EditText inputNik;
    @BindView(R.id.input_nama_lengkap)
    EditText inputNamaLengkap;
    @BindView(R.id.input_alamat)
    EditText inputAlamat;
    @BindView(R.id.input_telepon)
    EditText inputTelepon;
    @BindView(R.id.btn_submit)
    Button btnSubmit;

    private String phoneNumber;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_user_baru);
        ButterKnife.bind(this);

        phoneNumber = getIntent().getStringExtra(Constants.TELPON);
        inputTelepon.setText(phoneNumber);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        btnSubmit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == btnSubmit){
            if (cekFillData()){
                submitData();
                startActivity(new Intent(DataUserBaruActivity.this, MainActivity.class));
                finish();
            }
        }
    }

    private boolean cekFillData(){
        boolean result = true;
        if (TextUtils.isEmpty(inputNik.getText()) || TextUtils.isEmpty(inputAlamat.getText()) || TextUtils.isEmpty(inputAlamat.getText()) ||
                TextUtils.isEmpty(inputTelepon.getText())) {
            result = false;
            ShowAlertDialog.showAlert("Wajib diisi", this);
        }
        return result;
    }

    private void submitData(){
        String dataNIK = inputNik.getText().toString();
        String dataNama = inputNamaLengkap.getText().toString();
        String dataAlamat = inputAlamat.getText().toString();
        String dataTelpon = inputTelepon.getText().toString();

        buatDataUser(dataNIK, dataNama, dataAlamat, dataTelpon);
    }

    private void buatDataUser(String NIK, String Nama, String Alamat, String Telpon){
        Map<String, Object> detailPenjualData = new HashMap<>();
        detailPenjualData.put(Constants.NIK, NIK);
        detailPenjualData.put(Constants.NAMA, Nama);
        detailPenjualData.put(Constants.ALAMAT, Alamat);
        detailPenjualData.put(Constants.TELPON, Telpon);
        mDatabase.child(Constants.PENJUAL).child(getUid()).child(Constants.DETAIL_PENJUAL).setValue(detailPenjualData);
        mDatabase.child(Constants.PENJUAL).child(getUid()).child(Constants.STATUS_BERJUALAN).setValue(false);
        mDatabase.child(Constants.PENJUAL).child(getUid()).child(Constants.STATUS_LOKASI).setValue(false);
        mDatabase.child(Constants.PENJUAL).child(getUid()).child(Constants.UID).setValue(getUid());
    }
}
