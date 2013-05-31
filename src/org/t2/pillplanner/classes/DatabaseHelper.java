package org.t2.pillplanner.classes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.t2.pillplanner.classes.User.LoginType;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

/**
 * Handles all database operations
 * 
 * @author Steve Ody (stephen.ody@tee2.org)
 */

public class DatabaseHelper
{

	private static final String DATABASE_NAME = "t2pillplanner.db";
	private static final int DATABASE_VERSION = 9;

	private Context context;
	private SQLiteDatabase db;

	public DatabaseHelper(Context context) 
	{
		this.context = context;      
	}

	public static String scrubInput(String input)
	{
		//add more reserved SQL characters to prevent a sql injection attack or just a crash
		String Output = input.replace("'", "''");
		return Output;
	}

	public List<User> getUserList()
	{

		OpenHelper openHelper = new OpenHelper(this.context);
		this.db = openHelper.getWritableDatabase();
		Cursor cursor = null;

		String query = "select UserID, Name, Password, LocalToken, GoogleToken, FacebookToken, photo from Users";
		cursor = this.db.rawQuery(query, null);

		if (cursor.moveToFirst()) 
		{
			List<User> outUsers = new ArrayList<User>();

			do 
			{
				User outUser = new User();
				outUser.setUserID(cursor.getString(0));
				outUser.setName(cursor.getString(1));
				outUser.setPassword(cursor.getString(2));
				outUser.setLocalToken(cursor.getString(3));
				outUser.setGoogleToken(cursor.getString(4));
				outUser.setFacebookToken(cursor.getString(5));

				//photo
				try
				{
					byte []temp = cursor.getBlob(6);
					Bitmap image = BitmapFactory.decodeByteArray(temp, 0, temp.length);
					outUser.setUserPhoto(image);
				}
				catch(Exception ex){}
				outUsers.add(outUser);
			}
			while (cursor.moveToNext());

			if (cursor != null && !cursor.isClosed()) 
			{
				cursor.close();
			}

			db.close();
			return outUsers;
		}
		else
		{
			cursor.close();
			db.close();
			return null;
		}
	}

	public User createNewUser(String name, String password, String GoogleToken, String FacebookToken, byte[] photo)
	{

		OpenHelper openHelper = new OpenHelper(this.context);
		this.db = openHelper.getWritableDatabase();

		try
		{
			//Need to check if this user already exists first

			//Generate a localtoken
			String LocalToken = UUID.randomUUID().toString();

			ContentValues insertValues = new ContentValues();
			insertValues.put("Name", name);
			insertValues.put("Password", password);
			insertValues.put("LocalToken", LocalToken);
			insertValues.put("GoogleToken", GoogleToken);
			insertValues.put("FacebookToken", FacebookToken);
			insertValues.put("photo", photo);
			db.insert("Users", null, insertValues);

			return getUserByToken(LoginType.LOCAL, LocalToken);
		}
		catch(Exception ex)
		{
			return null;
		}
		finally
		{
			db.close();
		}

	}

	public boolean setUserToken(int userID, User.LoginType type, String token)
	{
		OpenHelper openHelper = new OpenHelper(this.context);
		this.db = openHelper.getWritableDatabase();

		try
		{
			if(type == LoginType.LOCAL)
			{
				db.execSQL("update Users set LocalToken = '" + token + "' where userid = " + userID);
			}
			else if (type == LoginType.GOOGLE)
			{
				db.execSQL("update Users set GoogleToken = '" + token + "' where userid = " + userID);
			}
			else if (type == LoginType.FACEBOOK)
			{
				db.execSQL("update Users set FacebookToken = '" + token + "' where userid = " + userID);
			}

			return true;
		}
		catch(Exception ex)
		{
			return false;
		}
		finally
		{
			db.close();
		}

	}

	public boolean updateUserPhoto(int userID, byte[] photo)
	{
		OpenHelper openHelper = new OpenHelper(this.context);
		this.db = openHelper.getWritableDatabase();

		try
		{
			ContentValues updateValues = new ContentValues();
			updateValues.put("photo", photo);
			db.update("Users", updateValues, "userid = " + userID, null);

			return true;
		}
		catch(Exception ex)
		{
			return false;
		}
		finally
		{
			db.close();
		}

	}
	
