package com.cubic.example.ui.remote;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import com.cubic.example.R;
import com.cubic.sdk.model.ConnectionConfiguration;
import com.cubic.sdk.preference.connect.IConnectPreferenceManager;
import com.cubic.sdk.preference.connect.local.LocalConnectPreferenceOptions;
import com.cubic.sdk.preference.connect.remote.RemoteConnecPreferenceManager;

public class RemoteSettingsActivity extends AppCompatActivity {

    private IConnectPreferenceManager mIRemoteConnecPreferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_settings);

        mIRemoteConnecPreferenceManager = new RemoteConnecPreferenceManager(getApplicationContext());
        setupUI();
    }

    private void setupUI() {
        initHostView();
        initSecureView();
    }

    private void initSecureView() {
        Switch switchView = this.findViewById(R.id.secureView);
        ConnectionConfiguration connectionConfiguration = mIRemoteConnecPreferenceManager.getConnectionConfiguration();
        switchView.setChecked(connectionConfiguration.isSecure());
        switchView.setOnCheckedChangeListener((compoundButton, b) -> mIRemoteConnecPreferenceManager.saveSecure(b));
    }

    private void initHostView() {
        EditText hostView = this.findViewById(R.id.hostView);
        ConnectionConfiguration connectionConfiguration = mIRemoteConnecPreferenceManager.getConnectionConfiguration();
        hostView.setText(connectionConfiguration.getHost());
        hostView.setHint(LocalConnectPreferenceOptions.Defaults.HOST);
        hostView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mIRemoteConnecPreferenceManager.saveHost(s.toString());
            }
        });
    }
}
