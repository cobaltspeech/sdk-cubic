package com.cubic.example.toast;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.Toast;

import androidx.annotation.NonNull;

public final class ToastManager implements IToastManager {

    private final Context mContext;
    private Toast mToast;

    public ToastManager(@NonNull Context context) {
        mContext = context;
    }

    private Toast showNormal(@NonNull String message, int duration, Drawable drawable) {
        if (drawable != null) {
            mToast = Toast.makeText(mContext, message, duration);
        } else {
            mToast = Toast.makeText(mContext, message, duration);
        }
        mToast.show();
        return mToast;
    }

    private Toast showSuccess(@NonNull String message, int duration, boolean showIcon) {
        mToast = Toast.makeText(mContext, message, duration);
        mToast.show();
        return mToast;
    }

    private Toast showInfo(@NonNull String message, int duration, boolean showIcon) {
        mToast = Toast.makeText(mContext, message, duration);
        mToast.show();
        return mToast;
    }

    private Toast showWarning(@NonNull String message, int duration, boolean showIcon) {
        mToast = Toast.makeText(mContext, message, duration);
        mToast.show();
        return mToast;
    }

    private Toast showError(@NonNull String message, int duration, boolean showIcon) {
        mToast = Toast.makeText(mContext, message, duration);
        mToast.show();
        return mToast;
    }


    @Override
    public void showShortToast(@NonNull TypeMessage type, @NonNull String message) {
        cancelToast();

        switch (type) {
            case Normal : {
                mToast = showNormal(message, Toast.LENGTH_SHORT, null);
                break;
            }
            case Success : {
                mToast = showSuccess(message, Toast.LENGTH_SHORT, true);
                break;
            }
            case Info : {
                mToast = showInfo(message, Toast.LENGTH_SHORT, true);
                break;
            }
            case Warning : {
                mToast = showWarning(message, Toast.LENGTH_SHORT, true);
                break;
            }
            case Error : {
                mToast = showError(message, Toast.LENGTH_SHORT, true);
                break;
            }
        }
    }

    @Override
    public void showLongToast(@NonNull TypeMessage type, @NonNull String message) {
        cancelToast();

        switch (type) {
            case Normal : {
                mToast = showNormal(message, Toast.LENGTH_LONG, null);
                break;
            }
            case Success : {
                mToast = showSuccess(message, Toast.LENGTH_LONG, true);
                break;
            }
            case Info : {
                mToast = showInfo(message, Toast.LENGTH_LONG, true);
                break;
            }
            case Warning : {
                mToast = showWarning(message, Toast.LENGTH_LONG, true);
                break;
            }
            case Error : {
                mToast = showError(message, Toast.LENGTH_LONG, true);
                break;
            }
        }
    }

    @Override
    public void cancelToast() {
        if (mToast != null) {
            mToast.cancel();
        }
    }
}
