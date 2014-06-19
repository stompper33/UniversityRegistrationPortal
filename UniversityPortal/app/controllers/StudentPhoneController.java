package controllers;

import com.universityregistration.model.Service.PersonService;
import com.universityregistration.model.User.PhoneNumber;

import play.mvc.*;
import play.data.*;
import static play.data.Form.*;
import views.html.*;
import models.*;

public class StudentPhoneController extends Controller {
	/**
	 * Defines a form wrapping the Address class
	 */
	final static Form<PhoneNumber> phoneForm = form(PhoneNumber.class);
	final static PersonService personService = new PersonService();
	private static String sid = "";
	final static String roleType="student";
	/**
	 * Display a blank form
	 */
	public static Result blank(String studId){
		sid = studId;
		return ok(phone_form.render(phoneForm, roleType));
		
	}
	
	public static Result submitPhone(){
		Form<PhoneNumber> filledForm = phoneForm.bindFromRequest();
		if(filledForm.hasErrors()){
			return badRequest(phone_form.render(filledForm, roleType));
		} else{
			PhoneNumber studPhone = filledForm.get();
			personService.addPhoneForPerson(sid, studPhone);
			return redirect(routes.StudentCredentialsController.blank(sid));
		}
	}
}
