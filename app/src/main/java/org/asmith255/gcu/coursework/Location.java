//
// Name                 Alexander Smith
// Student ID           S2028248
// Programme of Study   BSc (Hons) Computer Games (Software Development)
//

package org.asmith255.gcu.coursework;

public class Location
{
    public String locationName;
    public String locationCode;

    //MAP COORDINATES

    public CurrentWeather locationWeather;
    public ForecastWeather[] locationForecast;
    public int forecastsLoaded;


    public Location(String name, String code)
    {
        locationName = name;
        locationCode = code;

        forecastsLoaded = 0;
    }
}