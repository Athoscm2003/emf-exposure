package com.carisio.apps.exposurebasestationradiation.util.mapstates;

import android.widget.Toast;

import com.carisio.apps.exposurebasestationradiation.MainActivity;
import com.carisio.apps.exposurebasestationradiation.R;
import com.carisio.apps.exposurebasestationradiation.util.MapDrawing;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class AddProbeState extends MapState {

	public AddProbeState(MainActivity mainActivity) {
		super(mainActivity);
	}

	@Override
	public void doProcessMouseClickOnMap(GoogleMap map, LatLng clickPosition) {
		MapDrawing.addProbe(map, clickPosition, "");
//		MapDrawing.addProbe(map, new LatLng(-15.8278845, -47.8436878), "");
	}

	@Override
	public MapState getNextState() {
		return this;
	}

	@Override
	public boolean doProcessMouseClickOnMarker(Marker marker) {
		// When waiting to insert a probe, the user can click at a marker
		// in this case, the default gmaps behavier is the ideal
		return false;
	}

	@Override
	public void doActivate() {
		getMainActivity().highlightAddProbeInfo();
		Toast.makeText(getMainActivity(), getMainActivity().getString(R.string.tap_to_create_probe), Toast.LENGTH_SHORT).show();
		getMainActivity().showMapFragment();
	}
}
