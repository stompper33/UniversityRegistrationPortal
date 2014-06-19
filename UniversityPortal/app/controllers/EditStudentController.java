package controllers;
import java.util.List;

import play.mvc.*;
import play.data.*;
import static play.data.Form.*;
import views.html.*;
import models.*;

import com.universityregistration.model.User.Address;
import com.universityregistration.model.User.Email;
import com.universityregistration.model.User.PhoneNumber;
import com.universityregistration.model.User.Professor;

import com.universityregistration.model.Service.PersonService;
import com.universityregistration.model.Service.ProfessorService;
import com.universityregistration.model.Service.StudentService;
import com.universityregistration.model.User.Student;

public class EditStudentController extends Controller {
	
	private static StudentService studService = new StudentService();
	private static PersonService personService = new PersonService();
	final static String roleType = "student"; 
	private static String sid = "";
	
	//For Editing purposes
	private static Form<Student> theStudentForm = form(Student.class);
	private static Form<Address> theAddressForm = form(Address.class);
	private static Form<PhoneNumber> thePhoneForm = form(PhoneNumber.class);
	private static Form<Email> theEmailForm = form(Email.class);
	
	//forms for additional 
	
	//Describes the search form
	public static class Search{
		public String searchId;
	}

	
	
	/***********************************************************************/
	/**      			STUDENT PORTAL INVOKED
	/***********************************************************************/
	//Request from student 
	public static Result getStudentProfile(String pid){
		sid = pid;
		Student student = studService.findStudentById(pid);
			return ok(student_profile_sr.render(student));
	}	
	
	//EDIT STUDENT PERSONAL INFO -- 1
	public static Result editPersonalInfoSR(String pid){
		Student retStudent = studService.findStudentById(pid);
		Form<Student> studForm = form(Student.class).fill(retStudent);
		theStudentForm = studForm;
		return ok(Edits_Student_Form_sr.render(retStudent, theStudentForm));
	}
	
	//UPDATE PERSONAL INFORMATION FOR STUDENT  -- 2
	public static Result updateBasicStudentSR(){
		Student rStudent = studService.findStudentById(sid);
		Form<Student> updatedStudentForm = theStudentForm.bindFromRequest();
			
		if(updatedStudentForm.hasErrors()){
			return badRequest(Edits_Student_Form_sr.render(rStudent, updatedStudentForm));
		}
			
		else{
			Student updatedStudent = updatedStudentForm.get();
			String studId = updatedStudent.getSsn();			
			updatedStudent.setID(studId);
			studService.modifyStudentPersonalInformation(updatedStudent);
				
			Student retStudent = studService.findStudentById(studId);
			return ok(student_profile_sr.render(retStudent));
		}
	}
	
	//EDITING ADDRESS--------------------------
	public static Result editAddressSR(int aid){
		Address addressToChange = personService.findAddressById(aid);
		Form<Address> addressForm = form(Address.class).fill(addressToChange);
		theAddressForm = addressForm;
		Student retStudent = studService.findStudentById(sid);
		return ok(edit_address_form_sr.render(retStudent, theAddressForm, roleType, aid));
	}
	
	//Submit -- Edited Address
		public static Result submitEditedAddressSR(int aid){
			Form<Address> updatedAddressForm = theAddressForm.bindFromRequest();
			Address updatedAddress = updatedAddressForm.get();
			updatedAddress.setAddressId(aid);
			personService.modifyAddress(sid, updatedAddress);
			Student retStudent = studService.findStudentById(sid);
			return ok(student_profile_sr.render(retStudent));
		}
	
		//EDIT EMAIL
		public static Result editEmailSR(int eid){
			Email email = personService.findEmailById(eid);
			Form<Email> emailForm = form(Email.class).fill(email);
			theEmailForm = emailForm;
			Student retStudent = studService.findStudentById(sid);
			return ok(edit_email_form_sr.render(retStudent, theEmailForm, roleType, eid));
		}
		
		//Submit -- Edited Email
		public static Result submitEditedEmailSR(int eid){
			Form<Email> updatedPhoneForm = theEmailForm.bindFromRequest();
			Email updatedEmail = updatedPhoneForm.get();
			updatedEmail.setEmailId(eid);
			personService.modifyEmail(sid, updatedEmail);
			
			//Testing:
			Student retStudent = studService.findStudentById(sid);
			return ok(student_profile_sr.render(retStudent));	
		}
	
		
		public static Result editPhoneSR(int pid){
			PhoneNumber phone = personService.findPhoneNumberById(pid);
			Form<PhoneNumber> phoneForm = form(PhoneNumber.class).fill(phone);
			Student retStudent = studService.findStudentById(sid);
			return ok(edit_phone_form_sr.render(retStudent, phoneForm, roleType, pid));
		}
		
