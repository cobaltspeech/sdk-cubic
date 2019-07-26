// Copyright (2019) Cobalt Speech and Language, Inc.

#ifndef CUBIC_CLIENT_H
#define CUBIC_CLIENT_H

#include <memory>
#include <string>
#include <vector>

#include "cubic.grpc.pb.h"
#include "cubic.pb.h"
#include "cubic_model.h"
#include "cubic_recognizer_stream.h"


//! CubicClient represents a single client connection to a running
//! Cubic server instance.
class CubicClient
{
public:
    //! Create a new client that is connected to a Cubic server instance
    //! running at the given url. The given url should include the port number.
    //! If secureConnection is true, the connection will use TLS/SSL to
    //! communicate with the server. Otherwise the connection is insecure.
    //! Note that the server must also be running with TLS/SSL for the
    //! secure connection to succeed.
    CubicClient(const std::string &url, bool secureConnection);
    ~CubicClient();

    //! Returns the version of Cubic used by the server.
    const std::string& cubicVersion();

    //! Returns the version of the server.
    const std::string& serverVersion();

    //! Returns a list of Cubic ASR models that the server is currently
    //! configured to use.
    std::vector<CubicModel> listModels();

    //! Do batch ASR recognition. Submit the recognizer config and audio to
    //! the server and wait for the final results.
    cobaltspeech::cubic::RecognitionResponse
        recognize(const cobaltspeech::cubic::RecognitionConfig &config,
                  const char* audioData, size_t sizeInBytes);

    //! Create an ASR recognition stream using the given config. The returned
    //! stream can push audio to the server and receive results back until the
    //! stream is closed.
    CubicRecognizerStream
        streamingRecognize(const cobaltspeech::cubic::RecognitionConfig &config);

    //! Set the timeout for requests to the server.
    void setRequestTimeout(unsigned int milliseconds);

private:
    std::string mCubicVersion;
    std::string mServerVersion;
    std::vector<CubicModel> mModels;
    unsigned int mTimeout;
    std::unique_ptr<cobaltspeech::cubic::Cubic::Stub> mStub;

    // Disable copy construction and assignments. Given the nature
    // of the client, it's a bad idea to try to copy an existing connection.
    CubicClient(const CubicClient &other);
    CubicClient& operator=(const CubicClient &other);

    // Convenience functions
    void setContextDeadline(grpc::ClientContext &ctx);
    void requestVersion();
};

#endif // CUBIC_CLIENT_H
