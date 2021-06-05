package com.example.rapiertech.model.basicpays;

import com.google.gson.annotations.SerializedName;

public class BasicPaysJobData{

	@SerializedName("name")
	private String name;

	@SerializedName("id")
	private int id;

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

	public BasicPaysJobData(String name) {
		this.name = name;
	}

	public String toString(){
		return name;
	}
}