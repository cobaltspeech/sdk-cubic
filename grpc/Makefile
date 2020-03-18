# Copyright (2019) Cobalt Speech and Language Inc.

.PHONY: all go py cs cs_push go-test py-test docs

all: go py docs

ifeq ($(PROTOINC),)
$(error "env variable PROTOINC must be defined to the location of googleapis proto files")
endif

GO_OUTDIR=go-cubic/cubicpb
PY_OUTDIR=py-cubic/cubic
CSHARP_OUTDIR=csharp-cubic/cubic
DOCS_OUTDIR=../docs-src/content/protobuf

go: ${GO_OUTDIR}/cubic.pb.go ${GO_OUTDIR}/gw/cubic.pb.gw.go go-test
py: $(PY_OUTDIR)/cubic_pb2.py ${PY_OUTDIR}/cubic_pb2_grpc.py py-test

## C#
## https://docs.microsoft.com/en-us/nuget/quickstart/create-and-publish-a-package-using-the-dotnet-cli
## You need to copy the PROTOINC files over to the ~/.nuget/packages/grpc.tools/1.22.0/build/native/include directory.
CSHARP_RELEASE_VERSION="1.3.0"
NUGET_API_KEY="" # Must be set to push the nuget package.
cs:
	cd csharp-cubic/ && dotnet build ./cubic.csproj \
		-p:PackageVersion=${CSHARP_RELEASE_VERSION}
cs_push:
	dotnet nuget push \
		./csharp-cubic/bin/Debug/Cubic-SDK.${CSHARP_RELEASE_VERSION}.nupkg \
		-k ${NUGET_API_KEY} \
		-s https://api.nuget.org/v3/index.json

docs: ${DOCS_OUTDIR}/autogen-doc-cubic-proto.md

${GO_OUTDIR}/cubic.pb.go: cubic.proto
	mkdir -p ${GO_OUTDIR}
	protoc -I . -I ${PROTOINC} --go_out=plugins=grpc:"${GO_OUTDIR}" $<

# grpc-gateway assumes that the gateway package lives in the same package as the
# main proto package.  However, the gateway code is not necessary for clients,
# and hence we move it to a separate package (gw) and the generated file needs
# to be edited so it can use cubicpb as an external package.  There is a test in
# go-cubic/client_test.go that verifies that this modified package still builds
# and serves the gateway.
${GO_OUTDIR}/gw/cubic.pb.gw.go: cubic.proto
	mkdir -p ${GO_OUTDIR}/gw
	protoc -I . -I ${PROTOINC} --grpc-gateway_out=logtostderr=true:${GO_OUTDIR}/gw $<
	sed -i -E "s/package cubicpb/package gw/g" $@
	sed -i -E "s/Package cubicpb is/Package gw is/g" $@
	sed -i -E "s|import \(|& \"github.com/cobaltspeech/sdk-cubic/grpc/go-cubic/cubicpb\"|" $@
	sed -i -E "s/protoReq /&cubicpb\./g" $@
	sed -i -E "s/(protoReq )cubicpb\.([A-Za-z]+\.)/\1\2/g" $@
	sed -i -E "s/, client /, client cubicpb./g" $@
	sed -i -E "s/Client /, client cubicpb./g" $@
	sed -i -E "s/[^(]*Client, runtime/cubicpb.&/" $@
	sed -i -E "s/New[A-Za-z]*Client/cubicpb.&/" $@
	cd $(dir $@) && go fmt $(notdir $@)

${PY_OUTDIR}/cubic_pb2.py: cubic.proto
	mkdir -p ${PY_OUTDIR}
	python -m grpc_tools.protoc -I . -I ${PROTOINC} --python_out=${PY_OUTDIR} $<

${PY_OUTDIR}/cubic_pb2_grpc.py: cubic.proto
	mkdir -p ${PY_OUTDIR}
	python -m grpc_tools.protoc -I . -I ${PROTOINC} --grpc_python_out=${PY_OUTDIR} $<

go-test:
	cd go-cubic && go test
	cd go-cubic/cubicpb/gw && go test

py-test:
	cd py-cubic/cubic && python test_client.py
	cd py-cubic/cubic && python test_tls.py

${DOCS_OUTDIR}/autogen-doc-cubic-proto.md: cubic.proto doc.md.tmpl
	protoc -I . -I ${PROTOINC} --doc_out=${DOCS_OUTDIR} --doc_opt=doc.md.tmpl,autogen-doc-cubic-proto.md cubic.proto

