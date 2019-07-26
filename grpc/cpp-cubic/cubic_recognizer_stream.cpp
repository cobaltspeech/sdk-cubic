// Copyright (2019) Cobalt Speech and Language, Inc.

#include "cubic_recognizer_stream.h"

#include "cubic_exception.h"

CubicRecognizerStream::CubicRecognizerStream(const CubicReaderWriter &readerWriter,
                                             const std::shared_ptr<grpc::ClientContext> &ctx) :
    mStream(readerWriter),
    mCtx(ctx)
{}

CubicRecognizerStream::~CubicRecognizerStream()
{}

void CubicRecognizerStream::pushAudio(const char *audioData, size_t sizeInBytes)
{
    if(sizeInBytes == 0)
        return;

    // Setup the request and write to the input stream.
    cobaltspeech::cubic::StreamingRecognizeRequest request;
    request.mutable_audio()->set_data(audioData, sizeInBytes);
    if(!mStream->Write(request))
    {
        throw CubicException("could not push audio - input stream is closed");
    }
}

void CubicRecognizerStream::audioFinished()
{
    if(!mStream->WritesDone())
    {
        throw CubicException("unsuccessful writing audio data");
    }
}

bool CubicRecognizerStream::receiveResults(cobaltspeech::cubic::RecognitionResponse *response)
{
    return mStream->Read(response);
}

void CubicRecognizerStream::close()
{
    grpc::Status status = mStream->Finish();
    if(!status.ok())
    {
        throw CubicException(status);
    }
}
