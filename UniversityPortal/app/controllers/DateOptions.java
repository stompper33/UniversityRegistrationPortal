package controllers;

import play.mvc.*;

import views.html.*;

public class DateOptions extends Controller {
  
    public static Result loadOptions() {
        return ok(regDates_options.render());
    }
  
}