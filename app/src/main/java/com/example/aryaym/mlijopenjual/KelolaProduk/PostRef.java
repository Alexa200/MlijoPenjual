package com.example.aryaym.mlijopenjual.KelolaProduk;

import java.io.Serializable;

/**
 * Created by AryaYM on 17/08/2017.
 */

public class PostRef implements Serializable {
    private String idProduk;
    private String idKategori;

    public String getIdProduk() {
        return idProduk;
    }

    public void setIdProduk(String idProduk) {
        this.idProduk = idProduk;
    }

    public String getIdKategori() {
        return idKategori;
    }

    public void setIdKategori(String idKategori) {
        this.idKategori = idKategori;
    }
}
