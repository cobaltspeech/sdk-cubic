# Copyright (2019) Cobalt Speech and Language Inc.

.PHONY: all go py cs swift cs_push android go-test py-test docs

all: docs go py swift

ifeq ($(PROTOINC),)
 $(error "env variable PROTOINC must be defined to the location of googleapis proto files")
endif

GO_OUTDIR=go-cubic/cubicpb
PY_OUTDIR=py-cubic/cubic
CSHARP_OUTDIR=csharp-cubic/cubic
DOCS_OUTDIR=../docs-src/content/protobuf
SWIFTDIR=../deps/swift/bin

go: go-pb go-gw go-test

go-pb:
	mkdir -p ${GO_OUTDIR}
	protoc -I . -I ${PROTOINC} --go_out=plugins=grpc:"${GO_OUTDIR}" cubic.proto

# grpc-gateway assumes that the gateway package lives in the same package as the
# main proto package.  However, the gateway code is not necessary for clients,
# and hence we move it to a separate package (gw) and the generated file needs
# to be edited so it can use cubicpb as an external package.  There is a test in
# go-cubic/client_test.go that verifies that this modified package still builds
# and serves the gateway.
go-gw:
	mkdir -p ${GO_OUTDIR}/gw
	protoc -I . -I ${PROTOINC} --grpc-gateway_out=logtostderr=true:${GO_OUTDIR}/gw cubic.proto
	sed -i -E "s/package cubicpb/package gw/g" ${GO_OUTDIR}/gw/cubic.pb.gw.go
	sed -i -E "s/Package cubicpb is/Package gw is/g" ${GO_OUTDIR}/gw/cubic.pb.gw.go
	sed -i -E "s|import \(|& \"github.com/cobaltspeech/sdk-cubic/grpc/go-cubic/cubicpb\"|" ${GO_OUTDIR}/gw/cubic.pb.gw.go
	sed -i -E "s/protoReq /&cubicpb\./g" ${GO_OUTDIR}/gw/cubic.pb.gw.go
	sed -i -E "s/(protoReq )cubicpb\.([A-Za-z]+\.)/\1\2/g" ${GO_OUTDIR}/gw/cubic.pb.gw.go
	sed -i -E "s/, client /, client cubicpb./g" ${GO_OUTDIR}/gw/cubic.pb.gw.go
	sed -i -E "s/, server /, server cubicpb./g" ${GO_OUTDIR}/gw/cubic.pb.gw.go
	sed -i -E "s/Client /, client cubicpb./g" ${GO_OUTDIR}/gw/cubic.pb.gw.go
	sed -i -E "s/[^(]*Client, runtime/cubicpb.&/" ${GO_OUTDIR}/gw/cubic.pb.gw.go
	sed -i -E "s/New[A-Za-z]*Client/cubicpb.&/" ${GO_OUTDIR}/gw/cubic.pb.gw.go
	cd $(dir ${GO_OUTDIR}/gw/cubic.pb.gw.go) && go fmt $(notdir ${GO_OUTDIR}/gw/cubic.pb.gw.go)

go-test:
	cd go-cubic && go test
	cd go-cubic/cubicpb/gw && go test

py: py-pb py-grpc py-test

py-pb:
	mkdir -p ${PY_OUTDIR}
	python -m grpc_tools.protoc -I . -I ${PROTOINC} --python_out=${PY_OUTDIR} cubic.proto

py-grpc:
	mkdir -p ${PY_OUTDIR}
	python -m grpc_tools.protoc -I . -I ${PROTOINC} --grpc_python_out=${PY_OUTDIR} cubic.proto

py-test:
	cd py-cubic/cubic && python test_client.py
	cd py-cubic/cubic && python test_tls.py

## C#
## https://docs.microsoft.com/en-us/nuget/quickstart/create-and-publish-a-package-using-the-dotnet-cli
## You need to copy the PROTOINC files over to the ~/.nuget/packages/grpc.tools/1.22.0/build/native/include directory.
CSHARP_RELEASE_VERSION="1.5.0"
NUGET_API_KEY="" # Must be set to push the nuget package.
cs:
	cd csharp-cubic/ && dotnet build ./cubic.csproj \
		-p:PackageVersion=${CSHARP_RELEASE_VERSION}
cs_push:
	dotnet nuget push \
		./csharp-cubic/bin/Debug/Cubic-SDK.${CSHARP_RELEASE_VERSION}.nupkg \
		-k ${NUGET_API_KEY} \
		-s https://api.nuget.org/v3/index.json

swift:
	protoc -I . -I ${PROTOINC} \
		--plugin=$$(which protoc-gen-grpc-swift) \
		--grpc-swift_out=Client=true,Server=false,Visibility=Public:./swift-cubic \
		--plugin=$$(which protoc-gen-swift) \
		--swift_opt=Visibility=Public \
		--swift_out=./swift-cubic \
		cubic.proto
android:
	cp cubic.proto android-cubic/src/main/proto/cubic.proto
	cd android-cubic && gradle wrapper && ./gradlew build

docs:
	protoc -I . -I ${PROTOINC} --doc_out=${DOCS_OUTDIR} --doc_opt=doc.md.tmpl,autogen-doc-cubic-proto.md cubic.proto

