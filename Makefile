# Copyright (2019) Cobalt Speech and Language Inc.

.PHONY: all deps gen depsbin deps-protoc clean
all: gen

SHELL := /bin/bash

TOP := $(shell pwd)

DEPSBIN := ${TOP}/deps/bin
DEPSGO := ${TOP}/deps/go
DEPSTMP := ${TOP}/deps/tmp
$(shell mkdir -p $(DEPSBIN) $(DEPSGO) $(DEPSTMP))

DEPSVENV := ${TOP}/deps/venv

export PATH := ${DEPSBIN}:${DEPSGO}/bin:$(PATH)
deps: deps-protoc deps-hugo deps-gendoc deps-gengo deps-gengateway deps-py

deps-protoc: ${DEPSBIN}/protoc
${DEPSBIN}/protoc:
	cd ${DEPSBIN}/../ && wget \
		"https://github.com/protocolbuffers/protobuf/releases/download/v3.7.1/protoc-3.7.1-linux-x86_64.zip" && \
		unzip protoc-3.7.1-linux-x86_64.zip && rm -f protoc-3.7.1-linux-x86_64.zip

deps-hugo: ${DEPSBIN}/hugo
${DEPSBIN}/hugo:
	cd ${DEPSBIN} && wget \
		"https://github.com/gohugoio/hugo/releases/download/v0.55.4/hugo_0.55.4_Linux-64bit.tar.gz" -O - | tar xz hugo

deps-gendoc: ${DEPSBIN}/protoc-gen-doc
${DEPSBIN}/protoc-gen-doc:
	cd ${DEPSBIN} && wget \
		"https://github.com/pseudomuto/protoc-gen-doc/releases/download/v1.3.0/protoc-gen-doc-1.3.0.linux-amd64.go1.11.2.tar.gz" -O - | tar xz --strip-components=1

deps-gengo: ${DEPSGO}/bin/protoc-gen-go
${DEPSGO}/bin/protoc-gen-go:
	rm -rf $(DEPSTMP)/gengo
	cd $(DEPSTMP) && mkdir gengo && cd gengo && go mod init tmp && GOPATH=${DEPSGO} go get github.com/golang/protobuf/protoc-gen-go@v1.3.1

deps-gengateway: ${DEPSGO}/bin/protoc-gen-grpc-gateway
${DEPSGO}/bin/protoc-gen-grpc-gateway:
	rm -rf $(DEPSTMP)/gengw
	cd $(DEPSTMP) && mkdir gengw && cd gengw && go mod init tmp && GOPATH=${DEPSGO} go get github.com/grpc-ecosystem/grpc-gateway/protoc-gen-grpc-gateway@v1.9.0

deps-py: ${DEPSVENV}/.done
${DEPSVENV}/.done:
	virtualenv -p python3 ${DEPSVENV}
	source ${DEPSVENV}/bin/activate && pip install grpcio-tools==1.20.0 googleapis-common-protos==1.5.9 && deactivate
	touch $@

gen: deps
	@ source ${DEPSVENV}/bin/activate && \
		PROTOINC=${DEPSGO}/pkg/mod/github.com/grpc-ecosystem/grpc-gateway@v1.9.0/third_party/googleapis \
		$(MAKE) -C grpc
	@ pushd docs-src && hugo -d ../docs && popd

clean:
	GOPATH=${DEPSGO} go clean -modcache
	rm -rf deps
