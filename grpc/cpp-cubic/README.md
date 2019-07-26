# Cubic C++ SDK
Unlike other languages, C++ has more strict requirements to
integrate with gRPC. The easiest way to accomodate these
requirements is to build gRPC as part of the C++ project
and use it to generate the required C++ files from the 
cubic.proto file.

## CMake Build
To help simplify the build process, this project uses
of [CMake](www.cmake.org). CMake will automatically download
the grpc source code and include it as a subproject, giving
access to the grpc libraries and the protoc compiler.

To build as a standalone library execute the following
commands:
```bash
# Create a build directory. It can be named anything and
# can exist outside of the source code directory.
mkdir build-cubic-client && cd build-cubic-client

# Run CMake to download grpc and generate makefiles.
# The final path specifies the directory that contains the
# CMakeLists.txt file for the cubic_client C++ project.
cmake -DCMAKE_BUILD_TYPE=Release <path/to/sdk-cubic/grpc/cpp-cubic>
make cubic_client

# To make a shared library, add -DBUILD_SHARED_LIBS=TRUE when running cmake
cmake -DCMAKE_BUILD_TYPE=Release -DBUILD_SHARED_LIBS=TRUE <path/to/sdk-cubic/grpc/cpp-cubic>
make cubic_client
```

To include this CMake project in another one, simply
copy this repository into your project and add the line

```cmake
add_subdirectory(sdk-cubic/grpc/cpp-cubic)
```

to your project's CMakeLists.txt.

## Build without CMake
When building without CMake, you must manually build and install 
gRPC as [described here](https://grpc.io/docs/quickstart/cpp/).
Once that is done, generate the grpc/protobuf files by running
protoc:

```bash
CUBIC_GRPC_DIR=<path/to/sdk-cubic/grpc>
GRPC_SOURCE_DIR=<path/to/grpc>
GRPC_INSTALL_DIR=<path/to/grpc/install>
OUTDIR=<path/to/generated/output>
mkdir -p $OUTDIR

GOOGLE_APIS_DIR=$GRPC_SOURCE_DIR/third_party/googleapis
PROTO_INCLUDE_PATH=$GRPC_INSTALL_DIR/include/google/protobuf:$GOOGLE_APIS_DIR

# Generate files for cubic
$GRPC_INSTALL_DIR/bin/protoc \
  -I $PROTO_INCLUDE_PATH:$CUBIC_GRPC_DIR \
  --grpc_out=$OUTDIR \
  --cpp_out=$OUTDIR \
  --plugin=protoc-gen-grpc=$GRPC_INSTALL_DIR/bin/grpc_cpp_plugin \
  $CUBIC_GRPC_DIR/cubic.proto

# Generate files for cubic's dependencies
$GRPC_INSTALL_DIR/bin/protoc \
  -I $PROTO_INCLUDE_PATH:$GOOGLE_APIS_DIR/google/api \
  --grpc_out=$OUTDIR \
  --cpp_out=$OUTDIR \
  --plugin=protoc-gen-grpc=$GRPC_INSTALL_DIR/bin/grpc_cpp_plugin \
  $GOOGLE_APIS_DIR/google/api/annotations.proto \
  $GOOGLE_APIS_DIR/google/api/http.proto
```

Once the files are generated, include them with the wrapper code
from this directory in your project. Then be sure to link your
binaries with libgrpc, libgrpc++, and libprotobuf (found in
the grpc installation's `lib` directory).
