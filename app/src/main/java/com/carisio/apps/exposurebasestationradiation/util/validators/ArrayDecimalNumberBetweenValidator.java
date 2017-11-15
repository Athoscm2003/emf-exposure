package com.carisio.apps.exposurebasestationradiation.util.validators;

import android.content.Context;

import com.carisio.apps.exposurebasestationradiation.R;
import com.carisio.apps.exposurebasestationradiation.util.Converter;

public class ArrayDecimalNumberBetweenValidator  extends BaseValidator {
	private double min, max;
	
	public ArrayDecimalNumberBetweenValidator(BaseValidator parent, Context ctx, double min, double max) {
		super(parent, ctx);
		this.min = min;
		this.max = max;
	}

	@Override
	public boolean doIsValid(Object o) {
		try {
			double[] numbers = Converter.string2DoubleArray(o.toString());
			if (numbers.length == 0)
				return false;
			
			for (int i = 0; i < numbers.length; i++) {
				double d = numbers[i];
				
				if (!(d >= min && d <= max)) {
					return false;
				}
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public String doGetErrorMessage(Object o, String name) {
		return name + " " + getString(R.string.validator_message_decimal_in_interval) + " [" + min + ", " + max + "].";
	}
}
