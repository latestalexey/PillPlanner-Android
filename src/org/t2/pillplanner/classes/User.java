package org.t2.pillplanner.classes;

import android.graphics.Bitmap;

public class User {

	public static enum LoginType {
	    LOCAL,
	    GOOGLE,
	    FACEBOOK
	}
	
	private String UserID = "";
	private String Name = "";
	private String Password = "";

	private String GoogleToken = "";
	private String FacebookToken = "";
	private String LocalToken = "";

	private Bitmap userPhoto;
	
	public User()
	{
			
	}

	public String getUserID() {
		return UserID;
	}


	public void setUserID(String userID) {
		UserID = userID;
	}


	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}


	public String getPassword() {
		return Password;
	}


	public void setPassword(String password) {
		Password = password;
	}


	public String getGoogleToken() {
		return GoogleToken;
	}


	public void setGoogleToken(String googleToken) {
		GoogleToken = googleToken;
	}


	public String getFacebookToken() {
		return FacebookToken;
	}


	public void setFacebookToken(String facebookToken) {
		FacebookToken = facebookToken;
	}


	public String getLocalToken() {
		return LocalToken;
	}


	public void setLocalToken(String localToken) {
		LocalToken = localToken;
	}

	public Bitmap getUserPhoto() {
		return userPhoto;
	}

	public void setUserPhoto(Bitmap userPhoto) {
		this.userPhoto = userPhoto;
	}
	
}
