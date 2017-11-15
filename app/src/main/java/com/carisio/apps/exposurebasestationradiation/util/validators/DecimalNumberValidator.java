package com.carisio.apps.exposurebasestationradiation.util.validators;

import android.content.Context;

import com.carisio.apps.exposurebasestationradiation.R;

public class DecimalNumberValidator extends BaseValidator {
	public DecimalNumberValidator(BaseValidator parent, Context ctx) {
		super(parent, ctx);
	}

	@Override
	public boolean doIsValid(Object o) {
		try {
			Double.parseDouble(o.toString());
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public String doGetErrorMessage(Object o, String name) {
		return name + " " + getString(R.string.validator_message_decimal_number);
	}

}
