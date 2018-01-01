package com.example.aryaym.mlijopenjual.KelolaProduk;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.example.aryaym.mlijopenjual.Base.BaseActivity;
import com.example.aryaym.mlijopenjual.Base.ImageLoader;
import com.example.aryaym.mlijopenjual.R;
import com.example.aryaym.mlijopenjual.Utils.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

/**
 * Created by AryaYM on 13/08/2017.
 */

public class KelolaProdukAdapter extends RecyclerView.Adapter<KelolaProdukViewHolder>{
    private List<PostRefModel> postRefModels;
    private Activity activity;
    private DatabaseReference mDatabase;

    public KelolaProdukAdapter(List<PostRefModel> postRefModels, Activity activity){
        this.postRefModels = postRefModels;
        this.activity = activity;
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public KelolaProdukViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View rootView = View.inflate(activity, R.layout.kelola_produk_adapter, null);
        return new KelolaProdukViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(final KelolaProdukViewHolder holder, int position) {
        final PostRefModel postRefModel = postRefModels.get(position);
        mDatabase.child(Constants.PRODUK_REGULER).child((postRefModel.getIdKategori())).child((postRefModel.getIdProduk()))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot != null){
                            final ProdukModel produkModel = dataSnapshot.getValue(ProdukModel.class);
                            if(produkModel != null){
                                try {
                                    holder.namaProduk.setText(produkModel.getNamaProduk());
                                    holder.kategoriProduk.setText(produkModel.getKategoriProduk());
                                    holder.hargaProduk.setText("Rp."+BaseActivity.rupiah().format(produkModel.getHargaProduk()));
                                    ImageLoader.getInstance().loadImageOther(activity, produkModel.getGambarProduk().get(0), holder.iconProduk);

                                    //onClick
                                    holder.menu.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(final View view) {
                                            final CharSequence[] dialogitem = {"edit", "hapus"};
                                            final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                                            builder.setItems(dialogitem, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    switch (which){
                                                        case 0:
                                                            Intent intent = new Intent(activity, UbahProdukActivity.class);
                                                            intent.putExtra(Constants.PRODUK, produkModel);
                                                            activity.startActivity(intent);
                                                            break;
                                                        case 1:
//                                                        //final String produkId = postRefModel.getProdukId();
                                                            final AlertDialog.Builder delBuilder = new AlertDialog.Builder(view.getContext());
                                                            delBuilder.setMessage(R.string.msg_hapusProduk).setCancelable(false)
                                                                    .setPositiveButton(R.string.lbl_ya, new DialogInterface.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(DialogInterface dialog, int which) {
                                                                            mDatabase.child(Constants.PRODUK_REGULER).child(postRefModel.getIdKategori()).child(postRefModel.getIdProduk()).removeValue();
                                                                            mDatabase.child(Constants.PENJUAL).child(BaseActivity.getUid()).child(Constants.PRODUK).child(postRefModel.getIdProduk()).removeValue();
                                                                        }
                                                                    })
                                                                    .setNegativeButton(R.string.lbl_tidak, new DialogInterface.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(DialogInterface dialog, int which) {

                                                                        }
                                                                    });
                                                            delBuilder.create().show();
                                                            //Toast.makeText("delete",Toast.LENGTH_SHORT).show();
                                                            // kelolaProduk.deleteProduk(String.valueOf(produkModel.getUid()), String.valueOf(postRefModel.getProdukId()),String.valueOf(postRefModel.getKategoriProduk()));
                                                            break;
                                                    }
                                                }
                                            });
                                            builder.create().show();
                                        }
                                    });
                                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Intent intent = new Intent(activity, DetailProdukActivity.class);
                                            intent.putExtra(Constants.PRODUK, produkModel);
                                            activity.startActivity(intent);
                                        }
                                    });
                                }catch (Exception e){

                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public int getItemCount(){
        return postRefModels.size();
    }
}
