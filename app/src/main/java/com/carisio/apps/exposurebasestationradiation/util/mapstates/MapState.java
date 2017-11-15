package com.carisio.apps.exposurebasestationradiation.util.mapstates;

import com.carisio.apps.exposurebasestationradiation.MainActivity;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public abstract class MapState {
	private static MapState currentState;
	private MainActivity activity;
	
	public MapState(MainActivity mainActivity) {
		activity = mainActivity;
	}
	
	public MainActivity getMainActivity() {
		return activity;
	}

	public void processMouseClickOnMap(GoogleMap map, LatLng clickPosition) {
		doProcessMouseClickOnMap(map, clickPosition);
		setCurrentState(getNextState());
	}
	
	public boolean processMouseClickOnMarker(Marker marker) {
		boolean result = doProcessMouseClickOnMarker(marker);
		setCurrentState(getNextState());
		return result;
	}
	
	public void activate() {
		activity.highlightNothing();
		doActivate();		
	}
	public abstract void doActivate();
	public abstract void doProcessMouseClickOnMap(GoogleMap map, LatLng clickPosition);
	public abstract boolean doProcessMouseClickOnMarker(Marker marker);
	public abstract MapState getNextState();
	
	public static void setCurrentState(MapState state) {
		currentState = state;
		currentState.activate();
	}
	public static MapState getCurrentState(MainActivity mainActivity) {
		if (currentState == null)
			currentState = new WaitingUserState(mainActivity);
		return currentState;
	}
}