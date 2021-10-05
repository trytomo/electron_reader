package com.example.uhf_bt.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.example.uhf_bt.R;
import com.example.uhf_bt.tool.SystemTool;

import androidx.annotation.DrawableRes;

public class BaseDialog {
    protected static final int NORMAL = 0;
    protected static final int BOTTOM = 1;
    protected static final int TOP = 2;

    protected Dialog mDialog;
    protected Window mDialogWindow;
    protected WindowManager.LayoutParams mDialogLp;
    protected Context mContext;
    protected View mContentView;
    protected boolean mCancelTouchOutside;
    protected boolean mCancelable;
    protected boolean mShowSoftInputMethod;

    protected int mWidth, mHeight;
    protected int minWidth, maxHeight;
    public BaseDialog(Context context, boolean cancelable) {
        this(context, cancelable, false);
    }

    public BaseDialog(Context context, boolean cancelable, boolean showSoftInputMethod) {
        this.mContext = context;
        this.mCancelable = cancelable;
        this.mCancelTouchOutside = true;
        this.mShowSoftInputMethod = showSoftInputMethod;
        init();
    }

    private void init() {
        minWidth = (int) (SystemTool.getScreenWidth(mContext) * 0.5f);
        maxHeight = (int) (SystemTool.getScreenHeight(mContext) * 0.95f);
        if(mShowSoftInputMethod) {
            mDialog = new Dialog(mContext, R.style.UsualLib_ShowSoftInputModeDialogStyle);
        } else {
            mDialog = new Dialog(mContext, R.style.UsualLib_NormalDialogStyle);
        }
        mDialog.setCanceledOnTouchOutside(mCancelTouchOutside);
        mDialog.setCancelable(mCancelable);
        mDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if(onKeyListener != null) {
                    return onKeyListener.onKey(dialog, keyCode, event);
                }
                return false;
            }
        });
        mDialogWindow = mDialog.getWindow();
        mDialogWindow.setBackgroundDrawableResource(R.drawable.usuallib_bg_normal_dialog);
        mDialogLp = mDialogWindow.getAttributes();
    }

    public <T extends BaseDialog> T setWindowBackground(@DrawableRes int resource) {
        mDialogWindow.setBackgroundDrawableResource(resource);
        return (T) this;
    }

    public <T extends BaseDialog> T setWindowBackground(Drawable drawable) {
        mDialogWindow.setBackgroundDrawable(drawable);
        return (T) this;
    }

    public boolean isShowing() {
        return mDialog.isShowing();
    }

    public void showNormalDialog() {
        showDialog(NORMAL, 0);
    }

    public void showBottomDialog() {
        showDialog(BOTTOM, 0);
    }

    public void showBottomDialog(int bottomOffset) {
        showDialog(BOTTOM, bottomOffset);
    }

    public void showTopDialog() {
        showDialog(TOP, 0);
    }

    public void showTopDialog(int topOffset) {
        showDialog(TOP, topOffset);
    }

    protected void showDialog(int location, int offset) {
        int style = -1;
        int gravity = -1;
        if(location == NORMAL) {
            mDialogLp.width = WindowManager.LayoutParams.WRAP_CONTENT;
            mDialogLp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        } else {
            if (location == BOTTOM) {
                style = R.style.UsualLib_ActionSheetDialogAnim;
                gravity = Gravity.BOTTOM;
            } else if (location == TOP) {
                style = R.style.UsualLib_TopDialogAnim;
                gravity = Gravity.TOP;
            }
            if(mWidth < SystemTool.getScreenWidth(mContext)) {
                mDialogWindow.setBackgroundDrawableResource(R.color.white);
            }
            mDialogWindow.setWindowAnimations(style);
            mDialogWindow.setGravity(gravity);
            mDialogLp.y = offset;
            mDialogLp.width = WindowManager.LayoutParams.MATCH_PARENT;
            mDialogLp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        }

        if(mWidth > 0) {
            mDialogLp.width = mWidth;
        } else {
            mContentView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                    int contentWidth = mContentView.getWidth();
                    if(contentWidth < minWidth) {
                        contentWidth = minWidth;
                        mDialogLp.width = minWidth;
                        mDialogWindow.setAttributes(mDialogLp);
                    }
                    view.setLayoutParams(new FrameLayout.LayoutParams(contentWidth, FrameLayout.LayoutParams.WRAP_CONTENT));
                    mContentView.removeOnLayoutChangeListener(this);
                }
            });
        }
        if(mHeight > 0) {
            mDialogLp.height = mHeight;
        } else {
            mContentView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                    int contentHeight = mContentView.getHeight();
                    if (contentHeight > maxHeight) {
                        view.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, maxHeight));
                        mDialogLp.height = maxHeight;
                        mDialogWindow.setAttributes(mDialogLp);
                    }
                    mContentView.removeOnLayoutChangeListener(this);
                }
            });
        }
        mDialogWindow.setAttributes(mDialogLp);

        if(mDialog.isShowing()) {
           mDialog.dismiss();
        }
        mDialog.show();
    }

    public void dismiss() {
        if(mDialog != null && mDialog.isShowing()) {
            SystemTool.hideInputMethod(mContext, mDialog.getCurrentFocus());
            mDialog.dismiss();
        }
    }

    public View getCurrFocus() {
        if(mDialog != null) {
            return mDialog.getCurrentFocus();
        }
        return null;
    }

    public View getContentView() {
        return this.mContentView;
    }

    public <T extends BaseDialog> T setContentView(View contentView) {
        this.mContentView = contentView;
        mDialog.setContentView(contentView);
        return (T) this;
    }

    public <T extends BaseDialog> T setCancelable(boolean cancelable) {
        this.mCancelable = cancelable;
        mDialog.setCancelable(cancelable);
        return (T) this;
    }

    public <T extends BaseDialog> T setCanceledOnTouchOutside(boolean cancel) {
        this.mCancelTouchOutside = cancel;
        mDialog.setCanceledOnTouchOutside(cancel);
        return (T) this;
    }

    public Context getContext() {
        return mContext;
    }

    public int getWidth() {
        return mWidth;
    }

    public <T extends BaseDialog> T setWidth(int width) {
        this.mWidth = width;
        return (T) this;
    }

    public int getHeight() {
        return mHeight;
    }

    public <T extends BaseDialog> T setHeight(int height) {
        this.mHeight = height;
        return (T) this;
    }

    public int getMinWidth() {
        return minWidth;
    }

    public <T extends BaseDialog> T setMinWidth(int minWidth) {
        this.minWidth = minWidth;
        return (T) this;
    }

    public int getMaxHeight() {
        return maxHeight;
    }

    public <T extends BaseDialog> T setMaxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
        return (T) this;
    }

    protected <T extends View> T getView(View parent, int id) {
        return (T) parent.findViewById(id);
    }

    private OnKeyListener onKeyListener;

    public OnKeyListener getOnKeyListener() {
        return onKeyListener;
    }

    public void setOnKeyListener(OnKeyListener onKeyListener) {
        this.onKeyListener = onKeyListener;
    }

    public interface OnKeyListener {
        boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event);
    }

    public int dp2px(int dp) {
        return SystemTool.dp2px(mContext, dp);
    }
}
