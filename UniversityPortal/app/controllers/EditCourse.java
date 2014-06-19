package controllers;

import play.mvc.*;
import play.data.*;
import static play.data.Form.*;
import views.html.*;
import com.universityregistration.model.Course.Course;
import com.universityregistration.model.Service.CourseService;

public class EditCourse extends Controller {
  
	private static Form<Course> createCourseForm = form(Course.class);
	private static CourseService courseServ = new CourseService();
	
    public static Result allCourses() {
        return ok(course_listing.render(courseServ.allCourses(), createCourseForm));
    }
	
	public static Result editCourse(String courseId){
		Course retCourse = courseServ.findCourseById(courseId);
		Form<Course> filledForm = form(Course.class).fill(retCourse);
		createCourseForm = filledForm;
		return ok(edit_course_info.render(createCourseForm));
	}

	public static Result submitChanges(){
        Form<Course> filledForm = createCourseForm.bindFromRequest();
        System.out.println("form field: " + filledForm.field("credits").value());
        if(filledForm.hasErrors()) {
            return badRequest(create_course.render(filledForm));
        } else {
            Course updatedCourse = filledForm.get();
            int credits = Integer.parseInt(filledForm.field("credits").value());
            updatedCourse.setCourseCredits(credits);
			courseServ.modifyCourseOffering(updatedCourse);
            return ok(course_listing.render(courseServ.allCourses(),createCourseForm));
        }	
	}
	
	public static Result deleteCourse(String courseId){
		courseServ.removeCourseById(courseId);
		return ok(course_listing.render(courseServ.allCourses(),createCourseForm));
	}

}