package it.unimib.quakeapp.models;

public class Point {
    public double lat;
    public double lng;
    public double depth;

    public Point(double lat, double lng, double depth){
        this.lat = lat;
        this.lng = lng;
        this.depth = depth;
    }
}