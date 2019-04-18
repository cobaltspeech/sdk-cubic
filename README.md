# SDK for Cubic (Cobalt's Speech Recognition Engine)

This repository contains the SDK for Cobalt's Cubic Speech Recognition Engine.

This README has instructions to _build_ the SDK.  For installing and using the
SDK, see the [SDK Docs](https://cobaltspeech.github.io/sdk-cubic).

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
[docdock](https://themes.gohugo.io/docdock/). The content is authored in the
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

### Tagging New Versions

This repository has several components, and they need more than just a "vX.Y.Z"
tag on the git repo.  In particular, this repository has two go modules, one of
which depends on the other, and in order to make sure correct versions are used,
we need to follow a few careful steps to release new versions on this
repository.

Step 1: Make sure all generated code and documentation is up to date.

```
pushd grpc && make && popd
pushd docs-src && hugo -d ../docs && popd
git diff --quiet || echo "You have uncommitted changes.  Please get them merged in via a PR before updating versions."
```

Step 2: Update the version number.

In addition to the git tags, we also save the version string in a few places in
our sources.  These strings should all be updated and a new commit created.  The
git tags should then be placed on that commit once merged to master.

Decide which version you'd like to tag. For this README, let's say the next
version to tag is `1.0.1`.

Step 3: Add version tags to the sources.

```
NEW_VERSION="1.0.1"

git checkout master
git checkout -b version-update-v$NEW_VERSION

sed -i 's|grpc/go-cubic v[0-9.]*|grpc/go-cubic v'$NEW_VERSION'|g' grpc/go-cubic/cubicpb/gw/go.mod
sed -i 's|version='\''[0-9.]*'\''|version='\'$NEW_VERSION\''|g' grpc/py-cubic/setup.py

git commit -m "Update version to v$NEW_VERSION"
git push origin version-update-v$NEW_VERSION
```

Step 4: Create a pull request and get changes merged to master.

Step 5: Create version tags on the latest master branch:

```
git checkout master
git pull origin master
git tag -a v$NEW_VERSION -m ''
git tag -a grpc/go-cubic/v$NEW_VERSION -m ''
git tag -a grpc/go-cubic/cubicpb/gw/v$NEW_VERSION -m ''
git push origin --tags
```

