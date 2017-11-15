package com.carisio.apps.exposurebasestationradiation.util.validators;

import com.carisio.apps.exposurebasestationradiation.R;

import android.content.Context;

public class DecimalNumberGreaterThanValidator extends BaseValidator {
	private double min;
	
	public DecimalNumberGreaterThanValidator(BaseValidator parent, Context ctx, double min) {
		super(parent, ctx);
		this.min = min;
	}

	@Override
	public boolean doIsValid(Object o) {
		try {
			double d = Double.parseDouble(o.toString());
			return d > min;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public String doGetErrorMessage(Object o, String name) {
		return name + " " + getString(R.string.validator_message_decimal_number_greater_than) + " " + min + ".";
	}

}
