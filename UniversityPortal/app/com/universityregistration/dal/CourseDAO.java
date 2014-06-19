package com.universityregistration.dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.universityregistration.model.Course.*;
import com.universityregistration.model.RegDates.RegistrationDates;
import com.universityregistration.model.User.Professor;
import com.universityregistration.model.User.Student;

public class CourseDAO {
	
	DatesDAO dDAO = new DatesDAO();
	
	//remove course from catalog -- modifys catalog_contains table after deletion
	public void removeCourseFromCatalog(CourseSection course, CourseCatalog catalog){
		
		Connection con = DBHelper.getConnection();
		PreparedStatement courseDel = null;
		
		try{
			
			String deleteCourseFromCatQuery = "DELETE FROM catalog_contains WHERE course_section_id = " +
						 course.getCourseSectionId() + " AND catalog_id = " + catalog.getCatId();
			courseDel = con.prepareStatement(deleteCourseFromCatQuery);
			courseDel.executeUpdate();
			
			//close
			courseDel.close(); //added 12/1/2013
		}
		catch(SQLException ex) {
			System.err.println("CourseDAO Threw a SQLException saving the course object");
			System.err.println(ex.getMessage());
		}
		finally {
			try{
				if(courseDel != null){
					System.out.println("Database closed after inserting course");
					courseDel.close();
				}
				if(con != null){
					con.close();
				}
			}
			catch(SQLException ex){
				System.err.println("CourseDAO Threw a SQLException saving the course object");
				System.err.println(ex.getMessage());
			}
		}
		
	}

	//returns a corresponding course for the given course_section
	public Course getCourseByCourseSection(CourseSection cs){
		
		Connection con = DBHelper.getConnection();
		
		try{
			Statement stmt = con.createStatement();
			String getCourseIdQuery = 
					"SELECT course_id FROM course_section WHERE course_section_id = "
					+cs.getCourseSectionId();
			
			ResultSet cidRS = stmt.executeQuery(getCourseIdQuery);
			
			String cid = "";
			while(cidRS.next()){
				cid = cidRS.getString("course_id");
			}
			cidRS.close();
			
			System.out.println("--------------->" + cid);
			//create course object
			Course retCourse = getSimpleCourseById(cid);
			
			return retCourse;
		}
		catch(SQLException e){
			System.err.println("CourseDAO: Threw Exception trying to retrieve course");
			System.err.println(e.getMessage());
			e.printStackTrace();
		}finally{
			try {
				if(con != null){
					System.out.println("Database closed after retrieving course");
					con.close();
				}
				
			  } catch (SQLException e) {
					System.err.println("CourseDAO: Threw Exception trying to retrieve course");
					System.err.println(e.getMessage());
					e.printStackTrace();
				}
		}
		
		return null;
		
	}	
	
	//update the catalog_contains tables to account for a new course section
	public void updateCatalogAfterAddition(Course course, Professor professor, CourseCatalog catalog){
		Connection conn = DBHelper.getConnection();
		PreparedStatement catPst = null;
		Statement stmt = null;
		
		//update the catalog_contains tables to account for the new course section
				try{
					//get course_section
					stmt = conn.createStatement();
					String selectCourseSection = "SELECT course_section_id FROM course_section WHERE course_id = '" +
							course.getCourseId() + "' AND instructor_ssn = '" + professor.getSsn() + "'";
					ResultSet csRS = stmt.executeQuery(selectCourseSection);
					int cid = 0;
					while(csRS.next()){
						cid = csRS.getInt("course_section_id");
					}

					csRS.close();
					String insertIntoCatQuery = "INSERT INTO catalog_contains (course_section_id, catalog_id) VALUES (?, ?)";
					catPst = conn.prepareStatement(insertIntoCatQuery);
					catPst.setInt(1, cid);
					catPst.setInt(2, catalog.getCatId());
					catPst.executeUpdate();
					
				}catch(SQLException ex){
					System.err.println("CourseDAO: Threw a SQLException saving the professor object.");
					System.out.println(ex.getMessage());					
				} finally{
					try{
						if (catPst != null)
							catPst.close();
						if (stmt != null)
							stmt.close();
						if (conn != null){
							System.out.println("Database closed after saving professor object");
							conn.close();
						}
					} catch (SQLException ex){
						System.err.println("ProfessorDAO: Threw a SQLException saving the professor object.");
						System.out.println(ex.getMessage());
					}
				}	
	}

	
	/*********************** Course CRUD METHODS *****************************************************/
	
