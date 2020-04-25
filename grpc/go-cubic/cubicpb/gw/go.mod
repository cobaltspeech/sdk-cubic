module github.com/cobaltspeech/sdk-cubic/grpc/go-cubic/cubicpb/gw

go 1.12

require (
	github.com/cobaltspeech/sdk-cubic/grpc/go-cubic v1.5.0
	github.com/golang/protobuf v1.4.0
	github.com/grpc-ecosystem/grpc-gateway v1.14.4
	golang.org/x/net v0.0.0-20200324143707-d3edc9973b7e // indirect
	golang.org/x/sys v0.0.0-20200413165638-669c56c373c4 // indirect
	google.golang.org/genproto v0.0.0-20200417142217-fb6d0575620b // indirect
	google.golang.org/grpc v1.28.1
)

replace github.com/cobaltspeech/sdk-cubic/grpc/go-cubic => ../../
