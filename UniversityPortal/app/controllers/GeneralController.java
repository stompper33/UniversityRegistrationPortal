package controllers;

import play.mvc.*;
import play.data.*;
import static play.data.Form.*;
import views.html.*;
import com.universityregistration.model.Course.CourseSection;
import com.universityregistration.model.Course.CourseCatalog;
import com.universityregistration.model.Course.Course;
import com.universityregistration.model.User.Professor;
import com.universityregistration.model.User.Student;
import com.universityregistration.model.Service.CourseService;
import com.universityregistration.model.Service.ProfessorService;
import com.universityregistration.model.Service.StudentService;

import java.util.*;

public class GeneralController extends Controller {

	private static CourseService courseServ = new CourseService();
	private static ProfessorService profServ = new ProfessorService();
	private static StudentService studServ = new StudentService(); 
	
		public static List<String> listOfAllCourseId(){
		
			List<String> courseId = new ArrayList<String>();
			List<Course> allCourse = courseServ.allCourses();
			
			for(Course c: allCourse){
				courseId.add(c.getCourseId());
			}
			return courseId;

	}
	
		public static HashMap<String, String> listOfAllProfessorId(){
			
			HashMap<String,String> profId = new HashMap<String,String>();
			List<Professor> allProfessors = profServ.allProfessors();
			profId.put("0", "STAFF");
			for(Professor p: allProfessors)
			{
				profId.put(p.getID(), p.getLastName() + ", " + p.getFirstName());
			}
			
			return profId;
		}
		
		public static HashMap<String, String> listOfAllCatalogId(){
			HashMap<String,String> catId = new HashMap<String,String>();
			List<CourseCatalog> allCatalogs = courseServ.retrieveAllCatalogs();
			
			for(CourseCatalog c: allCatalogs)
			{
				catId.put(Integer.toString(c.getCatId()), c.getTerm()+c.getYear());
			}
			
			return catId;
		}
		
		public static List<CourseCatalog> getAllCatalogs(){
			return courseServ.retrieveAllCatalogs();
		}
		
		public static List<CourseListing> allMyProfCourses(String profId) {
	
        Professor retProf = profServ.findProfessorById(profId);
		List<CourseListing> myListOfCourses = new ArrayList<CourseListing>();
		List<CourseSection> myCourses = retProf.getCoursesTeaching();
		
		for(CourseSection cs: myCourses){
			Course c = courseServ.retrieveCourseForCourseSection(cs);
			CourseListing cl = new CourseListing();			
			cl.courseId = c.getCourseId();			
			cl.cname = c.getCourseName();
			cl.courseSectionId = cs.getCourseSectionId();			
			cl.day = cs.getDay();
			cl.location = cs.getLocation();
			cl.time = cs.getTime();
			cl.instructor = retProf.getLastName();
			myListOfCourses.add(cl);
			}
		return myListOfCourses;
    }
	
		public static List<CourseListing> allMyStudCourses(String studId) {
			
	        Student retStud = studServ.findStudentById(studId);
			List<CourseListing> myListOfCourses = new ArrayList<CourseListing>();
			List<CourseSection> myCourses = retStud.getCurrentCourses();
			System.out.println(">>>>>>>>PRIsize: " + myCourses.size());
			
			for(CourseSection cs: myCourses){
				Professor retProf = profServ.findProfessorById(cs.getInstructorSsn());
				Course c = courseServ.retrieveCourseForCourseSection(cs);
				CourseListing cl = new CourseListing();			
				cl.courseId = c.getCourseId();			
				cl.cname = c.getCourseName();
				cl.courseSectionId = cs.getCourseSectionId();			
				cl.day = cs.getDay();
				cl.location = cs.getLocation();
				cl.time = cs.getTime();
				cl.instructor = retProf.getLastName();
				cl.status = cs.getStatus();
				myListOfCourses.add(cl);
				}
			return myListOfCourses;
	    }		
		
		public static List<CourseListing> allMyStudSecCourses(String studId){

	        Student retStud = studServ.findStudentById(studId);
			List<CourseListing> myListOfSecCourses = new ArrayList<CourseListing>();
			List<CourseSection> myCourses = retStud.getSecondaryCourses();
			System.out.println("\n\n>>>>>>>>SECsize: " + myCourses.size());
			
			for(CourseSection cs: myCourses){
				Professor retProf = profServ.findProfessorById(cs.getInstructorSsn());
				Course c = courseServ.retrieveCourseForCourseSection(cs);
				CourseListing cl = new CourseListing();			
				cl.courseId = c.getCourseId();			
				cl.cname = c.getCourseName();
				cl.courseSectionId = cs.getCourseSectionId();			
				cl.day = cs.getDay();
				cl.location = cs.getLocation();
				cl.time = cs.getTime();
				cl.instructor = retProf.getLastName();
				cl.status = cs.getStatus();
				myListOfSecCourses.add(cl);
				}
			return myListOfSecCourses;
		}
		
		public static List<CourseListing> allCourseListingsForCatalog(int catId) {
		List<CourseListing> myListOfCourses = new ArrayList<CourseListing>();
		List<CourseSection> allCoursesForCatId = courseServ.retrieveAllCourseSectionByCatId(catId);
		
		for(CourseSection cs : allCoursesForCatId){
			Professor retProf = profServ.findProfessorById(cs.getInstructorSsn());
			Course c = courseServ.retrieveCourseForCourseSection(cs);
			CourseListing cl = new CourseListing();			
			cl.courseId = c.getCourseId();
			cl.location = cs.getLocation();
			cl.cname = c.getCourseName();
			cl.courseSectionId = cs.getCourseSectionId();			
			cl.day = cs.getDay();
			cl.time = cs.getTime();
			cl.status = cs.getStatus();
			cl.instructor = retProf.getLastName();
			myListOfCourses.add(cl);
			}
		return myListOfCourses;
    }
		
		public static boolean containsCourse(List<CourseListing> myCourses, CourseListing cl){
			for (CourseListing c : myCourses)
				if(c.courseSectionId == cl.courseSectionId)
					return true;

				return false;
		}
		
		public static boolean containsSecCourse(List<CourseListing> mySecCourses, CourseListing cl){
			for (CourseListing c : mySecCourses)
				if(c.courseSectionId == cl.courseSectionId)
					return true;

				return false;
		}
			
    
		public static class CourseListing{
			public String courseId;
			public int courseSectionId;
			public String location;
			public String cname;
			public String day;
			public String time;
			public String instructor;
			public String status;
		}
}