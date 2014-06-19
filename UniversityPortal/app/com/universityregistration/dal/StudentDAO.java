package com.universityregistration.dal;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

import com.universityregistration.model.BillReport.BillReport;
import com.universityregistration.model.Course.Course;
import com.universityregistration.model.Course.CourseSection;
import com.universityregistration.model.GradeReport.GradeReport;
import com.universityregistration.model.Service.StudentService;
import com.universityregistration.model.User.Address;
import com.universityregistration.model.User.Email;
import com.universityregistration.model.User.PhoneNumber;
import com.universityregistration.model.User.Student;

public class StudentDAO {
	
	private PersonDAO personDAO = new PersonDAO();  //for updating personal information
	private GradeReportDAO gradeReportDAO = new GradeReportDAO();
	private BillReportDAO billReportDAO = new BillReportDAO();
	
	
	//CREATE A NEW STUDENT - INSERT A NEW STUDENT RECORD IN STUDENT TABLE
	public void insertNewStudent(Student newStudent){
		Connection con = DBHelper.getConnection();
		PreparedStatement studPst = null;
		
		try{
			String insertStudentQuery = "INSERT INTO Student(uid, first_name, last_name, dob, ssn, major, class_level)" +
							" VALUES (?, ?, ?, ?, ?, ?, ?)";
			studPst = con.prepareStatement(insertStudentQuery);
			studPst.setString(1, newStudent.getSsn());
			studPst.setString(2,  newStudent.getFirstName());
			studPst.setString(3, newStudent.getLastName());
			studPst.setString(4, newStudent.getDob());
			studPst.setString(5, newStudent.getSsn());
			studPst.setString(6, newStudent.getMajor());
			studPst.setInt(7,  newStudent.getClassLevel());
			studPst.executeUpdate();
			
			//close to save
			studPst.close();
			con.close();
			
			//Obtain Id for student just inserted. Needed for upating address,email,phone, 
			//and is_taking tables			
			String sid = newStudent.getSsn();
			
			//processing student addresses
			for(Address a : newStudent.getAddress()){
				personDAO.insertPersonAddresses(sid, a);
			}
			
			//processing student phonenumbers
			for(PhoneNumber p: newStudent.getPhoneNumber()){
				personDAO.insertPersonPhonenumbers(sid, p);
			}
			
			//processing student emails
			for(Email e : newStudent.getEmail()){
				personDAO.insertPersonEmails(sid, e);
			}
			
		} catch(SQLException ex){
			System.err.println("StudentDAO: Threw a SQLException inserting student object.");
			System.out.println(ex.getMessage());			
		}finally{
			try{
				if(studPst != null)
					studPst.close();
				if (con != null){
					System.out.println("Database closed after inserting student");
					con.close();
				}
			}catch (SQLException ex){
				System.err.println("StudentDAO: Threw a SQLException inserting student object.");
				System.out.println(ex.getMessage());
			}
		}		
	}

