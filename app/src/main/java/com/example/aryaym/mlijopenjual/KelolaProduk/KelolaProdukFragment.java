package com.example.aryaym.mlijopenjual.KelolaProduk;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.aryaym.mlijopenjual.Base.BaseActivity;
import com.example.aryaym.mlijopenjual.R;
import com.example.aryaym.mlijopenjual.Utils.Constants;
import com.example.aryaym.mlijopenjual.Utils.MyLinearLayoutManager;
import com.example.aryaym.mlijopenjual.Utils.ShowSnackbar;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by AryaYM on 12/08/2017.
 */

public class KelolaProdukFragment extends Fragment implements View.OnClickListener {

    @BindView(R.id.recycler_my_produk)
    RecyclerView mRecycler;
    @BindView(R.id.fab_new_produk)
    FloatingActionButton fabNewProduk;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.img_no_result)
    ImageView imgNoResult;
   // Unbinder unbinder;

    private View rootView;

    private DatabaseReference mDatabase;
    private List<PostRefModel> postRefModels = new ArrayList<>();
    private KelolaProdukAdapter kelolaProdukAdapter;
    private MyLinearLayoutManager customLinearLayoutManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstance) {
        getActivity().setTitle(R.string.title_activity_kelola_produk);
        rootView = inflater.inflate(R.layout.fragment_kelola_produk, container, false);
        ButterKnife.bind(this, rootView);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        kelolaProdukAdapter = new KelolaProdukAdapter(postRefModels, getActivity());
        customLinearLayoutManager = new MyLinearLayoutManager(getActivity());
        mRecycler.setLayoutManager(customLinearLayoutManager);
        mRecycler.setAdapter(kelolaProdukAdapter);
        fabNewProduk.setOnClickListener(this);
        loadDataProduk();
        return rootView;
    }

    private void showItemData() {
        progressBar.setVisibility(View.GONE);
        mRecycler.setVisibility(View.VISIBLE);
        imgNoResult.setVisibility(View.GONE);
    }

    private void hideItemData() {
        progressBar.setVisibility(View.VISIBLE);
        mRecycler.setVisibility(View.GONE);
        imgNoResult.setVisibility(View.GONE);
    }

    private void loadDataProduk() {
        try {
            getProdukPenjual(BaseActivity.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        hideItemData();
                        getProdukPenjual(BaseActivity.getUid()).addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                try {
                                    if (dataSnapshot != null){
                                        PostRefModel postRefModel = dataSnapshot.getValue(PostRefModel.class);
                                        if (!postRefModels.contains(postRefModel)) {
                                            postRefModels.add(postRefModel);
                                            kelolaProdukAdapter.notifyDataSetChanged();
                                        }
                                    }
                                    showItemData();
                                }catch (Exception e){

                                }
                            }

                            @Override
                            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                                try {
                                    if (dataSnapshot != null){
                                        PostRefModel postRefModel = dataSnapshot.getValue(PostRefModel.class);
                                        if (!postRefModels.contains(postRefModel)) {
                                            postRefModels.clear();
                                            postRefModels.add(postRefModel);
                                            kelolaProdukAdapter.notifyDataSetChanged();
                                        }
                                    }
                                    showItemData();
                                }catch (Exception e){

                                }
                            }

                            @Override
                            public void onChildRemoved(DataSnapshot dataSnapshot) {
                                try {
                                    PostRefModel postRefModel = dataSnapshot.getValue(PostRefModel.class);
                                    int indexMyPostInList = IndexProduk(postRefModel);
                                    postRefModels.remove(indexMyPostInList);
                                    kelolaProdukAdapter.notifyDataSetChanged();
                                } catch (Exception e) {
                                    ShowSnackbar.showSnack(getActivity(), getActivity().getResources().getString(R.string.msg_retry));
                                }
                            }

                            @Override
                            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }else {
                        imgNoResult.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        mRecycler.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } catch (Exception e) {
            ShowSnackbar.showSnack(getActivity(), getActivity().getResources().getString(R.string.msg_retry));
            imgNoResult.setVisibility(View.VISIBLE);
        }
    }

    public Query getProdukPenjual(String uid) {
        Query query = mDatabase.child(Constants.PENJUAL).child(uid).child(Constants.PRODUK);
        return query;
    }

    private int IndexProduk(PostRefModel postRefModel){
        int index = 0;
        for (int i = 0; i < postRefModels.size(); i++){
            if (postRefModels.get(i).getIdProduk().equals(postRefModel.getIdProduk())){
                index = i;
                break;
            }
        }
        return index;
    }

    @Override
    public void onClick(View v) {
        if (v == fabNewProduk){
            Intent intent = new Intent(KelolaProdukFragment.this.getActivity(), TambahProdukActivity.class);
            startActivity(intent);
        }
    }

}
