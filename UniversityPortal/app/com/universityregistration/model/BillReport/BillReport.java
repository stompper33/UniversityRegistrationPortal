package com.universityregistration.model.BillReport;

import com.universityregistration.dal.CourseDAO;
import com.universityregistration.model.Course.*;
import com.universityregistration.model.User.*;

import java.util.List;

public class BillReport {
    private Student student = new Student();
    private float balance = 0;
    private int totalCredits = 0;
    private final float undergraduateTuition = (float) 450.00; // Per credit (subject to change)
    private final float graduateTuition = (float) 903.00;  // Per credit (subject to change)
    private int billId;
    private float rate = 0;
    private String term="";
    private int year = 0;
    private CourseDAO cdao = new CourseDAO();
    
    public BillReport(){}
    
    public BillReport(Student student, String term, int year){
        this.student = student;
    	this.term = term;
    	this.year = year;
        createBillStatement();
    }
    
    public void setBillId(int billId){
        this.billId = billId;
    }
    
    public int getBillId(){
        return billId;
    }
    
    public String getTerm(){
    	return term;
    }
    
    public void setTerm(String term){
    	this.term = term;
    }
    
    public int getYear(){
    	return year;
    }
    
    public void setYear(int year){
    	this.year = year;
    }
    
    public Student getStudent(){
        return student;
    }
    
    public void setTotalCredits(int totalCredits){
        this.totalCredits = totalCredits;
    }
    
    public int getTotalCredits(){
        return totalCredits;
    }
    
    public float getBalance(){
    	return this.balance;
    }
    
    public void setBalance(float amount){
    	this.balance = amount;
    }
    
    public void setStudent(Student student){
    	this.student = student;
    }
    
    public void setRate(float rate){
    	this.rate = rate;
    }
    
    public float getRate(){
    	return rate;
    }
    
    
    //*********************************
    private int calcTotalCredits(){
        int total = 0;
        List<CourseSection> studentCourses = student.getCurrentCourses();
        for (CourseSection cs : studentCourses){
        	System.out.println(cs.getCourseSectionId());
        	System.out.println(cs.getInstructorSsn());
        	Course c = cdao.getCourseByCourseSection(cs);
        	System.out.println("NUMBER OF CREDITS: " + c.getCourseCredits());
            total += c.getCourseCredits();
        }
        return total;
    }
    
    private float getStudentTuition(){
        
        float tuitionRate;
        
        //determine tuition by student grade level
        if(student.getClassLevel() == 0)
            tuitionRate = undergraduateTuition;
        else if (student.getClassLevel() == 1)
            tuitionRate = graduateTuition;
        else
            tuitionRate = 0;
            
        return tuitionRate;
    }
    
    private float calcTuition(){
        float billTotal =  totalCredits * getStudentTuition();
        return billTotal;
    }
    
    
    public void createBillStatement(){
        this.totalCredits = calcTotalCredits();
        this.rate = getStudentTuition();
        this.balance = calcTuition();
        
    } 
}
