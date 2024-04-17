//
// Name                 Alexander Smith
// Student ID           S2028248
// Programme of Study   BSc (Hons) Computer Games (Software Development)
//

package org.asmith255.gcu.coursework;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import org.w3c.dom.Text;

public class ForecastDisplay extends Fragment implements View.OnClickListener
{
    private TextView day1Date;
    private TextView day1Temperature;
    private ImageView day1Icon;

    private TextView day2Date;
    private TextView day2Temperature;
    private ImageView day2Icon;

    private TextView day3Date;
    private TextView day3Temperature;
    private ImageView day3Icon;

    private Button expandDayOneButton;
    private Button expandDayTwoButton;
    private Button expandDayThreeButton;

    private View day1Section;
    private TextView day1WeatherDesc;
    private TextView day1WindSpeed;
    private TextView day1WindDirection;
    private TextView day1Humidity;
    private TextView day1Visibility;
    private TextView day1UVRisk;
    private TextView day1Pollution;
    private TextView day1Pressure;

    private View day2Section;
    private TextView day2WeatherDesc;
    private TextView day2WindSpeed;
    private TextView day2WindDirection;
    private TextView day2Humidity;
    private TextView day2Visibility;
    private TextView day2UVRisk;
    private TextView day2Pollution;
    private TextView day2Pressure;

    private View day3Section;
    private TextView day3WeatherDesc;
    private TextView day3WindSpeed;
    private TextView day3WindDirection;
    private TextView day3Humidity;
    private TextView day3Visibility;
    private TextView day3UVRisk;
    private TextView day3Pollution;
    private TextView day3Pressure;

    private ForecastWeather[] storedForecasts;


    public ForecastDisplay()
    {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_forecast, container, false);

        day1Date = (TextView)v.findViewById(R.id.forecastDay1Day);
        day1Temperature = (TextView)v.findViewById(R.id.forecastDay1Temp);
        day1Icon = (ImageView)v.findViewById(R.id.forecastDay1Icon);

        day2Date = (TextView)v.findViewById(R.id.forecastDay2Day);
        day2Temperature = (TextView)v.findViewById(R.id.forecastDay2Temp);
        day2Icon = (ImageView)v.findViewById(R.id.forecastDay2Icon);

        day3Date = (TextView)v.findViewById(R.id.forecastDay3Day);
        day3Temperature = (TextView)v.findViewById(R.id.forecastDay3Temp);
        day3Icon = (ImageView)v.findViewById(R.id.forecastDay3Icon);

        expandDayOneButton = (Button)v.findViewById(R.id.forecastDay1ExpandButton);
        expandDayOneButton.setOnClickListener(this);
        expandDayTwoButton = (Button)v.findViewById(R.id.forecastDay2ExpandButton);
        expandDayTwoButton.setOnClickListener(this);
        expandDayThreeButton = (Button)v.findViewById(R.id.forecastDay3ExpandButton);
        expandDayThreeButton.setOnClickListener(this);

        day1Section = (View)v.findViewById(R.id.forecastDay1Expanded);
        day1WeatherDesc = (TextView)v.findViewById(R.id.forecastDay1WeatherDescription);
        day1WindSpeed = (TextView)v.findViewById(R.id.forecastDay1WindSpeed);
        day1WindDirection = (TextView)v.findViewById(R.id.forecastDay1WindDirection);
        day1Humidity = (TextView)v.findViewById(R.id.forecastDay1Humidity);
        day1Visibility = (TextView)v.findViewById(R.id.forecastDay1Visibility);
        day1UVRisk = (TextView)v.findViewById(R.id.forecastDay1UVRisk);
        day1Pollution = (TextView)v.findViewById(R.id.forecastDay1Pollution);
        day1Pressure = (TextView)v.findViewById(R.id.forecastDay1Pressure);

        day2Section = (View)v.findViewById(R.id.forecastDay2Expanded);
        day2WeatherDesc = (TextView)v.findViewById(R.id.forecastDay2WeatherDescription);
        day2WindSpeed = (TextView)v.findViewById(R.id.forecastDay2WindSpeed);
        day2WindDirection = (TextView)v.findViewById(R.id.forecastDay2WindDirection);
        day2Humidity = (TextView)v.findViewById(R.id.forecastDay2Humidity);
        day2Visibility = (TextView)v.findViewById(R.id.forecastDay2Visibility);
        day2UVRisk = (TextView)v.findViewById(R.id.forecastDay2UVRisk);
        day2Pollution = (TextView)v.findViewById(R.id.forecastDay2Pollution);
        day2Pressure = (TextView)v.findViewById(R.id.forecastDay2Pressure);

