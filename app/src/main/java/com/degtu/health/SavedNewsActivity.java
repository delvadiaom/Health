package com.degtu.health;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.degtu.health.databinding.ActivityNewsBinding;
import com.degtu.health.databinding.ActivitySavedNewsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SavedNewsActivity extends AppCompatActivity {

    ActivitySavedNewsBinding binding;
    ArrayList<NewsModel> newsModels;
    NewsAdapter newsAdapter;
    NewsModel newsModel;
    DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySavedNewsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();

        binding.youhavenopost.setText("You have no any saved post.");
        binding.news.setVisibility(View.INVISIBLE);
        binding.removebutton.setVisibility(View.INVISIBLE);
        binding.abletoseesaednews.setVisibility(View.INVISIBLE);



        database = FirebaseDatabase.getInstance().getReference("Saves");

        String publishedAt = getIntent().getStringExtra("publishedAt");
        String uid = FirebaseAuth.getInstance().getUid();
        newsModels = new ArrayList<>();
        newsAdapter = new NewsAdapter(getApplicationContext(),newsModels);
        binding.news.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        binding.news.setAdapter(newsAdapter);

        assert uid != null;
        try {
            FirebaseDatabase.getInstance().getReference().child("Saves").child(uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    newsModels.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        NewsModel newsModel = dataSnapshot.getValue(NewsModel.class);
                        newsModels.add(newsModel);
                        binding.youhavenopost.setVisibility(View.INVISIBLE);
                        binding.removebutton.setVisibility(View.VISIBLE);
                        binding.abletoseesaednews.setVisibility(View.VISIBLE);
                        binding.abletoseesaednews.setText("Your saved posts");
                        binding.news.setVisibility(View.VISIBLE);
                    }
                    newsAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }catch (RuntimeException e){
            e.printStackTrace();
        }

        binding.removebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removepost();
                binding.youhavenopost.setVisibility(View.VISIBLE);
                binding.abletoseesaednews.setVisibility(View.INVISIBLE);
                binding.removebutton.setVisibility(View.INVISIBLE);
                FirebaseDatabase.getInstance().getReference().child("Saves").child(uid).removeValue();
                Toast.makeText(SavedNewsActivity.this, "Posts are removed", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void removepost() {
        ActivityNewsBinding.inflate(getLayoutInflater()).showsavednews.setImageResource(R.drawable.showsaved);
    }
}