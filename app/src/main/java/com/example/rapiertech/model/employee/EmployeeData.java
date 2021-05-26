package com.example.rapiertech.model.employee;

import com.google.gson.annotations.SerializedName;

public class EmployeeData {

	@SerializedName("role")
	private String role;

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

	public void setRole(String role){
		this.role = role;
	}

	public String getRole(){
		return role;
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
}