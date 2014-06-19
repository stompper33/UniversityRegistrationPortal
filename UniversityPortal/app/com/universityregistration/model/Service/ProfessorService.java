package com.universityregistration.model.Service;

import java.util.List;

import com.universityregistration.dal.GradeReportDAO;
import com.universityregistration.dal.PersonDAO;
import com.universityregistration.dal.ProfessorDAO;
import com.universityregistration.model.Course.*;
import com.universityregistration.model.User.*;

public class ProfessorService {
    
	private ProfessorDAO profDAO = new ProfessorDAO();
	private PersonDAO personDAO = new PersonDAO();
	private GradeReportDAO grDAO = new GradeReportDAO();
	private CourseService courseService = new CourseService();
   

	
    //Will use this implementation in actuality
    public void assignProfessorToCourse(Professor professor, Course course, CourseCatalog catalog, String location, String day, String time){
    	profDAO.registerProfessorToCourse(professor, course, catalog, location, day, time);
    }
    
    //unassigns a professor to teach a course
    public void unassignProfessorToCourse(Professor professor, CourseSection course){  
    	profDAO.unregisterProfessorFromCourse(professor, course);    	
    }    
    
    //update grade for a given student (not in DAO)
    public void updateGrade(Student student, CourseSection course, String grade, String term){
    	grDAO.updateGradeReport(student, course, grade, term);
    }    
    
    //Retrieves a course section roster (not in DAO)
    public List<Student> requestCourseSectionRoster(CourseSection courseSection){
    	return courseService.getCourseRoster(courseSection);
    }
    
    //****************** CRUD *******************
    //create professor  -- C
    public void createProfessor(Professor professor){
    	profDAO.insertNewProfessor(professor);
    }
    
    //update a professor in the professor table  -- modifes personal information ONLY -- U
    public void modifyProfessorPersonalInformation(Professor professor){
    	personDAO.updatePersonalInformation(professor);
    }
    
    /********** GENERAL METHOD TO REMOVE A PERSONAL RECORD (EMAIL, ADDRESS, OR PHONENUMBER) FOR A PROFESSOR ************/
    //delete a personal record for professor
    public void removePersonalRecord(Professor professor, Object obj){
    	personDAO.deletePersonalInformation(professor, obj);
    }

    //returns a professor based on a given ID -- R
    public Professor findProfessorById(String profId){
        return profDAO.getProfessorById(profId);
    }
    

  //get all professors
    public List<Professor> allProfessors(){
    	return profDAO.getAllProfessors();
    }
    
    /************************** 12.02.2013 *******************************/
    public List<Professor> retrieveAllProfessors(){
    return profDAO.getAllProfessors();
    }
    
    public List<Professor> retrieveFilteredProfessors(String filter){
    return profDAO.searchProfessors(filter);
    }
    
    public boolean professorAlreadyExists(String sid){
    	return profDAO.professorExists(sid);
    }
    
}
