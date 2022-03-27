package com.degtu.health;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Toast;

import com.degtu.health.databinding.ActivityDetailQuestionBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

public class DetailQuestionActivity extends AppCompatActivity {

    ActivityDetailQuestionBinding binding;
    String IP;
    FirebaseFirestore firebaseFirestore;
    String flag="0";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailQuestionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new  NotificationChannel("My Noti","My Notification", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        getSupportActionBar().hide();

        firebaseFirestore = FirebaseFirestore.getInstance();

        String title = getIntent().getStringExtra("title");
        String question = getIntent().getStringExtra("question");

        binding.title.setText(title);
        binding.question.setText(question);

        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            URL myIp = new URL("https://checkip.amazonaws.com//");

            URLConnection connection = myIp.openConnection();
            connection.setConnectTimeout(1000);
            connection.setReadTimeout(1000);

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            IP = bufferedReader.readLine();
        }catch (MalformedURLException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        firebaseFirestore.collection("questions").document(title).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value.exists()){
                    String namee = value.getString("nameofuser");
                    binding.username.setText(namee);
                }
            }
        });

        firebaseFirestore.collection("users").document(IP).addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value.exists()){
                    String ipp = value.getString("ipuser");
                    if (IP.equals(ipp)){
                        binding.solvebtn.setVisibility(View.INVISIBLE);
                        binding.solveedittext.setVisibility(View.INVISIBLE);
                        binding.editsolution.setVisibility(View.INVISIBLE);
                        binding.savesolution.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });

        firebaseFirestore.collection("doctors").document(IP).addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value.exists()){
                    String ippdoc = value.getString("ipdoctor");
                    if (IP.equals(ippdoc)){
                        binding.solvebtn.setVisibility(View.VISIBLE);
                        binding.solveedittext.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        binding.solvebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String solution = binding.solveedittext.getText().toString();
                Map<String,Object> sol = new HashMap<>();
                sol.put("solution",solution);
                sol.put("flagg",flag);
                firebaseFirestore.collection("solution").document(title).set(sol).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(DetailQuestionActivity.this, "Your solution has been added", Toast.LENGTH_SHORT).show();
                    }
                });
                binding.answer.setText(solution);
                binding.answer.setVisibility(View.VISIBLE);
                binding.solveedittext.setVisibility(View.INVISIBLE);
                binding.solvebtn.setVisibility(View.INVISIBLE);
                firebaseFirestore.collection("questions").document(title).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (value.exists()){
                            String ipuser = value.getString("ipuser");
                            if (IP.equals(ipuser)){
                                NotificationCompat.Builder builder = new NotificationCompat.Builder(DetailQuestionActivity.this,"My Noti");
                                builder.setContentTitle("Health is wealth");
                                builder.setSmallIcon(R.drawable.covid19);
                                builder.setContentText("You must get vaccinated with your two doses. See other precautions that must be taken by you.");
                                builder.setAutoCancel(true);
                                Intent intent = new Intent(DetailQuestionActivity.this,MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                                @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendingIntent =  PendingIntent.getActivity(DetailQuestionActivity.this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
                                builder.setContentIntent(pendingIntent);

                                NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(DetailQuestionActivity.this);
                                notificationManagerCompat.notify(1,builder.build());
                            }
                        }
                    }
                });
            }
        });

        binding.editsolution.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseFirestore.collection("solution").document(title).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (value.exists()){
                            String flg = value.getString("flagg");
                            if (flg.equals("0")){
                                binding.answer.setVisibility(View.VISIBLE);
                                binding.editsolution.setVisibility(View.INVISIBLE);
                                binding.savesolution.setVisibility(View.VISIBLE);
                                binding.solveedittext.setVisibility(View.VISIBLE);
                                String takeoldans = binding.answer.getText().toString();
                                binding.solveedittext.setText(takeoldans);
                            }
                        }
                    }
                });
            }
        });

        binding.savesolution.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String takenew = binding.solveedittext.getText().toString();
                firebaseFirestore.collection("solution").document(title).update("solution",takenew);
                binding.solveedittext.setVisibility(View.INVISIBLE);
                binding.editsolution.setVisibility(View.VISIBLE);
                binding.answer.setText(takenew);
            }
        });

        firebaseFirestore.collection("solution").document(title).addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value.exists()){
                    String soll = value.getString("solution");
                    binding.answer.setText(soll);
                    binding.solvebtn.setVisibility(View.INVISIBLE);
                    binding.solveedittext.setVisibility(View.INVISIBLE);
                }
            }
        });
    }
}