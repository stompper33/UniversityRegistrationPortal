package com.universityregistration.model.User;

public class PhoneNumber {
    private String phonenumber;
    private int phoneNumberId;
    private String type;
    
    public PhoneNumber(){}
    
    public PhoneNumber(String phonenumber){
        this.phonenumber = phonenumber;
    }
    
    public PhoneNumber(String phonenumber, String type){
    	this.phonenumber = phonenumber;
    	this.type = type;
    }
    
    public void setPhoneNumberId(int phoneNumberId){
        this.phoneNumberId = phoneNumberId;
    }
    
    public void setType(String type){
    	this.type = type;
    }
    
    public String getType(){
    	return type;
    }
    public int getPhoneNumberId(){
        return phoneNumberId;
    }
    
    public void setPhonenumber(String phonenumber)
    {
        this.phonenumber = phonenumber;
    }
    public String getPhonenumber()
    {
        return phonenumber;
    }
}
