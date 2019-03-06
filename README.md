# SDK for Cubic (Cobalt's Speech Recognition Engine)

This repository contains the SDK for Cobalt's Cubic Speech Recognition Engine.

## Network API (using GRPC)

The `grpc` folder at the top level of this repository contains code for Cubic's
GRPC API.  The `grpc/cubic.proto` file is the authoritative service definition of
the API and is used for auto generating SDK code in multiple languages.

### Auto-generated code

The `grpc` folder contains auto-generated code in several languages.  In order
to generate the code again, you should run `cd grpc && make`.  Generated code is
checked in, and you must make sure it is up to date when you push commits to
this repository.

Code generation has the following dependencies:
  - The protobuf compiler itself.  On ubuntu, this package is `protobuf-compiler`.
  - The golang plugins:
    - `go get -u github.com/golang/protobuf/protoc-gen-go@v1.3.0`
    - `go get -u github.com/grpc-ecosystem/grpc-gateway/protoc-gen-grpc-gateway@v1.8.2`
