package com.universityregistration.dal;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Date;

import com.universityregistration.model.BillReport.BillReport;
import com.universityregistration.model.Course.Course;
import com.universityregistration.model.Course.CourseCatalog;
import com.universityregistration.model.Course.CourseSection;
import com.universityregistration.model.RegDates.RegistrationDates;
import com.universityregistration.model.Service.CourseService;
import com.universityregistration.model.User.Address;
import com.universityregistration.model.User.Email;
import com.universityregistration.model.User.PhoneNumber;
import com.universityregistration.model.User.Professor;
import com.universityregistration.model.User.Student;

public class ProfessorDAO {
	
	private CourseDAO courseDAO = new CourseDAO();
	private PersonDAO personDAO = new PersonDAO();
	private DatesDAO dateDAO = new DatesDAO();

	public void registerProfessorToCourse(Professor professor, Course course, CourseCatalog catalog, String location, String day, String time){
		
		//insert a reference of this course into the course_section table
		Connection conn = DBHelper.getConnection();
		PreparedStatement profToCoursePst = null;
		
		RegistrationDates dates = dateDAO.getRegistrationDatesByCatId(catalog.getCatId());
		System.out.println("start date: " + dates.getStartDate());
		System.out.println("end date: " + dates.getStartDate());
		
		try{
			//Inserting a reference to the course in the course-section table
			String profToCourseStmt = 
					"INSERT INTO course_section (course_id, instructor_ssn, status, location, start_date, end_date, day, time) VALUES (?,?,?,?,?,?,?,?)";
			profToCoursePst = conn.prepareStatement(profToCourseStmt);
			profToCoursePst.setString(1, course.getCourseId());
			profToCoursePst.setString(2, professor.getSsn());
			profToCoursePst.setString(3, "open");
			profToCoursePst.setString(4, location);
			profToCoursePst.setDate(5, dates.getStartDate());
			profToCoursePst.setDate(6, dates.getEndDate());
			profToCoursePst.setString(7, day);
			profToCoursePst.setString(8, time);
			profToCoursePst.executeUpdate();
			
		}catch(SQLException ex){
			System.err.println("ProfessorDAO: Threw a SQLException Trying Assign Professor to Course.");
			System.out.println(ex.getMessage());			
		} finally{
			try{
				if (profToCoursePst != null)
					profToCoursePst.close();
				if (conn != null){
					System.out.println("database closed after professor assigned");
					conn.close();
				}
			} catch (SQLException ex){
				System.err.println("ProfessorDAO: Threw a SQLException Trying Assign Professor to Course.");
				System.out.println(ex.getMessage());
			}
		}
		
		//update is_teaching		
		CourseDAO courseDAO = new CourseDAO();
		courseDAO.insertIsTeaching(professor, course);
		
		//Create courseDAO obj
		//update the catalog_contains tables to account for a new course section
		courseDAO.updateCatalogAfterAddition(course, professor, catalog);
	}	
	
	
	//unregistering a professor from a course section - will set professor to staff
	public void unregisterProfessorFromCourse(Professor professor, CourseSection course){
		
		//Prior to removing course_section, create an announcement record for reference
		courseDAO.createAnnouncement(professor, course);
		
		CourseService helper = new CourseService();
		String term = helper.getTermForCourseSection(course);
		int year = helper.getYearForGivenCourseSection(course);
				
				
		//Also get list of students currently enrolled in the course to be canceled. Will use this list to update bill reports
		List<Student> origRoster = course.getRoster();		
		
		//removes course section from professor's teaching list (course_section table)
		removeProfessorFromCourseTeaching(professor, course); // HELPER method
		
		//After removal was completely successful, update bill reports for students no longer are enrolled in the course.
		for (Student s: origRoster){
			System.out.println("Trying to update student bill reports...");
			//Get updated versions of each student previously enrolled in the deleted course
			StudentDAO studentDAO = new StudentDAO();
			Student newStudVersion = studentDAO.getStudentById(s.getID());
			
			//Update each student's bill report
			BillReportDAO bpdao = new BillReportDAO();
			bpdao.updateBillReport(newStudVersion, term, year);
		}
	}

