package org.t2.pillplanner;

import com.actionbarsherlock.app.SherlockFragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MapInfoFragment extends SherlockFragment {
			
	LinearLayout llInfo;
	ImageView ivClose;
	TextView tvName;
	TextView tvAddress;
	TextView tvPhone;
	
	Button btnCall;
	Button btnNavigate;
	
	public void setInfoVisiblility(boolean visible)
	{
		if(visible)
			llInfo.setVisibility(View.VISIBLE);
		else
			llInfo.setVisibility(View.GONE);
	}
	
	public void setDetails(String name, String address, String phone)
	{
		tvName.setText(name);
		tvAddress.setText(address);
		tvPhone.setText(phone);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view; 
		view = inflater.inflate(R.layout.fragment_mapinfo, null);
		
		llInfo = (LinearLayout) view.findViewById(R.id.llInfo);
		ivClose = (ImageView) view.findViewById(R.id.ivClose);
		ivClose.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				setInfoVisiblility(false);
			}
		});
		
		tvName = (TextView) view.findViewById(R.id.tvName);
		tvAddress = (TextView) view.findViewById(R.id.tvAddress);
		tvPhone = (TextView) view.findViewById(R.id.tvPhone);
		
		btnCall = (Button) view.findViewById(R.id.btnCall);
		btnCall.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + tvPhone.getText().toString())));
			}
		});
		
		btnNavigate = (Button) view.findViewById(R.id.btnNavigate);
		btnNavigate.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" + tvAddress.getText().toString())));
			}
		});
		
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
}

