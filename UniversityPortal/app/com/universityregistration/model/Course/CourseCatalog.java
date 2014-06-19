package com.universityregistration.model.Course;

import java.util.List;
import java.util.ArrayList;

public class CourseCatalog {
	private int catId;
    private String term;
    private int year;
    private List<CourseSection> courseList = new ArrayList<CourseSection>();
    
    public CourseCatalog(){}
    
    public void setCatId(int catId){
    	this.catId = catId;
    }
    
    public int getCatId(){
    	return catId;
    }
    
    public CourseCatalog(String term, int year){
        this.term = term;
        this.year = year;
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
    
    public void setCourseList(List<CourseSection> courseList){
        this.courseList = courseList;
    }
    
    public List<CourseSection> getCourseList(){
        return courseList;
    }
    
    public void addCourseListing(CourseSection newCourse){
        courseList.add(newCourse);
    }
    
    public void deleteCourseListing(CourseSection oldCourse){
        courseList.remove(oldCourse);
    }
    
    public void print(){
        System.out.println(term + " " + year + " CATALOG");
        for (int i = 0; i < courseList.size(); i++)
            System.out.println(courseList.get(i));
    }
}
