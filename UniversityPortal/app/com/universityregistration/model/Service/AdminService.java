package com.universityregistration.model.Service;

import com.universityregistration.dal.DatesDAO;
import com.universityregistration.model.RegDates.RegistrationDates;

public class AdminService {
	
	private DatesDAO dateDAO = new DatesDAO();

	public void setDates(RegistrationDates rd, int catId){
		dateDAO.insertRegistrationDates(rd, catId);
	}
	
	public void updateDates(RegistrationDates rd, int catId){
		dateDAO.updateRegistrationDatesByCatId(rd, catId);
	}
	
	public void retreiveDatesByCatId(int catId){
		dateDAO.getRegistrationDatesByCatId(catId);
	}
	
	public boolean datesAlreadySet(int catId){
		return dateDAO.datesAreSet(catId);
	}
}
