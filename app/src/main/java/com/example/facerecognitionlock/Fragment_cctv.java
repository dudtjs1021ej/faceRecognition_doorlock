package com.example.facerecognitionlock;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
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
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kakao.sdk.template.model.FeedTemplate;

import java.util.ArrayList;

import static android.content.Context.NOTIFICATION_SERVICE;


public class Fragment_cctv extends Fragment {
    Button button;
    Button alarm;

   // NotificationManager manager;
    NotificationCompat.Builder builder;
    //private static String CHANNEL_ID = "channel1";
    //private static String CHANEL_NAME = "Channel1";

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Visitors> arrayList;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    public Fragment_cctv() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup view=(ViewGroup)inflater.inflate(R.layout.fragment_cctv, container, false);

        button=(Button)view.findViewById(R.id.button);
        alarm=(Button)view.findViewById(R.id.button2);
        recyclerView=view.findViewById(R.id.recyclerview);

        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        arrayList=new ArrayList<>();
        visitorList();

        PendingIntent plntent = PendingIntent.getActivity(getActivity(), 0, new Intent(getActivity().getApplicationContext(), MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        builder = new NotificationCompat.Builder(getActivity(), "channel1")
                .setOngoing(true) //노티피케이션유지
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("주문알림")
                .setContentText("주문이 들어왔어요")
                .setDefaults(Notification.DEFAULT_SOUND)
                //.setLargeIcon(mLamg)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(plntent);

        //System.out.println("testtest"+arrayList.get(0).toString());
/*
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReferenceFromUrl("gs://fir-connjava.appspot.com");

        //다운로드할 파일을 가르키는 참조 만들기
        StorageReference pathReference = storageReference.child("visitors/visitor.jpg");

        //Url을 다운받기
        pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Toast.makeText(getActivity().getApplicationContext(), "다운로드 성공 : "+ uri, Toast.LENGTH_SHORT).show();
               // inputName.setText(uri.toString());

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity().getApplicationContext(), "다운로드 실패", Toast.LENGTH_SHORT).show();
            }
        });
        //----storage에서 이미지 불러오기---------------------------------------------------------

 */


        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
              visitorList();
            }

        });

        alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotificationManager notiMan = (NotificationManager) getActivity().getSystemService(NOTIFICATION_SERVICE);
                if (Build.VERSION.SDK_INT >= 26)
                {
                    notiMan.createNotificationChannel(new NotificationChannel("chnotl", "채널", NotificationManager.IMPORTANCE_DEFAULT));
                }
                notiMan.notify(1004, builder.build());
            }
        });


        return view;
    }

    public void visitorList(){ //방문자 목록 불러오기
        database=FirebaseDatabase.getInstance(); //파이어베이스 데이터베이스 연동

        databaseReference=database.getReference("Visitors"); //DB연결
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //파이어베이스 데이터베이스 get
                arrayList.clear(); //기존 배열리스트 초기화

                for(DataSnapshot snapshot1 : dataSnapshot.getChildren()){ //realtime database의 Visitors에 가서 모든 항목 다 가져옴
                    Visitors visitor=snapshot1.getValue(Visitors.class);
                    arrayList.add(visitor);
                    //System.out.println("testtest"+arrayList.get(0).toString());
                }
                adapter.notifyDataSetChanged();

                for(Visitors item:arrayList){
                    System.out.println("사진: "+item.getprofile()+" 시간 : "+item.getTime());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //db가져올 떄 에러가 난 경우
                Log.e("MainActivity", String.valueOf(error.toException()));

            }
        });
        adapter=new CustomAdapter(arrayList,getActivity());
        recyclerView.setAdapter(adapter);

    }




}