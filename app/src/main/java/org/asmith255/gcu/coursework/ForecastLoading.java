//
// Name                 Alexander Smith
// Student ID           S2028248
// Programme of Study   BSc (Hons) Computer Games (Software Development)
//

package org.asmith255.gcu.coursework;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;

public class ForecastLoading extends Fragment
{
    private ProgressBar loadingProgress;
    public boolean fragmentReady = false;


    public ForecastLoading()
    {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        fragmentReady = false;

        View v = inflater.inflate(R.layout.fragment_forecast_loading, container, false);

        Bundle progressBundle = getArguments();

        loadingProgress = v.findViewById(R.id.loadForecastBar);
        if (progressBundle != null)
        {
            loadingProgress.setProgress(progressBundle.getInt("progress"));
        }
        else
        {
            loadingProgress.setProgress(0);
        }

        fragmentReady = true;

        return v;
    }


    public void setLoadProgress(int progress)
    {
        loadingProgress.setProgress(progress);
    }


    @Override
    public void onDestroyView()
    {
        loadingProgress = null;
        super.onDestroyView();
    }
}