package com.universityregistration.dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.universityregistration.model.User.Address;
import com.universityregistration.model.User.Email;
import com.universityregistration.model.User.PhoneNumber;
import com.universityregistration.model.User.User;

public class UserDAO {
	
	public boolean authenticateUser(String username, String password){

		Statement stmt = null;
		Connection con = DBHelper.getConnection();
		try{			
			//1st. create Professor obj based on student table
			stmt = con.createStatement();
			String checkUserExists = "SELECT user_id FROM user WHERE EXISTS (SELECT * FROM user" +
				" WHERE username = '" + username + "' AND password = '" + password +"')";
			ResultSet userRS = stmt.executeQuery(checkUserExists);

			while(userRS.next()){
				userRS.close();
				stmt.close();
				return true;
			}
			userRS.close();
		}
		catch (SQLException ex){
			System.err.println("ProfessorDAO: Threw a SQLException retrieving the professor object.");
			System.err.println(ex.getMessage());
			ex.printStackTrace();
		}finally{
			try{
				if(stmt != null)
					stmt.close();
				if(con != null)
					con.close();
			}
			catch (SQLException ex){
				System.err.println("ProfessorDAO: Threw a SQLException retrieving the professor object.");
				System.err.println(ex.getMessage());
				ex.printStackTrace();
			}
		}
		return false;		
	}//end of checkUserExists

	public User findUserById(String pId){
	
			Statement stmt = null;
			
		try{
			//Get Professor
			
			//1st. create Professor obj based on student table
			stmt = DBHelper.getConnection().createStatement();
			String checkUserExists = "SELECT user_id, username, password, role FROM user WHERE user_id = '" + pId + "'";
			ResultSet uRS = stmt.executeQuery(checkUserExists);
			User user = new User();
			while(uRS.next()){
				user.setUid(uRS.getString("user_id"));
				user.setUsername(uRS.getString("username"));
				user.setPassword(uRS.getString("password"));
				user.setRole(uRS.getString("role"));	
			}
			uRS.close();
			stmt.close();
			return user;
		}
		catch (SQLException ex){
			System.err.println("ProfessorDAO: Threw a SQLException retrieving the professor object.");
			System.err.println(ex.getMessage());
			ex.printStackTrace();
		}finally{
			try{
				if(stmt != null)
					stmt.close();
			}
			catch (SQLException ex){
				System.err.println("ProfessorDAO: Threw a SQLException retrieving the professor object.");
				System.err.println(ex.getMessage());
				ex.printStackTrace();
			}
		}
		return null;	
	}
	
	public User findUserByUsernamePass(String username, String pass){
	
			Statement stmt = null;
		try{
			//Get Professor
			
			//1st. create Professor obj based on student table
			stmt = DBHelper.getConnection().createStatement();
			String checkUserExists = "SELECT user_id, username, password, role FROM user WHERE username = '" + username +
						"' AND password = '" + pass +"'";
			ResultSet uRS = stmt.executeQuery(checkUserExists);
			User user = new User();
			while(uRS.next()){
				user.setUid(uRS.getString("user_id"));
				user.setUsername(uRS.getString("username"));
				user.setPassword(uRS.getString("password"));
				user.setRole(uRS.getString("role"));	
			}
			uRS.close();
			stmt.close();
			return user;
		}
		catch (SQLException ex){
			System.err.println("ProfessorDAO: Threw a SQLException retrieving the professor object.");
			System.err.println(ex.getMessage());
			ex.printStackTrace();
		}finally{
			try{
				if(stmt != null)
					stmt.close();
			}
			catch (SQLException ex){
				System.err.println("ProfessorDAO: Threw a SQLException retrieving the professor object.");
				System.err.println(ex.getMessage());
				ex.printStackTrace();
			}
		}
		return null;	
	}
	
	public void insertNewUser(User newUser){
		Connection con = DBHelper.getConnection();
		PreparedStatement userPst = null;
		
		try{
			String insertUserQuery = "INSERT INTO User(user_id, username, password, role)" +
							" VALUES (?, ?, ?, ?)";
			userPst = con.prepareStatement(insertUserQuery);
			userPst.setString(1, newUser.getUid());
			userPst.setString(2,  newUser.getUsername());
			userPst.setString(3, newUser.getPassword());
			userPst.setString(4, newUser.getRole());
			userPst.executeUpdate();
			
			//close to save
			userPst.close();
			con.close();
			
		} catch(SQLException ex){
			System.err.println("StudentDAO: Threw a SQLException inserting student object.");
			System.out.println(ex.getMessage());			
		}finally{
			try{
				if(userPst != null)
					userPst.close();
				if (con != null){
					System.out.println("Database closed after inserting student");
					con.close();
				}
			}catch (SQLException ex){
				System.err.println("StudentDAO: Threw a SQLException inserting student object.");
				System.out.println(ex.getMessage());
			}
		}				
	}
}
