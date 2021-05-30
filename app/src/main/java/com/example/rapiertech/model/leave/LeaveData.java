package com.example.rapiertech.model.leave;

import com.google.gson.annotations.SerializedName;

public class LeaveData {

	@SerializedName("duration")
	private String duration;

	@SerializedName("reason")
	private String reason;

	@SerializedName("from_date")
	private String fromDate;

	@SerializedName("to_date")
	private String toDate;

	@SerializedName("user_id")
	private int userId;

	@SerializedName("id")
	private int id;

	@SerializedName("reject_reason")
	private String rejectReason;

	@SerializedName("leave_type_id")
	private int leaveTypeId;

	@SerializedName("status")
	private String status;

	public void setDuration(String duration){
		this.duration = duration;
	}

	public String getDuration(){
		return duration;
	}

	public void setReason(String reason){
		this.reason = reason;
	}

	public String getReason(){
		return reason;
	}

	public void setFromDate(String fromDate){
		this.fromDate = fromDate;
	}

	public String getFromDate(){
		return fromDate;
	}

	public void setToDate(String toDate){
		this.toDate = toDate;
	}

	public String getToDate(){
		return toDate;
	}

	public void setUserId(int userId){
		this.userId = userId;
	}

	public int getUserId(){
		return userId;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}

	public void setRejectReason(String rejectReason){
		this.rejectReason = rejectReason;
	}

	public String getRejectReason(){
		return rejectReason;
	}

	public void setLeaveTypeId(int leaveTypeId){
		this.leaveTypeId = leaveTypeId;
	}

	public int getLeaveTypeId(){
		return leaveTypeId;
	}

	public void setStatus(String status){
		this.status = status;
	}

	public String getStatus(){
		return status;
	}
}