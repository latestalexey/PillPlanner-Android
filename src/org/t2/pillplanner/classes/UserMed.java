package org.t2.pillplanner.classes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UserMed  implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -648822986692734275L;
	private String UserID = "";
	private String UMID = "";
	private String ProductName = "";
	private String DrugForm = "";
	private String Dosage = "";
	private String Reason = "";
	private String Warnings = "";
	private String Notes = "";

	public List<ReminderTime> scheduledTimes = new ArrayList<ReminderTime>();
	
	public String getReason() {
		return Reason;
	}

	public void setReason(String reason) {
		Reason = reason;
	}

	public String getWarnings() {
		return Warnings;
	}
	public void setWarnings(String warnings) {
		Warnings = warnings;
	}

	public String getNotes() {
		return Notes;
	}

	public void setNotes(String notes) {
		Notes = notes;
	}

	public UserMed()
	{
			
	}

	public String getUMID() {
		return UMID;
	}

	public void setUMID(String uMID) {
		UMID = uMID;
	}
	
	public String getUserID() {
		return UserID;
	}


	public void setUserID(String userID) {
		UserID = userID;
	}


	public String getProductName() {
		return ProductName;
	}


	public void setProductName(String productName) {
		ProductName = productName;
	}


	public String getDrugForm() {
		return DrugForm;
	}


	public void setDrugForm(String drugForm) {
		DrugForm = drugForm;
	}


	public String getDosage() {
		return Dosage;
	}


	public void setDosage(String dosage) {
		Dosage = dosage;
	}

	
}
