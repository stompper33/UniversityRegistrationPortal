package com.universityregistration.model.User;

public class User {
	private String uId;
	private String username;
	private String password;
	private String role;
	
	public User(){}
	
	public void setUid(String uId){
		this.uId = uId;
	}
	public void setUsername(String username){
		this.username = username;
	}
	public void setPassword(String password){
		this.password = password;
	}
	public void setRole(String role){
		this.role = role;
	}
	public String getUid(){
		return uId;
	}
	public String getUsername(){
		return username;
	}
	public String getPassword(){
		return password;
	}
	public String getRole(){
		return role;
	}
}
