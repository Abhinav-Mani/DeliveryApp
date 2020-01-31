package com.binarybeasts.deliveryapp.Model;

import java.io.Serializable;

public class DeliveryPerson implements Serializable {
    String PhoneNumber;
    String Name;
    String Address;

    public DeliveryPerson() {
    }

    public DeliveryPerson(String phoneNumber, String name, String address) {
        PhoneNumber = phoneNumber;
        Name = name;
        Address = address;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }
}
