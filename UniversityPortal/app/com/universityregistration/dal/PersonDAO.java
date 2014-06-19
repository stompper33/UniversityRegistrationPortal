package com.universityregistration.dal;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import java.sql.Connection;
import com.universityregistration.model.Course.CourseSection;
import com.universityregistration.model.User.Address;
import com.universityregistration.model.User.Email;
import com.universityregistration.model.User.Person;
import com.universityregistration.model.User.PhoneNumber;
import com.universityregistration.model.User.Professor;
import com.universityregistration.model.User.Student;

public class PersonDAO {
	
	/*******************
	 CREATE methods -- C
	 ********************/
	//inserting a 'persons' address into ADDRESS - TABLE
	//Used: for creating rows in the address table
	public void insertPersonAddresses(String sid, Address address){
			
			Connection con = DBHelper.getConnection();
			PreparedStatement studAddPst = null;
			
			try{
			//Add student to  table
			//String type = "student";
			//Statement st = DBHelper.getConnection().createStatement();
			String insertStudAddressQuery = 
					"INSERT INTO address (student_id, street, city, state, zip, address_id, type)" 
							+ "VALUES (?, ?, ?, ?, ?, ?, ?)";
			studAddPst = con.prepareStatement(insertStudAddressQuery);
			studAddPst.setString(1,  sid);
			studAddPst.setString(2, address.getStreet());
			studAddPst.setString(3, address.getCity());
			studAddPst.setString(4, address.getState());
			studAddPst.setString(5, address.getZip());
			studAddPst.setInt(6, address.getAddressId());
			studAddPst.setString(7, address.getType());
			studAddPst.executeUpdate();
			} 
			
			catch(SQLException ex){
				System.err.println("Inserting PERSON addresses: Threw a SQLException saving the student object.");
				System.err.println(ex.getMessage());
			}
			finally{
				
				try{
					if (studAddPst!=null){
						studAddPst.close();
					}
					if (con != null){
						con.close();
					}
				} catch(SQLException ex){
					
					System.err.println("Inserting PERSON addresses: Threw a SQLException saving the student object.");
					System.err.println(ex.getMessage());
				}
				finally{}
			}
			
		}
		
		
	//inserting a 'persons' phonenumbers into PHONE - TABLE
	//Used: for creating rows in the PhoneNumbers table
	public void insertPersonPhonenumbers(String sid, PhoneNumber p){
			
			Connection con = DBHelper.getConnection();
			PreparedStatement studPhonePst = null;
			
			try{
			//Add student to  table
			//String type = "student";
			
			//Statement st = DBHelper.getConnection().createStatement();
			String insertStudPhoneQuery = 
					"INSERT INTO phone (phone_id, student_id, phone_number, type)" 
							+ "VALUES (?, ?, ?, ?)";
			studPhonePst = con.prepareStatement(insertStudPhoneQuery);
			studPhonePst.setInt(1,  p.getPhoneNumberId());
			studPhonePst.setString(2, sid);
			studPhonePst.setString(3, p.getPhonenumber());
			studPhonePst.setString(4, p.getType());
			studPhonePst.executeUpdate();
			
			//close to save
			studPhonePst.close();
			} 

			catch(SQLException ex){
				System.err.println("Inserting PERSON phonenumbers: Threw a SQLException saving the student object.");
				System.err.println(ex.getMessage());
			}
			finally{
				
				try{
					if (studPhonePst!=null){
						studPhonePst.close();
					}
					if(con != null)
						con.close();
				} catch(SQLException ex){
					
					System.err.println("Inserting PERSON phonenumbers: Threw a SQLException saving the student object.");
					System.err.println(ex.getMessage());
				}
				finally{}
			}
		}
		
