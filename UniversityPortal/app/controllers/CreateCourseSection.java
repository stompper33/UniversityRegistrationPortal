package controllers;

import play.mvc.*;
import play.data.*;
import static play.data.Form.*;
import views.html.*;

import com.universityregistration.dal.CourseDAO;
import com.universityregistration.model.Course.CourseSection;
import com.universityregistration.model.Course.CourseCatalog;
import com.universityregistration.model.Course.Course;
import com.universityregistration.model.User.Professor;
import com.universityregistration.model.Service.CourseService;
import com.universityregistration.model.Service.ProfessorService;
import java.util.*;

public class CreateCourseSection extends Controller {
  
	final static Form<NewCourseSection> createCourseSectionForm = form(CreateCourseSection.NewCourseSection.class);
	private static CourseService courseServ = new CourseService();
	private static ProfessorService profServ = new ProfessorService();
	
    public static Result blank() {
        return ok(create_course_section.render(courseServ.allCourses(), profServ.allProfessors(), form(NewCourseSection.class)));
    }
    
    public static Result submit() {
        Form<NewCourseSection> filledForm = createCourseSectionForm.bindFromRequest();

        if(filledForm.hasErrors()) {
            return badRequest(create_course_section.render(courseServ.allCourses(), profServ.allProfessors(), filledForm));
        } else {
			NewCourseSection nc = filledForm.get();
			String courseId = nc.courseId;
			String instructorId = nc.instructor_ssn;
			String location = nc.location;
			String day = nc.day;
			String time = nc.time;
			int catId = nc.catId;
			
			System.out.println("courseId: " + courseId);
			System.out.println("instructorId: " + instructorId);
			System.out.println("location: " + location);
			System.out.println("day: " + day);
			System.out.println("time: " + time);
			System.out.println("catId: " + catId);
			
			CourseCatalog retCatalog = courseServ.findCourseCatalogById(catId);
			Professor retProf = profServ.findProfessorById(instructorId);
			Course retCourse = courseServ.findCourseById(courseId);
			
			profServ.assignProfessorToCourse(retProf, retCourse, retCatalog, location, day, time);
            return ok(courseSection_summary.render(retCatalog, retProf, retCourse));
        }
    }
   
    public static class NewCourseSection {
		public int catId;
        public String courseId;
		public String instructor_ssn;
		public String location;
		public String day;
		public String time;
    }
}