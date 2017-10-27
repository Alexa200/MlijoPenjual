package com.example.aryaym.mlijopenjual.InformasiKonsumen;

import java.io.Serializable;

/**
 * Created by AryaYM on 27/10/2017.
 */

public class KonsumenModel implements Serializable {
    private String avatar;
    private String nama;
    private String noTelp;
    private String uid;
    private String deviceToken;
    private String alamat;

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDeviceToken() {
        return deviceToken;
    }
}
