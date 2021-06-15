package com.example.rapiertech.model.project;

import com.google.gson.annotations.SerializedName;

public class ProjectActivityData {

	@SerializedName("profile_photo_path")
	private String profilePhotoPath;

	@SerializedName("file")
	private String file;

	@SerializedName("updated_at")
	private String updatedAt;

	@SerializedName("project_id")
	private int projectId;

	@SerializedName("user_id")
	private int userId;

	@SerializedName("user_name")
	private String userName;

	@SerializedName("comment")
	private String comment;

	@SerializedName("id")
	private int id;

	public void setFile(String file){
		this.file = file;
	}

	public String getFile(){
		return file;
	}

	public void setUpdatedAt(String updatedAt){
		this.updatedAt = updatedAt;
	}

	public String getUpdatedAt(){
		return updatedAt;
	}

	public void setProjectId(int projectId){
		this.projectId = projectId;
	}

	public int getProjectId(){
		return projectId;
	}

	public void setUserId(int userId){
		this.userId = userId;
	}

	public int getUserId(){
		return userId;
	}

	public void setUserName(String userName){
		this.userName = userName;
	}

	public String getUserName(){
		return userName;
	}

	public void setComment(String comment){
		this.comment = comment;
	}

	public String getComment(){
		return comment;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}

	public String getProfilePhotoPath() {
		return profilePhotoPath;
	}

	public void setProfilePhotoPath(String profilePhotoPath) {
		this.profilePhotoPath = profilePhotoPath;
	}
}