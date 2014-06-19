package com.universityregistration.model.Service;

import java.util.Date;
import java.util.Calendar;
import java.util.List;

import com.universityregistration.dal.BillReportDAO;
import com.universityregistration.dal.DatesDAO;
import com.universityregistration.dal.GradeReportDAO;
import com.universityregistration.dal.PersonDAO;
import com.universityregistration.dal.StudentDAO;
import com.universityregistration.model.BillReport.BillReport;
import com.universityregistration.model.Course.*;
import com.universityregistration.model.GradeReport.GradeReport;
import com.universityregistration.model.RegDates.RegistrationDates;
import com.universityregistration.model.Service.*;
import com.universityregistration.model.User.*;

public class StudentService {
	StudentDAO studentDAO = new StudentDAO();
	GradeReportDAO gradeDAO = new GradeReportDAO();
	BillReportDAO billReportDAO = new BillReportDAO();
	PersonDAO personDAO = new PersonDAO();
	DatesDAO dDAO = new DatesDAO();
	CourseService courseServ = new CourseService();
 

    //Creates - Inserts a student record (tuple) inside the Student table -- Administrative Method
    public void createStudent(Student newStudent){
    	studentDAO.insertNewStudent(newStudent);
    }
    
    //Updates personal information for a student (update in DB)
    public void modifyStudentPersonalInformation(Student student){
    	personDAO.updatePersonalInformation(student);
    }

    //Register secondary course
    public boolean registerSecondaryCourse(Student student, CourseSection course, int catId){
        if(isWithinRegDates(catId) && notMaxSecondary(student)){
        	studentDAO.addSecondaryCourse(student, course);
        	/*******************************************************/
        	CourseService cserv = new CourseService();
        	CourseSection newCS = cserv.findCourseSectionById(course.getCourseSectionId());
    	 	newCS.updateStatus();
    	 	cserv.modifyCourseSection(newCS);
    	 	/********************************************************/      	
        	
        	return true;
        }
        else
        	return false;
    }
    
    public boolean isWithinRegDates(int catId){
    	
    	if(isAfterFirstDayToRegister(catId) && isBeforeLastDayToRegister(catId))
    		return true;
    	else
    		return false;
    }
    
    public boolean notMaxPrimary(Student student){
    	if(student.getCurrentCourses().size() < 4)
    		return true;
    	else
    		return false;
    }
    
    public boolean notMaxSecondary(Student student){
    	if(student.getSecondaryCourses().size() < 2)
    		return true;
    	else
    		return false;
    }
    
    //Unregister secondary course
    public boolean withdrawlSecondaryCourse(Student student, CourseSection course, int catId){
        if(isBeforeLastDayToDropClasses(catId)){
        	studentDAO.removeSecondaryCourse(student, course);
        	/*******************************************************/
        	CourseService cserv = new CourseService();
        	CourseSection newCS = cserv.findCourseSectionById(course.getCourseSectionId());
    	 	newCS.updateStatus();
    	 	cserv.modifyCourseSection(newCS);
    	 	/********************************************************/
			return true;
		}
		else{
			return false;
        }
    }
    
    
  //Request Bill Report
   public BillReport requestBillReport(Student student, String term, int year){
	   return billReportDAO.getBillReport(student, term, year);
   }      
    
    //Request Report Card 
    public List<GradeReport> requestReportCard(Student student, String term){
    	return gradeDAO.getReportCardForStudent(student, term);
    }
    
    //retrieve transcripts
    public List<GradeReport> requestTranscripts(Student student){
    	return gradeDAO.retrieveTranscripts(student);
    }
    
    public GradeReport requestReportCard(String sId, int csId){
    	return gradeDAO.getReportCardForStudent(sId, csId);
    }
    
    
    /*********************************************************
    HELPER FUNCTIONS::
    **********************************************************/
  //Register - Add a coursesection to student's current courses (schedule)
    public boolean registerCurrentCourse(Student student, CourseSection course, int catId){
    	 if(isWithinRegDates(catId) && notMaxPrimary(student)){
			studentDAO.registerStudentForCourse(student, course);
        	/*******************************************************/
        	CourseService cserv = new CourseService();
        	CourseSection newCS = cserv.findCourseSectionById(course.getCourseSectionId()); //retrieve
    	 	newCS.updateStatus();		//update status based on NEW roster size
    	 	cserv.modifyCourseSection(newCS);		//resubmit modification into DB!
    	 	/********************************************************/

        	return true;
        }
    	 else
    		 return false;
    }
    
    //Unregister - Drop a coursesection from a studen't current courses (schedule)
    public boolean withdrawlCurrentCourse(Student student, CourseSection course, int catId){
        if(isBeforeLastDayToDropClasses(catId)){
        	studentDAO.withdrawlStudentFromCourse(student, course);
        	/*******************************************************/
        	CourseService cserv = new CourseService();
        	CourseSection newCS = cserv.findCourseSectionById(course.getCourseSectionId());
    	 	newCS.updateStatus();
    	 	cserv.modifyCourseSection(newCS);
    	 	/********************************************************/
			return true;
		}
		else{
			return false;
        }
    }
    
    //Checks that the current date is before the last day to drop classes
    private Boolean isBeforeLastDayToDropClasses(int catId){
        Date today = new Date();
        RegistrationDates rd = dDAO.getRegistrationDatesByCatId(catId);
        return (today.compareTo(rd.getLastDayToDropClasses()) < 0);
    }
    
    //Checks that the current date is before the last day to register
    private Boolean isBeforeLastDayToRegister(int catId){
    	Date today = new Date();
    	RegistrationDates rd = dDAO.getRegistrationDatesByCatId(catId);
    	return (today.compareTo(rd.getLastDayToRegister()) < 0);
    }
    
    //Checks that the current day is after the first day to register
    private Boolean isAfterFirstDayToRegister(int catId){
    	Date today = new Date();
    	RegistrationDates rd = dDAO.getRegistrationDatesByCatId(catId);
    	return (today.compareTo(rd.getFirstDayToRegister()) > 0);
    }    
    
    //Returns a student given a student ID
    public Student findStudentById(String studentId){
    	try{
    		Student student = studentDAO.getStudentById(studentId);
        return student;
    	} catch(Exception ex){
    		System.err.println("StudentService: Threw a Exception retrieving student.");
    	}
    	return null;
    }
    
    
    /*******************************************************************
     END OF HELPER FUNCTIONS
    ********************************************************************/

    /****************************************************************************/
    /*************************** 12.02.2013 UPDATES *****************************/  
       public List<Student> retrieveAllStudents(){
       return studentDAO.getAllStudents();
       }
       
       public List<Student> retrieveFilteredStudents(String filter){
    	   return studentDAO.searchStudents(filter);
       }
       
       public boolean studentAlreadyExists(String sid){
       		return studentDAO.studentExists(sid);
       }


}
