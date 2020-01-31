package com.binarybeasts.deliveryapp.Utility;

public class DistanceCalculator {
    String destination,start;
    Checker checker;

    public DistanceCalculator(String destination, String start) {
        this.destination = destination;
        this.start = start;
    }

    private double deg2rad(double deg){
        return deg*(Math.PI/180.0);
    }
    private double calDistance(double lat1,double lon1,double lat2,double lon2){
        double R=6371;
        double dLat=deg2rad(lat1-lat2),dLon=deg2rad(lon1-lon2);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                      Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) *
                      Math.sin(dLon/2) * Math.sin(dLon/2);
        double c=2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double d=R*c;
        return d;

    }
    public String getDistance(){
        checker =new Checker();
        if(!checker.isMapCoordinates(destination))
        return destination;
        if(!checker.isMapCoordinates(start))
        return destination;
        String a[]=destination.split(" ");
        String b[]=start.split(" ");
        Double dist = calDistance(Double.valueOf(a[0]),Double.valueOf(a[1]),Double.valueOf(b[0]),Double.valueOf(b[1]));
        return Math.round(dist*1.4)+" km(approx)";
    }
}
