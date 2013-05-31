package org.t2.pillplanner;

import java.util.ArrayList;
import java.util.Arrays;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;


public class ScheduleDialogFragment extends DialogFragment {

	private Spinner spWeekday, spHour, spMinute;
	
	private WeekdayAdapter mWeekdayAdapter;
	private HourAdapter mHourAdapter;
	private MinuteAdapter mMinuteAdapter;

	private static ArrayList<String> WEEKDAYS;
	private static ArrayList<String> HOURS;
	private static ArrayList<String> MINUTES;

	private Button btnAddSchedule;
	
	public interface ScheduleListener{
	     void ScheduleComplete(String day, String hour, String minute);
	  }
		
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View v = (View) inflater.inflate(R.layout.dialog_schedule, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(v);
        
        WEEKDAYS = new ArrayList<String>(Arrays.asList("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"));
        HOURS = new ArrayList<String>(Arrays.asList("00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11","12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23"));
        MINUTES = new ArrayList<String>(Arrays.asList("00", "45", "30", "15"));

        mWeekdayAdapter = new WeekdayAdapter(getActivity());
        spWeekday = (Spinner) v.findViewById(R.id.spWeekday);
        spWeekday.setAdapter(mWeekdayAdapter);
				
        mHourAdapter = new HourAdapter(getActivity());
        spHour = (Spinner) v.findViewById(R.id.spHour);
        spHour.setAdapter(mHourAdapter);

        mMinuteAdapter = new MinuteAdapter(getActivity());
        spMinute = (Spinner) v.findViewById(R.id.spMinute);
        spMinute.setAdapter(mMinuteAdapter);
				
        btnAddSchedule = (Button) v.findViewById(R.id.btnAddSchedule);
        btnAddSchedule.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				
				// Return selections text to activity
				String day = "" + spWeekday.getSelectedItemPosition();
				String hour = HOURS.get(spHour.getSelectedItemPosition());
				String minute = MINUTES.get(spMinute.getSelectedItemPosition());
				((ScheduleListener) getTargetFragment()).ScheduleComplete(day, hour, minute);
				ScheduleDialogFragment.this.dismiss();
				
			}
		});
        
		Dialog result = builder.create();
		result.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		
		return result; //builder.create();
	}
	
	private static final class WeekdayAdapter extends ArrayAdapter<String> {
		public WeekdayAdapter(Context ctx) {
			super(ctx, android.R.layout.simple_list_item_1, WEEKDAYS);
		}
	}
	private static final class HourAdapter extends ArrayAdapter<String> {
		public HourAdapter(Context ctx) {
			super(ctx, android.R.layout.simple_list_item_1, HOURS);
		}
	}
	private static final class MinuteAdapter extends ArrayAdapter<String> {
		public MinuteAdapter(Context ctx) {
			super(ctx, android.R.layout.simple_list_item_1, MINUTES);
		}
	}

}
