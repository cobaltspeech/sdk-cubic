package gw_test

import (
	"context"
	"fmt"
	"net"
	"net/http"
	"testing"
	"time"

	"github.com/cobaltspeech/sdk-cubic/grpc/go-cubic/cubicpb"
	"github.com/cobaltspeech/sdk-cubic/grpc/go-cubic/cubicpb/gw"
	"github.com/golang/protobuf/ptypes/empty"
	"github.com/grpc-ecosystem/grpc-gateway/runtime"
	"google.golang.org/grpc"
)

// Test that the generated gateway code builds correctly.
//
// The Makefile used for generating the grpc gateway code also changes the
// package name in that code, and in doing so, it uses regular expressions to
// replace patterns.  If the protoc-gen-grpc-gateway code changes the structure
// of the original generated file, our patches may need updating, otherwise the
// final generated code might be broken.  We therefore check here that we can
// indeed build that code and serve it with a HTTP mux, something a server
// implementation would do.
func TestGateway(t *testing.T) {
	svr, port, err := setupGRPCServer()
	defer svr.Stop()

	if err != nil {
		t.Errorf("could not set up testing server: %v", err)
	}

	gwmux := runtime.NewServeMux(runtime.WithMarshalerOption(
		runtime.MIMEWildcard,
		&runtime.JSONPb{OrigName: true, EmitDefaults: true}))

	opts := []grpc.DialOption{grpc.WithInsecure()}
	err = gw.RegisterCubicHandlerFromEndpoint(context.Background(), gwmux, fmt.Sprintf("localhost:%d", port), opts)
	if err != nil {
		t.Errorf("could not setup http gateway: %v", err)
	}

	mux := http.NewServeMux()
	mux.Handle("/api/", gwmux)

	httpsvr := &http.Server{
		Addr:    fmt.Sprintf(":0"),
		Handler: mux,
	}

	go func() {
		if err := httpsvr.ListenAndServe(); err != nil {
			if err != http.ErrServerClosed {
				t.Errorf("unable to start grpc gateway: %v", err)
			}
		}
	}()

	<-time.After(1 * time.Second)
	_ = httpsvr.Shutdown(context.Background())
}

type stubServer struct{}

func (s *stubServer) Version(ctx context.Context, e *empty.Empty) (*cubicpb.VersionResponse, error) {
	return nil, fmt.Errorf("not implemented")
}
func (s *stubServer) ListModels(ctx context.Context, r *cubicpb.ListModelsRequest) (*cubicpb.ListModelsResponse, error) {
	return nil, fmt.Errorf("not implemented")
}
func (s *stubServer) Recognize(ctx context.Context, r *cubicpb.RecognizeRequest) (*cubicpb.RecognitionResponse, error) {
	return nil, fmt.Errorf("not implemented")
}
func (s *stubServer) StreamingRecognize(stream cubicpb.Cubic_StreamingRecognizeServer) error {
	return fmt.Errorf("not implemented")
}

func setupGRPCServer() (*grpc.Server, int, error) {
	lis, err := net.Listen("tcp", ":0")
	if err != nil {
		return nil, 0, err
	}

	s := grpc.NewServer()
	cubicpb.RegisterCubicServer(s, &stubServer{})
	go func() { _ = s.Serve(lis) }()
	return s, lis.Addr().(*net.TCPAddr).Port, nil
}
