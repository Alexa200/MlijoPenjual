package com.example.aryaym.mlijopenjual.KelolaPenjualan;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.aryaym.mlijopenjual.Base.BaseActivity;
import com.example.aryaym.mlijopenjual.Base.ImageLoader;
import com.example.aryaym.mlijopenjual.InformasiKonsumen.KonsumenModel;
import com.example.aryaym.mlijopenjual.InformasiKonsumen.LokasiKonsumenActivity;
import com.example.aryaym.mlijopenjual.KelolaProduk.ProdukModel;
import com.example.aryaym.mlijopenjual.Obrolan.ObrolanActivity;
import com.example.aryaym.mlijopenjual.R;
import com.example.aryaym.mlijopenjual.Utils.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by AryaYM on 25/09/2017.
 */

public class DetailTransaksiActivity extends BaseActivity implements View.OnClickListener {


    @BindView(R.id.imgProduk)
    ImageView imgProduk;
    @BindView(R.id.txtNamaProduk)
    TextView txtNamaProduk;
    @BindView(R.id.jml_item_produk)
    TextView jmlItemProduk;
    @BindView(R.id.total_harga_produk)
    TextView totalHargaProduk;
    @BindView(R.id.catatan_pembeli)
    TextView catatanPembeli;
    @BindView(R.id.nama_penerima)
    TextView namaPenerima;
    @BindView(R.id.alamat_lengkap)
    TextView alamatLengkap;
    @BindView(R.id.nomortelp_penerima)
    TextView telpPenerima;
    @BindView(R.id.status_transaksi)
    TextView statusTransaksi;
    @BindView(R.id.input_biaya_kirim)
    EditText inputBiayaKirim;
    @BindView(R.id.txt_biaya_kirim)
    TextView txtBiayaKirim;
    @BindView(R.id.status_layout)
    LinearLayout statusLayout;
    @BindView(R.id.btn_tolak_pesanan)
    Button btnTolakPesanan;
    @BindView(R.id.btn_terima_pesanan)
    Button btnTerimaPesanan;
    @BindView(R.id.btn_perbarui_status)
    Button btnPerbaruiStatus;
    @BindView(R.id.penerima_layout)
    LinearLayout penerimaLayout;
    @BindView(R.id.input_penerima_pesanan)
    EditText inputPenerimaPesanan;
    @BindView(R.id.txt_penerima_pesanan)
    TextView txtPenerimaPesanan;
    @BindView(R.id.txt_harga_produk)
    TextView txtHargaProduk;
    @BindView(R.id.txt_satuan_digit)
    TextView txtSatuanDigit;
    @BindView(R.id.txt_satuan)
    TextView txtSatuan;
    @BindView(R.id.input_total_harga)
    EditText inputTotalHarga;

    private static final String TAG = "DetailTransaksiActivity";
    @BindView(R.id.btn_kirim_pesan)
    Button btnKirimPesan;
    @BindView(R.id.btn_lihat_peta)
    Button btnLihatPeta;

    private DatabaseReference mDatabase;
    private TransaksiModel transaksiModel;

