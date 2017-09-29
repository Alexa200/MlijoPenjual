package com.example.aryaym.mlijopenjual.Profil;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.aryaym.mlijopenjual.R;

public class KelolaAlamatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kelola_alamat);
       // setTitle(R.string.title_activity_kelola_alamat);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_tambah_alamat, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        if (item.getItemId() == R.id.btn_tambah_alamat){
//            Intent intent = new Intent(KelolaAlamatActivity.this, TambahAlamatActivity.class);
//            startActivity(intent);
//        }
//        return super.onOptionsItemSelected(item);
//    }
}
