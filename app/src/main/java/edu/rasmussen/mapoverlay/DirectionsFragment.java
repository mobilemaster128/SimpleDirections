package edu.rasmussen.mapoverlay;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import java.net.MalformedURLException;
import java.net.URL;

/**
 * Handles fragment for requesting and displaying step by step instructions using Google Maps API v2
 */
public class DirectionsFragment extends Fragment {
    // Handlers
    private MainActivity mainActivity;
    private Context context;

    private EditText originEditText, destinationEditText;
    private TextView downloadingTextView, directionsTextView;
    private URL url;
    // request XML output
    private static String urlRequest = "https://maps.googleapis.com/maps/api/directions/xml?";
    private static String SERVER_KEY;
    // object that will hold directional info
    private Directions directions;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_directions, container, false);
        SERVER_KEY = getResources().getString(R.string.SERVER_KEY);

        // get context to pass to AsyncTask
        context = getActivity();
        mainActivity = (MainActivity) getActivity();

        originEditText = (EditText) view.findViewById(R.id.startEditText);
        destinationEditText =(EditText) view.findViewById(R.id.destinationEditText);
        directionsTextView = (TextView) view.findViewById(R.id.directionsTextView);
        downloadingTextView = (TextView) view.findViewById(R.id.downloadingTextView);

        // hide download result text when not in use
        downloadingTextView.setVisibility(View.GONE);

        // button click triggers AsyncTask to download directions
        Button button = (Button) view.findViewById(R.id.submitButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get locations from input
                String start = originEditText.getText().toString();
                String end = destinationEditText.getText().toString();
                // construct url
                String toUrl = urlRequest + "origin=" + start + "&" + "destination=" + end + "&" + "key=" + SERVER_KEY;
                try {
                    url = new URL(toUrl);
                    // start task
                    new DownloadDirectionsTask(DirectionsFragment.this, context).execute(url);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        });
        return view;
    }

    /**
     * Sets directions and calls updateDirections to show them
     * @param directions directions object to use
     */
    public void setDirections(Directions directions)
    {
        this.directions = directions;
        // show results
        downloadingTextView.setVisibility(View.VISIBLE);
        downloadingTextView.setText(R.string.success_string);
        updateDirections();
        // update map with route
        mainActivity.updateMap("Start", "Destination", directions);
    }

    /**
     * Update list of directions
     */
    private void updateDirections()
    {
        // clear any previous text
        directionsTextView.setText("");
        // step counter
        int count = 1;
        for(String step : directions.directions)
        {
            directionsTextView.append(Integer.toString(count) + ". ");
            directionsTextView.append(step);
            // add each step on new line
            directionsTextView.append("\r\n");
            count++;
        }
        directionsTextView.invalidate();
    }
}
