package com.carisio.apps.exposurebasestationradiation.util.validators;

import com.carisio.apps.exposurebasestationradiation.R;

import android.content.Context;

public class DecimalNumberBetweenValidator extends BaseValidator {
	private double min, max;
	
	public DecimalNumberBetweenValidator(BaseValidator parent, Context ctx, double min, double max) {
		super(parent, ctx);
		this.min = min;
		this.max = max;
	}

	@Override
	public boolean doIsValid(Object o) {
		try {
			double d = Double.parseDouble(o.toString());
			return d >= min && d <= max;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public String doGetErrorMessage(Object o, String name) {
		return name + " " + getString(R.string.validator_message_decimal_in_interval) + " [" + min + ", " + max + "].";
	}
}
