package com.example.aryaym.mlijopenjual.KelolaPenjualan;

import java.io.Serializable;

/**
 * Created by AryaYM on 20/09/2017.
 */

public class TransaksiModel implements Serializable {

    String idPemesanan, idKonsumen, idProduk, idKategori, catatanKonsumen,
            penerima, tipeTransaksi, tanggalKirim, waktuKirim;
    Double totalHarga, biayaKirim;
    long tanggalPesan;
    int statusTransaksi, jumlahProduk;

    public TransaksiModel(){}

    public String getIdPemesanan() {
        return idPemesanan;
    }

    public long getTanggalPesan() {
        return tanggalPesan;
    }

    public String getIdKategori() {
        return idKategori;
    }

    public String getIdKonsumen() {
        return idKonsumen;
    }

    public String getIdProduk() {
        return idProduk;
    }

    public Double getTotalHarga() {
        return totalHarga;
    }

    public String getCatatanKonsumen() {
        return catatanKonsumen;
    }

    public int getStatusTransaksi() {
        return statusTransaksi;
    }

    public int getJumlahProduk() {
        return jumlahProduk;
    }

    public Double getBiayaKirim() {
        return biayaKirim;
    }

    public String getPenerima() {
        return penerima;
    }

    public String getTipeTransaksi() {
        return tipeTransaksi;
    }

    public String getTanggalKirim() {
        return tanggalKirim;
    }

    public String getWaktuKirim() {
        return waktuKirim;
    }
}