	//Method used to remove/unassign professor to course - the course will now be assigned to STAFF
	public void removeProfessorFromCourseTeaching(Professor professor, CourseSection course){
		Connection con = DBHelper.getConnection();
		Statement stmt = null;
		try{
			stmt = con.createStatement();
			
			String updateCourseQuery = "UPDATE course_section SET instructor_ssn = 'staff'" + 
					" WHERE course_section_id = " + course.getCourseSectionId() + " AND " +
					" instructor_ssn = '" + professor.getID() +"'";
			
			stmt.executeUpdate(updateCourseQuery);
			
			//close
			stmt.close();
			
			//update is_teaching tbl - with "staff" for is_teaching
			courseDAO.updateTeacherInIsTeachingTable("staff", course.getCourseSectionId());
			
		} 		catch(SQLException ex) {
			System.err.println("ProfessorDAO Threw a SQLException UPDATING CS teacher to STAFF");
			System.err.println(ex.getMessage());
		}
		finally {
			try{
				if(stmt != null){
					stmt.close();
				}
				if(con != null){
					con.close();
				}
			}
			catch(SQLException ex){
				System.err.println("ProfessorDAO Threw a SQLException UPDATING CS teacher to STAFF");
				System.err.println(ex.getMessage());
			}
		}		
	}	
	
	//unregister professor from a course
	public void removeCourseTeaching(Professor professor, CourseSection course){
		//remove from the course_section that references this professor
		Connection conn = DBHelper.getConnection();
		PreparedStatement profDeletePst = null;
		
		try{
			//register student for course;
			String registerStudentToCourseQuery = "DELETE FROM course_section WHERE instructor_ssn = " + professor.getSsn() +
					" AND course_section_id = " + course.getCourseSectionId();
			profDeletePst = conn.prepareStatement(registerStudentToCourseQuery);
			profDeletePst.executeUpdate();
		} catch(SQLException ex){
			System.err.println("ProfessorDAO: Threw a SQLException while modifying professor object.");
			System.out.println(ex.getMessage());
			
		} finally{
			try{
				if (profDeletePst != null)
					profDeletePst.close();
				if (conn != null){
					System.out.println("connection closed after unassigning professor from course");
					conn.close();
				}
			} catch (SQLException ex){
				System.err.println("ProfessorDAO: Threw a SQLException while modifying professor object.");
				System.out.println(ex.getMessage());
			}
		}		
		
	}
	
	//************************ ADMINISTRATIVE METHOD *************************************
	//CREATE New Professor IN Professor TABLE ---------------- C
	public void insertNewProfessor(Professor professor){
		
			Connection con = DBHelper.getConnection();
	        PreparedStatement profPst = null;

	        try {
	        	//Insert the customer object
	            String insertProfStm = "INSERT INTO Professor(uid, first_name, last_name, dob, ssn, department )" +
	            		" VALUES(?, ?, ?, ?, ?, ?)";
	            profPst = con.prepareStatement(insertProfStm);
	            profPst.setString(1, professor.getSsn());
	            profPst.setString(2, professor.getFirstName());
	            profPst.setString(3, professor.getLastName());       
	            profPst.setString(4, professor.getDob());
	            profPst.setString(5, professor.getSsn());
	            profPst.setString(6, professor.getDepartment());
	            profPst.executeUpdate();
	            
	            // Close to manage
            	profPst.close();
            	con.close();
            	
	            //Obtain the ID for the Professor just inserted above
	            String pId = professor.getSsn();
	            
		        //INSERT person Addresses INTO Address TABLE
		        for(Address a : professor.getAddress())
		        	personDAO.insertPersonAddresses(pId, a);
		
		        //INSERT person PhoneNumbers INTO Phone TABLE
            	for(PhoneNumber p : professor.getPhoneNumber())
            		personDAO.insertPersonPhonenumbers(pId, p);
		            		
            	//INSERT person EmailAddresses INTO Email TABLE
            	for(Email e : professor.getEmail())
            		personDAO.insertPersonEmails(pId, e);
		            		
	        }catch (SQLException ex) {
	      	      System.err.println("ProfessorDAO: Threw a SQLException saving the professor object.");
	    	      System.err.println(ex.getMessage());
	        
	        }finally {

	            try {
	                if (profPst != null) {
	                	profPst.close();
	                }
	                if (con != null) {
	                	System.out.println("Database closed after saving profesor object");
	                    con.close();
	                }
	            } catch (SQLException ex) {
	      	      System.err.println("Professor DAO: Threw a SQLException saving the professor object.");
	    	      System.err.println(ex.getMessage());
	            }
	        }
	    }
	//**************************************************************************************
	
