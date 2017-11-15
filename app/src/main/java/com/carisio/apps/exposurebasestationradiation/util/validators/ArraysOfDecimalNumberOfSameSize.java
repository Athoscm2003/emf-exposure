package com.carisio.apps.exposurebasestationradiation.util.validators;

import com.carisio.apps.exposurebasestationradiation.R;
import com.carisio.apps.exposurebasestationradiation.util.Converter;

import android.content.Context;

public class ArraysOfDecimalNumberOfSameSize extends BaseValidator {

	public ArraysOfDecimalNumberOfSameSize(BaseValidator parent, Context ctx) {
		super(parent, ctx);
	}

	/**
	 * Object should be an String[] object where each string is an array of double converted in a string
	 */
	@Override
	public boolean doIsValid(Object o) {
		try {
			String[] array = (String[])o;
			
			if (array.length < 2)
				return false;
			
			int initialSize = Converter.string2DoubleArray(array[0]).length;
			for (int i = 1; i < array.length; i++) {
				int size = Converter.string2DoubleArray(array[i]).length;
				if (initialSize != size)
					return false;
			}
			
			return true;
		} catch (Exception e) {
			return false;			
		}
	}

	@Override
	public String doGetErrorMessage(Object o, String name) {
		return name + " " + getString(R.string.validator_message_arrays_of_decimal_numbers_of_same_size);
	}

}
