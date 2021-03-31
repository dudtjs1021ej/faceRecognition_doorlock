package com.example.facerecognitionlock;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.os.Environment.DIRECTORY_PICTURES;

public class Face extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    //String mCurrentPhotoPath;
    final static int REQUEST_TAKE_PHOTO=10;

    ImageView iv=null;
    Button btn_takepic=null;
    Button btn_choose=null;
    Button btn_load=null;
    EditText editText;
    private Uri filepath;

    DatabaseReference mRootRef=FirebaseDatabase.getInstance().getReference();
    DatabaseReference conditionRef = mRootRef.child("name");
    DatabaseReference conditionValue = mRootRef.child("start_value");
    private String TAG;

    private MediaScanner mMediaScanner;
    private String imageFilePath;
    //private Uri photoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face);

        mMediaScanner=MediaScanner.getInstance(getApplicationContext());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                            PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "권한 설정 완료");
            } else {
                Log.d(TAG, "권한 설정 요청");
                ActivityCompat.requestPermissions(Face.this, new String[]
                        {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }

        //사진
        btn_takepic = (Button) findViewById(R.id.take_picture);
        iv = (ImageView) findViewById(R.id.image);
        editText=(EditText)findViewById(R.id.edit_name);
        btn_takepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                capture();
            }
        });


        //storage
        btn_choose = (Button) findViewById(R.id.bt_choose);
        btn_load = (Button) findViewById(R.id.bt_upload);

        btn_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "이미지를 선택하세요."), 0);
            }
        });

        btn_load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult");
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Permissson: " + permissions[0] + "was " + grantResults[0]);
        }
    }

    private File createImageFile() throws IOException{
        String name;
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HHmmss", Locale.getDefault());
        Date curDate=new Date(System.currentTimeMillis());
        String num=format.format(curDate);

        if(editText.length()==0){
            name="User"+num;
        }else{
            name=editText.getText().toString();
        }
        String imageFileName= name + "_";

        File storageDir=getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image=File.createTempFile(imageFileName, ".jpg", storageDir);
        //mCurrentPhotoPath=image.getAbsolutePath();
        imageFilePath=image.getAbsolutePath();
        return image;
    }

    public void capture(){
        Intent takePictureIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePictureIntent.resolveActivity(getPackageManager())!=null){
            File photoFile = null;
            try{
                photoFile = createImageFile();
            } catch(IOException ex) {
                ex.printStackTrace();
            }
            if(photoFile!=null){
                filepath= FileProvider.getUriForFile(getApplicationContext(),getPackageName(),photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,filepath);
                startActivityForResult(takePictureIntent,REQUEST_TAKE_PHOTO);
            }
        }
    }

    private void uploadFile(){
        if(filepath!=null){
            final ProgressDialog progressDialog=new ProgressDialog(this);
            progressDialog.setTitle("UpLoading...");
            progressDialog.show();

            FirebaseStorage storage=FirebaseStorage.getInstance();
            String name;
            if(editText.length()==0){
                name="User";
            }else{
                name=editText.getText().toString().trim();
            }
            conditionRef.setValue(name);
            conditionValue.setValue("True");
            String filename=name+"1.jpg";
            StorageReference storageRef=storage.getReferenceFromUrl("gs://fir-connjava.appspot.com/").child("images/"+filename);

            storageRef.putFile(filepath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(),"Upload Success!",Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(),"Upload Fail!",Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getApplicationContext(),"파일을 먼저 선택하세요.",Toast.LENGTH_SHORT).show();
        }
        editText.setText(" ");
    }
    private int exifOrientationToDegress(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    private Bitmap rotate(Bitmap bitmap, float degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //check the permission
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            iv.setImageBitmap(imageBitmap);
        }

        //take pic
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            Bitmap bitmap = BitmapFactory.decodeFile(imageFilePath);
            ExifInterface exif = null;

            try {
                exif = new ExifInterface(imageFilePath);
            } catch (IOException e) {
                e.printStackTrace();
            }

            int exifOrientation;
            int exifDegree;

            if(exif !=null){
                exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                exifDegree=exifOrientationToDegress(exifOrientation);
            }else{
                exifDegree=0;
            }

            String result = "";

            String filename;
            SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HHmmss", Locale.getDefault());
            Date curDate=new Date(System.currentTimeMillis());
            String num=format.format(curDate);

            if(editText.length()==0){
                filename="User"+num;
            }else{
                filename=editText.getText().toString();
            }

            String strFolderName = Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES) + File.separator + "LockLock" + File.separator;
            File file = new File(strFolderName);
            if( !file.exists() )
                file.mkdirs();

            File f = new File(strFolderName + "/" + filename + ".jpg");
            result = f.getPath();

            FileOutputStream fOut = null;
            try {
                fOut = new FileOutputStream(f);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                result = "Save Error fOut";
            }

            // 비트맵 사진 폴더 경로에 저장
            rotate(bitmap,exifDegree).compress(Bitmap.CompressFormat.JPEG,70, fOut);
            //bitmap.compress(Bitmap.CompressFormat.JPEG,70, fOut);

            try {
                fOut.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fOut.close();
                // 방금 저장된 사진을 갤러리 폴더 반영 및 최신화
                mMediaScanner.mediaScanning(strFolderName + "/" + filename + ".jpg");
            } catch (IOException e) {
                e.printStackTrace();
                result = "File close Error";
            }
            iv.setImageBitmap(rotate(bitmap,exifDegree));
            //iv.setImageBitmap(bitmap);
        }


        //image choose
        if (requestCode == 0 && resultCode == RESULT_OK) {
            filepath = data.getData();
            Log.d(TAG, "uri:" + String.valueOf(filepath));
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filepath);
                iv.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}