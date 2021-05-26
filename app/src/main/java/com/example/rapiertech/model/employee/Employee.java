package com.example.rapiertech.model.employee;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class Employee{

	@SerializedName("data")
	private List<EmployeeData> data;

	@SerializedName("message")
	private String message;

	@SerializedName("status")
	private boolean status;

	public void setData(List<EmployeeData> data){
		this.data = data;
	}

	public List<EmployeeData> getData(){
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