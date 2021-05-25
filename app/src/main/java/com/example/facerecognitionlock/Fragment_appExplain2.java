package com.example.facerecognitionlock;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

public class Fragment_appExplain2 extends Fragment {
    ImageView iv2;

    public Fragment_appExplain2(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_app_explain2, container, false);

        iv2 = (ImageView) view.findViewById(R.id.imageView2);
        iv2.setImageResource(R.drawable.a2);

        return view;
    }
}
