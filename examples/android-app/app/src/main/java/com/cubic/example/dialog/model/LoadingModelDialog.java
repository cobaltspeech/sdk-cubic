package com.cubic.example.dialog.model;

import android.app.Dialog;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.cubic.example.R;

public class LoadingModelDialog extends BaseDialogFragment {

    public static final String STATE = "STATE";

    @Override
    protected int getLayoutRes() {
        return R.layout.dialog_loading;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = getDialogBuilder();
        builder.setCancelable(false);
        setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();
        Bundle arguments = getArguments();
        String string = arguments.getString(STATE);
        TextView tv = dialog.findViewById(R.id.messageView);
        tv.setText(string);
        return dialog;
    }


}