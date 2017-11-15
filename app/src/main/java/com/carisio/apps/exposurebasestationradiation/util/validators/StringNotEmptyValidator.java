package com.carisio.apps.exposurebasestationradiation.util.validators;

import com.carisio.apps.exposurebasestationradiation.R;

import android.content.Context;

public class StringNotEmptyValidator extends BaseValidator {

	public StringNotEmptyValidator(BaseValidator parent, Context ctx) {
		super(parent, ctx);
	}

	@Override
	public boolean doIsValid(Object o) {
		if (o != null && ((String)o).length() > 0)
			return true;
		else
			return false;
	}

	@Override
	public String doGetErrorMessage(Object o, String name) {
		return name + " " + getString(R.string.validator_message_string_not_empty);
	}

}
