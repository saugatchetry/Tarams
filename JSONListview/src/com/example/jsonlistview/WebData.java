package com.example.jsonlistview;

import java.io.Serializable;

public class WebData implements Serializable{
	
	String userName;
	String userId;
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	

}
