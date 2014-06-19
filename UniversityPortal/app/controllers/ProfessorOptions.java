package controllers;

import play.mvc.*;

import views.html.*;

public class ProfessorOptions extends Controller {
  
    public static Result loadOptions() {
        return ok(professor_options.render());
    }
  
}