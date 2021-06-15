package com.example.rapiertech.model.login;

import com.google.gson.annotations.SerializedName;

public class LoginData {

	@SerializedName("pp_path")
	private String ppPath;

	@SerializedName("user_id")
	private String userId;

	@SerializedName("name")
	private String name;

	@SerializedName("email")
	private String email;

	@SerializedName("role_id")
	private String roleId;

	public void setUserId(String userId){
		this.userId = userId;
	}

	public String getUserId(){
		return userId;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setEmail(String email){
		this.email = email;
	}

	public String getEmail(){
		return email;
	}

	public void setRoleId(String roleId) { this.roleId = roleId; }

	public String getRoleId() { return roleId; }

	public String getPpPath() {
		return ppPath;
	}

	public void setPpPath(String ppPath) {
		this.ppPath = ppPath;
	}
}