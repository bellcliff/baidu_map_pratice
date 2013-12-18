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
	/**
	 * MapView 是地图主控件
	 */
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
			/**
			 * 如果BMapManager没有初始化则初始化BMapManager
			 */
			app.mBMapManager.init(MyApplication.strKey,
					new MyApplication.MyGeneralListener());
		}

		setContentView(R.layout.activity_main);

		mMapView = (MapView) findViewById(R.id.bmapView);
		/**
		 * 获取地图控制器
		 */
		mMapController = mMapView.getController();
		/**
		 * 设置地图是否响应点击事件 .
		 */
		mMapController.enableClick(true);
		/**
		 * 设置地图缩放级别
		 */
		mMapController.setZoom(19);
		/**
		 * 显示内置缩放控件
		 */
		mMapView.setBuiltInZoomControls(true);

		initOverlay();

		/**
		 * 设定地图中心点
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

	@SuppressWarnings("unchecked")
	public void initOverlay() {
		/**
		 * 创建自定义overlay
		 */
		mOverlay = new MyOverlay(getResources().getDrawable(
				R.drawable.icon_marka), mMapView);

		GeoPoint p1 = new GeoPoint((int) (mLatBidu * 1E6),
				(int) (mLngBidu * 1E6));
		OverlayItem item1 = new OverlayItem(p1, "i耳目", "");
		/**
		 * 设置overlay图标，如不设置，则使用创建ItemizedOverlay时的默认图标.
		 */
		item1.setMarker(getResources().getDrawable(R.drawable.icon_marka));
		/**
		 * 将item 添加到overlay中 注意： 同一个itme只能add一次
		 */
		mOverlay.addItem(item1);
		/**
		 * 保存所有item，以便overlay在reset后重新添加
		 */
		mItems = new ArrayList<OverlayItem>();
		mItems.addAll(mOverlay.getAllItem());

		/**
		 * 将overlay 添加至MapView中
		 */
		mMapView.getOverlays().add(mOverlay);
		/**
		 * 刷新地图
		 */
		mMapView.refresh();
	}

	@SuppressWarnings("rawtypes")
	public class MyOverlay extends ItemizedOverlay {

		public MyOverlay(Drawable defaultMarker, MapView mapView) {
			super(defaultMarker, mapView);
		}

		@Override
		public boolean onTap(int index) {
			// OverlayItem item = getItem(index);
			// mCurItem = item;
			Toast.makeText(getApplication(), "启动i耳目", Toast.LENGTH_SHORT)
					.show();
			playVideo();
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
		String source = "";
		if (source == null || source.equals("")) {
			/**
			 * 简单检测播放源的合法性,不合法不播放
			 */
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
}