		//Submit -- Edited Phone
		public static Result submitEditedPhoneSR(int pid){
			Form<PhoneNumber> updatedPhoneForm = thePhoneForm.bindFromRequest();
			PhoneNumber updatedPhone = updatedPhoneForm.get();
			updatedPhone.setPhoneNumberId(pid);
			personService.modifyPhone(sid, updatedPhone);
			
			//TESTING:
			Student retStudent = studService.findStudentById(sid);
			return ok(student_profile_sr.render(retStudent));
			
		}
		
		
		//DELETION METHODS
		public static Result delAddressSR(int aid){
			personService.removeAddress(aid);		
			return ok(student_profile_sr.render(studService.findStudentById(sid)));
		}
				
		public static Result delPhoneSR(int pid){
			personService.removePhone(pid);
			return ok(student_profile_sr.render(studService.findStudentById(sid)));
		}
				
		public static Result delEmailSR(int eid){
			personService.removeEmail(eid);
			return ok(student_profile_sr.render(studService.findStudentById(sid)));
		}
				
		//ADD new address, email, or phone
		//DIRECT STUDENT to form for adding additional email, phone, or address
		public static Result createAddressSR(){
			theAddressForm = form(Address.class);
			Student retStudent = studService.findStudentById(sid);
			System.out.println("id: " + sid);
			System.out.println(retStudent.getFirstName() +" "+ retStudent.getLastName());
			return ok(add_new_address_form_sr.render(retStudent, theAddressForm, roleType));
		}
				
		public static Result createEmailSR(){
			theEmailForm = form(Email.class);
			Student retStudent = studService.findStudentById(sid);
			return ok(add_new_email_form_sr.render(retStudent, theEmailForm, roleType));
		}
				
		public static Result createPhoneSR(){
			thePhoneForm = form(PhoneNumber.class);
			Student retStudent = studService.findStudentById(sid);
			return ok(add_new_phone_form_sr.render(retStudent, thePhoneForm, roleType));
		}
				
		//Submit additional address, email, or phone
		public static Result submitNewAddressSR(){
			Form<Address> retAddressForm = theAddressForm.bindFromRequest();
			Address addressToAdd = retAddressForm.get();
			personService.addAddressForPerson(sid, addressToAdd);
			return ok(student_profile_sr.render(studService.findStudentById(sid)));
		}
				
		public static Result submitNewEmailSR(){
			Form<Email> retEmailForm = theEmailForm.bindFromRequest();
			Email emailToAdd = retEmailForm.get();
			personService.addEmailForPerson(sid, emailToAdd);
			return ok(student_profile_sr.render(studService.findStudentById(sid)));
		}
				
		public static Result submitNewPhoneSR(){
			Form<PhoneNumber> retPhoneForm = thePhoneForm.bindFromRequest();
			PhoneNumber phoneToAdd = retPhoneForm.get();
			personService.addPhoneForPerson(sid, phoneToAdd);
			return ok(student_profile_sr.render(studService.findStudentById(sid)));		
		}		

		
	/*************************************************************************/
						/** ADMIN INVOKED!!!
	/*************************************************************************/
	
	public static Result editPersonalInfo(String sid){
		Student stud = studService.findStudentById(sid);
		Form<Student> studForm = form(Student.class).fill(stud);
		theStudentForm = studForm;
		return ok(Edits_Student_Form.render(theStudentForm));
	}
	
	public static Result editAddress(int aid){		
		Address addressToChange = personService.findAddressById(aid);
		String studId = personService.findUidForAddId(aid);
		Form<Address> addressForm = form(Address.class).fill(addressToChange);
		theAddressForm = addressForm;
		return ok(edit_address_form.render(theAddressForm, roleType, aid, studId));
	}

	
	public static Result editEmail(int eid){
		Email email = personService.findEmailById(eid);
		Form<Email> emailForm = form(Email.class).fill(email);
		theEmailForm = emailForm;
		return ok(edit_email_form.render(theEmailForm, roleType, eid));
	}
	
	public static Result editPhone(int pid){
		PhoneNumber phone = personService.findPhoneNumberById(pid);
		Form<PhoneNumber> phoneForm = form(PhoneNumber.class).fill(phone);
		return ok(edit_phone_form.render(phoneForm, roleType, pid));
	}
	
	//Submit -- Edited Address
	public static Result submitEditedAddress(int aid){
		Form<Address> updatedAddressForm = theAddressForm.bindFromRequest();
		Address updatedAddress = updatedAddressForm.get();
		updatedAddress.setAddressId(aid);
		personService.modifyAddress(sid, updatedAddress);
		//TESTing:
		Student retStudent = studService.findStudentById(sid);
		return ok(student_profile.render(retStudent));
	}
	
