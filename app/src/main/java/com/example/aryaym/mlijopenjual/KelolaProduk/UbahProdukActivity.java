package com.example.aryaym.mlijopenjual.KelolaProduk;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.aryaym.mlijopenjual.Base.BaseActivity;
import com.example.aryaym.mlijopenjual.R;
import com.example.aryaym.mlijopenjual.Utils.Constants;
import com.example.aryaym.mlijopenjual.Utils.ShowSnackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UbahProdukActivity extends BaseActivity implements View.OnClickListener{

    @BindView(R.id.input_nama_produk)
    EditText inputNamaProduk;
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
    @BindView(R.id.nama_satuan_view)
    TextView namaSatuanView;

    private ProdukModel produkModel;
    private DatabaseReference mDatabase;
    private ArrayAdapter<String> spinnerSatuanAdapter;
    private String namaSatuan;
    private String namaProduk,  satuanProduk, deskripsiProduk;
    private Double hargaProduk;
    private boolean isSpinnerTouched = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubah_produk);
        ButterKnife.bind(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.title_activity_ubah_produk);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        //get intent data
        produkModel = (ProdukModel) getIntent().getSerializableExtra(Constants.PRODUK);
        loadData();
        spinnerData();
        btnSimpan.setOnClickListener(this);
        btnBatal.setOnClickListener(this);
    }

    private void spinnerData() {
        //spinnerSatuan
        spinnerSatuanAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.arrSatuan));
        spinnerSatuanAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spnNamaSatuan.setAdapter(spinnerSatuanAdapter);
        spnNamaSatuan.setSelected(false);
        spnNamaSatuan.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                isSpinnerTouched = true;
                return false;
            }
        });
        spnNamaSatuan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!isSpinnerTouched){
                    ((TextView) view).setText(null);
                }else {
                    ((TextView) view).setText(null);
                    namaSatuanView.setText(spnNamaSatuan.getSelectedItem().toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void loadData() {
        try {
            mDatabase.child(Constants.PRODUK_REGULER).child(produkModel.getKategoriProduk()).child(produkModel.getIdProduk()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot != null) {
                        ProdukModel produkModel = dataSnapshot.getValue(ProdukModel.class);
                        if (produkModel != null) {
                            inputNamaProduk.setText(produkModel.getNamaProduk());
                            //image
                            inputHargaProduk.setText(rupiah().format(produkModel.getHargaProduk()) );
                            inputNominalSatuan.setText(produkModel.getSatuanProduk());
                            namaSatuanView.setText(produkModel.getNamaSatuan());
                            inputDeskripsiProduk.setText(produkModel.getDeskripsiProduk());
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } catch (Exception e) {
            ShowSnackbar.showSnack(this, "error");
        }
    }

    private void perbaruiProduk(){
        namaProduk = inputNamaProduk.getText().toString();
        hargaProduk = Double.parseDouble(inputHargaProduk.getText().toString());
        satuanProduk = inputNominalSatuan.getText().toString() ;
        deskripsiProduk = inputDeskripsiProduk.getText().toString();
        namaSatuan = namaSatuanView.getText().toString();
        simpanProduk(namaProduk, hargaProduk, satuanProduk, deskripsiProduk, namaSatuan);
    }

    private void simpanProduk(String namaProduk, Double hargaProduk, String satuanProduk, String deskripsiProduk, String namaSatuan){
        long waktuDibuat = new Date().getTime();
        HashMap<String, Object> dataProduk = new HashMap<>();
        dataProduk.put(Constants.WAKTU_DIBUAT, waktuDibuat);
        dataProduk.put(Constants.NAMAPRODUK, namaProduk);
        dataProduk.put(Constants.HARGAPRODUK, hargaProduk);
        dataProduk.put(Constants.DIGITSATUAN, satuanProduk);
        dataProduk.put(Constants.NAMASATUAN, namaSatuan);
        dataProduk.put(Constants.DESKRIPSI, deskripsiProduk);
        mDatabase.child(Constants.PRODUK_REGULER).child(produkModel.getKategoriProduk()).child(produkModel.getIdProduk()).updateChildren(dataProduk);
        showProgessDialog();
    }

    @Override
    public void onClick(View v) {
        if (v == btnSimpan){
            perbaruiProduk();
            finish();
        }else if (v == btnBatal){

        }
    }
}
