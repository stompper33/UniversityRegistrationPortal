package controllers;

import play.mvc.*;
import play.data.*;
import static play.data.Form.*;
import views.html.*;
import com.universityregistration.model.Course.Course;
import com.universityregistration.model.Service.CourseService;
import java.util.List;
import java.util.ArrayList;

public class CreateCourse extends Controller {

	final static Form<Course> createCourseForm = form(Course.class);
	
	//Services needed
	final static CourseService courseServ = new CourseService();
	
	
    public static Result blank() {
        return ok(create_course.render(createCourseForm));
    }
 

	
    public static Result submit() {
        Form<Course> filledForm = createCourseForm.bindFromRequest();
		List<Course> pre = new ArrayList<Course>();
		
        if(filledForm.hasErrors()) {
            return badRequest(create_course.render(filledForm));
        } else {
        	int credits = Integer.parseInt(filledForm.field("credits").value());
        	Course newCourse = filledForm.get();
        	newCourse.setCourseCredits(credits);
			if(filledForm.field("prereqs1").value() != null){
				String p1 = filledForm.field("prereqs1").value();
				pre.add(courseServ.findCourseById(p1));
				}
			if(filledForm.field("prereqs2").value() != null){
				String p2 = filledForm.field("prereqs2").value();
				pre.add(courseServ.findCourseById(p2));
			}
			
			newCourse.setPrereqs(pre);
			
			courseServ.createCourseOffering(newCourse);
            return ok(courseSummary.render(newCourse));
        }
    }
}