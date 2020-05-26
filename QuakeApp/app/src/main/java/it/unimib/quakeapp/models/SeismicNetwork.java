package it.unimib.quakeapp.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class SeismicNetwork {
    public String doi;
    public Date endDate;
    public String name;
    public String fdsnCode;
    public Date startDate;

    public SeismicNetwork(JSONObject network) throws NullPointerException, JSONException {
        if (network == null) {
            throw new NullPointerException("Feature JSON should not be null");
        }

        this.doi = network.getString("doi");
        this.endDate = new Date(network.getLong("end_date"));
        this.name = network.getString("name");
        this.fdsnCode = network.getString("fdsn_code");
        this.startDate = new Date(network.getLong("start_date"));
    }
}
