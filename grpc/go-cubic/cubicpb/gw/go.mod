module github.com/cobaltspeech/sdk-cubic/grpc/go-cubic/cubicpb/gw

go 1.12

require (
	github.com/cobaltspeech/sdk-cubic/grpc/go-cubic v1.0.1
	github.com/golang/protobuf v1.3.0
	github.com/grpc-ecosystem/grpc-gateway v1.8.5
	google.golang.org/grpc v1.19.0
)

replace github.com/cobaltspeech/sdk-cubic/grpc/go-cubic => ../../
