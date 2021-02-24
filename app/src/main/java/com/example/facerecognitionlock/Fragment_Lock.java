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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.Executor;


public class Fragment_Lock extends Fragment {

    ImageButton door_btn;
    String door="close";

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
            }
            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getActivity().getApplicationContext(), "지문인증에 실패했습니다.", Toast.LENGTH_SHORT).show();
            }

        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("지문 인증")
                .setSubtitle("기기에 등록된 지문을 이용하여 지문을 인증해주세요.")
                .setNegativeButtonText("취소")
                .setDeviceCredentialAllowed(false)
                .build();

        door_btn=(ImageButton)view.findViewById(R.id.imageButton);
        door_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(door.equals("open")) {
                    biometricPrompt.authenticate(promptInfo);
                    door_btn.setImageDrawable(getActivity().getDrawable(R.drawable.door_close_remove));
                    door="close";
                    conditionRef.setValue(door);
                }
                else if(door.equals("close")) {
                    biometricPrompt.authenticate(promptInfo);
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