package com.universityregistration.model.User;
import java.util.*;

public class Person {

		private String firstName;
		private String lastName;
		private String dob;
		private String ssn;
		private String uID;
		private List<Address> address = new ArrayList<Address>();
		private List<PhoneNumber> phonenumber = new ArrayList<PhoneNumber>();
		private List<Email> email = new ArrayList<Email>();

		public Person() {}
		
		public Person(String firstName, String lastName, String dob, String ssn){
			this.firstName = firstName;
			this.lastName = lastName;
			this.dob = dob;
			this.ssn = ssn;
		}
		
		public void setFirstName(String fname){
			this.firstName = fname;
		}
		public String getFirstName(){
			return firstName;
		}
		
		public void setLastName(String lname){
			this.lastName = lname;
		}
		public String getLastName(){
			return lastName;
		}
		
		public void setDob(String bdate){
			this.dob = bdate;
		}
		public String getDob(){
			return dob;
		}
		
		public void setSsn(String ssn){
			this.ssn = ssn;
		}
		public String getSsn(){
			return ssn;
		}
		
		public void setID(String id){
			this.uID = id;
		}
		public String getID(){
			return uID;
		}
		
		public void setAddress(List<Address> address){
			this.address = address;
		}
		public List<Address> getAddress(){
			return address;
		}

		public void setPhoneNumber(List<PhoneNumber> phonenumber){
			this.phonenumber = phonenumber;
		}
		public List<PhoneNumber> getPhoneNumber(){
			return phonenumber;
		}

		public void setEmail(List<Email> email){
			this.email = email;
		}
		public List<Email> getEmail(){
			return email;
		}

		public String toString(){
			return (firstName + " " + lastName);
		}	
}