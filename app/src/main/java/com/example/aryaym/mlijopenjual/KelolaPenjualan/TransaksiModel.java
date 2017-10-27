package com.example.aryaym.mlijopenjual.KelolaPenjualan;

import java.io.Serializable;

/**
 * Created by AryaYM on 20/09/2017.
 */

public class TransaksiModel implements Serializable {

    String idPemesanan, idPembeli, idPenjual, idProduk, idKategori, catatanKonsumen, penerima, tipeTransaksi;
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

    public void setTanggalPesan(long tanggalPesan) {
        this.tanggalPesan = tanggalPesan;
    }

    public String getIdKategori() {
        return idKategori;
    }

    public void setIdKategori(String idKategori) {
        this.idKategori = idKategori;
    }

    public String getIdPenjual() {
        return idPenjual;
    }

    public void setIdPenjual(String idPenjual) {
        this.idPenjual = idPenjual;
    }

    public String getIdPembeli() {
        return idPembeli;
    }

    public String getIdProduk() {
        return idProduk;
    }

    public void setIdProduk(String idProduk) {
        this.idProduk = idProduk;
    }

    public Double getTotalHarga() {
        return totalHarga;
    }

    public String getCatatanKonsumen() {
        return catatanKonsumen;
    }

    public void setCatatanKonsumen(String catatanKonsumen) {
        this.catatanKonsumen = catatanKonsumen;
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
}
