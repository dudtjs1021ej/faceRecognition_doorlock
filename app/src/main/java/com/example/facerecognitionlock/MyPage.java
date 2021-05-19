package com.example.facerecognitionlock;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MyPage extends Activity {

    TextView mpNickname, mpEmail;
    EditText mpPwd;
    Button setBtn, deleteUser;

    String nickname, email, uid;
    //String pwd=null;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_mypage);

        mpNickname = findViewById(R.id.mpNickname2);
        mpEmail = findViewById(R.id.mpEmail2);
        mpPwd = findViewById(R.id.mpPwd2);

        setBtn = findViewById(R.id.setBtn);
        deleteUser = findViewById(R.id.deleteUser);


        firebaseAuth = FirebaseAuth.getInstance();

        //유저가 로그인 하지 않은 상태라면 null 상태이고 이 액티비티를 종료하고 로그인 액티비티를 연다.
        if (firebaseAuth.getCurrentUser() == null) {
            finish();
            //startActivity(new Intent(MyPage.this, Login.class));
        }

        //유저가 있다면, null이 아니면 계속 진행
        firebaseUser = firebaseAuth.getCurrentUser();

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference("Users");


//        ref.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                uid=snapshot.getValue(String.class);
//                System.out.println("uidddddddd"+uid);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
        //uid = "uWPz7kqWZOUIrcWzLaEQ9LntDcp1";
        uid = firebaseUser.getUid();
        System.out.println("uidddddddd" + uid);

        ref.child(uid).child("email").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                email = snapshot.getValue(String.class);
                mpEmail.setText(email);
                System.out.println("eeeeeeeeeeemail" + email);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ref.child(uid).child("nickname").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                nickname = snapshot.getValue(String.class);
                mpNickname.setText(nickname);
                System.out.println("nicknameeeeeee" + nickname);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // 비밀번호 변경
        setBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pwd = mpPwd.getText().toString().trim();
                System.out.println("pwddddddd" + pwd + pwd.getClass().getName());
                firebaseUser.updatePassword(pwd)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(MyPage.this, "비밀번호가 변경되었습니다.", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Toast.makeText(MyPage.this, "비밀번호 변경에 실패했습니다.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        // 회원 탈퇴
        deleteUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MyPage.this);
                builder.setTitle("회원 탈퇴").setMessage("정말로 탈퇴하시겠습니까?");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        firebaseUser.delete()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        ref.child(uid).setValue(null);
                                        Toast.makeText(MyPage.this, "정상적으로 탈퇴되었습니다.", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        Toast.makeText(MyPage.this, "회원 탈퇴에 실패했습니다.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(MyPage.this, "회원 탈퇴가 취소되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        finish();
        startActivity(new Intent(MyPage.this,MainActivity.class));
    }
}

