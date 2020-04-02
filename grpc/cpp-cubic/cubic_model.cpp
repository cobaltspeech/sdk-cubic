// Copyright (2019) Cobalt Speech and Language, Inc.

#include "cubic_model.h"

CubicModel::CubicModel(const cobaltspeech::cubic::Model &model) :
    mId(model.id()),
    mName(model.name()),
    mSampleRate(model.attributes().sample_rate()),
    mSupportsContext(model.attributes().supports_context())
{   
    // getting list of context tokens
    cobaltspeech::cubic::ModelAttributes attributes = model.attributes();
    mAllowedContextTokens.resize(attributes.allowed_context_tokens_size());
    for(int i=0; i<mAllowedContextTokens.size(); i++) {
      mAllowedContextTokens[i] = *(attributes.mutable_allowed_context_tokens(i));
    }
}

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

bool CubicModel::supportsContext() const
{
    return mSupportsContext;
}

std::vector<std::string> CubicModel::allowedContextTokens() const
{
    return mAllowedContextTokens;
}
