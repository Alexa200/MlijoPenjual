package com.example.aryaym.mlijopenjual.Obrolan;

import com.example.aryaym.mlijopenjual.Utils.Constants;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.Map;

/**
 * Created by AryaYM on 24/10/2017.
 */

public class ObrolanPresenter {
    private ObrolanActivity view;
    private DatabaseReference mDatabase;

    public ObrolanPresenter(ObrolanActivity view){
        this.view = view;
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    private void tambahData(String pengirimId, String penerimaId, ObrolanModel obrolanModel, boolean kontenMilikku){
        try {
            obrolanModel.setKontenMilikku(kontenMilikku);
            obrolanModel.setPenerima(penerimaId);
            String key = mDatabase.child(Constants.OBROLAN).child(pengirimId).child(penerimaId).push().getKey();

            Map<String, Object> data = obrolanModel.toMap();
            mDatabase.child(Constants.OBROLAN).child(pengirimId).child(penerimaId).child(key).setValue(data);
        }catch (Exception e){

        }
    }

    public Query loadData(String pengirimId, String penerimaId){
        return mDatabase.child(Constants.OBROLAN).child(pengirimId).child(penerimaId);
    }

    public void updateObrolanKonsumen(String pengirimId, String penerimaId, ObrolanModel obrolanModel){
        try {
            ObrolanTerakhir obrolanTerakhir = new ObrolanTerakhir(penerimaId, obrolanModel.getTimestamp()* -1);
            Map<String, Object> data = obrolanTerakhir.toMap();
            mDatabase.child(Constants.KONSUMEN).child(penerimaId).child(Constants.OBROLAN).child(pengirimId).updateChildren(data);
           // mDatabase.child(Constants.PENJUAL).child(penerimaId).child(Constants.OBROLAN).child(pengirimId).updateChildren(data);
        }catch (Exception e){

        }
    }
    public void updateObrolanPenjual(String pengirimId, String penerimaId, ObrolanModel obrolanModel){
        try {
            ObrolanTerakhir obrolanTerakhir = new ObrolanTerakhir(penerimaId, obrolanModel.getTimestamp()* -1);
            Map<String, Object> data = obrolanTerakhir.toMap();
         //   mDatabase.child(Constants.KONSUMEN).child(pengirimId).child(Constants.OBROLAN).child(penerimaId).updateChildren(data);
            mDatabase.child(Constants.PENJUAL).child(pengirimId).child(Constants.OBROLAN).child(penerimaId).updateChildren(data);
        }catch (Exception e){

        }
    }

    public void tambahObrolan(String penerimaId, ObrolanModel obrolanModel){
        tambahData(view.getUid(), penerimaId, obrolanModel, true);
        tambahData(penerimaId, view.getUid(), obrolanModel, false);
        //update chat terakhir
        updateObrolanKonsumen(view.getUid(), penerimaId, obrolanModel);
        updateObrolanPenjual(view.getUid(), penerimaId, obrolanModel);
    }
}
