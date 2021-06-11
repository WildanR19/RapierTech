package com.example.rapiertech.model.project;

import com.google.gson.annotations.SerializedName;

public class ProjectCategoryData {

	@SerializedName("category_name")
	private String categoryName;

	@SerializedName("id")
	private int id;

	public void setCategoryName(String categoryName){
		this.categoryName = categoryName;
	}

	public String getCategoryName(){
		return categoryName;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}

	public ProjectCategoryData(String categoryName) {
		this.categoryName = categoryName;
	}

	public String toString(){
		return categoryName;
	}
}