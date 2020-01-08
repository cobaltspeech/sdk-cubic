package com.cubic.example.dialog.model;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.cubic.sdk.model.CubicAudioConfiguration;
import com.cubic.example.MainActivity;
import com.cubic.example.R;

import java.util.ArrayList;
import java.util.List;

public class SettingsDialog extends BaseDialogFragment {

    @Override
    protected int getLayoutRes() {
        return R.layout.dialog_settings;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        setTitleText(getString(R.string.app_settings_dialog_title));
        AlertDialog.Builder builder = getDialogBuilder();

        List<CubicAudioConfiguration> list = (ArrayList) getArguments().getParcelableArrayList(Args.CONFIGS);
        if (list != null) {
            String[] array = new String[list.size()];
            for (int i = 0; i < list.size(); i++) {
                array[i] = list.get(i).getName();
            }
            builder.setItems(array, (dialog, item) -> {
                MainActivity activity = (MainActivity) requireActivity();
                activity.onSelectAudioConfig(list.get(item));
            });
        }
        AlertDialog dialog = builder.create();
        dialog.show();
        return dialog;
    }

    @Override
    public void onPositiveClickButton() {
        super.onPositiveClickButton();
        startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
    }

    public interface Args {
        String CONFIGS = "CONFIGS";
    }
}