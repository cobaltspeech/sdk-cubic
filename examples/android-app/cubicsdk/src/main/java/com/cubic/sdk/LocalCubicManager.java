package com.cubic.sdk;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.cubic.sdk.common.Logger;
import com.cubic.sdk.preference.connect.local.LocalConnectPreferenceManager;
import com.cubic.sdk.repository.model.Model;
import com.cubic.sdk.model.LocalCubicAudioConfiguration;
import com.cubic.sdk.repository.IRepository;
import com.cubic.sdk.repository.ModelRepository;

import java.util.List;

import cobaltspeech.cubic.CubicOuterClass;

import cubicsvr.Cubicsvr;
import cubicsvr.Server;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

public final class LocalCubicManager extends BaseCubicManager<LocalCubicAudioConfiguration> {

    private final IRepository mIRepository;
    private Server mEmbeddedServer;

    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    public LocalCubicManager(@NonNull Context context,
                             @NonNull OnLocalCubicChangeListene listener) {
        super(
                context,
                new LocalConnectPreferenceManager(context),
                listener
        );
        mIRepository = new ModelRepository(context);

        for (Model model : mIRepository.getModels()) {
            mCubicAudioConfigurations.add(new LocalCubicAudioConfiguration(model));
        }
    }

    @Override
    protected void onCubicServiceStart(@NonNull CubicOuterClass.ListModelsResponse value) {
        if (!mCubicAudioConfigurations.isEmpty()) {
            List<CubicOuterClass.Model> list = value.getModelsList();
            for (CubicOuterClass.Model model : list) {
                if (TextUtils.equals(model.getName(), mCubicAudioConfiguration.getName())) {
                    mCubicAudioConfiguration.setServerAudioConfiguration(model);
                    return;
                }
            }
        }
    }

    @Override
    public void disconnect() {
        super.disconnect();
        stopEmbeddedServer();
    }

    @Override
    public void setAudioConfiguration(LocalCubicAudioConfiguration configuration) {
        super.setAudioConfiguration(configuration);
        disconnect();

        PublishSubject<String> singleSubject = PublishSubject.create();
        Disposable subscribe = singleSubject
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(((OnLocalCubicChangeListene) mOnCubicChangeListener)::onLoadingModel);

        mCompositeDisposable.add(subscribe);

        mCubicAudioConfiguration = configuration;
        DisposableSingleObserver<Boolean> singleObserver = mIRepository.loadModel(
                configuration.getConfiguration(),
                singleSubject
        )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<Boolean>() {

                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        startEmbeddedServer();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.exception(e);
                        mOnCubicChangeListener.onError(e);
                    }
                });

        mCompositeDisposable.add(singleObserver);
    }

    private void startEmbeddedServer() {
        if (mEmbeddedServer != null) {
            connect();
            Logger.debug("Embedded server already running.");
        }
        mOnCubicChangeListener.onConnecting();

        DisposableSingleObserver<String> singleObserver = Single.just("")
                .doOnSuccess(d ->
                        mEmbeddedServer = Cubicsvr.start(mIRepository.getConfigFile().getPath())
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<String>() {
                    @Override
                    public void onSuccess(String aBoolean) {
                        connect();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mOnCubicChangeListener.onError(e);
                        Logger.exception(e);
                    }
                });

        mCompositeDisposable.add(singleObserver);
    }

    private void stopEmbeddedServer() {
        if (mEmbeddedServer == null) {
            return;
        }

        try {
            mEmbeddedServer.stop();
        } catch (Exception e) {
            Logger.exception(e);
        }

        mEmbeddedServer = null;
    }
}
