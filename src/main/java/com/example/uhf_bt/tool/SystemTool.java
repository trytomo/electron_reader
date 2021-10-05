package com.example.uhf_bt.tool;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

public class SystemTool {
	private final static String TAG = "SystemTool";

	public static boolean setSysDate(int year, int month, int day) {
		Calendar cal = Calendar.getInstance();
		cal.set(year, month, day);
		long when = cal.getTimeInMillis();
		if(when / 1000 < Integer.MAX_VALUE) {
			return SystemClock.setCurrentTimeMillis(when);
		}
		return false;
	}

	public static boolean setSysTime(int hourOfDay, int minute, int second, Context context) {
		ContentResolver resolver = context.getContentResolver();
		android.provider.Settings.System
				.putString(resolver, android.provider.Settings.System.TIME_12_24, "24");
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
		cal.set(Calendar.MINUTE, minute);
		cal.set(Calendar.SECOND, second);
		cal.set(Calendar.MILLISECOND, 0);
		long when = cal.getTimeInMillis();
		if(when / 1000 < Integer.MAX_VALUE) {
			return SystemClock.setCurrentTimeMillis(when);
		}
		return false;
	}

	public static int getScreenWidth(Context context) {
		DisplayMetrics dm = new DisplayMetrics();
		getDefaultDisplay(context).getMetrics(dm);
		return dm.widthPixels;
	}

	public static int getScreenHeight(Context context) {
		DisplayMetrics dm = new DisplayMetrics();
		getDefaultDisplay(context).getMetrics(dm);
		return dm.heightPixels;
	}

