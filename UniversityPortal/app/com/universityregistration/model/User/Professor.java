package com.universityregistration.model.User;

import java.util.*;

import com.universityregistration.model.Course.*;


public class Professor extends Person{
	private String department;
	private List<CourseSection> coursesTeaching = new ArrayList<CourseSection>();
	
	public Professor(){}
	
	public Professor(String firstName, String lastName, String dob, String ssn, String department){
		super(firstName, lastName, dob, ssn);
		this.department = department;
	}
	
	public void setDepartment(String department){
		this.department = department;
	}
	public String getDepartment(){
		return department;
	}
	
	public void setCoursesTeaching(List<CourseSection> coursesTeaching){
		this.coursesTeaching = coursesTeaching;
	}
	
	public List<CourseSection> getCoursesTeaching(){
		return coursesTeaching;
	}
	
	public void addCourse(CourseSection course){
		this.coursesTeaching.add(course);
	}
	
	public void dropCourse(CourseSection course){
		this.coursesTeaching.remove(course);
	}
	
	public void requestRoster(CourseSection section){
		section.printRoster();
	}
	
	/* Add funtions for GradeReport - update */
}
