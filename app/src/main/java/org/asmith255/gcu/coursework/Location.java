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

    public CurrentWeather locationWeather = new CurrentWeather();
    public ForecastWeather[] locationForecast = new ForecastWeather[3];
    public int forecastsLoaded;


    public Location(String name, String code)
    {
        locationName = name;
        locationCode = code;

        forecastsLoaded = 0;
    }
}