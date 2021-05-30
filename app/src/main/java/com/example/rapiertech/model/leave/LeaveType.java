package com.example.rapiertech.model.leave;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class LeaveType{

	@SerializedName("data")
	private List<LeaveTypeData> data;

	@SerializedName("message")
	private String message;

	@SerializedName("status")
	private boolean status;

	public void setData(List<LeaveTypeData> data){
		this.data = data;
	}

	public List<LeaveTypeData> getData(){
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