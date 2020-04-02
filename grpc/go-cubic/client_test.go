// Copyright (2019) Cobalt Speech and Language Inc.

// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package cubic_test

import (
	"bytes"
	"context"
	"fmt"
	"io"
	"net"
	"testing"
	"time"

	"github.com/cobaltspeech/sdk-cubic/grpc/go-cubic"
	"github.com/cobaltspeech/sdk-cubic/grpc/go-cubic/cubicpb"
	"github.com/golang/protobuf/proto"
	"github.com/golang/protobuf/ptypes/empty"
	"google.golang.org/grpc"
)

// MockCubicServer implements cubicpb.CubicServer so we can use it to test our
// client.  The implementation is interleaved with the appropriate test
// functions below.
type MockCubicServer struct{}

// Test Version

var ExpectedVersionResponse = &cubicpb.VersionResponse{Cubic: "none", Server: "test"}

func (s *MockCubicServer) Version(ctx context.Context, e *empty.Empty) (*cubicpb.VersionResponse, error) {
	return ExpectedVersionResponse, nil
}

func TestVersion(t *testing.T) {
	svr, port, err := setupGRPCServer()
	defer svr.Stop()

	if err != nil {
		t.Fatalf("could not set up testing server: %v", err)
	}

	c, err := cubic.NewClient(fmt.Sprintf("localhost:%d", port), cubic.WithInsecure())
	if err != nil {
		t.Errorf("could not create client: %v", err)
	}
	defer c.Close()

	v, err := c.Version(context.Background())
	if err != nil {
		t.Errorf("did not expect error in version; got %v", err)
	}

	if !proto.Equal(v, ExpectedVersionResponse) {
		t.Errorf("version failed; got %v, want %v", v, ExpectedVersionResponse)
	}
}

// Test ListModels

var ExpectedListModelsResponse = &cubicpb.ListModelsResponse{
	Models: []*cubicpb.Model{
		&cubicpb.Model{Id: "1"},
	},
}

func (s *MockCubicServer) ListModels(ctx context.Context, r *cubicpb.ListModelsRequest) (*cubicpb.ListModelsResponse, error) {
	return ExpectedListModelsResponse, nil
}

func TestListModels(t *testing.T) {
	svr, port, err := setupGRPCServer()
	defer svr.Stop()

	if err != nil {
		t.Fatalf("could not set up testing server: %v", err)
	}

	c, err := cubic.NewClient(fmt.Sprintf("localhost:%d", port), cubic.WithInsecure())
	if err != nil {
		t.Errorf("could not create client: %v", err)
	}
	defer c.Close()

	m, err := c.ListModels(context.Background())
	if err != nil {
		t.Errorf("did not expect error in listmodels; got %v", err)
	}

	if !proto.Equal(m, ExpectedListModelsResponse) {
		t.Errorf("listmodels failed; got %v, want %v", m, ExpectedListModelsResponse)
	}
}

// Test CompileContext

var ExpectedCompileContextResponse = &cubicpb.CompileContextResponse{
	Context: &cubicpb.CompiledContext{
		Data: []byte{0x00, 0x01, 0x02, 0x03}},
}

func (s *MockCubicServer) CompileContext(ctx context.Context, r *cubicpb.CompileContextRequest) (*cubicpb.CompileContextResponse, error) {
	return ExpectedCompileContextResponse, nil
}

func TestCompileContext(t *testing.T) {
	svr, port, err := setupGRPCServer()
	defer svr.Stop()

	if err != nil {
		t.Fatalf("could not set up testing server: %v", err)
	}

	c, err := cubic.NewClient(fmt.Sprintf("localhost:%d", port), cubic.WithInsecure())
	if err != nil {
		t.Errorf("could not create client: %v", err)
	}
	defer c.Close()

	phrases := map[string]float32{
		"COVID":   0.0,
		"COVFEFE": 0.0,
		"NAMBIA":  0.0,
	}
	m, err := c.CompileContext(context.Background(), "1", "oov", phrases)
	if err != nil {
		t.Errorf("did not expect error in compilecontext; got %v", err)
	}

	if !proto.Equal(m, ExpectedCompileContextResponse) {
		t.Errorf("compilecontext failed; got %v, want %v", m, ExpectedCompileContextResponse)
	}
}

// Test Recognize

var ExpectedRecognizeResponse = &cubicpb.RecognitionResponse{
	Results: []*cubicpb.RecognitionResult{
		&cubicpb.RecognitionResult{
			Alternatives: []*cubicpb.RecognitionAlternative{
				&cubicpb.RecognitionAlternative{
					Transcript: "This is a test",
				},
			},
		},
	},
}

func (s *MockCubicServer) Recognize(ctx context.Context, r *cubicpb.RecognizeRequest) (*cubicpb.RecognitionResponse, error) {
	if r.Config.AudioEncoding != cubicpb.RecognitionConfig_RAW_LINEAR16 {
		return nil, fmt.Errorf("audio encoding not supported")
	}

	return ExpectedRecognizeResponse, nil
}

func TestRecognize(t *testing.T) {
	svr, port, err := setupGRPCServer()
	defer svr.Stop()

	if err != nil {
		t.Fatalf("could not set up testing server: %v", err)
	}

	c, err := cubic.NewClient(fmt.Sprintf("localhost:%d", port), cubic.WithInsecure())
	if err != nil {
		t.Errorf("could not create client: %v", err)
	}
	defer c.Close()

	_, err = c.Recognize(context.Background(),
		&cubicpb.RecognitionConfig{
			AudioEncoding: cubicpb.RecognitionConfig_MP3,
		},
		bytes.NewReader([]byte{}))

	if err == nil {
		t.Errorf("expected error with MP3 encoding, got none")
	}

	resp, err := c.Recognize(context.Background(), &cubicpb.RecognitionConfig{}, bytes.NewReader([]byte{}))
	if err != nil {
		t.Errorf("did not expect error in recognize; got %v", err)
	}

	if !proto.Equal(resp, ExpectedRecognizeResponse) {
		t.Errorf("recognize failed; got %v, want %s", resp, ExpectedVersionResponse)
	}
}

