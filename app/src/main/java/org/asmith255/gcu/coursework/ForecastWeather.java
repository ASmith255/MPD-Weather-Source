//
// Name                 Alexander Smith
// Student ID           S2028248
// Programme of Study   BSc (Hons) Computer Games (Software Development)
//

package org.asmith255.gcu.coursework;

public class ForecastWeather
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
}