package com.example.rapiertech.model.empdetail;

import com.google.gson.annotations.SerializedName;

public class EmpDetail{

	@SerializedName("data")
	private EmpDetailData empDetailData;

	@SerializedName("message")
	private String message;

	@SerializedName("status")
	private boolean status;

	public void setData(EmpDetailData empDetailData){
		this.empDetailData = empDetailData;
	}

	public EmpDetailData getData(){
		return empDetailData;
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