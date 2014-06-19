package com.universityregistration.model.RegDates;

import java.util.Date;

public final class RegistrationDates {

	public static java.sql.Date lastDayToDropClasses;
	public static java.sql.Date startDate;
	public static java.sql.Date endDate;
	public static java.sql.Date lastDayToRegister;
	public static java.sql.Date firstDayToRegister;
	
	public void setLastDayToDropClasses(java.sql.Date lastDayToDropClasses){
		this.lastDayToDropClasses = lastDayToDropClasses;
	}
	
	public java.sql.Date getLastDayToDropClasses(){
		return lastDayToDropClasses;
	}
	
	public void setStartDate(java.sql.Date startDate){
		this.startDate = startDate;
	}
	
	public java.sql.Date getStartDate(){
		return startDate;
	}
	
	public void setEndDate(java.sql.Date endDate){
		this.endDate = endDate;
	}
	
	public java.sql.Date getEndDate(){
		return endDate;
	}
	
	public void setLastDayToRegiter(java.sql.Date lastDayToRegister){
		this.lastDayToRegister = lastDayToRegister;
	}
	
	public java.sql.Date getLastDayToRegister(){
		return lastDayToRegister;
	}
	
	
	public void setFirstDayToRegister(java.sql.Date firstDayToRegister){
		this.firstDayToRegister = firstDayToRegister;
	}
	
	public java.sql.Date getFirstDayToRegister(){
		return firstDayToRegister;
	}
}
