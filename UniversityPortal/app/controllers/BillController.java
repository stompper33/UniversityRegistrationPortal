package controllers;

import java.util.HashMap;
import java.util.List;

import com.universityregistration.model.BillReport.BillReport;
import com.universityregistration.model.Service.BillReportService;
import com.universityregistration.model.Service.StudentService;
import com.universityregistration.model.User.Student;

import play.mvc.*;
import play.data.*;
import static play.data.Form.*;
import views.html.*;
import models.*;

public class BillController extends Controller {
	
	private static String sid = "";
	private static StudentService studService = new StudentService();
	private static BillReportService billService = new BillReportService();
	private static Form<BillSelector> selectForm = form(BillSelector.class);
	
	public static class BillSelector{
		public int billId;
	}
	
	
	public static Result displayStudentFinanceOptions(String studId){
		Student student = studService.findStudentById(studId);
		return ok(student_finance_options.render(student));
	}
	
	public static Result blank(String studId){
		//sid = studId;
		//use db query method from DAL to get terms/years related to student only
		sid = studId;
		Student student = studService.findStudentById(sid);
		List<BillReport> studBillReports = billService.getAllStudentBillReports(sid);
		HashMap<String, String> billMap = new HashMap<String, String>();
		for(BillReport bp : studBillReports){
			billMap.put(Integer.toString(bp.getBillId()), bp.getTerm() + " " + bp.getYear());
		}
		return ok(bill_options.render(student, billMap, selectForm));
	}
	
	public static Result generateBill(){
		Student retStudent = studService.findStudentById(sid);
		Form<BillSelector> bs = selectForm.bindFromRequest();
		if(bs.hasErrors())
			return badRequest(bill_options.render(retStudent, new HashMap<String, String>(), bs));
		else{
			BillSelector newBillSelector = bs.get();
			int bid = newBillSelector.billId;
			BillReport bp = billService.retrieveBillReportById(bid);
			return ok(bill_summary.render(retStudent, bp));
		}		
	}

}
