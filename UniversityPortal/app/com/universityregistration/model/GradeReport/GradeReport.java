package com.universityregistration.model.GradeReport;
import com.universityregistration.model.Course.CourseSection;

public class GradeReport {
	private int reportId;
	private CourseSection courseSection;
	private String grade;
	private String term;
	private int year;
	
	public GradeReport() {}
	
	public GradeReport(CourseSection courseSection, String term, int year){
		//this.courseSection = courseSection;
		this.term = term;
		this.grade = "A";
		this.year = year;
	}
	
	public GradeReport(CourseSection courseSection, String grade, String term){
		//this.courseSection = courseSection;
		this.grade = grade;
		this.term = term;
	}	
	
	public void setReportId(int reportId)
	{
		this.reportId = reportId;
	}
	
	public int getReportId(){
		return this.reportId;
	}
	
	/*
	public void setCourseSectionId(CourseSection courseSection){
		this.courseSection = courseSection;
	}
	*/
	
	public String getGrade(){
		return grade;
	}
	
	public void setGrade(String string){
		this.grade = string;
	}
	
	
	public void setCourseSection(CourseSection courseSection){
		this.courseSection = courseSection;
	}
	
	
	public CourseSection getCourseSection(){
		return courseSection;
	}
	
	public void setTerm(String term){
		this.term = term;
	}
	
	public String getTerm(){
		return term;
	}	
	
	public void setYear(int year){
		this.year = year;
	}
	
	public int getYear(){
		return year;
	}
}
