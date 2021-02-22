package com.example.facerecognitionlock;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class Fragment_Lock extends Fragment {

    ImageButton door_btn;
    String door="close";

    public Fragment_Lock() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_lock, container, false);

        DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference conditionRef = mRootRef.child("door");

        door_btn=(ImageButton)view.findViewById(R.id.imageButton);
        door_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(door.equals("open")) {
                    door_btn.setImageDrawable(getActivity().getDrawable(R.drawable.door_close_remove));
                    door="close";
                    conditionRef.setValue(door);
                }
                else if(door.equals("close")) {
                    door_btn.setImageDrawable(getActivity().getDrawable(R.drawable.door_open_remove));
                    door="open";
                    conditionRef.setValue(door);
                }
            }
        });
        // Inflate the layout for this fragment
        return view;


    }
}