	public User getUserByToken(User.LoginType type, String token)
	{
		User outUser = null;
		OpenHelper openHelper = new OpenHelper(this.context);
		this.db = openHelper.getWritableDatabase();
		Cursor cursor = null;

		if(type == LoginType.LOCAL)
		{
			String query = "select UserID, Name, Password, LocalToken, GoogleToken, FacebookToken from Users where LocalToken = '" + token + "'";
			cursor = this.db.rawQuery(query, null);
		}
		else if (type == LoginType.GOOGLE)
		{
			String query = "select UserID, Name, Password, LocalToken, GoogleToken, FacebookToken from Users where GoogleToken = '" + token + "'";
			cursor = this.db.rawQuery(query, null);
		}
		else if (type == LoginType.FACEBOOK)
		{
			String query = "select UserID, Name, Password, LocalToken, GoogleToken, FacebookToken, photo from Users where FacebookToken = '" + token + "'";
			cursor = this.db.rawQuery(query, null);
		}

		if (cursor.moveToFirst()) 
		{
			do 
			{
				outUser = new User();
				outUser.setUserID(cursor.getString(0));
				outUser.setName(cursor.getString(1));
				outUser.setPassword(cursor.getString(2));
				outUser.setLocalToken(cursor.getString(3));
				outUser.setGoogleToken(cursor.getString(4));
				outUser.setFacebookToken(cursor.getString(5));
				
				//photo
				try
				{
					byte []temp = cursor.getBlob(6);
					Bitmap image = BitmapFactory.decodeByteArray(temp, 0, temp.length);
					outUser.setUserPhoto(image);
				}
				catch(Exception ex){}
				
			}
			while (cursor.moveToNext());

			if (cursor != null && !cursor.isClosed()) 
			{
				cursor.close();
			}

			db.close();
			return outUser;
		}
		else
		{
			if (cursor != null && !cursor.isClosed()) 
			{
				cursor.close();
			}

			db.close();
			return null;
		}
	}

	public User getUserByUsernamePass(String username, String password)
	{
		User outUser = null;
		OpenHelper openHelper = new OpenHelper(this.context);
		this.db = openHelper.getWritableDatabase();
		Cursor cursor = null;

		String query = "select UserID, Name, Password, LocalToken, GoogleToken, FacebookToken, photo from Users where Name = '" + username + "' and Password = '" + password + "'";
		cursor = this.db.rawQuery(query, null);

		if (cursor.moveToFirst()) 
		{
			do 
			{
				outUser = new User();
				outUser.setUserID(cursor.getString(0));
				outUser.setName(cursor.getString(1));
				outUser.setPassword(cursor.getString(2));
				outUser.setLocalToken(cursor.getString(3));
				outUser.setGoogleToken(cursor.getString(4));
				outUser.setFacebookToken(cursor.getString(5));
				
				//photo
				try
				{
					byte []temp = cursor.getBlob(6);
					Bitmap image = BitmapFactory.decodeByteArray(temp, 0, temp.length);
					outUser.setUserPhoto(image);
				}
				catch(Exception ex){}
				
			}
			while (cursor.moveToNext());

			if (cursor != null && !cursor.isClosed()) 
			{
				cursor.close();
			}

			db.close();
			return outUser;
		}
		else
		{
			cursor.close();
			db.close();
			return null;
		}
	}

	public List<String> getDrugNameContains(String nameLike)
	{

		OpenHelper openHelper = new OpenHelper(this.context);
		this.db = openHelper.getWritableDatabase();
		String query = "select PRODUCTNAME from FDAMEDS";

		List<String> list = new ArrayList<String>();

		Cursor cursor = this.db.rawQuery(query, null);
		if (cursor.moveToFirst()) 
		{
			do 
			{
				String row = cursor.getString(0);
				list.add(row); 
			} 
			while (cursor.moveToNext());
		}
		if (cursor != null && !cursor.isClosed()) 
		{
			cursor.close();
		}

		db.close();

		return list;
	}

