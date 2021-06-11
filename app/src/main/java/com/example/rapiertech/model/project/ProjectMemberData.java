package com.example.rapiertech.model.project;

import com.google.gson.annotations.SerializedName;

public class ProjectMemberData {

	@SerializedName("user_id")
	private int userId;

	@SerializedName("project_id")
	private int projectId;

	@SerializedName("user_name")
	private String userName;

	@SerializedName("id")
	private int id;

	@SerializedName("profile_photo_path")
	private String profilePhotoPath;

	public void setUserId(int userId){
		this.userId = userId;
	}

	public int getUserId(){
		return userId;
	}

	public void setProjectId(int projectId){
		this.projectId = projectId;
	}

	public int getProjectId(){
		return projectId;
	}

	public void setUserName(String userName){
		this.userName = userName;
	}

	public String getUserName(){
		return userName;
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
}