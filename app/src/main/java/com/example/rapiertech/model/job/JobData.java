package com.example.rapiertech.model.job;

import com.google.gson.annotations.SerializedName;

public class JobData {

	@SerializedName("total")
	private int total;

	@SerializedName("name")
	private String name;

	@SerializedName("id")
	private int id;

	public void setTotal(int total){
		this.total = total;
	}

	public int getTotal(){
		return total;
	}

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

	public JobData(String name) {
		this.name = name;
	}

	public String toString(){
		return name;
	}
}