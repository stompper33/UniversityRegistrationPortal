package com.universityregistration.model.Service;

import com.universityregistration.dal.GradeReportDAO;
import com.universityregistration.model.Course.CourseSection;
import com.universityregistration.model.GradeReport.GradeReport;
import com.universityregistration.model.User.Student;

public class GradeReportService {
	
	//GradeReport gradeReport = new GradeReport();
	GradeReportDAO grDAO = new GradeReportDAO();
		
	//update grade -- Professor service -- U   
	public void updateGradeReport(Student student, CourseSection courseSection, String newGrade, String term){
		grDAO.updateGradeReport(student, courseSection, newGrade, term);
	}
	//retrieve grade -- student request  --- R
	public GradeReport getGradeById(int grId){
		return grDAO.getGradeReportById(grId);
	}
	
	public GradeReport getGradeReportForGivenStudentIdAndCourseSectionId(String studId, int csId){
		return grDAO.getReportCardForStudent(studId, csId);
	}
	
	public String retrieveTermForCourseSection(CourseSection cs){
		return grDAO.getTermForCourseSection(cs);
	}
	
	
}
