package com.example.rapiertech.model.empdetail;

import com.google.gson.annotations.SerializedName;

public class EmpDetailData {

	@SerializedName("address")
	private String address;

	@SerializedName("status_id")
	private int statusId;

	@SerializedName("gender")
	private String gender;

	@SerializedName("user_id")
	private int userId;

	@SerializedName("phone")
	private String phone;

	@SerializedName("department_id")
	private int departmentId;

	@SerializedName("job_id")
	private int jobId;

	@SerializedName("join_date")
	private String joinDate;

	@SerializedName("id")
	private int id;

	@SerializedName("last_date")
	private String lastDate;

	public void setAddress(String address){
		this.address = address;
	}

	public String getAddress(){
		return address;
	}

	public void setStatusId(int statusId){
		this.statusId = statusId;
	}

	public int getStatusId(){
		return statusId;
	}

	public void setGender(String gender){
		this.gender = gender;
	}

	public String getGender(){
		return gender;
	}

	public void setUserId(int userId){
		this.userId = userId;
	}

	public int getUserId(){
		return userId;
	}

	public void setPhone(String phone){
		this.phone = phone;
	}

	public String getPhone(){
		return phone;
	}

	public void setDepartmentId(int departmentId){
		this.departmentId = departmentId;
	}

	public int getDepartmentId(){
		return departmentId;
	}

	public void setJobId(int jobId){
		this.jobId = jobId;
	}

	public int getJobId(){
		return jobId;
	}

	public void setJoinDate(String joinDate){
		this.joinDate = joinDate;
	}

	public String getJoinDate(){
		return joinDate;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}

	public void setLastDate(String lastDate){
		this.lastDate = lastDate;
	}

	public String getLastDate(){
		return lastDate;
	}
}