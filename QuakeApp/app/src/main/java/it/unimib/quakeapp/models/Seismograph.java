package it.unimib.quakeapp.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Seismograph {
    //Network | Station | Latitude | Longitude | Elevation | Sitename | StartTime | EndTime

    public String fdsnCode;
    public String station;
    public double latitude;
    public double longitude;
    public double elevation;
    public String sitename;
    public Date startTime;
    public Date endTime;

    public Seismograph(String psv) throws NullPointerException, JSONException, ParseException {
        if (psv == null) {
            throw new NullPointerException("Seismograph PSV should not be null");
        }

        if (!psv.contains("|")) {
            throw new ParseException("PSV line should contain pipes (|)", 0);
        }

        String[] values = psv.split("\\|");

        if (values.length < 8) {
            throw new ParseException("PSV line doesn't have enough values", 0);
        }

        fdsnCode = values[0];
        station = values[1];
        latitude = Double.parseDouble(values[2]);
        longitude = Double.parseDouble(values[3]);
        elevation = Double.parseDouble(values[4]);
        sitename = values[5];
        startTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).parse(values[6]);
        endTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).parse(values[7]);
        if (endTime != null && endTime.after(new Date())) {
            endTime = null;
        }
    }
}
