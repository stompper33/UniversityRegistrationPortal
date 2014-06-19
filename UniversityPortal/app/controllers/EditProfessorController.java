package controllers;
import java.util.List;

import play.mvc.*;
import play.data.*;
import static play.data.Form.*;
import views.html.*;
import models.*;

import com.universityregistration.model.Service.PersonService;
import com.universityregistration.model.Service.ProfessorService;
import com.universityregistration.model.User.Address;
import com.universityregistration.model.User.Email;
import com.universityregistration.model.User.PhoneNumber;
import com.universityregistration.model.User.Professor;
import com.universityregistration.model.User.Student;

public class EditProfessorController extends Controller {
	
	private static ProfessorService profService = new ProfessorService();
	private static PersonService personService = new PersonService();
	final static String roleType="staff";
	private static String pid = "";
	
	//For editing purposes
	private static Form<Professor> theProfessorForm = form(Professor.class);
	private static Form<Address> theAddressForm = form(Address.class);
	private static Form<PhoneNumber> thePhoneForm = form(PhoneNumber.class);
	private static Form<Email> theEmailForm = form(Email.class);
	
	//Describes the search form
	public static class Search{
		public String searchId;
	}

	
	/***********************************************************************/
	public static Result editPersonalInfo(String pid){
		Professor prof = profService.findProfessorById(pid);
		Form<Professor> profForm = form(Professor.class).fill(prof);
		theProfessorForm = profForm;
		return ok(Edits_Professor_Form.render(theProfessorForm));
	}
	
	public static Result editAddress(int aid){		
		Address addressToChange = personService.findAddressById(aid);
		String profId = personService.findUidForAddId(aid);
		Form<Address> addressForm = form(Address.class).fill(addressToChange);
		theAddressForm = addressForm;
		return ok(edit_professor_address_form.render(theAddressForm, roleType, aid, profId));
	}

	
	public static Result editEmail(int eid){
		Email email = personService.findEmailById(eid);
		String profId = personService.findUidForEmailId(eid);
		Form<Email> emailForm = form(Email.class).fill(email);
		theEmailForm = emailForm;
		return ok(edit_professor_email_form.render(theEmailForm, roleType, eid, profId));
	}
	
	public static Result editPhone(int phnid){
		PhoneNumber phone = personService.findPhoneNumberById(phnid);
		String profId = personService.findUidForPhoneId(phnid);
		Form<PhoneNumber> phoneForm = form(PhoneNumber.class).fill(phone);
		return ok(edit_professor_phone_form.render(phoneForm, roleType, phnid, profId));
	}
	
	//Submit -- Edited Address
	public static Result submitEditedAddress(int aid){
		Form<Address> updatedAddressForm = theAddressForm.bindFromRequest();
		Address updatedAddress = updatedAddressForm.get();
		updatedAddress.setAddressId(aid);
		personService.modifyAddress(pid, updatedAddress);
		Professor retProfessor = profService.findProfessorById(pid);
		return ok(professor_profile.render(retProfessor));
	}
	
	//Submit -- Edited Phone
	public static Result submitEditedPhone(int phnid){
		Form<PhoneNumber> updatedPhoneForm = thePhoneForm.bindFromRequest();
		PhoneNumber updatedPhone = updatedPhoneForm.get();
		updatedPhone.setPhoneNumberId(phnid);
		personService.modifyPhone(pid, updatedPhone);
		
		//TESTING:
		Professor retProfessor = profService.findProfessorById(pid);
		return ok(professor_profile.render(retProfessor));
		
	}
	
	//Submit -- Edited Email
	public static Result submitEditedEmail(int eid){
		Form<Email> updatedPhoneForm = theEmailForm.bindFromRequest();
		Email updatedEmail = updatedPhoneForm.get();
		updatedEmail.setEmailId(eid);
		personService.modifyEmail(pid, updatedEmail);
		
		//Testing:
		Professor retProfessor = profService.findProfessorById(pid);
		return ok(professor_profile.render(retProfessor));	
	}
	
	//update personal information
	public static Result updateBasicProfessor(){
		Form<Professor> updatedProfessorForm = theProfessorForm.bindFromRequest();
		
		if(updatedProfessorForm.hasErrors()){
			return badRequest(Edits_Professor_Form.render(updatedProfessorForm));
		}
		
		else{
			Professor updatedProfessor = updatedProfessorForm.get();
			String profId = updatedProfessor.getSsn();
			updatedProfessor.setID(profId);
			profService.modifyProfessorPersonalInformation(updatedProfessor);
			
			Professor retProfessor = profService.findProfessorById(profId);
			return ok(professor_profile.render(retProfessor));
		}
	}
	
	
	
	
	/********************************************************/
	//DELETION METHODS
	public static Result delAddress(int aid){
		personService.removeAddress(aid);		
		return ok(professor_profile.render(profService.findProfessorById(pid)));
	}
	