	//inserting a 'persons' emails into EMAIL - TABLE
	//Used: for creating rows in the Email table
	public void insertPersonEmails(String sid, Email e){
			
			Connection con = DBHelper.getConnection();
			PreparedStatement studEmailPst = null;
			
			try{
			//Add student to  table
			String insertStudEmailQuery = 
					"INSERT INTO email (email_id, student_id, email, type)" 
							+ "VALUES (?, ?, ?, ?)";
			studEmailPst = con.prepareStatement(insertStudEmailQuery);
			studEmailPst.setInt(1,  e.getEmailId());
			studEmailPst.setString(2, sid);
			studEmailPst.setString(3, e.getEmail());
			studEmailPst.setString(4, e.getType());
			studEmailPst.executeUpdate();
			} 
			
			catch(SQLException ex){
				System.err.println("Inserting PERSON emails: Threw a SQLException saving the student object.");
				System.err.println(ex.getMessage());
			}
			finally{
				
				try{
					if (studEmailPst!=null){
						studEmailPst.close();
					}
					if(con != null)
						con.close();
				} catch(SQLException ex){
					System.err.println("Inserting PERSON emails: Threw a SQLException saving the student object.");
					System.err.println(ex.getMessage());
				}
				finally{}
			}
		}

	
	
	/************************************************
	 UPDATE methods -- (USES HELPER METHODS) -- U
	 ************************************************/
	//updating personal information -- Used to update personal information for a given user
	//This method will update all Addresses, Emails, and PhoneNumbers for a given user!
	public void updatePersonalInformation(Person person){
			
			try{
				//GENERATE APPROPRIATE QUERY STRING BASED ON TYPE OF person PASSED IN:
				//update personal information (name, dob, ssn. etc...) -- FOR STUDENTS
				if(person instanceof Student){
					String updateQuery = "UPDATE student SET first_name ='" 
						+ person.getFirstName() + "', last_name ='"
						+ person.getLastName() + "', dob = '"
						+ person.getDob() + "', ssn = '"
						+ person.getSsn() + "', major ='"
						+ ((Student) person).getMajor() + "', class_level = "
						+ ((Student) person).getClassLevel()
						+ " WHERE uid = '"
						+ person.getID() + "'";
					updatePersonBasicInfo(person, updateQuery); //UPDATE EVERYTHING BUT emails, 
																	//phonenumbers, and addresses the given student
				}
				//update personal information (name, dob, ssn. etc...) -- FOR PROFESSORS
				if(person instanceof Professor){
					String updateQuery = "UPDATE professor SET first_name ='" 
						+ person.getFirstName() + "', last_name ='"
						+ person.getLastName() + "', dob = '"
						+ person.getDob() + "', ssn = '"
						+ person.getSsn() + "', department ='"
						+ ((Professor) person).getDepartment() + "'"
						+ " WHERE uid = '"
						+ person.getID() + "'";
					updatePersonBasicInfo(person, updateQuery); //UPDATE EVERYTHING BUT emails,
																	//phonenumbers, and addresses for the given professor
				}
				
				//NOW UPDATE THE ADDRESSES, EMAILS, AND PHONENUMBERS FOR THE GIVEN PERSON PASSED IN
				//Update Addresses 
				for(Address a : person.getAddress())
					updatingPersonAddresses(person.getID(), a); //HELPER METHODS
				
				//update emails
				for (Email e : person.getEmail())
					updatingPersonEmails(person.getID(), e);	//HELPER METHOD
				
				//update phonenumbers
				for (PhoneNumber p : person.getPhoneNumber())
					updatingPersonPhoneNumbers(person.getID(), p); //HELPER METHOD
				
			}
			finally{}
		}
		
	/**********************************************
	 DELETE methods -- (USES HELPER METHODS) -- D
	 *********************************************/
	public void deletePersonalInformation(Person person, Object obj){
		
		//GENERATE APPROPRIATE QUERY STRING BASED ON TYPE OF obj PASSED IN:
		
		//delete person information -- FOR Addresses
		if(obj instanceof Address){
			String deleteAddressQuery = "DELETE FROM address WHERE student_id = '" + person.getID() + "' AND address_id = "
					+ ((Address)obj).getAddressId();
			deletePersonAddress(deleteAddressQuery); 
		}
		
		//delete person information -- FOR Emails
		if(obj instanceof Email){
			System.out.println ("inside email delete method");
			String deleteEmailQuery = "DELETE FROM email WHERE student_id = '" + person.getID() + "' AND email_id = "
					+ ((Email)obj).getEmailId();
			deletePersonEmail(deleteEmailQuery);
		}
			
		//delete person information -- for PhoneNumbers
		if(obj instanceof PhoneNumber){
			String deletePhoneQuery = "DELETE FROM phone WHERE student_id = '" + person.getID() + "' AND phone_id = " 
					+ ((PhoneNumber)obj).getPhoneNumberId();
			deletePersonPhoneNumber(deletePhoneQuery);
		}
			
	}
	
	
	/***************************************************************
	HELPER METHODS - CALLED WITHIN OTHER FUNCTIONS IN THE CLASS
	****************************************************************/
	
