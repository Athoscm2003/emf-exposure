package com.carisio.apps.exposurebasestationradiation;

import android.app.ActionBar.LayoutParams;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.carisio.apps.exposurebasestationradiation.util.AppProperties;
import com.carisio.apps.exposurebasestationradiation.util.ColorMap;
import com.carisio.apps.exposurebasestationradiation.util.ColorMap.COLORMAP_TYPE;
import com.carisio.apps.exposurebasestationradiation.util.MapDrawing;
import com.carisio.apps.exposurebasestationradiation.util.ProjectDatabase;
import com.carisio.apps.exposurebasestationradiation.util.UtilView;
import com.carisio.apps.exposurebasestationradiation.util.validators.BaseValidator;
import com.carisio.apps.exposurebasestationradiation.util.validators.DecimalNumberGreaterThanOrEqualValidator;
import com.carisio.apps.exposurebasestationradiation.util.validators.DecimalNumberGreaterThanValidator;

public class PropertiesFragment extends RetainedFragment implements OnFocusChangeListener {
	private EditText edtProbeHeight;
	private EditText edtMin;
	private EditText edtMax;
	private EditText[] fields;
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		Spinner spnColorMap = (Spinner) getView().findViewById(R.id.spinner_colormap_options);		
		outState.putInt("spncolormap", spnColorMap.getSelectedItemPosition());
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			final Bundle savedInstanceState) {
		
		final View view = inflater.inflate(R.layout.properties_layout, container, false);
		loadEditText(view);
		final TextView txtMinValue = (TextView) view.findViewById(R.id.textview_min_value);
		final TextView txtMaxValue = (TextView) view.findViewById(R.id.textview_max_value);
		final Spinner spn = ((Spinner)view.findViewById(R.id.spinner_show_in_box_options));
		spn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				if (spn.getSelectedItem().equals(getString(R.string.e_field))) {
					txtMinValue.setText(getString(R.string.min_value_vm));
					txtMaxValue.setText(getString(R.string.max_value_vm));
				} else {
					txtMinValue.setText(getString(R.string.min_value_percentage));
					txtMaxValue.setText(getString(R.string.max_value_percentage));
				}
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		final Spinner colorMaps = (Spinner)view.findViewById(R.id.spinner_colormap_options);
		ViewTreeObserver viewTreeObserver = colorMaps.getViewTreeObserver();
		if (viewTreeObserver.isAlive()) {
		  viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
		    @Override
		    public void onGlobalLayout() {
		    	colorMaps.getViewTreeObserver().removeOnGlobalLayoutListener(this);
		    	colorMaps.setAdapter(new ColorMapSpinnerAdapter(getContext(), 0, ColorMap.COLORMAP_TYPE.values()));
		    	
		    	// This need to be done only after the spinner of colormap was setted.
		    	// And the spinner should be setted after we can get parent.getWidth() != 0
		    	initializeColorMapSpinner(view, savedInstanceState);
		    }
		  });
		}
		
		Button save = (Button) view.findViewById(R.id.button_save_properties);
		save.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				saveProperties();
			}
		});
		Button cancel = (Button) view.findViewById(R.id.button_cancel_properties);
		cancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getFragmentManager().popBackStack();
			}
		});
		
		initializeVariables(view);
		return view;
	}

	private void loadEditText(View view) {
		edtProbeHeight = (EditText) view.findViewById(R.id.edittext_probe_height);
		edtMin= (EditText) view.findViewById(R.id.edittext_min_value);
		edtMax = (EditText) view.findViewById(R.id.edittext_max_value);
		
		fields = new EditText[]{edtProbeHeight, edtMin, edtMax};
		
		edtProbeHeight.setOnFocusChangeListener(this);
		edtMin.setOnFocusChangeListener(this);
		edtMax.setOnFocusChangeListener(this);
		
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
	private void initializeColorMapSpinner(View view, Bundle savedInstanceState) {
		/*
		 * This method is called by a ViewTreeObserver.OnGlobalLayoutListener listener.
		 * This means it overrides the default method of android. The default method restores
		 * the state (if any) of the components in onActivityCreated.
		 * 
		 * The first time this Fragment is created, there are no information in the components and
		 * the spinner should be initialized with AppProperties.
		 * 
		 * After this moment, android saved automatically the state of the components. 
		 * So, if the fragment, for example, has its orientation changed,
		 * the value of colormap spinner is restored automatically and cannot be changed
		 * here using the AppProperties class.
		 * 
		 * Therefore, we need to set a new variable in savedInstanceState to control this information.
		 */
		if (savedInstanceState == null || savedInstanceState.getInt("spncolormap", -1) == -1) {
			
			Spinner spnColorMap = (Spinner) view.findViewById(R.id.spinner_colormap_options);
			ColorMap.COLORMAP_TYPE type = AppProperties.getColorMapType();
			
			for (int i = 0; i < ColorMap.COLORMAP_TYPE.values().length; i++) {
				if (type.equals(spnColorMap.getItemAtPosition(i))) {
					spnColorMap.setSelection(i);
					break;
				}
			}			
		}
	}
	private void initializeVariables(View view) {
		EditText edtProbeHeight = (EditText) view.findViewById(R.id.edittext_probe_height);
		double probeHeightValue = AppProperties.getProbeHeight();
		edtProbeHeight.setText("" + probeHeightValue);
		
		Spinner spnShowInBox = (Spinner) view.findViewById(R.id.spinner_show_in_box_options);
		String option = AppProperties.getShowInBox(getContext());
		spnShowInBox.setSelection(getPositionInArray(option, getResources().getStringArray(R.array.show_in_box_options)));
		
		EditText edtMinValue = (EditText) view.findViewById(R.id.edittext_min_value);
		double minValue = AppProperties.getMinValueInBox();
		edtMinValue.setText("" + minValue);

		EditText edtMaxValue = (EditText) view.findViewById(R.id.edittext_max_value);
		double maxValue = AppProperties.getMaxValueInBox();
		edtMaxValue.setText("" + maxValue);

		Spinner spnMapType = (Spinner) view.findViewById(R.id.spinner_map_type_options);
		String mapType = AppProperties.getMapType(getContext());
		spnMapType.setSelection(getPositionInArray(mapType, getResources().getStringArray(R.array.map_type_options)));
		
	}
	
	private int getPositionInArray(String str, String[] stringArray) {
		int result = 0;
		for (int i = 0; i < stringArray.length; i++) {
			if (str.equals(stringArray[i])) {
				result = i;
				break;
			}
		}
		return result;
	}

	private void saveProperties() {
		EditText edtprobeHeight = (EditText) getView().findViewById(R.id.edittext_probe_height);
		String probeHeight = edtprobeHeight.getText().toString();
		
		Spinner spnShowInBox = (Spinner) getView().findViewById(R.id.spinner_show_in_box_options);
		String selectedShowInBox = spnShowInBox.getSelectedItem().toString();

		EditText edtMinValue = (EditText) getView().findViewById(R.id.edittext_min_value);
		String minValue = edtMinValue.getText().toString();
		
		EditText edtMaxValue = (EditText) getView().findViewById(R.id.edittext_max_value);
		String maxValue = edtMaxValue.getText().toString();
		
		Spinner spnColorMap = (Spinner) getView().findViewById(R.id.spinner_colormap_options);		

		Spinner spnMapType = (Spinner) getView().findViewById(R.id.spinner_map_type_options);
		String selectedMapType = spnMapType.getSelectedItem().toString();
		
		String errorMessage = validateProbeHeight(probeHeight);
		errorMessage += validateMinValue(minValue);
		errorMessage += validateMaxValue(maxValue, minValue);
		
		if (errorMessage.trim().equals("")) {
			boolean needClearBox = AppProperties.getProbeHeight() != Double.parseDouble(probeHeight);
			
			AppProperties.setProbeHeight(probeHeight);
			AppProperties.setShowInBox(selectedShowInBox, getActivity());
			AppProperties.setMinValueInBox(minValue);
			AppProperties.setMaxValueInBox(maxValue);
			AppProperties.setColorMapType((ColorMap.COLORMAP_TYPE)spnColorMap.getSelectedItem());
			AppProperties.setMapType(selectedMapType, getActivity());
			
			AppProperties.saveProperties(getContext());
			
			if (needClearBox) {
				ProjectDatabase.deleteBox(getContext());
				MapDrawing.removeBox();
			}
			
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
	
	private String validateMaxValue(String maxValue, String minValue) {
		double value = 0;
		try {
			value = Double.parseDouble(minValue);
		} catch (Exception e) {
			
		}
		DecimalNumberGreaterThanValidator validator = new DecimalNumberGreaterThanValidator(null, getContext(), value);
		return getErrorMessage(validator, maxValue, ((TextView)getView().findViewById(R.id.textview_max_value)).getText().toString());
	}

	private String validateMinValue(String minValue) {
		DecimalNumberGreaterThanOrEqualValidator validator = new DecimalNumberGreaterThanOrEqualValidator(null, getContext(), 0);
		return getErrorMessage(validator, minValue, ((TextView)getView().findViewById(R.id.textview_min_value)).getText().toString());
	}

	private String validateProbeHeight(String height) {
		DecimalNumberGreaterThanValidator validator = new DecimalNumberGreaterThanValidator(null, getContext(), 0);
		return getErrorMessage(validator, height, getString(R.string.probe_height_with_units));
	}
	private String getErrorMessage(BaseValidator validator, Object obj, String property) {
		String errorMessage = "";
		if (!validator.isValid(obj)) {
			return validator.getErrorMessage(obj, property);
		}
		return errorMessage;
	}
	class ColorMapSpinnerAdapter extends ArrayAdapter<COLORMAP_TYPE> {
		private int width = 0;
		private int height = 0;
		
		private ColorMap[] colorMaps;
		public ColorMapSpinnerAdapter(Context context, int resource, COLORMAP_TYPE[] types) {
			super(context, resource, types);
			
			colorMaps = new ColorMap[types.length];
			for (int i = 0; i < types.length; i++) {
				colorMaps[i] = new ColorMap(100, 0, 100, 128, types[i]);
			}
		}
		
		@Override
		public View getDropDownView(int position, View convertView,
				ViewGroup parent) {
			return getColorMapView(position, convertView, parent, 10);
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			return getColorMapView(position, convertView, parent, 0);
		}
		private View getColorMapView(int position, View convertView,
				ViewGroup parent, int deltaY) {
			ImageView imgView = new ImageView(getContext());
			imgView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			imgView.setVisibility(ImageView.VISIBLE);

			if (width == 0 || height == 0) {
				width = parent.getWidth();
				height = parent.getHeight();
			}
			
			Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
			Canvas canvas = new Canvas(bitmap);
			
			ColorMap colorMap = colorMaps[position];
			float space = (float) (width*1.0/100.0);
			for (int i = 0; i < 100; i++) {
				Paint paint = new Paint();
				paint.setStyle(Paint.Style.FILL_AND_STROKE);
				paint.setColor(colorMap.GetColor(i));
				canvas.drawRect(i*space, deltaY, (i+1)*space, height-deltaY, paint);
			}

			imgView.setImageBitmap(bitmap);
			return imgView;
		}
	}
}
