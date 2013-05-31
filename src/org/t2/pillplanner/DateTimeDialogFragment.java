package org.t2.pillplanner;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;


public class DateTimeDialogFragment extends DialogFragment {

	private Button btnAdd;
	private TimePicker timePicker;
	private DatePicker datePicker;
	
	public interface OneTimeListener{
	     void OneTimeComplete(String date, int hour, int minute);
	  }
		
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View v = (View) inflater.inflate(R.layout.dialog_datetime, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(v);

        timePicker = (TimePicker) v.findViewById(R.id.timePicker);
        datePicker = (DatePicker) v.findViewById(R.id.datePicker);
        
        btnAdd = (Button) v.findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				
				// Return selections text to activity
				((OneTimeListener) getTargetFragment()).OneTimeComplete(datePicker.getMonth() + "/" + datePicker.getDayOfMonth()+"/" + datePicker.getYear(), timePicker.getCurrentHour(), timePicker.getCurrentMinute());
				DateTimeDialogFragment.this.dismiss();
				
			}
		});
        
		Dialog result = builder.create();
		result.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		
		return result; //builder.create();
	}

}
