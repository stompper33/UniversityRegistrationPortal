package com.universityregistration.dal;

import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.universityregistration.model.Course.CourseSection;
import com.universityregistration.model.GradeReport.*;
import com.universityregistration.model.Service.CourseService;
import com.universityregistration.model.User.Student;

public class GradeReportDAO {
	
	public GradeReportDAO() {}
	
	//Create
	//initializes a grade report for new student
		public void initializeStudentGradeReport(Student student, CourseSection course){
			Connection conn = DBHelper.getConnection();
			PreparedStatement gradePst = null;
			
			String initGrade = "Na";
			String term = getTermForCourseSection(course);
			int year = getYearForCourseSection(course);
			
			try{
				//register student for course;
				String intializeGradeReportQuery = 
						"INSERT INTO grade_report (student_id, section_id, grade, term, year) VALUES (?, ?, ?, ?, ?)";
				gradePst = conn.prepareStatement(intializeGradeReportQuery);
				gradePst.setString(1, student.getID());
				gradePst.setInt(2, course.getCourseSectionId());
				gradePst.setString(3, initGrade);
				gradePst.setString(4, term);
				gradePst.setInt(5, year);
				gradePst.executeUpdate();
			} catch(SQLException ex){
				System.err.println("StudentDAO: Threw a SQLException saving student grade report.");
				System.out.println(ex.getMessage());			
			} finally{
				try{
					if (gradePst != null)
						gradePst.close();
					if (conn != null){
						System.out.println("Database closed after grade report");
						conn.close();
					}
				} catch (SQLException ex){
					System.err.println("StudentDAO: Threw a SQLException saving student grade report.");
					System.out.println(ex.getMessage());
				}
			}
		}
	//Get gradeReport -- helper function
	public GradeReport getGradeReportById(int reportId){
		
		CourseDAO courseSectionDAO = new CourseDAO();
		
		Connection con = DBHelper.getConnection();
		Statement stmt = null;
		try{
			stmt = con.createStatement();
			String selectGradeReportQuery = "SELECT report_id, section_id, grade, term, year FROM grade_report WHERE report_id = " + reportId;
			ResultSet gradeRS = stmt.executeQuery(selectGradeReportQuery);
			
			GradeReport gr = new GradeReport();
			int csid = 0;
			while(gradeRS.next()){
				gr.setReportId(gradeRS.getInt("report_id"));
				CourseSection cs = courseSectionDAO.getCourseSectionById(gradeRS.getInt("report_id"));
				gr.setCourseSection(cs);				
				gr.setGrade(gradeRS.getString("grade"));
				gr.setTerm(gradeRS.getString("term"));
				gr.setYear(gradeRS.getInt("year"));
				
				csid = gradeRS.getInt("section_id");
				
			}	
			
			CourseService cservice = new CourseService();
			gr.setCourseSection(cservice.findCourseSectionById(csid));
			gradeRS.close();
			
			return gr;
			
		}
	    catch (SQLException se) {
		      System.err.println("GradeReportDAO: Threw a SQLException retrieving the grade report object.");
		      System.err.println(se.getMessage());
		      se.printStackTrace();
		 }finally{
			 try{
				 if(stmt != null)
					 stmt.close();
				 if(con != null)
					 con.close();
			 }catch (SQLException se) {
			      System.err.println("GradeReportDAO: Threw a SQLException retrieving the grade report object.");
			      System.err.println(se.getMessage());
			      se.printStackTrace();
			 }
		 }
		    
		    return null;
		
	}
	
	
	
	
	//Retrieve a gradeReport -- student invoked
	public List<GradeReport> getReportCardForStudent(Student student, String term){
		
		Connection con = DBHelper.getConnection();
		Statement stmt = null;
		try { 		
	    	//Get report
	    	stmt = con.createStatement();
	    	String selectReportCardQuery = "SELECT report_id FROM grade_report WHERE student_id = '" 
	    			+ student.getID() + "' AND term = '" + term + "'";

	    	ResultSet reportCardRS = stmt.executeQuery(selectReportCardQuery);      
	    	System.out.println("CustomerDAO: *************** Query " + selectReportCardQuery);
	    	
	    	List<GradeReport> reportCard = new ArrayList<GradeReport>();
	    	
	      
    	  //GradeReport customer = new Customer();
	      while ( reportCardRS.next() ) {
	    	  GradeReport gr = getGradeReportById(reportCardRS.getInt("report_id"));
	    	  reportCard.add(gr);
	    }
	      
	      //close to manage resources
	      reportCardRS.close();
		
		
		return reportCard;
		
	}catch (SQLException se) {
	      System.err.println("GradeReportDAO: Threw a SQLException retrieving the grade report object.");
	      System.err.println(se.getMessage());
	      se.printStackTrace();
	}finally{
		try{
			if(stmt != null)
				stmt.close();
			if(con != null)
				con.close();
		}catch (SQLException se) {
		      System.err.println("GradeReportDAO: Threw a SQLException retrieving the grade report object.");
		      System.err.println(se.getMessage());
		      se.printStackTrace();
		}
	}
	
		return null;	
}
	
	
	//get grade report for student based on current courses ONLY
	public GradeReport getReportCardForStudent(String sId, int csId){
		
		Connection con = DBHelper.getConnection();
		Statement stmt = null;
		
		try { 		
	    	//Get report
			stmt = con.createStatement();
	    	String selectReportCardQuery = "SELECT report_id FROM grade_report WHERE student_id = '" 
	    			+ sId + "' AND section_id = " + csId + "";

	    	ResultSet reportCardRS = stmt.executeQuery(selectReportCardQuery);      
	    	System.out.println("CustomerDAO: *************** Query " + selectReportCardQuery);
	    	
	    	GradeReport gr = new GradeReport();
	    	
	      
    	  //GradeReport customer = new Customer();
	      while ( reportCardRS.next() ) {
	    	  gr = getGradeReportById(reportCardRS.getInt("report_id"));
		  }
		      
		  //close to manage resources
		  reportCardRS.close();
			
			
		  return gr;
			
		}catch (SQLException se) {
		      System.err.println("GradeReportDAO: Threw a SQLException retrieving the grade report object.");
		      System.err.println(se.getMessage());
		      se.printStackTrace();
		}finally{
			try{
				if(stmt != null)
					stmt.close();
				if(con != null)
					con.close();
			}catch (SQLException se) {
			      System.err.println("GradeReportDAO: Threw a SQLException retrieving the grade report object.");
			      System.err.println(se.getMessage());
			      se.printStackTrace();
			}
		}
		return null;
	}
	
	
	
	
	
	
	
