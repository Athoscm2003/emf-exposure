// Permite a criação de mapas com range de cores diferentes?

package com.carisio.apps.exposurebasestationradiation.util;

import java.util.ArrayList;

import telecom.util.Pair;
import android.graphics.Color;

public class ColorMap {
	private int numberOfColors;
	private double begin;
	private double end;
	private ArrayList<Pair<Range, Integer>> colors;
	private int alpha;
	public static enum COLORMAP_TYPE {GREEN_TO_RED, BLUE_TO_RED};

	public ColorMap(int numberOfColors, double begin, double end, int alpha) {
		this(numberOfColors, begin, end, alpha, COLORMAP_TYPE.GREEN_TO_RED);
		createMapGreenToRed();
	}
	public ColorMap(int numberOfColors, double begin, double end, int alpha, COLORMAP_TYPE type) {
		while (numberOfColors % 5 != 0) {
			numberOfColors++;
		}
		this.numberOfColors = numberOfColors;
		this.begin = begin;
		this.end = end;
		this.alpha = alpha;

		if (type.equals(COLORMAP_TYPE.GREEN_TO_RED))
			createMapGreenToRed();
		else if (type.equals(COLORMAP_TYPE.BLUE_TO_RED))
			createMapBlueToRed();
	}
	public int GetColor(double value) {
		if (value < begin)
			return colors.get(0).getSecond();
		if (value > end)
			return colors.get(colors.size()-1).getSecond();

		int color = 0;
		for (Pair<Range, Integer> pair : colors) {
			if (pair.getFirst().at(value)) {
				color = pair.getSecond();
				break;
			}
		}
		return color;
	}
	public double Min() {
		return begin;
	}
	public double Max() {
		return end;
	}

	private void createMapBlueToRed() {
		colors = new ArrayList<Pair<Range, Integer>>();

		double deltaX = (end-begin)/numberOfColors;
		int i = 0, cont = 0;
		int numberOfColorsInInterval = numberOfColors/5;

		for (; i < numberOfColorsInInterval; i++) {
			Range subRange = new Range(begin + i*deltaX, begin + (i+1)*deltaX);
			Integer c = Color.argb(alpha, 0, 0, 128 + (int)((cont*(128))/numberOfColorsInInterval));
			colors.add(new Pair<Range, Integer>(subRange, c));
			cont++;
		}
		cont = 0;
		for (; i < 2*numberOfColorsInInterval; i++) {
			Range subRange = new Range(begin + i*deltaX, begin + (i+1)*deltaX);
			Integer c = Color.argb(alpha, 0, (int)((cont*255.0)/numberOfColorsInInterval), 255);
			colors.add(new Pair<Range, Integer>(subRange, c));
			cont++;
		}
		cont = 0;
		for (; i < 3*numberOfColorsInInterval; i++) {
			Range subRange = new Range(begin + i*deltaX, begin + (i+1)*deltaX);
			Integer c = Color.argb(alpha, (int)((cont*255.0)/numberOfColorsInInterval), 255, 255 - (int)((cont*255.0)/numberOfColorsInInterval));
			colors.add(new Pair<Range, Integer>(subRange, c));
			cont++;
		}
		cont = 0;
		for (; i < 4*numberOfColorsInInterval; i++) {
			Range subRange = new Range(begin + i*deltaX, begin + (i+1)*deltaX);
			Integer c = Color.argb(alpha, 255, 255 - (int)((cont*255.0)/numberOfColorsInInterval), 0);
			colors.add(new Pair<Range, Integer>(subRange, c));
			cont++;
		}
		cont = 0;
		for (; i < 5*numberOfColorsInInterval; i++) {
			Range subRange = new Range(begin + i*deltaX, begin + (i+1)*deltaX);
			if (i == 5*numberOfColorsInInterval-1) {
				// Due to float division, it is necessary to create the last range to the end
				subRange = new Range(begin + i*deltaX, end);
			}
			Integer c = Color.argb(alpha, 255 - (int)((cont*(128))/numberOfColorsInInterval) ,0,0);
			colors.add(new Pair<Range, Integer>(subRange, c));
			cont++;
		}
	}

	private void createMapGreenToRed() {
		colors = new ArrayList<Pair<Range, Integer>>();

		double deltaX = (end-begin)/numberOfColors;
		int i = 0, cont = 0;
		int numberOfColorsInInterval = numberOfColors/5;

		for (; i < numberOfColorsInInterval; i++) {
			Range subRange = new Range(begin + i*deltaX, begin + (i+1)*deltaX);
			Integer c = Color.argb(alpha, 0, 128 + (int)((cont*(128))/numberOfColorsInInterval), 0);
			colors.add(new Pair<Range, Integer>(subRange, c));
			cont++;
		}
		cont = 0;
		for (; i < 2*numberOfColorsInInterval; i++) {
			Range subRange = new Range(begin + i*deltaX, begin + (i+1)*deltaX);
			Integer c = Color.argb(alpha, (int)((cont*128.0)/numberOfColorsInInterval), 255, 0);
			colors.add(new Pair<Range, Integer>(subRange, c));
			cont++;
		}
		cont = 0;
		for (; i < 3*numberOfColorsInInterval; i++) {
			Range subRange = new Range(begin + i*deltaX, begin + (i+1)*deltaX);
			Integer c = Color.argb(alpha, 128 + (int)((cont*128.0)/numberOfColorsInInterval), 255, 0);
			colors.add(new Pair<Range, Integer>(subRange, c));
			cont++;
		}
		cont = 0;
		for (; i < 4*numberOfColorsInInterval; i++) {
			Range subRange = new Range(begin + i*deltaX, begin + (i+1)*deltaX);
			Integer c = Color.argb(alpha, 255, 255 - (int)((cont*128.0)/numberOfColorsInInterval), 0);
			colors.add(new Pair<Range, Integer>(subRange, c));
			cont++;
		}
		cont = 0;
		for (; i < 5*numberOfColorsInInterval; i++) {
			Range subRange = new Range(begin + i*deltaX, begin + (i+1)*deltaX);
			if (i == 5*numberOfColorsInInterval-1) {
				// Due to float division, it is necessary to create the last range to the end
				subRange = new Range(begin + i*deltaX, end);
			}
			Integer c = Color.argb(alpha, 255,  128 - (int)((cont*(128))/numberOfColorsInInterval), 0);
			colors.add(new Pair<Range, Integer>(subRange, c));
			cont++;
		}
	}
}

class Range {
	private double begin;
	private double end;

	public Range(double b, double e) {
		begin = b;
		end = e;
	}
	public boolean at(double d) {
		return d <= end && d >= begin;
	}
}