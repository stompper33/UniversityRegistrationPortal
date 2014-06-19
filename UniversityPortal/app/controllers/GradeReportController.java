package controllers;
import com.universityregistration.model.Course.Course;
import com.universityregistration.model.Course.CourseCatalog;
import com.universityregistration.model.Course.CourseSection;
import com.universityregistration.model.GradeReport.GradeReport;
import com.universityregistration.model.Service.CourseService;
import com.universityregistration.model.Service.GradeReportService;
import com.universityregistration.model.Service.ProfessorService;
import com.universityregistration.model.Service.StudentService;
import com.universityregistration.model.User.Professor;
import com.universityregistration.model.User.Student;

import java.util.ArrayList;
import java.util.List;
import play.mvc.*;
import play.data.*;
import static play.data.Form.*;
import views.html.*;
import models.*;

public class GradeReportController extends Controller{

	private static String sid = "";
	private static StudentService studService = new StudentService();
	
	public static class CustomGradeObj{ //object represents on grade report for a given course
		public String term;
		public int year;
		public String coursename;
		public String instructorname;
		public String currentGrade;
		
	}
	
	//public static Result getGrades(String sid){
	public static Result displayReportCard(String studId){
		sid = studId;
		List<CustomGradeObj> gReport = generateGradeReport(sid);
		Student student = studService.findStudentById(sid);
		return ok(report_card.render(student, gReport));
	}
	
	public static Result displayTranscripts(String studId){
		Student student = studService.findStudentById(studId);
		List<CustomGradeObj> transcript = generateTranscript(studId);
		return ok(student_transcript.render(student, transcript));
	}
	
	/******** HELPER METHODS **********************************************/
	public static List<CustomGradeObj> generateGradeReport(String studId){

		StudentService studService = new StudentService();
		
		Student thisStudent = studService.findStudentById(studId);
		List<GradeReport> studentGrades = new ArrayList<GradeReport>();
		for(CourseSection cs : thisStudent.getCurrentCourses()){
			GradeReport gr = studService.requestReportCard(thisStudent.getID(), cs.getCourseSectionId());
			studentGrades.add(gr);
		}
		
		List<CustomGradeObj> gradeReport = customizeGradeReport(studentGrades);
		
		return gradeReport;
		
	}
	
	//Create transcripts
	public static List<CustomGradeObj> generateTranscript(String studId){
		StudentService studService = new StudentService();
		Student retStudent = studService.findStudentById(studId);
		List<GradeReport> allStudCourses = studService.requestTranscripts(retStudent);
		List<CustomGradeObj> transcript = customizeGradeReport(allStudCourses);
		return transcript;
	}
	
	//Create customObjects for any GradeReport list
	public static List<CustomGradeObj> customizeGradeReport(List<GradeReport> list){
		
		//Iterate through each gradeReport object and create a customGradeObj for each
		//in the process
		CourseService courseService = new CourseService();
		ProfessorService profService = new ProfessorService();
		
		List<CustomGradeObj> retGrades = new ArrayList<CustomGradeObj>();
		for(GradeReport gr : list){
				
			//Create a custom grade object for each grade report
			CustomGradeObj cgo = new CustomGradeObj();
						
			//for each gradereport, retrieve grade, year, and term
			cgo.currentGrade = gr.getGrade();
			cgo.term = gr.getTerm();
			cgo.year = gr.getYear();
							
			//Also retrieve additional information
			CourseSection gradeReportCourseSection = gr.getCourseSection();
			Course gradeReportCourse = 
					courseService.retrieveCourseForCourseSection(gradeReportCourseSection);
			String teacherId = gradeReportCourseSection.getInstructorSsn();
			Professor teacher = profService.findProfessorById(teacherId);
							
			cgo.instructorname = teacher.getFirstName() + " " + teacher.getLastName();
			cgo.coursename = gradeReportCourse.getCourseName();
							
			retGrades.add(cgo);			
		}
					
		return retGrades;	
	}
}
