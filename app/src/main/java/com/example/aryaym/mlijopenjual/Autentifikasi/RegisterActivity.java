package com.example.aryaym.mlijopenjual.Autentifikasi;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.aryaym.mlijopenjual.MainActivity;
import com.example.aryaym.mlijopenjual.R;
import com.example.aryaym.mlijopenjual.Utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by AryaYM on 27/05/2017.
 */

public class RegisterActivity extends Fragment {

    @BindView(R.id.noktp)
    EditText inputKTP;
    @BindView(R.id.nama)
    EditText inputNama;
    @BindView(R.id.nomortelp)
    EditText inputNoTelp;
    @BindView(R.id.email)
    EditText inputEmail;
    @BindView(R.id.password)
    EditText inputPassword;
    @BindView(R.id.sign_up_button)
    Button signUpButton;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    Unbinder unbinder;
    private FirebaseAuth auth;

    public RegisterActivity() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_register, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        auth = FirebaseAuth.getInstance();


        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String noktp = inputKTP.getText().toString().trim();
                final String nama = inputNama.getText().toString().trim();
                final String notelp = inputNoTelp.getText().toString().trim();
                final String email = inputEmail.getText().toString().trim();
                final String password = inputPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getContext(), R.string.msg_email_kosong, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getContext(), R.string.msg_password_kosong, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(getContext(), R.string.msg_minimum_password, Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                Toast.makeText(getActivity(),"Harap tunggu, sedang proses membuat akun anda", Toast.LENGTH_LONG).show();
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterActivity.this.getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Toast.makeText(getActivity(), "Akun anda berhasil dibuat", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);

                        if (task.isSuccessful()) {
                            auth.signInWithEmailAndPassword(email, password);
                            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child(Constants.PENJUAL);
                            DatabaseReference currentUserDB = mDatabase.child(auth.getCurrentUser().getUid());
                            currentUserDB.child(Constants.KTP).setValue(noktp);
                            currentUserDB.child(Constants.NAMA).setValue(nama);
                            currentUserDB.child(Constants.TELPON).setValue(notelp);
                            currentUserDB.child(Constants.EMAIL).setValue(email);
                            currentUserDB.child(Constants.UID).setValue(getUid());
                            currentUserDB.child(Constants.DEVICE_TOKEN).setValue(getToken());
                            currentUserDB.child(Constants.PENJUALAN).child(Constants.PENJUALAN_BARU).child(Constants.TRANSAKSI_COUNT).setValue(0);
                            currentUserDB.child(Constants.PENJUALAN).child(Constants.STATUS_PENGIRIMAN).child(Constants.TRANSAKSI_COUNT).setValue(0);
                            currentUserDB.child(Constants.PENJUALAN).child(Constants.RIWAYAT_TRANSAKSI).child(Constants.TRANSAKSI_COUNT).setValue(0);
                            startActivity(new Intent(RegisterActivity.this.getActivity(), MainActivity.class));
                            getActivity().finish();
                        } else {
                            Toast.makeText(getActivity(), "error" + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private String getToken(){
        return FirebaseInstanceId.getInstance().getToken();
    }

    @NonNull
    private String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