	//REGISTER FOR COURSE
	public void registerStudentForCourse(Student student, CourseSection course){
		System.out.println(student.getID());
		System.out.println(course.getCourseSectionId());
		Connection conn = DBHelper.getConnection();
		PreparedStatement studPst = null;
		
		try{
			//register student for course;
			String registerStudentToCourseQuery = "INSERT INTO is_taking (student_id, course_section_id) VALUES (?, ?)";
			studPst = conn.prepareStatement(registerStudentToCourseQuery);
			studPst.setString(1, student.getID());
			studPst.setInt(2, course.getCourseSectionId());
			studPst.executeUpdate();
			
			studPst.close();
			
		} catch(SQLException ex){
			System.err.println("StudentDAO: Threw a SQLException Registering the student object.");
			System.out.println(ex.getMessage());			
		} finally{
			try{
				if (studPst != null)
					studPst.close();
				if (conn != null){
					System.out.println("Database closed after registering student to course");
					conn.close();
				}
			} catch (SQLException ex){
				System.err.println("StudentDAO: Threw a SQLException Registering the student object.");
				System.out.println(ex.getMessage());
			}
		}
		
		//Create grade report for student in Grade_Report table
		gradeReportDAO.initializeStudentGradeReport(student, course);  //helper method
		
		String term = gradeReportDAO.getTermForCourseSection(course); //helper method
		int year = gradeReportDAO.getYearForCourseSection(course);
	
		Student updatedVersion = getStudentById(student.getID());
		
		if(billReportDAO.bpExists(student.getID(), term, year)){
			System.out.println("----> bill report record exists! ");
			billReportDAO.updateBillReport(updatedVersion, term, year);
		}
		else{
			System.out.println("------> bill report DOES NOT exist!");
			billReportDAO.initilizeStudentBillReport(updatedVersion, term, year);
		}
		
	}// End of registerStudentForCourse
		
	
	//UNREGISTER FOR COURSE -- CURRENT
	public void withdrawlStudentFromCourse(Student student, CourseSection course){
			
			Connection conn = DBHelper.getConnection();
			PreparedStatement studPst = null;
			
			try{
				//register student for course;
				String registerStudentToCourseQuery = "DELETE FROM is_taking WHERE student_id = '" + student.getID() +
						"' AND course_section_id = " + course.getCourseSectionId();
				studPst = conn.prepareStatement(registerStudentToCourseQuery);
				studPst.executeUpdate();
			} catch(SQLException ex){
				System.err.println("StudentDAO: Threw a SQLException delete curr course the student object.");
				System.out.println(ex.getMessage());				
			} finally{
				try{
					if (studPst != null)
						studPst.close();
					if (conn != null){
						System.out.println("database closed after unregistering student");
						conn.close();
					}
				} catch (SQLException ex){
					System.err.println("StudentDAO: Threw a SQLException delete curr course the student object.");
					System.out.println(ex.getMessage());
				}
			}		
			
			//update grade_report table after deletion
			deleteGradeReport(student, course);  //helper method
			
			Student updatedVersion = getStudentById(student.getID());
			String term = gradeReportDAO.getTermForCourseSection(course); //helper method
			int year = gradeReportDAO.getYearForCourseSection(course);			
			
			if(billReportDAO.bpExists(student.getID(), term, year)){
				System.out.println("----> bill report record exists! ");
				billReportDAO.updateBillReport(updatedVersion, term, year);
			}
			else{
				System.out.println("------> bill report DOES NOT exist!");
				billReportDAO.initilizeStudentBillReport(updatedVersion, term, year);
			}			
			
		}// End of withdrawlStudentFromCourse
	
	//Removes grade report record to reflect student dropping a course
	public void deleteGradeReport(Student student, CourseSection course){
		
		Connection con = DBHelper.getConnection();
		PreparedStatement grDel = null;
		
		try{
			
			String deleteGradeReportQuery = "DELETE FROM grade_report WHERE student_id = '" 
						+ student.getID() + "' AND section_id = "
						+ course.getCourseSectionId();
			
			grDel = con.prepareStatement(deleteGradeReportQuery);
			grDel.executeUpdate();
			
			//close
		}
		catch(SQLException ex) {
		}
		finally {
			try{
				if(grDel != null){
					grDel.close();
				}
				if(con != null){
					System.out.println("Database closed after deleting grade report");
					con.close();
				}
			}
			catch(SQLException ex){
				System.err.println("StudentDAO Threw a SQLException deleting grade report object");
				System.err.println(ex.getMessage());
			}
		}
	}
		
		
	//Add course to student's secondary course listing
	public void addSecondaryCourse(Student student, CourseSection course)
		{
			
			Connection con = DBHelper.getConnection();
			PreparedStatement studPst = null;
			
			try{
			//Add student to  table
			String type = "secondary";
			String regQuery = "INSERT INTO might_take (student_id, course_section_id, type) VALUES (?, ?, ?)";
			studPst = con.prepareStatement(regQuery);
			studPst.setString(1,  student.getID());
			studPst.setInt(2, course.getCourseSectionId());
			studPst.setString(3, type);
			studPst.execute();
			} 
			
			catch(SQLException ex){
				System.err.println("StudentDAO: Threw a SQLException add sec the student object.");
				System.err.println(ex.getMessage());				
			}
			finally{
				
				try{
					if (studPst!=null){
						studPst.close();
					}
					if(con != null){
						System.out.println("database closed after adding secondary course");
						con.close();
					}
				} catch(SQLException ex){
					
					System.err.println("StudentDAO: Threw a SQLException add sec the student object.");
					System.err.println(ex.getMessage());
				}
				finally{}
			}

		}//end of addSecondaryCourse


