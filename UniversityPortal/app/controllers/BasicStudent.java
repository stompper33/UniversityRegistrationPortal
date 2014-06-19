package controllers;

import com.universityregistration.model.Service.StudentService;
import com.universityregistration.model.User.Student;

import play.mvc.*;
import play.data.*;
import static play.data.Form.*;

import views.html.*;

import models.*;

public class BasicStudent extends Controller {
    
    /**
     * Defines a form wrapping the Student class.
     */ 
    final static Form<Student> studentForm = form(Student.class);
  
    final static String roleType = "student";
    
    final static StudentService studService = new StudentService();
    /**
     * Display a blank form.
     */ 
    public static Result blank() {
        return ok(basic_student_form.render(studentForm));
    }
  
    /**
     * Handle the form submission.
     */
    public static Result submitBasicStudent() {
        Form<Student> filledForm = studentForm.bindFromRequest();
        
        if(filledForm.hasErrors()) {
            return badRequest(basic_student_form.render(filledForm));
        } else {
            Student created = filledForm.get();
            String sid = created.getSsn();
            
            if(!(studService.studentAlreadyExists(sid))){
            	studService.createStudent(created);
            	flash("success", "Professor successfully created");
            }
            else{
            	flash("fail", "Professor with ID: " + sid + " already exists");
            	return ok(basic_student_form.render(filledForm));
            }            
            
            return redirect(routes.StudentAddressController.blank(sid));
        }
    }
  
}