package com.example.rapiertech.model.home;

import com.google.gson.annotations.SerializedName;

public class HomeData {

	@SerializedName("total_employee")
	private int totalEmployee;

	@SerializedName("total_department")
	private int totalDepartment;

	@SerializedName("total_project")
	private int totalProject;

	@SerializedName("total_job")
	private int totalJob;

	public void setTotalEmployee(int totalEmployee){
		this.totalEmployee = totalEmployee;
	}

	public int getTotalEmployee(){
		return totalEmployee;
	}

	public void setTotalDepartment(int totalDepartment){
		this.totalDepartment = totalDepartment;
	}

	public int getTotalDepartment(){
		return totalDepartment;
	}

	public void setTotalProject(int totalProject){
		this.totalProject = totalProject;
	}

	public int getTotalProject(){
		return totalProject;
	}

	public void setTotalJob(int totalJob){
		this.totalJob = totalJob;
	}

	public int getTotalJob(){
		return totalJob;
	}
}