	public static Result delPhone (int phnId){
		personService.removePhone(phnId);
		return ok(professor_profile.render(profService.findProfessorById(pid)));
	}
	
	public static Result delEmail(int eid){
		personService.removeEmail(eid);
		return ok(professor_profile.render(profService.findProfessorById(pid)));
	}
	
	//ADD new address, email, or phone
	//DIRECT STUDENT to form for adding additional email, phone, or address
	public static Result createAddress(){
		theAddressForm = form(Address.class);
		return ok(add_new_professor_address_form.render(theAddressForm, roleType));
	}
	
	public static Result createEmail(){
		theEmailForm = form(Email.class);
		return ok(add_new_professor_email_form.render(theEmailForm, roleType));
	}
	
	public static Result createPhone(){
		thePhoneForm = form(PhoneNumber.class);
		return ok(add_new_professor_phone_form.render(thePhoneForm, roleType));
	}
	
	//Submit additional address, email, or phone
	public static Result submitNewAddress(){
		Form<Address> retAddressForm = theAddressForm.bindFromRequest();
		Address addressToAdd = retAddressForm.get();
		personService.addAddressForPerson(pid, addressToAdd);
		return ok(professor_profile.render(profService.findProfessorById(pid)));
	}
	
	public static Result submitNewEmail(){
		Form<Email> retEmailForm = theEmailForm.bindFromRequest();
		Email emailToAdd = retEmailForm.get();
		personService.addEmailForPerson(pid, emailToAdd);
		return ok(professor_profile.render(profService.findProfessorById(pid)));
	}
	
	public static Result submitNewPhone(){
		Form<PhoneNumber> retPhoneForm = thePhoneForm.bindFromRequest();
		PhoneNumber phoneToAdd = retPhoneForm.get();
		personService.addPhoneForPerson(pid, phoneToAdd);
		return ok(professor_profile.render(profService.findProfessorById(pid)));		
	}
	
	
	

	
	/*****************************************************************/
	/**				PROFESSOR INVOKED!!!				*************/
	/****************************************************************/
	public static Result getProfessorInfoPR(String profId){
			pid = profId;
			Professor retProfessor = profService.findProfessorById(profId);
			return ok(professor_profile_pr.render(retProfessor));
		
	}
	
	/***********************************************************************/
	public static Result editPersonalInfoPR(String pid){
		Professor prof = profService.findProfessorById(pid);
		Form<Professor> profForm = form(Professor.class).fill(prof);
		theProfessorForm = profForm;
		return ok(Edits_Professor_Form_pr.render(prof, theProfessorForm));
	}
	
	public static Result editAddressPR(int aid){
		Professor prof = profService.findProfessorById(pid);
		Address addressToChange = personService.findAddressById(aid);
		Form<Address> addressForm = form(Address.class).fill(addressToChange);
		theAddressForm = addressForm;
		return ok(edit_professor_address_form_pr.render(prof, theAddressForm, roleType, aid));
	}

	
	public static Result editEmailPR(int eid){
		Professor prof = profService.findProfessorById(pid);
		Email email = personService.findEmailById(eid);
		Form<Email> emailForm = form(Email.class).fill(email);
		theEmailForm = emailForm;
		return ok(edit_professor_email_form_pr.render(prof, theEmailForm, roleType, eid));
	}
	
	public static Result editPhonePR(int phnid){
		Professor prof = profService.findProfessorById(pid);
		PhoneNumber phone = personService.findPhoneNumberById(phnid);
		Form<PhoneNumber> phoneForm = form(PhoneNumber.class).fill(phone);
		return ok(edit_professor_phone_form_pr.render(prof, phoneForm, roleType, phnid));
	}
	
	//Submit -- Edited Address
	public static Result submitEditedAddressPR(int aid){
		Form<Address> updatedAddressForm = theAddressForm.bindFromRequest();
		Address updatedAddress = updatedAddressForm.get();
		updatedAddress.setAddressId(aid);
		System.out.println(">>>>>>>>>>>>>>>>>" +pid);
		personService.modifyAddress(pid, updatedAddress);
		
		Professor retProfessor = profService.findProfessorById(pid);
		return ok(professor_profile_pr.render(retProfessor));
	}
	
	//Submit -- Edited Phone
	public static Result submitEditedPhonePR(int phnid){
		Form<PhoneNumber> updatedPhoneForm = thePhoneForm.bindFromRequest();
		PhoneNumber updatedPhone = updatedPhoneForm.get();
		updatedPhone.setPhoneNumberId(phnid);
		personService.modifyPhone(pid, updatedPhone);
		
		Professor retProfessor = profService.findProfessorById(pid);
		return ok(professor_profile_pr.render(retProfessor));
		
	}
	
