package com.cubic.sdk.repository;

import androidx.annotation.NonNull;

import com.cubic.sdk.repository.model.Model;

import java.io.File;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.subjects.PublishSubject;

public interface IRepository {

    List<Model> getModels();

    File getConfigFile();

    Single<Boolean> loadModel(@NonNull Model model);

    Single<Boolean> loadModel(@NonNull Model model, PublishSubject<String> callback);
}
