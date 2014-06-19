package controllers;

import play.mvc.*;
import play.data.*;
import static play.data.Form.*;
import views.html.*;
import views.html.helper.form;
import controllers.GeneralController.*;
import java.util.List;
import java.util.ArrayList;
import com.universityregistration.model.Course.Course;
import com.universityregistration.model.Course.CourseSection;
import com.universityregistration.model.User.Professor;
import com.universityregistration.model.User.Student;
import com.universityregistration.model.Service.CourseService;
import com.universityregistration.model.Service.GradeReportService;
import com.universityregistration.model.Service.ProfessorService;
import com.universityregistration.model.Service.StudentService;

public class MyProfCourses extends Controller {

	private static Form<SearchCatalogBox> searchCatalogForm = form(MyProfCourses.SearchCatalogBox.class);
	private static Form<GradeChangeForm> changeGradeForm = form(MyProfCourses.GradeChangeForm.class);
	private static CourseService courseServ = new CourseService();
	private static ProfessorService profServ = new ProfessorService();
	private static GradeReportService grServ = new GradeReportService();
	private static StudentService studServ = new StudentService();
	
	private static String pid = "";
	
	public static class SearchCatalogBox{
		public Integer catalogId;
	}

	public static class GradeChangeForm{
		public String grade;
	}
	
    public static Result allMyCourses(String profId) {
    	pid = profId;
        Professor retProf = profServ.findProfessorById(profId);
		return ok(my_prof_courses.render(retProf, GeneralController.allMyProfCourses(profId)));
    }
	
	public static Result unassignCourse(String profId, int courseSectionId){
		Professor retProf = profServ.findProfessorById(profId);
		CourseSection retCs = courseServ.findCourseSectionById(courseSectionId);
		profServ.unassignProfessorToCourse(retProf, retCs);
		Professor retProfAfterDrop = profServ.findProfessorById(profId);
		return ok(my_prof_courses.render(retProfAfterDrop, GeneralController.allMyProfCourses(profId)));
	}
	
	public static Result searchCatalogBlank(String profId){
		Professor retProf = profServ.findProfessorById(profId);
		searchCatalogForm = form(MyProfCourses.SearchCatalogBox.class);
		return ok(professor_catalog_selection.render(retProf, GeneralController.listOfAllCatalogId(), searchCatalogForm));
	
	}
	
	public static Result searchCatalogSubmit(String profId){
	
		Form<SearchCatalogBox> filledForm = searchCatalogForm.bindFromRequest();
		Professor retProf = profServ.findProfessorById(profId);	
		
		
        if(filledForm.hasErrors()) {
            return badRequest(professor_catalog_selection.render(retProf, GeneralController.listOfAllCatalogId(), filledForm));
        } 
		else{
		    SearchCatalogBox search = filledForm.get();
			List<CourseListing> coursesForCatalog = GeneralController.allCourseListingsForCatalog(search.catalogId);
			List<CourseListing> myProfCourses = GeneralController.allMyProfCourses(profId);
			return ok(professor_course_selection.render(retProf, coursesForCatalog, myProfCourses));	
		}
		
	}

	public static Result assignCourse(String profId, int courseSectionId){
		Professor retProf = profServ.findProfessorById(profId);
		CourseSection retCs = courseServ.findCourseSectionById(courseSectionId);
		courseServ.modifyCourseSectionProf(retProf, retCs);
		Professor retProfessorAfterAdd = profServ.findProfessorById(profId);
		return ok(my_prof_courses.render(retProfessorAfterAdd, GeneralController.allMyProfCourses(profId)));		
	}
	
	public static Result viewRoster(String profId, int courseSectionId){
		Professor retProf = profServ.findProfessorById(profId);
		CourseSection retCs = courseServ.findCourseSectionById(courseSectionId);
		List<Student> roster = retCs.getRoster();
		return ok(my_course_roster.render(retProf, roster, courseSectionId));
	}
	
	public static Result getGradebookForGivenCourseSection(String profId, int csId){
		
		Professor retProf = profServ.findProfessorById(profId);
		CourseSection retCs = courseServ.findCourseSectionById(csId);
		List<Student> roster = retCs.getRoster();
		return ok(prof_gradebook.render(changeGradeForm, retProf, roster, csId, grServ));
	}
	
	public static Result changeGrade(String studId, int csId){
		CourseSection retCs = courseServ.findCourseSectionById(csId);
		Student retStud = studServ.findStudentById(studId);
		String term = grServ.retrieveTermForCourseSection(retCs);
		Professor retProf = profServ.findProfessorById(pid);
		
		Form<GradeChangeForm> filledForm = changeGradeForm.bindFromRequest();
		GradeChangeForm gf = filledForm.get();
		String grade = gf.grade;
		
		grServ.updateGradeReport(retStud, retCs, grade, term);
		List<Student> roster = retCs.getRoster();
		
		return ok(prof_gradebook.render(new Form(GradeChangeForm.class), retProf, roster, csId, grServ));
	}
}