		/**********************
		 UPDATE helper methods
		 *********************/
	//Used for updating a specific record in the Address table (based on person id) -- for Addresses ONLY
	public void updatingPersonAddresses(String pid, Address a){

			Connection conn = DBHelper.getConnection();
			Statement statement = null;
			

			String updateQuery = "UPDATE Address"
					+ " SET street = '"+ a.getStreet() + "', "
					+ "city = '" + a.getCity() + "', "
					+ "state = '" + a.getState() + "', "
					+ "zip = '" + a.getZip() + "', "
					+ "type = '" + a.getType() + "' "
					+ "WHERE student_id = '" + pid + "' AND "
					+ "address_id = " + a.getAddressId();

			try {
				statement = conn.createStatement();
				// execute update SQL stetement
				statement.executeUpdate(updateQuery);

			} catch (SQLException e) {
				System.out.println(e.getMessage());

			} finally {

				try{
					if (statement != null) {
						statement.close();
					}

					if (conn != null) {
						conn.close();

					}

				}catch(SQLException ex){
					System.err.println("Updating PERSON addresses: Threw a SQLException saving the Address object.");
					System.err.println(ex.getMessage());

				}

				finally{}

	 		}		

		}
	
	//Used for updating a specific record in the PhoneNumber table (based on person id) -- for PhoneNumbers ONLY
	public void updatingPersonPhoneNumbers(String pid, PhoneNumber p){

			Connection conn = DBHelper.getConnection();
			Statement statement = null;

			String updateQuery = "UPDATE Phone"
					+ " SET phone_number = '"+ p.getPhonenumber() + "', "
					+ "type = '" + p.getType() + "' "
					+ "WHERE student_id = '" + pid + "' AND "
					+ "phone_id = " + p.getPhoneNumberId();
			
			try {
				statement = conn.createStatement();

				// execute update SQL stetement
				statement.executeUpdate(updateQuery);

			} catch (SQLException e) {
				System.out.println(e.getMessage());

			} finally {

				try{

					if (statement != null) {
						statement.close();

					}

					if (conn != null) {
						conn.close();

					}

				}catch(SQLException ex){				
					System.err.println("Updating PERSON PhoneNumber: Threw a SQLException saving the PhoneNumber object.");
					System.err.println(ex.getMessage());

				}

				finally{}

	 		}			

		}

	//Used for updating a specific record in the Email table (based on person id) -- for Emails ONLY
	public void updatingPersonEmails(String pid, Email e){
			Connection conn = DBHelper.getConnection();
			Statement statement = null;
			String updateQuery = "UPDATE Email"
					+ " SET email = '" + e.getEmail() + "', "
					+ "type = '" + e.getType() + "' "
					+ "WHERE student_id = '" + pid + "' AND "
					+ "email_id = " + e.getEmailId();

			try {
				statement = conn.createStatement();
				statement.executeUpdate(updateQuery);	 

			} catch (SQLException ex) {
				System.out.println(ex.getMessage());

			} finally {

				try{
					if (statement != null) {
						statement.close();

					}

					if (conn != null) {
						conn.close();
					}

				}catch(SQLException ex){					

					System.err.println("Updating PERSON Email: Threw a SQLException saving the Email object.");
					System.err.println(ex.getMessage());

				}

				finally{}
			}

	 	}

	//USED TO UPDATE BASIC INFO: FirstName, LastName, Dob, Ssn, etc... (EXCLUDING emails, phones, addresses)		
	private void updatePersonBasicInfo(Person person, String query){
			Connection conn = DBHelper.getConnection();
			Statement stmt = null;
			
			try{
				stmt = conn.createStatement();
				stmt.executeUpdate(query);
			}catch(SQLException e){
				System.out.println(e.getMessage());
			} finally {
				try{
					if(stmt != null){
						stmt.close();
					}
					if(conn != null)
						conn.close();
				}catch(SQLException ex){
					System.err.println("UPDATING PERSON BASIC INFO: Threw SQLException.");
					System.err.println(ex.getMessage());
				}
				finally{}
			}
			
		}

