// Criação de fragmento que retém mudanças de configuração?

package com.carisio.apps.exposurebasestationradiation;

import android.support.v4.app.Fragment;
import android.os.Bundle;

public class RetainedFragment extends Fragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}
}
