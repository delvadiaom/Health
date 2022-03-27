package com.degtu.health;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.degtu.health.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    ActivityMainBinding binding;
    QuestionAdapter questionAdapter;
    ArrayList<Question> questionArrayList;
    FirebaseFirestore firebaseFirestore;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle drawerToggle;
    NavigationView navigationView;
    String IP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);


        drawerLayout = findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
        navigationView = findViewById(R.id.nav_view);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        firebaseFirestore = FirebaseFirestore.getInstance();


        questionArrayList = new ArrayList<>();
        questionAdapter = new QuestionAdapter(getApplicationContext(),questionArrayList);
        binding.questions.setHasFixedSize(true);
        binding.questions.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        binding.questions.setAdapter(questionAdapter);

        firebaseFirestore.collection("questions").addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error!=null){
                    return;
                }else {
                    if (!value.isEmpty()) {
                        assert value != null;
                        for (DocumentChange documentChange : value.getDocumentChanges()) {
                            questionArrayList.add(documentChange.getDocument().toObject(Question.class));
                            questionAdapter.notifyDataSetChanged();
                        }
                    }else {
                        binding.questions.setVisibility(View.INVISIBLE);
                        binding.noanyrequests.setVisibility(View.VISIBLE);
                        Toast.makeText(MainActivity.this, "No any issues", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        binding.bottomNavigationView.setSelectedItemId(R.id.questions);

        binding.bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.questions:
                        return true;

                    case R.id.upload:
                        startActivity(new Intent(getApplicationContext(),UploadActivity.class));
                        finishAfterTransition();
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(drawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_covidCasesInfo){

            Intent intent = new Intent(this,CountryCaseActivity.class);
                startActivity(intent);
                drawerLayout.closeDrawer(GravityCompat.START);
                item.setChecked(false);
                return true;
        }else if (item.getItemId() == R.id.nav_share){
            Intent intent = new Intent(this,NewsActivity.class);
            startActivity(intent);
            drawerLayout.closeDrawer(GravityCompat.START);
            item.setChecked(false);
            return true;
        }else if ((item.getItemId() == R.id.logout)){
            Intent intent = new Intent(this,ChooseRoleActivity.class);
            startActivity(intent);
            drawerLayout.closeDrawer(GravityCompat.START);
            item.setChecked(false);
            Toast.makeText(this, "Logged Out", Toast.LENGTH_SHORT).show();
            firebaseFirestore.collection("users").document(IP).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (value.exists()){
                        String ipiip = value.getString("ipuser");
                        if (IP.equals(ipiip)){
                            firebaseFirestore.collection("users").document(IP).delete();
                        }
                    }
                }
            });
            firebaseFirestore.collection("doctors").document(IP).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (value.exists()){
                        String ipiip = value.getString("ipdoctor");
                        if (IP.equals(ipiip)){
                            firebaseFirestore.collection("doctors").document(IP).delete();
                        }
                    }
                }
            });
            return true;
        }
        return false;
    }
}