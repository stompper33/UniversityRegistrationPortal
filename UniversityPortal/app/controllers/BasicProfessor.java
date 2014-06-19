package controllers;

import com.universityregistration.model.Service.ProfessorService;
import com.universityregistration.model.User.Professor;

import play.mvc.*;
import play.data.*;
import static play.data.Form.*;
import views.html.*;
import models.*;

public class BasicProfessor extends Controller {
    
    /**
     * Defines a form wrapping the Student class.
     */ 
    final static Form<Professor> professorForm = form(Professor.class);
  
    final static String roleType = "staff";
    
    final static ProfessorService profService = new ProfessorService();
    /**
     * Display a blank form.
     */ 
    public static Result blank() {
        return ok(basic_professor_form.render(professorForm));
    }
  
    /**
     * Handle the form submission.
     */
    public static Result submitBasicProfessor() {
        Form<Professor> filledForm = professorForm.bindFromRequest();
        
        if(filledForm.hasErrors()) {
            return badRequest(basic_professor_form.render(filledForm));
        } else {
            Professor createdProf = filledForm.get();
            String sid = createdProf.getSsn();
            
            if(!(profService.professorAlreadyExists(sid))){
            	profService.createProfessor(createdProf);
            	flash("success", "Professor successfully created");
            }
            else{
            	flash("fail", "Professor with ID: " + sid + " already exists");
            	return ok(basic_professor_form.render(filledForm));
            }
            
            return redirect(routes.ProfessorAddressController.blank(sid));
        }
    }
  
}