		/**********************
		 DELETE helper methods
		************************/
	//DELETE a 'persons' Address from ADDRESS - TABLE
	public void deletePersonAddress(String deleteQuery){
		
			System.out.println("Inside deletePersonAddress method");
			Connection conn = DBHelper.getConnection();
			Statement stmt = null;
		
			try {
				stmt = conn.createStatement();	 
				// execute delete SQL stetement
				stmt.executeUpdate(deleteQuery);
	 
				System.out.println("Address Record is deleted!");
	 
			} catch (SQLException e) {
				System.err.println("DELETING PERSON address: Threw a SQLException deleting Address object.");
				System.out.println(e.getMessage());
	 
			} finally {
				try{
					if (stmt != null) {
						stmt.close();
					}
	 
					if (conn != null) {
						conn.close();
					}
				}catch(SQLException ex){
					
					System.err.println("DELETING PERSON address: Threw a SQLException deleting Address object.");
					System.err.println(ex.getMessage());
				}
			}
		}
		
	//DELETE a 'persons' PhoneNumber from PHONE - TABLE
	public void deletePersonPhoneNumber(String deleteQuery){
			Connection conn = DBHelper.getConnection();
			Statement stmt = null;
		
			try {
				stmt = conn.createStatement();
				
				// execute delete SQL stetement
				stmt.execute(deleteQuery);
	 
				System.out.println("Phone Record deleted!");
	 
			} catch (SQLException e) {
				System.err.println("Deleting PERSON phonenumber: Threw a SQLException deleting Phone object.");
				System.out.println(e.getMessage());
	 
			} finally {
				try{
					if (stmt != null) {
						stmt.close();
					}
	 
					if (conn != null) {
						conn.close();
					}
				}catch(SQLException ex){					
					System.err.println("Deleting PERSON phonenumber: Threw a SQLException deleting Phone object.");
					System.err.println(ex.getMessage());
				}
			}
		}	
		
	//DELETE a 'perons' Email from EMAIL - TABLE
	public void deletePersonEmail(String deleteQuery){
		
			Connection conn = DBHelper.getConnection();
			Statement stmt = null;
		
			try {
				stmt = conn.createStatement();
				
				// execute delete SQL stetement
				stmt.execute(deleteQuery);	 
				System.out.println("Email Record deleted!");
	 
			} catch (SQLException x) {
				System.err.println("DELETING PERSON emails: Threw a SQLException deleting Email object.");
				System.out.println(x.getMessage());
	 
			} finally {
				try{
					if (stmt != null) {
						stmt.close();
					}
	 
					if (conn != null) {
						conn.close();
					}
				}catch(SQLException ex){					
					System.err.println("DELETING PERSON emails: Threw a SQLException deleting Email object.");
					System.err.println(ex.getMessage());
				}
			}
		}

	/*************************************************************
	 * DELETING METHODS: EMAIL, PHONE, ADDRESS
	 *************************************************************/
	//DELETING ADDRESS
    public void deleteAddress(int aid){
    	
    	Connection con = DBHelper.getConnection();
    	PreparedStatement stmt = null;
    	
    	try{
    		String deleteAddressQuery = "DELETE FROM address WHERE address_id = "
    				+ aid;
    		stmt = con.prepareStatement(deleteAddressQuery);
    		stmt.executeUpdate();
    	}catch(SQLException ex){
    		System.err.println("SQLException: Threw error exception when deleting address");
    		System.err.println(ex.getMessage());
    	}
    	finally{
    		try{
    			if(stmt != null)
    				stmt.close();
    			if(con != null)
    				con.close();
    		}catch(SQLException ex){
        		System.err.println("SQLException: Threw error exception when deleting address");
        		System.err.println(ex.getMessage());
    		}
    	}
    }
    
    //DELETING PHONE
    public void deletePhone(int phnId){
    	
    	Connection con = DBHelper.getConnection();
    	PreparedStatement stmt = null;
    	
    	try{
    		String deletePhoneQuery = "DELETE FROM phone WHERE phone_id = "
    				+ phnId;
    		stmt = con.prepareStatement(deletePhoneQuery);
    		stmt.executeUpdate();
    	}catch(SQLException ex){
    		System.err.println("SQLException: Threw error exception when deleting phone");
    		System.err.println(ex.getMessage());
    	}
    	finally{
    		try{
    			if(stmt != null)
    				stmt.close();
    			if(con != null)
    				con.close();
    		}catch(SQLException ex){
        		System.err.println("SQLException: Threw error exception when deleting phone");
        		System.err.println(ex.getMessage());
    		}
    	}
    }
    
