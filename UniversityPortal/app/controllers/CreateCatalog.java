package controllers;

import play.mvc.*;
import play.data.*;
import static play.data.Form.*;
import views.html.*;
import com.universityregistration.model.Course.CourseCatalog;
import com.universityregistration.model.Service.CourseService;

public class CreateCatalog extends Controller {
  
	final static Form<CourseCatalog> createCatalogForm = form(CourseCatalog.class);
	private static Form<CatSearch> catSrchForm = form(CreateCatalog.CatSearch.class);
	//Services needed
	final static CourseService courseServ = new CourseService();

	//Services needed
	/********* 11/28/2013 ***********/
	public static class CatSearch{
	public Integer catId;
	}
	/**********************************/	
	
	
    public static Result blank() {
        return ok(create_course_catalog.render(createCatalogForm));
    }
	
	 /*
	 *
     * Handle the form submission.
     */
    public static Result submit() {
        Form<CourseCatalog> filledForm = createCatalogForm.bindFromRequest();

        if(filledForm.hasErrors()) {
            return badRequest(create_course_catalog.render(filledForm));
        } else {
            CourseCatalog newCatalog = filledForm.get();
			courseServ.createNewCatalog(newCatalog);
            return ok(catalogSummary.render(newCatalog));
        }
    }
    
    
    /**************************** 11/28/2011 *****************************/
    public static Result viewFinalization(){
    	return ok(finalize_catalog.render(catSrchForm));
    }
    
    public static Result finalizeCatalog(){
    	Form<CatSearch> thisForm = catSrchForm.bindFromRequest();
   
    	if(thisForm.hasErrors())
    		return badRequest(finalize_catalog.render(thisForm));
    	else{
		    CatSearch catSrchId = thisForm.get();
		    System.out.println("+++++++++++++++++++++++++++++>>>>>>>" +catSrchId.catId);
		    CourseCatalog thisCatalog = courseServ.findCourseCatalogById(catSrchId.catId);
		    courseServ.finalizeCat(thisCatalog);
		    return ok(course_options.render());
		}
    }
    /*******************************************************************/    
}