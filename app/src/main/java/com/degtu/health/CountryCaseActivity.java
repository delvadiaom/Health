package com.degtu.health;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.degtu.health.databinding.ActivityCountryCaseBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CountryCaseActivity extends AppCompatActivity {

    ActivityCountryCaseBinding binding;
    CountryAdapter countryAdapter;
    private List<CountryData> countryData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCountryCaseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();

        countryData = new ArrayList<>();
        countryAdapter = new CountryAdapter(this,countryData);
        binding.countries.setLayoutManager(new LinearLayoutManager(this));
        binding.countries.setHasFixedSize(true);
        binding.countries.setAdapter(countryAdapter);

        APICountryUtilities.getAPIinterface().getCountryData().enqueue(new Callback<List<CountryData>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(Call<List<CountryData>> call, Response<List<CountryData>> response) {
                assert response.body() != null;
                try {
                    countryData.addAll(response.body());
                    countryAdapter.notifyDataSetChanged();
                }catch (Exception e){
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<List<CountryData>> call, Throwable t) {
                Toast.makeText(CountryCaseActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
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