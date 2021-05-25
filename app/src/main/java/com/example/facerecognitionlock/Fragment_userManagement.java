
package com.example.facerecognitionlock;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.view.ViewGroup.LayoutParams;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;


public class Fragment_userManagement extends Fragment {

    ArrayList<String> imgURLs=new ArrayList<>();
    ImageButton button;
    ImageButton refreshButton;
    LinearLayout userLinearLayout;
    Context thisContext;
    int i=-1;

    public Fragment_userManagement() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_user_management, container, false);
        thisContext = container.getContext();
        button = (ImageButton) view.findViewById(R.id.btn);
        refreshButton = (ImageButton) view.findViewById(R.id.refreshButton);
        userLinearLayout=(LinearLayout) view.findViewById(R.id.userLinearLayout);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), UserRegisterActivity.class);
                startActivity(intent);
            }
        });

        refreshButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                userLinearLayout.removeAllViews();
                listAll();
            }
        });
        listAll();

        return view;
    }
    public void listAll(){
        StorageReference listRef = FirebaseStorage.getInstance().getReference().child("images/");
        // listAll(): 폴더 내의 모든 이미지를 가져오는 함수
        listRef.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {


                        // 폴더 내의 item이 동날 때까지 모두 가져온다.
                        for (StorageReference item : listResult.getItems()) {

                            ImageView iv=new ImageView(thisContext);
                            iv.setLayoutParams(new LayoutParams(500, 500));
                            iv.setScaleType(ImageView.ScaleType.FIT_CENTER);

                            item.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        // Glide 이용하여 이미지뷰에 로딩
                                        System.out.println("task url:" + task.getResult());
                                        imgURLs.add(task.getResult().toString());
                                        i++;
                                        Glide.with(thisContext).load(imgURLs.get(i)).into(iv);
                                        userLinearLayout.addView(iv);
                                    } else {
                                        // URL을 가져오지 못하면 토스트 메세지
                                        Toast.makeText(thisContext, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }


                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Uh-oh, an error occurred!
                                }
                            });
                        }
                    }
                });
    }
}