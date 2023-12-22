// Contém métodos que convertem arrays de double para strings e vice-versa

package com.carisio.apps.exposurebasestationradiation.util;

import java.util.StringTokenizer;

public class Converter {
	public static String doubleArray2String(double[] array) {
		int size = array.length;
		String str = "";
		
		if (size == 0)
			return str;
		
		for (int i = 0; i < size-1; i++)
			str += array[i] + " ";
		str += array[size-1];
		
		return str;
	}
	public static double[] string2DoubleArray(String str) {
		StringTokenizer tokenizer = new StringTokenizer(str, " ");
		
		double[] array = new double[tokenizer.countTokens()];

		int i = 0;
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			double d = Double.parseDouble(token);
			array[i] = d;
			i++;
		}
		return array;
	}

}