    //DELETING EMAIL
    public void deleteEmail(int eid){
    	Connection con = DBHelper.getConnection();
    	PreparedStatement stmt = null;
    	
    	try{
    		String deleteEmailQuery = "DELETE FROM email WHERE email_id = "
    				+ eid;
    		stmt = con.prepareStatement(deleteEmailQuery);
    		stmt.executeUpdate();
    	}catch(SQLException ex){
    		System.err.println("SQLException: Threw error exception when deleting email");
    		System.err.println(ex.getMessage());
    	}
    	finally{
    		try{
    			if(stmt != null)
    				stmt.close();
    			if(con != null)
    				con.close();
    		}catch(SQLException ex){
        		System.err.println("SQLException: Threw error exception when deleting email");
        		System.err.println(ex.getMessage());
    		}
    	}
    }
	
	
	/********************************
	 * RETRIEVING PERSONAL INFORMATION
	 ************************************/
	public Address getAddressById(int aid) {
		
		Connection conn = DBHelper.getConnection();
		Statement stmt = null;
		try{
			stmt = conn.createStatement();
			String getAddressQuery = 
					"SELECT student_id, street, city, state, zip, address_id, type FROM address WHERE address_id = "
							+ aid;
			ResultSet addressRS = stmt.executeQuery(getAddressQuery);
			Address retAddress = new Address();
			while(addressRS.next()){
				retAddress.setStreet(addressRS.getString("street"));
				retAddress.setCity(addressRS.getString("city"));
				retAddress.setState(addressRS.getString("state"));
				retAddress.setZip(addressRS.getString("zip"));
				retAddress.setAddressId(addressRS.getInt("address_id"));
				retAddress.setType(addressRS.getString("type"));				
			}
			addressRS.close();
			
			return retAddress;
			
		}catch(SQLException ex){
			System.err.println("Retrieving address: Threw a SQLException retrieving Address object.");
			System.out.println(ex.getMessage());
			
		}finally{
			try{
				if(stmt != null)
					stmt.close();
				if(conn != null)
					conn.close();
			}catch(SQLException ex){
				System.err.println("Retrieving address: Threw a SQLException retrieving address object.");
				System.out.println(ex.getMessage());
			}
		}
		return null;			
	}
	
	//get email by id
	public Email getEmailById(int eid){
		Connection conn = DBHelper.getConnection();
		Statement stmt = null;
		try{
			stmt = conn.createStatement();
			String getEmailQuery = 
					"SELECT email_id, student_id, email, type FROM email WHERE email_id = "
							+ eid;
			ResultSet emailRS = stmt.executeQuery(getEmailQuery);
			Email retEmail = new Email();
			while (emailRS.next()){
				retEmail.setEmailId(emailRS.getInt("email_id"));
				retEmail.setEmail(emailRS.getString("email"));
				retEmail.setType(emailRS.getString("type"));
			}
			
			emailRS.close();
			return retEmail;
			
		}catch(SQLException ex){
			System.err.println("Retrieving Email: Threw a SQLException retrieving email object.");
			System.out.println(ex.getMessage());
			
		}finally{
			try{
				if(stmt != null)
					stmt.close();
				if(conn != null)
					conn.close();
			}catch(SQLException ex){
				System.err.println("Retrieving Email: Threw a SQLException retrieving email object.");
				System.out.println(ex.getMessage());
			}
		}
		return null;
	}
	
