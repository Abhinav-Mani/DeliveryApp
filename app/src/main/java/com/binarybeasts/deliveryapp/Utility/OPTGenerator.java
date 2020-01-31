package com.binarybeasts.deliveryapp.Utility;

public class OPTGenerator {
    public String getOTP(int l)
    {
        String OTP="";
        for (int i=0;i<l;i++)
        {
            OTP+= getRandomCharacter();
        }
        return OTP;
    }

    private char getRandomCharacter() {
        return (char) ((int)Math.floor(Math.random()*26)+'a');
    }
}
