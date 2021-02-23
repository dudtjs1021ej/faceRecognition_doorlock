
package com.example.facerecognitionlock;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Fragment_FaceRecognition extends Fragment {
    private static final int RESULT_OK = 2;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    String mCurrentPhotoPath;
    final static int REQUEST_TAKE_PHOTO=10;

    ImageView iv=null;
    Button btn_takepic=null;
    Button btn_choose=null;
    Button btn_load=null;
    EditText editText;
    private Uri filepath;
    private String TAG;

    DatabaseReference mRootRef=FirebaseDatabase.getInstance().getReference();
    DatabaseReference conditionRef=mRootRef.child("name");

    public Fragment_FaceRecognition() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_face_recognition, container, false);
        // Inflate the layout for this fragment



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                            PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "권한 설정 완료");
            } else {
                Log.d(TAG, "권한 설정 요청");
                requestPermissions(new String[]
                        {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }

        //사진
        btn_takepic = (Button)v.findViewById(R.id.take_picture);
        iv = (ImageView)v.findViewById(R.id.image);
        editText=(EditText)v.findViewById(R.id.edit_name);
        btn_takepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                capture();
            }
        });
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText.setText("");
            }
        });

        //storage
        btn_choose = (Button)v.findViewById(R.id.bt_choose);
        btn_load = (Button)v.findViewById(R.id.bt_upload);

        btn_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                getActivity().startActivityForResult(Intent.createChooser(intent, "이미지를 선택하세요."), 0);
            }
        });

        btn_load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //conditionRef.setValue(editText.getText().toString());
                uploadFile();
            }
        });
        database=FirebaseDatabase.getInstance();

        databaseReference=database.getReference("User");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Register",String.valueOf(error.toException()));
            }
        });
        return v;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult");
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Permissson: " + permissions[0] + "was " + grantResults[0]);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //iv.setImageURI(data.getData())
        super.onActivityResult(requestCode, resultCode, data);
        for(Fragment fragment : getFragmentManager().getFragments()){
            fragment.onActivityResult(requestCode, resultCode, data);
        }
        //권한 설정
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            iv.setImageBitmap(imageBitmap);
        }

        //choose
        if (requestCode == 0 && resultCode == RESULT_OK) {
            filepath = data.getData();
            Log.d(TAG, "uri:" + String.valueOf(filepath));
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filepath);
                iv.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //take pic
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            File file = new File(mCurrentPhotoPath);
            Bitmap bitmap;
            if (Build.VERSION.SDK_INT >= 29) {
                ImageDecoder.Source source =
                        ImageDecoder.createSource(getActivity().getContentResolver(), Uri.fromFile(file));
                try {
                    bitmap = ImageDecoder.decodeBitmap(source);
                    if (bitmap != null) {
                        iv.setImageBitmap(bitmap);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), Uri.fromFile(file));
                    if (bitmap != null) {
                        iv.setImageBitmap(bitmap);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }
    private File createImageFile() throws IOException{
        String name=editText.getText().toString();
        String imageFileName= name;
        File storageDir=getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if(!storageDir.exists())storageDir.mkdirs();
        File image=File.createTempFile(imageFileName, ".jpg", storageDir);
        mCurrentPhotoPath=image.getAbsolutePath();
        return image;
    }

    public void capture(){
        Intent takePictureIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePictureIntent.resolveActivity(getActivity().getPackageManager())!=null){
            File photoFile = null;
            try{
                photoFile = createImageFile();
            } catch(IOException ex) {
                ex.printStackTrace();
            }
            if(photoFile!=null){
                Uri photoURI= FileProvider.getUriForFile(getActivity(),
                        "com.example.facerecognitionlock",photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,photoURI);
                getActivity().startActivityForResult(takePictureIntent,REQUEST_TAKE_PHOTO);
            }
        }
    }

    private void uploadFile(){
        if(filepath!=null){
            final ProgressDialog progressDialog=new ProgressDialog(getActivity());
            progressDialog.setTitle("UpLoading...");
            progressDialog.show();

            FirebaseStorage storage=FirebaseStorage.getInstance();

            String name=editText.getText().toString();
            conditionRef.setValue(name);
            String filename=name;

            StorageReference storageRef=storage.getReferenceFromUrl("gs://fir-connjava.appspot.com/").child("images/"+filename);

            storageRef.putFile(filepath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity().getApplicationContext(),"Upload Success!",Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity().getApplicationContext(),"Upload Fail!",Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @SuppressWarnings("VisibleForTests")
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            double progress=(100*snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                            progressDialog.setMessage("Uploaded "+ ((int)progress)+"%...");
                        }
                    });
        }else{
            Toast.makeText(getActivity().getApplicationContext(),"파일을 먼저 선택하세요.",Toast.LENGTH_SHORT).show();
        }
        editText.setText(" ");
    }

}