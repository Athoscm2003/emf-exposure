package com.carisio.apps.exposurebasestationradiation.util;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import telecom.basestation.BaseStation;
import telecom.util.Pair;
import telecom.util.SimpleMatrix;
import android.content.Context;

import com.google.android.gms.maps.model.LatLng;

public class ProjectDatabase {
	// FILES NAMES
	private static String BS_FILE = "basestationdb.txt";
	private static String BOX_FILE = "boxdb.txt";

	// BASE STATIONS
	private static ArrayList<BaseStation> baseStations = null;
	
	// BOX
	private static Pair<SimpleMatrix, SimpleMatrix> eAndTer = null;
	private static LatLng latLngMin = null;
	private static LatLng latLngMax = null;
	
	// BASE STATION DATABASE
	public static void addBS(BaseStation bs, Context c) {
		baseStations.add(bs);
		saveBaseStations(c);
		deleteBox(c);
	}
	public static ArrayList<BaseStation> getBaseStations(Context c) {
		if (baseStations == null) {
			loadBaseStations(c);
		}
		return baseStations;
	}
	public static BaseStation findBS(String id) {
		for (BaseStation bs : baseStations)
			if (bs.getId().equals(id))
				return bs;
		
		return null;
	}
	public static void deleteAllBaseStation(Context c) {
		baseStations.clear();
		saveBaseStations(c);
		deleteBox(c);
	}
	public static void saveBaseStations(Context c) {
		try {
			FileOutputStream outFile = c.openFileOutput(BS_FILE, Context.MODE_PRIVATE);
			OutputStreamWriter outWriter = new OutputStreamWriter(outFile);
			
			for (BaseStation bs : baseStations)
				outWriter.append(bs.toString()).append("\n");
			
			outWriter.close();
			outFile.close();
		} catch (Exception e) {
		}		
	}
	private static void loadBaseStations(Context c) {
		File f = c.getFileStreamPath(BS_FILE);
		baseStations = new ArrayList<BaseStation>();
		
		try {
			if (f.exists()) {
				FileInputStream inFile = c.openFileInput(BS_FILE);
				BufferedReader inReader = new BufferedReader(new InputStreamReader(inFile));
				
				String line;
				while ((line = inReader.readLine()) != null) {
					BaseStation bs = BaseStation.fromString(line);
					baseStations.add(bs);
				}
				inReader.close();
				inFile.close();
			}
		} catch (FileNotFoundException e) {
		} catch (Exception e) {
			deleteAllBaseStation(c);
		}		
	}
	
	// BOX DATABASE
	public static void saveBox(Context c, Pair<SimpleMatrix, SimpleMatrix> eAndTer, double latMin, double latMax, double lngMin, double lngMax) {
		try {
			ProjectDatabase.eAndTer = eAndTer;
			ProjectDatabase.latLngMin = new LatLng(latMin, lngMin);
			ProjectDatabase.latLngMax = new LatLng(latMax, lngMax);
			
			DataOutputStream outFile = new DataOutputStream(new FileOutputStream(c.getFileStreamPath(BOX_FILE)));
			outFile.writeDouble(latMin);
			outFile.writeDouble(latMax);
			outFile.writeDouble(lngMin);
			outFile.writeDouble(lngMax);
			
			Pair<Integer, Integer> size = eAndTer.getFirst().getSize();
			int N = size.getFirst();
			int M = size.getSecond();
			outFile.writeInt(N);
			outFile.writeInt(M);
			
			SimpleMatrix eField = eAndTer.getFirst();
			SimpleMatrix ter = eAndTer.getSecond();
			
			for (int i = 0; i < N; i++) {
				for (int j = 0; j < M; j++) {
					outFile.writeDouble(eField.getElement(i, j));
					outFile.writeDouble(ter.getElement(i, j));
				}
			}
			outFile.close();
		} catch (Exception e) {
		}
	}
	public static void loadBox(Context c) {
		try {
			File file = c.getFileStreamPath(BOX_FILE);
			if (!file.exists())
				return;
			
			DataInputStream inFile = new DataInputStream(c.openFileInput(BOX_FILE));
			double latMin = inFile.readDouble();
			double latMax = inFile.readDouble();
			double lngMin = inFile.readDouble();
			double lngMax = inFile.readDouble();
			latLngMin = new LatLng(latMin, lngMin);
			latLngMax = new LatLng(latMax, lngMax);
			
			int N = inFile.readInt();
			int M = inFile.readInt();
			
			SimpleMatrix eField = new SimpleMatrix(N, M);
			SimpleMatrix ter = new SimpleMatrix(N, M);
			
			for (int i = 0; i < N; i++) {
				for (int j = 0; j < M; j++) {
					eField.setElement(i, j, inFile.readDouble());
					ter.setElement(i, j, inFile.readDouble());
				}
			}
			inFile.close();
			eAndTer = new Pair<SimpleMatrix, SimpleMatrix>(eField, ter);
		} catch (Exception e) {
		}
	}
	public static void deleteBox(Context c) {
		File file = c.getFileStreamPath(BOX_FILE);
		file.delete();
		latLngMin = null;
		latLngMax = null;
		eAndTer = null;
	}
	public static LatLng getBoxLatLngMin() {
		if (latLngMin == null) 
			latLngMin = new LatLng(0, 0);
		return latLngMin;
	}
	public static LatLng getBoxLatLngMax() {
		if (latLngMax == null)
			latLngMax = new LatLng(0, 0);
		return latLngMax;
	}
	public static Pair<SimpleMatrix, SimpleMatrix> getBoxEAndTer() {
		if (eAndTer == null) {
			eAndTer = new Pair<SimpleMatrix, SimpleMatrix>(new SimpleMatrix(0, 0), new SimpleMatrix(0, 0));
		}
		return eAndTer;
	}
	public static void deleteBaseStation(Context ctx, BaseStation bs) {
		baseStations.remove(bs);
		saveBaseStations(ctx);
		deleteBox(ctx);
	}
}
