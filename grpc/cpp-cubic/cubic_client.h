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

    //! Compiles the given list of phrases or words into a compact, fast to
    //! access form for Cubic, which may later be provided in a `recognize` or
    //! `streamingRecognize` call to aid speech recognition.
    //!
    //! The modelID is the unique identifier of the model to compile the context
    //! information for.
    //!
    //! The token is a string allowed by the model being used, such as "names"
    //! or "airports", that is used to determine the place in the recognition
    //! output where the provided list of phrases or words may appear. The
    //! allowed tokens for a given model can be obtained via the `listModels`
    //! method and looking up the `allowedContextTokens()` attribute for the
    //! model.
    //!
    //! The phrases (or words) are given as a vector of strings.
    //!
    //! Optionally, a vector of positive floating numbers, boostValues, may also
    //! be provided, one for each phrase or word in the phrase list. These
    //! values can be used to increase the likelihood of the corresponding entry
    //! in the phrases by setting a higher value for it. The boostValues slice
    //! can be set to nil if boosting is not required.
    //!
    //! If given boostValues, the new probability of the corresponding phrase
    //! entry becomes (boost + 1.0) * old probability. By default, all provded
    //! phrases or words are given an equal probability of 1/N, where N = total
    //! number of phrases or words.The new probabilities are normalized after
    //! boosting so that they sum to one. This means that if all phrases are
    //! given the same boost value, they will still have the same default
    //! likelihood. This also means that the boost value can be any positive
    //! value, but for most cases, values between 0 to 10 work well. Negative
    //! values may be provided but they will be treated as 0 (no boost).
    cobaltspeech::cubic::CompiledContext
        compileContext(const std::string &modelID, const std::string &token, 
                       const std::vector<std::string> &phrases, 
                       const std::vector<float> &boostValues = std::vector<float>());

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
