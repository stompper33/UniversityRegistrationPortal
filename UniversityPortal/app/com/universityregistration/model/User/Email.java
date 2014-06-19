package com.universityregistration.model.User;

public class Email {
    private String email;
    private int emailId;
    private String type;
    
    
    public Email(){}
    
    public Email(String email, String type){
    	this.email = email;
    	this.type = type;
    }
    public Email (String email){
        this.email = email;
    }
    
    public void setType(String type){
    	this.type = type;
    }
    
    public String getType(){
    	return type;
    }
    
    public void setEmailId(int emailId){
        this.emailId = emailId;
    }
    
    public int getEmailId(){
        return emailId;
    }
    
    public void setEmail(String email)
    {
        this.email = email;
    }
    public String getEmail()
    {
        return email;
    }
}