	//Submit -- Edited Email
	public static Result submitEditedEmailPR(int eid){
		Form<Email> updatedPhoneForm = theEmailForm.bindFromRequest();
		Email updatedEmail = updatedPhoneForm.get();
		updatedEmail.setEmailId(eid);
		personService.modifyEmail(pid, updatedEmail);
		
		Professor retProfessor = profService.findProfessorById(pid);
		return ok(professor_profile_pr.render(retProfessor));	
	}
	
	//update personal information
	public static Result updateBasicProfessorPR(){
		Form<Professor> updatedProfessorForm = theProfessorForm.bindFromRequest();
		Professor prof = profService.findProfessorById(pid);
		if(updatedProfessorForm.hasErrors()){
			return badRequest(Edits_Professor_Form_pr.render(prof, updatedProfessorForm));
		}
		
		else{
			Professor updatedProfessor = updatedProfessorForm.get();
			String profId = updatedProfessor.getSsn();
			updatedProfessor.setID(profId);
			profService.modifyProfessorPersonalInformation(updatedProfessor);
			
			Professor retProfessor = profService.findProfessorById(profId);
			return ok(professor_profile_pr.render(retProfessor));
		}
	}
	
		
	/********************************************************/
	//DELETION METHODS
	public static Result delAddressPR(int aid){
		personService.removeAddress(aid);		
		return ok(professor_profile_pr.render(profService.findProfessorById(pid)));
	}
	
	public static Result delPhonePR(int phnId){
		personService.removePhone(phnId);
		return ok(professor_profile_pr.render(profService.findProfessorById(pid)));
	}
	
	public static Result delEmailPR(int eid){
		personService.removeEmail(eid);
		return ok(professor_profile_pr.render(profService.findProfessorById(pid)));
	}
	
	//ADD new address, email, or phone
	//DIRECT STUDENT to form for adding additional email, phone, or address
	public static Result createAddressPR(){
		Professor prof = profService.findProfessorById(pid);
		theAddressForm = form(Address.class);
		return ok(add_new_professor_address_form_pr.render(prof, theAddressForm, roleType));
	}
	
	public static Result createEmailPR(){
		Professor prof = profService.findProfessorById(pid);
		theEmailForm = form(Email.class);
		return ok(add_new_professor_email_form_pr.render(prof, theEmailForm, roleType));
	}
	
	public static Result createPhonePR(){
		Professor prof = profService.findProfessorById(pid);
		thePhoneForm = form(PhoneNumber.class);
		return ok(add_new_professor_phone_form_pr.render(prof, thePhoneForm, roleType));
	}
	
	//Submit additional address, email, or phone
	public static Result submitNewAddressPR(){
		Form<Address> retAddressForm = theAddressForm.bindFromRequest();
		Address addressToAdd = retAddressForm.get();
		personService.addAddressForPerson(pid, addressToAdd);
		return ok(professor_profile_pr.render(profService.findProfessorById(pid)));
	}
	
	public static Result submitNewEmailPR(){
		Form<Email> retEmailForm = theEmailForm.bindFromRequest();
		Email emailToAdd = retEmailForm.get();
		personService.addEmailForPerson(pid, emailToAdd);
		return ok(professor_profile_pr.render(profService.findProfessorById(pid)));
	}
	
	public static Result submitNewPhonePR(){
		Form<PhoneNumber> retPhoneForm = thePhoneForm.bindFromRequest();
		PhoneNumber phoneToAdd = retPhoneForm.get();
		personService.addPhoneForPerson(pid, phoneToAdd);
		return ok(professor_profile_pr.render(profService.findProfessorById(pid)));		
	}
	
	
	//new addition 12/2/2013
	public static Result profList(String sortBy, String order, String filter) {
		List<Professor> allProfessors = profService.retrieveFilteredProfessors(filter);
        return ok(professor_edit.render(allProfessors, sortBy, order, filter));
	}
	
	public static Result blank2(){
		return redirect (routes.EditProfessorController.profList("lastName", "asc", ""));
	}
	
	public static Result getProfessorInfo2(String profId){
			pid = profId;
			Professor retProfessor = profService.findProfessorById(profId);
			return ok(professor_profile.render(retProfessor));
	}
	//end 12/2/2013
	
	
	/*
	 * Method for CANCEL link -> redirect to 'Personal Profile' homepage
	 */	
	public static Result backToProfProfileHome(){
		Professor rp = profService.findProfessorById(pid);
		return ok(professor_profile.render(rp));
	}	
	
	
	public static Result backToProfProfileHomePR(){
		Professor rp = profService.findProfessorById(pid);
		return ok(professor_profile_pr.render(rp));
	}	
}
