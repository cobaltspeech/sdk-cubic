// Copyright (2021) Cobalt Speech and Language, Inc.

#include "cubic_client.h"

#include <chrono>

#include <grpc/grpc.h>
#include <grpcpp/channel.h>
#include <grpcpp/client_context.h>
#include <grpcpp/create_channel.h>

#include "cubic.grpc.pb.h"
#include "cubic_exception.h"


CubicClient::CubicClient(const std::string &url) :
    mCubicVersion(""),
    mServerVersion(""),
    mTimeout(30000)
{
    // Quick runtime check to verify that the user has linked against
    // a version of protobuf that is compatible with the version used
    // to generate the c++ files.
    GOOGLE_PROTOBUF_VERIFY_VERSION;

    // Set up insecure credentials
    auto creds = grpc::InsecureChannelCredentials();

    // Create the channel and stub
    std::unique_ptr<cobaltspeech::cubic::Cubic::Stub> tmpStub =
            cobaltspeech::cubic::Cubic::NewStub(grpc::CreateChannel(url, creds));
    mStub.swap(tmpStub);
}

CubicClient::CubicClient(const std::string &url, const grpc::SslCredentialsOptions &opts) :
    mCubicVersion(""),
    mServerVersion(""),
    mTimeout(30000)
{
    // Quick runtime check to verify that the user has linked against
    // a version of protobuf that is compatible with the version used
    // to generate the c++ files.
    GOOGLE_PROTOBUF_VERIFY_VERSION;

    // Set up secure credentials
    auto creds = grpc::SslCredentials(opts);

    // Create the channel and stub
    std::unique_ptr<cobaltspeech::cubic::Cubic::Stub> tmpStub =
            cobaltspeech::cubic::Cubic::NewStub(grpc::CreateChannel(url, creds));
    mStub.swap(tmpStub);
}

CubicClient::~CubicClient()
{}

const std::string& CubicClient::cubicVersion()
{
    // Check if we have it cached.
    if(mCubicVersion.empty())
        this->requestVersion();

    return mCubicVersion;
}

const std::string& CubicClient::serverVersion()
{
    // Check if we have it cached.
    if(mServerVersion.empty())
        this->requestVersion();

    return mServerVersion;
}

std::vector<CubicModel> CubicClient::listModels()
{
    // Check if we have already cached the models
    if(mModels.empty())
    {
        // If it is not cached, make the gRPC request.
        grpc::ClientContext ctx;
        cobaltspeech::cubic::ListModelsRequest request;
        cobaltspeech::cubic::ListModelsResponse response;

        this->setContextDeadline(ctx);
        grpc::Status status = mStub->ListModels(&ctx, request, &response);

        if(!status.ok())
            throw CubicException(status);

        // Cache the models.
        for(int i=0; i < response.models_size(); i++)
        {
            CubicModel model(response.models(i));
            mModels.push_back(model);
        }
    }

    return mModels;
}

cobaltspeech::cubic::CompiledContext
    CubicClient::compileContext(const std::string &modelID, const std::string &token, 
                                const std::vector<std::string> &phrases,
                                const std::vector<float> &boostValues)
{

    // Setup the request
    cobaltspeech::cubic::CompileContextRequest request;
    request.set_model_id(modelID);
    request.set_token(token);
    if (boostValues.size() > 0) 
    {
        if (boostValues.size() != phrases.size())
        {
          throw CubicException("number of boost values not the same as number of phrases");
        }
        
        for(int i=0; i < phrases.size(); i++) 
        {
            cobaltspeech::cubic::ContextPhrase *ptr = request.add_phrases();
            ptr->set_text(phrases[i]);
            ptr->set_boost(boostValues[i]);
        }
    }
    else // no boost values
    {
        for(int i=0; i < phrases.size(); i++) 
        {
            cobaltspeech::cubic::ContextPhrase *ptr = request.add_phrases();
            ptr->set_text(phrases[i]);
            ptr->set_boost(0);
        }
    }

    // Setup the context and make the request
    cobaltspeech::cubic::CompileContextResponse response;
    grpc::ClientContext ctx;
    this->setContextDeadline(ctx);
    grpc::Status status = mStub->CompileContext(&ctx, request, &response);
    if (!status.ok()){
      throw CubicException(status);
    }

    return response.context();
}

cobaltspeech::cubic::RecognitionResponse
  CubicClient::recognize(const cobaltspeech::cubic::RecognitionConfig &config,
                         const char* audioData, size_t sizeInBytes)
{
    // Setup the request
    cobaltspeech::cubic::RecognizeRequest request;
    request.mutable_audio()->set_data(audioData, sizeInBytes);
    request.mutable_config()->CopyFrom(config);

    // Setup the context and make the request
    cobaltspeech::cubic::RecognitionResponse response;
    grpc::ClientContext ctx;
    this->setContextDeadline(ctx);
    grpc::Status status = mStub->Recognize(&ctx, request, &response);
    if(!status.ok())
        throw CubicException(status);

    return response;
}

CubicRecognizerStream CubicClient::streamingRecognize(
        const cobaltspeech::cubic::RecognitionConfig &config)
{
    // We need the context to exist for as long as the stream,
    // so we are creating it as a managed pointer.
    std::shared_ptr<grpc::ClientContext> ctx(new grpc::ClientContext);
    this->setContextDeadline(*ctx);

    // Create the grpc reader/writer.
    std::shared_ptr<grpc::ClientReaderWriter<cobaltspeech::cubic::StreamingRecognizeRequest,
                                             cobaltspeech::cubic::RecognitionResponse>>
            readerWriter(mStub->StreamingRecognize(ctx.get()));

    // Send the first message (the recognizer config) to Cubic. We must
    // do this before we can send any audio data.
    cobaltspeech::cubic::StreamingRecognizeRequest request;
    request.mutable_config()->CopyFrom(config);
    if(!readerWriter->Write(request))
        throw CubicException("couldn't send recognizer config to Cubic");

    return CubicRecognizerStream(readerWriter, ctx);
}

void CubicClient::setRequestTimeout(unsigned int milliseconds)
{
    mTimeout = milliseconds;
}

CubicClient::CubicClient(const CubicClient &)
{
    // Do nothing. This copy constructor is intentionally private
    // and does nothing because we don't want to copy client objects.
}

CubicClient& CubicClient::operator=(const CubicClient &)
{
    // Do nothing. The assignment operator is intentionally private
    // and does nothing because we don't want to copy client objects.
    return *this;
}

void CubicClient::setContextDeadline(grpc::ClientContext &ctx)
{
    std::chrono::system_clock::time_point deadline =
            std::chrono::system_clock::now() + std::chrono::milliseconds(mTimeout);
    ctx.set_deadline(deadline);
}

void CubicClient::requestVersion()
{
    grpc::ClientContext ctx;
    google::protobuf::Empty request;
    cobaltspeech::cubic::VersionResponse response;

    this->setContextDeadline(ctx);
    grpc::Status status = mStub->Version(&ctx, request, &response);
    if(!status.ok())
        throw CubicException(status);

    mCubicVersion = response.cubic();
    mServerVersion = response.server();
}
