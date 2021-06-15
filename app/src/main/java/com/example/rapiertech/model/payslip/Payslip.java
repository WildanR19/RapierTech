package com.example.rapiertech.model.payslip;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class Payslip{

	@SerializedName("data")
	private List<PayslipData> data;

	@SerializedName("message")
	private String message;

	@SerializedName("status")
	private boolean status;

	public void setData(List<PayslipData> data){
		this.data = data;
	}

	public List<PayslipData> getData(){
		return data;
	}

	public void setMessage(String message){
		this.message = message;
	}

	public String getMessage(){
		return message;
	}

	public void setStatus(boolean status){
		this.status = status;
	}

	public boolean isStatus(){
		return status;
	}
}