	//CREATE a new course by inserting a given course into course table .......... C
	public void insertCourseOffering(Course course){
		Connection con = DBHelper.getConnection();
		PreparedStatement coursePst = null;
		PreparedStatement preReqPst = null;
		
		try{
			
			String addCourseToCourseTable = "INSERT INTO course (course_id, course_name, description, credits) VALUES (?, ?, ?, ?)";
			coursePst = con.prepareStatement(addCourseToCourseTable);
			coursePst.setString(1, course.getCourseId());
			coursePst.setString(2, course.getCourseName());
			coursePst.setString(3, course.getCourseDescription());
			coursePst.setInt(4, course.getCourseCredits());
			coursePst.executeUpdate();
			
			//close
			coursePst.close();
			
			for(Course c : course.getPrereqs()){
				String addToPreReqTable = "INSERT INTO prerequisite (course_id, prerequisite_id) VALUES(?,?)";
				preReqPst = con.prepareStatement(addToPreReqTable);
				preReqPst.setString(1, course.getCourseId());
				preReqPst.setString(2, c.getCourseId());
				preReqPst.executeUpdate();
			}
			
			preReqPst.close();
		}
		catch(SQLException ex) {
			System.err.println("CourseDAO Threw a SQLException saving the course object");
			System.err.println(ex.getMessage());
		}
		finally {
			try{
				if(coursePst != null){
					coursePst.close();
				}
				if(preReqPst != null){
					preReqPst.close();
				}
				if(con != null){
					System.out.println("Connection closed after creating course");
					con.close();
				}
			}
			catch(SQLException ex){
				System.err.println("CourseDAO Threw a SQLException saving the course object");
				System.err.println(ex.getMessage());
			}
		}
	}
	
	//METHOD RETRIEVES a COURSE by ID............................................. R
	public Course getCourseById(String cId){
		
		Connection con = DBHelper.getConnection();
		Statement stmt = null;
		
		try{
			//get course
			stmt = con.createStatement();
			String selectCourseQuery = 
			"SELECT course_id, course_name, description, credits FROM course WHERE course_id = '" +
						cId + "'";
			ResultSet courseRS = stmt.executeQuery(selectCourseQuery);
			
			//Get course
			Course retCourse = new Course();
			
			while(courseRS.next()){
				//set course id, status, location, start-date, end-date, and professor
				retCourse.setCourseId(courseRS.getString("course_id"));
				retCourse.setCourseName(courseRS.getString("course_name"));
				retCourse.setCourseDescription(courseRS.getString("description"));
				retCourse.setCourseCredits(courseRS.getInt("credits"));				
			}
			
			//close to manage resources
			courseRS.close();
			
			
			//Retrieve the course's prerequisites
			String rosterQuery = "SELECT prerequisite_id FROM prerequisite WHERE course_id = '" + cId + "'";
			ResultSet prereqRS = stmt.executeQuery(rosterQuery);
			
			//Get prereqs and put them in prereqCourses
			List<Course> prereqCourses = new ArrayList<Course>();
			while(prereqRS.next()){
				Course c = getCourseById(prereqRS.getString("prerequisite_id"));
				prereqCourses.add(c);
			}
			
			retCourse.setPrereqs(prereqCourses);		
			
			
			//close to manage resources			
			prereqRS.close();
			
			return retCourse;
		}
		catch(SQLException ex){
			System.err.println("CourseDAO: Threw a SQLException retrieving the course-section object");
			System.err.println(ex.getMessage());
			ex.printStackTrace();
		}finally{
			try{
				if (stmt != null)
					stmt.close();
				if (con != null){
					System.out.println("Database closed after retrieving course section");
					con.close();
				}
			}catch(SQLException ex){
				System.err.println("CourseDAO: Threw a SQLException retrieving the course-section object");
				System.err.println(ex.getMessage());
				ex.printStackTrace();
				
			}
		}
		
		return null;
	}
	
	
	//Retrieves a SIMPLE course object
	public Course getSimpleCourseById(String cId){
		
		Connection con = DBHelper.getConnection();
		Statement stmt = null;
		
		try{
			//get course
			stmt = con.createStatement();
			String selectCourseQuery = 
			"SELECT course_id, course_name, description, credits FROM course WHERE course_id = '" +
						cId + "'";
			ResultSet courseRS = stmt.executeQuery(selectCourseQuery);
			
			//Get course
			Course retCourse = new Course();
			
			while(courseRS.next()){
				//set course id, status, location, start-date, end-date, and professor
				retCourse.setCourseId(courseRS.getString("course_id"));
				retCourse.setCourseName(courseRS.getString("course_name"));
				retCourse.setCourseDescription(courseRS.getString("description"));
				retCourse.setCourseCredits(courseRS.getInt("credits"));				
			}
			
			//close to manage resources
			courseRS.close();
			
			return retCourse;
		}
		catch(SQLException ex){
			System.err.println("CourseDAO: Threw a SQLException retrieving SIMPLE course object");
			System.err.println(ex.getMessage());
			ex.printStackTrace();
		}finally{
			try{
				if (stmt != null)
					stmt.close();
				if (con != null){
					System.out.println("Database closed after retrieving SIMPLE Course section");
					con.close();
				}
			}catch(SQLException ex){
				System.err.println("CourseDAO: Threw a SQLException retrieving SIMPLE Course-section object");
				System.err.println(ex.getMessage());
				ex.printStackTrace();
				
			}
		}
		
		return null;
	}
	
