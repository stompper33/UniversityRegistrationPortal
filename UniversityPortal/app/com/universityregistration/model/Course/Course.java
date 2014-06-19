package com.universityregistration.model.Course;

import java.util.List;
import java.util.ArrayList;

public class Course {
    private String courseId;
    private String courseName;
    private String courseDescription;
    private int courseCredits;
    List<Course> prereqs = new ArrayList<Course>();
    
    public Course(String courseId, String courseName, String courseDescription, int credits){
        this.courseId = courseId;
        this.courseName = courseName;
        this.courseDescription = courseDescription;
        this.courseCredits = credits;
    }
    
    public Course(){}
    
    public void setCourseId(String courseId){
        this.courseId = courseId;
    }
    
    public String getCourseId(){
        return courseId;
    }
    
    public void setCourseName(String courseName){
        this.courseName = courseName;
    }
    
    public String getCourseName(){
        return courseName;
    }
    
    public void setCourseDescription(String courseDescription){
        this.courseDescription = courseDescription;
    }
    
    public String getCourseDescription(){
        return courseDescription;
    }
    
    public void setCourseCredits(int credits){
        this.courseCredits = credits;
    }
    
    public int getCourseCredits(){
        return courseCredits;
    }
    
    public void setPrereqs(List<Course> prereqs){
        this.prereqs = prereqs;
    }
    
    public List<Course> getPrereqs(){
        return prereqs;
    }
    
    public void addPrereq(Course newPrereq){
        prereqs.add(newPrereq);
    }
    
    public String toString(){
        String output = courseId + ":  " + courseName;
        output += "\nCredits:  " + courseCredits;
        output += "\n\n" + courseDescription;
        return output;
    }
}