	public List<String> getDrugForms()
	{

		OpenHelper openHelper = new OpenHelper(this.context);
		this.db = openHelper.getWritableDatabase();
		String query = "select DRUGFORM from DRUGFORMS";

		List<String> list = new ArrayList<String>();

		Cursor cursor = this.db.rawQuery(query, null);
		if (cursor.moveToFirst()) 
		{
			do 
			{
				String row = cursor.getString(0);
				list.add(row); 
			} 
			while (cursor.moveToNext());
		}
		if (cursor != null && !cursor.isClosed()) 
		{
			cursor.close();
		}

		db.close();

		return list;
	}

	public long addDrugToUser(String userID, String drugName, String drugForm, String drugDosage, String Reason, String Warnings, String Notes)
	{
		OpenHelper openHelper = new OpenHelper(this.context);
		this.db = openHelper.getWritableDatabase();
		long result = -1l;
		
		try
		{			
			ContentValues insertValues = new ContentValues();
			insertValues.put("user", userID);
			insertValues.put("productname", drugName);
			insertValues.put("drugform", drugForm);
			insertValues.put("dosage", drugDosage);
			insertValues.put("deleted", 0);
			insertValues.put("reason", Reason);
			insertValues.put("warnings", Warnings);
			insertValues.put("notes", Notes);
			result = db.insert("usermeds", null, insertValues);
		}
		catch(Exception ex)
		{
			return -1l;
		}
		
		return result;

	}
	
	public long addTimeToUserSchedule(String userID, long usermed, int notificationenabled, int repeatingcount, long notificationminutes, boolean AsNeeded, long StaticTime, int DayOfWeek, int HourOfDay, int MinuteOfDay)
	{//TODO:Current 
		OpenHelper openHelper = new OpenHelper(this.context);
		this.db = openHelper.getWritableDatabase();
		long result = -1l;
		
		try
		{			
			ContentValues insertValues = new ContentValues();
			insertValues.put("user", userID);
			insertValues.put("usermed", usermed);
			insertValues.put("notificationenabled", notificationenabled);
			insertValues.put("repeatingcount", repeatingcount);
			insertValues.put("notificationminutes", notificationminutes);
			insertValues.put("AsNeeded", AsNeeded);
			insertValues.put("StaticTime", StaticTime);
			insertValues.put("DayOfWeek", DayOfWeek);
			insertValues.put("HourOfDay", HourOfDay);
			insertValues.put("MinuteOfDay", MinuteOfDay);
			result = db.insert("reminders", null, insertValues);
		}
		catch(Exception ex)
		{
			return -1l;
		}
		
		return result;

	}

	public boolean editUserScheduledDrug(String usermedid, String userID, String drugName, String drugForm, String drugDosage, String Reason, String Warnings, String Notes)
	{
		OpenHelper openHelper = new OpenHelper(this.context);
		this.db = openHelper.getWritableDatabase();

		try
		{
			//USERMEDS (UMID INTEGER PRIMARY KEY, USER INTEGER, PRODUCTNAME TEXT, DRUGFORM TEXT, DOSAGE TEXT)
			String query = "update usermeds set productname = '" + drugName + "', drugform = '" + drugForm + "', dosage = '" + drugDosage + "', reason = '" + Reason + "', warnings = '" + Warnings + "', notes = '" + Notes + "' where umid = " + usermedid;
			db.execSQL(query);
		}
		catch(Exception ex)
		{
			return false;
		}

		return true;
	}


	public boolean removeUserScheduledDrug(String usermedid)
	{
		OpenHelper openHelper = new OpenHelper(this.context);
		this.db = openHelper.getWritableDatabase();

		try
		{
			//USERMEDS (UMID INTEGER PRIMARY KEY, USER INTEGER, PRODUCTNAME TEXT, DRUGFORM TEXT, DOSAGE TEXT)
			String query = "update usermeds set deleted = 1 where umid = " + usermedid;
			db.execSQL(query);
		}
		catch(Exception ex)
		{
			return false;
		}

		return true;
	}

