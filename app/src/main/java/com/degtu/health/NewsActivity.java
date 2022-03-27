package com.degtu.health;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.degtu.health.databinding.ActivityNewsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewsActivity extends AppCompatActivity {

    ActivityNewsBinding binding;
    String api = "a065b672f9ce470c9c439a5586113f79";
    NewsAdapter newsAdapter;
    String country="in";
    ArrayList<NewsModel> newsModels;
    String category="health";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNewsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();

        String publishedAt = getIntent().getStringExtra("publishedAt");
        String title = getIntent().getStringExtra("title");


        newsModels = new ArrayList<>();
        newsAdapter = new NewsAdapter(getApplicationContext(),newsModels);
        binding.news.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        binding.news.setAdapter(newsAdapter);

        FirebaseDatabase.getInstance().getReference().child("Saves").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    binding.showsavednews.setImageResource(R.drawable.saved);
                }else {
                    binding.showsavednews.setImageResource(R.drawable.showsaved);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        APINewsUtilities.getAPINewsInterface().getNews(country,50,category,api).enqueue(new Callback<mainNews>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(Call<mainNews> call, Response<mainNews> response) {
                if (response.isSuccessful()){
                    newsModels.addAll(response.body().getArticles());
                    newsAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<mainNews> call, Throwable t) {

            }
        });

        binding.showsavednews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NewsActivity.this,SavedNewsActivity.class);
                intent.putExtra("publishedAt",publishedAt);
                startActivity(intent);
            }
        });

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}