	//Retrieve professor given an ID ---- R
	public Professor getProfessorById(String professorId){
		
		Connection con = DBHelper.getConnection();
		Statement stmt = null;
		
		try{
			//Get Professor
			
			//1st. create Professor obj based on student table
			stmt = con.createStatement();
			String selectProfessorQuery = "SELECT uId, first_name, last_name, dob, ssn, department FROM professor WHERE uId = '"
					+ professorId + "'";
			ResultSet profRS = stmt.executeQuery(selectProfessorQuery);
			
			//Get Professor
			Professor professor = new Professor();
			while(profRS.next()){
				professor.setID(profRS.getString("uId"));
				professor.setFirstName(profRS.getString("first_name"));
				professor.setLastName(profRS.getString("last_name"));
				professor.setDob(profRS.getString("dob"));
				professor.setSsn(profRS.getString("ssn"));
				professor.setDepartment(profRS.getString("department"));				
			}
			
			//close to manage resources
			profRS.close();			
			
			String type = "staff";
			
			//Get addresses---------------------------
			String selectAddressesQuery = "SELECT street, city, state, zip, address_id FROM Address WHERE student_id = '" + professorId + "'" +
					" AND type = '" + type + "'";
			ResultSet addRS =  stmt.executeQuery(selectAddressesQuery);
			List<Address> professorAddresses = new ArrayList<Address>(); //to store addresses for student
			
			//Iterate through result set and create address objects for each row returned
			while(addRS.next()){
				Address address = new Address();
				address.setStreet(addRS.getString("street"));
				address.setCity(addRS.getString("city"));
				address.setState(addRS.getString("state"));
				address.setZip(addRS.getString("zip"));
				address.setAddressId(addRS.getInt("address_id"));
				
				//add to professor's Address list
				professorAddresses.add(address);
				
			}
			addRS.close();
			//End of getting addresses ------------------------
			
			//Get phone ------------------------------
			String selectPhoneQuery = "SELECT phone_id, phone_number FROM phone WHERE student_id = '" + professorId +"'" +
					" AND type = '" + type + "'";
			ResultSet phoneRS = stmt.executeQuery(selectPhoneQuery);
			List<PhoneNumber> professorPhoneNos = new ArrayList<PhoneNumber>(); // to store phone numbers for professor
			
			//Iterate through result set and create phone objects for each row returned
			while(phoneRS.next()){
				PhoneNumber phone = new PhoneNumber();
				phone.setPhoneNumberId(phoneRS.getInt("phone_id"));
				phone.setPhonenumber(phoneRS.getString("phone_number"));
				
				//add to professor's phonenumber list
				professorPhoneNos.add(phone);
			}
			
			phoneRS.close();
			//End of getting phone-numbers ---------------------------------
			
			//Get emails -----------------------------------------
			String selectEmailQuery = "SELECT email_id, email FROM email WHERE student_id = '" + professorId + "'" +
					" AND type = '" + type + "'";
			ResultSet emailRS = stmt.executeQuery(selectEmailQuery);
			List<Email> professorEmails = new ArrayList<Email>();
			
			//Iterate through the result set and create Email objects for each row returned
			while(emailRS.next()){
				Email email = new Email();
				email.setEmailId(emailRS.getInt("email_id"));
				email.setEmail(emailRS.getString("email"));
				
				//add to professor's email list
				professorEmails.add(email);
			}
			emailRS.close();
			//End of getting emails ---------------
			

			//Get CourseTeaching courses for professor
			List<CourseSection> isTeaching = new ArrayList<CourseSection>();
			String selectCourseSectionId = "SELECT course_section_id FROM is_teaching " +
					"WHERE professor_id = '" + professorId +"'";
			ResultSet courseSectionRS = stmt.executeQuery(selectCourseSectionId);
			
			while(courseSectionRS.next()){
				CourseSection cs =  courseDAO.getCourseSectionById(courseSectionRS.getInt("course_section_id"));
				isTeaching.add(cs);
			}
			
			courseSectionRS.close();
			
			//Set professor's addresses, phone numbers, and email, courseTeaching attributes
			professor.setAddress(professorAddresses);
			professor.setPhoneNumber(professorPhoneNos);
			professor.setEmail(professorEmails);
			professor.setCoursesTeaching(isTeaching);
			
			return professor;
		}
		catch (SQLException ex){
			System.err.println("ProfessorDAO: Threw a SQLException retrieving the professor object.");
			System.err.println(ex.getMessage());
			ex.printStackTrace();
		}finally{
			try{
				if (stmt != null) {
					stmt.close();
				}
				if (con != null) {
					System.out.println("Database closed after saving profesor object");
					con.close();
				}
			}catch(SQLException ex){
				System.err.println("ProfessorDAO: Threw a SQLException retrieving the professor object.");
				System.err.println(ex.getMessage());
				ex.printStackTrace();
			}
        }
		return null;		
	}//end of getProfessorById
	

	
	public List<Professor> getAllProfessors(){
		
		Connection conn = DBHelper.getConnection();
		Statement stmt = null;
		try{
			stmt = conn.createStatement();
			String getAllProfessorsQuery = "SELECT uid FROM professor";
			ResultSet cRS = stmt.executeQuery(getAllProfessorsQuery);
			
			List<Professor> allProfessors = new ArrayList<Professor>();
			while(cRS.next()){
				
				Professor p = getProfessorById(cRS.getString("uid"));
				allProfessors.add(p);
			}
			
			cRS.close();
			
			return allProfessors;
			
		}catch(SQLException ex){
			System.err.println("SQLException Thrown: Error when trying to get all course Ids");
			System.err.println(ex.getMessage());
		}finally{
			try{
				if (stmt != null)
					stmt.close();
				if (conn != null)
					conn.close();
			}
			catch(SQLException ex){
				System.err.println("SQLException Thrown: Error when trying to get all course Ids");
				System.err.println(ex.getMessage());				
			}
		}
		return null;
	}
	
	

