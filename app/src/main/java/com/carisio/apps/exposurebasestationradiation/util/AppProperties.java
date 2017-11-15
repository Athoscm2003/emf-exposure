package com.carisio.apps.exposurebasestationradiation.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import com.carisio.apps.exposurebasestationradiation.R;
import com.carisio.apps.exposurebasestationradiation.util.ColorMap.COLORMAP_TYPE;

import android.content.Context;

public class AppProperties {
	private static Properties properties = null;
	private static String PROPERTIES_FILE = "config.properties";

	private enum SHOW_IN_BOX_OPTIONS {TER, E_FIELD};
	private enum MAP_TYPES {NORMAL, TERRAIN, SATELLITE, HYBRID, NONE};
	
	public static String PROBE_HEIGHT = "probe height";
	public static String COLOR_MAP_TYPE = "color map type";
	public static String SHOW_IN_BOX = "show in box";
	public static String MIN_VALUE_IN_BOX = "min value in box";
	public static String MAX_VALUE_IN_BOX = "max value in box";
	public static String MAP_TYPE = "map type";
	public static String DONT_SHOW_THIS_MESSAGE_AGAIN = "dont show this message again";

	public static void loadProperties(Context c) {
		try {
			properties = new Properties();
			File file = c.getFileStreamPath(PROPERTIES_FILE);
			
			if (!file.exists()) {
				return;
			}
			FileInputStream inFile = c.openFileInput(PROPERTIES_FILE);
			properties.load(inFile);
			inFile.close();
		} catch (Exception e) {
			
		}
	}
	
	public static void saveProperties(Context c) {
		try {
			FileOutputStream outFile = c.openFileOutput(PROPERTIES_FILE, Context.MODE_PRIVATE);
			properties.store(outFile, "");
			outFile.close();
		} catch (Exception e) {
			
		}
	}
	
	public static String getProperty(String name) {
		String result = properties.getProperty(name);
		if (result == null)
			result = "";
		return result;
	}
	public static int getIntProperty(String name) {
		String value = getProperty(name);
		if (value.equals(""))
			return 0;
		return Integer.parseInt(value);
	}
	public static double getDoubleProperty(String name) {
		String value = getProperty(name);
		if (value.equals(""))
			return 0;
		return Double.parseDouble(value);
	}
	public static void setProperty(String name, String value) {
		properties.setProperty(name, value);
	}
	public static void setProperty(String name, int value) {
		properties.setProperty(name, "" + value);
	}
	public static void setProperty(String name, double value) {
		properties.setProperty(name, "" + value);
	}
	
	/* SPECIFIC PROPERTIES. THIS IS USEFULL TO GET DEFAULT VALUES */
	public static boolean isDontShowThisMessageAgain() {
		String value = getProperty(DONT_SHOW_THIS_MESSAGE_AGAIN);
		return "true".equals(value);
	}
	public static void setDontShowThisMessageAgain(boolean value) {
		setProperty(DONT_SHOW_THIS_MESSAGE_AGAIN, "" + value);
	}
	public static double getProbeHeight() {
		double value = getDoubleProperty(PROBE_HEIGHT);
		if (value == 0)
			value = 1.7;
		return value;
	}
	public static void setProbeHeight(String value) {
		setProperty(PROBE_HEIGHT, value);
	}
	public static void setShowInBox(String option, Context c) {
		SHOW_IN_BOX_OPTIONS opt = SHOW_IN_BOX_OPTIONS.E_FIELD;
		
		if (c.getString(R.string.e_field).equals(option)) {
			opt = SHOW_IN_BOX_OPTIONS.E_FIELD;
		} else if (c.getString(R.string.total_exposure_rate).equals(option)) {
			opt = SHOW_IN_BOX_OPTIONS.TER;
		}
		
		setProperty(SHOW_IN_BOX, opt.ordinal());		
	}
	public static String getShowInBox(Context c) {
		int value = getIntProperty(SHOW_IN_BOX);
		SHOW_IN_BOX_OPTIONS showOptions = SHOW_IN_BOX_OPTIONS.values()[value];
		if (showOptions.equals(SHOW_IN_BOX_OPTIONS.E_FIELD)) {
			return c.getString(R.string.e_field);
		} else {
			return c.getString(R.string.total_exposure_rate);
		}
	}
	public static void setMinValueInBox(String min) {
		setProperty(MIN_VALUE_IN_BOX, min);
	}
	public static double getMinValueInBox() {
		double value = getDoubleProperty(MIN_VALUE_IN_BOX);
		return value;
	}
	public static void setMaxValueInBox(String max) {
		setProperty(MAX_VALUE_IN_BOX, max);
	}
	public static double getMaxValueInBox() {
		double value = getDoubleProperty(MAX_VALUE_IN_BOX);
		if (value == 0)
			value = 1;
		return value;
	}
	public static String getMapType(Context c) {
		int value = getIntProperty(MAP_TYPE);
		MAP_TYPES type = MAP_TYPES.values()[value];

		if (type.equals(MAP_TYPES.NONE)) {
			return c.getString(R.string.map_type_none);
		} else if (type.equals(MAP_TYPES.TERRAIN)) {
			return c.getString(R.string.map_type_terrain);
		} else if (type.equals(MAP_TYPES.SATELLITE)) {
			return c.getString(R.string.map_type_satellite);
		} else if (type.equals(MAP_TYPES.HYBRID)) {
			return c.getString(R.string.map_type_hybrid);
		} else {
			return c.getString(R.string.map_type_normal);
		}
	}
	public static void setMapType(String mapType, Context c) {
		MAP_TYPES type = MAP_TYPES.NONE;
		
		if (c.getString(R.string.map_type_none).equals(mapType)) {
			type = MAP_TYPES.NONE;
		} else if (c.getString(R.string.map_type_terrain).equals(mapType)) {
			type = MAP_TYPES.TERRAIN;
		} else if (c.getString(R.string.map_type_satellite).equals(mapType)) {
			type = MAP_TYPES.SATELLITE;
		} else if (c.getString(R.string.map_type_hybrid).equals(mapType)) {
			type = MAP_TYPES.HYBRID;
		} else if (c.getString(R.string.map_type_normal).equals(mapType)) {
			type = MAP_TYPES.NORMAL;
		}
		
		setProperty(MAP_TYPE, type.ordinal());
	}

	public static void setColorMapType(COLORMAP_TYPE selectedItem) {
		setProperty(COLOR_MAP_TYPE, selectedItem.ordinal());
	}

	public static ColorMap.COLORMAP_TYPE getColorMapType() {
		return ColorMap.COLORMAP_TYPE.values()[getIntProperty(COLOR_MAP_TYPE)];
	}
}
