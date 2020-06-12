package com.example.huaweitaskapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.huaweitaskapp.POJOClasses.DailyClass;

import java.util.List;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MainView> {

    List<DailyClass> dailyWeatherList;

    public MainAdapter(List<DailyClass> dailyWeatherList){
        this.dailyWeatherList = dailyWeatherList;
    }

    @NonNull
    @Override
    public MainAdapter.MainView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_inside_layout, parent, false);
        return new MainAdapter.MainView(layoutView);
    }

    class MainView  extends RecyclerView.ViewHolder {
        TextView mainTextView;
        TextView descriptionTextView;
        TextView dayTempTextView;
        TextView numericalValueTextView;

        public MainView(View itemView) {

            super(itemView);

            mainTextView = itemView.findViewById(R.id.mainTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            dayTempTextView = itemView.findViewById(R.id.dayTempTextView);
            numericalValueTextView = itemView.findViewById(R.id.numericalValueTextView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final MainAdapter.MainView holder, int position) {
        holder.mainTextView.setText(dailyWeatherList.get(position).getWeather().get(0).getMain());
        holder.descriptionTextView.setText(dailyWeatherList.get(position).getWeather().get(0).getDescription());

        String dayTemp = dailyWeatherList.get(position).getTemperatures().getDay() + "°C";
        holder.dayTempTextView.setText(dayTemp);
        String numericalValue = "Day " + (position + 1);
        holder.numericalValueTextView.setText(numericalValue);
    }

    @Override
    public int getItemCount() {
        return Math.min(dailyWeatherList.size(), 7);
    }
}
