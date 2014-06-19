package controllers;

import play.mvc.*;
import play.data.*;
import static play.data.Form.*;
import views.html.*;
import views.html.*;

import com.universityregistration.model.Service.StudentService;
import com.universityregistration.model.Service.ProfessorService;
import com.universityregistration.model.Service.UserService;
import com.universityregistration.model.User.User;
import com.universityregistration.model.User.Professor;
import com.universityregistration.model.User.Student;
import java.util.*;

public class UserLogin extends Controller{
  
	final static Form<User> userForm = form(User.class);
	private static UserService userServ = new UserService();
	private static StudentService studServ = new StudentService();
	private static ProfessorService profServ = new ProfessorService();

    public static Result submit() {
        Form<User> filledForm = userForm.bindFromRequest();
		User u = filledForm.get();
		String user = u.getUsername();
		String pass = u.getPassword();
        if(filledForm.hasErrors()) {
            return badRequest(index.render(userForm));
        }
		else{
			if(userServ.userExists(user, pass)){
				User retUser = userServ.getUserByUsernamePassword(user, pass);
				
				if(retUser.getRole().equals("staff")){
					Professor retProfessor = profServ.findProfessorById(retUser.getUid());
					return ok(professor_dash.render(retProfessor));
				}
				else if(retUser.getRole().equals("student")){
					Student retStudent = studServ.findStudentById(retUser.getUid());
					return ok(student_dash.render(retStudent));
				}
				else if(retUser.getRole().equals("admin")){
					return ok(admin_dash.render(retUser));
					}
			}
				return ok(index.render(userForm));
		}
    }
    
    public static Result studentHome(String studId){
    	Student retStudent = studServ.findStudentById(studId);
    	return ok(student_dash.render(retStudent));
    }
    
    public static Result professorHome(String profId){
    	Professor retProfessor = profServ.findProfessorById(profId);
    	return ok(professor_dash.render(retProfessor));
    }
    
    public static Result adminHome(String uId){
    	User retUser = userServ.getUserById(uId);
    	return ok(admin_dash.render(retUser));
    }    
}