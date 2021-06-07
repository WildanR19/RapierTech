package com.example.rapiertech.model.event;

import com.google.gson.annotations.SerializedName;

public class EventData {

	@SerializedName("start")
	private String start;

	@SerializedName("end")
	private String end;

	@SerializedName("id")
	private int id;

	@SerializedName("title")
	private String title;

	public void setStart(String start){
		this.start = start;
	}

	public String getStart(){
		return start;
	}

	public void setEnd(String end){
		this.end = end;
	}

	public String getEnd(){
		return end;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}

	public void setTitle(String title){
		this.title = title;
	}

	public String getTitle(){
		return title;
	}
}