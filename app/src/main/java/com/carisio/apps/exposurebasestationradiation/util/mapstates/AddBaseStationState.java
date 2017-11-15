package com.carisio.apps.exposurebasestationradiation.util.mapstates;

import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.carisio.apps.exposurebasestationradiation.AddEditBaseStationFragment;
import com.carisio.apps.exposurebasestationradiation.MainActivity;
import com.carisio.apps.exposurebasestationradiation.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class AddBaseStationState extends MapState {

	public AddBaseStationState(MainActivity mainActivity) {
		super(mainActivity);
	}

	@Override
	public void doProcessMouseClickOnMap(GoogleMap map, LatLng clickPosition) {
		Fragment f = new AddEditBaseStationFragment(clickPosition, map, null);
		getMainActivity().showFragment(f);
	}

	@Override
	public MapState getNextState() {
		return new WaitingUserState(getMainActivity());
	}

	@Override
	public boolean doProcessMouseClickOnMarker(Marker marker) {
		// When waiting to insert a base station, the user can click at a marker
		// in this case, the default gmaps behaviour is the ideal
		return false;
	}

	@Override
	public void doActivate() {
		getMainActivity().highlightAddBSInfo();
		Toast.makeText(getMainActivity(), getMainActivity().getString(R.string.tap_to_create_bs), Toast.LENGTH_SHORT).show();
		getMainActivity().showMapFragment();		
	}
	
}