        day3Section = (View)v.findViewById(R.id.forecastDay3Expanded);
        day3WeatherDesc = (TextView)v.findViewById(R.id.forecastDay3WeatherDescription);
        day3WindSpeed = (TextView)v.findViewById(R.id.forecastDay3WindSpeed);
        day3WindDirection = (TextView)v.findViewById(R.id.forecastDay3WindDirection);
        day3Humidity = (TextView)v.findViewById(R.id.forecastDay3Humidity);
        day3Visibility = (TextView)v.findViewById(R.id.forecastDay3Visibility);
        day3UVRisk = (TextView)v.findViewById(R.id.forecastDay3UVRisk);
        day3Pollution = (TextView)v.findViewById(R.id.forecastDay3Pollution);
        day3Pressure = (TextView)v.findViewById(R.id.forecastDay3Pressure);

        Bundle forecasts = getArguments();
        if (forecasts != null)
        {
            storedForecasts = (ForecastWeather[])forecasts.getParcelableArray("forecast");
            displayWeatherData();
        }

        return v;
    }


    public void displayWeatherData()
    {
        day1Date.setText(storedForecasts[0].day);
        day1Temperature.setText(storedForecasts[0].minTemp + " / " + storedForecasts[0].maxTemp);
        day1WeatherDesc.setText(storedForecasts[0].weather);
        day1WindSpeed.setText(storedForecasts[0].windSpeed);
        day1WindDirection.setText(storedForecasts[0].windDirection);
        day1Humidity.setText(storedForecasts[0].humidity);
        day1Visibility.setText(storedForecasts[0].visibility);
        day1UVRisk.setText(storedForecasts[0].uvRisk);
        day1Pollution.setText(storedForecasts[0].pollution);
        day1Pressure.setText(storedForecasts[0].pressure);


        day2Date.setText(storedForecasts[1].day);
        day2Temperature.setText(storedForecasts[1].minTemp + " / " + storedForecasts[1].maxTemp);
        day2WeatherDesc.setText(storedForecasts[1].weather);
        day2WindSpeed.setText(storedForecasts[1].windSpeed);
        day2WindDirection.setText(storedForecasts[1].windDirection);
        day2Humidity.setText(storedForecasts[1].humidity);
        day2Visibility.setText(storedForecasts[1].visibility);
        day2UVRisk.setText(storedForecasts[1].uvRisk);
        day2Pollution.setText(storedForecasts[1].pollution);
        day2Pressure.setText(storedForecasts[1].pressure);

        day3Date.setText(storedForecasts[2].day);
        day3Temperature.setText(storedForecasts[2].minTemp + " / " + storedForecasts[2].maxTemp);
        day3WeatherDesc.setText(storedForecasts[2].weather);
        day3WindSpeed.setText(storedForecasts[2].windSpeed);
        day3WindDirection.setText(storedForecasts[2].windDirection);
        day3Humidity.setText(storedForecasts[2].humidity);
        day3Visibility.setText(storedForecasts[2].visibility);
        day3UVRisk.setText(storedForecasts[2].uvRisk);
        day3Pollution.setText(storedForecasts[2].pollution);
        day3Pressure.setText(storedForecasts[2].pressure);
    }


    @Override
    public void onDestroyView()
    {
        day1Date = null;
        day1Temperature = null;
        day1Icon = null;
        day2Date = null;
        day2Temperature = null;
        day2Icon = null;
        day3Date = null;
        day3Temperature = null;
        day3Icon = null;

        super.onDestroyView();
    }


    @Override
    public void onClick(View v)
    {
        if (v == expandDayOneButton)
        {
            if (day1Section.getVisibility() == View.GONE)
            {
                day1Section.setVisibility(View.VISIBLE);
                expandDayOneButton.setText("Collapse Details");
            }
            else
            {
                day1Section.setVisibility(View.GONE);
                expandDayOneButton.setText("Expand Details");
            }
        }
        else if (v == expandDayTwoButton)
        {
            if (day2Section.getVisibility() == View.GONE)
            {
                day2Section.setVisibility(View.VISIBLE);
                expandDayTwoButton.setText("Collapse Details");
            }
            else
            {
                day2Section.setVisibility(View.GONE);
                expandDayTwoButton.setText("Expand Details");
            }
        }
        else if (v == expandDayThreeButton)
        {
            if (day3Section.getVisibility() == View.GONE)
            {
                day3Section.setVisibility(View.VISIBLE);
                expandDayThreeButton.setText("Collapse Details");
            }
            else
            {
                day3Section.setVisibility(View.GONE);
                expandDayThreeButton.setText("Expand Details");
            }
        }
    }
}