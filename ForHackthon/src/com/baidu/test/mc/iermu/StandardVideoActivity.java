package com.baidu.test.mc.iermu;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.VideoView;

import com.baidu.cyberplayer.core.BVideoView;

public class StandardVideoActivity extends Activity {
	LinearLayout LL;
	List<BVideoView> bvvs = new ArrayList<BVideoView>();
	List<VideoView> vvs = new ArrayList<VideoView>();
	private static String AK = "cuqt8mBeWp6OMaLUmlC5u02E";
	private static String SK = "1kBXnL5ccPpAWZzUoCglcyr1XG9yd0lD";
	String[] paths = {
			"rtmp://hz.bms.baidu.com:1935/live/b959319e686b11e3b7af5cf3fce6e084?deviceid=137892435131&sign=DTAES-CqnyQSm05YocyMV8Skl5IwA2-m%2B%2FJmFWYwhinlKAz0N4Tl64tunI%3D&time=1387611525&expire=1408347525",
			"rtmp://qd.bms.baidu.com:1935/live/7824648a6a0711e3b7af5cf3fce6e084?deviceid=137892521483&sign=DTAES-CqnyQSm05YocyMV8Skl5IwA2-Fa35Rh%2FymeYGhe0NEAHrqXfZU04%3D&time=1387611557&expire=1408347557",
	// "rtmp://hz.bms.baidu.com:1935/live/ee9879c8694c11e39887782bcb118ade?deviceid=137892519579&sign=DTAES-CqnyQSm05YocyMV8Skl5IwA2-OweUhicxoVuOXR09AR7AHx8J1O4%3D&time=1387611571&expire=1408347571",
	// "rtmp://hz.bms.baidu.com:1935/live/7c4016f069e811e3b7af5cf3fce6e084?deviceid=137892521019&sign=DTAES-CqnyQSm05YocyMV8Skl5IwA2-aCCLU20NIeQGPW3aCwwruEfjzrs%3D&time=1387611584&expire=1408347584"
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.video_play);
		LL = (LinearLayout) findViewById(R.id.video_player);
		if (bvvs.isEmpty())
			// initVideo();
			addBaiduVideoPlayer(paths);
		// if (vvs.isEmpty()) {
		// addVideoPlayer(paths);
		// }
	}

	@Override
	protected void onPause() {
		super.onPause();
		for (BVideoView bvv : bvvs) {
			if (bvv.isActivated())
				bvv.pause();
		}
		for (VideoView vv : vvs) {
			if (vv.isPlaying())
				vv.pause();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
		for (BVideoView bvv : bvvs) {
			bvv.resume();
		}
		for (VideoView vv : vvs) {
			vv.resume();
		}
	}

	private void initVideo() {
		new Thread() {
			public void run() {
				try {
					String results = loadURL("http://iermudata.duapp.com/");
					if (results != null) {
						JSONArray js = new JSONArray(results);
						List<String> paths = new ArrayList<String>();
						for (int i = 0; i < js.length(); i++) {
							paths.add(js.getJSONObject(i).getString("url"));
						}
						addBaiduVideoPlayer(paths);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			};
		}.start();
	}

	private String loadURL(String url) throws IOException {
		HttpClient httpclient = new DefaultHttpClient();
		HttpResponse response = httpclient.execute(new HttpGet(url));
		StatusLine statusLine = response.getStatusLine();
		if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			response.getEntity().writeTo(out);
			out.close();
			return out.toString();
			// ..more logic
		}
		return null;
	}

	private void addBaiduVideoPlayer(String[] paths) {
		addBaiduVideoPlayer(Arrays.asList(paths));
	}

	private void addBaiduVideoPlayer(List<String> paths) {
		System.out.println(paths);
		BVideoView.setAKSK(AK, SK);
		for (String path : paths) {
			final BVideoView bvv = new BVideoView(this);
			bvv.showCacheInfo(true);
			bvv.setVideoPath(path);
			LL.addView(bvv);
			bvvs.add(bvv);
			new Thread() {
				public void run() {
					bvv.start();
				}
			}.start();
		}
	}

	private void addVideoPlayer(String[] paths) {
		for (String path : paths) {
			final VideoView vv = new VideoView(this);
			vv.setVideoPath(path);
			LL.addView(vv);
			vvs.add(vv);
			new Thread() {
				public void run() {
					vv.start();
				}
			}.start();
		}
	}
}
