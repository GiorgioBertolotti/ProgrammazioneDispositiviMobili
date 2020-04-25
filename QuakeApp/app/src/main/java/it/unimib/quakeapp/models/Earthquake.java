package it.unimib.quakeapp.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class Earthquake {
    public String id;
    public double richter_mag;
    public double mercalli;
    public String place;
    public Date time;
    public Date updated;
    public String url;
    public String detail;
    public int felt;
    public double cdi;
    public int mmi;
    public String alert;
    public String status;
    public boolean tsunami;
    public int sig;
    public String net;
    public String code;
    public String ids;
    public String sources;
    public String types;
    public int nst;
    public double dmin;
    public double rms;
    public double gap;
    public String magType;
    public String title;
    public Point coordinates;

    public Earthquake(JSONObject feature) throws NullPointerException, JSONException {
        if (feature == null) {
            throw new NullPointerException("Feature JSON should not be null");
        }

        this.id = feature.getString("id");

        JSONObject properties = feature.getJSONObject("properties");
        this.richter_mag = properties.getDouble("mag");
        this.place = properties.getString("place");
        this.time = new Date(properties.getLong("time"));
        this.updated = new Date(properties.getLong("updated"));
        this.url = properties.getString("url");
        this.detail = properties.getString("detail");
        this.felt = properties.getInt("felt");
        this.cdi = properties.getDouble("cdi");
        this.mmi = properties.getInt("mmi");
        this.alert = properties.getString("alert");
        this.status = properties.getString("status");
        this.tsunami = properties.getInt("tsunami") == 1;
        this.sig = properties.getInt("sig");
        this.net = properties.getString("net");
        this.code = properties.getString("code");
        this.ids = properties.getString("ids");
        this.sources = properties.getString("sources");
        this.types = properties.getString("types");
        this.nst = properties.getInt("nst");
        this.dmin = properties.getDouble("dmin");
        this.rms = properties.getDouble("rms");
        this.gap = properties.getDouble("gap");
        this.magType = properties.getString("magType");
        this.title = properties.getString("title");

        JSONObject geometry = feature.getJSONObject("geometry");
        JSONArray coordinates = geometry.getJSONArray("coordinates");
        double lat = coordinates.getDouble(0);
        double lng = coordinates.getDouble(1);
        this.coordinates = new Point(lat, lng);

        double[] richterToMercalliSteps = {2, 2.5, 2.8, 3.5, 4.2, 4.8, 5.4, 6.1, 6.5, 6.9, 7.3};
        boolean found = false;
        for (int i = 0; i < richterToMercalliSteps.length; i++) {
            if (this.richter_mag < richterToMercalliSteps[i]) {
                this.mercalli = i + 1;
                found = true;
                break;
            }
        }
        if (!found) {
            this.mercalli = richterToMercalliSteps.length + 1;
        }
    }
}
