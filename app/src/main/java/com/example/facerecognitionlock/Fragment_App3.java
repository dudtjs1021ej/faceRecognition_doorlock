package com.example.facerecognitionlock;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

public class Fragment_App3 extends Fragment {
    ImageView iv3;

    public Fragment_App3(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_app3, container, false);

        iv3 = (ImageView) view.findViewById(R.id.imageView3);
        iv3.setImageResource(R.drawable.a3);

        return view;
    }
}