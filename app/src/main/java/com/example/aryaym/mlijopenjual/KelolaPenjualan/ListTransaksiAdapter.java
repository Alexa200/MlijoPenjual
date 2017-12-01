package com.example.aryaym.mlijopenjual.KelolaPenjualan;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.example.aryaym.mlijopenjual.Base.ImageLoader;
import com.example.aryaym.mlijopenjual.InformasiKonsumen.KonsumenModel;
import com.example.aryaym.mlijopenjual.KelolaProduk.ProdukModel;
import com.example.aryaym.mlijopenjual.R;
import com.example.aryaym.mlijopenjual.Utils.Constants;
import com.example.aryaym.mlijopenjual.Utils.DateFormatter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

/**
 * Created by AryaYM on 20/09/2017.
 */

public class ListTransaksiAdapter extends RecyclerView.Adapter<ListTransaksiViewHolder> {

    private Activity activity;
    private List<TransaksiModel> listTransaksi;
    private DatabaseReference mDatabase;

    public ListTransaksiAdapter(Activity activity, List<TransaksiModel> listTransaksi){
        this.activity = activity;
        this.listTransaksi = listTransaksi;
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public ListTransaksiViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = View.inflate(activity, R.layout.item_transaksi, null);
        return new ListTransaksiViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(final ListTransaksiViewHolder holder, int position) {
        final TransaksiModel transaksiModel = listTransaksi.get(position);
//        if (transaksiModel != null){
//            try {
//               holder.txtNamaProduk.setText(transaksiModel.getNamaProduk());
//            }catch (Exception e){
//
//            }
//        }
        holder.txtTanggalTransaksi.setText(DateFormatter.formatDateByYMD(transaksiModel.getTanggalPesan()));
        holder.txtJmlProduk.setText("Jumlah :" + " " + String.valueOf(transaksiModel.getJumlahProduk()));
        holder.txtTanggalKirim.setText(transaksiModel.getTanggalKirim());
        holder.txtWaktuKirim.setText(transaksiModel.getWaktuKirim());
    //    holder.txtStatusTransaksi.setText(String.valueOf(transaksiModel.getStatusTransaksi()) );
        if (transaksiModel.getStatusTransaksi() == 1){
            holder.txtStatusTransaksi.setTextColor(Color.RED);
            holder.txtStatusTransaksi.setText(Constants.MENUNGGU);
        }else if(transaksiModel.getStatusTransaksi() ==2){
            holder.txtStatusTransaksi.setTextColor(Color.GREEN);
            holder.txtStatusTransaksi.setText(Constants.DIPROSES);
        }else if(transaksiModel.getStatusTransaksi() ==3){
            holder.txtStatusTransaksi.setTextColor(Color.GREEN);
            holder.txtStatusTransaksi.setText(Constants.DIKIRIM);
        }else if(transaksiModel.getStatusTransaksi() ==4){
            holder.txtStatusTransaksi.setTextColor(Color.GREEN);
            holder.txtStatusTransaksi.setText(Constants.TERKIRIM);
        }else if(transaksiModel.getStatusTransaksi() ==5){
            holder.txtStatusTransaksi.setTextColor(Color.RED);
            holder.txtStatusTransaksi.setText(Constants.DITOLAK);
        }else if(transaksiModel.getStatusTransaksi() ==6){
            holder.txtStatusTransaksi.setTextColor(Color.RED);
            holder.txtStatusTransaksi.setText(Constants.DIBATALKAN);
        }else if (transaksiModel.getStatusTransaksi() == 7) {
            holder.txtStatusTransaksi.setTextColor(Color.GREEN);
            holder.txtStatusTransaksi.setText(Constants.DITERIMA);
        }


        mDatabase.child(Constants.KONSUMEN).child(transaksiModel.getIdKonsumen()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final KonsumenModel konsumenModel = dataSnapshot.getValue(KonsumenModel.class);
                if (konsumenModel != null) {
                    holder.txtAlamatKonsumen.setText(konsumenModel.getDetailKonsumen().get(Constants.ALAMAT).toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mDatabase.child(transaksiModel.getTipeTransaksi()).child(transaksiModel.getIdKategori()).child(transaksiModel.getIdProduk())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot != null){
                            final ProdukModel produkModel = dataSnapshot.getValue(ProdukModel.class);
                            if (produkModel != null){
                                try {
                                    holder.txtNamaProduk.setText(produkModel.getNamaProduk());
                                    // holder.txtTanggalTransaksi.setText(transaksiModel.get);
                                    ImageLoader.getInstance().loadImageAvatar(activity, produkModel.getImgProduk().get(0), holder.iconProduk);
                                }catch (Exception e){

                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, DetailTransaksiActivity.class);
                intent.putExtra(Constants.TRANSAKSI, transaksiModel);
                activity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listTransaksi.size();
    }
}
