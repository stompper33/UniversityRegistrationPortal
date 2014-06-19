package controllers;
import java.util.HashMap;
import java.util.List;

import com.universityregistration.model.BillReport.BillReport;
import com.universityregistration.model.Service.BillReportService;
import com.universityregistration.model.Service.GradeReportService;
import com.universityregistration.model.Service.StudentService;
import com.universityregistration.model.User.Student;

import play.mvc.*;
import play.data.*;
import static play.data.Form.*;
import views.html.*;
import models.*;

public class AcademicsController extends Controller {

	private static String sid = "";
	private static StudentService studService = new StudentService();
	private static GradeReportService gps = new GradeReportService();
	
	public static Result diplayStudentAcademicOptions(String studId){
		Student student = studService.findStudentById(studId);
		return ok(student_academic_options.render(student));
	}
	
	public static Result viewGradeReport(String studId){
		return redirect(routes.GradeReportController.displayReportCard(studId));
	}
	
	public static Result viewTranscripts(String studId){
		return redirect(routes.GradeReportController.displayTranscripts(studId));
	}
}
