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

// Package cubic provides for interacting with an instance of cubic server using
// GRPC for performing speech recognition.
package cubic

import (
	"context"
	"crypto/tls"
	"crypto/x509"
	"fmt"
	"io"
	"io/ioutil"
	"sync"

	"github.com/cobaltspeech/sdk-cubic/grpc/go-cubic/cubicpb"
	"github.com/golang/protobuf/ptypes/empty"
	"google.golang.org/grpc"
	"google.golang.org/grpc/credentials"
)

// Client is an object for interacting with the Cubic GRPC API.
//
// All methods except Close may be called concurrently.
type Client struct {
	conn   *grpc.ClientConn
	client cubicpb.CubicClient
}

// NewClient creates a new Cubic client to use the provided address as the cubic
// server and with the provided tls configuration.  This should be the default
// method you use in production when connecting with a TLS enabled server.
func NewClient(addr string, tc *tls.Config) (*Client, error) {
	conn, err := grpc.Dial(addr, grpc.WithTransportCredentials(credentials.NewTLS(tc)))
	if err != nil {
		return nil, fmt.Errorf("unable to create a client: %v", err)
	}

	return &Client{
		conn:   conn,
		client: cubicpb.NewCubicClient(conn),
	}, nil
}

// NewClientWithInsecure creates a new Cubic Client to use the provided addr as
// the cubic server.  Use this method when working with a non-TLS cubic server,
// such as during debugging.
func NewClientWithInsecure(addr string) (*Client, error) {
	conn, err := grpc.Dial(addr, grpc.WithInsecure())
	if err != nil {
		return nil, fmt.Errorf("unable to create a client: %v", err)
	}

	return &Client{
		conn:   conn,
		client: cubicpb.NewCubicClient(conn),
	}, nil
}

// NewClientWithMutualTLS creates a new Cubic Client to use the provided addr as
// the cubic server.  Use this method if Cobalt has provided you with TLS
// certificates for mutual authentication.
func NewClientWithMutualTLS(addr string, certPem []byte, keyPem []byte, caCert []byte) (*Client, error) {
	clientCert, err := tls.X509KeyPair(certPem, keyPem)
	if err != nil {
		return nil, fmt.Errorf("unable to create a client: %v", err)
	}

	caCertPool := x509.NewCertPool()
	if ok := caCertPool.AppendCertsFromPEM(caCert); !ok {
		return nil, fmt.Errorf("unable to use given caCert: %v", err)
	}

	return NewClient(addr, &tls.Config{
		Certificates: []tls.Certificate{clientCert},
		RootCAs:      caCertPool,
	})
}

// Close closes the connection to the API service.  The user should only invoke
// this when the client is no longer needed.  Pending or in-progress calls to
// other methods may fail with an error if Close is called, and any subsequent
// calls with this client will also fail.
func (c *Client) Close() error {
	return c.conn.Close()
}

// Version queries the server for its version
func (c *Client) Version(ctx context.Context) (*cubicpb.VersionResponse, error) {
	return c.client.Version(ctx, &empty.Empty{})
}

// ListModels retrieves a list of available speech recognition models
func (c *Client) ListModels(ctx context.Context) (*cubicpb.ListModelsResponse, error) {
	return c.client.ListModels(ctx, &cubicpb.ListModelsRequest{})
}

// Recognize performs synchronous speech recognition and returns after all audio
// has been processed.  It is expected that this request be used for short audio
// segments (less than a minute long).  For longer content, the
// `StreamingRecognize` method should be used.
func (c *Client) Recognize(
	ctx context.Context,
	cfg *cubicpb.RecognitionConfig,
	audio io.Reader) (*cubicpb.RecognitionResponse, error) {
	b, err := ioutil.ReadAll(audio)
	if err != nil {
		return nil, fmt.Errorf("unable to read audio: %v", err)
	}

	return c.client.Recognize(ctx, &cubicpb.RecognizeRequest{
		Config: cfg,
		Audio:  &cubicpb.RecognitionAudio{Data: b},
	})
}

