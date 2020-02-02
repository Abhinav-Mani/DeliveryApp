package com.binarybeasts.deliveryapp.Model;



import java.io.Serializable;

public class DeliveryRequests implements Serializable {
    String FarmersNumber,FarmersAddress,CustomerPhoneNo, CustomerAddress,Amount,UID,Driver;

    public DeliveryRequests(){}

    public DeliveryRequests(String farmersNumber,String farmersAddress, String customerPhoneNo, String customerAddress, String amount) {
        FarmersNumber = farmersNumber;
        FarmersAddress = farmersAddress;
        CustomerPhoneNo = customerPhoneNo;
        CustomerAddress = customerAddress;
        Amount = amount;
    }

    public String getDriver() {
        return Driver;
    }

    public void setDriver(String driver) {
        Driver = driver;
    }

    public String getFarmersNumber() {
        return FarmersNumber;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public void setFarmersNumber(String farmersNumber) {
        FarmersNumber = farmersNumber;
    }

    public String getFarmersAddress() {
        return FarmersAddress;
    }

    public void setFarmersAddress(String farmersAddress) {
        FarmersAddress = farmersAddress;
    }

    public String getCustomerPhoneNo() {
        return CustomerPhoneNo;
    }

    public void setCustomerPhoneNo(String customerPhoneNo) {
        CustomerPhoneNo = customerPhoneNo;
    }

    public String getCustomerAddress() {
        return CustomerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        CustomerAddress = customerAddress;
    }

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }
}
