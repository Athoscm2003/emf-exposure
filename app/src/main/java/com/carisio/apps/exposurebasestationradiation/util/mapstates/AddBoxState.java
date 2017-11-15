package com.carisio.apps.exposurebasestationradiation.util.mapstates;

import telecom.basestation.BaseStation;
import telecom.nir.NIR;
import telecom.propagation.FreeSpace;
import telecom.util.Pair;
import telecom.util.Point2D;
import telecom.util.SimpleMatrix;
import android.widget.Toast;

import com.carisio.apps.exposurebasestationradiation.MainActivity;
import com.carisio.apps.exposurebasestationradiation.R;
import com.carisio.apps.exposurebasestationradiation.util.AppProperties;
import com.carisio.apps.exposurebasestationradiation.util.MapDrawing;
import com.carisio.apps.exposurebasestationradiation.util.ProjectDatabase;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class AddBoxState extends MapState {
	public AddBoxState(MainActivity mainActivity) {
		super(mainActivity);
	}

	private LatLng firstPosition;
	
	@Override
	public void doProcessMouseClickOnMap(GoogleMap map, LatLng clickPosition) {
		firstPosition = clickPosition;
	}

	@Override
	public MapState getNextState() {
		if (firstPosition == null)
			return this;
		else
			return new ContinueBoxState(getMainActivity(), firstPosition);
	}

	@Override
	public boolean doProcessMouseClickOnMarker(Marker marker) {
		// When waiting to insert a box, the user can click at a marker
		// in this case, the default gmaps behavier is the ideal
		return false;
	}

	@Override
	public void doActivate() {
		getMainActivity().highlightAddBoxInfo();
		Toast.makeText(getMainActivity(), getMainActivity().getString(R.string.tap_to_create_box_begin_point), Toast.LENGTH_SHORT).show();
		getMainActivity().showMapFragment();
	}

}

class ContinueBoxState extends MapState {
	private LatLng firstPosition;
	
	public ContinueBoxState(MainActivity mainActivity, LatLng firstPosition) {
		super(mainActivity);
		this.firstPosition = firstPosition;
		mainActivity.highlightAddBoxInfo();
	}
	
	@Override
	public void doProcessMouseClickOnMap(GoogleMap map, LatLng clickPosition) {
		MainActivity mainActivity = getMainActivity();
		NIR nir = new NIR();
		FreeSpace fs = new FreeSpace();
		
		for (BaseStation BS : ProjectDatabase.getBaseStations(mainActivity))
			nir.addBaseStation(BS, fs);

		double latMin = Math.min(firstPosition.latitude, clickPosition.latitude);
		double latMax = Math.max(firstPosition.latitude, clickPosition.latitude);
		double lngMin = Math.min(firstPosition.longitude, clickPosition.longitude);
		double lngMax = Math.max(firstPosition.longitude, clickPosition.longitude);
		
		int nStepsLng = 10;
		int nStepsLat = (int) (((latMax-latMin)/(lngMax-lngMin))*nStepsLng);
		if (nStepsLat > nStepsLng) {
			nStepsLng = (int) (((lngMax-lngMin)/(latMax-latMin))*nStepsLng);
			if (nStepsLng == 0)
				nStepsLng = 1;
		}
		Pair<SimpleMatrix, SimpleMatrix> EAndTer = nir.evalEandTERAtPlane(new Point2D(firstPosition.latitude, firstPosition.longitude), 
				new Point2D(clickPosition.latitude, clickPosition.longitude), AppProperties.getProbeHeight(), nStepsLng);
		
		ProjectDatabase.saveBox(mainActivity, EAndTer, latMin, latMax, lngMin, lngMax);
		MapDrawing.drawProject(mainActivity, map);
	}

	@Override
	public MapState getNextState() {
		return new WaitingUserState(getMainActivity());
	}

	@Override
	public boolean doProcessMouseClickOnMarker(Marker marker) {
		// When performing the second click to create a box, it is not OK if the user 
		// click at a marker, because it can be confusing. In this happens, shows the marker
		// info e cancel the box creation
		return false;
	}

	@Override
	public void doActivate() {
		getMainActivity().highlightAddBoxInfo();
		Toast.makeText(getMainActivity(), getMainActivity().getString(R.string.tap_to_create_box_end_point), Toast.LENGTH_SHORT).show();		
	}
	
}