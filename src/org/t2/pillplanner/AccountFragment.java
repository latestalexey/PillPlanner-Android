package org.t2.pillplanner;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.t2.pillplanner.classes.DatabaseHelper;
import org.t2.pillplanner.classes.Global;
import org.t2.pillplanner.classes.User;

import com.actionbarsherlock.app.SherlockFragment;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class AccountFragment extends SherlockFragment {

    private Context ctx;
	private DatabaseHelper db;
	private ImageView ivPhoto;
	private Bitmap mImageBitmap;

	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		ctx = getSherlockActivity();
		db = new DatabaseHelper(ctx);
		
		getSherlockActivity().getSupportActionBar().setTitle("Edit Account");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.fragment_account, null);

		((TextView)v.findViewById(R.id.username)).setText(Global.currentUser.getName());

		ivPhoto = (ImageView) v.findViewById(R.id.usericon);
		if(Global.currentUser.getUserPhoto() != null)
			ivPhoto.setImageBitmap(Global.currentUser.getUserPhoto());

		
		ivPhoto.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dispatchTakePictureIntent(112);
			}
		});

		return v;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		//final Bundle state = savedInstanceState != null ? savedInstanceState : getArguments();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		handleSmallCameraPhoto(data);
	}

	private void handleSmallCameraPhoto(Intent intent) {
		if(intent != null)
		{
			Bundle extras = intent.getExtras();
			mImageBitmap = (Bitmap) extras.get("data");
			ivPhoto.setImageBitmap(mImageBitmap);
			
			//Get bytearray from photo if exists
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			mImageBitmap.compress(CompressFormat.PNG, 0, outputStream);
			
			db.updateUserPhoto(Integer.parseInt(Global.currentUser.getUserID()), outputStream.toByteArray());
		}
	}

	private void dispatchTakePictureIntent(int actionCode) {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(takePictureIntent, actionCode);
	}

	public static boolean isIntentAvailable(Context context, String action) {
		final PackageManager packageManager = context.getPackageManager();
		final Intent intent = new Intent(action);
		List<ResolveInfo> list =
				packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
		return list.size() > 0;
	}

}
