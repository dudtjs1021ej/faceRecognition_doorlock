
package com.example.facerecognitionlock;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class Fragment_FaceRecognition extends Fragment {

    ArrayList<String> imgURLs=new ArrayList<>();

    Button button;
    ImageView iv1; ImageView iv2; ImageView iv3; ImageView iv4;
    Context thisContext;
    int i=-1;

    public Fragment_FaceRecognition() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_face_recognition, container, false);

        thisContext = container.getContext();
        button = (Button) view.findViewById(R.id.btn);
//        iv1 = (ImageView) view.findViewById(R.id.imageView1);
//        iv2 = (ImageView) view.findViewById(R.id.imageView4);
//        iv3 = (ImageView) view.findViewById(R.id.imageView3);
//        iv4 = (ImageView) view.findViewById(R.id.imageView2);
//        imgURLs.clear();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Face.class);
                startActivity(intent);
            }
        });

        StorageReference listRef = FirebaseStorage.getInstance().getReference().child("images/");





        // listAll(): 폴더 내의 모든 이미지를 가져오는 함수
        listRef.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {


                        // 폴더 내의 item이 동날 때까지 모두 가져온다.
                        for (StorageReference item : listResult.getItems()) {
                            LinearLayout userLinearLayout=(LinearLayout) view.findViewById(R.id.userLinearLayout);
                            ImageView iv=new ImageView(thisContext);

                            //iv.getLayoutParams().width = 200;
                            //iv.getLayoutParams().height = 200;
                           // iv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                            iv.setLayoutParams(new LayoutParams(500, 500));
                            iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
                            //userLinearLayout.addView(iv);

                            item.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                  //  int i=-1;
                                    if (task.isSuccessful()) {
                                        // Glide 이용하여 이미지뷰에 로딩
                                        System.out.println("task url:" + task.getResult());
                                        imgURLs.add(task.getResult().toString());
                                        i++;


                                        //Glide.with(thisContext).load(imgURLs.get(i)).into(iv);
                                       // Glide.with(thisContext).load(task.getResult()).into(iv);
                                        //System.out.println("");
                                        //Glide.with(thisContext)
                                          //      .load(task.getResult())
                                           //     .into(iv);
                                       // i++;

                                        //System.out.println("i="+i);
                                      // if(i==0) {
                                           LoadImage loadImage = new LoadImage(imgURLs.get(i));
                                           Bitmap bitmap = loadImage.getBitmap();
                                           iv.setImageBitmap(bitmap);
                                       //}
                                            //Glide.with(thisContext).load(imgURLs.get(i)).into(iv);
//                                        else if(i==1)
//                                            Glide.with(thisContext).load(imgURLs.get(i)).into(iv2);
//                                        else if(i==2)
//                                            Glide.with(thisContext).load(imgURLs.get(i)).into(iv3);
//                                        else if(i==3)
//                                            Glide.with(thisContext).load(imgURLs.get(i)).into(iv4);
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

        //for(int i=0;i<imgURLs.size();i++)
          //  System.out.println("Test URL : "+imgURLs.get(i));


//        Glide.with(thisContext).load(imgURLs.get(0)).into(iv1);
//        Glide.with(thisContext).load(imgURLs.get(1)).into(iv2);
//        Glide.with(thisContext).load(imgURLs.get(2)).into(iv3);
//        Glide.with(thisContext).load(imgURLs.get(3)).into(iv4);



        return view;
    }


    public class LoadImage {

        private String imgPath;
        private Bitmap bitmap;

        public LoadImage(String imgPath){
            this.imgPath = imgPath;
        }

        public Bitmap getBitmap(){
            Thread imgThread = new Thread(){
                @Override
                public void run(){
                    try {
                        URL url = new URL(imgPath);
                        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                        conn.setDoInput(true);
                        conn.connect();
                        InputStream is = conn.getInputStream();
                        bitmap = BitmapFactory.decodeStream(is);
                    }catch (IOException e){
                    }
                }
            };
            imgThread.start();
            try{
                imgThread.join();
            }catch (InterruptedException e){
                e.printStackTrace();
            }finally {
                return bitmap;
            }
        }

    }



}