	public List<ReminderTime> getRemindersforUserMed(String usermedID)
	{
		OpenHelper openHelper = new OpenHelper(this.context);
		this.db = openHelper.getWritableDatabase();

		String query = "select reminderid, user, usermed, repeatingcount, dayofweek, hourofday, minuteofday, statictime, notificationenabled, notificationminutes, asneeded from reminders where usermed = " + usermedID;

		List<ReminderTime> list = new ArrayList<ReminderTime>();

		Cursor cursor = this.db.rawQuery(query, null);
		if (cursor.moveToFirst()) 
		{
			do 
			{
				ReminderTime tmp = new ReminderTime();
				tmp.setReminderID(cursor.getInt(0));
				tmp.setUserID(cursor.getInt(1));
				tmp.setUserMedID(cursor.getInt(2));
				tmp.setRepeatingCount(cursor.getInt(3));
				tmp.setDayofweek(cursor.getInt(4));
				tmp.setHourofday(cursor.getInt(5));
				tmp.setMinute(cursor.getInt(6));
				tmp.setStatictime(cursor.getLong(7));
				tmp.setNotificationenabled(cursor.getInt(8));
				tmp.setNotificationminutes(cursor.getLong(9));
				if(cursor.getInt(10) >0)
					tmp.setAsNeeded(true);
				list.add(tmp);
			} 
			while (cursor.moveToNext());
		}
		if (cursor != null && !cursor.isClosed()) 
		{
			cursor.close();
		}

		db.close();

		return list;
	}

	public boolean addReminderForUsermed(String userID, String usermedID, int repeatingCount, int dayofweek, int hourofday, int minute, long staticTime, boolean notificationEnabled, long notificationMinutes)
	{
		OpenHelper openHelper = new OpenHelper(this.context);
		this.db = openHelper.getWritableDatabase();

		try
		{			
			ContentValues insertValues = new ContentValues();
			insertValues.put("user", userID);
			insertValues.put("usermed", usermedID);
			insertValues.put("repeatingcount", repeatingCount);
			insertValues.put("dayofweek", dayofweek);
			insertValues.put("hourofday", hourofday);
			insertValues.put("minuteofday", minute);
			insertValues.put("statictime", staticTime);
			insertValues.put("notificationenabled", notificationEnabled);
			insertValues.put("notifcationminutes", notificationMinutes);
			db.insert("reminders", null, insertValues);
		}
		catch(Exception ex)
		{
			return false;
		}


		return true;
	}

	public boolean toggleNotification(int reminderid, int enabled)
	{
		OpenHelper openHelper = new OpenHelper(this.context);
		this.db = openHelper.getWritableDatabase();

		try
		{
			//USERMEDS (UMID INTEGER PRIMARY KEY, USER INTEGER, PRODUCTNAME TEXT, DRUGFORM TEXT, DOSAGE TEXT)
			String query = "update reminders set notificationenabled = " + enabled + "  where reminderid = " + reminderid;
			db.execSQL(query);
		}
		catch(Exception ex)
		{
			return false;
		}

		return true;
	}
	
	public List<UserMed> getUserSchedule(String userID)
	{
		OpenHelper openHelper = new OpenHelper(this.context);
		this.db = openHelper.getWritableDatabase();

		String query = "select u.umid, u.productname, u.drugform, u.dosage, u.reason, u.warnings, u.notes from usermeds u where u.deleted != 1 and u.user = " + userID;

		List<UserMed> list = new ArrayList<UserMed>();

		Cursor cursor = this.db.rawQuery(query, null);
		if (cursor.moveToFirst()) 
		{
			do 
			{
				UserMed tmp = new UserMed();
				tmp.setUMID(cursor.getString(0));
				tmp.setProductName(cursor.getString(1));
				tmp.setDrugForm(cursor.getString(2));
				tmp.setDosage(cursor.getString(3));
				tmp.setReason(cursor.getString(4));
				tmp.setWarnings(cursor.getString(5));
				tmp.setNotes(cursor.getString(6));
				tmp.scheduledTimes = getRemindersforUserMed(tmp.getUMID());
				
				list.add(tmp);
			} 
			while (cursor.moveToNext());
		}
		if (cursor != null && !cursor.isClosed()) 
		{
			cursor.close();
		}

		db.close();

		return list;
	}