	//Submit -- Edited Phone
	public static Result submitEditedPhone(int pid){
		Form<PhoneNumber> updatedPhoneForm = thePhoneForm.bindFromRequest();
		PhoneNumber updatedPhone = updatedPhoneForm.get();
		updatedPhone.setPhoneNumberId(pid);
		personService.modifyPhone(sid, updatedPhone);
		
		//TESTING:
		Student retStudent = studService.findStudentById(sid);
		return ok(student_profile.render(retStudent));
		
	}
	
	//Submit -- Edited Email
	public static Result submitEditedEmail(int eid){
		Form<Email> updatedPhoneForm = theEmailForm.bindFromRequest();
		Email updatedEmail = updatedPhoneForm.get();
		updatedEmail.setEmailId(eid);
		personService.modifyEmail(sid, updatedEmail);
		
		//Testing:
		Student retStudent = studService.findStudentById(sid);
		return ok(student_profile.render(retStudent));	
	}
	
	
	//UPDATE PERSONAL INFORMATION FOR STUDENT
	public static Result updateBasicStudent(){
		Form<Student> updatedStudentForm = theStudentForm.bindFromRequest();
		
		if(updatedStudentForm.hasErrors()){
			return badRequest(Edits_Student_Form.render(updatedStudentForm));
		}
		
		else{
			Student updatedStudent = updatedStudentForm.get();
			String studId = updatedStudent.getSsn();			
			updatedStudent.setID(studId);
			studService.modifyStudentPersonalInformation(updatedStudent);
			
			Student retStudent = studService.findStudentById(studId);
			return ok(student_profile.render(retStudent));
		}
	}
	
	
	
	
	/********************************************************/
	//DELETION METHODS
	public static Result delAddress(int aid){
		personService.removeAddress(aid);		
		return ok(student_profile.render(studService.findStudentById(sid)));
	}
	
	public static Result delPhone (int pid){
		personService.removePhone(pid);
		return ok(student_profile.render(studService.findStudentById(sid)));
	}
	
	public static Result delEmail(int eid){
		personService.removeEmail(eid);
		return ok(student_profile.render(studService.findStudentById(sid)));
	}
	
	//ADD new address, email, or phone
	//DIRECT STUDENT to form for adding additional email, phone, or address
	public static Result createAddress(){
		theAddressForm = form(Address.class);
		return ok(add_new_address_form.render(theAddressForm, roleType));
	}
	
	public static Result createEmail(){
		theEmailForm = form(Email.class);
		return ok(add_new_email_form.render(theEmailForm, roleType));
	}
	
	public static Result createPhone(){
		thePhoneForm = form(PhoneNumber.class);
		return ok(add_new_phone_form.render(thePhoneForm, roleType));
	}
	
	//Submit additional address, email, or phone
	public static Result submitNewAddress(){
		Form<Address> retAddressForm = theAddressForm.bindFromRequest();
		Address addressToAdd = retAddressForm.get();
		personService.addAddressForPerson(sid, addressToAdd);
		return ok(student_profile.render(studService.findStudentById(sid)));
	}
	
	public static Result submitNewEmail(){
		Form<Email> retEmailForm = theEmailForm.bindFromRequest();
		Email emailToAdd = retEmailForm.get();
		personService.addEmailForPerson(sid, emailToAdd);
		return ok(student_profile.render(studService.findStudentById(sid)));
	}
	
	public static Result submitNewPhone(){
		Form<PhoneNumber> retPhoneForm = thePhoneForm.bindFromRequest();
		PhoneNumber phoneToAdd = retPhoneForm.get();
		personService.addPhoneForPerson(sid, phoneToAdd);
		return ok(student_profile.render(studService.findStudentById(sid)));		
	}
	
	
	//added FOR ABE'S 12/2/2013
	public static Result studList(String sortBy, String order, String filter) {
		List<Student> allStudents = studService.retrieveFilteredStudents(filter);
        return ok(student_edit.render(allStudents, sortBy, order, filter));
	}
	
	public static Result blank2(){
		return redirect (routes.EditStudentController.studList("lastName", "asc", ""));
	}
	
	public static Result getStudentInfo2(String studId){
			sid = studId;
			Student retStudent = studService.findStudentById(studId);
			return ok(student_profile.render(retStudent));
	}
	
	
	/*
	 * Method for CANCEL link -> redirect to 'Personal Profile' homepage
	 */
	public static Result backToStudProfileHome(){
		Student rs = studService.findStudentById(sid);
		return ok(student_profile.render(rs));
	}
	
	public static Result backToStudProfileHomeSR(){
		Student rs = studService.findStudentById(sid);
		return ok(student_profile_sr.render(rs));
	}
}
