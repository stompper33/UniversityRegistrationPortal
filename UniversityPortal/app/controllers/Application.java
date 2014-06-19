package controllers;

import play.mvc.*;
import play.data.*;
import static play.data.Form.*;
import views.html.*;

import com.universityregistration.model.Service.UserService;
import com.universityregistration.model.User.User;

public class Application extends Controller {
	
	private static UserService userServ = new UserService();
	final static Form<User> userForm = form(User.class);
    public static Result index() {
        return ok(index.render(userForm));
    }  
	
	//this is only works for the one ADMIN
	public static Result goToAmdinDash(){
		User admin = userServ.getUserById("staff");
		return ok(admin_dash.render(admin));
	}
}