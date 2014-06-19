package com.universityregistration.model.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import com.universityregistration.dal.DBHelper;
import com.universityregistration.dal.PersonDAO;
import com.universityregistration.model.Course.*;
import com.universityregistration.model.Service.*;
import com.universityregistration.model.User.*;

public class PersonService {
    private Address address = new Address();
    private PhoneNumber phone = new PhoneNumber();
    private Email email = new Email();
    private Person person = new Person();
    
    PersonDAO personDAO = new PersonDAO();
    
    //Modifying personal information
    public void changeFirstName(String firstName){
        person.setFirstName(firstName);
    }
    
    public void changeLastName(String lastName){
        person.setLastName(lastName);
    }
    
    public void changeDob(String Dob){
         person.setDob(Dob);
    }
    
    public void changeId(String id){
        person.setID(id);
    }
    
    public void changeSsn(String ssn){
        person.setSsn(ssn);
    }
    
    public void changeAddress(Address newAddress){
    	address.setAddressId(newAddress.getAddressId());
    	address.setStreet(newAddress.getStreet());
    	address.setCity(newAddress.getCity());
    	address.setState(newAddress.getCity());
    	address.setZip(newAddress.getZip());
    }
    
    public void changeEmail(Email newEmail){
    	email.setEmailId(newEmail.getEmailId());
    	email.setEmail(newEmail.getEmail());
    	email.setType(newEmail.getType());
    	
    }
    
    public void changePhoneNumber(PhoneNumber newPhone){
    	phone.setPhoneNumberId(newPhone.getPhoneNumberId());
    	phone.setPhonenumber(newPhone.getPhonenumber());
    	phone.setType(newPhone.getType());
    }
    
    /** personal data methods for students */
    
    public void addAddressForPerson(String studId, Address address){
    	personDAO.insertPersonAddresses(studId, address);
    }
    
    
    public void addEmailForPerson(String studId, Email email){
    	personDAO.insertPersonEmails(studId, email);
    }
    
    public void addPhoneForPerson(String studId, PhoneNumber phone){
    	personDAO.insertPersonPhonenumbers(studId, phone);
    }
    
    /*** MODIFY SINGEL PERSONAL RECORD FOR A PERSON ****/
    public void modifyAddress(String pid, Address address){
    	personDAO.updatingPersonAddresses(pid, address);
    }
    
    public void modifyPhone(String pid, PhoneNumber phone){
    	personDAO.updatingPersonPhoneNumbers(pid, phone);
    }
    
    public void modifyEmail(String pid, Email email){
    	personDAO.updatingPersonEmails(pid, email);
    }
    
    
    
    
    
    
    
    
    //DELETE
    public void removeAddress(int aid){
    	personDAO.deleteAddress(aid);
    }
    
    public void removeEmail(int eid){
    	personDAO.deleteEmail(eid);
    }
    
    public void removePhone(int phnId){
    	personDAO.deletePhone(phnId);
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    /************************************************************************/
    public Address findAddressById(int aid){
    	return personDAO.getAddressById(aid);

    }
    
    public PhoneNumber findPhoneNumberById(int phoneId){
        return personDAO.getPhoneById(phoneId);
    }
    
    public Email findEmailById(int emailId){
        return personDAO.getEmailById(emailId);
    }
    
    /******************* GET uId fro given address,phone,email id ***********************/
    public String findUidForAddId(int addId){
    	return personDAO.getUserIdForAddressId(addId);
    }
    
    public String findUidForEmailId(int emId){
    	return personDAO.getUserIdForEmailId(emId);
    }
    
    public String findUidForPhoneId(int phId){
    	return personDAO.getUserIdForPhoneId(phId);
    }
    
    
    
}
