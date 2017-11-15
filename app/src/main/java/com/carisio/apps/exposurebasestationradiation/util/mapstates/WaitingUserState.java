package com.carisio.apps.exposurebasestationradiation.util.mapstates;

import com.carisio.apps.exposurebasestationradiation.MainActivity;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class WaitingUserState extends MapState {
	public WaitingUserState(MainActivity mainActivity) {
		super(mainActivity);
	}
	@Override
	public void doProcessMouseClickOnMap(GoogleMap map, LatLng clickPosition) {
		// Do nothing
	}
	@Override
	public MapState getNextState() {
		// Still waiting
		return this;
	}
	@Override
	public boolean doProcessMouseClickOnMarker(Marker marker) {
		// Default Google Map behavior
		return false;
	}
	@Override
	public void doActivate() {
		// Do nothing
	}
}