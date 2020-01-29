package com.cubic.example.dialog.model;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.cubic.example.R;

public class NetworkDialog extends BaseDialogFragment {

    @Override
    protected int getLayoutRes() {
        return R.layout.dialog_network;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        setTitleText(getString(R.string.app_network_dialog_title));
        setPositiveText(R.string.app_network_dialog_positive_button);
        AlertDialog.Builder builder = getDialogBuilder();
        setCancelable(false);
        return builder.create();
    }

    @Override
    public void onPositiveClickButton() {
        super.onPositiveClickButton();
        startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
    }
}