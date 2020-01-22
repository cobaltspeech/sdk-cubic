package com.cubic.example;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.TextView;

import com.cubic.sdk.CubicManager;
import com.cubic.sdk.ICubicManager;
import com.cubic.sdk.OnCubicChangeListener;
import com.cubic.sdk.exception.AudioPermissionException;
import com.cubic.sdk.exception.ChannelShutdownException;
import com.cubic.sdk.exception.NetworkException;
import com.cubic.sdk.model.CubicAudioConfiguration;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.cubic.example.dialog.DialogManager;
import com.cubic.example.dialog.IDialogManager;
import com.cubic.example.dialog.model.ConnectionDialog;
import com.cubic.example.dialog.model.NetworkDialog;
import com.cubic.example.dialog.model.SettingsDialog;
import com.cubic.example.toast.IMessageManager;
import com.cubic.example.toast.ToastManager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton mRecognitionButtonView;
    private TextView mResultView;

    private IMessageManager mIToastManager;
    private IDialogManager mIDialogManager;
    private ICubicManager mICubicManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_main);
        mIToastManager = new ToastManager(this);
        mIDialogManager = new DialogManager(getSupportFragmentManager());
        initCubicManager();
        setupUI();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mIDialogManager.onDestroy();
    }

    private void setupUI() {
        mResultView = this.findViewById(R.id.textViewResults);
        this.findViewById(R.id.clearButtonView).setOnClickListener(view -> mResultView.setText(""));
        this.findViewById(R.id.settingsButtonView).setOnClickListener(view -> {
            List<CubicAudioConfiguration> list = mICubicManager.getAudioConfigurations();
            if (!list.isEmpty()) {
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList(SettingsDialog.Args.CONFIGS, (ArrayList) list);
                mIDialogManager.showDialog(SettingsDialog.class, bundle);
            }
        });
        initTalkButton();
    }

    public void onSelectAudioConfig(CubicAudioConfiguration configuration) {
        mICubicManager.setAudioConfiguration(configuration);
    }

    private void initCubicManager() {
        mICubicManager = new CubicManager(getApplicationContext(), getLifecycle(),
                new OnCubicChangeListener() {
                    @Override
                    public void onConnect() {
                        mIDialogManager.hideDialog(ConnectionDialog.class);
                        enableTalkButton();
                    }

                    @Override
                    public void onConnecting() {
                        mIDialogManager.showDialog(ConnectionDialog.class);
                        blockTalkButton();
                    }

                    @Override
                    public void onDisconnect() {
                        mIDialogManager.hideDialog(ConnectionDialog.class);
                        blockTalkButton();
                    }

                    @Override
                    public void onGetAudioConfigurations(@NonNull List<CubicAudioConfiguration> audioConfigurations) {
                    }

                    @Override
                    public void onText(@NonNull String text) {
                        mResultView.append("\n" + text);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        mIDialogManager.hideDialog(ConnectionDialog.class);
                        blockTalkButton();
                        if (e instanceof ChannelShutdownException) {
                            return;
                        }
                        if (e instanceof NetworkException) {
                            mIDialogManager.showDialog(NetworkDialog.class);
                        } else if (e instanceof AudioPermissionException) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                if (!shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO)) {
                                    mIToastManager.showMessage(getString(R.string.app_error_permission));
                                } else {
                                    requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 1);
                                }
                            } else mIToastManager.showMessage(getString(R.string.app_error_permission));
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

    private void enableTalkButton() {
        mRecognitionButtonView.setEnabled(true);
    }

    private void blockTalkButton() {
        mRecognitionButtonView.setEnabled(false);
        mRecognitionButtonView.setImageResource(R.drawable.ic_mic_off);
    }
}