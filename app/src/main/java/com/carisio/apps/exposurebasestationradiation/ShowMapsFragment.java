package com.carisio.apps.exposurebasestationradiation;

import static com.carisio.apps.exposurebasestationradiation.util.Format.format;

import java.util.List;

import telecom.basestation.BaseStation;
import telecom.basestation.DirectivityCat2BS;
import telecom.nir.NIR;
import telecom.propagation.FreeSpace;
import telecom.util.Pair;
import telecom.util.Point3D;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.carisio.apps.exposurebasestationradiation.util.AppProperties;
import com.carisio.apps.exposurebasestationradiation.util.MapDrawing;
import com.carisio.apps.exposurebasestationradiation.util.ProjectDatabase;
import com.carisio.apps.exposurebasestationradiation.util.UtilView;
import com.carisio.apps.exposurebasestationradiation.util.mapstates.MapState;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class ShowMapsFragment extends SupportMapFragment implements OnMapReadyCallback, OnMapClickListener, OnMarkerClickListener, OnMapLongClickListener {
	private GoogleMap map;
	private static CameraPosition position;
	private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
	private static boolean PERMISSION_GRANTED = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if (map != null)
			position = map.getCameraPosition();
	}
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
    	super.onActivityCreated(savedInstanceState);
    	getMapAsync(this);
    }

	@Override
	public void onMapReady(GoogleMap newMap) {
		map = newMap;
		map.setOnMapClickListener(this);
		map.setOnMarkerClickListener(this);
		map.setOnMapLongClickListener(this);

		configureInfoWindow();

		if (ContextCompat.checkSelfPermission(this.getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
			map.setMyLocationEnabled(true);
		} else {
			checkLocationPermission();
		}
        map.getUiSettings().setZoomControlsEnabled(true);
        setMapType();
		setCameraToLastKnownPosition();

		MapDrawing.drawProject(getContext(), map);
	}


	private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

        	if (shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_FINE_LOCATION)) {
        		// This code was based on https://stackoverflow.com/questions/38072363/marshmallow-permissions-and-explanation
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle(getString(R.string.location_permission_needed))
                .setMessage(getString(R.string.location_explanation_message))
                .setPositiveButton(getString(R.string.ok_button), new DialogInterface.OnClickListener() {
                		@Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                			requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                					MY_PERMISSIONS_REQUEST_LOCATION );
                            }
                })
                .show();
                UtilView.applyAlertDialogStyle(dialog);
            } else {
                // No explanation needed, we can request the permission.
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION );
            }
        }
	}
	@Override
	public void onRequestPermissionsResult(int requestCode,
			String[] permissions, int[] grantResults) {
		if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            	PERMISSION_GRANTED = true;
            	map.setMyLocationEnabled(true);
            	setCameraToLastKnownPosition();
            }
		}
	}
	private void configureInfoWindow() {
		map.setInfoWindowAdapter(new InfoWindowAdapter() {

			@Override
			public View getInfoWindow(Marker marker) {
				DirectivityCat2BS bs = (DirectivityCat2BS) ProjectDatabase.findBS(marker.getTitle());
				if (bs != null) {
					return showBaseStationDetails(bs);
				} else {
					return showProbeDetails(marker);
				}
			}

			@Override
			public View getInfoContents(Marker marker) {
				return null;
			}
			private Pair<Double, Double> getEandTERatProbe(Marker marker, double probeHeight) {
				NIR nir = new NIR();
				FreeSpace fs = new FreeSpace();

				for (BaseStation BS : ProjectDatabase.getBaseStations(getActivity()))
					nir.addBaseStation(BS, fs);


				Pair<Double, Double> result = nir.evalEandTERAtProbe(new Point3D(marker.getPosition().latitude, marker.getPosition().longitude, probeHeight));

				return result;
			}
			private View showProbeDetails(Marker marker) {
				LayoutInflater inflater = getActivity().getLayoutInflater();
		    	View view = inflater.inflate(R.layout.probe_details_layout, null);

		    	TextView latitude = (TextView) view.findViewById(R.id.probe_latitude);
		    	TextView longitude = (TextView) view.findViewById(R.id.probe_longitude);
		    	TextView height = (TextView) view.findViewById(R.id.probe_height);
		    	TextView eField = (TextView) view.findViewById(R.id.e_field);
		    	TextView ter = (TextView) view.findViewById(R.id.total_exposure_rate);

		    	double probeHeight = AppProperties.getProbeHeight();
		    	Pair<Double, Double> result = getEandTERatProbe(marker, probeHeight);

				latitude.setText(format(marker.getPosition().latitude, 7) + " º");
				longitude.setText(format(marker.getPosition().longitude, 7) + " º");
				height.setText(format(probeHeight, 2) + " m");
				eField.setText(format(result.getFirst(), 2) + " V/m");
				ter.setText(format(result.getSecond(), 2) + " %");

				return view;
			}

			private View showBaseStationDetails(DirectivityCat2BS bs) {
				LayoutInflater inflater = getActivity().getLayoutInflater();
		    	View view = inflater.inflate(R.layout.base_station_details_layout, null);

		    	TextView name = (TextView) view.findViewById(R.id.bs_name);
		    	TextView latitude = (TextView) view.findViewById(R.id.bs_latitude);
		    	TextView longitude = (TextView) view.findViewById(R.id.bs_longitude);
		    	TextView height = (TextView) view.findViewById(R.id.bs_height);
		    	TextView frequency = (TextView) view.findViewById(R.id.bs_frequency);
		    	TextView tilt = (TextView) view.findViewById(R.id.bs_tilt);
		    	TextView vertical_beamwidth = (TextView) view.findViewById(R.id.bs_vertical_beamwidth);
		    	TextView eirp = (TextView) view.findViewById(R.id.bs_eirp);
		    	TextView sidelobe = (TextView) view.findViewById(R.id.bs_sidelobe_envelope);

		    	name.setText(bs.getName());
		    	latitude.setText(format(bs.getLatitude(), 7) + " �");
				longitude.setText(format(bs.getLongitude(), 7) + " �");
				height.setText(format(bs.getHeight(), 2, ", ") + " m");
				frequency.setText(format(bs.getFrequencyMHz(), 3, ", ") + " MHz");
				tilt.setText(format(bs.getTiltDegree(), 2, ", ") + " �");
				vertical_beamwidth.setText(format(bs.getThetaBwVerticalDegree(), 1, ", ") + " �");
				eirp.setText(format(bs.getEirpMaxdBm(), 1, ", ") + " dBm");
				sidelobe.setText(format(bs.getMaxSideLobeEnvelopedB(), 2, ", ") + " dB");

				return view;
			}
		});
	}

	private void setMapType() {
        String mapType = AppProperties.getMapType(getActivity());
        if (mapType.equals(getString(R.string.map_type_none)))
        	map.setMapType(GoogleMap.MAP_TYPE_NONE);
        else if (mapType.equals(getString(R.string.map_type_hybrid)))
        	map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        else if (mapType.equals(getString(R.string.map_type_normal)))
        	map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        else if (mapType.equals(getString(R.string.map_type_satellite)))
        	map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        else if (mapType.equals(getString(R.string.map_type_terrain)))
        	map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
	}

	private void setCameraToLastKnownPosition() {
		if (position == null) {
			Location lastKnownLocation = getLastKnownLocation();

			if (lastKnownLocation != null) {
				LatLng latLng = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
				position = CameraPosition.builder().target(latLng).zoom(15).build();
			}
		}
		if (position != null) {
			CameraUpdate update = CameraUpdateFactory.newCameraPosition(position);
			map.animateCamera(update);
		}
	}

	private Location getLastKnownLocation() {
		LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
	    List<String> providers = locationManager.getProviders(true);
	    Location bestLocation = null;
	    for (String provider : providers) {
	        Location l = locationManager.getLastKnownLocation(provider);
	        if (l == null) {
	            continue;
	        }
	        if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
	            bestLocation = l;
	        }
	    }
	    return bestLocation;
	}
	@Override
	public void onMapClick(LatLng position) {
		MapState.getCurrentState((MainActivity) getContext()).processMouseClickOnMap(map, position);
	}
	@Override
	public boolean onMarkerClick(Marker marker) {
		return MapState.getCurrentState((MainActivity) getContext()).processMouseClickOnMarker(marker);
	}
	@Override
	public void onMapLongClick(LatLng position) {

	}

}