	public List<String> getDrugDosages(String drugname)
	{

		OpenHelper openHelper = new OpenHelper(this.context);
		this.db = openHelper.getWritableDatabase();
		String query = "select distinct ACTIVE_NUMERATOR_STRENGTH, ACTIVE_INGRED_UNIT from FDAMEDS where PROPRIETARYNAME = '" + drugname + "' order by ACTIVE_NUMERATOR_STRENGTH";

		List<String> list = new ArrayList<String>();

		Cursor cursor = this.db.rawQuery(query, null);
		if (cursor.moveToFirst()) 
		{
			do 
			{
				String row = cursor.getString(0);

				//Hack to separate semicolon delimited values
				if(row.contains(";"))
				{
					String[] spl = row.split(";");
					//for(int i=0;i<spl.length;i++)
					//	list.add(spl[i]);

					list.add(spl[0] + spl[1]);
				}
				else
					list.add(row); 
			} 
			while (cursor.moveToNext());
		}
		if (cursor != null && !cursor.isClosed()) 
		{
			cursor.close();
		}

		db.close();

		return list;
	}

	private static class OpenHelper extends SQLiteOpenHelper 
	{
		Context dbContext;

		OpenHelper(Context context) 
		{
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			dbContext = context;
		}

		@Override
		public void onCreate(SQLiteDatabase db) 
		{	//TODO: DATABSE STRUCTURE MARKING

			//DRUGFORMS
			String createDRUGFORMS = "CREATE TABLE IF NOT EXISTS DRUGFORMS (DFID INTEGER PRIMARY KEY, DRUGFORM TEXT);";
			db.execSQL(createDRUGFORMS);

			//FDAMEDS
			String createFDAMEDS = "CREATE TABLE IF NOT EXISTS FDAMEDS (MedID INTEGER PRIMARY KEY, PRODUCTNAME TEXT);";
			db.execSQL(createFDAMEDS);

			//USERMEDS
			String createUSERMEDS = "CREATE TABLE IF NOT EXISTS USERMEDS (UMID INTEGER PRIMARY KEY, USER INTEGER, PRODUCTNAME TEXT, DRUGFORM TEXT, DOSAGE TEXT, DELETED INTEGER, REASON TEXT, WARNINGS TEXT, NOTES TEXT);";
			db.execSQL(createUSERMEDS);

			//REMINDERS
			String createREMINDERS = "CREATE TABLE IF NOT EXISTS REMINDERS (REMINDERID INTEGER PRIMARY KEY, USER INTEGER, USERMED INTEGER, REPEATINGCOUNT INTEGER, DAYOFWEEK INTEGER, HOUROFDAY INTEGER, MINUTEOFDAY INTEGER, STATICTIME LONG, NOTIFICATIONENABLED INTEGER, NOTIFICATIONMINUTES LONG, ASNEEDED INTEGER);";
			db.execSQL(createREMINDERS);

			//PILLSTAKEN
			String createPILLSTAKEN = "CREATE TABLE IF NOT EXISTS PILLSTAKEN (PILLSTAKENID INTEGER PRIMARY KEY, REMINDERTIME INTEGER, TAKENTIME LONG);";
			db.execSQL(createPILLSTAKEN);

			//USERS
			String createUSER = "CREATE TABLE IF NOT EXISTS USERS (UserID INTEGER PRIMARY KEY, Name TEXT, Password TEXT, LocalToken TEXT, GoogleToken TEXT, FacebookToken TEXT, photo BLOB);";
			db.execSQL(createUSER);

			//POPULATE DB
			insertFDAAssetFile(db);
			insertDrugForms(db);
			createDebugUser(db, "steve", "a", "", "");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
		{
			//needs to NOT drop users,usermeds,reminders, and pillstaken
			//else the user will lose data on upgrade.
			try
			{
				db.execSQL("drop table USERS");
				db.execSQL("drop table FDAMEDS");
				db.execSQL("drop table DRUGFORMS");
				db.execSQL("drop table USERMEDS");
				db.execSQL("drop table REMINDERS");
				db.execSQL("drop table REMINDERTIMES");
				db.execSQL("drop table PILLSTAKEN");
			}
			catch(Exception ex)
			{}
			onCreate(db);
		}

		public boolean insertFDAAssetFile(SQLiteDatabase db)
		{
			int counter = 1;
			try {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(dbContext.getAssets().open("product.txt"), "UTF-8")); 

				String mLine = reader.readLine();
				while (mLine != null) {
					//process
					String[] fieldSplit = mLine.split("\t");
					String query = "insert into FDAMEDS (PRODUCTNAME) values ('"+ scrubInput(fieldSplit[0]) + "')";
					try
					{

						db.execSQL(query);
						Log.v("Imported", ""+counter++);

					}
					catch(Exception ex){
						Log.v("SQL", query);
					}
					mLine = reader.readLine(); 
				}

				reader.close();
				return true;

			} catch (IOException e) {
				return false;
			}
		}

