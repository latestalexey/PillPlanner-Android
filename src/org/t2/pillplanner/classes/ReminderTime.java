package org.t2.pillplanner.classes;

import java.io.Serializable;

public class ReminderTime implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int userID = -1;
	private int reminderID = -1;
	private int dayofweek = 0;
	private int hourofday = 0;
	private int minute = 0;
	private long statictime = 0l;;
	private int notificationenabled = 0;
	private long notificationminutes = 15;
	private int repeatingCount = 9999;
	private int userMedID = -1;
	private boolean asNeeded = false;
	
	public boolean getAsNeeded(){
		return asNeeded;
	}
	public void setAsNeeded(boolean asNeeded){
		this.asNeeded = asNeeded;
	}
	public int getUserID() {
		return userID;
	}
	public void setUserID(int userID) {
		this.userID = userID;
	}
	public int getUserMedID() {
		return userMedID;
	}
	public void setUserMedID(int userMedID) {
		this.userMedID = userMedID;
	}
	public int getRepeatingCount() {
		return repeatingCount;
	}
	public void setRepeatingCount(int repeatingCount) {
		this.repeatingCount = repeatingCount;
	}
	public int getReminderID() {
		return reminderID;
	}
	public void setReminderID(int reminderID) {
		this.reminderID = reminderID;
	}
	public int getDayofweek() {
		return dayofweek;
	}
	public void setDayofweek(int dayofweek) {
		this.dayofweek = dayofweek;
	}
	public int getHourofday() {
		return hourofday;
	}
	public void setHourofday(int hourofday) {
		this.hourofday = hourofday;
	}
	public int getMinute() {
		return minute;
	}
	public void setMinute(int minute) {
		this.minute = minute;
	}
	public long getStatictime() {
		return statictime;
	}
	public void setStatictime(long statictime) {
		this.statictime = statictime;
	}
	public int getNotificationenabled() {
		return notificationenabled;
	}
	public void setNotificationenabled(int notificationenabled) {
		this.notificationenabled = notificationenabled;
	}
	public long getNotificationminutes() {
		return notificationminutes;
	}
	public void setNotificationminutes(long notificationminutes) {
		this.notificationminutes = notificationminutes;
	}
	
}
