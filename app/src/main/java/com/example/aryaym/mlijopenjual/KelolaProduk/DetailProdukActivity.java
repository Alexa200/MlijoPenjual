package com.example.aryaym.mlijopenjual.KelolaProduk;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.aryaym.mlijopenjual.Base.InternetConnection;
import com.example.aryaym.mlijopenjual.R;
import com.example.aryaym.mlijopenjual.Utils.Constants;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailProdukActivity extends AppCompatActivity {


    @BindView(R.id.imgProduk)
    ImageView imgProduk;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.nama_produk_view)
    TextView namaProdukView;
    @BindView(R.id.harga_produk_view)
    TextView hargaProdukView;
    @BindView(R.id.kategori_produk_view)
    TextView kategoriProdukView;
    @BindView(R.id.satuan_produk_view)
    TextView satuanProdukView;
    @BindView(R.id.detail_produk_view)
    TextView detailProdukView;

    private DatabaseReference mProdukReference;
    private AdapterImagePager mViewImagePager;
    private ProdukModel produkModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_produk);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mProdukReference = FirebaseDatabase.getInstance().getReference();
        produkModel = (ProdukModel) getIntent().getSerializableExtra(Constants.PRODUK);
        initInfo();
    }

    private void initInfo(){
        if (InternetConnection.getInstance().isOnline(DetailProdukActivity.this)){
            if (produkModel.getImgProduk().size() > 0){
                imgProduk.setVisibility(View.GONE);
                mViewImagePager = new AdapterImagePager(this, produkModel.getImgProduk());
                viewPager.setAdapter(mViewImagePager);
            }else {
                viewPager.setVisibility(View.GONE);
                imgProduk.setImageResource(R.drawable.no_image);
            }
            mViewImagePager = new AdapterImagePager(this, produkModel.getImgProduk());
            viewPager.setAdapter(mViewImagePager);

            loadData();
        }
    }

    private void loadData() {
        try {
            setTitle(produkModel.getNamaProduk());
            namaProdukView.setText(produkModel.getNamaProduk());
            kategoriProdukView.setText(produkModel.getKategoriProduk());
            hargaProdukView.setText(produkModel.getHargaProduk().toString());
            satuanProdukView.setText(produkModel.getSatuanProduk() + " " + produkModel.getNamaSatuan());
            detailProdukView.setText(produkModel.getDeskripsiProduk());
        }catch (Exception e){

        }
    }
}
