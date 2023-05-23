package com.example.healthappttt.Fragment;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.healthappttt.Data.RetrofitClient;
import com.example.healthappttt.R;
import com.example.healthappttt.interface_.ServiceApi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class ProfileFragment extends Fragment {
    // 파이어스토어에 접근하기 위한 객체 생성
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;// 파이어베이스 유저관련 접속하기위한 변수
    private FirebaseStorage storage;
    private StorageReference sref;
    private static int progress_percent;
//    private UserInfo mDataset;
//    private Context mContext;

    ServiceApi apiService = RetrofitClient.getClient().create(ServiceApi.class);


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        SharedPreferences sharedPref = getActivity().getSharedPreferences("myPref", Context.MODE_PRIVATE);
        String useremail = sharedPref.getString("useremail", "");
        // Inflate the layout for this fragment
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_proflie, container, false);
        // 아이디 연결
        FrameLayout ProfileFrame = view.findViewById(R.id.ProfileFrame);

        TextView mytempreture2 = ProfileFrame.findViewById(R.id.MyTempreture2);
        TextView username = ProfileFrame.findViewById(R.id.UserName);
        TextView locationname = ProfileFrame.findViewById(R.id.MyLocation);
        TextView squat = ProfileFrame.findViewById(R.id.squat);
        TextView bench = ProfileFrame.findViewById(R.id.benchpress);
        TextView deadlift = ProfileFrame.findViewById(R.id.deadlift);
        ImageView userImg = ProfileFrame.findViewById(R.id.UserImg);
        ProgressBar mtprogresser = ProfileFrame.findViewById(R.id.MTProgresser);
        mAuth = FirebaseAuth.getInstance();


        //CollectionReference -> 파이어스토어의 컬랙션 참조하는 객체
        DocumentReference productRef = db.collection("users").document(mAuth.getCurrentUser().getUid());
        //get()을 통해서 해당 컬랙션의 정보를 가져옴

        //단일 문서의 내용 검색
        productRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) { // 데이터가 존재할 경우
//                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                        mytempreture2.setText(document.getData().get("userTemperature").toString().concat("km"));
                        username.setText(document.getData().get("userName").toString());
                        bench.setText(document.getData().get("bench").toString());
                        deadlift.setText(document.getData().get("deadlift").toString());
                        squat.setText(document.getData().get("squat").toString());
                        locationname.setText(document.getData().get("locationName").toString());

//                      TODO 아직 미완성 단계

//                      Glide.with(getView()).load(document.getData().get("profileImg")).fitCenter().into(userImg);

//                      document.getData().get("key").toString();
//                      document.getData().get("GoodTime").toString();


                        progress_percent = 0;
                        String dcutempre = document.getData().get("userTemperature").toString();
                        new Thread() {
                            public void run() {
                                while (true) {
                                    try {
                                        while (!Thread.currentThread().isInterrupted()) {
                                            progress_percent += 1;
                                            Thread.sleep(5);
                                            Log.d("test", "progress_percent" + progress_percent);
                                            mtprogresser.setProgress(progress_percent);
                                            // TODO 유저온도가 소수일 경우도 처리 가능해야함
                                            if (progress_percent >= Double.parseDouble(dcutempre)) {
                                                currentThread().interrupt();
                                            }
                                        }
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }.start();


                    } else {
                        Log.d(TAG, "No such document!");
                    }
                } else {
                    Log.d(TAG, "get failed with", task.getException());
                }
            }
        });

        String fileName = mAuth.getCurrentUser().getUid();

        File profilefile = null;

        try {
            profilefile = File.createTempFile("images", "jpeg");
        } catch (IOException e) {
            e.printStackTrace();
        }
        storage = FirebaseStorage.getInstance();
        StorageReference sref = storage.getReference().child("article/photo").child(fileName);
        File finalProfilefile = profilefile;
        sref.getFile(profilefile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                // Local temp file has been created
                Glide.with(getContext()).load(finalProfilefile).into(userImg);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });

        return view;
    }


}