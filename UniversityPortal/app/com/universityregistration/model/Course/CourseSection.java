package com.universityregistration.model.Course;

import com.universityregistration.model.RegDates.RegistrationDates;
import com.universityregistration.model.User.*;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

public class CourseSection {
    private List<Student> roster = new ArrayList<Student>();
    //private Professor instructor = new Professor();
    private String instructor_ssn;
    private final int capacity = 10;
    private final int min = 3;
    private String status = "open";
    private String location;
    private int courseSectionId;
    private String day;
    private String time;
    
    public CourseSection(){}
    
    public CourseSection(String location, String instructor_ssn){
        //super(courseId, courseName, courseDescription);
        this.location = location;
        this.instructor_ssn = instructor_ssn;
    }
    
    // constructor for courseSection without a location
    public CourseSection(String instructor){
        //super(courseId, courseName, courseDescription);
        this.instructor_ssn = instructor_ssn;
    }   
    
    public void setRoster(List<Student> roster){
            this.roster = roster;
    }
    
    public List<Student> getRoster(){
        return roster;
    }
    
    public void setInstructorSsn(String instructor_ssn){
        this.instructor_ssn = instructor_ssn;
    }
    
    public String getInstructorSsn(){
        return instructor_ssn;
    }
    
    public void setStatus(String status){
        this.status = status;
    }
    
    public String getStatus(){
        return status;
    }
    
    public void setLocation(String location){
        this.location = location;
    }
    
    public String getLocation(){
        return location;
    }
    
    public void printRoster(){
        for(int i = 0; i < roster.size(); i++){
            System.out.println(roster.get(i).toString());
        }
    }
    
    public void addStudent(Student s){
        if (status.equals("open")){
            roster.add(s);
        }
        updateStatus();
    }
    
public void dropStudent(Student s){
    	roster.remove(s);
    	updateStatus();
    }
    
    public void updateStatus(){
        if (roster.size() < capacity && !(this.status.equals("canceled"))){
            setStatus("open");
        }
        else if(roster.size() < capacity && this.status.equals("canceled")){
        	setStatus("canceled");
        }
        else{ 
            setStatus("closed");
        }
    }
    
    public int getEnrollmentSize(){
        return roster.size();
    }
    
    /*
    public String toString(){
        return instructor.toString() + "\n" + location + "\n" + RegistrationDates.startDate + " - " + RegistrationDates.endDate + "\n" + super.toString() + "\n\n" + "Enrollment Status: "
        + status + "\n" ;
    }
    */
    
    public void setCourseSectionId(int courseSectionId)
    {
    	this.courseSectionId = courseSectionId;
    }
    
    public int getCourseSectionId(){
    	return courseSectionId;
    }
    
    public void setDay(String day){
    	this.day = day;
    }
    
    public String getDay(){
    	return day;
    }
    
    public void setTime(String time){
    	this.time = time;
    }
    
    public String getTime(){
    	return time;
    }
}
