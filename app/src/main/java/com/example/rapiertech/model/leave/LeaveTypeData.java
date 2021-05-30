package com.example.rapiertech.model.leave;

import com.google.gson.annotations.SerializedName;

public class LeaveTypeData {

	@SerializedName("type_name")
	private String typeName;

	@SerializedName("color")
	private String color;

	@SerializedName("id")
	private int id;

	public void setTypeName(String typeName){
		this.typeName = typeName;
	}

	public String getTypeName(){
		return typeName;
	}

	public void setColor(String color){
		this.color = color;
	}

	public String getColor(){
		return color;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}
}