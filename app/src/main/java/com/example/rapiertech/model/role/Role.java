package com.example.rapiertech.model.role;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class Role{

	@SerializedName("data")
	private List<RoleData> data;

	@SerializedName("message")
	private String message;

	@SerializedName("status")
	private boolean status;

	public void setData(List<RoleData> data){
		this.data = data;
	}

	public List<RoleData> getData(){
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