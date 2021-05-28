package com.example.rapiertech.model.role;

import com.google.gson.annotations.SerializedName;

public class RoleData {

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

	public RoleData(String name) {
		this.name = name;
	}

	public String toString(){
		return name;
	}
}