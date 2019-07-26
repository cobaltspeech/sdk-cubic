# Copyright (2019) Cobalt Speech and Language, Inc.

cmake_minimum_required(VERSION 3.5.0)
project(cubic_client)

include(CobaltGRPC.cmake)

set(CMAKE_CXX_STANDARD 11)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# Generate the grpc files from our proto files.
run_protoc(RESULT_FILES CUBIC_PROTOFILES
    PROTOS ../cubic.proto
    OUTPUT_DIR ${CMAKE_CURRENT_BINARY_DIR})

# Also generate dependency files (our cubic.proto file uses these).
run_protoc(RESULT_FILES GOOGLE_PROTOFILES
    PROTOS
      ${grpc_SOURCE_DIR}/third_party/googleapis/google/api/annotations.proto
      ${grpc_SOURCE_DIR}/third_party/googleapis/google/api/http.proto
    OUTPUT_DIR ${CMAKE_CURRENT_BINARY_DIR}
    OUTPUT_SUBDIR google/api)

# Setup the library
add_library(cubic_client
    ${CUBIC_PROTOFILES}
    ${GOOGLE_PROTOFILES}
    cubic_client.cpp
    cubic_client.h
    cubic_exception.cpp
    cubic_exception.h
    cubic_model.cpp
    cubic_model.h
    cubic_recognizer_stream.cpp
    cubic_recognizer_stream.h)

# Setup the linking and include directories for the library.
# We give it PUBLIC visibility so other targets can use the
# include directories and libraries.
target_include_directories(cubic_client PUBLIC
    ${CMAKE_CURRENT_SOURCE_DIR}
    ${CMAKE_CURRENT_BINARY_DIR})

target_link_libraries(cubic_client PUBLIC
    grpc grpc++ libprotobuf)
