package org.t2.pillplanner;

import java.util.ArrayList;

import org.t2.pillplanner.classes.DatabaseHelper;
import org.t2.pillplanner.classes.Global;
import org.t2.pillplanner.classes.User;
import org.t2.pillplanner.PasswordDialogFragment;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;

public class LoginFragment extends SherlockFragment implements PasswordDialogFragment.LoginListener, OnItemClickListener {

	private LoginAdapter mAdapter;
	private ListView mListView;

	private DatabaseHelper db;
	static Context ctx;

	private LinearLayout llNewAccount;

	private PasswordDialogFragment passwordFragment;
	private String selectedUsername;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);


		//
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

		if (savedInstanceState != null)
		{
			savedInstanceState.remove("android:support:fragments");
		} 

		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		getSherlockActivity().getSupportActionBar().setTitle("Login");

		ctx = getSherlockActivity();
		db = new DatabaseHelper(ctx);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View v = inflater.inflate(R.layout.fragment_login, container,false);

		llNewAccount = (LinearLayout) v.findViewById(R.id.llNewAccount);
		llNewAccount.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				final Intent intent = new Intent(getSherlockActivity(), CreateAccountActivity.class);
				startActivity(intent);
			}
		});
		
		final int padding = getResources().getDimensionPixelSize(R.dimen.padding_large);

		mListView = (ListView) v.findViewById(R.id.list);
		mListView.setPadding(padding, 0, padding, 0);
		mListView.setCacheColorHint(0);
		mListView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
		mListView.setOnItemClickListener(this);


		return v;
	}

	@Override
	public void onResume() {
		super.onResume();
		refreshData();
	}

	private void refreshData()
	{
		new AsyncTask<Void, Integer, Boolean>() { 
			protected void onPostExecute(Boolean result) {

				mAdapter = new LoginAdapter(getSherlockActivity());
				mListView.setAdapter(mAdapter);
				mListView.requestFocus();
				//mListView.setAdapter(mAdapter);
			}

			@Override
			protected Boolean doInBackground(Void... params) {
				Global.USERS = (ArrayList<User>) db.getUserList();
				return null;
			}
		}.execute(); // start the background processing


	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		//inflater.inflate(R.menu.fragment_reading_home, menu);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {

		selectedUsername = Global.USERS.get(position).getName();
		FragmentManager fm = getSherlockActivity().getSupportFragmentManager();
		@SuppressWarnings("unused")
		FragmentTransaction ft =fm.beginTransaction();

		// Create and show the dialog.
		passwordFragment = new PasswordDialogFragment();
		passwordFragment.setTargetFragment(this, 111);
		passwordFragment.show(fm, "");

	}

	private static class LoginAdapter extends ArrayAdapter<User> {

		public LoginAdapter(Context ctx) {

			super(ctx, R.layout.include_login_row, Global.USERS);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				v = LayoutInflater.from(getContext()).inflate(R.layout.include_login_row, null);
			}

			User cUser = getItem(position);
			((TextView)v.findViewById(R.id.username)).setText(cUser.getName());
			
			if(cUser.getUserPhoto() != null)
				((ImageView)v.findViewById(R.id.usericon)).setImageBitmap(cUser.getUserPhoto());

			return v;
		}
	}

	@Override
	public void LoginComplete(String password) {

		Global.currentUser = db.getUserByUsernamePass(selectedUsername, password);
		if(Global.currentUser == null)
			Toast.makeText(ctx, "Login Failed", Toast.LENGTH_LONG).show();
		else
		{
			Intent intent = new Intent(ctx, MainActivity.class);
			startActivity(intent);
			getSherlockActivity().finish();
		}

	}
}

