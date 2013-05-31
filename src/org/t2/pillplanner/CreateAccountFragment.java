package org.t2.pillplanner;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.t2.pillplanner.classes.DatabaseHelper;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;

public class CreateAccountFragment extends SherlockFragment {

	private DatabaseHelper db;

	//private TextView tvPasswordLabel;
	private EditText etUsername;
	private EditText etPassword;
	private EditText etPasswordConfirm;
	private Button btnSubmit;
	private Bitmap mImageBitmap;
	private ImageView ivPhoto;

	Context ctx;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		ctx = getSherlockActivity();
		db = new DatabaseHelper(ctx);

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

		if (savedInstanceState != null)
		{
			mImageBitmap = savedInstanceState.getParcelable("photo");
		} 

		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		getSherlockActivity().getSupportActionBar().setTitle("Create New Account");
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		outState.putParcelable("photo", mImageBitmap);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View v = inflater.inflate(R.layout.fragment_createaccount, container,false);

		ivPhoto = (ImageView) v.findViewById(R.id.ivPhoto);
		ivPhoto.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dispatchTakePictureIntent(112);
			}
		});
		if(mImageBitmap != null)
			ivPhoto.setImageBitmap(mImageBitmap);

		etUsername = (EditText) v.findViewById(R.id.etUsername);
		etPassword = (EditText) v.findViewById(R.id.etPassword);
		etPasswordConfirm = (EditText) v.findViewById(R.id.etPasswordConfirm);
		btnSubmit = (Button) v.findViewById(R.id.btnCreate);
		btnSubmit.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				if(etPassword.getText().toString().trim().equals(etPasswordConfirm.getText().toString().trim()))
				{

					new AsyncTask<Void, Integer, Boolean>() { 
						protected void onPostExecute(Boolean result) {

							getSherlockActivity().finish();

						}

						@Override
						protected Boolean doInBackground(Void... params) {
							
							//Get bytearray from photo if exists
							ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//							mImageBitmap.compress(CompressFormat.PNG, 0, outputStream);
							
							//autoLogin on create commented out
							//Global.currentUser = db.createNewUser(etUsername.getText().toString().trim(), etPassword.getText().toString().trim(), "", "");
							db.createNewUser(etUsername.getText().toString().trim(), etPassword.getText().toString().trim(), "", "", outputStream.toByteArray());
							return null;

						}
					}.execute(); // start the background processing
				}
				else
				{
					Toast.makeText(ctx, "Passwords do not match...", Toast.LENGTH_LONG).show();
				}


			}
		});

		return v;
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

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		//inflater.inflate(R.menu.fragment_reading_home, menu);
	}

}

