package controllers;

import com.universityregistration.model.Service.PersonService;
import com.universityregistration.model.User.Address;
import com.universityregistration.model.User.Email;

import play.mvc.*;
import play.data.*;
import static play.data.Form.*;
import views.html.*;
import models.*;

public class StudentEmailController extends Controller {

	
	/**
	 * Defines a form wrapping the Address class
	 */
	final static Form<Email> emailForm = form(Email.class);
	final static PersonService personService = new PersonService();
	private static String sid = "";
	final static String roleType="student";
	/**
	 * Display a blank form
	 */
	public static Result blank(String studId){
		sid = studId;
		return ok(email_form.render(emailForm, roleType));	
	}
	
	public static Result submitEmail(){
		Form<Email> filledForm = emailForm.bindFromRequest();
		if(filledForm.hasErrors()){
			return badRequest(email_form.render(filledForm, roleType));
		} else{
			Email studEmail= filledForm.get();
			personService.addEmailForPerson(sid, studEmail);
			return redirect(routes.StudentPhoneController.blank(sid));
		}
	}	
	
}
