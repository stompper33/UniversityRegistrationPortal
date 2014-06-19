package com.universityregistration.dal;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import com.universityregistration.model.BillReport.BillReport;
import com.universityregistration.model.Service.StudentService;
import com.universityregistration.model.User.Student;

public class BillReportDAO {
	
	public BillReportDAO() {}
	
	//CREATE Bill Report ------ C
	public void initilizeStudentBillReport(Student student, String term, int year){
		System.out.println("=====================================================================================================\nINSIDE Initialize BillReport!! Method\n\n");
		BillReport bp = new BillReport(student, term, year);
		Connection con = DBHelper.getConnection();
		PreparedStatement billPst = null;
		
		try{
			//System.out.println("Inside initializeStudentBillReport!");
			String insertQuery = "INSERT into bill_report(student_id, term, year, total_credits, rate, balance)"
					+ " VALUES(?,?,?,?,?,?)";
			billPst = con.prepareStatement(insertQuery);
			billPst.setString(1, student.getSsn());
			billPst.setString(2, bp.getTerm());
			billPst.setInt(3, bp.getYear());
			billPst.setInt(4, bp.getTotalCredits());
			billPst.setFloat(5, bp.getRate());
			billPst.setFloat(6, bp.getBalance());
			
			billPst.executeUpdate();
			
		}catch(SQLException ex){
			System.err.println("BillDAO: Threw a SQLException inserting a Bill object.");
			System.err.println(ex.getMessage());
		}finally{
			try{
				if(billPst != null){
					billPst.close();
				}
				if(con != null){
					System.out.println("Database closed after inserting bill report");
					con.close();
				}
				
			}catch(SQLException ex){
				System.err.println("BillDAO: Threw a SQLException inserting a Bill object.");
				System.err.println(ex.getMessage());
			}
		}
	}
	
	
	//RETRIEVE Bill Report ---- R
	public BillReport getBillReport(Student student, String term, int year){
		
		try{
			Statement st = DBHelper.getConnection().createStatement();
			String selectStudentBillReportQuery = "SELECT bill_id, student_id, term, year, total_credits, rate, balance FROM bill_report WHERE student_id = "+ student.getID();
			ResultSet studBillRS = st.executeQuery(selectStudentBillReportQuery);
			
			BillReport bill = new BillReport();
			while(studBillRS.next()){
				bill.setBillId(studBillRS.getInt("bill_id"));
				bill.setTerm(studBillRS.getString("term"));
				bill.setYear(studBillRS.getInt("year"));
				bill.setTotalCredits(studBillRS.getInt("total_credits"));
				bill.setBalance(studBillRS.getFloat("balance"));
				bill.setRate(studBillRS.getFloat("rate"));
			}
			StudentDAO studDAO = new StudentDAO();
			Student stud = studDAO.getStudentById(student.getID());
			bill.setStudent(stud);
			
			return bill;
		}
	    catch (SQLException se) {
		      System.err.println("BillReportDAO: Threw a SQLException retrieving the bill report object.");
		      System.err.println(se.getMessage());
		      se.printStackTrace();
		    }		
		
		return null;
	}

