//
// Name                 Alexander Smith
// Student ID           S2028248
// Programme of Study   BSc (Hons) Computer Games (Software Development)
//

package org.asmith255.gcu.coursework;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.util.Pair;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.ViewSwitcher;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
    private ViewSwitcher pageSwitcher;

    private ProgressBar currentWeatherLoadProgressBar;

    private final String currentWeatherURLSource = "https://weather-broker-cdn.api.bbci.co.uk/en/observation/rss/";
    private final String forecastURLSource = "https://weather-broker-cdn.api.bbci.co.uk/en/forecast/rss/3day/";

    private Handler uiUpdateHandler;
    private final static int MESSAGE_UPDATE_CURRENT_WEATHER_LOAD_PROGRESS = 1;
    private final static int MESSAGE_UPDATE_CURRENT_WEATHER_FINISHED = 2;
    private final static int MESSAGE_UPDATE_FORECAST_LOAD_PROGRESS = 3;
    private final static int MESSAGE_UPDATE_FORECAST_FINISHED = 4;

    private Location[] locations = new Location[6];

    private int currentPage = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Set up action bar
        Toolbar appToolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(appToolbar);

        pageSwitcher = (ViewSwitcher)findViewById(R.id.viewSwitcher);

        currentWeatherLoadProgressBar = (ProgressBar)findViewById(R.id.loadCurrentWeatherBar);

        //Set up Thread UI Update handler
        if (uiUpdateHandler == null)
        {
            uiUpdateHandler = new Handler(Looper.getMainLooper())
            {
                @Override
                public void handleMessage(Message msg)
                {
                    if (msg.what == MESSAGE_UPDATE_CURRENT_WEATHER_LOAD_PROGRESS)
                    {
                        //Update current weather loading progress bar
                        currentWeatherLoadProgressBar.setProgress(currentWeatherLoadProgressBar.getProgress() + (int)msg.obj);
                    }
                    else if (msg.what == MESSAGE_UPDATE_CURRENT_WEATHER_FINISHED)
                    {
                        //Switch from loading to weather screen
                        currentWeatherLoadProgressBar.setProgress(1);
                        pageSwitcher.showNext();
                        showLocationData(currentPage);
                    }
                    else if (msg.what == MESSAGE_UPDATE_FORECAST_LOAD_PROGRESS)
                    {
                        //If loaded forecast is for currently-viewed location,
                        if ((int)msg.obj == currentPage)
                        {
                            //  UPDATE FORECAST LOAD PROGRESS BAR
                        }
                    }
                    else if (msg.what == MESSAGE_UPDATE_FORECAST_FINISHED)
                    {
                        //If loaded forecast is for currently-viewed location,
                        if ((int)msg.obj == currentPage)
                        {
                            //  COLLAPSE LOADING FORECAST FRAGMENT

                            //  EXPAND FORECAST DATA FRAGMENT
                            //  DISPLAY FORECAST DATA
                        }
                    }
                }
            };
        }

        //Set up location data
        locations[0] = new Location("Glasgow", "2648579");
        locations[1] = new Location("London", "2643743");
        locations[2] = new Location("New York", "5128581");
        locations[3] = new Location("Oman", "287286");
        locations[4] = new Location("Mauritius", "934154");
        locations[5] = new Location("Bangladesh", "1185241");

        //Load and parse current weather for all locations
        startProgress();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.locationselectmenu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.glasgow:
                showLocationData(0);
                break;

            case R.id.london:
                showLocationData(1);
                break;

            case R.id.newyork:
                showLocationData(2);
                break;

            case R.id.oman:
                showLocationData(3);
                break;

            case R.id.mauritius:
                showLocationData(4);
                break;

            case R.id.bangladesh:
                showLocationData(5);
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }


    @Override
    public void onClick(View v)
    {
        //IF 'PREVIOUS LOCATION' BUTTON PRESSED,
        //  IF currentPage == 0
        //    currentPage = locations.length-1
        //  ELSE
        //    currentPage--
        //  showLocationData(currentPage)
        //ELSE IF 'NEXT LOCATION' BUTTON PRESSED,
        //  IF currentPage == locations.length-1
        //    currentPage = 0
        //  ELSE
        //    currentPage++
        //  showLocationData(currentPage)
    }


    public void startProgress()
    {
        currentWeatherLoadProgressBar.setProgress(0);

        // Run network access on a separate thread;
        Runnable loadCurrentRunnable = new LoadCurrentWeatherThread();
        Executor threadExecutor = Executors.newSingleThreadExecutor();
        threadExecutor.execute(loadCurrentRunnable);
    }


    private void showLocationData(int locationIndex)
    {
        //SHOW CURRENT LOCATION DATA
        //-Location name
        //-Date
        //-Weather
        //-Temperature
        //-Wind Speed
        //-Humidity
        //-Visibility

        //IF LOADING FORECAST FRAGMENT IS ACTIVE,
        //COLLAPSE FORECAST LOADING BAR

        //IF FORECAST DATA FRAGMENT IS ACTIVE,
        //COLLAPSE ANY EXTENDED DETAIL PANELS
        //COLLAPSE FORECAST DATA FRAGMENT

        //IF FORECAST LOADED FOR NEW LOCATION,
        //  ENLARGE FORECAST DATA FRAGMENT
        //  SET FORECAST DATA FRAGMENT TEXT
        //ELSE
        //  ENLARGE LOADING FORECAST FRAGMENT
        //  SET LOADING FORECAST PROGRESS BAR
    }


    private class LoadCurrentWeatherThread implements Runnable
    {
        private String result;


        @Override
        public void run()
        {
            for (int i = 0; i < locations.length; i++)
            {
                result = null;
                locations[i].locationWeather = parseCurrentWeather(locations[i].locationCode);
            }


            //Send message to switch from loading screen to main screen
            Message currentWeatherLoadedMessage = new Message();
            currentWeatherLoadedMessage.what = MESSAGE_UPDATE_CURRENT_WEATHER_FINISHED;
            currentWeatherLoadedMessage.obj = true;
            uiUpdateHandler.sendMessage(currentWeatherLoadedMessage);


            for (int i = 0; i < locations.length; i++)
            {
                result = null;
                locations[i].locationForecast = parseForecast(locations[i].locationCode);

                //Send message to display location's forecast
                Message forecastLoadedMessage = new Message();
                forecastLoadedMessage.what = MESSAGE_UPDATE_FORECAST_FINISHED;
                forecastLoadedMessage.obj = i;      //Index of location that the forecast belongs to
                uiUpdateHandler.sendMessage(forecastLoadedMessage);
            }
        }


        private CurrentWeather parseCurrentWeather(String locationCode)
        {
            String url = currentWeatherURLSource + locationCode;

            URL aurl;
            URLConnection yc;
            BufferedReader in = null;
            String inputLine = "";

            Log.e("MyTag","in run");

            try
            {
                Log.e("MyTag","in try");
                aurl = new URL(url);
                yc = aurl.openConnection();
                in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
                while ((inputLine = in.readLine()) != null)
                {
                    result = result + inputLine;
                    Log.e("MyTag",inputLine);

                }
                in.close();
            }
            catch (IOException ae)
            {
                Log.e("MyTag", "ioexception");
            }

            //Get rid of the first tag <?xml version="1.0" encoding="utf-8"?>
            int i = result.indexOf(">");
            result = result.substring(i+1);
            Log.e("MyTag - cleaned",result);


            CurrentWeather currentParsingData = null;
            try
            {
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput( new StringReader( result ) );
                int eventType = xpp.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT)
                {
                    if(eventType == XmlPullParser.START_TAG)
                    {
                        if (xpp.getName().equalsIgnoreCase("item"))
                        {
                            currentParsingData = new CurrentWeather();
                        }
                        if (xpp.getName().equalsIgnoreCase("title") && currentParsingData != null)
                        {
                            String temp = xpp.nextText();

                            //WEATHER
                            //Split title data using : and ,
                            String delimiter = "[:,]";
                            String[] splitData = temp.split(delimiter);
                            currentParsingData.weather = splitData[1].trim();
                        }
                        else if (xpp.getName().equalsIgnoreCase("pubDate") && currentParsingData != null)
                        {
                            String temp = xpp.nextText();

                            //DATE
                            //Split date data using spaces
                            String delimiter = "[\\s]";
                            String[] splitData = temp.split(delimiter);
                            currentParsingData.date = splitData[0] + " " + splitData[1] + " " + splitData[2] + " " + splitData[3];
                        }
                        else if (xpp.getName().equalsIgnoreCase("description") && currentParsingData != null)
                        {
                            String temp = xpp.nextText();

                            //Split description data using : and ,
                            String delimiter = "[:,]";
                            String[] splitData = temp.split(delimiter);

                            //TEMPERATURE
                            String temperatureString = splitData[1].trim();
                            //Only take the celsius value
                            for (int tempCharIndex = 0; tempCharIndex < temperatureString.length(); tempCharIndex++)
                            {
                                if (temperatureString.charAt(tempCharIndex) == 'C')
                                {
                                    currentParsingData.temperature = temperatureString.substring(0, tempCharIndex+1);
                                    break;
                                }
                            }

                            //WIND SPEED
                            currentParsingData.windSpeed = splitData[5].trim();

                            //HUMIDITY
                            currentParsingData.humidity = splitData[7].trim();

                            //VISIBILITY
                            currentParsingData.visibility = splitData[12].trim();
                        }
                    }
                    else if(eventType == XmlPullParser.END_TAG)
                    {
                        if (xpp.getName().equalsIgnoreCase("item") && currentParsingData != null)
                        {
                            Message progressMessage = new Message();
                            progressMessage.what = MESSAGE_UPDATE_CURRENT_WEATHER_LOAD_PROGRESS;
                            progressMessage.obj = 1;
                            uiUpdateHandler.sendMessage(progressMessage);
                        }
                    }

                    eventType = xpp.next(); // Get the next event  before looping again
                } // End of while
            }
            catch (XmlPullParserException ae1)
            {
                Log.e("MyTag","Parsing error" + ae1.toString());
            }
            catch (IOException ae1)
            {
                Log.e("MyTag","IO error during parsing");
            }
            Log.d("MyTag","End of document reached");


            return currentParsingData;
        }


        private ForecastWeather[] parseForecast(String locationCode)
        {
            String url = forecastURLSource + locationCode;

            URL aurl;
            URLConnection yc;
            BufferedReader in = null;
            String inputLine = "";

            Log.e("MyTag","in run");

            try
            {
                Log.e("MyTag","in try");
                aurl = new URL(url);
                yc = aurl.openConnection();
                in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
                while ((inputLine = in.readLine()) != null)
                {
                    result = result + inputLine;
                    Log.e("MyTag",inputLine);

                }
                in.close();
            }
            catch (IOException ae)
            {
                Log.e("MyTag", "ioexception");
            }

            //Get rid of the first tag <?xml version="1.0" encoding="utf-8"?>
            int i = result.indexOf(">");
            result = result.substring(i+1);
            Log.e("MyTag - cleaned",result);


            ForecastWeather[] currentParsingData = null;
            int dayIndex = -1;
            try
            {
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput( new StringReader( result ) );
                int eventType = xpp.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT)
                {
                    if(eventType == XmlPullParser.START_TAG)
                    {
                        if (xpp.getName().equalsIgnoreCase("item"))
                        {
                            if (currentParsingData == null)
                            {
                                currentParsingData = new ForecastWeather[3];
                                currentParsingData[0] = new ForecastWeather();
                                currentParsingData[1] = new ForecastWeather();
                                currentParsingData[2] = new ForecastWeather();
                            }

                            dayIndex++;
                        }
                        if (xpp.getName().equalsIgnoreCase("title") && currentParsingData != null)
                        {
                            String temp = xpp.nextText();

                            String delimiter = "[:,]";
                            String[] splitData = temp.split(delimiter);

                            //DAY
                            currentParsingData[dayIndex].day = splitData[0];

                            //WEATHER
                            currentParsingData[dayIndex].weather = splitData[1].trim();
                        }
                        else if (xpp.getName().equalsIgnoreCase("description") && currentParsingData != null)
                        {
                            String temp = xpp.nextText();

                            String delimiter = "[:,]";
                            String[] splitData = temp.split(delimiter);

                            //MAX TEMP
                            String maxTempString = splitData[1].trim();
                            //Only take the temperature in celsius
                            for (int maxTempCharIndex = 0; maxTempCharIndex < maxTempString.length(); maxTempCharIndex++)
                            {
                                if (maxTempString.charAt(maxTempCharIndex) == 'C')
                                {
                                    currentParsingData[dayIndex].maxTemp = maxTempString.substring(0, maxTempCharIndex+1);
                                    break;
                                }
                            }

                            //MIN TEMP
                            String minTempString = splitData[3].trim();
                            //Only take the temperature in celsius
                            for (int minTempCharIndex = 0; minTempCharIndex < minTempString.length(); minTempCharIndex++)
                            {
                                if (minTempString.charAt(minTempCharIndex) == 'C')
                                {
                                    currentParsingData[dayIndex].minTemp = minTempString.substring(0, minTempCharIndex+1);
                                    break;
                                }
                            }

                            //WIND DIRECTION
                            currentParsingData[dayIndex].windDirection = splitData[5].trim();

                            //WIND SPEED
                            currentParsingData[dayIndex].windSpeed = splitData[7].trim();

                            //VISIBILITY
                            currentParsingData[dayIndex].visibility = splitData[9].trim();

                            //PRESSURE
                            currentParsingData[dayIndex].pressure = splitData[11].trim();

                            //HUMIDITY
                            currentParsingData[dayIndex].humidity = splitData[13].trim();

                            //UV RISK
                            currentParsingData[dayIndex].uvRisk = splitData[15].trim();

                            //POLLUTION
                            currentParsingData[dayIndex].pollution = splitData[17].trim();
                        }
                    }
                    else if (eventType == XmlPullParser.END_TAG)
                    {
                        if (xpp.getName().equalsIgnoreCase("item"))
                        {
                            //Update current location's 'forecastsLoaded' counter
                            for (int locationIndex = 0; locationIndex < locations.length; locationIndex++)
                            {
                                if (locationCode == locations[locationIndex].locationCode)
                                {
                                    locations[locationIndex].forecastsLoaded++;

                                    //Send message to update forecast loading bar
                                    Message forecastProgressMessage = new Message();
                                    forecastProgressMessage.what = MESSAGE_UPDATE_FORECAST_LOAD_PROGRESS;
                                    forecastProgressMessage.obj = locationIndex;        //Index of location that the forecast belongs to
                                    uiUpdateHandler.sendMessage(forecastProgressMessage);

                                    break;
                                }
                            }
                        }
                    }
                    eventType = xpp.next(); // Get the next event  before looping again
                } // End of while
            }
            catch (XmlPullParserException ae1)
            {
                Log.e("MyTag","Parsing error" + ae1.toString());
            }
            catch (IOException ae1)
            {
                Log.e("MyTag","IO error during parsing");
            }
            Log.d("MyTag","End of document reached");


            return currentParsingData;
        }
    }
}