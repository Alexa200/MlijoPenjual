package com.example.aryaym.mlijopenjual.KelolaPenjualan;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.aryaym.mlijopenjual.R;

/**
 * Created by AryaYM on 20/09/2017.
 */

public class ListTransaksiViewHolder extends RecyclerView.ViewHolder {

    TextView txtTanggalTransaksi, txtNamaProduk, txtJmlProduk, txtAlamatKonsumen, txtStatusTransaksi, txtTanggalKirim, txtWaktuKirim;
    ImageView iconProduk;

    public ListTransaksiViewHolder(View itemView){
        super(itemView);

        txtTanggalTransaksi = (TextView) itemView.findViewById(R.id.tanggal_transaksi);
        txtNamaProduk = (TextView) itemView.findViewById(R.id.nama_produk);
        txtJmlProduk = (TextView) itemView.findViewById(R.id.jumlah_produk);
        txtAlamatKonsumen = (TextView) itemView.findViewById(R.id.alamat_konsumen);
        txtStatusTransaksi = (TextView) itemView.findViewById(R.id.status_transaksi);
        iconProduk = (ImageView) itemView.findViewById(R.id.img_produk);
        txtTanggalKirim = (TextView) itemView.findViewById(R.id.txt_tanggal_kirim);
        txtWaktuKirim = (TextView) itemView.findViewById(R.id.txt_waktu_kirim);
    }
}
