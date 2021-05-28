package com.example.rapiertech.model.empstatus;

import com.google.gson.annotations.SerializedName;

public class EmpStatusData {

	@SerializedName("status_name")
	private String statusName;

	@SerializedName("id")
	private int id;

	public void setStatusName(String statusName){
		this.statusName = statusName;
	}

	public String getStatusName(){
		return statusName;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}

	public EmpStatusData(String statusName) {
		this.statusName = statusName;
	}

	public String toString(){
		return statusName;
	}
}