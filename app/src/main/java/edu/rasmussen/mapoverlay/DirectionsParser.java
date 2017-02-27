package edu.rasmussen.mapoverlay;

import android.util.Xml;

import com.google.android.gms.maps.model.LatLng;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Handles parsing of google directions xml result from an input stream. Returns a Directions
 * object with the parsed info.
 * @author Madison Liddell
 * @since 11/2/2015
 */
public class DirectionsParser
{
    // no namespace
    private static final String ns = null;
    private ArrayList<String> directions;
    private ArrayList<LatLng> latLngs;

    /**
     * Parses xml from input stream.
     * @return directions object containing results; null if unable to parse
     */
    public Directions parse(InputStream inputStream) throws IOException
    {
        if (inputStream == null)
            return null;
        try {
            // setup parser
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(inputStream, null);
            parser.nextTag();

            return readDirectionsResponse(parser); // start reading
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } finally {
            inputStream.close();
        }
        return null;
    }

    /**
     * Handles processing of entire xml result using recursion
     * @param parser parser to read from
     * @return directions object containing results
     */
    private Directions readDirectionsResponse(XmlPullParser parser)
    {
        directions = new ArrayList<>(); //new list of directions
        latLngs = new ArrayList<>();

        try {
            parser.require(XmlPullParser.START_TAG, ns, "DirectionsResponse");
            // keep reading until the end tags for DirectionsResponse is reached
            while (parser.next() != XmlPullParser.END_TAG)
            {
                // only need start tags
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }
                String name = parser.getName();
                // Starts by looking for the route tag
                if (name.equals("route")) {
                    readRoute(parser);
                } else {
                    skip(parser);
                }
            }
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }
        return new Directions(latLngs, directions);
    }

    /**
     * Processes route tags
     * @param parser parser to read from
     */
    private void readRoute(XmlPullParser parser) throws IOException, XmlPullParserException
    {
        parser.require(XmlPullParser.START_TAG, ns, "route");
        while (parser.next() != XmlPullParser.END_TAG)
        {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("leg")) {
                readLeg(parser); // route leads to leg
            } else {
                skip(parser);
            }
        }
        parser.require(XmlPullParser.END_TAG, ns, "route");
    }

    /**
     * Processes leg tags
     * @param parser parser to read from
     */
    private void readLeg(XmlPullParser parser) throws IOException, XmlPullParserException
    {
        parser.require(XmlPullParser.START_TAG, ns, "leg");
        while (parser.next() != XmlPullParser.END_TAG)
        {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("step")) {
                readStep(parser); // leg leads to step
            } else {
                skip(parser);
            }
        }
        parser.require(XmlPullParser.END_TAG, ns, "leg");
    }

    /**
     * Processes step tags
     * @param parser parser to read from
     */
    private void readStep(XmlPullParser parser) throws IOException, XmlPullParserException
    {
        parser.require(XmlPullParser.START_TAG, ns, "step");
        while (parser.next() != XmlPullParser.END_TAG)
        {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            switch (name) {
                case "start_location":
                    latLngs.add(readStartLocation(parser));
                    break;
                case "end_location":
                    latLngs.add(readEndLocation(parser));
                    break;
                case "html_instructions":
                    directions.add(readInstructions(parser));
                    // step leads to html_directions which is our step by step directions
                    break;
                default:
                    skip(parser);
                    break;
            }
        }
        parser.require(XmlPullParser.END_TAG, ns, "step");
    }

    /**
     * Processes start_location tags
     * @param parser parser to read from
     */
    private LatLng readStartLocation(XmlPullParser parser) throws IOException, XmlPullParserException {
        Double lat = 0.0, lng = 0.0;
        parser.require(XmlPullParser.START_TAG, ns, "start_location");
        while (parser.next() != XmlPullParser.END_TAG)
        {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            switch (name) {
                case "lat":
                    lat = readLat(parser);
                    break;
                case "lng":
                    lng = readLng(parser);
                    break;
                default:
                    skip(parser);
                    break;
            }
        }
        parser.require(XmlPullParser.END_TAG, ns, "start_location");

        return new LatLng(lat, lng);
    }

    /**
     * Processes end_location tags
     * @param parser parser to read from
     */
    private LatLng readEndLocation(XmlPullParser parser) throws IOException, XmlPullParserException {
        Double lat = 0.0, lng = 0.0;
        parser.require(XmlPullParser.START_TAG, ns, "end_location");
        while (parser.next() != XmlPullParser.END_TAG)
        {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            switch (name) {
                case "lat":
                    lat = readLat(parser);
                    break;
                case "lng":
                    lng = readLng(parser);
                    break;
                default:
                    skip(parser);
                    break;
            }
        }
        parser.require(XmlPullParser.END_TAG, ns, "end_location");

        return new LatLng(lat, lng);
    }

    /**
     * Processes lat tags
     * @param parser parser to read from
     */
    private Double readLat(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "lat");
        Double lat = Double.parseDouble(readText(parser));
        parser.require(XmlPullParser.END_TAG, ns, "lat");
        return lat;
    }

    /**
     * Processes lng tags
     * @param parser parser to read from
     */
    private Double readLng(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "lng");
        Double lng = Double.parseDouble(readText(parser));
        parser.require(XmlPullParser.END_TAG, ns, "lng");
        return lng;
    }

    /**
     * Processes html_instructions tags
     * @param parser parser to read from
     * @return string with direction step
     */
    private String readInstructions(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "html_instructions");
        String step = readText(parser);
        // remove html from string
        step = android.text.Html.fromHtml(step).toString();
        parser.require(XmlPullParser.END_TAG, ns, "html_instructions");
        return step;
    }

    /**
     * Extracts text values
     * @param parser parser to read from
     * @return String containing text
     */
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException
    {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText(); //retrieve text from between tag
            parser.nextTag();
        }
        return result;
    }

    /**
     * Skips unneeded tags
     * @param parser parse to read from
     */
    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {            //stop when depth = 0
            switch (parser.next()) {    //go to next tag
                case XmlPullParser.END_TAG:
                    depth--;            //decrease depth when at end tag
                    break;
                case XmlPullParser.START_TAG:
                    depth++;            //increase it for start tag
                    break;
            }
        }
    }
}

