package it.unimib.quakeapp.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SeismicNetwork implements Serializable {
    public String doi;
    public Date endDate;
    public String name;
    public String fdsnCode;
    public Date startDate;

    public SeismicNetwork(JSONObject network) throws NullPointerException, JSONException, ParseException {
        if (network == null) {
            throw new NullPointerException("Feature JSON should not be null");
        }

        this.doi = network.getString("doi");
        if (!network.isNull("end_date")) {
            this.endDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(network.getString("end_date"));
        }
        this.name = network.getString("name");
        this.fdsnCode = network.getString("fdsn_code");
        this.startDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(network.getString("start_date"));
    }
}
