//CourseService METHODs::::::::::::::::::::::::
package com.universityregistration.model.Service;

import java.util.List;

import com.universityregistration.dal.CourseDAO;
import com.universityregistration.dal.GradeReportDAO;
import com.universityregistration.model.Course.*;
import com.universityregistration.model.User.*;

public class CourseService {
	
	private CourseDAO courseSectionDAO = new CourseDAO();
    
    public List<Student> getCourseRoster(CourseSection cs){
    	return cs.getRoster();
    }
    
    public String getStatus(CourseSection cs){
    	return cs.getStatus();
    }
    
    public String getInstructorSsn(CourseSection cs){
    	return cs.getInstructorSsn();
    }
    
    public void modifyCourseSection(CourseSection cs){
    	courseSectionDAO.updateCourseSection(cs);
    }
    
    public void modifyCourseSectionProf(Professor prof, CourseSection cs){
    	courseSectionDAO.updateCourseSectionProfessor(prof, cs);
    }
    
    public void finalizeCourseList(CourseCatalog catalog){
        for(CourseSection c : catalog.getCourseList()){
            if (c.getStatus().equals("open") && c.getRoster().size()<3)
                c.setStatus("canceled");
        }
    }
    
    public List<CourseSection> retrieveAllCourseSectionByCatId(int catId){
    	return courseSectionDAO.allCourseSectionByCatId(catId);
    }
    
    public int retrieveCatIdByCourseSectionId(int csId){
    	return courseSectionDAO.getCatIdForCourseSection(csId);
    }
    
    //********Administrative Course Functions (C.R.U.D.) ***********************************************
    //This method will create a course offering (insert into Course Table) -- invoked my Admin
    public void createCourseOffering(Course course){
    	courseSectionDAO.insertCourseOffering(course);
    }
    
    public Course findCourseById(String cId){
    	return courseSectionDAO.getCourseById(cId);
    }
    
    public void modifyCourseOffering(Course course){
    	courseSectionDAO.updateCourseOffering(course);
    }
    
    public void removeCourseOffering(Course course){
    	courseSectionDAO.deleteCourseOffering(course);
    }
    
    public void removeCourseById(String csId){
    	courseSectionDAO.deleteCourseOfferingById(csId);
    }
    
    public List<Course> allCourses(){
    	return courseSectionDAO.getAllCourses();
    }
    //************************************************************************************************
        
    //db tasks
    public CourseSection findCourseSectionById(int csId){
        return courseSectionDAO.getCourseSectionById(csId);
    }
    
    public CourseCatalog findCourseCatalogById(int ccId){
        return courseSectionDAO.getCourseCatalogById(ccId);
    }
    
    public Course retrieveCourseForCourseSection(CourseSection cs){
    	return courseSectionDAO.getCourseByCourseSection(cs);
    }
    
    
    /*** Course_Catalog CREATE ***************************************************************************/
    public void createNewCatalog(CourseCatalog catalog){
    	courseSectionDAO.insertCatalog(catalog);
    }
    
    
    
    public String getTermForCourseSection(CourseSection cs){
    	GradeReportDAO gdao = new GradeReportDAO();
    	return gdao.getTermForCourseSection(cs);
    }
    
    public int getYearForGivenCourseSection(CourseSection cs){
    	GradeReportDAO gdao = new GradeReportDAO();
    	return gdao.getYearForCourseSection(cs);
    }
    
    public List<CourseCatalog> retrieveAllCatalogs(){
    	return courseSectionDAO.getAllCatalogs();
    }
    
    
    /************************** 11/27/2013 ************************************/
    /***********************************************************************************/
    /********************** CourseService METHODs *************************************/
    public void updateStatusOfCourseSection(int csId, String status){
    	courseSectionDAO.updateCourseSectionStatus(csId, status);
    }
    
    public void finalizeCat(CourseCatalog catalog){
    	//distinguish which courses do not meet requirement of 'AT LEAST 3 enrolled students'
    	String cancelStatus = "canceled";
    	List<CourseSection> currentCourses = catalog.getCourseList();
    	
    	for(CourseSection cs : currentCourses){
    		
    		if(!(meetsSizeReq(cs)) || !(hasTeacher(cs))){
    			cs.setStatus(cancelStatus);
    			updateStatusOfCourseSection(cs.getCourseSectionId(), cancelStatus);
    			adjustStudents(cs.getRoster(), cs, catalog.getCatId());
    			
    		}
    	}
    	
    }
    
  //Adjust students -- CourseService HELPER-METHOD!
    public void adjustStudents(List<Student> canceledRoster, CourseSection cs, int catId){
    	StudentService studService = new StudentService();
    	BillReportService billService = new BillReportService();
    	GradeReportDAO grdao = new GradeReportDAO();

    	for(Student s: canceledRoster){ // iterates through roster and drops course from student's sched.
    		
    		studService.withdrawlCurrentCourse(s, cs, catId); //delete student record from is_taking
    		
    		
    		//Adjust student schedules
    		Student thisStudent = studService.findStudentById(s.getID());
    		
    		for(CourseSection secCs : thisStudent.getSecondaryCourses()){
    			//iterates through secondary course and TRYS to assign one to student
    			if((secCs.getStatus()).equals("open")){    				
    				studService.registerCurrentCourse(thisStudent, secCs, catId);
    				studService.withdrawlSecondaryCourse(thisStudent, secCs, catId);
    				
    				break;							
    			}
    		}
    		
    		//For each student update his/her billreport
    		String term = grdao.getTermForCourseSection(cs);
    		int year = grdao.getYearForCourseSection(cs);
    		Student newStud = studService.findStudentById(s.getID());
    		billService.modifyBillReport(newStud, term, year);
    	}
    	
    }
    //THESE METHODS ARE TO BE PUT INSIDE CourseService CLASS (HELPER METHODS):
    public boolean meetsSizeReq(CourseSection cs){
    	if(cs.getRoster().size() >= 3)
    		return true;
    	else
    		return false;
    }

    public boolean hasTeacher(CourseSection cs){
    	if(cs.getInstructorSsn().equals("staff"))
    		return false;
    		
    	else
    		return true;
    }
    
}