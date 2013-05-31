package org.t2.pillplanner;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

public class EulaFragment extends SherlockFragment {
    @SuppressWarnings("unused")
    private static final String TAG = "EulaFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_eula, null);
        ((TextView)v.findViewById(R.id.eulatext)).setText(Html.fromHtml(getString(R.string.eula_content)));
        
        return v;
    }
}
