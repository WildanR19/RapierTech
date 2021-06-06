package com.example.rapiertech.model.payslip;

import com.google.gson.annotations.SerializedName;

public class PayslipData {

	@SerializedName("to_date")
	private String toDate;

	@SerializedName("user_id")
	private int userId;

	@SerializedName("for_date")
	private String forDate;

	@SerializedName("overtimes")
	private int overtimes;

	@SerializedName("payment")
	private String payment;

	@SerializedName("id")
	private int id;

	@SerializedName("allowances")
	private int allowances;

	@SerializedName("deductions")
	private int deductions;

	@SerializedName("others")
	private int others;

	@SerializedName("salary")
	private int salary;

	@SerializedName("status")
	private String status;

	@SerializedName("emp_name")
	private String empName;

	@SerializedName("job")
	private String job;

	public void setToDate(String toDate){
		this.toDate = toDate;
	}

	public String getToDate(){
		return toDate;
	}

	public void setUserId(int userId){
		this.userId = userId;
	}

	public int getUserId(){
		return userId;
	}

	public void setForDate(String forDate){
		this.forDate = forDate;
	}

	public String getForDate(){
		return forDate;
	}

	public void setOvertimes(int overtimes){
		this.overtimes = overtimes;
	}

	public int getOvertimes(){
		return overtimes;
	}

	public void setPayment(String payment){
		this.payment = payment;
	}

	public String getPayment(){
		return payment;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}

	public void setAllowances(int allowances){
		this.allowances = allowances;
	}

	public int getAllowances(){
		return allowances;
	}

	public void setDeductions(int deductions){
		this.deductions = deductions;
	}

	public int getDeductions(){
		return deductions;
	}

	public void setOthers(int others){
		this.others = others;
	}

	public int getOthers(){
		return others;
	}

	public void setSalary(int salary){
		this.salary = salary;
	}

	public int getSalary(){
		return salary;
	}

	public void setStatus(String status){
		this.status = status;
	}

	public String getStatus(){
		return status;
	}

	public String getEmpName() { return empName; }

	public void setEmpName(String empName) { this.empName = empName; }

	public String getJob() { return job; }

	public void setJob(String job) { this.job = job; }
}