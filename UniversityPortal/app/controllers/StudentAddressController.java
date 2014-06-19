package controllers;
import com.universityregistration.model.Service.PersonService;
import com.universityregistration.model.User.Address;

import play.mvc.*;
import play.data.*;
import static play.data.Form.*;
import views.html.*;
import models.*;

public class StudentAddressController extends Controller {
	
	/**
	 * Defines a form wrapping the Address class
	 */
	final static Form<Address> addressForm = form(Address.class);
	private static String sid = "";
	final static String roleType="student";
	final static PersonService personService = new PersonService();
	/**
	 * Display a blank form
	 */
	public static Result blank(String studId){
		sid = studId;
		return ok(address_form.render(addressForm, roleType));
		
	}
	
	public static Result submitAddress(){
		Form<Address> filledForm = addressForm.bindFromRequest();
		if(filledForm.hasErrors()){
			return badRequest(address_form.render(filledForm, roleType));
		} else{
			Address createdAddress = filledForm.get();
			personService.addAddressForPerson(sid, createdAddress);
			return redirect(routes.StudentEmailController.blank(sid));
		}
	}
}
