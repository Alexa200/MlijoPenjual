package com.example.aryaym.mlijopenjual.KelolaProduk;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.aryaym.mlijopenjual.R;

/**
 * Created by AryaYM on 13/07/2017.
 */

public class KelolaProdukViewHolder extends RecyclerView.ViewHolder {

    public TextView namaProduk, kategoriProduk, hargaProduk;
    public ImageButton menu;
    public ImageView iconProduk;

    public KelolaProdukViewHolder(View itemView){
        super(itemView);

        namaProduk= (TextView) itemView.findViewById(R.id.nama_produk);
        kategoriProduk= (TextView) itemView.findViewById(R.id.nama_kategori);
        hargaProduk= (TextView) itemView.findViewById(R.id.harga_produk);
        iconProduk = (ImageView) itemView.findViewById(R.id.icon_produk);
        menu = (ImageButton) itemView.findViewById(R.id.menu_kelola_produk);


    }
}
