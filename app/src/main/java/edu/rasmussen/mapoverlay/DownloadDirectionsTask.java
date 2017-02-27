package edu.rasmussen.mapoverlay;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * AsyncTask for downloading driving directions using Google Directions API.
 * @author Madison Liddell
 * @since 10/30/2015
 */
public class DownloadDirectionsTask extends AsyncTask<URL, Integer, Directions>
{
    private ProgressDialog mProgressDialog;
    private DirectionsFragment directionsFragment; // called by this fragment

    private DirectionsParser directionsParser;
    private Directions directions;

    private InputStream inputStream = null;

    public DownloadDirectionsTask(Fragment directionsFragment, Context context)
    {
        this.directionsFragment = (DirectionsFragment) directionsFragment;
        mProgressDialog = new ProgressDialog(context);
        directionsParser= new DirectionsParser();
        directions = new Directions();
    }

    @Override
    protected void onPreExecute() {
        // show progress dialog
        mProgressDialog.setTitle(R.string.download_string);
        mProgressDialog.setProgressStyle(mProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setProgress(0);
        mProgressDialog.setMax(100);
        mProgressDialog.show();
        super.onPreExecute();
    }

    /**
     * Downloads directions in background
     * @param params url to download
     * @return directions object
     */
    @Override
    protected Directions doInBackground(URL... params)
    {
        // connect to webservice and download some data
        return loadXmlFromNetwork(params[0]);
    }

    /**
     * Downloads xml from google directions, then parses it, and returns directions object with
     * parsed direction details.
     * @param url url to download from
     * @return list of directions
     */
    private Directions loadXmlFromNetwork(URL url)
    {
        try {
            // download xml
            inputStream = downloadUrl(url);

            // parse xml
            directions = directionsParser.parse(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directions;
    }

    /**
     * Given a URL, establishes an HttpUrlConnection and get the input stream
     * @param url url to connect to
     * @return input stream from url
     */
    private InputStream downloadUrl(URL url) throws IOException {
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Starts the query
        conn.connect();

        return conn.getInputStream();
    }

    /**
     * Called when background computation finishes. Receives directions and sends them to
     * activity to be displayed.
     * @param directions requested directions
     */
    @Override
    protected void onPostExecute(Directions directions)
    {
        // hide progress dialog
        if(mProgressDialog.isShowing())
            mProgressDialog.dismiss();

        if (directions != null)
        {
            Toast.makeText(directionsFragment.getActivity().getApplicationContext(), "Download success!", Toast.LENGTH_SHORT).show();
            // send directions back to main
            directionsFragment.setDirections(directions);
        }
        else
            super.onPostExecute(directions);
    }

    /**
     * Update progress bar
     * @param values the progress amount to show
     */
    @Override
    protected void onProgressUpdate(Integer... values)
    {
        super.onProgressUpdate(values);
        mProgressDialog.setProgress(values[0]);
    }
}
