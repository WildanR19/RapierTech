package com.example.rapiertech.model.home;

import com.google.gson.annotations.SerializedName;

public class Home{

	@SerializedName("data")
	private HomeData homeData;

	@SerializedName("message")
	private String message;

	@SerializedName("status")
	private boolean status;

	public void setData(HomeData homeData){
		this.homeData = homeData;
	}

	public HomeData getData(){
		return homeData;
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