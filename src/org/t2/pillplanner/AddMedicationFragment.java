package org.t2.pillplanner;

import java.util.ArrayList;
import java.util.Arrays;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.t2.pillplanner.classes.DatabaseHelper;
import org.t2.pillplanner.classes.Global;
import org.t2.pillplanner.classes.ReminderTime;
//import org.t2.pillplanner.classes.NDCDrug;
import org.t2.pillplanner.classes.UserMed;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;

public class AddMedicationFragment extends SherlockFragment implements ScheduleDialogFragment.ScheduleListener, DateTimeDialogFragment.OneTimeListener {
	@SuppressWarnings("unused")
	private static final String TAG = "AddMedicationFragment";
	private ViewGroup mReminderHeader, mReminderWrapper, mDetailsHeader, mDetailsWrapper, mDosageHeader, mDosageWrapper, mScheduleHeader, mScheduleWrapper, mProviderHeader, mProviderWrapper;
	private ImageView mReminderToggle, mDetailsToggle, mDosageToggle, mScheduleToggle, mProviderToggle;

	private Context ctx;
	private DatabaseHelper db;
	private DrugAdapter mNameAdapter;
	private FormAdapter mFormAdapter;
	private ReminderAdapter mReminderAdapter;
	private static ArrayList<String> DRUGNAMES;
	private static ArrayList<String> DRUGFORMS;
	private static ArrayList<ReminderTime> SCHEDULETIMES = new ArrayList<ReminderTime>();
	private static ArrayList<String> WEEKDAYS = new ArrayList<String>(Arrays.asList("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"));

	//private ArrayList<NDCDrug> drugResults;

	private EditText etDosage;
	private AutoCompleteTextView etSearch;
	private Spinner etDrugForm;
	private Spinner spScheduledTimes;
	private UserMed editReminder;

	private EditText etReason;
	private EditText etWarnings;
	private EditText etNotes;

	private CheckBox chkAsNeeded;
	private LinearLayout llAsNeeded;

	private ScheduleDialogFragment scheduleDialog;
	private DateTimeDialogFragment dateTimeDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ctx = getSherlockActivity();
		db = new DatabaseHelper(ctx);

		//If a med is passed into the bundle, edit mode is on
		try
		{
			Bundle extras = getSherlockActivity().getIntent().getExtras();
			editReminder = (UserMed) extras.getSerializable("EXISTINGMED");
		}
		catch(Exception ex){

		}

