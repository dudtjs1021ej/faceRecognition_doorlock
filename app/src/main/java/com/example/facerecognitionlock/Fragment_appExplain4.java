package com.example.facerecognitionlock;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

public class Fragment_appExplain4 extends Fragment {
    ImageView iv4;

    public Fragment_appExplain4(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_app_explain4, container, false);

        iv4 = (ImageView) view.findViewById(R.id.imageView4);
        iv4.setImageResource(R.drawable.a4);

        return view;
    }
}