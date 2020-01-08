package com.cubic.example.toast;

import androidx.annotation.NonNull;

public interface IToastManager {

    void showShortToast(@NonNull TypeMessage type, @NonNull String message);

    void showLongToast(@NonNull TypeMessage type, @NonNull String message);

    void cancelToast();
}
