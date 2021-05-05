package com.example.facerecognitionlock;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MyPage extends Activity {

    TextView mpNickname, mpEmail, mpPwd;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_mypage);

        mpNickname = findViewById(R.id.mpNickname2);
        mpEmail = findViewById(R.id.mpEmail2);
        mpPwd = findViewById(R.id.mpPwd2);

        firebaseAuth =  FirebaseAuth.getInstance();

        String email = mpEmail.getText().toString().trim();
        String pwd = mpPwd.getText().toString().trim();
        firebaseAuth.signInWithEmailAndPassword(email, pwd)
                .addOnCompleteListener(MyPage.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            Intent intent = new Intent(MyPage.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(MyPage.this, "이메일 혹은 비밀번호를 다시 확인해 주세요.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