	public static int getRealScreenWidth(Context context) {
		DisplayMetrics realMetrics = new DisplayMetrics();
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
			getDefaultDisplay(context).getRealMetrics(realMetrics);
		} else {
			getDefaultDisplay(context).getMetrics(realMetrics);
		}
		return realMetrics.widthPixels;
	}

	public static int getRealScreenHeight(Context context) {
		DisplayMetrics realMetrics = new DisplayMetrics();
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
			getDefaultDisplay(context).getRealMetrics(realMetrics);
		} else {
			getDefaultDisplay(context).getMetrics(realMetrics);
		}
		return realMetrics.heightPixels;
	}

	public static int getStatusBarHeight(Context context) {
		int height = 0;
		int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			height = context.getResources().getDimensionPixelSize(resourceId);
		}
		return height;
	}

	public static void setStatusBarColor(Activity activity, int color, boolean isDarkText) {
		if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
			if(isDarkText) {
				setStatusDarkText(activity);
			}
			activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			activity.getWindow().setStatusBarColor(ContextCompat.getColor(activity, color));
		}
	}

	public static void setStatusDarkText(Activity activity) {
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
		}
	}

	public static void resetStatusText(Activity activity) {
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
		}
	}

	public static void setNavigationBarColor(Activity activity, int color) {
		if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
			activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
			activity.getWindow().setNavigationBarColor(ContextCompat.getColor(activity, color));
		}
	}

	private static void hideNavigationBar(Activity activity) {
		hideNavigationBar(activity, false, false);
	}

	private static void hideNavigationBar(Activity activity, boolean fullscreen, boolean lithtStatusBar) {
		View decorView = activity.getWindow().getDecorView();
		int flags = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
				| View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
		if(fullscreen) {
			flags = flags | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_FULLSCREEN;
		}
		if(lithtStatusBar) {
			flags = flags | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
		}
		decorView.setSystemUiVisibility(flags);
	}

	public static boolean hasNavigationBar(Context context) {
		int screenRealWidth = getRealScreenWidth(context);
		int screenRealHeight = getRealScreenHeight(context);

		int screenWidth = getScreenWidth(context);
		int screenHeight = getScreenHeight(context);
		return screenRealWidth > screenWidth || screenRealHeight > screenHeight;
	}

	public static int getNavigationBarHeight(Context context) {
		int result = 0;
		if (hasNavigationBar(context)) {
			Resources res = context.getResources();
			int resourceId = res.getIdentifier("navigation_bar_height", "dimen", "android");
			if (resourceId > 0) {
				result = res.getDimensionPixelSize(resourceId);
			}
		}
		return result;
	}

	public static int getNavigationBarLacation(Context context) {
		int screenRealWidth = getRealScreenWidth(context);
		int screenWidth = getScreenWidth(context);
		if (screenRealWidth != screenWidth) {
			return 1;
		} else {
			return 0;
		}
	}

	private static String getNavigationBarOverride() {
		String sNavBarOverride = null;
		try {
			Class c = Class.forName("android.os.SystemProperties");
			Method m = c.getDeclaredMethod("get", String.class);
			m.setAccessible(true);
			sNavBarOverride = (String) m.invoke(null, "qemu.hw.mainkeys");
		} catch (Throwable e) {
		}
		return sNavBarOverride;
	}

	private static Display getDefaultDisplay(Context context) {
		WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		return manager.getDefaultDisplay();
	}

	public static void showSoftInput(Context context, View view) {
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (view != null) {
			imm.showSoftInput(view, InputMethodManager.RESULT_UNCHANGED_SHOWN);
		}
	}

	public static void showSoftInput(final Context context, final View view, long delay) {
		if(delay > 0) {
			TimerTask task = new TimerTask() {
				@Override
				public void run() {
					showSoftInput(context, view);
				}
			};
			new Timer().schedule(task, delay);
		} else {
			showSoftInput(context, view);
		}
	}

	public static Boolean hideInputMethod(Context context, View v) {
		InputMethodManager imm = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm != null && v != null) {
			return imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
		}
		return false;
	}

	public static int dp2px(Context context, float dpVal) {
		float scale = context.getResources().getDisplayMetrics().density;
		return (int) (scale * dpVal + 0.5f);
	}

	public static int px2dp(Context context, float pxVal) {
		float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxVal / scale + 0.5f);
	}

	public static void installApp(Context context,Uri contentUri) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
		context.startActivity(intent);
	}

	public static void installApp(Context context, String apkFileName) {
		installApp(context, new File(apkFileName));
	}

	public static void installApp(Context context, File apkFile) {
		installApp(context, Uri.fromFile(apkFile));
	}

	public static void installAppForMoreSdk24(Context context, String authority, File apkFile) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		Uri contentUri = FileProvider.getUriForFile(context, authority, apkFile);
		intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
		context.startActivity(intent);
	}

	public static class InstallAppBroadcast extends BroadcastReceiver {
		public final static String ADD = "add";
		public final static String REMOVE = "add";
		public final static String RESTART = "add";
		public final static String CHANGED = "add";
		public final static String REPLACE = "add";
		private OnReveiceListener onReviceListener;
		public InstallAppBroadcast(OnReveiceListener onReviceListener) {
			this.onReviceListener = onReviceListener;
		}
		@Override
		public void onReceive(Context context, Intent intent) {
			if(onReviceListener != null) {
				if (Intent.ACTION_PACKAGE_ADDED.equals(intent.getAction())) {
					onReviceListener.onReceive(context, ADD);
				} else if (Intent.ACTION_PACKAGE_REMOVED.equals(intent.getAction())) {
					onReviceListener.onReceive(context, REMOVE);
				} else if (Intent.ACTION_PACKAGE_CHANGED.equals(intent.getAction())) {
					onReviceListener.onReceive(context, CHANGED);
				} else if (Intent.ACTION_PACKAGE_REPLACED.equals(intent.getAction())) {
					onReviceListener.onReceive(context, REPLACE);
				} else if (Intent.ACTION_PACKAGE_RESTARTED.equals(intent.getAction())) {
					onReviceListener.onReceive(context, RESTART);
				}
			}
		}

		public interface OnReveiceListener {
			void onReceive(Context context, String action);
		}
	}

	public static String getIMEI(Context context) {
		String imei = "";
		if(ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
			TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			imei = tm.getDeviceId();
			return TextUtils.isEmpty(imei) ? "0" : imei;
		}
		return imei;
	}

	public static String getVersionName(Context context) {
		return getPackageInfo(context).versionName;
	}

	public static int getVersionCode(Context context) {
		return getPackageInfo(context).versionCode;
	}

	private static PackageInfo getPackageInfo(Context context) {
		PackageInfo info = null;
		try {
			info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}

		if(info == null) {
			info = new PackageInfo();
		}
		return info;
	}

	private static PackageInfo getProviderPackageInfo(Context context) {
		PackageInfo info = null;
		try {
			info = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_PROVIDERS);
		} catch (PackageManager.NameNotFoundException e) {
			Log.e(TAG, "package not found");
			e.printStackTrace();
		}
		return info;
	}
}
