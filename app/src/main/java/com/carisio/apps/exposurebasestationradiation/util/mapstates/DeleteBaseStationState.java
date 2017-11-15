package com.carisio.apps.exposurebasestationradiation.util.mapstates;

import telecom.basestation.BaseStation;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.Toast;

import com.carisio.apps.exposurebasestationradiation.MainActivity;
import com.carisio.apps.exposurebasestationradiation.R;
import com.carisio.apps.exposurebasestationradiation.util.MapDrawing;
import com.carisio.apps.exposurebasestationradiation.util.ProjectDatabase;
import com.carisio.apps.exposurebasestationradiation.util.UtilView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class DeleteBaseStationState extends MapState {

	public DeleteBaseStationState(MainActivity mainActivity) {
		super(mainActivity);
	}

	private MapState nextState;
	
	@Override
	public void doProcessMouseClickOnMap(GoogleMap map, LatLng clickPosition) {
		nextState = new WaitingUserState(getMainActivity());
	}

	@Override
	public boolean doProcessMouseClickOnMarker(final Marker marker) {
		final MainActivity mainActivity = getMainActivity();
		final BaseStation bs = ProjectDatabase.findBS(marker.getTitle());
		if (bs == null) {
			nextState = this;
			return false;
		} else {
			AlertDialog dialog = new AlertDialog.Builder(getMainActivity())
		    .setTitle(mainActivity.getString(R.string.delete))
		    .setMessage(mainActivity.getString(R.string.are_you_sure_you_want_to_delete_bs))
		    .setCancelable(false)
		    .setPositiveButton(mainActivity.getString(R.string.yes_button), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					ProjectDatabase.deleteBaseStation(mainActivity, bs);
					marker.remove();
					MapDrawing.removeBox();
					MapDrawing.removeProbe();					
				}
			})
		    .setNegativeButton(mainActivity.getString(R.string.no_button), null)
		    .setIcon(android.R.drawable.ic_dialog_alert)
		    .show();
			UtilView.applyAlertDialogStyle(dialog);
			
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
		getMainActivity().highlightDeleteBSInfo();
		Toast.makeText(getMainActivity(), getMainActivity().getString(R.string.tap_to_delete_bs), Toast.LENGTH_SHORT).show();
		getMainActivity().showMapFragment();
		
	}

}
