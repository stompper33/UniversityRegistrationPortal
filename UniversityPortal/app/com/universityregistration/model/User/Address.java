package com.universityregistration.model.User;

public class Address {
    private int addressId;
    private String street;
    private String city;
    private String state;
    private String zip;
    private String type;
    
    
    public Address(){}
    
    public Address(String street, String city, String state, String zip, String type){
    	this.street = street;
    	this.city = city;
    	this.state = state;
    	this.zip = zip;
    	this.type = type;
    }
    /*
    public Address(String street, String city, String state, String zip){
        this.street = street;
        this.city = city;
        this.state = state;
        this.zip = zip;
    }*/
    
    public void setAddressId(int addressId){
        this.addressId = addressId;
    }
    
    public int getAddressId(){
        return addressId;
    }
    
    public void setType(String type){
    	this.type = type;
    }
    
    public String getType(){
    	return type;
    }
    public void setStreet(String street){
        this.street = street;
    }
    
    public String getStreet(){
        return street;
    }
    
    public void setCity(String city){
        this.city = city;
    }
    
    public String getCity(){
        return city;
    }
    
    public void setState(String state){
        this.state = state;
    }
    
    public String getState(){
        return state;
    }
    
    public void setZip(String zip){
        this.zip = zip;
    }
    
    public String getZip(){
        return zip;
    }
}
