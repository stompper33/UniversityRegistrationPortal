package controllers;

import play.mvc.*;
import play.data.*;
import static play.data.Form.*;
import views.html.*;
import com.universityregistration.model.Course.Course;
import com.universityregistration.model.Course.CourseSection;
import com.universityregistration.model.Service.CourseService;

public class CourseSelectionController extends Controller {

	private static CourseService courseServ = new CourseService();

	public static Result showCourseInfo(String cId){
		Course retCourse = courseServ.findCourseById(cId);
		return ok(course_info.render(retCourse));
	}
}