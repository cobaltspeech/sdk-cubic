# Copyright (2019) Cobalt Speech and Language Inc.

.PHONY: all go go-test docs

all: go docs

GO_OUTDIR=go-cubic/cubicpb
DOCS_OUTDIR=../docs-src/content/protobuf

go: ${GO_OUTDIR}/cubic.pb.go ${GO_OUTDIR}/gw/cubic.pb.gw.go go-test
docs: ${DOCS_OUTDIR}/autogen-doc-cubic-proto.md

${GO_OUTDIR}/cubic.pb.go: cubic.proto
	mkdir -p ${GO_OUTDIR}
	protoc -I . -I ${GOPATH}/src/github.com/grpc-ecosystem/grpc-gateway/third_party/googleapis --go_out=plugins=grpc:"${GO_OUTDIR}" $<

# grpc-gateway assumes that the gateway package lives in the same package as the
# main proto package.  However, the gateway code is not necessary for clients,
# and hence we move it to a separate package (gw) and the generated file needs
# to be edited so it can use cubicpb as an external package.  There is a test in
# go-cubic/client_test.go that verifies that this modified package still builds
# and serves the gateway.
${GO_OUTDIR}/gw/cubic.pb.gw.go: cubic.proto
	mkdir -p ${GO_OUTDIR}/gw
	protoc -I . -I ${GOPATH}/src/github.com/grpc-ecosystem/grpc-gateway/third_party/googleapis --grpc-gateway_out=logtostderr=true:${GO_OUTDIR}/gw $<
	sed -i -E "s/package cubicpb/package gw/g" $@
	sed -i -E "s/Package cubicpb is/Package gw is/g" $@
	sed -i -E "s|import \(|& \"github.com/cobaltspeech/sdk-cubic/grpc/go-cubic/cubicpb\"|" $@
	sed -i -E "s/protoReq /&cubicpb\./g" $@
	sed -i -E "s/(protoReq )cubicpb\.([A-Za-z]+\.)/\1\2/g" $@
	sed -i -E "s/, client /, client cubicpb./g" $@
	sed -i -E "s/Client /, client cubicpb./g" $@
	sed -i -E "s/[^(]*Client, runtime/cubicpb.&/" $@
	sed -i -E "s/New[A-Za-z]*Client/cubicpb.&/" $@
	go fmt $@

go-test:
	cd go-cubic && go test

${DOCS_OUTDIR}/autogen-doc-cubic-proto.md: cubic.proto doc.md.tmpl
	protoc -I . -I ${GOPATH}/src/github.com/grpc-ecosystem/grpc-gateway/third_party/googleapis --doc_out=${DOCS_OUTDIR} --doc_opt=doc.md.tmpl,autogen-doc-cubic-proto.md cubic.proto

