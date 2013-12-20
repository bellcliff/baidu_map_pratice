package com.baidu.test.mc.iermu;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.MKEvent;

public class MyApplication extends Application {

	private static MyApplication mInstance = null;
	// for home
	// public static final String strKey = "lfGknURiy4lApRRGstaORHnM";
	// for work
	public static final String strKey = "GOgGIZnysCsGwzmdEYQKjEIl";
	public boolean m_bKeyRight = true;
	BMapManager mBMapManager = null;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();

		mInstance = this;
		initEngineManager(this);
	}

	public void initEngineManager(Context context) {
		if (mBMapManager == null) {
			mBMapManager = new BMapManager(context);
		}

		if (!mBMapManager.init(strKey, new MyGeneralListener())) {
			Toast.makeText(getInstance().getApplicationContext(),
					"BMapManager key error", Toast.LENGTH_LONG).show();
		}
	}

	public static MyApplication getInstance() {
		return mInstance;
	}

	static class MyGeneralListener implements MKGeneralListener {

		@Override
		public void onGetNetworkState(int iError) {
			if (iError == MKEvent.ERROR_NETWORK_CONNECT) {
				Toast.makeText(getInstance().getApplicationContext(),
						"network error", Toast.LENGTH_LONG).show();
			} else if (iError == MKEvent.ERROR_NETWORK_DATA) {
				Toast.makeText(getInstance().getApplicationContext(),
						"network data error", Toast.LENGTH_LONG).show();
			}
			// ...
		}

		@Override
		public void onGetPermissionState(int iError) {
			if (iError != 0) {
				Toast.makeText(getInstance().getApplicationContext(),
						"key error" + iError, Toast.LENGTH_LONG).show();
				getInstance().m_bKeyRight = false;
			} else {
				getInstance().m_bKeyRight = true;
				Toast.makeText(getInstance().getApplicationContext(), "ok",
						Toast.LENGTH_LONG).show();
			}
		}
	}
}
