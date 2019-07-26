// Copyright (2019) Cobalt Speech and Language, Inc.

#include "cubic_model.h"

CubicModel::CubicModel(const cobaltspeech::cubic::Model &model) :
    mId(model.id()),
    mName(model.name()),
    mSampleRate(model.attributes().sample_rate())
{}

CubicModel::~CubicModel()
{}

const std::string& CubicModel::id() const
{
    return mId;
}

const std::string& CubicModel::name() const
{
    return mName;
}

unsigned int CubicModel::sampleRate() const
{
    return mSampleRate;
}