	//updating grade report --- invoked by professors service
	public void updateGradeReport (Student student, CourseSection course, String grade, String term){
		
		Connection con = DBHelper.getConnection();
		Statement stmt = null;
		try{
			stmt = con.createStatement();
			String updateGradeReportQuery = "UPDATE grade_report SET grade = '"+ grade +
					"' WHERE student_id = '" + student.getID() + "' AND section_id = " 
					+ course.getCourseSectionId() + " AND term ='" + term + "'";
			
			stmt.executeUpdate(updateGradeReportQuery);
			
		}
	    catch (SQLException se) {
		      System.err.println("GradeReportDAO: Threw a SQLException updating the grade report object.");
		      System.err.println(se.getMessage());
		      se.printStackTrace();
		 }
		finally{
			try{
				if(stmt != null)
					stmt.close();
				if(con != null)
					con.close();
			}catch(SQLException se) {
			      System.err.println("GradeReportDAO: Threw a SQLException updating the grade report object.");
			      System.err.println(se.getMessage());
			      se.printStackTrace();
			 }
		}
	}
	
	
	
	//retrieve transcripts -- (student-service invoked)
	public List<GradeReport> retrieveTranscripts(Student student){
		
		Connection con = DBHelper.getConnection();
		Statement stmt = null;
		
		try { 		
	    	//Get ALL courses for student 
			stmt = con.createStatement();
	    	String selectReportCardQuery = "SELECT report_id FROM Grade_Report WHERE student_id = '" 
	    			+ student.getID() + "'";

	    	ResultSet transRS = stmt.executeQuery(selectReportCardQuery);      
	    	System.out.println("CustomerDAO: *************** Query " + selectReportCardQuery);
	    	
	    	List<GradeReport> transcript = new ArrayList<GradeReport>();
	    	
	      
    	  //GradeReport customer = new Customer();
	      while ( transRS.next() ) {
	    	  GradeReport gr = getGradeReportById(transRS.getInt("report_id"));
	    	  transcript.add(gr);
	    }
	      
	      //close to manage resources
	      transRS.close();
		
		
		return transcript;
		
	}catch (SQLException se) {
	      System.err.println("GradeReportDAO: Threw a SQLException retrieving the grade report object.");
	      System.err.println(se.getMessage());
	      se.printStackTrace();
	}finally{
		try{
			if(stmt != null)
				stmt.close();
			if(con != null)
				con.close();
		}catch (SQLException se) {
		      System.err.println("GradeReportDAO: Threw a SQLException retrieving the grade report object.");
		      System.err.println(se.getMessage());
		      se.printStackTrace();
		}
	}
	
		return null;	
		
	}
	
	
	
	
	//GET term and year FOR course_section
	public String getTermForCourseSection(CourseSection cs){
		
		Connection conn = DBHelper.getConnection();
		Statement stmt = null;
		
		try{
			stmt = conn.createStatement();
			String getTermQuery = 
					"SELECT cc.term FROM course_catalog cc, catalog_contains ct"
					+ " WHERE cc.catalog_id = ct.catalog_id AND ct.course_section_id = "
					+ cs.getCourseSectionId();
			
			ResultSet termRS = stmt.executeQuery(getTermQuery);
			
			String t = "";
			while(termRS.next()){
				t = termRS.getString("term");
			}
			
			termRS.close();
			
			return t;
			
		}
		catch(SQLException ex){
			System.err.println("SQLException: Thrown error trying to retrieve section term");
			System.err.println(ex.getMessage());
		}
		finally{
			try{
				if(stmt != null)
					stmt.close();
				if(conn != null)
					conn.close();
			}catch(SQLException ex){
				System.err.println("SQLException: Thrown error trying to retrieve section term");
				System.err.println(ex.getMessage());
			}
		}
		return null;
	}
	
	
	//GET YEAR FOR ANY COURSE SECTION WITHIN A CATALOG
	public int getYearForCourseSection(CourseSection cs){
		
		Connection conn = DBHelper.getConnection();
		Statement stmt = null;
		
		try{
			String getYearQuery = 
					"SELECT cc.year FROM course_catalog cc, catalog_contains ct"
					+ " WHERE cc.catalog_id = ct.catalog_id AND ct.course_section_id = "
					+ cs.getCourseSectionId();
			stmt = conn.createStatement();
			
			ResultSet termRS = stmt.executeQuery(getYearQuery);
			
			int yr = 0;
			while(termRS.next()){
				yr = termRS.getInt("year");
			}
			
			termRS.close();
			
			return yr;
			
		}
		catch(SQLException ex){
			System.err.println("SQLException: Thrown error trying to retrieve section term");
			System.err.println(ex.getMessage());
		}
		finally{
			try{
				if(stmt != null)
					stmt.close();
				if(conn != null)
					conn.close();
			}catch(SQLException ex){
				System.err.println("SQLException: Thrown error trying to retrieve section term");
				System.err.println(ex.getMessage());
			}
		}
		return (Integer) null;
	}	
}