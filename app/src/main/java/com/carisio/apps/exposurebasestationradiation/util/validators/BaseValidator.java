package com.carisio.apps.exposurebasestationradiation.util.validators;

import android.content.Context;

public abstract class BaseValidator {
	private BaseValidator parent;
	private Context ctx;
	
	public String getString(int resId) {
		return ctx.getString(resId);
	}
	public BaseValidator(BaseValidator parent, Context ctx) {
		this.parent = parent;
		this.ctx = ctx;
	}

	public boolean isValid(Object o) {
		boolean result = doIsValid(o);
		
		if (parent != null) {
			result &= parent.isValid(o);
		}
		
		return result;
	}
	
	public abstract boolean doIsValid(Object o);
	
	public String getErrorMessage(Object o, String name) {
		boolean valid = doIsValid(o);
		String result = "";
		if (!valid) {
			result += doGetErrorMessage(o, name) + "\n";
		}
		
		if (parent != null && !(parent.isValid(o))) {
			result += parent.getErrorMessage(o, name);
		}
		
		return result;
	}
	public abstract String doGetErrorMessage(Object o, String name);
	
}