		public void createDebugUser(SQLiteDatabase db, String name, String password, String GoogleToken, String FacebookToken)
		{


			try
			{
				//Need to check if this user already exists first

				//Generate a localtoken
				String LocalToken = UUID.randomUUID().toString();

				//insert into DB
				db.execSQL("insert into Users (Name, Password, LocalToken, GoogleToken, FacebookToken) values('" + name + "', '" + password + "', '" + LocalToken + "', '" + GoogleToken + "', '" + FacebookToken + "')");
			}
			catch(Exception ex)
			{
			}

		}

		public boolean insertDrugForms(SQLiteDatabase db)
		{
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('AEROSOL')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('AEROSOL, FOAM')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('AEROSOL, METERED')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('AEROSOL, POWDER')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('AEROSOL, SPRAY')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('BAR, CHEWABLE')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('BEAD')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('BEAD, IMPLANT, EXTENDED RELEASE')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('BLOCK')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('CAPSULE')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('CAPSULE, COATED')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('CAPSULE, COATED PELLETS')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('CAPSULE, COATED, EXTENDED RELEASE')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('CAPSULE, DELAYED RELEASE')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('CAPSULE, DELAYED RELEASE PELLETS')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('CAPSULE, EXTENDED RELEASE')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('CAPSULE, FILM COATED, EXTENDED RELEASE')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('CAPSULE, GELATIN COATED')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('CAPSULE, LIQUID FILLED')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('CAPSULE, LIQUID FILLED')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('CEMENT')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('CIGARETTE')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('CLOTH')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('CONCENTRATE')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('CONE')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('CORE, EXTENDED RELEASE')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('CREAM')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('CREAM, AUGMENTED')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('CRYSTAL')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('CULTURE')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('DIAPHRAGM')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('DISC')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('DOUCHE')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('DRESSING')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('DRUG DELIVERY SYSTEM')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('ELIXIR')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('EMULSION')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('ENEMA')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('EXTRACT')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('FIBER, EXTENDED RELEASE')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('FILM')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('FILM, EXTENDED RELEASE')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('FILM, SOLUBLE')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('FOR SOLUTION')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('FOR SUSPENSION')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('FOR SUSPENSION, EXTENDED RELEASE')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('GAS')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('GEL')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('GEL, DENTIFRICE')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('GEL, METERED')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('GENERATOR')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('GLOBULE')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('GRAFT')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('GRANULE')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('GRANULE, DELAYED RELEASE')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('GRANULE, EFFERVESCENT')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('GRANULE, FOR SOLUTION')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('GRANULE, FOR SUSPENSION')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('GRANULE, FOR SUSPENSION, EXTENDED RELEASE')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('GUM')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('GUM, CHEWING')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('GUM, RESIN')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('IMPLANT')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('INHALANT')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('INJECTABLE, LIPOSOMAL')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('INJECTION')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('INJECTION, EMULSION')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('INJECTION, LIPID COMPLEX')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('INJECTION, POWDER, FOR SOLUTION')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('INJECTION, POWDER, FOR SUSPENSION')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('INJECTION, POWDER, FOR SUSPENSION, EXTENDED RELEASE')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('INJECTION, POWDER, LYOPHILIZED, FOR LIPOSOMAL SUSPENSION')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('INJECTION, POWDER, LYOPHILIZED, FOR SOLUTION')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('INJECTION, POWDER, LYOPHILIZED, FOR SUSPENSION')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('INJECTION, POWDER, LYOPHILIZED, FOR SUSPENSION, EXTENDED RELEASE')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('INJECTION, SOLUTION')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('INJECTION, SOLUTION, CONCENTRATE')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('INJECTION, SUSPENSION')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('INJECTION, SUSPENSION, EXTENDED RELEASE')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('INJECTION, SUSPENSION, LIPOSOMAL')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('INJECTION, SUSPENSION, SONICATED')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('INSERT')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('INSERT, EXTENDED RELEASE')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('INTRAUTERINE DEVICE')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('IRRIGANT')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('JELLY')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('KIT')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('LINER, DENTAL')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('LINIMENT')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('LIPSTICK')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('LIQUID')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('LIQUID, EXTENDED RELEASE')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('LOTION')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('LOTION, AUGMENTED')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('LOTION/SHAMPOO')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('LOZENGE')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('MOUTHWASH')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('NOT APPLICABLE')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('OIL')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('OINTMENT')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('OINTMENT, AUGMENTED')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('PACKING')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('PASTE')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('PASTE, DENTIFRICE')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('PASTILLE')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('PATCH')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('PATCH, EXTENDED RELEASE')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('PATCH, EXTENDED RELEASE, ELECTRICALLY CONTROLLED')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('PELLET')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('PELLET, IMPLANTABLE')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('PELLETS, COATED, EXTENDED RELEASE')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('PILL')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('PLASTER')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('POULTICE')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('POWDER')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('POWDER, DENTIFRICE')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('POWDER, FOR SOLUTION')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('POWDER, FOR SUSPENSION')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('POWDER, METERED')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('RING')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('RINSE')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('SALVE')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('SHAMPOO')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('SHAMPOO, SUSPENSION')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('SOAP')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('SOLUTION')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('SOLUTION, CONCENTRATE')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('SOLUTION, FOR SLUSH')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('SOLUTION, GEL FORMING / DROPS')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('SOLUTION, GEL FORMING, EXTENDED RELEASE')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('SOLUTION/ DROPS')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('SPONGE')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('SPRAY')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('SPRAY, METERED')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('SPRAY, SUSPENSION')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('STICK')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('STRIP')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('SUPPOSITORY')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('SUPPOSITORY, EXTENDED RELEASE')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('SUSPENSION')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('SUSPENSION, EXTENDED RELEASE')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('SUSPENSION/ DROPS')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('SUTURE')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('SWAB')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('SYRUP')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('TABLET')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('TABLET, CHEWABLE')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('TABLET, COATED')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('TABLET,COATED PARTICLES')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('TABLET, DELAYED RELEASE')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('TABLET, DELAYED RELEASE PARTICLES')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('TABLET, DISPERSIBLE')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('TABLET, EFFERVESCENT')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('TABLET, EXTENDED RELEASE')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('TABLET, FILM COATED')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('TABLET, FILM COATED, EXTENDED RELEASE')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('TABLET, FOR SOLUTION')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('TABLET, FOR SUSPENSION')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('TABLET, MULTILAYER')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('TABLET, MULTILAYER, EXTENDED RELEASE')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('TABLET, ORALLY DISINTEGRATING')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('TABLET, ORALLY DISINTEGRATING, DELAYED RELEASE')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('TABLET, SOLUBLE')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('TABLET, SUGAR COATED')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('TAMPON')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('TAPE')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('TINCTURE')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('TROCHE')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('UNASSIGNED')");
			db.execSQL("insert into DRUGFORMS (DRUGFORM) values ('WAFER')");
			return true;
		}
	}
}