	//METHOD UPDATES A COURSE IN THE COURSE TABLE ................................ U
	public void updateCourseOffering(Course course){
		
		Connection con = DBHelper.getConnection();
		Statement stmt = null;
		
		try{
			stmt = con.createStatement();
			
			String updateCourseQuery = "UPDATE course SET course_name = '" + course.getCourseName() 
					+"', description = '" + course.getCourseDescription() 
					+ "', credits =" + course.getCourseCredits()
					+ " WHERE course_id = '" + course.getCourseId() +"'";
			
			stmt.executeUpdate(updateCourseQuery);
			
		} catch(SQLException ex){
			System.err.println("CourseDAO: Threw a SQLException while updating course object");
			System.err.println(ex.getMessage());
			ex.printStackTrace();
		}finally{
			try{
				if (stmt != null)
					stmt.close();
				if(con != null){
					System.out.println("Database closed after updating course object");
					con.close();
				}
			}catch(SQLException ex){
				System.err.println("CourseDAO: Threw a SQLException while updating course object");
				System.err.println(ex.getMessage());
				ex.printStackTrace();
			}
	}	
		}
	
	//METHOD REMOVES A COURSE OFFERING FROM THE COURSE TABL....................... D
	public void deleteCourseOffering(Course course){
		Connection con = DBHelper.getConnection();
		PreparedStatement courseDel = null;
		
		try{
			
			String deleteCourseFromCourseTable = "DELETE FROM course WHERE course_id = '" +
						course.getCourseId() + "'";
			courseDel = con.prepareStatement(deleteCourseFromCourseTable);
			courseDel.executeUpdate();
			
			//close
		}
		catch(SQLException ex) {
		}
		finally {
			try{
				if(courseDel != null){
					courseDel.close();
				}
				if(con != null){
					System.out.println("database closed after course was deleted from course table");
					con.close();
				}
			}
			catch(SQLException ex){
				System.err.println("CourseDAO Threw a SQLException deleting the course object");
				System.err.println(ex.getMessage());
			}
		}
	}
	
