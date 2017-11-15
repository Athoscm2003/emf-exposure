package com.carisio.apps.exposurebasestationradiation.util.mapstates;

import telecom.basestation.BaseStation;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.carisio.apps.exposurebasestationradiation.AddEditBaseStationFragment;
import com.carisio.apps.exposurebasestationradiation.MainActivity;
import com.carisio.apps.exposurebasestationradiation.R;
import com.carisio.apps.exposurebasestationradiation.util.ProjectDatabase;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class EditBaseStationState extends MapState {
	public EditBaseStationState(MainActivity mainActivity) {
		super(mainActivity);
	}

	private MapState nextState = null;
	
	@Override
	public void doProcessMouseClickOnMap(GoogleMap map, LatLng clickPosition) {
		nextState = new WaitingUserState(getMainActivity());
	}

	@Override
	public boolean doProcessMouseClickOnMarker(Marker marker) {
		final BaseStation bs = ProjectDatabase.findBS(marker.getTitle());
		if (bs == null) {
			nextState = this;
			return false;
		} else {
			Fragment f = new AddEditBaseStationFragment(marker.getPosition(), null, bs.getId());
			getMainActivity().showFragment(f);
			nextState = new WaitingUserState(getMainActivity());
			return true;
		}
	}

	@Override
	public MapState getNextState() {
		return nextState;
	}

	@Override
	public void doActivate() {
		getMainActivity().highlightEditBSInfo();
		Toast.makeText(getMainActivity(), getMainActivity().getString(R.string.tap_to_edit_bs), Toast.LENGTH_SHORT).show();
		getMainActivity().showMapFragment();	
	}
}
