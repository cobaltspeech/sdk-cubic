module github.com/cobaltspeech/sdk-cubic/grpc/go-cubic/cubicpb/gw

go 1.12

require (
	github.com/cobaltspeech/sdk-cubic/grpc/go-cubic v1.6.1
	github.com/golang/protobuf v1.4.3
	github.com/grpc-ecosystem/grpc-gateway v1.16.0
	google.golang.org/grpc v1.35.0
)

replace github.com/cobaltspeech/sdk-cubic/grpc/go-cubic => ../../