	/***END***********************************************************************************************/
		
	
	/************************ CourseSection CRUD METHODS  (NO DELETE METHOD) ******************************
	 * 1. CREATE (C) - done with assignProfessorToCourseSection() : CourseSection created when Prof Assign.
	 * 2. RETRIEVE (R) - getCourseSectionById()
	 * 3. UPDATE (U) - updateCourseSection()
	 * ***************************************************************************************************/
	//UPDATE a CourseSection -- U
	public void updateCourseSection(CourseSection cs){
		
		Connection conn = DBHelper.getConnection();
		Statement stmt = null;
		String updateQuery = "UPDATE course_section SET instructor_ssn = '"
				+ cs.getInstructorSsn() + "', status ='"
				+ cs.getStatus() + "', location = '"
				+ cs.getLocation() + "', day = '"
				+ cs.getDay() + "', time = '"
				+ cs.getTime() + "' WHERE course_section_id = " 
				+ cs.getCourseSectionId();
		
		try{
			stmt = conn.createStatement();
			stmt.executeUpdate(updateQuery);
		}catch(SQLException e){
			System.out.println(e.getMessage());
			
		}finally{
			try{
				if(stmt != null)
					stmt.close();
				if(conn != null){
					System.out.println("Connection closed after course update");
					conn.close();
				}
			}catch(SQLException e){
				System.err.println("UPDATING course section: Threw SQL Exception");
				System.err.println(e.getMessage());
			}
		}
	}
	
	//RETRIEVE a CourseSection -- R
	public CourseSection getCourseSectionById(int courseId){
		Connection con = DBHelper.getConnection();
		Statement stmt = null;
		try{
			//get course
			stmt = con.createStatement();
			String selectCourseQuery = 
			"SELECT course_section_id, instructor_ssn, status, location, day, time FROM course_section WHERE course_section_id = " +
						courseId;
			ResultSet courseRS = stmt.executeQuery(selectCourseQuery);
			
			//Get course
			CourseSection coursesection = new CourseSection();
			
			while(courseRS.next()){
				//set course id, status, location, start-date, end-date, and professor
				coursesection.setCourseSectionId(courseRS.getInt("course_section_id"));
				coursesection.setInstructorSsn(courseRS.getString("instructor_ssn"));
				coursesection.setStatus(courseRS.getString("status"));
				coursesection.setLocation(courseRS.getString("location"));
				coursesection.setDay(courseRS.getString("day"));
				coursesection.setTime(courseRS.getString("time"));
				 
			}
			//close to manage 
			courseRS.close();
			
			//Retrieve course roster
			//1 ) get students taking the course via their ids
			String rosterQuery = "SELECT student_id FROM is_taking WHERE course_section_id = " + courseId;
			ResultSet rosterRS = stmt.executeQuery(rosterQuery);
			System.out.println("CourseDAO: ********* Query " + rosterQuery);
			
			//Get roster
			List<Student> roster = new ArrayList<Student>();
			StudentDAO studentDAO = new StudentDAO();
			while(rosterRS.next()){
				Student s = studentDAO.getSimpleStudentById(rosterRS.getString("student_id"));
				roster.add(s);
			}
			
			coursesection.setRoster(roster);		
						
			//close to manage resources
			rosterRS.close();
			
			return coursesection;
		}
		catch(SQLException ex){
			System.err.println("CourseDAO: Threw a SQLException retrieving the cours-section object");
			System.err.println(ex.getMessage());
			ex.printStackTrace();
		}finally{
			try{
				if(stmt!=null)
					stmt.close();
				if(con != null){
					System.out.println("Database closed after retrieving course-section");
					con.close();
				}
			}catch(SQLException ex){
				System.err.println("CourseDAO: Threw a SQLException retrieving the cours-section object");
				System.err.println(ex.getMessage());
				ex.printStackTrace();
				
			}
		}
		
		return null;
	}	
	
	/***END***********************************************************************************************/
	
	
	//UPDATING is_teaching
	public void insertIsTeaching(Professor professor, Course course){
		Connection conn = DBHelper.getConnection();
		PreparedStatement catPst = null;
		Statement stmt = null;
		
				try{
					//get course_section
					stmt = conn.createStatement();
					String selectCourseSection = "SELECT course_section_id FROM course_section WHERE course_id = '"
							+ course.getCourseId() + "' AND instructor_ssn = '" + professor.getSsn() + "'";
					ResultSet csRS = stmt.executeQuery(selectCourseSection);
					int csid = 0;
					while(csRS.next()){
						csid = csRS.getInt("course_section_id");
					}
					//System.out.println(cid);					
					
					//close to save resources
					csRS.close();
					stmt.close();
					
					String insertIntoIsTeachingQuery = "INSERT INTO is_teaching (professor_id, course_section_id) VALUES (?, ?)";
					catPst = conn.prepareStatement(insertIntoIsTeachingQuery);
					catPst.setString(1, professor.getID());
					catPst.setInt(2, csid);
					catPst.executeUpdate();
					
					catPst.close();
					
				}catch(SQLException ex){
					System.err.println("ProfessorDAO: Threw a SQLException saving the professor object.");
					System.out.println(ex.getMessage());					
				} finally{
					try{
						if (catPst != null)
							catPst.close();
						if (stmt != null)
							stmt.close();
						if (conn != null){
							System.out.println("Database connection closed after saving professor object");
							conn.close();
						}
					} catch (SQLException ex){
						System.err.println("ProfessorDAO: Threw a SQLException saving the professor object.");
						System.out.println(ex.getMessage());
					}
				}	
	}
	
	
	
	
	
