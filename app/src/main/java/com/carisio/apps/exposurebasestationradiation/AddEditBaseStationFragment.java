package com.carisio.apps.exposurebasestationradiation;

import static com.carisio.apps.exposurebasestationradiation.util.Format.format;
import telecom.basestation.DirectivityCat2BS;
import telecom.util.Point2D;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.carisio.apps.exposurebasestationradiation.util.Converter;
import com.carisio.apps.exposurebasestationradiation.util.MapDrawing;
import com.carisio.apps.exposurebasestationradiation.util.ProjectDatabase;
import com.carisio.apps.exposurebasestationradiation.util.UtilView;
import com.carisio.apps.exposurebasestationradiation.util.validators.ArrayDecimalNumberBetweenValidator;
import com.carisio.apps.exposurebasestationradiation.util.validators.ArrayDecimalNumberGreaterThanValidator;
import com.carisio.apps.exposurebasestationradiation.util.validators.ArrayDecimalNumberLowerThanValidator;
import com.carisio.apps.exposurebasestationradiation.util.validators.ArrayDecimalNumberValidator;
import com.carisio.apps.exposurebasestationradiation.util.validators.ArraysOfDecimalNumberOfSameSize;
import com.carisio.apps.exposurebasestationradiation.util.validators.BaseValidator;
import com.carisio.apps.exposurebasestationradiation.util.validators.DecimalNumberValidator;
import com.carisio.apps.exposurebasestationradiation.util.validators.StringNotEmptyValidator;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

public class AddEditBaseStationFragment extends RetainedFragment implements OnFocusChangeListener {
	private EditText edtName;
	private EditText edtLatitude;
	private EditText edtLongitude;
	private EditText edtHeight;
	private EditText edtFreq;
	private EditText edtTilt;
	private EditText edtVertBW;
	private EditText edtEIRP;
	private EditText edtSSL;
	private EditText[] fields;
	
	private LatLng latLng;
	private GoogleMap map;
	private String bsID;
	private boolean add;
	
	/* If bsID is null, than this class is used to add a new BS.
	 * Otherwise, this class is used to edit it.*/
	public AddEditBaseStationFragment(LatLng latLng, GoogleMap map, String bsID) {
		this.latLng = latLng;
		this.map = map;
		this.bsID = bsID;
		this.add = (bsID == null);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.add_edit_basestation_layout, container, false);
		
		loadEditText(view);
		
		TextView title = (TextView)view.findViewById(R.id.add_edit_bs);
		title.setText(getString(add ? R.string.add_base_station : R.string.edit_base_station));
		
