// Aplica formatação em números de acordo com a localidade en-US?

package com.carisio.apps.exposurebasestationradiation.util;

import java.util.Locale;

public class Format {
	public static String format(double[] num, int nDecimal, String separator) {
		String result = "";
		String format = "%." + nDecimal + "f";
		for (int i = 0; i < num.length-1; i++) {
			result += String.format(Locale.ENGLISH, format, num[i]) + separator;
		}
		result += String.format(Locale.ENGLISH, format, num[num.length-1]);
		return result;
	}
	public static String format(double num, int nDecimal) {
		String format = "%." + nDecimal + "f";
		return String.format(Locale.ENGLISH, format, num);
	}
}
