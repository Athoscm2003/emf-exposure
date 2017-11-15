package com.carisio.apps.exposurebasestationradiation.util;

import android.app.AlertDialog;
import android.util.TypedValue;
import android.widget.TextView;

public class UtilView {
	public static void applyAlertDialogStyle(AlertDialog dialog) {
		TextView textView = (TextView) dialog.findViewById(android.R.id.message);
		textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
	}
}
