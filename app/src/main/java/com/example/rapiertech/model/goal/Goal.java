package com.example.rapiertech.model.goal;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class Goal{

	@SerializedName("data")
	private List<GoalData> data;

	@SerializedName("message")
	private String message;

	@SerializedName("status")
	private boolean status;

	public void setData(List<GoalData> data){
		this.data = data;
	}

	public List<GoalData> getData(){
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