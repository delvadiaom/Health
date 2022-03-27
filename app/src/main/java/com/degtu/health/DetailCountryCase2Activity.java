package com.degtu.health;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.degtu.health.databinding.ActivityDetailCountryCase2Binding;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DetailCountryCase2Activity extends AppCompatActivity {

    ActivityDetailCountryCase2Binding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailCountryCase2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();


        String cases = getIntent().getStringExtra("confirm");
        String flag = getIntent().getStringExtra("flag");
        String countryname = getIntent().getStringExtra("name");
        String active = getIntent().getStringExtra("active");
        String recovered = getIntent().getStringExtra("recovered");
        String deaths = getIntent().getStringExtra("deaths");
        String tests = getIntent().getStringExtra("tests");
        String todaycase = getIntent().getStringExtra("todaycase");
        String todayrecovered = getIntent().getStringExtra("todayrecovered");
        String todaydeath = getIntent().getStringExtra("todaydeath");
        String critical = getIntent().getStringExtra("critical");
        String caseperonemillion = getIntent().getStringExtra("caseperonemillion");
        String deathperonemillion = getIntent().getStringExtra("deathperonemillion");
        String testsperonemillion = getIntent().getStringExtra("testsperonemillion");
        String oneCasePerPeople = getIntent().getStringExtra("oneCasePerPeople");
        String oneDeathPerPeople = getIntent().getStringExtra("oneDeathPerPeople");
        String oneTestPerPeople = getIntent().getStringExtra("oneTestPerPeople");
        String activePerOneMillion = getIntent().getStringExtra("activePerOneMillion");
        String recoveredPerOneMillion = getIntent().getStringExtra("recoveredPerOneMillion");
        String criticalPerOneMillion = getIntent().getStringExtra("criticalPerOneMillion");
        String population = getIntent().getStringExtra("population");
        String continent = getIntent().getStringExtra("continent");
        String updated = getIntent().getStringExtra("updated");
        String countryimage = getIntent().getStringExtra("countryimage");


        binding.countryname.setText(countryname);
        binding.confirm.setText(NumberFormat.getInstance().format(Integer.parseInt(cases)));
        binding.deaths.setText(NumberFormat.getInstance().format(Integer.parseInt(deaths)));
        binding.active.setText(NumberFormat.getInstance().format(Integer.parseInt(active)));
        binding.recovered.setText(NumberFormat.getInstance().format(Integer.parseInt(recovered)));
        binding.tests.setText(NumberFormat.getInstance().format(Integer.parseInt(tests)));
        binding.critical.setText(NumberFormat.getInstance().format(Integer.parseInt(critical)));
        binding.casesperonemillion.setText(NumberFormat.getInstance().format(Integer.parseInt(caseperonemillion)));
        DateFormat format = new SimpleDateFormat("MMM dd, yyyy");
        long milliseconds = Long.parseLong(updated);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliseconds);
        binding.date.setText("Updated At " + format.format(calendar.getTime()));


        binding.toolcountry.setText(countryname + " - statistic");
        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}