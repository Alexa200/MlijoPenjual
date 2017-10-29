package com.example.aryaym.mlijopenjual.Obrolan;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.aryaym.mlijopenjual.Base.BaseActivity;
import com.example.aryaym.mlijopenjual.Base.InternetConnection;
import com.example.aryaym.mlijopenjual.R;
import com.example.aryaym.mlijopenjual.Utils.Constants;
import com.example.aryaym.mlijopenjual.Utils.ShowAlertDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AryaYM on 24/10/2017.
 */

public class DaftarObrolanFragment extends Fragment {

    private View view;
    private RecyclerView mRecycler;
    private List<String> listUid;
    private DaftarObrolanAdapter daftarObrolanAdapter;
    private ProgressBar progressBar;
    private DatabaseReference mDatabase;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        view = inflater.inflate(R.layout.fragment_daftar_obrolan, container, false);
        getActivity().setTitle(R.string.title_activity_obrolan);
        mRecycler = (RecyclerView) view.findViewById(R.id.recycler_chat);
        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        listUid = new ArrayList<>();
        daftarObrolanAdapter = new DaftarObrolanAdapter(getActivity(), listUid);
        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            if (InternetConnection.getInstance().isOnline(getActivity())){
                loadDataDaftarObrolan();
            }else {
                progressBar.setVisibility(View.GONE);
            }
        }else {
            ShowAlertDialog.showAlert(getActivity().getResources().getString(R.string.msg_retry), getActivity());
            progressBar.setVisibility(View.GONE);
            mRecycler.setVisibility(View.VISIBLE);
        }
        mRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecycler.setAdapter(daftarObrolanAdapter);
        return view;
    }

    private void showItemData() {
        progressBar.setVisibility(View.GONE);
        mRecycler.setVisibility(View.VISIBLE);
    }

    private void hideItemData() {
        progressBar.setVisibility(View.VISIBLE);
        mRecycler.setVisibility(View.GONE);
    }

    private Query getSemueObrolan(String currentId){
        return mDatabase.child(Constants.PENJUAL).child(currentId).child(Constants.OBROLAN).orderByChild(Constants.TIMESTAMP);
    }

    private void loadDataDaftarObrolan(){
        try {
            getSemueObrolan(BaseActivity.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        hideItemData();
                        getSemueObrolan(BaseActivity.getUid()).addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                try {
                                    if (dataSnapshot != null){
                                        if (!listUid.contains(dataSnapshot.getKey())){
                                            listUid.add(dataSnapshot.getKey());
                                            daftarObrolanAdapter.notifyDataSetChanged();
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
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }catch (Exception e){

        }
    }
}
