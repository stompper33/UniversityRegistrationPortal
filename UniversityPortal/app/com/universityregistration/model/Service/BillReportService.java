package com.universityregistration.model.Service;

import java.util.List;

import com.universityregistration.dal.BillReportDAO;
import com.universityregistration.model.BillReport.*;
import com.universityregistration.model.User.*;

public class BillReportService {
	public BillReportDAO billDAO = new BillReportDAO();
	
	//CREATE a Bill Report for student
	public void createBillReport(Student student, String term, int year){
		billDAO.initilizeStudentBillReport(student, term, year);
	}
	
	//RETRIEVE Bill Report
    public BillReport getBillReport(Student student, String term, int year){
        return billDAO.getBillReport(student, term, year);
    } 
    
    public BillReport retrieveBillReportById(int bid){
    	return billDAO.getBillReportById(bid);
    }
    
    //UPDATE Bill Report
    public void modifyBillReport(Student student, String term, int year){
    	billDAO.updateBillReport(student, term, year);
    }
    
    //RETRIEVE ALL STUDENT BILL_REPORTS
    public List<BillReport> getAllStudentBillReports(String id){
    	return billDAO.getAllBillReports(id);
    }
}
