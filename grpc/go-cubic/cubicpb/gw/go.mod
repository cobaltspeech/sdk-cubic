module github.com/cobaltspeech/sdk-cubic/grpc/go-cubic/cubicpb/gw

go 1.12

require (
	github.com/cobaltspeech/sdk-cubic/grpc/go-cubic v1.4.0
	github.com/golang/protobuf v1.3.4
	github.com/grpc-ecosystem/grpc-gateway v1.14.1
	google.golang.org/grpc v1.27.1
)

replace github.com/cobaltspeech/sdk-cubic/grpc/go-cubic => ../../
