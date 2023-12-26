// Permite a criação de mapas com sondas, marcadores e caixas?

package com.carisio.apps.exposurebasestationradiation.util;

import java.util.ArrayList;

import telecom.basestation.BaseStation;
import telecom.util.Pair;
import telecom.util.SimpleMatrix;
import android.content.Context;

import com.carisio.apps.exposurebasestationradiation.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

public class MapDrawing {
	// There is only one probe in the map
	public static Marker probe;
	public static ArrayList<Marker> baseStationsMarkers = new ArrayList<Marker>();
	public static ArrayList<Polygon> box;

	public static void drawProject(Context ctx, GoogleMap map) {
		if (map == null)
			return;
		clearDrawing();
		
		loadBS(ctx, map);
		loadBox(ctx, map);
	}
	public static void clearDrawing() {
		removeBaseStationMarkers();
		removeProbe();
		removeBox();
	}
	
	// PROBE METHODS
	public static void addProbe(GoogleMap map, LatLng position, String probeName) {
		removeProbe();
		probe = createMarker(map, position, probeName, R.drawable.probe_at_map);
		probe.showInfoWindow();
	}
	public static void removeProbe() {
		if (probe != null)
			probe.remove();
	}
	
	private static void removeBaseStationMarkers() {
		for (Marker bs : baseStationsMarkers) {
			bs.remove();
		}
	}

	private static void loadBS(Context ctx, GoogleMap map) {
		ArrayList<BaseStation> baseStations = ProjectDatabase.getBaseStations(ctx);
		for (BaseStation bs : baseStations) {
			baseStationsMarkers.add(createMarker(map, new LatLng(bs.getPosition().getLatitude(), bs.getPosition().getLongitude()), bs.getId(), R.drawable.bs_at_map));
		}
	}
	private static void loadBox(Context ctx, GoogleMap map) {
		SimpleMatrix matrix = null;
		ColorMap colorMap = new ColorMap(100, AppProperties.getMinValueInBox(), AppProperties.getMaxValueInBox(), 128, AppProperties.getColorMapType());
		
		Pair<SimpleMatrix, SimpleMatrix> eAndTer = ProjectDatabase.getBoxEAndTer();
		if (eAndTer.getFirst().getSize().getFirst() == 0) {
			ProjectDatabase.loadBox(ctx);
			eAndTer = ProjectDatabase.getBoxEAndTer();
		}
		LatLng pos1 = ProjectDatabase.getBoxLatLngMin();
		LatLng pos2 = ProjectDatabase.getBoxLatLngMax();
		
		double latMin = Math.min(pos1.latitude, pos2.latitude);
		double latMax = Math.max(pos1.latitude, pos2.latitude);
		double lngMin = Math.min(pos1.longitude, pos2.longitude);
		double lngMax = Math.max(pos1.longitude, pos2.longitude);
		
		String showEOrTER = AppProperties.getShowInBox(ctx);
		if (showEOrTER.equals(ctx.getString(R.string.e_field)))
			matrix = eAndTer.getFirst();
		else {
			matrix = eAndTer.getSecond();
		}
		
		Pair<Integer, Integer> size = matrix.getSize();
		int NLat = size.getFirst();
		int NLng = size.getSecond();
		double stepLat = (latMax - latMin)/NLat;
		double stepLng = (lngMax - lngMin)/NLng;
		
		for (int i = 0; i < NLat; i++) {
			double p1Lat = latMax - i*stepLat;
			double p2Lat = latMax - (i+1)*stepLat;
			for (int j = 0; j < NLng; j++) {
				double p1Lng = lngMin + j*stepLng;
				double p2Lng = lngMin + (j+1)*stepLng;
				
				double val = matrix.getElement(i, j);
				int color = colorMap.GetColor(val);
				PolygonOptions options = new PolygonOptions();
				options.add(new LatLng(p1Lat, p1Lng), new LatLng(p2Lat, p1Lng), new LatLng(p2Lat, p2Lng), new LatLng(p1Lat, p2Lng), new LatLng(p1Lat, p1Lng))
						.fillColor(color)
						.strokeWidth(0);
				box.add(map.addPolygon(options));
			}
		}
	}
	public static void removeBox() {
		if (box != null) {
			for (Polygon p : box)
				p.remove();
			box.clear();
		}
		box = new ArrayList<Polygon>();		
	}
	
	private static Marker createMarker(GoogleMap map, LatLng position, String title, int resourceID) {
		MarkerOptions markerOptions = new MarkerOptions();
		markerOptions.position(position).title(title);
		Marker marker = map.addMarker(markerOptions);
		marker.setIcon(BitmapDescriptorFactory.fromResource(resourceID));
		return marker;
	}
}