	//Add remove course from student's secondary course listing
	public void removeSecondaryCourse(Student student, CourseSection course){
			
			Connection con = DBHelper.getConnection();
			PreparedStatement studPst = null;
			
			try{
			//Add student to  table
			String type = "secondary";
			String regQuery = "DELETE FROM might_take WHERE student_id = '" + student.getID() + "' AND course_section_id =" 
					+ course.getCourseSectionId() + " AND type= '" + type + "'";
			studPst = con.prepareStatement(regQuery);
			studPst.executeUpdate();
			} 
			
			catch(SQLException ex){
				System.err.println("StudentDAO: Threw a SQLException remove sec the student object.");
				System.err.println(ex.getMessage());				
			}
			finally{
				
				try{
					if (studPst!=null){
						studPst.close();
					}
					if(con != null){
						System.out.println("Database closed after removing secondary course");
						con.close();
					}
				} catch(SQLException ex){
					
					System.err.println("StudentDAO: Threw a SQLException remove sec the student object.");
					System.err.println(ex.getMessage());
				}
				finally{}
			}
		}// End of removeSecondaryCourse (...) method	
		
	
	//GET STUDENT BY ID
	public Student getStudentById(String studentId){
		
		Connection con = DBHelper.getConnection();
		Statement stmt = null;
		
		try{
			//Get student
			
			//1st. create student obj based on student table
			stmt = con.createStatement();
			String selectStudentQuery = "SELECT uId, first_name, last_name, dob, ssn, major, class_level FROM student WHERE uId = '"
					+ studentId + "'";
			ResultSet studRS = stmt.executeQuery(selectStudentQuery);
			System.out.println("StudentDAO: ******** Query:" + selectStudentQuery);
			
			//Get Student
			Student student = new Student();
			while(studRS.next()){
				student.setID(studRS.getString("uId"));
				student.setFirstName(studRS.getString("first_name"));
				student.setLastName(studRS.getString("last_name"));
				student.setDob(studRS.getString("dob"));
				student.setSsn(studRS.getString("ssn"));
				student.setMajor(studRS.getString("major"));
				student.setClassLevel(studRS.getInt("class_level"));
				
			}
			
			//close to manage resources
			studRS.close();
			
			String type = "student";
			//Get addresses---------------------------
			String selectAddressesQuery = "SELECT street, city, state, zip, address_id FROM Address WHERE student_id = '" + studentId +
					"' AND type = '" + type + "'";
			ResultSet addRS =  stmt.executeQuery(selectAddressesQuery);
			List<Address> studentAddresses = new ArrayList<Address>(); //to store addresses for student
			
			//Iterate through result set and create address objects for each row returned
			while(addRS.next()){
				Address address = new Address();
				address.setStreet(addRS.getString("street"));
				address.setCity(addRS.getString("city"));
				address.setState(addRS.getString("state"));
				address.setZip(addRS.getString("zip"));
				address.setAddressId(addRS.getInt("address_id"));
				
				//add to student's Address list
				studentAddresses.add(address);
			}//End of getting addresses ------------------------
			
			addRS.close();
			
			//Get phone ------------------------------
			String selectPhoneQuery = "SELECT phone_id, phone_number FROM phone WHERE student_id = '" + studentId + "'" +
					" AND type = '" + type + "'";
			ResultSet phoneRS = stmt.executeQuery(selectPhoneQuery);
			List<PhoneNumber> studentPhoneNos = new ArrayList<PhoneNumber>(); // to store phone numbers for student
			
			//Iterate through result set and create phone objects for each row returned
			while(phoneRS.next()){
				PhoneNumber phone = new PhoneNumber();
				phone.setPhoneNumberId(phoneRS.getInt("phone_id"));
				phone.setPhonenumber(phoneRS.getString("phone_number"));
				
				//add to student's phonenumber list
				studentPhoneNos.add(phone);
			}
			
			phoneRS.close();
			//End of getting phone-numbers ---------------------------------
			
			//Get emails -----------------------------------------
			String selectEmailQuery = "SELECT email_id, email FROM email WHERE student_id = '" + studentId +
					"' AND type = '" + type + "'";
			ResultSet emailRS = stmt.executeQuery(selectEmailQuery);
			List<Email> studentEmails = new ArrayList<Email>();
			
			//Iterate through the result set and create Email objects for each row returned
			while(emailRS.next()){
				Email email = new Email();
				email.setEmailId(emailRS.getInt("email_id"));
				email.setEmail(emailRS.getString("email"));
				
				//add to student's email list
				studentEmails.add(email);
			}
			emailRS.close();
			//End of getting emails ---------------
			
			//Set student's addresses, phone numbers, and email attributes
			student.setAddress(studentAddresses);
			student.setPhoneNumber(studentPhoneNos);
			student.setEmail(studentEmails);
			
			
			
			String pctype="primary";
			String sctype = "secondary";
			CourseDAO courseDAO = new CourseDAO();
			List<CourseSection> studentSecondaryCourses = new ArrayList<CourseSection>();
			List<CourseSection> studentCurrentCourses = new ArrayList<CourseSection>();
			
			
			//Now get/set student's SECONDARY courses
			String getSecondaryCourseQuery = "SELECT course_section_id FROM might_take WHERE student_id = '" +
						studentId + "'" + " AND type = '" + sctype + "'";
			ResultSet secondaryRS = stmt.executeQuery(getSecondaryCourseQuery);
			
			//Iterate for secondary courses now
			while(secondaryRS.next()){
				CourseSection sc = courseDAO.getCourseSectionById(secondaryRS.getInt("course_section_id"));
				studentSecondaryCourses.add(sc);
			}//end of setting/getting secondary-courses
			
			secondaryRS.close();
			
			//Set/get CURRENT courses
			String getCurrentCoursesQuery = "SELECT course_section_id FROM is_taking WHERE student_id = '" +
							studentId + "'";
			ResultSet currentRS = stmt.executeQuery(getCurrentCoursesQuery);
			while(currentRS.next()){
				CourseSection cc = courseDAO.getCourseSectionById(currentRS.getInt("course_section_id"));
				studentCurrentCourses.add(cc);
			}
			
			currentRS.next();
			
			student.setSecondaryCourses(studentSecondaryCourses);
			student.setCurrentCourses(studentCurrentCourses);

			return student;
			
		}
		catch (SQLException ex){
			System.err.println("StudentDAO: Threw a SQLException retrieving the student object.");
			System.err.println(ex.getMessage());
			ex.printStackTrace();
		}finally{
			try{
				if(stmt != null)
					stmt.close();
				if(con != null){
					System.out.println("Database closed after retrieving student");
					con.close();
				}
			}catch (SQLException ex){
				System.err.println("StudentDAO: Threw a SQLException retrieving the student object.");
				System.err.println(ex.getMessage());
				ex.printStackTrace();
			}
		}
		
		return null;
	} // End of getStudentById() method
	
	
	public Student getSimpleStudentById(String studentId){
		
		Connection con = DBHelper.getConnection();
		Statement stmt = null;
		
		try{
			//Get student
			
			//1st. create student obj based on student table
			stmt = con.createStatement();
			String selectStudentQuery = "SELECT uId, first_name, last_name, dob, ssn, major, class_level FROM student WHERE uId = '"
					+ studentId + "'";
			ResultSet studRS = stmt.executeQuery(selectStudentQuery);
			System.out.println("StudentDAO: ******** Query:" + selectStudentQuery);
			
			//Get Student
			Student student = new Student();
			while(studRS.next()){
				student.setID(studRS.getString("uId"));
				student.setFirstName(studRS.getString("first_name"));
				student.setLastName(studRS.getString("last_name"));
				student.setDob(studRS.getString("dob"));
				student.setSsn(studRS.getString("ssn"));
				student.setMajor(studRS.getString("major"));
				student.setClassLevel(studRS.getInt("class_level"));
				
			}
			
			//close to manage resources
			studRS.close();
					
			return student;
			
		}
		catch (SQLException ex){
			System.err.println("StudentDAO: Threw a SQLException retrieving simple student object.");
			System.err.println(ex.getMessage());
			ex.printStackTrace();
		}finally{
			try{
				if(stmt != null)
					stmt.close();
				if(con != null){
					System.out.println("Database closed after retrieving student");
					con.close();
				}
			}catch (SQLException ex){
				System.err.println("StudentDAO: Threw a SQLException retrieving simple student object.");
				System.err.println(ex.getMessage());
				ex.printStackTrace();
			}
		}
		
		return null;
	}
	
