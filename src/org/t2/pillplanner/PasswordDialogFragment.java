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
import android.widget.EditText;

public class PasswordDialogFragment extends DialogFragment {
	

	//public EditText etUsername;
	private EditText etPassword;
	private Button btnSubmit;
	
	public interface LoginListener{
	     void LoginComplete(String password);
	  }
		
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View v = (View) inflater.inflate(R.layout.dialog_login, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(v);

        //etUsername = (EditText) v.findViewById(R.id.etUsername);
        etPassword = (EditText) v.findViewById(R.id.etPassword);
        btnSubmit = (Button) v.findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				
				// Return input text to activity
				((LoginListener) getTargetFragment()).LoginComplete(etPassword.getText().toString().trim());
	            PasswordDialogFragment.this.dismiss();
				
			}
		});
        
		Dialog result = builder.create();
		result.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		
		return result; //builder.create();
	}

}
