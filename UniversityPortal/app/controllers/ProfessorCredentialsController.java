package controllers;

import static play.data.Form.form;

import com.universityregistration.model.Service.PersonService;
import com.universityregistration.model.Service.ProfessorService;
import com.universityregistration.model.Service.UserService;
import com.universityregistration.model.User.Email;
import com.universityregistration.model.User.PhoneNumber;
import com.universityregistration.model.User.Professor;

import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.*;
import com.universityregistration.model.User.User;

public class ProfessorCredentialsController extends Controller {
	/**
	 * Defines a form wrapping the Address class
	 */
	final static Form<User> userForm = form(User.class);
	final static UserService userServ = new UserService();
	final static ProfessorService profServ = new ProfessorService();
	private static String pid = "";
	private static String role = "staff";
	/**
	 * Display a blank form
	 */
	public static Result blank(String profId){
		pid = profId;
		return ok(professor_credentials_form.render(userForm));
		
	}
	
	public static Result submitCredentials(){
		Form<User> filledForm = userForm.bindFromRequest();
		if(filledForm.hasErrors()){
			return badRequest(professor_credentials_form.render(filledForm));
		} else{
			User newUser = filledForm.get();
			newUser.setUid(pid);
			newUser.setRole(role);
			userServ.addNewUser(newUser);
			
			return redirect(routes.EditProfessorController.blank2());
		}
	}
}
