package controllers;
import play.data.*;
import static play.data.Form.*;
import play.mvc.*;
import com.universityregistration.model.Service.AdminService;
import views.html.*;
import com.universityregistration.model.RegDates.*;
public class DatesController extends Controller {
	
	final static Form<SetDatesForm> setDatesForm = form (DatesController.SetDatesForm.class);
	public static AdminService adminServ = new AdminService();
  
    public static Result setDatesBlank() {
        return ok(courseDates_form.render(setDatesForm));
    }

	public static Result submitDates(){
        Form<SetDatesForm> filledForm = setDatesForm.bindFromRequest();

        if(filledForm.hasErrors()) {
            return badRequest(courseDates_form.render(filledForm));
        } 
		else
		{
			SetDatesForm datesForm = filledForm.get();
			int catId = datesForm.catalogId;
			RegistrationDates rd = new RegistrationDates();
			
			java.sql.Date startDay = java.sql.Date.valueOf(datesForm.startDate);
			java.sql.Date endDay = java.sql.Date.valueOf(datesForm.endDate);
			java.sql.Date regOpen = java.sql.Date.valueOf(datesForm.firstDayToRegister);
			java.sql.Date regClose = java.sql.Date.valueOf(datesForm.lastDayToRegister);
			java.sql.Date dropDay = java.sql.Date.valueOf(datesForm.lastDayToDropClasses);
			
			rd.setEndDate(endDay);
			rd.setFirstDayToRegister(regOpen);
			rd.setLastDayToDropClasses(dropDay);
			rd.setLastDayToRegiter(regClose);
			rd.setStartDate(startDay);
			
			if(!adminServ.datesAlreadySet(catId))
				adminServ.setDates(rd, catId);
			else
				adminServ.updateDates(rd, catId);
			
            return ok(courseDateSubmission.render(rd, catId, datesForm));
        }	
	}
	
	public static class SetDatesForm{
		public String lastDayToRegister;
		public String firstDayToRegister;
		public String lastDayToDropClasses;
		public String startDate;
		public String endDate;
		public int catalogId;
	}  
}