package com.example.oopandroidapi.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.oopandroidapi.R;
import com.example.oopandroidapi.data.CompanyData;
import com.example.oopandroidapi.data.MunicipalityData;
import com.example.oopandroidapi.data.TrafficData;
import com.example.oopandroidapi.data.WeatherData;
import com.example.oopandroidapi.retriever.CompanyDataRetriever;
import com.example.oopandroidapi.retriever.MunicipalityDataRetriever;
import com.example.oopandroidapi.retriever.TrafficDataRetriever;
import com.example.oopandroidapi.retriever.WeatherDataRetriever;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class InformantionActivity extends AppCompatActivity {
    private TextView infoTitle;
    private TextView infoNotExist;
    private TextView infoNetworkError;
    private ImageButton infoBackButton;
    private TextView infoPopulation;
    private TextView infoPopulationChange;
    private TextView infoWorkplace;
    private TextView infoEmployment;
    private TextView infoCompany;
    private TextView infoTraffic;
    private TextView infoWeather;
    private TextView Loading;
    private ImageView infoWeatherIcon;
    private String municipalityName;
    private String municipalityCode;
    private Button quizButton;
    private WeatherData weatherData;
    private ArrayList<MunicipalityData> municipalityDataArrayList;
    private float workplaceData;
    private float employmentData;
    private CompanyData companyData;
    private TrafficData trafficData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_informantion);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Bundle extras = getIntent().getExtras();
        municipalityName = extras.getString("municipalityName");

        String names = null, codes = null;
        try {
            InputStream is = getResources().openRawResource(R.raw.municipality);
            Reader in = new InputStreamReader(is);
            BufferedReader bufferedReader = new BufferedReader(in);
            codes = bufferedReader.readLine();
            names = bufferedReader.readLine();
            bufferedReader.close();
            in.close();
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<String> municipalityNames = Arrays.asList(names.replace("\"", "").split(","));
        List<String> municipalityCodes = Arrays.asList(codes.replace("\"", "").split(","));
        for (int i = 0; i < municipalityNames.size(); i++) {
            if (municipalityNames.get(i).equals(municipalityName)) {
                municipalityCode = municipalityCodes.get(i);
                break;
            }
        }

        bindView();
    }

    private void bindView() {
        infoTitle = findViewById(R.id.infoTitle);
        infoNotExist = findViewById(R.id.infoNotExist);
        infoNetworkError = findViewById(R.id.infoNetError);
        infoBackButton = findViewById(R.id.infoBackButton);
        infoBackButton.setOnClickListener(v -> finish());
        infoPopulation = findViewById(R.id.infoPopulation);
        infoPopulationChange = findViewById(R.id.infoPopulationChange);
        infoWorkplace = findViewById(R.id.infoWorkplace);
        infoEmployment = findViewById(R.id.infoEmployment);
        infoCompany = findViewById(R.id.infoCompany);
        infoTraffic = findViewById(R.id.infoTraffic);
        infoWeather = findViewById(R.id.infoWeather);
        infoWeatherIcon = findViewById(R.id.infoWeatherIcon);
        quizButton = findViewById(R.id.quizButton);
        Loading = findViewById(R.id.Loading);

        Loading.setVisibility(View.VISIBLE);
        quizButton.setVisibility(View.GONE);
        infoNetworkError.setVisibility(View.GONE);
        if (municipalityCode == null) {
            infoNotExist.setVisibility(View.VISIBLE);
            infoTitle.setVisibility(View.GONE);
            infoPopulation.setVisibility(View.GONE);
            infoPopulationChange.setVisibility(View.GONE);
            infoWorkplace.setVisibility(View.GONE);
            infoEmployment.setVisibility(View.GONE);
            infoCompany.setVisibility(View.GONE);
            infoTraffic.setVisibility(View.GONE);
            infoWeather.setVisibility(View.GONE);
            infoWeatherIcon.setVisibility(View.GONE);
            quizButton.setVisibility(View.GONE);
            Loading.setVisibility(View.GONE);
        } else {
            infoNotExist.setVisibility(View.GONE);
            infoTitle.setVisibility(View.VISIBLE);
            infoPopulation.setVisibility(View.VISIBLE);
            infoPopulationChange.setVisibility(View.VISIBLE);
            infoWorkplace.setVisibility(View.VISIBLE);
            infoEmployment.setVisibility(View.VISIBLE);
            infoCompany.setVisibility(View.VISIBLE);
            infoTraffic.setVisibility(View.VISIBLE);
            infoWeather.setVisibility(View.VISIBLE);
            updateViews();
        }
    }

    private void updateViews() {
        infoTitle.setText(municipalityName);

        WeatherDataRetriever weatherDataRetriever = new WeatherDataRetriever();
        MunicipalityDataRetriever municipalityDataRetriever = new MunicipalityDataRetriever();
        CompanyDataRetriever companyDataRetriever = new CompanyDataRetriever();
        TrafficDataRetriever trafficDataRetriever = new TrafficDataRetriever();
        Context context = this;
        ExecutorService service = Executors.newSingleThreadExecutor();
        service.execute(new Runnable() {
                @Override
                public void run() {
                    MunicipalityDataRetriever.getMunicipalityCodesMap();
                    municipalityDataArrayList = municipalityDataRetriever.getData(context, municipalityName);
                    weatherData = weatherDataRetriever.getData(municipalityName);
                    workplaceData = municipalityDataRetriever.getWorkplaceData(context, municipalityName);
                    employmentData = municipalityDataRetriever.getEmploymentData(context, municipalityName);
                    companyData = companyDataRetriever.getData(municipalityName);
                    trafficData = trafficDataRetriever.getData(context, municipalityCode);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (municipalityDataArrayList == null || weatherData == null ||
                                    companyData == null || trafficData == null) {
                                infoNetworkError.setVisibility(View.VISIBLE);
                                Loading.setVisibility(View.GONE);
                                infoTitle.setVisibility(View.GONE);
                                infoPopulation.setVisibility(View.GONE);
                                infoPopulationChange.setVisibility(View.GONE);
                                infoWorkplace.setVisibility(View.GONE);
                                infoEmployment.setVisibility(View.GONE);
                                infoCompany.setVisibility(View.GONE);
                                infoTraffic.setVisibility(View.GONE);
                                infoWeather.setVisibility(View.GONE);
                                infoWeatherIcon.setVisibility(View.GONE);
                                return;
                            }

                            MunicipalityData data = municipalityDataArrayList.get(municipalityDataArrayList.size() - 1);
                            infoPopulation.setText("Population: " + data.getPopulation());
                            infoPopulationChange.setText("Population change: " + data.getIncrease());
                            infoWorkplace.setText("Workplace: " + workplaceData + "%");
                            infoEmployment.setText("Employment: " + employmentData + "%");
                            infoCompany.setText("Company: " + companyData.getNumber());
                            infoTraffic.setText("Traffic: " + trafficData.getNumber());

                            String weatherDataAsString =
                                    "Weather now: " + weatherData.getMain() + "(" + weatherData.getDescription() + ")\n" +
                                    "Temperature: " + weatherData.getTemperature() + "\n" +
                                    "Wind speed: " + weatherData.getWindSpeed();
                            infoWeather.setText(weatherDataAsString);

                            String weatherMain = weatherData.getMain();
                            if (weatherMain.toLowerCase().contains("cloud")) {
                                infoWeatherIcon.setImageResource(R.drawable.clouds);
                            } else if (weatherMain.toLowerCase().contains("rain")) {
                                infoWeatherIcon.setImageResource(R.drawable.rain);
                            } else if (weatherMain.toLowerCase().contains("clear")) {
                                infoWeatherIcon.setImageResource(R.drawable.clear);
                            } else if (weatherMain.toLowerCase().contains("snow")) {
                                infoWeatherIcon.setImageResource(R.drawable.snow);
                            } else if (weatherMain.toLowerCase().contains("thunder")) {
                                infoWeatherIcon.setImageResource(R.drawable.thunder);
                            } else if (weatherMain.toLowerCase().contains("mist")) {
                                infoWeatherIcon.setImageResource(R.drawable.mist);
                            } else if (weatherMain.toLowerCase().contains("drizzle")) {
                                infoWeatherIcon.setImageResource(R.drawable.drizzle);
                            } else {
                                infoWeatherIcon.setImageResource(R.drawable.sunny);
                            }
                            Loading.setVisibility(View.GONE);
                            quizButton.setVisibility(View.VISIBLE);
                            quizButton.setOnClickListener(v -> {
                                Intent intent = new Intent(InformantionActivity.this, QuizActivity.class);
                                intent.putExtra("data", (Serializable) municipalityDataArrayList);
                                intent.putExtra("municipalityName", municipalityName);
                                startActivity(intent);
                            });
                        }
                    });
                }
            });
    }
}