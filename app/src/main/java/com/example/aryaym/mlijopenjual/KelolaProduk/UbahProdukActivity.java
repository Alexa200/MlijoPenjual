package com.example.aryaym.mlijopenjual.KelolaProduk;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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

import butterknife.BindView;
import butterknife.ButterKnife;

public class UbahProdukActivity extends BaseActivity {

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
    @BindView(R.id.input_kategori_view)
    TextView inputKategoriView;
    @BindView(R.id.nama_satuan_view)
    TextView namaSatuanView;

    private ProdukModel produkModel;
    private DatabaseReference mDatabase;
    private ArrayAdapter<String> spinnerKategoriAdapter, spinnerSatuanAdapter;
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
    }

    private void spinnerData() {
        //spinnerKategori
        spinnerKategoriAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.arrKategori));
        spinnerKategoriAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spnKategoriProduk.setAdapter(spinnerKategoriAdapter);
        spnKategoriProduk.setSelected(false);
        spnKategoriProduk.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                isSpinnerTouched = true;
                return false;
            }
        });
        spnKategoriProduk.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!isSpinnerTouched) {
                    ((TextView) view).setText(null);
                } else {
                    ((TextView) view).setText(null);
                    inputKategoriView.setText(spnKategoriProduk.getSelectedItem().toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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
                            inputKategoriView.setText(produkModel.getKategoriProduk());
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
}