	//**************************** HELPER FUNCTIONS **********************************************
	//********************************************************************************************
	
	public int getStudentIdBySsn(String ssn) throws SQLException{
		
		Connection con = DBHelper.getConnection();
		Statement st = null;
		
		try{
			st = con.createStatement();
			String selectStudIdQuery = "SELECT uId FROM student WHERE ssn = '" + ssn + "'";
			ResultSet studIdRS = st.executeQuery(selectStudIdQuery);
			
			while(studIdRS.next()){
				int thisStudentId = studIdRS.getInt("uId");	
				return thisStudentId;			
			}		
			
			studIdRS.close();
		}
	    catch (SQLException se) {
		      System.err.println("Retrieving Student Id: Threw a SQLException retrieving the grade report object.");
		      System.err.println(se.getMessage());
		      se.printStackTrace();
		 }finally{
			 if(st != null)
				 st.close();
			 if(con != null){
				 System.out.println("database closed after grade report object closed");
				 con.close();
			 }
		 }
		    
		 return (Integer) null;
	
	}

	//**************************************************************************************************
	//*********************************** end of helper functions **************************************

		/*****************************************************************************/
		/*************************** 12.02.2013 UPDATE *******************************/
		/*****************************************************************************/
		public List<Student> searchStudents (String filter){
			String filt = "%" + filter + "%";
			Connection conn = DBHelper.getConnection();
			Statement stmt = null;
				try{
					stmt = conn.createStatement();
					String filterQuery = "SELECT uid, first_name, last_name, dob, ssn, major, class_level FROM student WHERE" 
					+ " last_name LIKE '" + filt + "'";
					ResultSet filterRS = stmt.executeQuery(filterQuery);
					List<Student> retStudentList = new ArrayList<Student>();
					while(filterRS.next()){
						Student s = new Student();
						s.setID(filterRS.getString("uId"));
						s.setFirstName(filterRS.getString("first_name"));
						s.setLastName(filterRS.getString("last_name"));
						s.setDob(filterRS.getString("dob"));
						s.setSsn(filterRS.getString("ssn"));
						s.setMajor(filterRS.getString("major"));
						s.setClassLevel(filterRS.getInt("class_level"));
						retStudentList.add(s);
					}
					filterRS.close();
					return retStudentList;
					
				}catch (SQLException ex){
					System.err.println("StudentDAO: Threw a SQLException retrieving filtered student list.");
					System.err.println(ex.getMessage());
					ex.printStackTrace();
					}finally{
						try{
							if(stmt != null){
							stmt.close();
							}
							if(conn != null){
							conn.close();
							}
						}catch (SQLException ex){
							System.err.println("StudentDAO: Threw a SQLException retrieving filtered student list.");
							System.err.println(ex.getMessage());
							ex.printStackTrace();
						}
					}
			return null;
		}
		
