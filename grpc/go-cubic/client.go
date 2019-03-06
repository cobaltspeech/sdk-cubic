// Copyright (2019) Cobalt Speech and Language Inc.

// Package cubic provides for interacting with an instance of cubic server using
// GRPC for performing speech recognition.
package cubic

import (
	"context"
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
// server.
func NewClient(addr string) (*Client, error) {
	conn, err := grpc.Dial(addr)
	if err != nil {
		return nil, fmt.Errorf("unable to create a client: %s", err)
	}

	return &Client{
		conn:   conn,
		client: cubicpb.NewCubicClient(conn),
	}, nil
}

// NewClientWithInsecure creates a new Cubic Client to use the provided addr as
// the cubic server.
//
// Transport layer security is disabled in the client.  This client will only
// work if the cubic server provided to you also has TLS disabled.
func NewClientWithInsecure(addr string) (*Client, error) {
	conn, err := grpc.Dial(addr, grpc.WithInsecure())
	if err != nil {
		return nil, fmt.Errorf("unable to create a client: %s", err)
	}

	return &Client{
		conn:   conn,
		client: cubicpb.NewCubicClient(conn),
	}, nil
}

// NewClientWithCertFile creates a new Cubic Client to use the provided addr as
// the cubic server.
//
// Transport layer security is enabled and configured to use the provided
// certificate file.  This certificate will be validated by the server, thus
// providing mutually-authenticated TLS.
func NewClientWithCertFile(addr string, certfile string) (*Client, error) {
	tc, err := credentials.NewClientTLSFromFile(certfile, "")
	if err != nil {
		return nil, fmt.Errorf("unable to create a client: %s", err)
	}

	conn, err := grpc.Dial(addr, grpc.WithTransportCredentials(tc))
	if err != nil {
		return nil, fmt.Errorf("unable to create a client: %s", err)
	}

	return &Client{
		conn:   conn,
		client: cubicpb.NewCubicClient(conn),
	}, nil
}

// Close closes the connection to the API service.  The user should only invoke
// this when the client is no longer needed.
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

// RecognitionResultHandler is a function that takes one result returned by the
// cubic server and handles it as required.  This function is called when the
// `StreamingRecognize` method is running, as results are received from cubic
// server.  This function should not block.
type RecognitionResultHandler func(*cubicpb.RecognitionResult)

// StreamingRecognize uses the bidirectional streaming API for performing speech
// recognition.  It sets up recognition using the given cfg.
//
// Data is read from the given audio reader into a buffer of the specified size
// and streamed to cubic server.  If bufsize is set to zero, a default value is
// chosen by this package.
//
// As results are sent by the cubic server, they will be sent to the provided
// resultHandler.
//
// If an error occurs: either in reading the audio, or in streaming it to the
// server, it will be returned.
//
// This function returns only after all results have been passed to the
// resultHandler.
func (c *Client) StreamingRecognize(ctx context.Context,
	cfg *cubicpb.RecognitionConfig, audio io.Reader,
	bufsize uint32, resultHandler RecognitionResultHandler) error {

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
		sendaudio(stream, cfg, audio, bufsize, errch)
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

		for i := range in.Results {
			resultHandler(in.Results[i])
		}
	}

	wg.Wait()

	select {
	case err := <-errch:
		return fmt.Errorf("streaming recognition failed: %v", err)
	default:
		return nil
	}
}

// sendaudio sends audio to a stream.
func sendaudio(stream cubicpb.Cubic_StreamingRecognizeClient,
	cfg *cubicpb.RecognitionConfig, audio io.Reader,
	bufsize uint32, errch chan<- error) {

	if err := stream.Send(&cubicpb.StreamingRecognizeRequest{
		Request: &cubicpb.StreamingRecognizeRequest_Config{Config: cfg},
	}); err != nil {
		errch <- err
		// if this failed, we don't need to CloseSend
		return
	}

	buf := make([]byte, bufsize)
	for {
		n, err := audio.Read(buf)
		if n > 0 {
			if err2 := stream.Send(&cubicpb.StreamingRecognizeRequest{
				Request: &cubicpb.StreamingRecognizeRequest_Audio{
					Audio: &cubicpb.RecognitionAudio{Data: buf[:n]},
				},
			}); err2 != nil {
				errch <- err2
				// if this failed, we don't need to CloseSend
				return
			}
		}

		if err != nil {
			// err could be io.EOF, or some other error reading from
			// audio.  In any case, we need to CloseSend, send the
			// appropriate error to errch and return from the function
			if err2 := stream.CloseSend(); err2 != nil {
				errch <- err2
				return
			}
			if err != io.EOF {
				errch <- err
			}
			return

		}
	}
}
