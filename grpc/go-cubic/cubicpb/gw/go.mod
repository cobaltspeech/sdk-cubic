module github.com/cobaltspeech/sdk-cubic/grpc/go-cubic/cubicpb/gw

go 1.12

require (
	github.com/cobaltspeech/sdk-cubic/grpc/go-cubic v1.6.4
	github.com/golang/protobuf v1.5.2
	github.com/grpc-ecosystem/grpc-gateway v1.16.0
	google.golang.org/grpc v1.36.1
)

replace github.com/cobaltspeech/sdk-cubic/grpc/go-cubic => ../../