const defaultStreamBufsize uint32 = 8192

// RecognitionResponseHandler is a type of callback function that will be called
// when the `StreamingRecognize` method is running.  For each response received
// from cubic server, this method will be called once.  The provided
// RecognitionResponse is guaranteed to be non-nil.  Since this function is
// executed as part of the streaming process, it should preferably return
// quickly and certainly not block.
type RecognitionResponseHandler func(*cubicpb.RecognitionResponse)

// StreamingRecognize uses the bidirectional streaming API for performing speech
// recognition.  It sets up recognition using the given cfg.
//
// Data is read from the given audio reader into a buffer of the specified size
// and streamed to cubic server.  If bufsize is set to zero, a default value is
// chosen by this package.
//
// As results are received from the cubic server, they will be sent to the
// provided handlerFunc.
//
// If any error occurs while reading the audio or sending it to the server, this
// method will immediately exit, returning that error.
//
// This function returns only after all results have been passed to the
// resultHandler.
func (c *Client) StreamingRecognize(ctx context.Context,
	cfg *cubicpb.RecognitionConfig, audio io.Reader,
	bufsize uint32, handlerFunc RecognitionResponseHandler) error {

	if bufsize == 0 {
		bufsize = defaultStreamBufsize
	}

	stream, err := c.client.StreamingRecognize(ctx)
	if err != nil {
		return fmt.Errorf("unable to start streaming recognition: %v", err)
	}

	// There are two concurrent processes going on.  We will create a new
	// goroutine to read audio and stream it to the server.  This goroutine
	// will receive results from the stream.  Errors could occur in both
	// goroutines.  We therefore setup a channel, errch, to hold these
	// errors. Both goroutines are designed to send up to one error, and
	// return immediately. Therefore we use a bufferred channel with a
	// capacity of two.
	errch := make(chan error, 2)

	// start streaming audio in a separate goroutine
	var wg sync.WaitGroup
	wg.Add(1)
	go func() {
		if err := sendaudio(stream, cfg, audio, bufsize); err != nil {
			errch <- nil
		}
		wg.Done()
	}()

	for {
		in, err := stream.Recv()
		if err == io.EOF {
			break
		}
		if err != nil {
			errch <- err
			break
		}

		handlerFunc(in)
	}

	wg.Wait()

	select {
	case err := <-errch:
		// There may be more than one error in the channel, but it is
		// very likely they are related (e.g. connection reset causing
		// both the send and recv to fail) and we therefore return the
		// first error and discard the other.
		return fmt.Errorf("streaming recognition failed: %v", err)
	default:
		return nil
	}
}

// sendaudio sends audio to a stream.
func sendaudio(stream cubicpb.Cubic_StreamingRecognizeClient,
	cfg *cubicpb.RecognitionConfig, audio io.Reader,
	bufsize uint32) error {

	// The first message needs to be a config message, and all subsequent
	// messages must be audio messages.

	// Send the recogniton config
	if err := stream.Send(&cubicpb.StreamingRecognizeRequest{
		Request: &cubicpb.StreamingRecognizeRequest_Config{Config: cfg},
	}); err != nil {
		// if this failed, we don't need to CloseSend
		return err
	}

	// Stream the audio.
	buf := make([]byte, bufsize)
	for {
		n, err := audio.Read(buf)
		if n > 0 {
			if err2 := stream.Send(&cubicpb.StreamingRecognizeRequest{
				Request: &cubicpb.StreamingRecognizeRequest_Audio{
					Audio: &cubicpb.RecognitionAudio{Data: buf[:n]},
				},
			}); err2 != nil {
				// if this failed, we don't need to CloseSend
				return err2
			}
		}

		if err != nil {
			// err could be io.EOF, or some other error reading from
			// audio.  In any case, we need to CloseSend, send the
			// appropriate error to errch and return from the function
			if err2 := stream.CloseSend(); err2 != nil {
				return err2
			}
			if err != io.EOF {
				return err
			}
			return nil

		}
	}
}
