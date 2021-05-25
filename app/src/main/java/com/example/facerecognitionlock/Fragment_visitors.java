package com.example.facerecognitionlock;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
//import com.kakao.sdk.template.model.FeedTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class Fragment_visitors extends Fragment {
    ImageButton button;
    Boolean bAlarm;
    String imgUrl=null;
    String visitTime=null;


    NotificationCompat.Builder builder;
    NotificationCompat.BigPictureStyle style;
    NotificationManager notificationManager;

    Intent push;
    PendingIntent fullScreenPendingIntent;
    Context thisContext;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Visitors> arrayList=new ArrayList<>();
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;



    public Fragment_visitors() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_visitors, container, false);
        button=view.findViewById(R.id.refreshButton3);

        recyclerView=view.findViewById(R.id.recyclerview);


        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);


        thisContext = container.getContext();

        visitorList();

        DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference conditionRef = mRootRef.child("alarm");
        conditionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bAlarm=snapshot.getValue(Boolean.class);
                System.out.println("bAlarm:" + bAlarm);
                System.out.println("type" + bAlarm.getClass().getName());

                if(bAlarm == true) {
                    LoadImage loadImage = new LoadImage(imgUrl);
                    System.out.println("imgUrlllll:" + imgUrl);
                    Bitmap bitmap = loadImage.getBitmap();
                    push = new Intent(thisContext, MainActivity.class);
                    fullScreenPendingIntent = PendingIntent.getActivity(thisContext, 0, push, PendingIntent.FLAG_CANCEL_CURRENT);
                    builder = new NotificationCompat.Builder(thisContext, "headup");

                    builder.setSmallIcon(R.drawable.label);
                    builder.setLargeIcon(bitmap);
                    builder.setContentTitle("출입 감지");
                    builder.setContentText(visitTime); // 방문 시간
                    // 사용자가 클릭시 자동 제거
                    builder.setAutoCancel(true);
                    // 최고 우선순위-head up
                    builder.setPriority(Notification.PRIORITY_MAX);
                    builder.setCategory(NotificationCompat.CATEGORY_MESSAGE);

                    style = new NotificationCompat.BigPictureStyle();
                    style.bigPicture(bitmap); // 방문자 사진
                    builder.setStyle(style);
                    builder.setFullScreenIntent(fullScreenPendingIntent, true);

                    // 알림 표시
                    notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        notificationManager.createNotificationChannel(new NotificationChannel("headup", "헤드업 채널", NotificationManager.IMPORTANCE_HIGH));
                    }
                    notificationManager.notify(12, builder.build());
                    System.out.println("alarm2");
                    bAlarm = false;
                    conditionRef.setValue(bAlarm);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        // 새로고침버튼
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
              visitorList();
            }

        });
        return view;
    }



    public void visitorList(){ //방문자 목록 불러오기
        database=FirebaseDatabase.getInstance(); //파이어베이스 데이터베이스 연동

        databaseReference=database.getReference("Visitors"); //DB연결
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //파이어베이스 데이터베이스 get
                arrayList.clear(); //기존 배열리스트 초기화

                for(DataSnapshot snapshot1 : dataSnapshot.getChildren()){ //realtime database의 Visitors에 가서 모든 항목 다 가져옴
                    Visitors visitor=snapshot1.getValue(Visitors.class);
                    arrayList.add(visitor);
                }
                adapter.notifyDataSetChanged();

                for(Visitors item:arrayList){
                    imgUrl = item.getprofile();
                    visitTime = item.getTime();
                    System.out.println("사진: "+imgUrl+" 시간 : "+visitTime);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //db가져올 떄 에러가 난 경우
                Log.e("MainActivity", String.valueOf(error.toException()));

            }
        });
        adapter=new VisitorsAdapter(arrayList,getActivity());
        recyclerView.setAdapter(adapter);

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