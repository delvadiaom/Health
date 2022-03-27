package com.degtu.health;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.degtu.health.databinding.CountryItemBinding;

import java.text.NumberFormat;
import java.util.List;
import java.util.Map;

public class CountryAdapter extends RecyclerView.Adapter<CountryAdapter.CountryViewHolder> {

    private Context context;
    private List<CountryData> list;

    public CountryAdapter(Context context, List<CountryData> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public CountryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.country_item,parent,false);
        return new CountryViewHolder(view);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filterList(List<CountryData> filterList){
        list = filterList;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull CountryViewHolder holder, int position) {

        CountryData countryData = list.get(position);
        holder.binding.cases.setText(NumberFormat.getInstance().format(Integer.parseInt(countryData.getCases())));
        holder.binding.countryname.setText(countryData.getCountry());

        Map<String,String> img = countryData.getCountryInfo();
        String url = img.get("flag");
        Glide.with(context).load(url).placeholder(R.drawable.placeholdercountry).into(holder.binding.countryImage);
        int cases = Integer.parseInt(countryData.getCases());
        if(cases>=50000){
            holder.binding.arrow.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24);
        }else {
            holder.binding.arrow.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,DetailCountryCase2Activity.class);
                intent.putExtra("flag",url);
                intent.putExtra("confirm",countryData.getCases());
                intent.putExtra("active",countryData.getActive());
                intent.putExtra("recovered",countryData.getRecovered());
                intent.putExtra("deaths",countryData.getDeaths());
                intent.putExtra("tests",countryData.getTests());
                intent.putExtra("todaycase",countryData.getTodayCases());
                intent.putExtra("todayrecovered",countryData.getTodayRecovered());
                intent.putExtra("countryimage",img.get("flag"));
                intent.putExtra("critical",countryData.getCritical());
                intent.putExtra("caseperonemillion",countryData.getCasesPerOneMillion());
                intent.putExtra("deathperonemillion",countryData.getDeathsPerOneMillion());
                intent.putExtra("testsperonemillion",countryData.getTestsPerOneMillion());
                intent.putExtra("oneCasePerPeople",countryData.getOneCasePerPeople());
                intent.putExtra("oneDeathPerPeople",countryData.getOneDeathPerPeople());
                intent.putExtra("oneTestPerPeople",countryData.getOneTestPerPeople());
                intent.putExtra("activePerOneMillion",countryData.getActivePerOneMillion());
                intent.putExtra("recoveredPerOneMillion",countryData.getRecoveredPerOneMillion());
                intent.putExtra("criticalPerOneMillion",countryData.getCriticalPerOneMillion());
                intent.putExtra("population",countryData.getPopulation());
                intent.putExtra("continent",countryData.getContinent());
                intent.putExtra("updated",countryData.getUpdated());
                intent.putExtra("todaydeath",countryData.getTodayDeaths());
                intent.putExtra("name",countryData.getCountry());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class CountryViewHolder extends RecyclerView.ViewHolder{

        CountryItemBinding binding;

        public CountryViewHolder(@NonNull View itemView) {
            super(itemView);

            binding = CountryItemBinding.bind(itemView);
        }
    }
}
