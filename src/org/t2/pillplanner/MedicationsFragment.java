package org.t2.pillplanner;

import java.util.ArrayList;

import org.t2.pillplanner.classes.Global;
import org.t2.pillplanner.classes.DatabaseHelper;
import org.t2.pillplanner.classes.UserMed;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MedicationsFragment extends SherlockFragment implements OnItemClickListener {

	private ListView lstOverview;
	private static ArrayList<UserMed> SCHEDULE = new ArrayList<UserMed>();
	private ScheduleAdapter mScheduleAdapter;

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
		view = inflater.inflate(R.layout.fragment_medications, null);

		lstOverview = (ListView) view.findViewById(R.id.list);
		lstOverview.setOnItemClickListener(this);
		registerForContextMenu(lstOverview);
		
		LinearLayout llAdd = (LinearLayout) view.findViewById(R.id.llAdd);
		llAdd.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), AddMedicationActivity.class);
				startActivity(intent);
			}
		});

		return view;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
		//Clicking an existing med to edit
		Intent intent = new Intent(getActivity(), AddMedicationActivity.class);
		Bundle args = new Bundle();
		args.putSerializable("EXISTINGMED", SCHEDULE.get(position));
		intent.putExtras(args);
		startActivity(intent);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		//final Bundle state = savedInstanceState != null ? savedInstanceState : getArguments();
	}

	private void refreshData()
	{
		new AsyncTask<Void, Integer, Boolean>() { 
			protected void onPostExecute(Boolean result) {
				
				mScheduleAdapter = new ScheduleAdapter(ctx);
				lstOverview.setAdapter(mScheduleAdapter);
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

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		if (v.getId()==R.id.list) {
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
			menu.setHeaderTitle("Medication");
			String[] menuItems = new String[]{"Remove Medication"};
			for (int i = 0; i<menuItems.length; i++) {
				menu.add(Menu.NONE, i, info.position, menuItems[i]);
			}
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		//AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
		int menuItemIndex = item.getItemId();

		db.removeUserScheduledDrug(SCHEDULE.get(menuItemIndex).getUMID());
		Toast.makeText(getSherlockActivity(), "Removed " + SCHEDULE.get(menuItemIndex).getProductName(), Toast.LENGTH_SHORT).show();
		refreshData();
		
		return true;
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	private static final class ScheduleAdapter extends ArrayAdapter<UserMed> {
		public ScheduleAdapter(Context ctx) {
			super(ctx, R.layout.include_medication_row, SCHEDULE);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				v = LayoutInflater.from(getContext()).inflate(R.layout.include_medication_row, null);
			}

			((TextView)v.findViewById(R.id.title)).setText(getItem(position).getProductName());

			//((ImageView)v.findViewById(R.id.icon)).setImageResource(getItem(position).getIconResource());

			return v;
		}
	}
}
