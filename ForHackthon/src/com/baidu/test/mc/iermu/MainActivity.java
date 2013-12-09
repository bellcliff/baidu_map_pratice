package com.baidu.test.mc.iermu;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.Ground;
import com.baidu.mapapi.map.GroundOverlay;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.platform.comapi.basestruct.GeoPoint;

public class MainActivity extends Activity {
	/**
	 * MapView �ǵ�ͼ���ؼ�
	 */
	private MapView mMapView = null;

	private MapController mMapController = null;
	private MyOverlay mOverlay = null;
	private PopupOverlay pop = null;
	private ArrayList<OverlayItem> mItems = null;
	private TextView popupText = null;
	private OverlayItem mCurItem = null;
	private Button button = null;
	private View popupInfo = null;
	private View popupLeft = null;
	private View popupRight = null;
	double mLngBidu = 116.30814954222;
	double mLatBidu = 40.056885091681;

	// ground overlay
	private GroundOverlay mGroundOverlay;
	private Ground mGround;
	private double mLon5 = 116.380338;
	private double mLat5 = 39.92235;
	private double mLon6 = 116.414977;
	private double mLat6 = 39.947246;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		MyApplication app = (MyApplication) getApplication();
		if (app.mBMapManager == null) {
			app.mBMapManager = new BMapManager(this);
			/**
			 * ���BMapManagerû�г�ʼ�����ʼ��BMapManager
			 */
			app.mBMapManager.init(MyApplication.strKey,
					new MyApplication.MyGeneralListener());
		}

		setContentView(R.layout.activity_main);

		mMapView = (MapView) findViewById(R.id.bmapView);
		/**
		 * ��ȡ��ͼ������
		 */
		mMapController = mMapView.getController();
		/**
		 * ���õ�ͼ�Ƿ���Ӧ����¼� .
		 */
		mMapController.enableClick(true);
		/**
		 * ���õ�ͼ���ż���
		 */
		mMapController.setZoom(19);
		/**
		 * ��ʾ�������ſؼ�
		 */
		mMapView.setBuiltInZoomControls(true);

		initOverlay();

		/**
		 * �趨��ͼ���ĵ�
		 */
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

	public void initOverlay() {
		/**
		 * �����Զ���overlay
		 */
		mOverlay = new MyOverlay(getResources().getDrawable(
				R.drawable.icon_marka), mMapView);

		GeoPoint p1 = new GeoPoint((int) (mLatBidu * 1E6),
				(int) (mLngBidu * 1E6));
		OverlayItem item1 = new OverlayItem(p1, "i��Ŀ", "");
		/**
		 * ����overlayͼ�꣬�粻���ã���ʹ�ô���ItemizedOverlayʱ��Ĭ��ͼ��.
		 */
		item1.setMarker(getResources().getDrawable(R.drawable.icon_marka));
		/**
		 * ��item ��ӵ�overlay�� ע�⣺ ͬһ��itmeֻ��addһ��
		 */
		mOverlay.addItem(item1);
		/**
		 * ��������item���Ա�overlay��reset���������
		 */
		mItems = new ArrayList<OverlayItem>();
		mItems.addAll(mOverlay.getAllItem());

		/**
		 * ��overlay �����MapView��
		 */
		mMapView.getOverlays().add(mOverlay);
		/**
		 * ˢ�µ�ͼ
		 */
		mMapView.refresh();
	}

	public class MyOverlay extends ItemizedOverlay {

		public MyOverlay(Drawable defaultMarker, MapView mapView) {
			super(defaultMarker, mapView);
		}

		@Override
		public boolean onTap(int index) {
			OverlayItem item = getItem(index);
			mCurItem = item;
			Toast.makeText(getApplication(), "����i��Ŀ", Toast.LENGTH_SHORT)
					.show();
			startCamera();
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

		private void startCamera() {
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			intent.putExtra(MediaStore.EXTRA_OUTPUT,
					Uri.fromFile(new File("/")));
			startActivityForResult(intent, 1);
		}

	}
}
