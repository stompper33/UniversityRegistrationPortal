package controllers;

import com.universityregistration.model.Service.PersonService;
import com.universityregistration.model.Service.ProfessorService;
import com.universityregistration.model.User.PhoneNumber;
import com.universityregistration.model.User.Professor;

import play.mvc.*;
import play.data.*;
import static play.data.Form.*;
import views.html.*;
import models.*;
public class ProfessorPhoneController extends Controller {
	/**
	 * Defines a form wrapping the Address class
	 */
	final static Form<PhoneNumber> phoneForm = form(PhoneNumber.class);
	final static PersonService personService = new PersonService();
	final static ProfessorService profServ = new ProfessorService();
	private static String sid = "";
	final static String roleType="staff";
	/**
	 * Display a blank form
	 */
	public static Result blank(String profId){
		sid = profId;
		return ok(phone_professor_form.render(phoneForm, roleType));
	}
	
	public static Result submitPhone(){
		Form<PhoneNumber> filledForm = phoneForm.bindFromRequest();
		if(filledForm.hasErrors()){
			return badRequest(phone_professor_form.render(filledForm, roleType));
		} else{
			PhoneNumber profPhone = filledForm.get();
			personService.addPhoneForPerson(sid, profPhone);
			return redirect(routes.ProfessorCredentialsController.blank(sid));
		}
	}
}
