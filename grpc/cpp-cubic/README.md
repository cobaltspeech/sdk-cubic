# Cubic C++ SDK
Unlike other languages, C++ has more strict requirements to
integrate with gRPC. The easiest way to accomodate these
requirements is to build gRPC as part of the C++ project
and use it to generate the required C++ files from the 
cubic.proto file.

## CMake Build
To help simplify the build process, this project uses
[CMake](www.cmake.org). CMake will automatically download
the gRPC source code and include it as a subproject, giving
access to the gRPC libraries and the protoc compiler.

To build as a stand-alone library execute the following
commands:
```bash
# Create a build directory. It can be named anything and
# can exist outside of the source code directory.
mkdir build-cubic-client && cd build-cubic-client

# Run CMake to download gRPC and generate makefiles.
# The final path specifies the directory that contains the
# CMakeLists.txt file for the cubic_client C++ project. By
# default this will create static libraries. To make a shared
# library instead, add -DBUILD_SHARED_LIBS=TRUE
cmake -DCMAKE_BUILD_TYPE=Release <path/to/sdk-cubic/grpc/cpp-cubic>
# OR 
cmake -DCMAKE_BUILD_TYPE=Release -DBUILD_SHARED_LIBS=TRUE <path/to/sdk-cubic/grpc/cpp-cubic>

# Build the library.
make cubic_client
```

To include this CMake project in another one, simply
copy this repository into your project and add the line

```cmake
add_subdirectory(sdk-cubic/grpc/cpp-cubic)
```

to your project's CMakeLists.txt. You may also include it
using the [FetchContent module](https://cmake.org/cmake/help/latest/module/FetchContent.html),
which has the added convenience of downloading a specific
version of the SDK from Github automatically.

```cmake
# Fetch Cubic SDK code and add to the project
include(FetchContent)
FetchContent_Declare(
    sdk_cubic
    GIT_REPOSITORY https://github.com/cobaltspeech/sdk-cubic.git
    GIT_TAG v1.6.2
)
FetchContent_Populate(sdk_cubic)

# The SDK's CMake file is not at the top-level directory of the repo,
# so we must tell CMake explicitly which subdirectory to add.
add_subdirectory(${sdk_cubic_SOURCE_DIR}/grpc/cpp-cubic ${sdk_cubic_BINARY_DIR})
```

### Windows Build
When building for Windows, we recommend running CMake in the Visual
Studio [command line](https://docs.microsoft.com/en-us/visualstudio/ide/reference/command-prompt-powershell?view=vs-2019).
The VS command line will automatically set several environment variables
that will be useful to CMake in defining compilers and other options.
You will also need to download and install the Netwide Assmbler from
[here](https://www.nasm.us/). Make sure it is available to your `PATH`
so that CMake can find it (or specify it manually in the CMake
configuration).

Also note that gRPC doesn't really support building shared libraries
on Windows. If you try it, you will likely get build errors, similar to
what is reported [here](https://github.com/grpc/grpc/issues/25311).

## Build without CMake
When building without CMake, you must manually build and install 
gRPC as [described here](https://grpc.io/docs/quickstart/cpp/).
Once that is done, generate the gRPC/protobuf files by running
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
the gRPC installation's `lib` directory).
