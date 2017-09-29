package com.example.aryaym.mlijopenjual.KelolaProduk;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by AryaYM on 08/07/2017.
 */

public class ProdukModel implements Serializable{

    String userId, namaProduk, idKategori,  digitSatuan, namaSatuan, deskripsiProduk;
    String idProduk;
    Double hargaProduk;
    ArrayList<String> images = new ArrayList<>();


    public ProdukModel(){}

    public ProdukModel( String idProduk, String idKategori){
        this.idProduk = idProduk;
        this.idKategori = idKategori;
    }

    public ProdukModel(String userId, String namaProduk, String idKategori, ArrayList<String> imgProduk, Double hargaProduk, String digitSatuan, String namaSatuan, String deskripsiProduk, String idProduk) {
        this.userId = userId;
        this.namaProduk = namaProduk;
        this.idKategori = idKategori;
        this.images = imgProduk;
        this.hargaProduk = hargaProduk;
        this.digitSatuan = digitSatuan;
        this.namaSatuan = namaSatuan;
        this.deskripsiProduk = deskripsiProduk;
        this.idProduk = idProduk;
    }

    // [START post_to_map]
//    @Exclude
//    public Map<String, Object> toProduk() {
//        HashMap<String, Object> result = new HashMap<>();
//        result.put("postBy", userId);
//        result.put("namaProduk", namaProduk);
//        result.put("idKategori", idKategori);
//        result.put("hargaProduk", hargaProduk);
//        result.put("digitSatuan", digitSatuan);
//        result.put("namaSatuan", namaSatuan);
//        result.put("idProduk",idProduk);
//        result.put("images",images);
//        result.put("deskripsiProduk", deskripsiProduk);
//        return result;
//    }
//    // [END post_to_map]
//
//    public Map<String, Object> toPenjual(){
//        HashMap<String, Object> result = new HashMap<>();
//        result.put("idKategori", idKategori);
//        result.put("idProduk", idProduk);
//
//        return result;
//    }


    public String getUid() {
        return userId;
    }

    public void setUid(String uid) {
        this.userId = uid;
    }

    public String getNamaProduk() {
        return namaProduk;
    }

    public void setNamaProduk(String namaProduk) {
        this.namaProduk = namaProduk;
    }

    public String getKategoriProduk() {
        return idKategori;
    }

    public void setKategoriProduk(String idKategori) {
        this.idKategori = idKategori;
    }

    public Double getHargaProduk() {
        return hargaProduk;
    }

    public void setHargaProduk(Double hargaProduk) {
        this.hargaProduk = hargaProduk;
    }

    public String getSatuanProduk() {
        return digitSatuan;
    }

    public void setSatuanProduk(String digitSatuan) {
        this.digitSatuan = digitSatuan;
    }

    public String getNamaSatuan() {
        return namaSatuan;
    }

    public void setNamaSatuan(String namaSatuan) {
        this.namaSatuan = namaSatuan;
    }

    public String getDeskripsiProduk() {
        return deskripsiProduk;
    }

    public void setDeskripsiProduk(String deskripsiProduk) {
        this.deskripsiProduk = deskripsiProduk;
    }

    public String getIdProduk() {
        return idProduk;
    }

    public void setIdProduk(String idProduk) {
        this.idProduk = idProduk;
    }

    public ArrayList<String> getImgProduk() {
        return images;
    }

    public void setImgProduk(ArrayList<String> imgProduk) {
        this.images = imgProduk;
    }
}
