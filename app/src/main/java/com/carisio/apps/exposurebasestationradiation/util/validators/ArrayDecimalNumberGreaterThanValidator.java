package com.carisio.apps.exposurebasestationradiation.util.validators;

import android.content.Context;

import com.carisio.apps.exposurebasestationradiation.R;
import com.carisio.apps.exposurebasestationradiation.util.Converter;

public class ArrayDecimalNumberGreaterThanValidator extends BaseValidator {
	private double min;
	
	public ArrayDecimalNumberGreaterThanValidator(BaseValidator parent, Context ctx, double min) {
		super(parent, ctx);
		this.min = min;
	}

	@Override
	public boolean doIsValid(Object o) {
		try {
			double[] numbers = Converter.string2DoubleArray(o.toString());
			if (numbers.length == 0)
				return false;
			
			for (int i = 0; i < numbers.length; i++) {
				double d = numbers[i];
				
				if (!(d > min)) {
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
		return name + " " + getString(R.string.validator_message_decimal_number_greater_than) + " " + min + ".";
	}
}
