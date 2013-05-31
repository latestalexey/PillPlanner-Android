package org.t2.pillplanner;

import java.util.ArrayList;
import java.util.Arrays;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;
import org.t2.pillplanner.classes.DatabaseHelper;
import org.t2.pillplanner.classes.Global;
import org.t2.pillplanner.classes.ReminderTime;
import org.t2.pillplanner.classes.UserMed;
import org.t2.pillplanner.classes.LocalDateRange;

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

public class OverviewFragment extends SherlockFragment {

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
		view = inflater.inflate(R.layout.fragment_overview, null);

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
				layoutFeed();
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

	public void layoutFeed()
	{
		medParent.removeAllViews();

		//Loop over three weeks of days starting one week previous to current to one past
		DateTime now = new DateTime();
		LocalDate start = now.toLocalDate().minusWeeks(1);
		LocalDate end = now.toLocalDate().plusWeeks(1);
		for (LocalDate date : new LocalDateRange(start, end))
		{
			View child = getSherlockActivity().getLayoutInflater().inflate(R.layout.include_overview_row, null);

			final ImageView mToggle = (ImageView)child.findViewById(R.id.med_toggle);
			final LinearLayout mHeader = (LinearLayout)child.findViewById(R.id.med_header);
			final LinearLayout mWrapper = (LinearLayout)child.findViewById(R.id.med_list);

			
			//Set the date title on the collapsible control
			TextView title = (TextView) child.findViewById(R.id.tvMedTitle);
			title.setText(date.toString("EE, MM/dd/yyyy"));

			boolean hasEntry = false;
			for(int i=0; i<SCHEDULE.size();i++)
			{

				for(int t=0; t<SCHEDULE.get(i).scheduledTimes.size();t++)
				{
					View sub = getSherlockActivity().getLayoutInflater().inflate(R.layout.include_overview_subrow, null);
					ReminderTime cReminder = SCHEDULE.get(i).scheduledTimes.get(t);

					if(cReminder.getStatictime() <= 0)
					{
						if(cReminder.getDayofweek() == date.getDayOfWeek())
						{
							hasEntry = true;
							((TextView) sub.findViewById(R.id.title)).setText(SCHEDULE.get(i).getProductName());
							((TextView) sub.findViewById(R.id.details)).setText(SCHEDULE.get(i).getDosage() + " at: " + cReminder.getHourofday() +":" + cReminder.getMinute());
							mWrapper.addView(sub);
						}
					}
				}
			}
			
			if(!hasEntry)
			{
				mWrapper.setVisibility(View.GONE);
				mToggle.setVisibility(View.GONE);
			}
			else
			{
				//final LinearLayout mList = (LinearLayout)child.findViewById(R.id.med_list);
				mHeader.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						final boolean visible = mWrapper.getVisibility() == View.VISIBLE;
						mToggle.setImageResource(visible ? R.drawable.ab_collapse : R.drawable.ab_expand);
						mWrapper.setVisibility(visible ? View.GONE : View.VISIBLE);
					}
				});

			}
			medParent.addView(child);
		}


	}

}
