package com.example.rapiertech.model.job;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class Job{

	@SerializedName("data")
	private List<JobData> data;

	@SerializedName("message")
	private String message;

	@SerializedName("status")
	private boolean status;

	public void setData(List<JobData> data){
		this.data = data;
	}

	public List<JobData> getData(){
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