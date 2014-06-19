package controllers;
import com.universityregistration.model.Service.PersonService;
import com.universityregistration.model.User.Address;

import play.mvc.*;
import play.data.*;
import static play.data.Form.form;
import play.data.Form;
import play.mvc.Result;
import views.html.*;
import models.*;

public class ProfessorAddressController extends Controller {
	/**
	 * Defines a form wrapping the Address class
	 */
	final static Form<Address> addressForm = form(Address.class);
	private static String sid = "";
	final static String roleType="staff";
	final static PersonService personService = new PersonService();
	/**
	 * Display a blank form
	 */
	public static Result blank(String profId){
		sid = profId;
		return ok(address_professor_form.render(addressForm, roleType));
		
	}
	
	public static Result submitAddress(){
		Form<Address> filledForm = addressForm.bindFromRequest();
		if(filledForm.hasErrors()){
			return badRequest(address_professor_form.render(filledForm, roleType));
		} else{
			Address createdAddress = filledForm.get();
			personService.addAddressForPerson(sid, createdAddress);
			return redirect(routes.ProfessorEmailController.blank(sid));
		}
	}
}
