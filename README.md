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
  - The documentation generation plugin:
    - `go get -u github.com/pseudomuto/protoc-gen-doc/cmd/protoc-gen-doc`

### Generating Documentation

The documentation here is generated using the excellent static-site generator,
[Hugo](https://gohugo.io). The hugo-template in use is
[docuapi](https://themes.gohugo.io/docuapi/). The content is authored in the
`docs-src/content` folder, and hugo-generated static website is stored in the
`docs` folder.

You can download the latest hugo binary from the [release
page](https://github.com/gohugoio/hugo/releases). Version 0.54 or later is
required.

If you are doing local development on the docs, you can use this command to
serve it locally:
```
cd docs-src
hugo server -D
```

To generate the static documentation content, run:
```
# first make sure the generated code is up to date.  This also generates the latest auto-docs.
pushd grpc && make && popd

# then build the static documentation pages
pushd docs-src && hugo -d ../docs && popd
```

Please make sure that when changing the documentation, the newly generated
changes in `docs` are also checked into this repository.

