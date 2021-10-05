package no.nordicsemi.android.nrftoolbox.dfu;

import android.app.Activity;

import no.nordicsemi.android.dfu.DfuBaseService;

public class DfuService extends DfuBaseService {

	@Override
	protected Class<? extends Activity> getNotificationTarget() {
		return  null;
	}

	@Override
	protected boolean isDebug() {
		return true;
	}
}
