package it.unimib.quakeapp.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Place {
    public double lat;
    public double lon;
    public String displayName;
    public List<String> addressDetails;

    public Place(JSONObject root) throws NullPointerException, JSONException {
        if (root == null) {
            throw new NullPointerException("Root JSON should not be null");
        }

        this.lat = root.getDouble("lat");
        this.lon = root.getDouble("lon");
        this.displayName = root.getString("display_name");

        JSONObject addressObj = root.getJSONObject("address");
        addressDetails = new ArrayList<>();

        Iterator<String> keysIterator = addressObj.keys();
        while (keysIterator.hasNext()) {
            String key = keysIterator.next();
            addressDetails.add(addressObj.getString(key));
        }
    }
}
