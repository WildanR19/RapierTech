package com.example.rapiertech.model.department;

import com.google.gson.annotations.SerializedName;

public class DepartmentData {

	@SerializedName("name")
	private String name;

	@SerializedName("id")
	private int id;

	@SerializedName("total")
	private int total;

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}
}