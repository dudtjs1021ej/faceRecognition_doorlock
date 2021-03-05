package com.example.facerecognitionlock;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.Executor;


public class Fragment_Lock extends Fragment {

    ImageButton door_btn;
    String door="close";
    String data_door;


    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;


    public Fragment_Lock() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_lock, container, false);

        DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference conditionRef = mRootRef.child("door");
        conditionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                data_door=snapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        door_btn=(ImageButton)view.findViewById(R.id.imageButton);

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("지문 인증")
                .setSubtitle("기기에 등록된 지문을 이용하여 지문을 인증해주세요.")
                .setNegativeButtonText("취소")
                .setDeviceCredentialAllowed(false)
                .build();

        // 지문인식
        executor = ContextCompat.getMainExecutor(getActivity());
        biometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getActivity().getApplicationContext(), "지문인증에 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(getActivity().getApplicationContext(), "지문인증에 성공했습니다.", Toast.LENGTH_SHORT).show();
                if(data_door.equals("wait")) {
                    if (door.equals("open")) {
                        System.out.println("test open");
                        door_btn.setImageDrawable(getActivity().getDrawable(R.drawable.door_close_remove));
                        door = "close";
                        conditionRef.setValue(door);

                    } else if (door.equals("close")) {
                        System.out.println("test close");
                        door_btn.setImageDrawable(getActivity().getDrawable(R.drawable.door_open_remove));
                        door = "open";
                        conditionRef.setValue(door);


                    }
                }

                }
            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getActivity().getApplicationContext(), "지문인증에 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
        door_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                biometricPrompt.authenticate(promptInfo);
                }

        });

        // Inflate the layout for this fragment
        return view;


    }
}