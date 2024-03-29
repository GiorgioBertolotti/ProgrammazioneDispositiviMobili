package it.unimib.quakeapp.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class Earthquake {
    public String id;
    public double richter_mag;
    public double mercalli;
    public String placeDesc;
    public Place place;
    public Date time;
    public Date updated;
    public String url;
    public String detail;
    public Integer felt;
    public Double cdi;
    public Integer mmi;
    public String alert;
    public String status;
    public boolean tsunami;
    public Integer sig;
    public String net;
    public String code;
    public String ids;
    public String sources;
    public String types;
    public Integer nst;
    public Double dmin;
    public Double rms;
    public Double gap;
    public String magType;
    public String title;
    public Point coordinates;

    public Earthquake(JSONObject feature) throws NullPointerException, JSONException {
        if (feature == null) {
            throw new NullPointerException("Feature JSON should not be null");
        }

        this.id = feature.getString("id");

        JSONObject properties = feature.getJSONObject("properties");
        this.richter_mag = properties.optDouble("mag");
        this.placeDesc = properties.getString("place");
        this.time = new Date(properties.getLong("time"));
        this.updated = new Date(properties.getLong("updated"));
        this.url = properties.getString("url");
        this.detail = properties.getString("detail");
        this.felt = properties.optInt("felt");
        this.cdi = properties.optDouble("cdi");
        this.mmi = properties.optInt("mmi");
        this.alert = properties.getString("alert");
        this.status = properties.getString("status");
        this.tsunami = properties.optInt("tsunami") == 1;
        this.sig = properties.optInt("sig");
        this.net = properties.getString("net");
        this.code = properties.getString("code");
        this.ids = properties.getString("ids");
        this.sources = properties.getString("sources");
        this.types = properties.getString("types");
        this.nst = properties.optInt("nst");
        this.dmin = properties.optDouble("dmin");
        this.rms = properties.optDouble("rms");
        this.gap = properties.optDouble("gap");
        this.magType = properties.getString("magType");
        this.title = properties.getString("title");

        JSONObject geometry = feature.getJSONObject("geometry");
        JSONArray coordinates = geometry.getJSONArray("coordinates");
        double lng = coordinates.optDouble(0);
        double lat = coordinates.optDouble(1);
        double depth = coordinates.optDouble(2);
        this.coordinates = new Point(lat, lng, depth);

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

    public void setPlace(Place place) {
        this.place = place;
    }

    public String getPlaceDescWithoutKm() {
        String[] words = this.placeDesc.split(" ");
        String toReturn = this.placeDesc;

        if (words.length > 3) {
            toReturn = "";
            for (int i = 3; i < words.length; i++) {
                toReturn += words[i] + " ";
            }
        }

        return toReturn;
    }
}
