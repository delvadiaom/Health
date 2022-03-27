package com.degtu.health;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.degtu.health.databinding.NewsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder>{

    private Context context;
    private ArrayList<NewsModel> newsModels;
    int share;
    View v;


    public NewsAdapter(Context context, ArrayList<NewsModel> newsModels) {
        this.context = context;
        this.newsModels = newsModels;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.news,parent,false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, @SuppressLint("RecyclerView") int position) {

        String uid = FirebaseAuth.getInstance().getUid();

        NewsModel newsModel = newsModels.get(position);
        holder.binding.headline.setText(newsModel.getTitle());
        DateFormat format = new SimpleDateFormat("MMM dd, yyyy");
        Calendar calendar = Calendar.getInstance();
        holder.binding.latestupdates.setText(format.format(calendar.getTime()));
        Glide.with(context).load(newsModel.getUrlToImage()).into(holder.binding.newsimg);

        String publishedAt = newsModel.getPublishedAt();
        String newsTitle = newsModels.get(position).getTitle();

        holder.binding.continuereading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,DetailNewsActivity.class);
                intent.putExtra("url",newsModels.get(position).getUrl());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

        holder.isLike(newsModel.getPublishedAt(),holder.binding.like);
        holder.numberofLikes(holder.binding.likes,newsModel.getPublishedAt());

        holder.binding.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (holder.binding.like.getTag().equals("like")){
                        try {
                            assert uid != null;
                            FirebaseDatabase.getInstance().getReference().child("Likes").child(newsModel.getPublishedAt()).child(uid).setValue(true);
                            Toast.makeText(context, "You liked a post", Toast.LENGTH_SHORT).show();
                        }catch (DatabaseException e){
                            e.printStackTrace();
                        }
                    }else {
                        try{
                            FirebaseDatabase.getInstance().getReference().child("Likes").child(newsModel.getPublishedAt()).child(uid).removeValue();
                            Toast.makeText(context, "You disliked a post", Toast.LENGTH_SHORT).show();
                        }catch (DatabaseException e){
                            e.printStackTrace();
                        }
                    }
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
            }
        });

        holder.binding.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    assert uid != null;
                    FirebaseDatabase.getInstance().getReference().child("Shares").child(newsModel.getPublishedAt()).child(uid).setValue(true);
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Shares").child(newsModel.getPublishedAt());
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.child(user.getUid()).exists()){
                                holder.binding.shares.setText((Integer.toString((int) snapshot.getChildrenCount())));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
            }
        });

        try {
            FirebaseDatabase.getInstance().getReference().child("Shares").child(newsModel.getPublishedAt()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        holder.binding.shares.setText(Integer.toString((int) snapshot.getChildrenCount()));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }catch (NullPointerException e){
            e.printStackTrace();
        }

        holder.binding.savethepost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                savedpost();
                FirebaseDatabase.getInstance().getReference().child("Saves").child(uid).child(publishedAt).setValue(newsModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(context, "Post has been saved", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }

    public boolean savedpost() {
        return true;
    }


    private void isSave(String titleofthenews, ImageView imageView){
        try {
            int i=0;
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Saves").child(titleofthenews);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    assert user != null;
                    if (snapshot.child(newsModels.get(i).getPublishedAt()).exists()){
                        imageView.setImageResource(R.drawable.saved);
                        imageView.setTag("saved");
                    }else {
                        imageView.setImageResource(R.drawable.save);
                        imageView.setTag("save");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }


    @Override
    public int getItemCount() {
        return newsModels.size();
    }

    public class NewsViewHolder extends RecyclerView.ViewHolder{

        NewsBinding binding;

        private void numberofsave(TextView saves, String title){
            try {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Saves").child(title);
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        saves.setText(Integer.toString((int) snapshot.getChildrenCount()));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }catch (NullPointerException e){
                e.printStackTrace();
            }
        }

        public void isLike(String newstitle, ImageView imageView){
            try {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Likes").child(newstitle);
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.child(user.getUid()).exists()){
                            imageView.setImageResource(R.drawable.heart);
                            imageView.setTag("liked");
                        }else {
                            imageView.setImageResource(R.drawable.like);
                            imageView.setTag("like");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }catch (NullPointerException e){
                e.printStackTrace();
            }
        }

        private void numberofLikes(TextView likes,String uid){
            try {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Likes").child(uid);
                reference.addValueEventListener(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        likes.setText(Integer.toString((int) snapshot.getChildrenCount()));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }catch (NullPointerException e){
                e.printStackTrace();
            }
        }

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);

            binding = NewsBinding.bind(itemView);
        }
    }
}
