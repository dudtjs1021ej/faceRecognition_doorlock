package com.example.facerecognitionlock;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

public class Fragment_appExplain1 extends Fragment {
    ImageView iv1;

    public Fragment_appExplain1(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_app_explain1, container, false);

        iv1 = (ImageView) view.findViewById(R.id.imageView1);
        iv1.setImageResource(R.drawable.a1);

        return view;
    }
}
