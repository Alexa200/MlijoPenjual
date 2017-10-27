package com.example.aryaym.mlijopenjual.Obrolan;

import com.example.aryaym.mlijopenjual.Utils.Constants;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by AryaYM on 24/10/2017.
 */

public class ObrolanModel {
    private String konten;
    private boolean kontenFoto;
    private boolean kontenMilikku;
    private long timestamp;
    private String pengirim;
    private String penerima;
    private String obrolanId;
    private boolean displayStatus;

    public ObrolanModel(){}

    public ObrolanModel(String konten, boolean kontenFoto, long timestamp, String pengirim){
        this.konten = konten;
        this.kontenFoto = kontenFoto;
        this.timestamp = timestamp;
        this.pengirim = pengirim;
    }

    public boolean isDisplayStatus() {
        return displayStatus;
    }

    public void setDisplayStatus(boolean displayStatus) {
        this.displayStatus = displayStatus;
    }

    public String getKonten() {
        return konten;
    }

    public void setKonten(String konten) {
        this.konten = konten;
    }

    public boolean isKontenFoto() {
        return kontenFoto;
    }

    public void setKontenFoto(boolean kontenFoto) {
        this.kontenFoto = kontenFoto;
    }

    public boolean isKontenMilikku() {
        return kontenMilikku;
    }

    public void setKontenMilikku(boolean kontenMilikku) {
        this.kontenMilikku = kontenMilikku;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getPengirim() {
        return pengirim;
    }

    public void setPengirim(String pengirim) {
        this.pengirim = pengirim;
    }

    public String getPenerima() {
        return penerima;
    }

    public void setPenerima(String penerima) {
        this.penerima = penerima;
    }

    public String getObrolanId() {
        return obrolanId;
    }

    public void setObrolanId(String obrolanId) {
        this.obrolanId = obrolanId;
    }

    public Map<String, Object> toMap(){
        Map<String, Object> mapDataObrolan = new HashMap<>();
        mapDataObrolan.put(Constants.KONTEN, konten);
        mapDataObrolan.put(Constants.KONTEN_FOTO, kontenFoto);
        mapDataObrolan.put(Constants.MILIKKU, kontenMilikku);
        mapDataObrolan.put(Constants.PENGIRIM, pengirim);
        mapDataObrolan.put(Constants.PENERIMA, penerima);
        mapDataObrolan.put(Constants.TIMESTAMP, timestamp);
        return mapDataObrolan;
    }
}
