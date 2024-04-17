//
// Name                 Alexander Smith
// Student ID           S2028248
// Programme of Study   BSc (Hons) Computer Games (Software Development)
//

package org.asmith255.gcu.coursework;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import org.w3c.dom.Text;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
    private ViewSwitcher pageSwitcher;

    private ProgressBar currentWeatherLoadProgressBar;

    private ImageView currentWeatherIcon;
    private TextView currentLocationNameText;
    private TextView currentDateText;
    private TextView currentTemperatureText;
    private TextView currentWindText;
    private TextView currentWeatherText;
    private TextView currentHumidityText;
    private TextView currentVisibilityText;

    private Button previousLocationButton;
    private Button nextLocationButton;

    private Fragment forecastSection;
    private Fragment forecastLoadingFragment;
    private Fragment forecastFragment;

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

        currentWeatherIcon = (ImageView)findViewById(R.id.currentWeatherIcon);
        currentLocationNameText = (TextView)findViewById(R.id.currentLocationNameText);
        currentDateText = (TextView)findViewById(R.id.currentDateText);
        currentTemperatureText = (TextView)findViewById(R.id.currentTemperatureText);
        currentWindText = (TextView)findViewById(R.id.currentWindSpeedText);
        currentWeatherText = (TextView)findViewById(R.id.currentWeatherText);
        currentHumidityText = (TextView)findViewById(R.id.currentHumidityText);
        currentVisibilityText = (TextView)findViewById(R.id.currentVisibilityText);

        previousLocationButton = (Button)findViewById(R.id.previousLocationButton);
        previousLocationButton.setOnClickListener(this);
        nextLocationButton = (Button)findViewById(R.id.nextLocationButton);
        nextLocationButton.setOnClickListener(this);

        //Set up forecast fragments
        forecastLoadingFragment = new ForecastLoading();
        forecastFragment = new ForecastDisplay();

        //Display initial forecast fragment (forecast loading bar)
        forecastSection = forecastLoadingFragment;
        ((ForecastLoading)forecastSection).fragmentReady = false;
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.forecastSection, forecastSection);
        transaction.commit();

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
                        pageSwitcher.showNext();
                        showLocationData(currentPage);
                    }
                    else if (msg.what == MESSAGE_UPDATE_FORECAST_LOAD_PROGRESS)
                    {
                        //If loaded forecast is for currently-viewed location & forecast load bar fragment is set up
                        if ((int)msg.obj == currentPage && forecastSection == forecastLoadingFragment && ((ForecastLoading)forecastSection).fragmentReady)
                        {
                            //Update forecast load bar
                            ((ForecastLoading)forecastSection).setLoadProgress(locations[currentPage].forecastsLoaded);
                        }
                    }
                    else if (msg.what == MESSAGE_UPDATE_FORECAST_FINISHED)
                    {
                        //If loaded forecast is for currently-viewed location,
                        if ((int)msg.obj == currentPage && forecastSection == forecastLoadingFragment)
                        {
                            //Refresh page, to display current weather and the forecast
                            showLocationData(currentPage);
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

        //Load and parse weather data for all locations on a separate thread
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
                currentPage = 0;
                showLocationData(0);
                break;

            case R.id.london:
                currentPage = 1;
                showLocationData(1);
                break;

            case R.id.newyork:
                currentPage = 2;
                showLocationData(2);
                break;

            case R.id.oman:
                currentPage = 3;
                showLocationData(3);
                break;

            case R.id.mauritius:
                currentPage = 4;
                showLocationData(4);
                break;

            case R.id.bangladesh:
                currentPage = 5;
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
        if (v == previousLocationButton)
        {
            if (currentPage == 0)
            {
                currentPage = locations.length - 1;
            }
            else
            {
                currentPage--;
            }

            showLocationData(currentPage);
        }
        else if (v == nextLocationButton)
        {
            if (currentPage == locations.length - 1)
            {
                currentPage = 0;
            }
            else
            {
                currentPage++;
            }

            showLocationData(currentPage);
        }
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
        //Show current location weather data
        currentLocationNameText.setText(locations[locationIndex].locationName);
        currentDateText.setText(locations[locationIndex].locationWeather.date);
        currentWeatherText.setText(locations[locationIndex].locationWeather.weather);
        currentTemperatureText.setText(locations[locationIndex].locationWeather.temperature);
        currentWindText.setText("Wind: " + locations[locationIndex].locationWeather.windSpeed);
        currentHumidityText.setText(locations[locationIndex].locationWeather.humidity + " Humidity");
        currentVisibilityText.setText(locations[locationIndex].locationWeather.visibility + " Visibility");

        //FIND AND SET CORRECT WEATHER ICON

        //MAKE GOOGLE MAPS FRAGMENT SHOW LOCATION

        //Close current forecast section fragment (whether loading or displaying details)
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.remove(forecastSection).commitNow();

        if (locations[locationIndex].forecastsLoaded == 3)
        {
            //Set forecast display fragment
            forecastSection = forecastFragment;

            //Pass forecast data to the fragment
            Bundle forecastDataBundle = new Bundle();
            forecastDataBundle.putParcelableArray("forecast", locations[locationIndex].locationForecast);
            forecastSection.setArguments(forecastDataBundle);

            //Inflate forecast fragment
            transaction.replace(R.id.forecastSection, forecastSection);
            transaction.commit();
        }
        else
        {
            //Set loading forecast bar
            forecastSection = forecastLoadingFragment;
            ((ForecastLoading)forecastSection).fragmentReady = false;

            //Set initial loading bar progress
            Bundle loadingProgressBundle = new Bundle();
            loadingProgressBundle.putInt("progress", locations[locationIndex].forecastsLoaded);
            forecastSection.setArguments(loadingProgressBundle);

            //Inflate loading forecast bar
            transaction.replace(R.id.forecastSection, forecastSection);
            transaction.commit();
        }
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
                            currentParsingData.weather = splitData[2].trim();
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

                            if (splitData[0].equalsIgnoreCase("Maximum Temperature"))
                            {
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
                                String windDirectionString = splitData[5].trim();
                                if (windDirectionString.equals("Direction not available"))
                                {
                                    currentParsingData[dayIndex].windDirection = "--";
                                }
                                else
                                {
                                    String[] windDirectionSplit = windDirectionString.split("[\\s]");
                                    StringBuilder windDirection = new StringBuilder();
                                    for (int windDirWordIndex = 0; windDirWordIndex < windDirectionSplit.length; windDirWordIndex++)
                                    {
                                        windDirection.append(windDirectionSplit[windDirWordIndex].charAt(0));
                                    }
                                    currentParsingData[dayIndex].windDirection = windDirection.toString();
                                }

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
                            else
                            {
                                //MAX TEMP
                                currentParsingData[dayIndex].maxTemp = "--";

                                //MIN TEMP
                                String minTempString = splitData[1].trim();
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
                                String windDirectionString = splitData[3].trim();
                                if (windDirectionString.equals("Direction not available"))
                                {
                                    currentParsingData[dayIndex].windDirection = "--";
                                }
                                else
                                {
                                    String[] windDirectionSplit = windDirectionString.split("[\\s]");
                                    StringBuilder windDirection = new StringBuilder();
                                    for (int windDirWordIndex = 0; windDirWordIndex < windDirectionSplit.length; windDirWordIndex++)
                                    {
                                        windDirection.append(windDirectionSplit[windDirWordIndex].charAt(0));
                                    }
                                    currentParsingData[dayIndex].windDirection = windDirection.toString();
                                }

                                //WIND SPEED
                                currentParsingData[dayIndex].windSpeed = splitData[5].trim();

                                //VISIBILITY
                                currentParsingData[dayIndex].visibility = splitData[7].trim();

                                //PRESSURE
                                currentParsingData[dayIndex].pressure = splitData[9].trim();

                                //HUMIDITY
                                currentParsingData[dayIndex].humidity = splitData[11].trim();

                                //UV RISK
                                currentParsingData[dayIndex].uvRisk = splitData[13].trim();

                                //POLLUTION
                                currentParsingData[dayIndex].pollution = splitData[15].trim();
                            }
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