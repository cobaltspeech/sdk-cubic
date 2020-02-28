package com.cubic.sdk.repository;

import android.content.Context;

import androidx.annotation.NonNull;

import com.cubic.sdk.preference.config.ConfigurationPreferenceManager;
import com.cubic.sdk.preference.config.ConfigurationPreferenceOptions;
import com.cubic.sdk.preference.config.IConfigurationPreferenceManager;
import com.cubic.sdk.preference.connect.IConnectPreferenceManager;
import com.cubic.sdk.preference.connect.local.LocalConnectPreferenceManager;
import com.cubic.sdk.repository.aws.AwsClient;
import com.cubic.sdk.repository.aws.IAwsClient;
import com.cubic.sdk.repository.model.Model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.SingleSubject;

public final class ModelRepository implements IRepository {

    private final String MODELS_FOLDER = ConfigurationPreferenceOptions.Defaults.MODELS_FOLDER;
    private final String CONFIG_FILE = ConfigurationPreferenceOptions.Defaults.CONFIG_FILE;
    private final String LICENSE_FILE = ConfigurationPreferenceOptions.Defaults.LICENSE_FILE;

    private final Context mContext;
    private final IConnectPreferenceManager mIConnectionPreferenceManager;
    private final IConfigurationPreferenceManager mIConfigurationPreferenceManager;
    private final IAwsClient mIAwsClient;

    private final List<Model> mModels = new ArrayList<>();

    public ModelRepository(@NonNull Context context) {
        mContext = context;
        mIConfigurationPreferenceManager = new ConfigurationPreferenceManager(context);
        mIConnectionPreferenceManager = new LocalConnectPreferenceManager(context);
        mIAwsClient = new AwsClient(context);

        mModels.add(new Model(
                "en_US-16khz-FF-20190912",
                "models/asr-kaldi/en_US/16khz/")
        );
    }

    private Single<File> buildDirectory() {
        return Single.just(mContext)
                .map(c -> {
                    File root = mContext.getExternalFilesDir(null);
                    File modelsDir = new File(root, MODELS_FOLDER);
                    if (!modelsDir.exists() && !modelsDir.mkdir())
                        throw new RuntimeException("Folder not created: " + MODELS_FOLDER);

                    initLicenseFile();

                    return modelsDir;
                });
    }

    private void initLicenseFile() throws IOException {
        File licenseFile;

        File root = mContext.getExternalFilesDir(null);
        if (root == null)
            throw new RuntimeException("File not created: " + MODELS_FOLDER);

        licenseFile = new File(root.getPath() + File.separatorChar + MODELS_FOLDER, LICENSE_FILE);
        if (licenseFile.exists()) return;

        if (!licenseFile.createNewFile())
            throw new RuntimeException("File not created: " + LICENSE_FILE);

        FileOutputStream fOut = new FileOutputStream(licenseFile);
        OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
        myOutWriter.append(mIConfigurationPreferenceManager.getLicense());

        myOutWriter.close();

        fOut.flush();
        fOut.close();
    }

    private Single<File> initConfigFile(@NonNull Model model) {
        return Single.just(model)
                .map(m -> {
                    File root = mContext.getExternalFilesDir(null);
                    File cfgfile = getConfigFile();

                    String data = "# cubicsvr configuration file for android files.\n" +
                            "\n" +
                            "[server]\n" +
                            "  GRPCPort = " + mIConnectionPreferenceManager.getConnectionConfiguration().getPort() + "  # Port to which GRPC server will bind\n" +
                            "  WebDemo = false  # do not enable web-demo\n" +
                            "\n" +
                            "  HTTPReadTimeout = \"0s\"\n" +
                            "  HTTPWriteTimeout = \"0s\"\n" +
                            "  HTTPIdleTimeout = \"0s\"\n" +
                            "\n" +
                            "[recognizer]\n" +
                            "  MaxTTL = \"10m\"\n" +
                            "  MaxIdleTimeout = \"30s\"\n" +
                            "  MaxAudioBytes = 52428800 # 50MB\n" +
                            "\n" +
                            "[license]\n" +
                            "    KeyFile = \"" + root + "/" + MODELS_FOLDER + "/" + LICENSE_FILE + "\"\n" +
                            "\n" +
                            "[[models]]\n" +
                            "  ID = \"1\"\n" +
                            "  Name = \"" + m.getName() + "\"\n" +
                            "  ModelConfigPath = \"./" + MODELS_FOLDER + "/" + m.getName() + "/" + m.getConfigFile() + "\"";

                    if (!cfgfile.exists() && !cfgfile.createNewFile())
                        throw new RuntimeException("File not created: " + CONFIG_FILE);

                    FileOutputStream fOut = new FileOutputStream(cfgfile);
                    OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
                    myOutWriter.append(data);

                    myOutWriter.close();

                    fOut.flush();
                    fOut.close();

                    return cfgfile;
                });
    }

    private String getModelsDir() {
        File root = mContext.getExternalFilesDir(null);
        if (root == null)
            throw new RuntimeException("Models dir not created");

        return root.getPath() + File.separatorChar + MODELS_FOLDER + File.separatorChar;
    }

    @Override
    public List<Model> getModels() {
        return mModels;
    }

    @Override
    public File getConfigFile() {
        File root = mContext.getExternalFilesDir(null);
        if (root == null)
            throw new RuntimeException("Config file not created");

        return new File(root, CONFIG_FILE);
    }

    @Override
    public Single<Boolean> loadModel(@NonNull Model model) {
        return loadModel(model, null);
    }

    @Override
    public Single<Boolean> loadModel(@NonNull Model model, PublishSubject<String> callback) {
        return Single.zip(
                buildDirectory(),
                initConfigFile(model),
                downloadModelConfig(model, callback),
                downloadModelNNET3(model, callback),
                downloadModelGraph(model, callback),
                (f, f2, f3, f4, f5) -> true
        );
    }

    private Single<File> downloadModelNNET3(@NonNull Model model, PublishSubject<String> callback) {
        return Single.just(model)
                .map(m -> {
                    String outFolder = getModelsDir() + m.getName() + File.separatorChar + "am";
                    File file = new File(outFolder, m.getNNET3File());

                    if (file.exists()) return file;

                    if (callback != null) callback.onNext("Download: " + m.getNNET3File());

                    mIAwsClient.downloadFolder(
                            m.getNNET3Path(),
                            outFolder,
                            m.getNNET3File());

                    return file;
                });
    }

    private Single<File> downloadModelGraph(@NonNull Model model, PublishSubject<String> callback) {
        return Single.just(model)
                .map(m -> {
                    String outFolder = getModelsDir() + m.getName();
                    File file = new File(outFolder, m.getGraphFile());

                    if (file.exists()) return file;

                    if (callback != null) callback.onNext("Download: " + m.getGraphFile());

                    mIAwsClient.downloadFolder(
                            m.getGraphPath(),
                            outFolder,
                            m.getGraphFile()
                    );

                    return file;
                });
    }

    private Single<File> downloadModelConfig(@NonNull Model model, PublishSubject<String> callback) {
        String outFolder = getModelsDir() + model.getName() + File.separatorChar;
        File file = new File(outFolder + model.getConfigFile());

        SingleSubject<File> bs = SingleSubject.create();

        if (file.exists()) return Single.just(file);

        if (callback != null) callback.onNext("Download: " + model.getConfigFile());

        mIAwsClient.downloadFile(
                model.getModelPath(),
                outFolder,
                model.getConfigFile(),
                bs
        );

        return bs;
    }
}
