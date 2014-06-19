package controllers;

import static play.data.Form.form;

import com.universityregistration.model.Service.StudentService;
import com.universityregistration.model.Service.UserService;
import com.universityregistration.model.User.Email;
import com.universityregistration.model.User.PhoneNumber;
import com.universityregistration.model.User.Professor;
import com.universityregistration.model.User.Student;

import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.*;
import com.universityregistration.model.User.User;

public class StudentCredentialsController extends Controller {
	/**
	 * Defines a form wrapping the Address class
	 */
	final static Form<User> userForm = form(User.class);
	final static UserService userServ = new UserService();
	final static StudentService studServ = new StudentService();
	private static String sid = "";
	private static String role = "student";
	/**
	 * Display a blank form
	 */
	public static Result blank(String studId){
		sid = studId;
		return ok(student_credentials_form.render(userForm));
		
	}
	
	public static Result submitCredentials(){
		Form<User> filledForm = userForm.bindFromRequest();
		if(filledForm.hasErrors()){
			return badRequest(student_credentials_form.render(filledForm));
		} else{
			User newUser = filledForm.get();
			newUser.setUid(sid);
			newUser.setRole(role);
			userServ.addNewUser(newUser);

			return redirect(routes.EditStudentController.blank2());
		}
	}
}