    int getStatus, statusBaru;
    double biayaKirim, totalHarga;
    String nama_penerima;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_transaksi);
        ButterKnife.bind(this);
        setTitle(R.string.title_activity_detail_transaksi);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnTerimaPesanan.setOnClickListener(this);
        btnPerbaruiStatus.setOnClickListener(this);
        btnTolakPesanan.setOnClickListener(this);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        transaksiModel = (TransaksiModel) getIntent().getSerializableExtra(Constants.TRANSAKSI);

        initInfo();
        loadDataPesanan();
        loadDataProduk();
        loadDataAlamat();

    }

    private void initInfo() {
        if (transaksiModel.getStatusTransaksi() == 1) {
            statusTransaksi.setText(Constants.MENUNGGU);
            tombolKonfirmasiTransaksi();
            Log.d(TAG, "data" + transaksiModel.getTipeTransaksi());
            if (transaksiModel.getTipeTransaksi().equals(Constants.PRODUK_REGULER)) {
                inputTotalHarga.setVisibility(View.GONE);
                totalHargaProduk.setVisibility(View.VISIBLE);
                inputBiayaKirim.setVisibility(View.VISIBLE);
                txtBiayaKirim.setVisibility(View.GONE);
            } else if (transaksiModel.getTipeTransaksi().equals(Constants.PRODUK_KHUSUS)) {
                tampilInputBiaya();
            }
            //tampilInputBiaya();

            statusLayout.setVisibility(View.GONE);
        } else if (transaksiModel.getStatusTransaksi() == 2) {
            statusTransaksi.setText(Constants.DIPROSES);
            tombolStatus();
            sembunyikanInputBiaya();
            penerimaLayout.setVisibility(View.GONE);
        } else if (transaksiModel.getStatusTransaksi() == 3) {
            statusTransaksi.setText(Constants.DIKIRIM);
            tombolStatus();
            sembunyikanInputBiaya();
            penerimaLayout.setVisibility(View.VISIBLE);
            txtPenerimaPesanan.setVisibility(View.GONE);
        } else if (transaksiModel.getStatusTransaksi() == 4) {
            statusTransaksi.setText(Constants.TERKIRIM);
            tombolStatus();
            sembunyikanInputBiaya();
            penerimaLayout.setVisibility(View.VISIBLE);
            inputPenerimaPesanan.setVisibility(View.GONE);
            txtPenerimaPesanan.setVisibility(View.VISIBLE);
        } else if (transaksiModel.getStatusTransaksi() == 5) {
            statusTransaksi.setText(Constants.DITOLAK);
            sembunyikanTombol();
            sembunyikanInputBiaya();
            penerimaLayout.setVisibility(View.GONE);
        } else if (transaksiModel.getStatusTransaksi() == 6) {
            statusTransaksi.setText(Constants.DIBATALKAN);
            sembunyikanTombol();
            sembunyikanInputBiaya();
            penerimaLayout.setVisibility(View.GONE);
        } else if (transaksiModel.getStatusTransaksi() == 7) {
            statusTransaksi.setText(Constants.DITERIMA);
            sembunyikanTombol();
            sembunyikanInputBiaya();
            penerimaLayout.setVisibility(View.VISIBLE);
            inputPenerimaPesanan.setVisibility(View.GONE);
            txtPenerimaPesanan.setVisibility(View.VISIBLE);
        }
    }

    private void tampilInputBiaya() {
        inputTotalHarga.setVisibility(View.VISIBLE);
        totalHargaProduk.setVisibility(View.GONE);
        inputBiayaKirim.setVisibility(View.VISIBLE);
        txtBiayaKirim.setVisibility(View.GONE);
    }

    private void sembunyikanInputBiaya() {
        inputTotalHarga.setVisibility(View.GONE);
        totalHargaProduk.setVisibility(View.VISIBLE);
        inputBiayaKirim.setVisibility(View.GONE);
        txtBiayaKirim.setVisibility(View.VISIBLE);
    }

    private void tombolKonfirmasiTransaksi() {
        btnTolakPesanan.setVisibility(View.VISIBLE);
        btnTerimaPesanan.setVisibility(View.VISIBLE);
        btnPerbaruiStatus.setVisibility(View.GONE);
    }

    private void tombolStatus() {
        btnTolakPesanan.setVisibility(View.GONE);
        btnTerimaPesanan.setVisibility(View.GONE);
        btnPerbaruiStatus.setVisibility(View.VISIBLE);
    }

    private void sembunyikanTombol() {
        btnTolakPesanan.setVisibility(View.GONE);
        btnTerimaPesanan.setVisibility(View.GONE);
        btnPerbaruiStatus.setVisibility(View.GONE);
    }

    private void loadDataPesanan() {
        try {
            catatanPembeli.setText(transaksiModel.getCatatanKonsumen());
            totalHargaProduk.setText("Rp." + rupiah().format(transaksiModel.getTotalHarga()));
            jmlItemProduk.setText(String.valueOf(transaksiModel.getJumlahProduk()));
            txtBiayaKirim.setText("Rp." + rupiah().format(transaksiModel.getBiayaKirim()));
            txtPenerimaPesanan.setText(transaksiModel.getPenerima());
        } catch (Exception e) {

        }
    }

    private void loadDataProduk() {
        mDatabase.child(transaksiModel.getTipeTransaksi()).child(transaksiModel.getIdKategori()).child(transaksiModel.getIdProduk()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ProdukModel produkModel = dataSnapshot.getValue(ProdukModel.class);
                if (produkModel != null) {
                    try {
                        txtNamaProduk.setText(produkModel.getNamaProduk());
                        txtSatuanDigit.setText(produkModel.getSatuanProduk());
                        txtSatuan.setText(produkModel.getNamaSatuan());
                        txtHargaProduk.setText("Rp." + rupiah().format(produkModel.getHargaProduk()));
                        ImageLoader.getInstance().loadImageOther(DetailTransaksiActivity.this, produkModel.getImgProduk().get(0), imgProduk);
                    } catch (Exception e) {

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loadDataAlamat() {
        mDatabase.child(Constants.KONSUMEN).child(transaksiModel.getIdPembeli()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final KonsumenModel konsumenModel = dataSnapshot.getValue(KonsumenModel.class);
                if (konsumenModel != null) {
                    namaPenerima.setText(konsumenModel.getDetailKonsumen().get(Constants.NAMA).toString());
                    alamatLengkap.setText(konsumenModel.getDetailKonsumen().get(Constants.ALAMAT).toString());
                    telpPenerima.setText(konsumenModel.getDetailKonsumen().get(Constants.TELPON).toString());

                    btnKirimPesan.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(DetailTransaksiActivity.this, ObrolanActivity.class);
                            intent.putExtra(Constants.KONSUMEN_MODEL, konsumenModel);
                            startActivity(intent);
                        }
                    });
                    btnLihatPeta.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(DetailTransaksiActivity.this, LokasiKonsumenActivity.class);
                            intent.putExtra(Constants.LATITUDE, Double.parseDouble(konsumenModel.getDetailKonsumen().get(Constants.LATITUDE).toString()));
                            intent.putExtra(Constants.LONGITUDE, Double.parseDouble(konsumenModel.getDetailKonsumen().get(Constants.LONGITUDE).toString()));
                            intent.putExtra(Constants.NAMA, konsumenModel.getDetailKonsumen().get(Constants.NAMA).toString());
                            startActivity(intent);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void terimaOrder() {
        mDatabase.child(Constants.PENJUAL).child(getUid()).child(Constants.PENJUALAN).child(Constants.PENJUALAN_BARU).child(transaksiModel.getIdPemesanan()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mDatabase.child(Constants.PENJUAL).child(getUid()).child(Constants.PENJUALAN).child(Constants.STATUS_PENGIRIMAN).child(transaksiModel.getIdPemesanan())
                        .setValue(dataSnapshot.getValue(), new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                //  ShowAlertDialog.showAlert("sukses", DetailTransaksiActivity.this);
                                if (transaksiModel.getTipeTransaksi().equals(Constants.PRODUK_REGULER)) {
                                    biayaKirim = Double.parseDouble(inputBiayaKirim.getText().toString());
                                    mDatabase.child(Constants.PENJUAL).child(getUid()).child(Constants.PENJUALAN).child(Constants.STATUS_PENGIRIMAN).child(transaksiModel.getIdPemesanan())
                                            .child(Constants.STATUS_TRANSAKSI).setValue(2);
                                    mDatabase.child(Constants.PENJUAL).child(getUid()).child(Constants.PENJUALAN).child(Constants.STATUS_PENGIRIMAN).child(transaksiModel.getIdPemesanan())
                                            .child(Constants.BIAYA_KIRIM).setValue(biayaKirim);

                                } else {
                                    biayaKirim = Double.parseDouble(inputBiayaKirim.getText().toString());
                                    totalHarga = Double.parseDouble(inputTotalHarga.getText().toString());
                                    mDatabase.child(Constants.PENJUAL).child(getUid()).child(Constants.PENJUALAN).child(Constants.STATUS_PENGIRIMAN).child(transaksiModel.getIdPemesanan())
                                            .child(Constants.STATUS_TRANSAKSI).setValue(2);
                                    mDatabase.child(Constants.PENJUAL).child(getUid()).child(Constants.PENJUALAN).child(Constants.STATUS_PENGIRIMAN).child(transaksiModel.getIdPemesanan())
                                            .child(Constants.BIAYA_KIRIM).setValue(biayaKirim);
                                    mDatabase.child(Constants.PENJUAL).child(getUid()).child(Constants.PENJUALAN).child(Constants.STATUS_PENGIRIMAN).child(transaksiModel.getIdPemesanan())
                                            .child(Constants.JUMLAH_HARGA_PRODUK).setValue(totalHarga);
                                }

                            }
                        });
                mDatabase.child(Constants.PENJUAL).child(getUid()).child(Constants.PENJUALAN).child(Constants.PENJUALAN_BARU).child(transaksiModel.getIdPemesanan()).removeValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mDatabase.child(Constants.KONSUMEN).child(transaksiModel.getIdPembeli()).child(Constants.PEMBELIAN).child(Constants.PEMBELIAN_BARU).child(transaksiModel.getIdPemesanan()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mDatabase.child(Constants.KONSUMEN).child(transaksiModel.getIdPembeli()).child(Constants.PEMBELIAN).child(Constants.STATUS_PEMBELIAN).child(transaksiModel.getIdPemesanan())
                        .setValue(dataSnapshot.getValue(), new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                //  ShowAlertDialog.showAlert("sukses", DetailTransaksiActivity.this);
                                if (transaksiModel.getTipeTransaksi().equals(Constants.PRODUK_REGULER)) {
                                    mDatabase.child(Constants.KONSUMEN).child(transaksiModel.getIdPembeli()).child(Constants.PEMBELIAN).child(Constants.STATUS_PEMBELIAN).child(transaksiModel.getIdPemesanan())
                                            .child(Constants.STATUS_TRANSAKSI).setValue(2);
                                    mDatabase.child(Constants.KONSUMEN).child(transaksiModel.getIdPembeli()).child(Constants.PEMBELIAN).child(Constants.STATUS_PEMBELIAN).child(transaksiModel.getIdPemesanan())
                                            .child(Constants.BIAYA_KIRIM).setValue(biayaKirim);
                                } else {
                                    mDatabase.child(Constants.KONSUMEN).child(transaksiModel.getIdPembeli()).child(Constants.PEMBELIAN).child(Constants.STATUS_PEMBELIAN).child(transaksiModel.getIdPemesanan())
                                            .child(Constants.STATUS_TRANSAKSI).setValue(2);
                                    mDatabase.child(Constants.KONSUMEN).child(transaksiModel.getIdPembeli()).child(Constants.PEMBELIAN).child(Constants.STATUS_PEMBELIAN).child(transaksiModel.getIdPemesanan())
                                            .child(Constants.BIAYA_KIRIM).setValue(biayaKirim);
                                    mDatabase.child(Constants.KONSUMEN).child(transaksiModel.getIdPembeli()).child(Constants.PEMBELIAN).child(Constants.STATUS_PEMBELIAN).child(transaksiModel.getIdPemesanan())
                                            .child(Constants.JUMLAH_HARGA_PRODUK).setValue(totalHarga);
                                }
                            }
                        });
                mDatabase.child(Constants.KONSUMEN).child(transaksiModel.getIdPembeli()).child(Constants.PEMBELIAN).child(Constants.PEMBELIAN_BARU).child(transaksiModel.getIdPemesanan()).removeValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void perbaruiStatus() {
        mDatabase.child(Constants.PENJUAL).child(getUid()).child(Constants.PENJUALAN).child(Constants.STATUS_PENGIRIMAN).child(transaksiModel.getIdPemesanan()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                TransaksiModel transaksiModel = dataSnapshot.getValue(TransaksiModel.class);
                getStatus = transaksiModel.getStatusTransaksi();
                statusBaru = getStatus + 1;
                if (getStatus < 4) {
                    mDatabase.child(Constants.PENJUAL).child(getUid()).child(Constants.PENJUALAN).child(Constants.STATUS_PENGIRIMAN).child(transaksiModel.getIdPemesanan())
                            .child(Constants.STATUS_TRANSAKSI).setValue(statusBaru);
                    mDatabase.child(Constants.KONSUMEN).child(transaksiModel.getIdPembeli()).child(Constants.PEMBELIAN).child(Constants.STATUS_PEMBELIAN).child(transaksiModel.getIdPemesanan())
                            .child(Constants.STATUS_TRANSAKSI).setValue(statusBaru);
                }
                if (getStatus == 3) {
                    mDatabase.child(Constants.PENJUAL).child(getUid()).child(Constants.PENJUALAN).child(Constants.STATUS_PENGIRIMAN).child(transaksiModel.getIdPemesanan())
                            .child(Constants.STATUS_TRANSAKSI).setValue(statusBaru);
                    mDatabase.child(Constants.KONSUMEN).child(transaksiModel.getIdPembeli()).child(Constants.PEMBELIAN).child(Constants.STATUS_PEMBELIAN).child(transaksiModel.getIdPemesanan())
                            .child(Constants.STATUS_TRANSAKSI).setValue(statusBaru);
                    //tambah nama penerima
                    nama_penerima = inputPenerimaPesanan.getText().toString();
                    mDatabase.child(Constants.PENJUAL).child(getUid()).child(Constants.PENJUALAN).child(Constants.STATUS_PENGIRIMAN).child(transaksiModel.getIdPemesanan())
                            .child(Constants.PENERIMA).setValue(nama_penerima);
                    mDatabase.child(Constants.KONSUMEN).child(transaksiModel.getIdPembeli()).child(Constants.PEMBELIAN).child(Constants.STATUS_PEMBELIAN).child(transaksiModel.getIdPemesanan())
                            .child(Constants.PENERIMA).setValue(nama_penerima);
                }
                //ShowAlertDialog.showAlert("Status transaksi berhasil diperbarui", DetailTransaksiActivity.this);
                //finish();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        finish();
    }

    private void tolakPesanan() {
        mDatabase.child(Constants.PENJUAL).child(getUid()).child(Constants.PENJUALAN).child(Constants.PENJUALAN_BARU).child(transaksiModel.getIdPemesanan()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mDatabase.child(Constants.PENJUAL).child(getUid()).child(Constants.PENJUALAN).child(Constants.RIWAYAT_TRANSAKSI).child(transaksiModel.getIdPemesanan())
                        .setValue(dataSnapshot.getValue(), new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                //  ShowAlertDialog.showAlert("sukses", DetailTransaksiActivity.this);
                                mDatabase.child(Constants.PENJUAL).child(getUid()).child(Constants.PENJUALAN).child(Constants.RIWAYAT_TRANSAKSI).child(transaksiModel.getIdPemesanan())
                                        .child(Constants.STATUS_TRANSAKSI).setValue(5);
                            }
                        });
                mDatabase.child(Constants.PENJUAL).child(getUid()).child(Constants.PENJUALAN).child(Constants.PENJUALAN_BARU).child(transaksiModel.getIdPemesanan()).removeValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mDatabase.child(Constants.KONSUMEN).child(transaksiModel.getIdPembeli()).child(Constants.PEMBELIAN).child(Constants.PEMBELIAN_BARU).child(transaksiModel.getIdPemesanan()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mDatabase.child(Constants.KONSUMEN).child(transaksiModel.getIdPembeli()).child(Constants.PEMBELIAN).child(Constants.RIWAYAT_TRANSAKSI).child(transaksiModel.getIdPemesanan())
                        .setValue(dataSnapshot.getValue(), new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                //  ShowAlertDialog.showAlert("sukses", DetailTransaksiActivity.this);
                                mDatabase.child(Constants.KONSUMEN).child(transaksiModel.getIdPembeli()).child(Constants.PEMBELIAN).child(Constants.RIWAYAT_TRANSAKSI).child(transaksiModel.getIdPemesanan())
                                        .child(Constants.STATUS_TRANSAKSI).setValue(5);
                            }
                        });
                mDatabase.child(Constants.KONSUMEN).child(transaksiModel.getIdPembeli()).child(Constants.PEMBELIAN).child(Constants.PEMBELIAN_BARU).child(transaksiModel.getIdPemesanan()).removeValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onClick(View v) {
        if (v == btnTerimaPesanan) {
            terimaOrder();
            finish();
        } else if (v == btnPerbaruiStatus) {
            perbaruiStatus();
            finish();
        } else if (v == btnTolakPesanan) {
            tolakPesanan();
            finish();
        }
    }
}
