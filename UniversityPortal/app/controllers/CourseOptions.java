package controllers;

import static play.data.Form.form;

import java.util.List;

import com.universityregistration.model.User.Professor;

import controllers.GeneralController.CourseListing;
import controllers.MyProfCourses.SearchCatalogBox;
import play.data.Form;
import play.mvc.*;

import views.html.*;

public class CourseOptions extends Controller {
  
	private static Form<SearchCatalogBox> searchCatalogForm = form(MyProfCourses.SearchCatalogBox.class);
	
    public static Result loadOptions() {
        return ok(course_options.render());
    }
    
    public static Result adminCatalogSelectBlank(){
   		searchCatalogForm = form(MyProfCourses.SearchCatalogBox.class);
   		return ok(admin_catalog_selection.render(GeneralController.listOfAllCatalogId(), searchCatalogForm));
    }
    
    public static Result adminCatSelectSubmit(){
		Form<SearchCatalogBox> filledForm = searchCatalogForm.bindFromRequest();
		
        if(filledForm.hasErrors()) {
            return badRequest(admin_catalog_selection.render(GeneralController.listOfAllCatalogId(), searchCatalogForm));
        } 
		else{
		    SearchCatalogBox search = filledForm.get();
			List<CourseListing> coursesForCatalog = GeneralController.allCourseListingsForCatalog(search.catalogId);
			return ok(term_courses.render(coursesForCatalog));	
		}
    }
  
}