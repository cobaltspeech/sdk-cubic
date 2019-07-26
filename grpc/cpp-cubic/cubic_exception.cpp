// Copyright (2019) Cobalt Speech and Language, Inc.

#include "cubic_exception.h"

CubicException::CubicException(const std::string &msg) :
    mMsg(msg)
{}

CubicException::CubicException(const grpc::Status &status) :
    mMsg(status.error_message())
{}

CubicException::~CubicException()
{}

const char* CubicException::what() const noexcept
{
    return mMsg.c_str();
}
