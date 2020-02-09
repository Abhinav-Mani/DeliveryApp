package com.binarybeasts.deliveryapp.Utility;

public class Checker {
    public boolean isMapCoordinates(String cord){
        if(cord==null)
            return false;
        return cord.matches("\\d{0,3}\\.\\d{6,} \\d{0,3}\\.\\d{6,}");
    }
}
