package com.carisio.apps.exposurebasestationradiation.util.validators;

import android.content.Context;

import com.carisio.apps.exposurebasestationradiation.R;
import com.carisio.apps.exposurebasestationradiation.util.Converter;

public class ArrayDecimalNumberValidator extends BaseValidator {
	public ArrayDecimalNumberValidator(BaseValidator parent, Context ctx) {
		super(parent, ctx);
	}

	@Override
	public boolean doIsValid(Object o) {
		try {
			double[] result = Converter.string2DoubleArray(o.toString());
			return result.length > 0;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public String doGetErrorMessage(Object o, String name) {
		return name + " " + getString(R.string.validator_message_array_decimal_number);
	}
}