		refreshData();

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_addmedication, null);

		this.getSherlockActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		if(editReminder == null)
		{
			getSherlockActivity().getSupportActionBar().setTitle("Add Medication");
		}
		else
		{
			getSherlockActivity().getSupportActionBar().setTitle("Edit Medication");
			((TextView)v.findViewById(R.id.tvAddDrug)).setText("Save Changes");
		}

		etDrugForm = (Spinner) v.findViewById(R.id.etDrugForm);

		etSearch = (AutoCompleteTextView) v.findViewById(R.id.etSearch);
		if(editReminder != null)
			etSearch.setText(editReminder.getProductName());

		etDosage = (EditText) v.findViewById(R.id.etDosage);
		if(editReminder != null)
			etDosage.setText(editReminder.getDosage());

		etReason = (EditText) v.findViewById(R.id.etReason);
		if(editReminder != null)
			etReason.setText(editReminder.getReason());

		etWarnings = (EditText) v.findViewById(R.id.etWarnings);
		if(editReminder != null)
			etWarnings.setText(editReminder.getWarnings());

		etNotes = (EditText) v.findViewById(R.id.etNotes);
		if(editReminder != null)
			etNotes.setText(editReminder.getNotes());

		llAsNeeded = (LinearLayout) v.findViewById(R.id.llAsNeeded);
		chkAsNeeded = (CheckBox) v.findViewById(R.id.chkAsNeeded);
		chkAsNeeded.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(chkAsNeeded.isChecked())
					llAsNeeded.setVisibility(View.GONE);
				else
					llAsNeeded.setVisibility(View.VISIBLE);
			}
		});


		Button btnScanner = (Button) v.findViewById(R.id.btnScan);
		btnScanner.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				startScan();
			}
		});

		Button btnPharmacy = (Button) v.findViewById(R.id.btnPharmacy);
		btnPharmacy.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				launchMapsActivity();
			}
		});

		Button btnOneTime = (Button) v.findViewById(R.id.btnOneTime);
		btnOneTime.setOnClickListener(new OnClickListener(){
			public void onClick(View v){

				FragmentManager fm = getSherlockActivity().getSupportFragmentManager();
				@SuppressWarnings("unused")
				FragmentTransaction ft =fm.beginTransaction();

				// Create and show the dialog.
				dateTimeDialog = new DateTimeDialogFragment();
				dateTimeDialog.setTargetFragment(AddMedicationFragment.this, 112);
				dateTimeDialog.show(fm, "");

			}
		});

		Button btnAddSchedule = (Button) v.findViewById(R.id.btnAddSchedule);
		btnAddSchedule.setOnClickListener(new OnClickListener(){
			public void onClick(View v){

				FragmentManager fm = getSherlockActivity().getSupportFragmentManager();
				@SuppressWarnings("unused")
				FragmentTransaction ft =fm.beginTransaction();

				// Create and show the dialog.
				scheduleDialog = new ScheduleDialogFragment();
				scheduleDialog.setTargetFragment(AddMedicationFragment.this, 112);
				scheduleDialog.show(fm, "");

			}
		});

		Button btnDelSchedule = (Button) v.findViewById(R.id.btnDelSchedule);
		btnDelSchedule.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				try
				{
					String timeDisplay = "";
					ReminderTime cReminder = SCHEDULETIMES.get(spScheduledTimes.getSelectedItemPosition());
					if(cReminder.getStatictime() > 0)
					{
						DateTime tmp = new DateTime(cReminder.getStatictime());				
						timeDisplay = tmp.getMonthOfYear() + "/" + tmp.getDayOfMonth() + "/" + tmp.getYear() + " " + tmp.getHourOfDay() + ":" + tmp.getMinuteOfHour();
					}
					else
						timeDisplay = WEEKDAYS.get(cReminder.getDayofweek()) + " @ " + cReminder.getHourofday() + ":" + cReminder.getMinute();

					new AlertDialog.Builder(getSherlockActivity())
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setTitle(timeDisplay)
					.setMessage("Are you sure you want to remove this scheduled time?")
					.setPositiveButton("Yes", new DialogInterface.OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialog, int which) {
							SCHEDULETIMES.remove(spScheduledTimes.getSelectedItemPosition());
							refreshData();
						}

					})
					.setNegativeButton("No", null)
					.show();
				}
				catch(Exception ex){}

			}
		});

		spScheduledTimes = (Spinner) v.findViewById(R.id.spScheduledTimes);

		mDosageToggle = (ImageView)v.findViewById(R.id.dosage_toggle);
		mDosageHeader = (ViewGroup)v.findViewById(R.id.dosage_header);
		mDosageWrapper = (ViewGroup)v.findViewById(R.id.dosage_wrapper);
		mDosageHeader.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				final boolean visible = mDosageWrapper.getVisibility() == View.VISIBLE;
				mDosageToggle.setImageResource(visible ? R.drawable.ab_collapse : R.drawable.ab_expand);
				mDosageWrapper.setVisibility(visible ? View.GONE : View.VISIBLE);
			}
		});

		mScheduleToggle = (ImageView)v.findViewById(R.id.schedule_toggle);
		mScheduleHeader = (ViewGroup)v.findViewById(R.id.schedule_header);
		mScheduleWrapper = (ViewGroup)v.findViewById(R.id.schedule_wrapper);
		mScheduleHeader.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				final boolean visible = mScheduleWrapper.getVisibility() == View.VISIBLE;
				mScheduleToggle.setImageResource(visible ? R.drawable.ab_collapse : R.drawable.ab_expand);
				mScheduleWrapper.setVisibility(visible ? View.GONE : View.VISIBLE);
			}
		});

		mReminderToggle = (ImageView)v.findViewById(R.id.reminder_toggle);
		mReminderHeader = (ViewGroup)v.findViewById(R.id.reminder_header);
		mReminderWrapper = (ViewGroup)v.findViewById(R.id.reminder_wrapper);
		mReminderHeader.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				final boolean visible = mReminderWrapper.getVisibility() == View.VISIBLE;
				mReminderToggle.setImageResource(visible ? R.drawable.ab_collapse : R.drawable.ab_expand);
				mReminderWrapper.setVisibility(visible ? View.GONE : View.VISIBLE);
			}
		});

		mDetailsToggle = (ImageView)v.findViewById(R.id.details_toggle);
		mDetailsHeader = (ViewGroup)v.findViewById(R.id.details_header);
		mDetailsWrapper = (ViewGroup)v.findViewById(R.id.details_wrapper);
		mDetailsHeader.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				final boolean visible = mDetailsWrapper.getVisibility() == View.VISIBLE;
				mDetailsToggle.setImageResource(visible ? R.drawable.ab_collapse : R.drawable.ab_expand);
				mDetailsWrapper.setVisibility(visible ? View.GONE : View.VISIBLE);
			}
		});

		mProviderToggle = (ImageView)v.findViewById(R.id.provider_toggle);
		mProviderHeader = (ViewGroup)v.findViewById(R.id.provider_header);
		mProviderWrapper = (ViewGroup)v.findViewById(R.id.provider_wrapper);
		mProviderHeader.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				final boolean visible = mProviderWrapper.getVisibility() == View.VISIBLE;
				mProviderToggle.setImageResource(visible ? R.drawable.ab_collapse : R.drawable.ab_expand);
				mProviderWrapper.setVisibility(visible ? View.GONE : View.VISIBLE);
			}
		});

		LinearLayout btnAddDrug = (LinearLayout) v.findViewById(R.id.llAddDrug);
		btnAddDrug.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				//New medication
				if(editReminder == null)
				{

					//Add new medication
					@SuppressWarnings("unused")
					long usermedid = db.addDrugToUser(Global.currentUser.getUserID(), etSearch.getText().toString().trim(), etDrugForm.getSelectedItem().toString().trim(), etDosage.getText().toString().trim(), etReason.getText().toString().trim(), etWarnings.getText().toString().trim(), etNotes.getText().toString().trim());

					//Loop over all the scheduled times and add to reminders
					for(int i=0; i < SCHEDULETIMES.size(); i++)
					{
						ReminderTime cReminder = SCHEDULETIMES.get(i);
						long timeid = db.addTimeToUserSchedule(Global.currentUser.getUserID(), usermedid, cReminder.getNotificationenabled(), cReminder.getRepeatingCount(), cReminder.getNotificationminutes(), cReminder.getAsNeeded(), cReminder.getStatictime(), cReminder.getDayofweek(), cReminder.getHourofday(), cReminder.getMinute());
					
					}

					if(usermedid >= 0)
					{
						Toast.makeText(ctx, "Medication was added to your schedule", Toast.LENGTH_LONG).show();
						getSherlockActivity().finish();
					}
					else
					{
						Toast.makeText(ctx, "Failed to add medication to schedule...", Toast.LENGTH_LONG).show();
					}
				}
				//Edit medication
				else
				{
					boolean success = db.editUserScheduledDrug(editReminder.getUMID(), Global.currentUser.getUserID(), etSearch.getText().toString().trim(), etDrugForm.getSelectedItem().toString().trim(), etDosage.getText().toString().trim(), etReason.getText().toString().trim(), etWarnings.getText().toString().trim(), etNotes.getText().toString().trim());

					if(success)
					{
						Toast.makeText(ctx, "Medication was edited successfully.", Toast.LENGTH_LONG).show();
						getSherlockActivity().finish();
					}
					else
					{
						Toast.makeText(ctx, "Failed to edit medication...", Toast.LENGTH_LONG).show();
					}
				}

			}
		});

		return v;
	}

	private void refreshData()
	{
		new AsyncTask<Void, Integer, Boolean>() { 
			protected void onPostExecute(Boolean result) {
				mNameAdapter = new DrugAdapter(ctx);
				mFormAdapter = new FormAdapter(ctx);
				mReminderAdapter = new ReminderAdapter(ctx);

				etSearch.setAdapter(mNameAdapter);
				etDrugForm.setAdapter(mFormAdapter);
				spScheduledTimes.setAdapter(mReminderAdapter);

				if(editReminder != null)
				{
					int position = DRUGFORMS.indexOf(editReminder.getDrugForm());
					etDrugForm.setSelection(position);
				}
			}

			@Override
			protected Boolean doInBackground(Void... params) {
				DRUGNAMES = (ArrayList<String>) db.getDrugNameContains("");
				DRUGFORMS = (ArrayList<String>) db.getDrugForms();
				return null;
			}
		}.execute(); // start the background processing
	}

	@Override
	public void ScheduleComplete(String day, String hour, String minute) {
		Toast.makeText(ctx, "Adding: " + day + " - " + hour + ":" + minute, Toast.LENGTH_LONG).show();
		ReminderTime tmp = new ReminderTime();
		tmp.setDayofweek(Integer.parseInt(day));
		tmp.setHourofday(Integer.parseInt(hour));
		tmp.setMinute(Integer.parseInt(minute));

		SCHEDULETIMES.add(tmp);
		refreshData();

	}

	@Override
	public void OneTimeComplete(String date, int hour, int minute) {
		Toast.makeText(ctx, "Adding: " + date + " " + hour + ":" + minute, Toast.LENGTH_LONG).show();
		ReminderTime tmp = new ReminderTime();

		DateTimeFormatter formatter = DateTimeFormat.forPattern("MM/dd/yyyy HH:mm");
		DateTime tmpDate = formatter.parseDateTime(date + " " + hour + ":" + minute);
		tmp.setStatictime(tmpDate.getMillis());

		SCHEDULETIMES.add(tmp);
		refreshData();

	}

	private void launchMapsActivity()
	{
		Intent intent = new Intent(getActivity(), PharmacyActivity.class);
		startActivity(intent);
	}

	public void startScan()
	{
		Intent intent = new Intent("com.google.zxing.client.android.SCAN");
		//intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
		startActivityForResult(intent, 0);
	}

	private static final class DrugAdapter extends ArrayAdapter<String> {
		public DrugAdapter(Context ctx) {
			super(ctx, android.R.layout.simple_list_item_1, DRUGNAMES);
		}
	}
	private static final class FormAdapter extends ArrayAdapter<String> {
		public FormAdapter(Context ctx) {
			super(ctx, android.R.layout.simple_list_item_1, DRUGFORMS);
		}
	}
	private static class ReminderAdapter extends ArrayAdapter<ReminderTime> {

		public ReminderAdapter(Context ctx) {

			super(ctx, R.layout.include_remindertimes_row, SCHEDULETIMES);
		}

		@Override
		public View getDropDownView(int position, View convertView, ViewGroup parent) {

			TextView v = (TextView)  super.getDropDownView(position, convertView, parent);
			if (v == null) {
				v = new TextView(getContext());
			}

			ReminderTime cReminder = getItem(position);
			if(cReminder.getStatictime() > 0)
			{
				DateTime tmp = new DateTime(cReminder.getStatictime());				
				v.setText(tmp.getMonthOfYear() + "/" + tmp.getDayOfMonth() + "/" + tmp.getYear() + " "  + tmp.getHourOfDay() + ":" + tmp.getMinuteOfHour());
			}
			else
				v.setText(WEEKDAYS.get(cReminder.getDayofweek()) + " @ " + cReminder.getHourofday() + ":" + cReminder.getMinute());
			return v;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				v = LayoutInflater.from(getContext()).inflate(R.layout.include_remindertimes_row, null);
			}

			ReminderTime cReminder = getItem(position);
			if(cReminder.getStatictime() > 0)
			{
				DateTime tmp = new DateTime(cReminder.getStatictime());				
				((TextView)v.findViewById(R.id.tvSchedule)).setText(tmp.getMonthOfYear() + "/" + tmp.getDayOfMonth() + "/" + tmp.getYear() + " "  + tmp.getHourOfDay() + ":" + tmp.getMinuteOfHour());
			}
			else
				((TextView)v.findViewById(R.id.tvSchedule)).setText(WEEKDAYS.get(cReminder.getDayofweek()) + " @ " + cReminder.getHourofday() + ":" + cReminder.getMinute());

			return v;
		}
	}
}
