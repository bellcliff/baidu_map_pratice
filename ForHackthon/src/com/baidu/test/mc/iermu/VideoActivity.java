package com.baidu.test.mc.iermu;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.Process;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.baidu.cyberplayer.core.BMediaController;
import com.baidu.cyberplayer.core.BVideoView;
import com.baidu.cyberplayer.core.BVideoView.OnCompletionListener;
import com.baidu.cyberplayer.core.BVideoView.OnErrorListener;
import com.baidu.cyberplayer.core.BVideoView.OnInfoListener;
import com.baidu.cyberplayer.core.BVideoView.OnPlayingBufferCacheListener;
import com.baidu.cyberplayer.core.BVideoView.OnPreparedListener;

public class VideoActivity extends Activity implements OnPreparedListener,
		OnCompletionListener, OnErrorListener, OnInfoListener,
		OnPlayingBufferCacheListener {
	private final String TAG = "VideoViewPlayingActivity";

	/**
	 * ����ak
	 */
	// private String AK = "";
	/**
	 * //����sk��ǰ16λ
	 */
	// private String SK = "";

	private String mVideoSource = null;

	private BVideoView mVV = null;
	private BMediaController mVVCtl = null;
	private RelativeLayout mViewHolder = null;
	private LinearLayout mControllerHolder = null;

	// private boolean mIsHwDecode = false;

	private EventHandler mEventHandler;
	private HandlerThread mHandlerThread;

	private final Object SYNC_Playing = new Object();

	private final int EVENT_PLAY = 0;

	private WakeLock mWakeLock = null;
	private static final String POWER_LOCK = "VideoViewPlayingActivity";
	private static String AK = "cuqt8mBeWp6OMaLUmlC5u02E";
	private static String SK = "1kBXnL5ccPpAWZzUoCglcyr1XG9yd0lD";

	/**
	 * ����״̬
	 */
	private enum PLAYER_STATUS {
		PLAYER_IDLE, PLAYER_PREPARING, PLAYER_PREPARED,
	}

	private PLAYER_STATUS mPlayerStatus = PLAYER_STATUS.PLAYER_IDLE;

	/**
	 * ��¼����λ��
	 */
	private int mLastPos = 0;

	class EventHandler extends Handler {
		public EventHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case EVENT_PLAY:
				/**
				 * ����Ѿ������ˣ��ȴ���һ�β��Ž���
				 */
				if (mPlayerStatus != PLAYER_STATUS.PLAYER_IDLE) {
					synchronized (SYNC_Playing) {
						try {
							SYNC_Playing.wait();
							Log.v(TAG, "wait player status to idle");
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}

				/**
				 * ���ò���url
				 */
				mVV.setVideoPath(mVideoSource);

				/**
				 * �����������Ҫ���
				 */
				if (mLastPos > 0) {

					mVV.seekTo(mLastPos);
					mLastPos = 0;
				}

				/**
				 * ��ʾ�������ػ�����ʾ
				 */
				mVV.showCacheInfo(true);

				/**
				 * ��ʼ����
				 */
				mVV.start();

				mPlayerStatus = PLAYER_STATUS.PLAYER_PREPARING;
				break;
			default:
				break;
			}
		}
	}

	/**
	 * ʵ���л�ʾ��
	 */
	private View.OnClickListener mPreListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Log.v(TAG, "pre btn clicked");
			/**
			 * ����Ѿ��������ţ���ֹͣ����
			 */
			if (mPlayerStatus != PLAYER_STATUS.PLAYER_IDLE) {
				mVV.stopPlayback();
			}

			/**
			 * ����һ���µĲ�������
			 */
			if (mEventHandler.hasMessages(EVENT_PLAY))
				mEventHandler.removeMessages(EVENT_PLAY);
			mEventHandler.sendEmptyMessage(EVENT_PLAY);
		}
	};

	private View.OnClickListener mNextListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Log.v(TAG, "next btn clicked");
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.controllerplaying);

		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		mWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK
				| PowerManager.ON_AFTER_RELEASE, POWER_LOCK);

		// mIsHwDecode = getIntent().getBooleanExtra("isHW", false);
		Uri uriPath = getIntent().getData();
		if (null != uriPath) {
			String scheme = uriPath.getScheme();
			if (null != scheme) {
				mVideoSource = uriPath.toString();
			} else {
				mVideoSource = uriPath.getPath();
			}
		}

		initUI();

		/**
		 * ������̨�¼������߳�
		 */
		mHandlerThread = new HandlerThread("event handler thread",
				Process.THREAD_PRIORITY_BACKGROUND);
		mHandlerThread.start();
		mEventHandler = new EventHandler(mHandlerThread.getLooper());
	}

	/**
	 * ��ʼ������
	 */
	private void initUI() {
		mViewHolder = (RelativeLayout) findViewById(R.id.view_holder);
		mControllerHolder = (LinearLayout) findViewById(R.id.controller_holder);

		/**
		 * ����ak��sk��ǰ16λ
		 */
		BVideoView.setAKSK(AK, SK);

		/**
		 * ����BVideoView��BMediaController
		 */
		mVV = new BVideoView(this);
		mVVCtl = new BMediaController(this);
		mViewHolder.addView(mVV);
		mControllerHolder.addView(mVVCtl);

		/**
		 * ע��listener
		 */
		mVV.setOnPreparedListener(this);
		mVV.setOnCompletionListener(this);
		mVV.setOnErrorListener(this);
		mVV.setOnInfoListener(this);
		mVVCtl.setPreNextListener(mPreListener, mNextListener);

		/**
		 * ����BMediaController
		 */
		mVV.setMediaController(mVVCtl);
		/**
		 * ���ý���ģʽ
		 */
		mVV.setDecodeMode(BVideoView.DECODE_SW);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.v(TAG, "onPause");
		/**
		 * ��ֹͣ����ǰ ������ȼ�¼��ǰ���ŵ�λ��,�Ա��Ժ��������
		 */
		if (mPlayerStatus == PLAYER_STATUS.PLAYER_PREPARED) {
			mLastPos = mVV.getCurrentPosition();
			mVV.stopPlayback();
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.v(TAG, "onResume");
		if (null != mWakeLock && (!mWakeLock.isHeld())) {
			mWakeLock.acquire();
		}
		/**
		 * ����һ�β�������,��Ȼ����һ��Ҫ���ⷢ��
		 */
		mEventHandler.sendEmptyMessage(EVENT_PLAY);
	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.v(TAG, "onStop");
		if (null != mWakeLock && mWakeLock.isHeld())
			mWakeLock.release();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		/**
		 * ������̨�¼������߳�
		 */
		mHandlerThread.quit();
		if (null != mWakeLock && mWakeLock.isHeld())
			mWakeLock.release();
		Log.v(TAG, "onDestroy");
	}

	@Override
	public boolean onInfo(int what, int extra) {
		// TODO Auto-generated method stub
		switch (what) {
		/**
		 * ��ʼ����
		 */
		case BVideoView.MEDIA_INFO_BUFFERING_START:
			break;
		/**
		 * ��������
		 */
		case BVideoView.MEDIA_INFO_BUFFERING_END:
			break;
		default:
			break;
		}
		return false;
	}

	/**
	 * ��ǰ����İٷֱȣ� �������onInfo�еĿ�ʼ����ͽ�����������ʾ�ٷֱȵ�����
	 */
	@Override
	public void onPlayingBufferCache(int percent) {
		// TODO Auto-generated method stub

	}

	/**
	 * ���ų���
	 */
	@Override
	public boolean onError(int what, int extra) {
		// TODO Auto-generated method stub
		Log.v(TAG, "onError");
		synchronized (SYNC_Playing) {
			SYNC_Playing.notify();
		}
		mPlayerStatus = PLAYER_STATUS.PLAYER_IDLE;
		return true;
	}

	/**
	 * �������
	 */
	@Override
	public void onCompletion() {
		// TODO Auto-generated method stub
		Log.v(TAG, "onCompletion");

		synchronized (SYNC_Playing) {
			SYNC_Playing.notify();
		}
		mPlayerStatus = PLAYER_STATUS.PLAYER_IDLE;
	}

	/**
	 * ����׼������
	 */
	@Override
	public void onPrepared() {
		// TODO Auto-generated method stub
		Log.v(TAG, "onPrepared");
		mPlayerStatus = PLAYER_STATUS.PLAYER_PREPARED;
	}
}
