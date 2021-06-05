package com.example.rapiertech.model.basicpays;

import com.google.gson.annotations.SerializedName;

public class BasicPaysData {

	@SerializedName("amount")
	private int amount;

	@SerializedName("name")
	private String name;

	@SerializedName("id")
	private int id;

	public void setAmount(int amount){
		this.amount = amount;
	}

	public int getAmount(){
		return amount;
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
}