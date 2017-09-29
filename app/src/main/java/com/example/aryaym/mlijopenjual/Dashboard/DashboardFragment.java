package com.example.aryaym.mlijopenjual.Dashboard;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.aryaym.mlijopenjual.R;

/**
 * Created by AryaYM on 04/07/2017.
 */

public class DashboardFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        return view;
    }
}
