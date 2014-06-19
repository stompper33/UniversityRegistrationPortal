package com.universityregistration.model.User;

import java.util.*;

import com.universityregistration.model.Course.*;
import com.universityregistration.model.GradeReport.GradeReport;


public class Student extends Person {
    private String major;
    private int classLevel; //classLevel (0-undergrad ; 1-graduate)
    private List<CourseSection> currentCourses = new ArrayList<CourseSection>(4);
    private List<CourseSection> primaryCourses = new ArrayList<CourseSection>(4);
    private List<CourseSection> secondaryCourses = new ArrayList<CourseSection>(2);
    private List<GradeReport> reportCard = new ArrayList<GradeReport>(4);
    
    public Student (){}
    
    public Student(String firstName, String lastName, String dob, String ssn, String major, int classLevel){
    	super(firstName, lastName, dob, ssn);
    	this.major = major;
    	this.classLevel = classLevel;
    }
    
    public void setMajor(String major){
        this.major = major;
    }
    public String getMajor(){
        return major;
    }
    
    public void setClassLevel(int classLevel){
        this.classLevel = classLevel;
    }
    public int getClassLevel(){
        return classLevel;
    }
    
    public void setCurrentCourses(List<CourseSection> courses){
        this.currentCourses = courses;
    }
    
    public List<CourseSection> getCurrentCourses(){
        return currentCourses;
    }
    
    public void setPrimaryCourses(List<CourseSection> primeCourses){
         this.primaryCourses = primeCourses;
    }
    
    public List<CourseSection> getPrimaryCourses(){
        return primaryCourses;
    }
    
    public void setSecondaryCourses(List<CourseSection> secondaryCourses){
        this.secondaryCourses = secondaryCourses;
    }
    
    public List<CourseSection> getSecondaryCourses(){
        return secondaryCourses;
    }
    

    public void setReportCard(List<GradeReport> reportCard){
        this.reportCard = reportCard;
    }
    
    public List<GradeReport> getGradeReport(){
        return reportCard;
    }   
    
    public void addCurrentCourse(CourseSection courseSection){
        this.currentCourses.add(courseSection);
    }
    
    public void dropCurrentCourse(CourseSection courseSection){
        this.currentCourses.remove(courseSection);
    }
    
    public void addPrimaryCourse(CourseSection courseSection){
    	this.primaryCourses.add(courseSection);
    }
    
    public void dropPrimaryCourse(CourseSection courseSection){
    	this.primaryCourses.add(courseSection);
    }
    
    public void addSecondaryCourse(CourseSection courseSection){
    	this.secondaryCourses.add(courseSection);
    }
    
    public void dropSecondaryCourse(CourseSection courseSection){
    	this.secondaryCourses.add(courseSection);
    }
    
    
    public void addGradeReportToReportCard(CourseSection course, String term, int year){
    	reportCard.add(new GradeReport(course, term, year));
	}

    public void deleteGradeReportFromReportCard(GradeReport gradeReport){
    	reportCard.remove(gradeReport);
    }
	
    @Override
    public boolean equals(Object object){
    	boolean result = false;
    	if (object == null || object.getClass() != getClass()) {
    	      result = false;
    	} 
    	else{
    	    Student student = (Student) object;
    	    if(this.getID().equals(student.getID())){
    	    	result = true;
    	    }
    	}
    	return result;
    }	
	
}
