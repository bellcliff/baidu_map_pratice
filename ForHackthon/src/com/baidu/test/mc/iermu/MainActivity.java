package com.baidu.test.mc.iermu;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Button;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.platform.comapi.basestruct.GeoPoint;

public class MainActivity extends Activity {
	private MapView mMapView = null;

	private MapController mMapController = null;
	private MyOverlay mOverlay = null;
	private PopupOverlay pop = null;
	private ArrayList<OverlayItem> mItems = null;
	// private TextView popupText = null;
	// private OverlayItem mCurItem = null;
	private Button button = null;
	// private View popupInfo = null;
	// private View popupLeft = null;
	// private View popupRight = null;
	double mLngBidu = 116.30814954222;
	double mLatBidu = 40.056885091681;

	// ground overlay
	// private GroundOverlay mGroundOverlay;
	// private Ground mGround;
	// private double mLon5 = 116.380338;
	// private double mLat5 = 39.92235;
	// private double mLon6 = 116.414977;
	// private double mLat6 = 39.947246;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		MyApplication app = (MyApplication) getApplication();
		if (app.mBMapManager == null) {
			app.mBMapManager = new BMapManager(this);
			app.mBMapManager.init(MyApplication.strKey,
					new MyApplication.MyGeneralListener());
		}

		setContentView(R.layout.activity_main);

		mMapView = (MapView) findViewById(R.id.bmapView);
		mMapController = mMapView.getController();
		mMapController.enableClick(true);
		mMapController.setZoom(19);
		mMapView.setBuiltInZoomControls(true);

		initOverlay();
		GeoPoint p = new GeoPoint((int) (mLatBidu * 1E6),
				(int) (mLngBidu * 1E6));
		mMapController.setCenter(p);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mMapView.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mMapView.onResume();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		mMapView.destroy();
		super.onDestroy();
	}

	@SuppressWarnings("unchecked")
	public void initOverlay() {
		mOverlay = new MyOverlay(getResources().getDrawable(R.drawable.images),
				mMapView);

		GeoPoint p1 = new GeoPoint((int) (mLatBidu * 1E6),
				(int) (mLngBidu * 1E6));
		OverlayItem item1 = new OverlayItem(p1, "i耳目", "");
		item1.setMarker(getResources().getDrawable(R.drawable.images));
		mOverlay.addItem(item1);
		mItems = new ArrayList<OverlayItem>();
		mItems.addAll(mOverlay.getAllItem());
		mMapView.getOverlays().add(mOverlay);
		mMapView.refresh();
	}

	@SuppressWarnings("rawtypes")
	public class MyOverlay extends ItemizedOverlay {

		public MyOverlay(Drawable defaultMarker, MapView mapView) {
			super(defaultMarker, mapView);
		}

		@Override
		public boolean onTap(int index) {
			playPicture();
			return true;
		}

		@Override
		public boolean onTap(GeoPoint pt, MapView mMapView) {
			if (pop != null) {
				pop.hidePop();
				mMapView.removeView(button);
			}
			return false;
		}

	}

	private void playVideo() {
		// get url
		String source = "rtmp://hz.bms.baidu.com:1935/live/b959319e686b11e3b7af5cf3fce6e084?deviceid=137892435131&sign=DTAES-CqnyQSm05YocyMV8Skl5IwA2-srRtbnrb8OMenU6CmZRNtNKBeTU%3D&time=1387536200&expire=1387536260";
		System.out.println(Uri.parse(source));
		if (source == null || source.equals("")) {
			Toast.makeText(this, "please input your video source",
					Toast.LENGTH_SHORT).show();
			source = "http://devimages.apple.com/iphone/samples/bipbop/gear4/prog_index.m3u8";

			Intent intent = new Intent(this, VideoActivity.class);
			intent.setData(Uri.parse(source));
			startActivity(intent);

		} else {
			Intent intent = new Intent(this, VideoActivity.class);
			intent.setData(Uri.parse(source));
			startActivity(intent);
		}
	}

	private void playPicture() {
		Intent intent = new Intent(this, StandardVideoActivity.class);
		startActivity(intent);
	}
}
