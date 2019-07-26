// Copyright (2019) Cobalt Speech and Language, Inc.

#ifndef CUBIC_RECOGNIZER_STREAM_H
#define CUBIC_RECOGNIZER_STREAM_H

#include <memory>

#include "cubic.grpc.pb.h"

//! The CubicRecognizerStream class represents a bidirectional stream to
//! the Cubic server. The client can push audio samples to the server
//! while simultaneously receiving recognition results.
class CubicRecognizerStream
{
public:
    using CubicReaderWriter = std::shared_ptr<grpc::ClientReaderWriter<cobaltspeech::cubic::StreamingRecognizeRequest,
                                                                 cobaltspeech::cubic::RecognitionResponse>>;

    //! Create a new ASR recognizer stream. Most users of this class should
    //! not need to call this constructor. Instead, they should use
    //! CubicClient::streamingRecognize().
    CubicRecognizerStream(const CubicReaderWriter &readerWriter,
                          const std::shared_ptr<grpc::ClientContext> &ctx);
    ~CubicRecognizerStream();

    //! Push the given audio data to the server. The data should match the
    //! format specified by the RecognitionConfig when the recognizer was
    //! created. It is thread-safe to call this method while also calling
    //! receiveResults().
    void pushAudio(const char* audioData, size_t sizeInBytes);

    //! Tell the server that no more audio data is coming. It is an error
    //! to call the pushAudio() method after calling this.
    void audioFinished();

    //! Receive recognition results from the server as they become available.
    //! Returns false when there are no more results to receive. It is thread-
    //! safe to call this method while also calling pushAudio().
    bool receiveResults(cobaltspeech::cubic::RecognitionResponse *response);

    //! Close the recognition stream. This should be done after writing AND
    //! reading are both finished.
    void close();

private:
    CubicReaderWriter mStream;
    std::shared_ptr<grpc::ClientContext> mCtx;
};

#endif // CUBIC_RECOGNIZER_STREAM_H
