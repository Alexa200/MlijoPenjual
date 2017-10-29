package com.example.aryaym.mlijopenjual.Profil;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.aryaym.mlijopenjual.Base.BaseActivity;
import com.example.aryaym.mlijopenjual.Base.ImageLoader;
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
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by AryaYM on 10/09/2017.
 */

public class ProfilFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    @BindView(R.id.img_avatar)
    CircleImageView imgAvatar;
    @BindView(R.id.txt_header_name)
    TextView txtHeaderName;
    @BindView(R.id.btn_pengaturan)
    Button btnPengaturan;
    @BindView(R.id.txt_email)
    TextView txtEmail;
    @BindView(R.id.txt_nomor_telpon)
    TextView txtNomorTelp;
    @BindView(R.id.txt_alamat_lengkap)
    TextView txtAlamat;
    Unbinder unbinder;

    private PenjualModel penjualModel;
    private DatabaseReference mDatabase;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public ProfilFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getActivity().setTitle(R.string.title_profil);
        final View view = inflater.inflate(R.layout.fragment_profil, container, false);
        unbinder = ButterKnife.bind(this, view);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        loadData();
        btnPengaturan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PengaturanFragment pengaturanFragment = new PengaturanFragment();
                getFragmentManager().beginTransaction().replace(R.id.main_fragment_container, pengaturanFragment).commit();
            }
        });
        return view;

    }

    private void loadData(){

        mDatabase.child(Constants.PENJUAL).child(BaseActivity.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                PenjualModel penjualModel = dataSnapshot.getValue(PenjualModel.class);
                if (penjualModel != null){
                    try {
                        txtHeaderName.setText(penjualModel.getDetailPenjual().get(Constants.NAMA).toString());
                        txtEmail.setText(penjualModel.getEmail());
                        txtNomorTelp.setText(penjualModel.getDetailPenjual().get(Constants.TELPON).toString());
                        txtAlamat.setText(penjualModel.getDetailPenjual().get(Constants.ALAMAT).toString());
                        ImageLoader.getInstance().loadImageAvatar(ProfilFragment.this.getActivity(), penjualModel.getDetailPenjual().get(Constants.AVATAR).toString(), imgAvatar);
                    }catch (Exception e){

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

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