		Button save = (Button) view.findViewById(R.id.button_save);
		save.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				addBaseStation();
			}
		});
		
		Button cancel = (Button) view.findViewById(R.id.button_cancel);
		cancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getFragmentManager().popBackStack();
			}
		});
		
		if (add)
			setDefaultValues(view);
		else 
			loadValuesFromId(view);
		
		return view;
	}
	private void loadEditText(View view) {
		edtName = (EditText) view.findViewById(R.id.edittext_name);
		edtLatitude = (EditText) view.findViewById(R.id.edittext_latitude);
		edtLongitude = (EditText) view.findViewById(R.id.edittext_longitude);
		edtHeight = (EditText) view.findViewById(R.id.edittext_basestation_height);
		edtFreq = (EditText) view.findViewById(R.id.edittext_frequency);
		edtTilt = (EditText) view.findViewById(R.id.edittext_tilt);
		edtVertBW = (EditText) view.findViewById(R.id.edittext_vertical_beamwidth);
		edtEIRP = (EditText) view.findViewById(R.id.edittext_transmitted_power);
		edtSSL = (EditText) view.findViewById(R.id.edittext_sidelobe_envelope);
		
		fields = new EditText[]{edtName, edtLatitude, edtLongitude, edtHeight, edtFreq, edtTilt, edtVertBW, edtEIRP, edtSSL};
		
		edtName.setOnFocusChangeListener(this);
		edtLatitude.setOnFocusChangeListener(this);
		edtLongitude.setOnFocusChangeListener(this);
		edtHeight.setOnFocusChangeListener(this);
		edtFreq.setOnFocusChangeListener(this);
		edtTilt.setOnFocusChangeListener(this);
		edtVertBW.setOnFocusChangeListener(this);
		edtEIRP.setOnFocusChangeListener(this);
		edtSSL.setOnFocusChangeListener(this);
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if (!hasFocus)
			return;
		
		for (int i = 0; i < fields.length; i++) {
			if (v.equals(fields[i])) {
				fields[i].setSelection(fields[i].length());
			}
		}
	}
	
	private void loadValuesFromId(View view) {
		DirectivityCat2BS bs = (DirectivityCat2BS) ProjectDatabase.findBS(bsID);
		
		edtName.setText(bs.getName());
		edtLatitude.setText(format(bs.getPosition().getLatitude(), 7));
		edtLongitude.setText(format(bs.getPosition().getLongitude(), 7));
		edtHeight.setText(format(bs.getHeight(), 2, " "));
		edtFreq.setText(format(bs.getFrequencyMHz(), 3, " "));
		edtTilt.setText(format(bs.getTiltDegree(), 2, " "));
		edtVertBW.setText(format(bs.getThetaBwVerticalDegree(), 2, " "));
		edtEIRP.setText(format(bs.getEirpMaxdBm(), 2, " "));
		edtSSL.setText(format(bs.getMaxSideLobeEnvelopedB(), 2, " "));
	}
	private void setDefaultValues(View view) {
		edtName.setText(getString(R.string.base_station) + " #" + (ProjectDatabase.getBaseStations(getContext()).size()+1));
		edtLatitude.setText(format(latLng.latitude, 7));
		edtLongitude.setText(format(latLng.longitude, 7));
		edtHeight.setText("30");
		edtFreq.setText("1800");
		edtTilt.setText("3");
		edtVertBW.setText("8");
		edtEIRP.setText("60");
		edtSSL.setText("-15");
	}
	
	private void addBaseStation() {
		String name = edtName.getText().toString().trim();
		String latitude = edtLatitude.getText().toString();
		String longitude = edtLongitude.getText().toString();
		String basestation_height = edtHeight.getText().toString();
		String frequency = edtFreq.getText().toString();
		String tilt = edtTilt.getText().toString();
		String teta_bw_v = edtVertBW.getText().toString();
		String eirp = edtEIRP.getText().toString();
		String ssl_envelope = edtSSL.getText().toString();

		String errorMessage = getErrorMessage(name, latitude, longitude, basestation_height, frequency, tilt, teta_bw_v, eirp, ssl_envelope);
		if (errorMessage.equals("")) {
			DirectivityCat2BS bs;
			if (add) {
				bs = new DirectivityCat2BS();
				ProjectDatabase.addBS(bs, getContext());
			} else {
				bs = (DirectivityCat2BS) ProjectDatabase.findBS(bsID);
			}
			bs.setName(name);
			bs.setPosition(new Point2D(Double.parseDouble(latitude), Double.parseDouble(longitude)));
			bs.setHeight(Converter.string2DoubleArray(basestation_height));
			bs.setFrequencyMHz(Converter.string2DoubleArray(frequency));
			bs.setTiltDegree(Converter.string2DoubleArray(tilt));
			bs.setThetaBwVerticalDegree(Converter.string2DoubleArray(teta_bw_v));
			bs.setEirpMaxdBm(Converter.string2DoubleArray(eirp));
			bs.setMaxSideLobeEnvelopedB(Converter.string2DoubleArray(ssl_envelope));
			
			ProjectDatabase.deleteBox(getContext());
			ProjectDatabase.saveBaseStations(getContext());
			MapDrawing.drawProject(getContext(), map);
			getFragmentManager().popBackStack();
		} else {
			AlertDialog dialog = new AlertDialog.Builder(getContext())
		    .setTitle(getString(R.string.error_validating_fields))
		    .setMessage(errorMessage)
		    .setCancelable(false)
		    .setPositiveButton(getString(R.string.ok_button), null)
		    .setIcon(android.R.drawable.ic_dialog_alert)
		    .show();
			UtilView.applyAlertDialogStyle(dialog);
		}
	}

	private String getErrorMessage(String name, String latitude, String longitude,
			String basestation_height, String frequency, String tilt,
			String teta_bw_v, String eirp, String ssl_envelope) {
		
		String errorMessage = validateName(name);
		errorMessage += validateLatitude(latitude);
		errorMessage += validateLongitude(longitude);
		errorMessage += validateHeight(basestation_height);
		errorMessage += validateFrequency(frequency);
		errorMessage += validateTilt(tilt);
		errorMessage += validateVerticalBW(teta_bw_v);
		errorMessage += validateeirp(eirp);
		errorMessage += validateSSL(ssl_envelope);
		errorMessage += validateSameSize(new String[]{basestation_height, frequency, tilt, teta_bw_v, eirp, ssl_envelope});

		return errorMessage.trim();
	}

	private String validateName(String name) {
		StringNotEmptyValidator validator = new StringNotEmptyValidator(null, getContext());
		return getErrorMessage(validator, name, getString(R.string.name));
	}
	private String validateLatitude(String latitude) {
		DecimalNumberValidator validator = new DecimalNumberValidator(null, getContext());
		return getErrorMessage(validator, latitude, getString(R.string.latitude_with_unit));
	}
	private String validateLongitude(String longitude) {
		DecimalNumberValidator validator = new DecimalNumberValidator(null, getContext());
		return getErrorMessage(validator, longitude, getString(R.string.longitude_with_unit));
	}
	private String validateHeight(String height) {
		ArrayDecimalNumberGreaterThanValidator validator = new ArrayDecimalNumberGreaterThanValidator(null, getContext(), 0);
		return getErrorMessage(validator, height, getString(R.string.basestation_height_with_unit));
	}
	private String validateFrequency(String freq) {
		ArrayDecimalNumberBetweenValidator validator = new ArrayDecimalNumberBetweenValidator(null, getContext(), 10, 5000);
		return getErrorMessage(validator, freq, getString(R.string.frequency_with_unit));
	}
	private String validateTilt(String tilt) {
		ArrayDecimalNumberBetweenValidator validator = new ArrayDecimalNumberBetweenValidator(null, getContext(), -90, 90);
		return getErrorMessage(validator, tilt, getString(R.string.tilt_with_unit));
	}
	private String validateVerticalBW(String tetaBwV) {
		ArrayDecimalNumberBetweenValidator validator = new ArrayDecimalNumberBetweenValidator(null, getContext(), 1, 360);
		return getErrorMessage(validator, tetaBwV, getString(R.string.vertical_beamwidth_with_unit));
	}
	private String validateeirp(String eirp) {
		ArrayDecimalNumberValidator validator = new ArrayDecimalNumberValidator(null, getContext());
		return getErrorMessage(validator, eirp, getString(R.string.transmitted_power_with_unit));
	}
	private String validateSSL(String ssl) {
		ArrayDecimalNumberLowerThanValidator validator = new ArrayDecimalNumberLowerThanValidator(null, getContext(), 0);
		return getErrorMessage(validator, ssl, getString(R.string.sidelobe_envelope_with_unit));
	}
	private String validateSameSize(String[] array) {
		ArraysOfDecimalNumberOfSameSize validator = new ArraysOfDecimalNumberOfSameSize(null, getContext());
		return getErrorMessage(validator, array, getString(R.string.basestation_height_with_unit) + ", " + getString(R.string.frequency_with_unit) 
				+ ", " + getString(R.string.tilt_with_unit) + ", " + getString(R.string.vertical_beamwidth_with_unit)
				+ ", " + getString(R.string.transmitted_power_with_unit) + ", " + getString(R.string.sidelobe_envelope_with_unit));
	}
	private String getErrorMessage(BaseValidator validator, Object obj, String property) {
		String errorMessage = "";
		if (!validator.isValid(obj)) {
			return validator.getErrorMessage(obj, property);
		}
		return errorMessage;
	}
}
