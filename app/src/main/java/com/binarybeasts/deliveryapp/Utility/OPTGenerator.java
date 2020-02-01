package com.binarybeasts.deliveryapp.Utility;

public class OPTGenerator {
    public String getOTP(int l)
    {
        StringBuilder OTP= new StringBuilder();
        for (int i=0;i<l;i++) {
            OTP.append(getRandomCharacter());
        }
        return OTP.toString();
    }

    private char getRandomCharacter() {
        return (char) ((int)Math.floor(Math.random()*26)+'a');
    }
}
