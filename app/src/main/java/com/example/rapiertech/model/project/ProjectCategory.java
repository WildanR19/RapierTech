package com.example.rapiertech.model.project;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ProjectCategory{

	@SerializedName("data")
	private List<ProjectCategoryData> data;

	@SerializedName("message")
	private String message;

	@SerializedName("status")
	private boolean status;

	public void setData(List<ProjectCategoryData> data){
		this.data = data;
	}

	public List<ProjectCategoryData> getData(){
		return data;
	}

	public void setMessage(String message){
		this.message = message;
	}

	public String getMessage(){
		return message;
	}

	public void setStatus(boolean status){
		this.status = status;
	}

	public boolean isStatus(){
		return status;
	}

}