package com.example.rapiertech.model.project;

import com.google.gson.annotations.SerializedName;

public class ProjectData {

	@SerializedName("notes")
	private String notes;

	@SerializedName("category_id")
	private int categoryId;

	@SerializedName("submitted_by")
	private int submittedBy;

	@SerializedName("created_at")
	private String createdAt;

	@SerializedName("project_summary")
	private String projectSummary;

	@SerializedName("id")
	private int id;

	@SerializedName("project_name")
	private String projectName;

	@SerializedName("deadline")
	private String deadline;

	@SerializedName("start_date")
	private String startDate;

	@SerializedName("status")
	private String status;

	public void setNotes(String notes){
		this.notes = notes;
	}

	public String getNotes(){
		return notes;
	}

	public void setCategoryId(int categoryId){
		this.categoryId = categoryId;
	}

	public int getCategoryId(){
		return categoryId;
	}

	public void setSubmittedBy(int submittedBy){
		this.submittedBy = submittedBy;
	}

	public int getSubmittedBy(){
		return submittedBy;
	}

	public void setCreatedAt(String createdAt){
		this.createdAt = createdAt;
	}

	public String getCreatedAt(){
		return createdAt;
	}

	public void setProjectSummary(String projectSummary){
		this.projectSummary = projectSummary;
	}

	public String getProjectSummary(){
		return projectSummary;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}

	public void setProjectName(String projectName){
		this.projectName = projectName;
	}

	public String getProjectName(){
		return projectName;
	}

	public void setDeadline(String deadline){
		this.deadline = deadline;
	}

	public String getDeadline(){
		return deadline;
	}

	public void setStartDate(String startDate){
		this.startDate = startDate;
	}

	public String getStartDate(){
		return startDate;
	}

	public void setStatus(String status){
		this.status = status;
	}

	public String getStatus(){
		return status;
	}
}