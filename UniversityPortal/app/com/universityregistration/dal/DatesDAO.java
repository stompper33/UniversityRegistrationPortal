package com.universityregistration.dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import com.universityregistration.model.RegDates.RegistrationDates;

public class DatesDAO {

	//FIND a registration dates
	public RegistrationDates getRegistrationDatesByCatId(int catId){
		
		Connection con = DBHelper.getConnection();
		Statement stmt = null;
		RegistrationDates rd = new RegistrationDates();
		try{
			stmt = con.createStatement();
			String selectDatesQuery = "SELECT start_date, end_date, reg_open, " +
					"reg_close, drop_date, cat_id FROM dates WHERE cat_id = " + catId;
			
			ResultSet datesRS = stmt.executeQuery(selectDatesQuery);
			
			while(datesRS.next()){
				rd.setStartDate(datesRS.getDate("start_date"));
				rd.setEndDate(datesRS.getDate("end_date"));
				rd.setFirstDayToRegister(datesRS.getDate("reg_open"));
				rd.setLastDayToRegiter(datesRS.getDate("reg_close"));
				rd.setLastDayToDropClasses(datesRS.getDate("drop_date"));
			}
			datesRS.close();
			return rd;
		}
		catch(SQLException ex) {
			System.err.println("CourseDAO Threw a SQLException retrieving Registration Dates");
			System.err.println(ex.getMessage());
		}
		finally {
			try{
				if(con != null)
					con.close();
				if(stmt != null)
					stmt.close();
			}
			catch(SQLException ex){
				System.err.println("CourseDAO Threw a SQLException retrieving Registration Dates");
				System.err.println(ex.getMessage());
			}
		}
		return null;
	}//end of FIND function
	
	//CREATE a registration dates
	public void insertRegistrationDates(RegistrationDates rd, int catId){
		Connection con = DBHelper.getConnection();
		PreparedStatement datePst = null;
		
		try{
			
			String addCourseToCourseTable = "INSERT INTO dates (start_date, end_date, reg_open, reg_close," +
				" drop_date, cat_id) VALUES (?, ?, ?, ?, ?, ?)";
			datePst = con.prepareStatement(addCourseToCourseTable);
			datePst.setDate(1, rd.getStartDate());
			datePst.setDate(2, rd.getEndDate());
			datePst.setDate(3, rd.getFirstDayToRegister());
			datePst.setDate(4, rd.getLastDayToRegister());
			datePst.setDate(5, rd.getLastDayToDropClasses());
			datePst.setInt(6, catId);
			datePst.executeUpdate();
		}
		catch(SQLException ex) {
			System.err.println("CourseDAO Threw a SQLException INSERTING into dates!");
			System.err.println(ex.getMessage());
		}
		finally {
			try{
				if(datePst != null){
					datePst.close();
				}
				if(con != null){
					System.out.println("Connection INSERTING into dates");
					con.close();
				}
			}
			catch(SQLException ex){
				System.err.println("CourseDAO Threw a SQLException INSERTING into dates!");
				System.err.println(ex.getMessage());
			}
		}
	}//end of INSERT function
	
	//UPDATE a registration dates for a given catalog
	public void updateRegistrationDatesByCatId(RegistrationDates rd, int catId){
		
		Connection con = DBHelper.getConnection();
		Statement stmt = null;
		
		try{
			stmt = con.createStatement();
			
			String updateCourseQuery = "UPDATE dates SET start_date = '" + rd.getStartDate() + 
					"', end_date = '" + rd.getEndDate() +
					"', reg_open = '" + rd.getFirstDayToRegister() +
					"', reg_close = '" + rd.getLastDayToRegister() +
					"', drop_date = '" + rd.getLastDayToDropClasses() +
					"' WHERE cat_id = " + catId;
		
		stmt.executeUpdate(updateCourseQuery);
			
		} catch(SQLException ex){
			System.err.println("CourseDAO: Threw a SQLException while updating dates object");
			System.err.println(ex.getMessage());
			ex.printStackTrace();
		}finally{
			try{
				if (stmt != null)
					stmt.close();
				if(con != null){
					System.out.println("Database closed after updating dates object");
					con.close();
				}
			}catch(SQLException ex){
				System.err.println("CourseDAO: Threw a SQLException while updating dates object");
				System.err.println(ex.getMessage());
				ex.printStackTrace();
			}
		}	
	}//end of UPDATE function

	public boolean datesAreSet(int catId){

		Statement stmt = null;
		Connection con = DBHelper.getConnection();
		
		try{			
			//1st. create Professor obj based on student table
			stmt = con.createStatement();
			String checkDates = "SELECT cat_id FROM dates WHERE EXISTS (SELECT * FROM dates" +
				" WHERE cat_id = " + catId + ")";
			ResultSet datesRS = stmt.executeQuery(checkDates);

			while(datesRS.next()){
				datesRS.close();
				stmt.close();
				con.close();
				return true;
			}
			datesRS.close();
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
	}
	
}//end of main class