	/************************** Course_Catalog CRUD METHODS (ONLY Create)*********************************/
	//CREATE a catalog
	public void insertCatalog(CourseCatalog catalog){
		Connection con = DBHelper.getConnection();
		PreparedStatement catPst = null;
		
		try{
			
			String insertCatalog = "INSERT INTO course_catalog (term, year) VALUES (?, ?)";
			catPst = con.prepareStatement(insertCatalog);
			catPst.setString(1, catalog.getTerm());
			catPst.setInt(2, catalog.getYear());
			catPst.executeUpdate();
			
			//close
			catPst.close();
		}
		catch(SQLException ex) {
			System.err.println("CourseDAO Threw a SQLException saving the course object");
			System.err.println(ex.getMessage());		
		}
		finally {
			try{
				if(catPst != null){
					catPst.close();
				}
				if(con != null){
					System.out.println("Connection closed after inserting course into catalog");
					con.close();
				}
			}
			catch(SQLException ex){
				System.err.println("CourseDAO Threw a SQLException saving the course object");
				System.err.println(ex.getMessage());
			}
		}
	}
	
	public CourseCatalog getCourseCatalogById(int catalogId){
		
		Connection con = DBHelper.getConnection();
		Statement stmt = null;
		
		try{
			stmt = con.createStatement();
			String selectCatalogQuery = "SELECT catalog_id, term, year FROM course_catalog " +
					"WHERE catalog_id = " + catalogId;
			ResultSet catalogRS = stmt.executeQuery(selectCatalogQuery);
			
			CourseCatalog retCatalog = new CourseCatalog();
			
			while(catalogRS.next()){
				retCatalog.setCatId(catalogRS.getInt("catalog_id"));
				retCatalog.setTerm(catalogRS.getString("term"));
				retCatalog.setYear(catalogRS.getInt("year"));
			}
			
			//close to save
			catalogRS.close();
			
		//GET all course sections - List<CourseSection> related to catalog
		String selectCourseSectionFromCatalogTbl = "SELECT course_section_id FROM catalog_contains " +
			"WHERE catalog_id = " + catalogId;
		ResultSet courseSectionRS = stmt.executeQuery(selectCourseSectionFromCatalogTbl);
		List<CourseSection> catalogCourseSections = new ArrayList<CourseSection>();
		while(courseSectionRS.next()){
			CourseSection catCourseSection = getCourseSectionById(courseSectionRS.getInt("course_section_id"));
			catalogCourseSections.add(catCourseSection);
		}
			
		retCatalog.setCourseList(catalogCourseSections);
		
		//close to save
		courseSectionRS.close();
		return retCatalog;

	}
	catch(SQLException e){
		System.err.println("CourseDAO: Threw Exception trying to retrieve course");
		System.err.println(e.getMessage());
		e.printStackTrace();
	}finally{
		try{
			if(stmt != null)
				stmt.close();
			if(con != null){
				System.out.println("Database closed after retrieving course");
				con.close();
			}
		}catch(SQLException e){
			System.err.println("CourseDAO: Threw Exception trying to retrieve course");
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}
	return null;
}
	/***END***********************************************************************************************/

	
	//GET ALL COURES
	public List<Course> getAllCourses(){
		Connection conn = DBHelper.getConnection();
		Statement stmt = null;
		try{
			stmt = conn.createStatement();
			String getAllCoursesQuery = "SELECT course_id FROM course";
			ResultSet cRS = stmt.executeQuery(getAllCoursesQuery);
			
			List<Course> allCourses = new ArrayList<Course>();
			while(cRS.next()){
				allCourses.add(getCourseById(cRS.getString("course_id")));
			}
			
			//close to save resources
			cRS.close();
			
			return allCourses;
			
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
	
	
	
	
	//GET ALL CATALOGS
	public List<CourseCatalog> getAllCatalogs(){
			Connection conn = DBHelper.getConnection();
			Statement stmt = null;
			try{
				stmt = conn.createStatement();
				String getAllCoursesQuery = "SELECT catalog_id FROM course_catalog";
				ResultSet csRS = stmt.executeQuery(getAllCoursesQuery);
				
				List<CourseCatalog> allCourses = new ArrayList<CourseCatalog>();
				while(csRS.next()){
					allCourses.add(getCourseCatalogById(csRS.getInt("catalog_id")));
				}
				
				csRS.close();				
				return allCourses;
				
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
	
	
	
	public void createAnnouncement(Professor professor, CourseSection cs) {
		Connection con = DBHelper.getConnection();
		PreparedStatement announcementPst = null;
		
		try{
			
			Course canceledCourse = getCourseByCourseSection(cs);
			String comment = 
					"COURSE: " + canceledCourse.getCourseId() + " " + canceledCourse.getCourseName() 
					+ ", TAUGHT BY: " + professor.getFirstName() + " " + professor.getLastName() 
					+ ", MEETING ON: " + cs.getDay() + ", " + cs.getTime() + " HAS BEEN CANCELED!";
			String createCommentQuery = "INSERT INTO announcement (course_section_id, comment) VALUES (?, ?)";
			announcementPst = con.prepareStatement(createCommentQuery);
			announcementPst.setInt(1, cs.getCourseSectionId());
			announcementPst.setString(2, comment);
			announcementPst.executeUpdate();
			
			announcementPst.close();
			
		}catch(SQLException ex){
			System.err.println("CourseDAO: Threw Exception trying add announcement");
			System.err.println(ex.getMessage());
			ex.printStackTrace();
			
		}finally{
			try{
				if(announcementPst != null)
					announcementPst.close();
				if(con != null){
					System.out.println("Database closed after adding announcement.");
					con.close();
				}
			}catch(SQLException ex){
				System.err.println("CourseDAO: Threw Exception trying add announcement");
				System.err.println(ex.getMessage());
				ex.printStackTrace();
				
			}
		}
		
	}
	
	
	
	public List<CourseSection> allCourseSectionByCatId(int catId){
		
		List<CourseSection> courseSections = new ArrayList<CourseSection>();
		Statement stmt = null;
		Connection con = DBHelper.getConnection();
		
		try{
			stmt = con.createStatement();
			String selectCSQuery = "SELECT course_section_id FROM catalog_contains WHERE catalog_id = " +
					catId;
			ResultSet csRS = stmt.executeQuery(selectCSQuery);
			while(csRS.next()){
				CourseSection retCS = getCourseSectionById(csRS.getInt("course_section_id"));
				courseSections.add(retCS);
			}
				csRS.close();
				return courseSections;
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
		
		return null;
	}




	public void deleteCourseOfferingById(String course){
		Connection con = DBHelper.getConnection();
		PreparedStatement courseDel = null;
		
		try{
			
			String deleteCourseFromCourseTable = "DELETE FROM course WHERE course_id = '" +
						course + "'";
			courseDel = con.prepareStatement(deleteCourseFromCourseTable);
			courseDel.executeUpdate();
			
			//close
			courseDel.close();
		}
		catch(SQLException ex) {
			System.err.println("CourseDAO Threw a SQLException deleting the course object");
			System.err.println(ex.getMessage());
		}
		finally {
			try{
				if(courseDel != null){
					courseDel.close();
				}
				if(con != null){
					con.close();
				}
			}
			catch(SQLException ex){
				System.err.println("CourseDAO Threw a SQLException deleting the course object");
				System.err.println(ex.getMessage());
			}
		}
	}
	
	

	
	//helper method to above method - AFTER a change to course section professor is assigned
	public void updateTeacherInIsTeachingTable(String profId, int courseSecId){
			System.out.println("entering updating isTeaching Table function!!!");
			Connection conn = DBHelper.getConnection();
			Statement stmt = null;
			String updateQuery = "UPDATE is_teaching SET professor_id = '"
					+ profId + "' WHERE course_section_id = " + courseSecId;
			
			try{
				stmt = conn.createStatement();
				stmt.executeUpdate(updateQuery);
			}catch(SQLException e){
				System.err.println("UPDATING is_teaching AFTER Professor change: Threw SQL Exception");
				System.err.println(e.getMessage());			
			}finally{
				try{
					if(stmt != null)
						stmt.close();
					if(conn != null)
						conn.close();
				}catch(SQLException e){
					System.err.println("UPDATING is_teaching AFTER Professor change: Threw SQL Exception");
					System.err.println(e.getMessage());
				}finally{}
			}		
			
		}

	
	//upate course section for a given professor
	public void updateCourseSectionProfessor(Professor prof, CourseSection cs){
		
		Connection conn = DBHelper.getConnection();
		Statement stmt = null;
		String updateQuery = "UPDATE course_section SET instructor_ssn = "
				+ prof.getSsn() + ", status ='"
				+ cs.getStatus() + "', location = '"
				+ cs.getLocation() + "', day = '"
				+ cs.getDay() + "', time = '"
				+ cs.getTime() + "' WHERE course_section_id = " 
				+ cs.getCourseSectionId();
		
		try{
			stmt = conn.createStatement();
			stmt.executeUpdate(updateQuery);
		}catch(SQLException e){
			System.err.println("UPDATING course section Professor: Threw SQL Exception");
			System.err.println(e.getMessage());			
		}finally{
			try{
				if(stmt != null)
					stmt.close();
				if(conn != null)
					conn.close();
			}catch(SQLException e){
				System.err.println("UPDATING course section Professor: Threw SQL Exception");
				System.err.println(e.getMessage());
			}finally{}
		}
		System.out.println("TRYING TO ENTER METHOD to SAVE INTO is_teaching TABLE!!");
		updateTeacherInIsTeachingTable(prof.getID(), cs.getCourseSectionId());
		System.out.println("OUT OF Trying to SAVE INTO is_teaching TABLE!!");
	}

	public int getCatIdForCourseSection(int csId){
		Connection con = DBHelper.getConnection();
		Statement stmt = null;
		try{
			//get course
			stmt = con.createStatement();
			String selectCourseQuery = 
			"SELECT catalog_id FROM catalog_contains WHERE course_section_id = " +
						csId;
			ResultSet catRS = stmt.executeQuery(selectCourseQuery);
			
			int catId = 0;
			while(catRS.next()){
				//set course id, status, location, start-date, end-date, and professor
				catId = catRS.getInt("catalog_id");				 
			}		
						
			//close to manage resources
			catRS.close();
			
			return catId;
		}
		catch(SQLException ex){
			System.err.println("CourseDAO: Threw a SQLException retrieving the cours-section object");
			System.err.println(ex.getMessage());
			ex.printStackTrace();
		}finally{
			try{
				if(stmt!=null)
					stmt.close();
				if(con != null){
					System.out.println("Database closed after retrieving course-section");
					con.close();
				}
			}catch(SQLException ex){
				System.err.println("CourseDAO: Threw a SQLException retrieving the cours-section object");
				System.err.println(ex.getMessage());
				ex.printStackTrace();
				
			}
		}
		return (Integer) null;		
	}
	
	//update course section status
	public void updateCourseSectionStatus(int csId, String status){
		Connection con = DBHelper.getConnection();
		Statement stmt = null;
		try{
			String updateStatus = "UPDATE course_section SET status = '" + status +
			"' WHERE course_section_id = " + csId;
			stmt=con.createStatement();
			stmt.executeUpdate(updateStatus);
		}
		catch(SQLException ex){
			System.err.println("SQLException: Threw exception while updating course status");
			System.err.println(ex.getMessage());
		}
		finally
		{
			try{
				if(stmt != null)
				stmt.close();
				if(con != null)
				con.close();
			}
			catch(SQLException ex){
				System.err.println("SQLException: Threw exception while updating course status");
				System.err.println(ex.getMessage());
			}
		}
	}	
}
