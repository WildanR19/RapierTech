package com.example.rapiertech.model.employee;

import com.google.gson.annotations.SerializedName;

public class EmployeeData {

	@SerializedName("role_id")
	private int role_id;

	@SerializedName("name")
	private String name;

	@SerializedName("id")
	private int id;

	@SerializedName("profile_photo_path")
	private String profilePhotoPath;

	@SerializedName("job")
	private String job;

	@SerializedName("email")
	private String email;

	@SerializedName("status")
	private String status;

	public void setRole_id(int role_id){
		this.role_id = role_id;
	}

	public int getRole_id(){
		return role_id;
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

	public void setProfilePhotoPath(String profilePhotoPath){
		this.profilePhotoPath = profilePhotoPath;
	}

	public String getProfilePhotoPath(){
		return profilePhotoPath;
	}

	public void setJob(String job){
		this.job = job;
	}

	public String getJob(){
		return job;
	}

	public void setEmail(String email){
		this.email = email;
	}

	public String getEmail(){
		return email;
	}

	public void setStatus(String status){
		this.status = status;
	}

	public String getStatus(){
		return status;
	}

	public EmployeeData(String name) {
		this.name = name;
	}
	public String toString(){
		return name;
	}
}