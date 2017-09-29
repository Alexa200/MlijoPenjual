package com.example.aryaym.mlijopenjual.KelolaPenjualan;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.aryaym.mlijopenjual.Base.BaseActivity;
import com.example.aryaym.mlijopenjual.R;
import com.example.aryaym.mlijopenjual.Utils.Constants;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListTransaksiActivity extends BaseActivity {

    String title, tipeTransaksi;
    @BindView(R.id.recycler_list_transaksi)
    RecyclerView mRecycler;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.img_no_result)
    ImageView imgNoResult;

    private DatabaseReference mDatabase;
    private List<TransaksiModel> transaksiList = new ArrayList<>();
    private ListTransaksiAdapter listTransaksiAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_transaksi);
        ButterKnife.bind(this);
        title = getIntent().getStringExtra(Constants.TITLE);
        tipeTransaksi = getIntent().getStringExtra(Constants.TRANSAKSI);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        listTransaksiAdapter = new ListTransaksiAdapter(this, transaksiList);
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ambilData();

        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRecycler.setAdapter(listTransaksiAdapter);
    }

    private void showItemData() {
        progressBar.setVisibility(View.GONE);
        mRecycler.setVisibility(View.VISIBLE);
    }

//    private void ambilData(){
//        mDatabase.child(Constants.KONSUMEN).child(getUid()).child(Constants.PEMBELIAN).child(tipeTransaksi).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (dataSnapshot != null){
//                    TransaksiModel transaksiModel = dataSnapshot.getValue(TransaksiModel.class);
//                    if (!transaksiList.contains(transaksiModel)){
//                        transaksiList.add(transaksiModel);
//                        listTransaksiAdapter.notifyDataSetChanged();
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }

    private Query getData(){
        Query query = mDatabase.child(Constants.PENJUAL).child(getUid()).child(Constants.PENJUALAN).child(tipeTransaksi);
        return query;
    }

    private void ambilData(){
        try {
            getData().addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    try {
                        if (dataSnapshot != null){
                            TransaksiModel transaksiModel = dataSnapshot.getValue(TransaksiModel.class);
                            if (!transaksiList.contains(transaksiModel)){
                                transaksiList.add(transaksiModel);
                                listTransaksiAdapter.notifyDataSetChanged();
                            }
                        }
                        showItemData();
                    }catch (Exception e){

                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }catch (Exception e){
            imgNoResult.setVisibility(View.VISIBLE);
        }
    }
}
