package com.cubic.example.ui.local;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.cubic.example.R;
import com.cubic.example.dialog.model.LoadingModelDialog;
import com.cubic.example.dialog.model.SecureConnectionDialog;
import com.cubic.example.dialog.DialogManager;
import com.cubic.example.dialog.IDialogManager;
import com.cubic.example.dialog.model.ConnectionDialog;
import com.cubic.example.dialog.model.NetworkDialog;
import com.cubic.example.message.IMessageManager;
import com.cubic.example.message.ToastManager;

import com.cubic.sdk.ICubicManager;
import com.cubic.sdk.LocalCubicManager;
import com.cubic.sdk.OnLocalCubicChangeListene;
import com.cubic.sdk.exception.AudioPermissionException;
import com.cubic.sdk.exception.ChannelShutdownException;
import com.cubic.sdk.exception.NetworkException;
import com.cubic.sdk.exception.SecureConnectionException;
import com.cubic.sdk.model.LocalCubicAudioConfiguration;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class LocalCubicActivity extends AppCompatActivity {

    private FloatingActionButton mRecognitionButtonView;
    private TextView mResultView;
    private ArrayList<String> mModels = new ArrayList<>();

    private IMessageManager mIToastManager;
    private IDialogManager mIDialogManager;
    private ICubicManager<LocalCubicAudioConfiguration> mICubicManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_main);
        mIToastManager = new ToastManager(this);
        mIDialogManager = new DialogManager(getSupportFragmentManager());
        initCubicManager();
        setupUI();
    }

    private void setupUI() {
        mResultView = this.findViewById(R.id.textViewResults);
        this.findViewById(R.id.clearButtonView).setOnClickListener(view -> mResultView.setText(""));
        this.findViewById(R.id.settingsButtonView).setOnClickListener(view -> {
            Intent intent = new Intent(this, LocalSettingsActivity.class);
            startActivity(intent);
        });
        initTalkButton();
        initAudioModelsView();
    }

    private void initAudioModelsView() {
        AppCompatSpinner appCompatSpinner = findViewById(R.id.modelView);
        ArrayAdapter modelViewAdapter = new ArrayAdapter<>(
                getApplicationContext(),
                R.layout.item_model_view,
                mModels);
        appCompatSpinner.setAdapter(modelViewAdapter);
        appCompatSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                if (pos > 0) {
                    List<LocalCubicAudioConfiguration> configurations = mICubicManager.getAudioConfigurations();
                    LocalCubicAudioConfiguration configuration = configurations.get(pos - 1);
                    mICubicManager.setAudioConfiguration(configuration);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        mModels.add("Select model");
        List<LocalCubicAudioConfiguration> configs = mICubicManager.getAudioConfigurations();
        for (LocalCubicAudioConfiguration config : configs) {
            mModels.add(config.getName());
        }
        modelViewAdapter.notifyDataSetChanged();
    }

    private void initCubicManager() {
        mICubicManager = new LocalCubicManager(getApplicationContext(),
                new OnLocalCubicChangeListene() {
                    @Override
                    public void onConnect() {
                        mIDialogManager.hideDialog(LoadingModelDialog.class);
                        mIDialogManager.hideDialog(ConnectionDialog.class);
                        enableControlls();
                    }

                    @Override
                    public void onConnecting() {
                        mIDialogManager.hideDialog(LoadingModelDialog.class);
                        mIDialogManager.showDialog(ConnectionDialog.class);
                        disableControlls();
                    }

                    @Override
                    public void onDisconnect() {
                        mIDialogManager.hideDialog(LoadingModelDialog.class);
                        mIDialogManager.hideDialog(ConnectionDialog.class);
                        disableControlls();
                    }

                    @Override
                    public void onLoadingModel(@NonNull String state) {
                        Bundle bundle = new Bundle();
                        bundle.putString(LoadingModelDialog.STATE, state);
                        mIDialogManager.showDialog(LoadingModelDialog.class, bundle);
                    }

                    @Override
                    public void onGetAudioConfigurations(@NonNull List<LocalCubicAudioConfiguration> configurations) {
                    }

                    @Override
                    public void onText(@NonNull String text) {
                        mResultView.append("\n" + text);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        mIDialogManager.hideDialog(LoadingModelDialog.class);
                        mIDialogManager.hideDialog(ConnectionDialog.class);
                        disableControlls();
                        if (e instanceof ChannelShutdownException) {
                            return;
                        }
                        if (e instanceof SecureConnectionException) {
                            mIDialogManager.showDialog(SecureConnectionDialog.class);
                        } else if (e instanceof NetworkException) {
                            mIDialogManager.showDialog(NetworkDialog.class);
                        } else if (e instanceof AudioPermissionException) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                if (!shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO)) {
                                    mIToastManager.showMessage(getString(R.string.app_error_permission));
                                } else {
                                    requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 1);
                                }
                            } else
                                mIToastManager.showMessage(getString(R.string.app_error_permission));
                        } else mIToastManager.showMessage("Some exception " + e.toString());
                    }
                });
    }

    private void initTalkButton() {
        mRecognitionButtonView = this.findViewById(R.id.buttonPushToTalk);
        mRecognitionButtonView.setEnabled(false);
        mRecognitionButtonView.setImageResource(R.drawable.ic_mic_off);
        mRecognitionButtonView.setOnTouchListener((view, motionEvent) -> {
            int action = motionEvent.getAction();
            if (action == MotionEvent.ACTION_UP) {
                mRecognitionButtonView.setImageResource(R.drawable.ic_mic_off);
                mICubicManager.stopTalk();
                return true;
            }
            if (action == MotionEvent.ACTION_DOWN) {
                mRecognitionButtonView.setImageResource(R.drawable.ic_mic);
                mICubicManager.talk();
                return true;
            }
            return false;
        });
    }

    private void enableControlls() {
        mRecognitionButtonView.setEnabled(true);
    }

    private void disableControlls() {
        mRecognitionButtonView.setEnabled(false);
        mRecognitionButtonView.setImageResource(R.drawable.ic_mic_off);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mICubicManager.disconnect();
    }
}