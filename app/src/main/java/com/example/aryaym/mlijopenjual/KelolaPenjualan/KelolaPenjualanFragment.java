package com.example.aryaym.mlijopenjual.KelolaPenjualan;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.aryaym.mlijopenjual.Base.BaseActivity;
import com.example.aryaym.mlijopenjual.R;
import com.example.aryaym.mlijopenjual.Utils.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class KelolaPenjualanFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    Unbinder unbinder;
    @BindView(R.id.pesanan_baru)
    LinearLayout pesananBaru;
    @BindView(R.id.status_penjualan)
    LinearLayout statusPenjualan;
    @BindView(R.id.riwayat_transaksi)
    LinearLayout riwayatTransaksi;
    @BindView(R.id.jml_penjualan_baru)
    TextView jmlPenjualanBaru;
    @BindView(R.id.jml_status_penjualan)
    TextView jmlStatusPenjualan;
    @BindView(R.id.jml_riwayat_transaksi)
    TextView jmlRiwayatTransaksi;

    private DatabaseReference mDatabase;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public KelolaPenjualanFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getActivity().setTitle(R.string.title_atur_profil);
        //ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        //actionBar.setDisplayHomeAsUpEnabled(true);
        View view = inflater.inflate(R.layout.fragment_kelola_penjualan, container, false);
        unbinder = ButterKnife.bind(this, view);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        loadData();
        //getData();
        pesananBaru.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(KelolaPenjualanFragment.this.getActivity(), ListTransaksiActivity.class);
                intent.putExtra(Constants.TITLE, "Pesanan Baru");
                intent.putExtra(Constants.TRANSAKSI, Constants.PENJUALAN_BARU);
                startActivity(intent);
            }
        });
        statusPenjualan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(KelolaPenjualanFragment.this.getActivity(), ListTransaksiActivity.class);
                intent.putExtra(Constants.TITLE, "Status Pesanan");
                intent.putExtra(Constants.TRANSAKSI, Constants.STATUS_PENGIRIMAN);
                startActivity(intent);
            }
        });
        riwayatTransaksi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(KelolaPenjualanFragment.this.getActivity(), ListTransaksiActivity.class);
                intent.putExtra(Constants.TITLE, "Riwayat Transaksi");
                intent.putExtra(Constants.TRANSAKSI, Constants.RIWAYAT_TRANSAKSI);
                startActivity(intent);
            }
        });
        return view;

    }

    private void loadData(){
        mDatabase.child(Constants.PENJUAL).child(BaseActivity.getUid()).child(Constants.PENJUALAN).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long newOrder = dataSnapshot.child(Constants.PENJUALAN_BARU).getChildrenCount();
                long statusOrder = dataSnapshot.child(Constants.STATUS_PENGIRIMAN).getChildrenCount();
                long historyOrder = dataSnapshot.child(Constants.RIWAYAT_TRANSAKSI).getChildrenCount();
                jmlPenjualanBaru.setText(Long.toString(newOrder));
                jmlStatusPenjualan.setText(Long.toString(statusOrder));
                jmlRiwayatTransaksi.setText(Long.toString(historyOrder));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

//    private void getData(){
//        Query query = mDatabase.child(Constants.PENJUAL).child(BaseActivity.getUid()).child(Constants.PENJUALAN).child(Constants.PENJUALAN_BARU).orderByChild(Constants.STATUS_TRANSAKSI).equalTo(2);
//        query.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                long order = dataSnapshot.getChildrenCount();
//                jmlRiwayatTransaksi.setText(Long.toString(order));
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