// Test Streaming Recognize

var ExpectedStreamingRecognizeResponse = &cubicpb.RecognitionResponse{
	Results: []*cubicpb.RecognitionResult{
		ExpectedRecognizeResponse.Results[0],
		ExpectedRecognizeResponse.Results[0],
	},
}

func (s *MockCubicServer) StreamingRecognize(stream cubicpb.Cubic_StreamingRecognizeServer) error {
	// verify that first message is config
	msg, err := stream.Recv()
	if err != nil {
		return fmt.Errorf("streamingrecognize failed: missing first message")
	}

	if msg.GetConfig() == nil {
		return fmt.Errorf("streamingrecognize failed: first message should be a config message")
	}

	if msg.GetConfig().GetModelId() == "test-nil" {
		// send a response with a nil result.  grpc does not allow this, and
		// this should fail.  but we test this to ensure that our client does
		// not need to check for nil before using the object.
		err = stream.Send(&cubicpb.RecognitionResponse{Results: []*cubicpb.RecognitionResult{nil}})
		return err
	}

	// verify that remaining messages are audio messages, and there are at least three of those.
	count := 0
	for {
		req, err := stream.Recv()
		if err == io.EOF {
			break
		}

		if err != nil {
			return fmt.Errorf("streamingrecognize failed: %v", err)
		}

		if req.GetAudio() == nil {
			return fmt.Errorf("streamingrecognize failed: all messages after the first should be audio messages")
		}

		count++
	}

	if count < 3 {
		return fmt.Errorf("streamingrecognize failed: expecting at least 3 test audio messages, got %d", count)
	}

	if err := stream.Send(ExpectedStreamingRecognizeResponse); err != nil {
		return fmt.Errorf("streamingrecognize2 failed: %v", err)
	}

	return nil
}

func TestStreamingRecognize(t *testing.T) {
	svr, port, err := setupGRPCServer()
	defer svr.Stop()

	if err != nil {
		t.Fatalf("could not set up testing server: %v", err)
	}

	c, err := cubic.NewClient(fmt.Sprintf("localhost:%d", port), cubic.WithInsecure())
	if err != nil {
		t.Errorf("could not create client: %v", err)
	}
	defer c.Close()

	var got *cubicpb.RecognitionResponse

	handleResult := func(resp *cubicpb.RecognitionResponse) {
		got = resp
	}

	audio := make([]byte, 10*4096) // all zeros

	err = c.StreamingRecognize(context.Background(), &cubicpb.RecognitionConfig{}, bytes.NewReader(audio), handleResult)
	if err != nil {
		t.Errorf("did not expect error in streamingrecognize; got %v", err)
	}

	if !proto.Equal(got, ExpectedStreamingRecognizeResponse) {
		t.Errorf("streamingrecognize failed: got %v; want %v", got, ExpectedStreamingRecognizeResponse)
	}

	// Now check that the server will never send a nil message.
	err = c.StreamingRecognize(context.Background(), &cubicpb.RecognitionConfig{ModelId: "test-nil"}, bytes.NewReader(audio), handleResult)
	if err == nil {
		t.Errorf("streamingrecognize should have failed when server sent a nil message, but instead succeeded")
	}

}

// Test Streaming Buffer Size Option
func TestStreamingBufSize(t *testing.T) {
	svr, port, err := setupGRPCServer()
	defer svr.Stop()

	if err != nil {
		t.Fatalf("could not set up testing server: %v", err)
	}

	_, err = cubic.NewClient(fmt.Sprintf("localhost:%d", port), cubic.WithInsecure(), cubic.WithStreamingBufferSize(0))
	if err == nil {
		t.Errorf("client creation with streaming buffer size 0, want failure, got success")
	}

	c, err := cubic.NewClient(fmt.Sprintf("localhost:%d", port), cubic.WithInsecure(), cubic.WithStreamingBufferSize(1))
	if err != nil {
		t.Errorf("client creation with streaming buffer size 1, want success, got %v", err)
	}
	defer c.Close()

}

func TestClient_InvalidURL(t *testing.T) {
	if _, err := cubic.NewClient(fmt.Sprintf("wrong_localhost:2727"), cubic.WithInsecure(),
		cubic.WithConnectTimeout(200*time.Millisecond)); err == nil {
		t.Errorf("connecting to invalid server: want error, got nil")
	}
}

func TestClient_Insecure(t *testing.T) {
	svr, port, err := setupGRPCServer() // server without TLS
	defer svr.Stop()

	if err != nil {
		t.Fatalf("could not set up testing server: %v", err)
	}

	// connection with TLS
	if _, err := cubic.NewClient(fmt.Sprintf("localhost:%d", port),
		cubic.WithConnectTimeout(200*time.Millisecond)); err == nil {
		t.Errorf("tls connection to non-tls server: want error, got nil")
	}
}

func setupGRPCServer() (*grpc.Server, int, error) {
	lis, err := net.Listen("tcp", ":0")
	if err != nil {
		return nil, 0, err
	}

	s := grpc.NewServer()
	cubicpb.RegisterCubicServer(s, &MockCubicServer{})
	go func() { _ = s.Serve(lis) }()
	return s, lis.Addr().(*net.TCPAddr).Port, nil
}
