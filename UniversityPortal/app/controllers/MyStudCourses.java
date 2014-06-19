package controllers;

import play.mvc.*;
import play.data.*;
import static play.data.Form.*;
import controllers.GeneralController.*;

import java.util.List;
import views.html.*;
import java.util.ArrayList;
import com.universityregistration.model.Course.Course;
import com.universityregistration.model.Course.CourseSection;
import com.universityregistration.model.User.Student;
import com.universityregistration.model.Service.CourseService;
import com.universityregistration.model.Service.ProfessorService;
import com.universityregistration.model.Service.StudentService;

public class MyStudCourses extends Controller {

	private static Form<SearchCatalogBox> searchCatalogForm = form(MyStudCourses.SearchCatalogBox.class);
	private static Form<ScheduleSelectBox> scheduleSelectForm = form(MyStudCourses.ScheduleSelectBox.class);
	private static CourseService courseServ = new CourseService();
	private static StudentService studServ = new StudentService();  
	
	public static class SearchCatalogBox{
		public int catalogId;
	}
	
	public static class ScheduleSelectBox{
		public String schedule;
	}
	
	public static Result searchCatalogBlank(String studId){
		Student retStud = studServ.findStudentById(studId);
		searchCatalogForm = form(MyStudCourses.SearchCatalogBox.class);
		return ok(student_catalog_selection.render(retStud, GeneralController.listOfAllCatalogId(), searchCatalogForm));
	
	}
	
	public static Result searchCatalogSubmit(String studId){
		Form<SearchCatalogBox> filledForm = searchCatalogForm.bindFromRequest();
		Student retStud = studServ.findStudentById(studId);	
		
        if(filledForm.hasErrors()) {
            return badRequest(student_catalog_selection.render(retStud, GeneralController.listOfAllCatalogId(), filledForm));
        } 
		else{
		    SearchCatalogBox search = filledForm.get();
			List<CourseListing> coursesForCatalog = GeneralController.allCourseListingsForCatalog(search.catalogId);
			List<CourseListing> myStudPriCourses = GeneralController.allMyStudCourses(studId);
			List<CourseListing> myStudSecCourses = GeneralController.allMyStudSecCourses(studId);
			return ok(student_course_selection.render(retStud, scheduleSelectForm, coursesForCatalog, myStudPriCourses, myStudSecCourses, search.catalogId));	
		}
		
	}	
	
    public static Result allMyCourses(String studId) {
        Student retStud = studServ.findStudentById(studId);
		return ok(my_stud_courses.render(retStud, GeneralController.allMyStudCourses(studId),  GeneralController.allMyStudSecCourses(studId)));
    }
    
	public static Result assignCourse(String studId, int courseSectionId, int catId){
		Form<ScheduleSelectBox> filledForm = scheduleSelectForm.bindFromRequest();
		ScheduleSelectBox sb = filledForm.get();
		Student retStud = studServ.findStudentById(studId);
		CourseSection retCs = courseServ.findCourseSectionById(courseSectionId);
		Student retStudentAfterAssign = null;
		
		if(sb.schedule.equals("primary") && !sb.schedule.equals("secondary")){
			studServ.registerCurrentCourse(retStud, retCs, catId);
			retStudentAfterAssign = studServ.findStudentById(studId);
			return ok(my_stud_courses.render(retStudentAfterAssign, GeneralController.allMyStudCourses(studId), GeneralController.allMyStudSecCourses(studId)));					
		}
		else{
			studServ.registerSecondaryCourse(retStud, retCs, catId);
			retStudentAfterAssign = studServ.findStudentById(studId);
			return ok(my_stud_courses.render(retStudentAfterAssign, GeneralController.allMyStudCourses(studId), GeneralController.allMyStudSecCourses(studId)));				
		}

	}   
	
	public static Result unassignCourse(String studId, int courseSectionId){
		int catId = courseServ.retrieveCatIdByCourseSectionId(courseSectionId);
		Student retStud = studServ.findStudentById(studId);
		CourseSection retCs = courseServ.findCourseSectionById(courseSectionId);
		studServ.withdrawlCurrentCourse(retStud, retCs, catId);
		Student retStudentAfterDrop = studServ.findStudentById(studId);
		return ok(my_stud_courses.render(retStudentAfterDrop, GeneralController.allMyStudCourses(studId), GeneralController.allMyStudSecCourses(studId)));
	}

	public static Result unassignSecCourse(String studId, int courseSectionId){
		int catId = courseServ.retrieveCatIdByCourseSectionId(courseSectionId);
		Student retStud = studServ.findStudentById(studId);
		CourseSection retCs = courseServ.findCourseSectionById(courseSectionId);
		studServ.withdrawlSecondaryCourse(retStud, retCs, catId);
		Student retStudentAfterSecDrop = studServ.findStudentById(studId);
		return ok(my_stud_courses.render(retStudentAfterSecDrop, GeneralController.allMyStudCourses(studId), GeneralController.allMyStudSecCourses(studId)));
	}	
	
	public static Result showCourseInfo(String cId){
		Course retCourse = courseServ.findCourseById(cId);
		return ok(course_info.render(retCourse));
	}	
}