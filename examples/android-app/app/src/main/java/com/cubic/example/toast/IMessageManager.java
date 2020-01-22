package com.cubic.example.toast;

import androidx.annotation.NonNull;

public interface IMessageManager {

    void showMessage(@NonNull String message);

    void cancelToast();
}