	//UPDATE Bill Report ------ U
	//update added 11282013
	public void updateBillReport(Student student, String term, int year){
		System.out.println("=====================================================================================================\nINSIDE Update BillReport!! Method\n\n");
		System.out.println("stud Id: "+ student.getID() + "\nTerm: " + term + "\nYear: " + year);
		StudentDAO studentDAO = new StudentDAO();
		Student updatedStudentVersion = studentDAO.getStudentById(student.getID());
		BillReport bp = new BillReport(updatedStudentVersion, term, year);
		System.out.println("\n\nbalance: " + bp.getBalance());
		System.out.println("rate: " + bp.getRate());
		System.out.println("tot credits: " + bp.getTotalCredits());
		Connection con = DBHelper.getConnection();
		Statement stmt = null;
		
		try{
			String updateQuery = "UPDATE bill_report SET total_credits = "
					+ bp.getTotalCredits() + ", rate = "
					+ bp.getRate() + ", balance = "
					+ bp.getBalance() + " WHERE student_id = '" + student.getID() + "' AND "
					+ "term = '" + term + "' AND year = " + year;
			stmt = con.createStatement();
			stmt.executeUpdate(updateQuery);
			
		}catch(SQLException ex){
		      System.err.println("BillReportDAO: Threw a SQLException updating bill report object.");
		      System.err.println(ex.getMessage());
		      ex.printStackTrace();
		}finally{
			try{
				if(stmt!=null)
					stmt.close();
				if(con != null){
					System.out.println("Database closed after updating bill report");
					con.close();
				}
			}catch(SQLException ex){
			      System.err.println("BillReportDAO: Threw a SQLException updating bill report object.");
			      System.err.println(ex.getMessage());
			      ex.printStackTrace();
			}
		}
	}
	
	
	//Check if billreport exists for given user for a specific term and year
	public boolean bpExists(String sid, String term, int year){
		
		Statement stmt = null;
		try{
			stmt = DBHelper.getConnection().createStatement();
			String checkExistence = "SELECT bill_id FROM bill_report WHERE EXISTS "
					+" (SELECT * FROM bill_report WHERE student_id = '" + sid + "' AND term = '"
					+ term + "' AND year = " + year + ")";
			
			ResultSet existRS = stmt.executeQuery(checkExistence);
			while(existRS.next()){
				existRS.close();
				stmt.close();
				return true;
			}
			
			existRS.close();
		}catch(SQLException ex){
			System.err.println("BillReportDAO: Threw a SQLException retrieving bill record (existence check)");
			System.err.println(ex.getMessage());
			ex.printStackTrace();
		}
		finally{
			try{
				if(stmt != null)
					stmt.close();
			}
			catch(SQLException ex){
				System.err.println("BillReportDAO: Threw a SQLException retrieving bill record (existence check)");
				System.err.println(ex.getMessage());
				ex.printStackTrace();
			}
		}
		
		return false;
	}
	
	
		//Retrieve Bill Report by ID
		public BillReport getBillReportById(int bid){
			Connection conn = DBHelper.getConnection();
			Statement stmt = null;
			try{
				stmt = conn.createStatement();
				String getBillById = 
						"SELECT bill_id, student_id, term, year, total_credits, rate, balance " 
						+ "FROM bill_report WHERE bill_id = " + bid;
				
				ResultSet billRS = stmt.executeQuery(getBillById);
				BillReport bp = new BillReport();
				while(billRS.next()){
					bp.setBillId(billRS.getInt("bill_id"));
					bp.setTerm(billRS.getString("term"));
					bp.setYear(billRS.getInt("year"));
					bp.setTotalCredits(billRS.getInt("total_credits"));
					bp.setRate(billRS.getFloat("rate"));
					bp.setBalance(billRS.getFloat("balance"));
				}
				billRS.close();
				
				return bp;
			}catch(SQLException ex){
				System.err.println("SQLException: Threw exception while retrieving bill report by ID");
				System.err.println(ex.getMessage());
			}
			finally{
				try{
					if(stmt != null)
						stmt.close();
					if(conn != null)
						conn.close();
				}catch(SQLException ex){
					System.err.println("SQLException: Threw exception while retrieving bill report by ID");
					System.err.println(ex.getMessage());
				}
			}
			return null;
		}
	
	
	//Retrieve all bill reports
		public List<BillReport> getAllBillReports(String id){
				
			Connection con = DBHelper.getConnection();
			Statement stmt = null;
			
			try{
				stmt = con.createStatement();
				String getAllBills = "SELECT bill_id, term, year, total_credits, rate, balance FROM bill_report "
						+ "WHERE student_id = '" + id + "'";
				
				List<BillReport> allBillReports = new ArrayList<BillReport>();
				StudentService studService = new StudentService();
				
				ResultSet billRS = stmt.executeQuery(getAllBills);
				
				while(billRS.next()){
					BillReport bp = new BillReport();
					bp.setBillId(billRS.getInt("bill_id"));
					bp.setTerm(billRS.getString("term"));
					bp.setYear(billRS.getInt("year"));
					bp.setTotalCredits(billRS.getInt("total_credits"));
					bp.setRate(billRS.getFloat("rate"));
					bp.setBalance(billRS.getFloat("balance"));
					
					allBillReports.add(bp);
				}
				
				billRS.close();
				
				return allBillReports;
				
			}catch(SQLException ex){
				System.err.println("SQLException: Thrown when retrieving ALL bill reports");
				System.err.println(ex.getMessage());
			}finally{
				try{
					if(stmt != null)
						stmt.close();
					if(con != null)
						con.close();
				}catch(SQLException ex){
					System.err.println("SQLException: Thrown when retrieving ALL bill reports");
					System.err.println(ex.getMessage());
				}
			}
			return null;
		}
}
