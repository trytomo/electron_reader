package com.example.uhf_bt;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;
import android.view.View;

import com.rscja.deviceapi.DeviceConfiguration;
import com.rscja.utility.StringUtility;

import java.util.HashMap;

import static android.content.Context.AUDIO_SERVICE;

public class Utils {
    private static final String TAG = "Utils";

    private static HashMap<Integer, Integer> soundMap = new HashMap<Integer, Integer>();
    private static SoundPool soundPool;
    private static float volumnRatio;
    private static AudioManager am;

    public static void initSound(Context context) {
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 5);
        soundMap.put(1, soundPool.load(context, R.raw.barcodebeep, 1));
        soundMap.put(2, soundPool.load(context, R.raw.serror, 1));
        am = (AudioManager) context.getSystemService(AUDIO_SERVICE);
    }

    public static void freeSound() {
        if (soundPool != null)
            soundPool.release();
        soundPool = null;
    }

    public static void playSound(int id) {

        float audioMaxVolumn = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float audioCurrentVolumn = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        volumnRatio = audioCurrentVolumn / audioMaxVolumn;
        try {
            soundPool.play(soundMap.get(id), volumnRatio,
                    volumnRatio,
                    1,
                    0,
                    1
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void alert(Activity act, int titleInt, String message, int iconInt) {
        alert(act, titleInt, message, iconInt, null);
    }

    public static void alert(Activity act, int titleInt, String message, int iconInt, DialogInterface.OnClickListener positiveListener) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(act);
            builder.setTitle(titleInt);
            builder.setMessage(message);
            builder.setIcon(iconInt);

            builder.setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            if (positiveListener != null) {
                builder.setPositiveButton(R.string.ok, positiveListener);
            }
            builder.create().show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void alert(Activity act, String title, View view, int iconInt, DialogInterface.OnClickListener positiveListener) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(act);
            builder.setTitle(title);
            builder.setView(view);
            builder.setIcon(iconInt);

            builder.setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            if (positiveListener != null) {
                builder.setPositiveButton(R.string.ok, positiveListener);
            }
            builder.create().show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void alert(Activity act, int titleInt, View view, int iconInt, DialogInterface.OnClickListener positiveListener) {
        alert(act, act.getString(titleInt), view, iconInt, positiveListener);
    }

    public static int toInt(String str, int defValue) {
        try {
            return Integer.parseInt(str);
        } catch (Exception e) {
        }
        return defValue;
    }

    public static boolean vailHexInput(String str) {

        if (str == null || str.length() == 0) {
            return false;
        }
        if (str.length() % 2 == 0) {
            return StringUtility.isHexNumberRex(str);
        }

        return false;
    }

    public static boolean isLandscape() {
        String model = DeviceConfiguration.getModel();
        Log.e(TAG, "model=" + model);
        return model.contains("P80")
                || model.contains("A8")
                || model.equals("CWJ600");
    }
}
