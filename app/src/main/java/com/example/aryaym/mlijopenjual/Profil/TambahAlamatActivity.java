package com.example.aryaym.mlijopenjual.Profil;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.aryaym.mlijopenjual.R;
import com.example.aryaym.mlijopenjual.Utils.ShowAlertDialog;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TambahAlamatActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.input_judul_alamat)
    EditText inputJudulAlamat;
    @BindView(R.id.input_alamat)
    EditText inputAlamat;
    @BindView(R.id.input_telepon)
    EditText inputTelepon;
    @BindView(R.id.btn_submit)
    Button btnSubmit;
    @BindView(R.id.input_nama_penerima)
    EditText inputNamaPenerima;

    DatabaseReference mDatabase;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_alamat);
        ButterKnife.bind(this);
    //    setTitle(R.string.title_activity_tambah_alamat);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        btnSubmit.setOnClickListener(this);
    }

    private boolean cekFillData() {
        boolean result = true;
        if (TextUtils.isEmpty(inputJudulAlamat.getText()) || TextUtils.isEmpty(inputAlamat.getText()) || TextUtils.isEmpty(inputNamaPenerima.getText()) ||
                TextUtils.isEmpty(inputTelepon.getText())) {
            result = false;
            ShowAlertDialog.showAlert("Wajib diisi", this);
        }
        return result;
    }

    private void submitData(){
        String judulAlamat = inputJudulAlamat.getText().toString();
        String alamat = inputAlamat.getText().toString();
        String namaPenerima = inputNamaPenerima.getText().toString();
        String telepon = inputTelepon.getText().toString();
     //   String uid = BaseActivity.getUid();
        
        buatAlamatBaru(judulAlamat, alamat, namaPenerima, telepon);
        
    }

    private void buatAlamatBaru(String judulAlamat, String alamat, String namaPenerima, String telepon ) {
//        String pushId = mDatabase.child(Constants.KONSUMEN).child(BaseActivity.getUid()).child(Constants.ALAMAT).push().getKey();
//        String alamatId = pushId;
//        Map<String, Object> alamatUser = new HashMap<>();
//        alamatUser.put(Constants.JUDUL_ALAMAT, judulAlamat);
//        alamatUser.put(Constants.ALAMAT, alamat);
//        alamatUser.put(Constants.NAMA_PENERIMA, namaPenerima);
//        alamatUser.put(Constants.TELPON, telepon);
//        alamatUser.put(Constants.ID_ALAMAT, alamatId);
//
//        mDatabase.child(Constants.KONSUMEN).child(BaseActivity.getUid()).child(Constants.ALAMAT_USER).child(pushId).setValue(alamatUser);
    }

    @Override
    public void onClick(View v) {
        if (v == btnSubmit) {
            if (cekFillData()){
                submitData();
                finish();
            }
        }
    }
}
