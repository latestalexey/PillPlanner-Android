package org.t2.pillplanner;

import java.util.ArrayList;
import java.util.Arrays;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.t2.pillplanner.classes.DatabaseHelper;
import org.t2.pillplanner.classes.Global;
import org.t2.pillplanner.classes.ReminderTime;
import org.t2.pillplanner.classes.UserMed;

import com.actionbarsherlock.app.SherlockFragment;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class RemindersFragment extends SherlockFragment {

	@SuppressWarnings("unused")
	private LinearLayout medParent;
	private static ArrayList<UserMed> SCHEDULE = new ArrayList<UserMed>();
	private static ArrayList<String> WEEKDAYS = new ArrayList<String>(Arrays.asList("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"));

	private Context ctx;
	private DatabaseHelper db;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		ctx = getSherlockActivity();
		db = new DatabaseHelper(ctx);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view; 
		view = inflater.inflate(R.layout.fragment_reminders, null);

		medParent = (LinearLayout) view.findViewById(R.id.list);

		return view;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		//final Bundle state = savedInstanceState != null ? savedInstanceState : getArguments();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	private void refreshData()
	{
		new AsyncTask<Void, Integer, Boolean>() { 
			protected void onPostExecute(Boolean result) {
				layoutMeds();
			}

			@Override
			protected Boolean doInBackground(Void... params) {
				SCHEDULE = (ArrayList<UserMed>) db.getUserSchedule(Global.currentUser.getUserID());
				return null;
			}
		}.execute(); // start the background processing
	}

	@Override
	public void onResume() {
		super.onResume();

		refreshData();

	}

	public void layoutMeds()
	{
		medParent.removeAllViews();

		for(int i= 0; i< SCHEDULE.size(); i++)
		{
			View child = getSherlockActivity().getLayoutInflater().inflate(R.layout.include_reminders_row, null);


			final ImageView mToggle = (ImageView)child.findViewById(R.id.med_toggle);
			final LinearLayout mHeader = (LinearLayout)child.findViewById(R.id.med_header);
			final LinearLayout mWrapper = (LinearLayout)child.findViewById(R.id.med_list);

			//final LinearLayout mList = (LinearLayout)child.findViewById(R.id.med_list);
			mHeader.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					final boolean visible = mWrapper.getVisibility() == View.VISIBLE;
					mToggle.setImageResource(visible ? R.drawable.ab_collapse : R.drawable.ab_expand);
					mWrapper.setVisibility(visible ? View.GONE : View.VISIBLE);
				}
			});


			//Set the med title on the collapsible control
			TextView title = (TextView) child.findViewById(R.id.tvMedTitle);
			title.setText(SCHEDULE.get(i).getProductName());

			for(int t=0; t<SCHEDULE.get(i).scheduledTimes.size();t++)
			{
				View sub = getSherlockActivity().getLayoutInflater().inflate(R.layout.include_reminders_subrow, null);

				ReminderTime cReminder = SCHEDULE.get(i).scheduledTimes.get(t);

				//DateTime rawDate = new DateTime(cReminder.getStatictime());

				if(cReminder.getStatictime() > 0)
				{
					DateTime rawDate = new DateTime(cReminder.getStatictime());
					((TextView) sub.findViewById(R.id.title)).setText(WEEKDAYS.get(rawDate.getDayOfWeek()) + ", " + rawDate.getMonthOfYear() + "/" + rawDate.getDayOfMonth() + "/" + rawDate.getYear() + " @ " + rawDate.getHourOfDay() + ":" + rawDate.getMinuteOfHour());
				}
				else
					((TextView) sub.findViewById(R.id.title)).setText(WEEKDAYS.get(cReminder.getDayofweek()) + " @ " + cReminder.getHourofday() + ":" + cReminder.getMinute());

				//Turn on/off the notifications
				final ImageView icon = ((ImageView) sub.findViewById(R.id.ivNotification));
				if(cReminder.getNotificationenabled() == 0)
					icon.setImageResource(R.drawable.ab_clock);
				else
					icon.setImageResource(R.drawable.ab_alarm);
				
				icon.setTag(i + ":" + t);
				icon.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						String[] positionSplit = icon.getTag().toString().split(":");
						ReminderTime rt = SCHEDULE.get(Integer.parseInt(positionSplit[0])).scheduledTimes.get(Integer.parseInt(positionSplit[1]));

						if(rt.getNotificationenabled() == 0)
							db.toggleNotification(rt.getReminderID(), 1);
						else
							db.toggleNotification(rt.getReminderID(), 0);
						
						refreshData();
					}
				});

				mWrapper.addView(sub);
			}
			medParent.addView(child);		

		}
	}

}