	//get phonenumber by id
	public PhoneNumber getPhoneById(int pid){
			
		Connection conn = DBHelper.getConnection();
		Statement stmt = null;
		
		try{
			stmt = conn.createStatement();
			String getPhoneQuery = 
					"SELECT phone_id, student_id, phone_number, type FROM phone WHERE phone_id = "
							+ pid;
			ResultSet phoneRS = stmt.executeQuery(getPhoneQuery);
			PhoneNumber retPhone = new PhoneNumber();
			while (phoneRS.next()){
				retPhone.setPhoneNumberId(phoneRS.getInt("phone_id"));
				retPhone.setPhonenumber(phoneRS.getString("phone_number"));
				retPhone.setType(phoneRS.getString("type"));
			}
			
			phoneRS.close();
			return retPhone;
				
		}catch(SQLException ex){
			System.err.println("Retrieving PhoneNumber: Threw a SQLException retrieving PhoneNumber object.");
			System.out.println(ex.getMessage());
				
		}finally{
			try{
				if(stmt != null)
					stmt.close();
				if(conn != null)
					conn.close();
			}catch(SQLException ex){
				System.err.println("Retrieving PhoneNumber: Threw a SQLException retrieving PhoneNumber object.");
				System.out.println(ex.getMessage());
			}
		}
		return null;
	}
	
	/*******************************************************************
	 END OF HELPER FUNCTION
	*******************************************************************/
	
	
	//added 11/30/2013
	/*FUCNTIONS TO GET STUDENT_ID (PERSON ID) for a given email, address, phone ID*/
	
	public String getUserIdForAddressId(int addId){
		Connection con = DBHelper.getConnection();
		Statement stmt = null;
		
		try{
			//Get Professor
			
			//1st. create Professor obj based on student table
			stmt = con.createStatement();
			String selectUidQuery = "SELECT student_id FROM Address WHERE address_id = "
					+ addId;
			ResultSet addRS = stmt.executeQuery(selectUidQuery);
			
			//Get Professor
			String uId  = null;
			while(addRS.next()){
				uId = addRS.getString("student_id");
			}
			
			//close to manage resources
			addRS.close();
			return uId;
			
		}catch(SQLException ex){
			System.err.println("PersonDAO: (762) Retrieving studentID: Threw a SQLException retrieving StudentID object.");
			System.out.println(ex.getMessage());
				
		}finally{
			try{
				if(stmt != null)
					stmt.close();
				if(con != null)
					con.close();
			}catch(SQLException ex){
				System.err.println("PersonDAO: (762) Retrieving studentID: Threw a SQLException retrieving StudentID object.");
				System.out.println(ex.getMessage());
			}
		}
		return null;
	}
	
	public String getUserIdForEmailId(int emId){
		Connection con = DBHelper.getConnection();
		Statement stmt = null;
		
		try{
			//Get Professor
			
			//1st. create Professor obj based on student table
			stmt = con.createStatement();
			String selectUidQuery = "SELECT student_id FROM Email WHERE email_id = "
					+ emId;
			ResultSet emRS = stmt.executeQuery(selectUidQuery);
			
			//Get Professor
			String uId  = null;
			while(emRS.next()){
				uId = emRS.getString("student_id");
			}
			
			//close to manage resources
			emRS.close();
			return uId;
			
		}catch(SQLException ex){
			System.err.println("PersonDAO: Retrieving studentID: Threw a SQLException retrieving StudentID for email.");
			System.out.println(ex.getMessage());
				
		}finally{
			try{
				if(stmt != null)
					stmt.close();
				if(con != null)
					con.close();
			}catch(SQLException ex){
				System.err.println("PersonDAO: (762) Retrieving studentID: Threw a SQLException retrieving StudentID for email.");
				System.out.println(ex.getMessage());
			}
		}
		return null;
	}

	public String getUserIdForPhoneId(int phId){
		Connection con = DBHelper.getConnection();
		Statement stmt = null;
		
		try{
			//Get Professor
			
			//1st. create Professor obj based on student table
			stmt = con.createStatement();
			String selectUidQuery = "SELECT student_id FROM Phone WHERE phone_id = "
					+ phId;
			ResultSet phRS = stmt.executeQuery(selectUidQuery);
			
			//Get Professor
			String uId  = null;
			while(phRS.next()){
				uId = phRS.getString("student_id");
			}
			
			//close to manage resources
			phRS.close();
			return uId;
			
		}catch(SQLException ex){
			System.err.println("PersonDAO: Retrieving studentID: Threw a SQLException retrieving StudentID for phone.");
			System.out.println(ex.getMessage());
				
		}finally{
			try{
				if(stmt != null)
					stmt.close();
				if(con != null)
					con.close();
			}catch(SQLException ex){
				System.err.println("PersonDAO: Retrieving studentID: Threw a SQLException retrieving StudentID for phone.");
				System.out.println(ex.getMessage());
			}
		}
		return null;
	}	
}