package com.carisio.apps.exposurebasestationradiation.util.validators;

import android.content.Context;

import com.carisio.apps.exposurebasestationradiation.R;
import com.carisio.apps.exposurebasestationradiation.util.Converter;

public class ArrayDecimalNumberLowerThanValidator extends BaseValidator {
	private double max;
	
	public ArrayDecimalNumberLowerThanValidator(BaseValidator parent, Context ctx, double max) {
		super(parent, ctx);
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

				if (!(d < max)) {
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
		return name + " " + getString(R.string.validator_message_decimal_number_lower_than) + " " + max + ".";
	}
}
