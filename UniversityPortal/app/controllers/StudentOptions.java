package controllers;

import play.mvc.*;

import views.html.*;

public class StudentOptions extends Controller {
  
    public static Result loadOptions() {
        return ok(student_options.render());
    }
  
}