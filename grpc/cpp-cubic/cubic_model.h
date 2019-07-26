// Copyright (2019) Cobalt Speech and Language, Inc.

#ifndef CUBIC_MODEL_H
#define CUBIC_MODEL_H

#include <string>

#include "cubic.pb.h"

//! The CubicModel class represents an ASR model that is being
//! used by the Cubic server.
class CubicModel
{
public:
    //! Create a CubicModel from the gRPC data structure.
    CubicModel(const cobaltspeech::cubic::Model &model);

    //! Destructor
    ~CubicModel();

    //! Return the id of the model, as specified in the server's
    //! config file.
    const std::string& id() const;

    //! Return the user-friendly name of the model, as specified
    //! in the server's config file.
    const std::string& name() const;

    //! Return the sample rate for the model in Hz.
    unsigned int sampleRate() const;

private:
    std::string mId;
    std::string mName;
    unsigned int mSampleRate;
};

#endif // CUBIC_MODEL_H
