package com.universityregistration.model.Service;

import com.universityregistration.dal.UserDAO;
import com.universityregistration.model.User.User;

public class UserService {
	UserDAO userDAO = new UserDAO();
	
	public boolean userExists(String username, String password){
		return userDAO.authenticateUser(username, password);
	}
	
	public User getUserById(String pId)
	{
		return userDAO.findUserById(pId);
	}
	
	public User getUserByUsernamePassword(String uname, String pass){
		return userDAO.findUserByUsernamePass(uname, pass);
	}
	
	public void addNewUser(User newUser){
		userDAO.insertNewUser(newUser);
	}
}