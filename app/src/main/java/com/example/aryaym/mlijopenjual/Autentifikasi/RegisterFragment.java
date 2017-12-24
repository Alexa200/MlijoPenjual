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

/**
 * Created by AryaYM on 28/10/2017.
 */

public class RegisterFragment extends Fragment {

    @BindView(R.id.email)
    EditText inputEmail;
    @BindView(R.id.password)
    EditText inputPassword;
    @BindView(R.id.sign_up_button)
    Button signUpButton;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    private FirebaseAuth auth;

    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_register, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        auth = FirebaseAuth.getInstance();


        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
                Toast.makeText(getActivity(), "Harap tunggu, sedang proses membuat akun anda", Toast.LENGTH_LONG).show();
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterFragment.this.getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Toast.makeText(getActivity(), "Akun anda berhasil dibuat", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);

                        if (task.isSuccessful()) {
                            auth.signInWithEmailAndPassword(email, password);
                            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child(Constants.PENJUAL);
                            DatabaseReference currentUserDB = mDatabase.child(auth.getCurrentUser().getUid());
                            currentUserDB.child(Constants.EMAIL).setValue(email);
                            currentUserDB.child(Constants.DEVICE_TOKEN).setValue(getToken());
                            startActivity(new Intent(RegisterFragment.this.getActivity(), DataUserBaruActivity.class));
                            getActivity().finish();
                        } else {
                            Toast.makeText(getActivity(), "error" + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private String getToken() {
        return FirebaseInstanceId.getInstance().getToken();
    }

    @NonNull
    private String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
}
