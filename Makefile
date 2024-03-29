# Copyright (2019) Cobalt Speech and Language Inc.

.PHONY: all deps gen depsbin deps-protoc clean
all: gen

PROTOC_VERSION := 3.11.4

PROTOC_GEN_DOC_VERSION := 1.3.1
PROTOC_GEN_DOC_GO_VERSION := 1.12.6

PROTOC_GEN_GO_VERSION := 1.4.0
PROTOC_GEN_GRPC_GATEWAY_VERSION := 1.14.4

PY_GRPC_VERSION := 1.28.1
PY_GRPCIO_VERSION := 1.31.0 # 1.32.0 uses boring SSL and some tls tests fail -- https://github.com/grpc/grpc/issues/24252
PY_GOOGLEAPIS_VERSION := 1.51.0
PY_PROTOBUF_VERSION := 3.20
SWIFT_GRPC_VERSION := 1.6.1

SHELL := /usr/bin/env bash

TOP := $(shell pwd)

DEPSSWIFT := ${TOP}/deps/swift
DEPSBIN := ${TOP}/deps/bin
DEPSGO := ${TOP}/deps/go
DEPSTMP := ${TOP}/deps/tmp

$(shell mkdir -p $(DEPSBIN) $(DEPSGO) $(DEPSTMP) $(DEPSSWIFT))

DEPSVENV := ${TOP}/deps/venv

SWIFT_BIN := $(shell which swift)
ifeq ($(SWIFT_BIN),)
 $(error "`swift` was not found in your PATH, please download and install swift from https://swift.org/download to continue")
endif

export PATH := ${DEPSBIN}:${DEPSGO}/bin:$(PATH)
deps: deps-protoc deps-gendoc deps-gengo deps-gengateway deps-py deps-swift

deps-protoc: ${DEPSBIN}/protoc
${DEPSBIN}/protoc:
	cd ${DEPSBIN}/../ && wget \
		"https://github.com/protocolbuffers/protobuf/releases/download/v$(PROTOC_VERSION)/protoc-$(PROTOC_VERSION)-linux-x86_64.zip" && \
		unzip protoc-$(PROTOC_VERSION)-linux-x86_64.zip && rm -f protoc-$(PROTOC_VERSION)-linux-x86_64.zip

deps-gendoc: ${DEPSBIN}/protoc-gen-doc
${DEPSBIN}/protoc-gen-doc:
	cd ${DEPSBIN} && wget \
		"https://github.com/pseudomuto/protoc-gen-doc/releases/download/v$(PROTOC_GEN_DOC_VERSION)/protoc-gen-doc-$(PROTOC_GEN_DOC_VERSION).linux-amd64.go$(PROTOC_GEN_DOC_GO_VERSION).tar.gz" -O - | tar xz --strip-components=1

deps-gengo: ${DEPSGO}/bin/protoc-gen-go
${DEPSGO}/bin/protoc-gen-go:
	rm -rf $(DEPSTMP)/gengo
	cd $(DEPSTMP) && mkdir gengo && cd gengo && go mod init tmp && GOPATH=${DEPSGO} go get github.com/golang/protobuf/protoc-gen-go@v$(PROTOC_GEN_GO_VERSION)

deps-gengateway: ${DEPSGO}/bin/protoc-gen-grpc-gateway
${DEPSGO}/bin/protoc-gen-grpc-gateway:
	rm -rf $(DEPSTMP)/gengw
	cd $(DEPSTMP) && mkdir gengw && cd gengw && go mod init tmp && GOPATH=${DEPSGO} go get github.com/grpc-ecosystem/grpc-gateway/protoc-gen-grpc-gateway@v$(PROTOC_GEN_GRPC_GATEWAY_VERSION)

deps-py: ${DEPSVENV}/.done
${DEPSVENV}/.done:
	virtualenv -p python3 ${DEPSVENV}
	source ${DEPSVENV}/bin/activate && pip install grpcio==$(PY_GRPCIO_VERSION) grpcio-tools==$(PY_GRPC_VERSION) googleapis-common-protos==$(PY_GOOGLEAPIS_VERSION) protobuf==$(PY_PROTOBUF_VERSION) && deactivate
	touch $@

deps-swift: ${DEPSSWIFT}/.done
${DEPSSWIFT}/.done:
	cd ${DEPSSWIFT} && wget \
		"https://github.com/grpc/grpc-swift/archive/${SWIFT_GRPC_VERSION}.tar.gz" && \
 		tar xzf ${SWIFT_GRPC_VERSION}.tar.gz && \
 		mv grpc-swift-${SWIFT_GRPC_VERSION} bin && \
 		rm -f ${SWIFT_GRPC_VERSION}.tar.gz && \
 		cd ${DEPSSWIFT}/bin && make plugins && \
 		cp protoc-gen-grpc-swift ${DEPSBIN} && \
 		cp protoc-gen-swift ${DEPSBIN}
		touch $@
 
gen: deps 
	@ source ${DEPSVENV}/bin/activate && \
		PROTOINC=${DEPSGO}/pkg/mod/github.com/grpc-ecosystem/grpc-gateway@v$(PROTOC_GEN_GRPC_GATEWAY_VERSION)/third_party/googleapis \
		$(MAKE) -C grpc

clean:
	GOPATH=${DEPSGO} go clean -modcache
	rm -rf deps
