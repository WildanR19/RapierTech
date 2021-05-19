package com.example.rapiertech.model.department;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class Department{

	@SerializedName("data")
	private List<DepartmentData> data;

	@SerializedName("message")
	private String message;

	@SerializedName("status")
	private boolean status;

	public void setData(List<DepartmentData> data){
		this.data = data;
	}

	public List<DepartmentData> getData(){
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