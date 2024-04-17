//
// Name                 Alexander Smith
// Student ID           S2028248
// Programme of Study   BSc (Hons) Computer Games (Software Development)
//

package org.asmith255.gcu.coursework;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class ForecastWeather implements Parcelable
{
    public String day;
    public String weather;
    public String minTemp;
    public String maxTemp;
    public String windDirection;
    public String windSpeed;
    public String visibility;
    public String pressure;
    public String humidity;
    public String uvRisk;
    public String pollution;


    public ForecastWeather()
    {
        day = "TEMPDAY";

        weather = "TEMPWEATHER";

        minTemp = "TEMPMINTEMPERATURE";
        maxTemp = "TEMPMAXTEMPERATURE";

        windDirection = "TEMPWIND";
        windSpeed = "TEMPSPEED";

        visibility = "TEMPVISIBILITY";

        pressure = "TEMPPRESSURE";

        humidity = "TEMPHUMIDITY";

        uvRisk = "TEMPUVRISK";

        pollution = "TEMPPOLLUTION";
    }


    protected ForecastWeather(Parcel in) {
        day = in.readString();
        weather = in.readString();
        minTemp = in.readString();
        maxTemp = in.readString();
        windDirection = in.readString();
        windSpeed = in.readString();
        visibility = in.readString();
        pressure = in.readString();
        humidity = in.readString();
        uvRisk = in.readString();
        pollution = in.readString();
    }


    public static final Creator<ForecastWeather> CREATOR = new Creator<ForecastWeather>() {
        @Override
        public ForecastWeather createFromParcel(Parcel in) {
            return new ForecastWeather(in);
        }

        @Override
        public ForecastWeather[] newArray(int size) {
            return new ForecastWeather[size];
        }
    };


    @Override
    public int describeContents()
    {
        return 0;
    }


    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(day);
        dest.writeString(weather);
        dest.writeString(minTemp);
        dest.writeString(maxTemp);
        dest.writeString(windDirection);
        dest.writeString(windSpeed);
        dest.writeString(visibility);
        dest.writeString(pressure);
        dest.writeString(humidity);
        dest.writeString(uvRisk);
        dest.writeString(pollution);
    }
}