		//get all students in db
		public List<Student> getAllStudents(){
			Connection conn = DBHelper.getConnection();
			Statement stmt = null;
			try{
				stmt=conn.createStatement();
				String getStudents =  "SELECT uid FROM student";
				ResultSet allStudRS = stmt.executeQuery(getStudents);
				List<Student> retStudentList = new ArrayList<Student>();
			while(allStudRS.next()){
				Student s = getStudentById(allStudRS.getString("uid"));	
				retStudentList.add(s);
			}
				allStudRS.close();
				return retStudentList;
			}catch (SQLException ex){
				System.err.println("StudentDAO: Threw a SQLException retrieving ALL students.");
				System.err.println(ex.getMessage());
				ex.printStackTrace();
			}finally{
				try{
					if(stmt != null)
					stmt.close();
					if(conn != null){
					conn.close();
					}
				}catch (SQLException ex){
					System.err.println("StudentDAO: Threw a SQLException retrieving ALL students.");
					System.err.println(ex.getMessage());
					ex.printStackTrace();
				}
			}
			return null;
		}

		 /****************************** 12.03.2013 ************************************/
		public boolean studentExists(String sid){
			Connection conn = DBHelper.getConnection();
			Statement stmt = null;
			boolean res = false;
		
			try{
				stmt = conn.createStatement();
				String exists = "SELECT uid FROM student WHERE uid = '" 
						+ sid + "'";
				ResultSet existsRS = stmt.executeQuery(exists);
				
				if(existsRS.next()){
					res= true;
				}
				
				existsRS.close();
				stmt.close();
				conn.close();
				
			}catch(SQLException ex){
				System.err.println("SQLException:Threw exception while checking professor existence");
				System.err.println(ex.getMessage());
			}
			finally{
				try{
					if (stmt != null)
						stmt.close();
					if (conn != null)
						conn.close();
				}catch(SQLException ex){
					System.err.println("SQLException:Threw exception while checking professor existence");
					System.err.println(ex.getMessage());
				}
			}
			return res;
		}		
		
}