	/*****************************************************************************/
	/*************************** 12.2.2013 UPDATE *******************************/
	/*****************************************************************************/
	public List<Professor> searchProfessors (String filter){

		String filt = "%" + filter + "%";
		Connection conn = DBHelper.getConnection();
		Statement stmt = null;
		try{
			stmt = conn.createStatement();
			String filterQuery = "SELECT uid, first_name, last_name, dob, ssn, department FROM professor WHERE" 
			+ " last_name LIKE '" + filt + "'";
			ResultSet filterRS = stmt.executeQuery(filterQuery);
			List<Professor> retProfessorList = new ArrayList<Professor>();
			while(filterRS.next()){
				Professor p = new Professor();
				p.setID(filterRS.getString("uId"));
				p.setFirstName(filterRS.getString("first_name"));
				p.setLastName(filterRS.getString("last_name"));
				p.setDob(filterRS.getString("dob"));
				p.setSsn(filterRS.getString("ssn"));
				p.setDepartment(filterRS.getString("department"));
				retProfessorList.add(p);
			}
			filterRS.close();
			return retProfessorList;
		}catch (SQLException ex){
			System.err.println("StudentDAO: Threw a SQLException retrieving filtered professor list.");
			System.err.println(ex.getMessage());
			ex.printStackTrace();
		}finally{
			try{
				if(stmt != null)
				stmt.close();
				if(conn != null){
				conn.close();
				}
			}catch (SQLException ex){
				System.err.println("StudentDAO: Threw a SQLException retrieving filtered professor list.");
				System.err.println(ex.getMessage());
				ex.printStackTrace();
			}
		}
		return null;
	}
	
	 /****************************** 12.03.2013 ************************************/
	public boolean professorExists(String sid){
		Connection conn = DBHelper.getConnection();
		Statement stmt = null;
		boolean res = false;
	
		try{
			stmt = conn.createStatement();
			String exists = "SELECT uid FROM professor WHERE uid = '" 
					+ sid + "'";
			ResultSet existsRS = stmt.executeQuery(exists);
			
			if(existsRS.next()){
				res= true;
			}
			
			existsRS.close();
			stmt.close();
			conn.close();
			
		}catch(SQLException ex){
			System.err.println("SQLException:Threw exception while checking professor existence");
			System.err.println(ex.getMessage());
		}
		finally{
			try{
				if (stmt != null)
					stmt.close();
				if (conn != null)
					conn.close();
			}catch(SQLException ex){
				System.err.println("SQLException:Threw exception while checking professor existence");
				System.err.println(ex.getMessage());
			}
		}
		return res;
	}

}