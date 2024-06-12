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

	"time"

	"github.com/cobaltspeech/sdk-cubic/grpc/go-cubic/cubicpb"
	"github.com/golang/protobuf/ptypes/empty"
	"google.golang.org/grpc"
	"google.golang.org/grpc/credentials"
)

// Client is an object for interacting with the Cubic GRPC API.
//
// All methods except Close may be called concurrently.
type Client struct {
	conn             *grpc.ClientConn
	cubic            cubicpb.CubicClient
	insecure         bool
	tlscfg           tls.Config
	streamingBufSize uint32
	connectTimeout   time.Duration
}

// NewClient creates a new Client that connects to a Cubic Server listening on
// the provided address.  Transport security is enabled by default.  Use Options
// to override default settings if necessary.
func NewClient(addr string, opts ...Option) (*Client, error) {
	return NewClientWithDialOptions(addr, []grpc.DialOption{}, opts...)
}

// NewClientWithDialOptions creates a new Client that connects to a Cubic Server listening on
// the provided address.  Transport security is enabled by default.  Use Options
// to override default settings if necessary.
// Additionally this factory method enables the integration to provide external DialOptions
func NewClientWithDialOptions(addr string, dialOpt []grpc.DialOption, opts ...Option) (*Client, error) {
	c := Client{}
	c.streamingBufSize = defaultStreamingBufsize
	c.connectTimeout = defaultConnectTimeout

	for _, opt := range opts {
		err := opt(&c)
		if err != nil {
			return nil, fmt.Errorf("unable to create a client: %v", err)
		}
	}

	var dopt grpc.DialOption

	if c.insecure {
		dopt = grpc.WithInsecure()
	} else {
		dopt = grpc.WithTransportCredentials(credentials.NewTLS(&c.tlscfg))
	}

	ctx, cancel := context.WithTimeout(context.Background(), c.connectTimeout)
	defer cancel()

	dialOpt = append(dialOpt, dopt,
		grpc.WithBlock(),
		grpc.WithReturnConnectionError(),
		grpc.FailOnNonTempDialError(true))

	conn, err := grpc.DialContext(ctx, addr, dialOpt...)
	if err != nil {
		return nil, fmt.Errorf("unable to create a client: %v", err)
	}
	c.conn = conn
	c.cubic = cubicpb.NewCubicClient(c.conn)
	return &c, nil
}


// Option configures how we setup the connection with a server.
type Option func(*Client) error

// WithInsecure returns an Option which disables transport security for this
// Client.  Use this when connecting to a non-TLS enabled cubic server, such as
// during debugging.
func WithInsecure() Option {
	return func(c *Client) error {
		c.insecure = true
		return nil
	}
}

// WithServerCert returns an Option which sets up the given PEM certificate as a
// root certificate that can validate the certificate presented by the server we
// are connecting to.  Use this when connecting to an instance of cubic server
// that is using a self-signed certificate.
func WithServerCert(cert []byte) Option {
	return func(c *Client) error {
		caCertPool := x509.NewCertPool()
		if ok := caCertPool.AppendCertsFromPEM(cert); !ok {
			return fmt.Errorf("unable to use given caCert")
		}
		c.tlscfg.RootCAs = caCertPool
		return nil
	}
}

// WithClientCert returns an Option which sets up the given PEM certificate and
// key as the credentials presented by this Client when connecting to a server.
// Use this when setting up mutually authenticated TLS.
func WithClientCert(certPem []byte, keyPem []byte) Option {
	return func(c *Client) error {
		clientCert, err := tls.X509KeyPair(certPem, keyPem)
		if err != nil {
			return err
		}

		c.tlscfg.Certificates = []tls.Certificate{clientCert}
		return nil
	}
}

// WithStreamingBufferSize returns an Option that sets up the buffer size
// (bytes) of each message sent from the Client to the server during streaming
// GRPC calls.  Use this only if Cobalt recommends you to do so.  A value n>0 is
// required.
func WithStreamingBufferSize(n uint32) Option {
	return func(c *Client) error {
		if n == 0 {
			return fmt.Errorf("invalid streaming buffer size of 0")
		}
		c.streamingBufSize = n
		return nil
	}
}

// WithConnectTimeout returns an Option that configures the timeout for
// establishing grpc connection with the server.  Use this only when you are on
// a slow network and when Cobalt recommends you to do so.
func WithConnectTimeout(t time.Duration) Option {
	return func(c *Client) error {
		c.connectTimeout = t
		return nil
	}
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
	return c.cubic.Version(ctx, &empty.Empty{})
}

// ListModels retrieves a list of available speech recognition models
func (c *Client) ListModels(ctx context.Context) (*cubicpb.ListModelsResponse, error) {
	return c.cubic.ListModels(ctx, &cubicpb.ListModelsRequest{})
}

// CompileContext compiles the given list of phrases or words into a compact,
// fast to access form for Cubic, which may later be provided in a `Recognize`
// or `StreamingRecognize` call to aid speech recognition.
//
// The modelID is the unique identifier of the model to compile the context
// information for. The model chosen needs to support context which can be
// verified by checking its ModelAttributes.ContextInfo obtained via
// `ListModels`.
//
// The token is a string allowed by the model being used, such as "name" or
// "airport", that is used to determine the place in the recognition output
// where the provided list of phrases or words may appear. The allowed tokens
// for a given model can be found in its ModelAttributes.ContextInfo obtained
// via the `ListModels` method.
//
// The phrases (or words) are given as a slice of strings.
//
// Optionally, a slice of positive floating numbers, boostValues, may also be
// provided, one for each phrase or word in the phrase list. These values can be
// used to increase the likelihood of the corresponding entry in the phrases by
// setting a higher value for it. The boostValues slice can be set to nil if
// boosting is not required.
//
// If given boostValues, the new probability of the corresponding phrase entry
// becomes (boost + 1.0) * old probability. By default, all provded phrases or
// words are given an equal probability of 1/N, where N = total number of
// phrases or words.The new probabilities are normalized after boosting so that
// they sum to one. This means that if all phrases are given the same boost
// value, they will still have the same default likelihood. This also means that
// the boost value can be any positive value, but for most cases, values between
// 0 to 10 work well. Negative values may be provided but they will be treated
// as 0 (no boost).
func (c *Client) CompileContext(
	ctx context.Context, modelID, token string, phrases []string, boostValues []float32,
) (*cubicpb.CompileContextResponse, error) {

	phraseList := make([]*cubicpb.ContextPhrase, len(phrases))
	if boostValues != nil {
		if len(boostValues) != len(phrases) {
			return nil, fmt.Errorf("len(boostValues) is not the same as len(phrases)")
		}
		for i, phrase := range phrases {
			phraseList[i] = &cubicpb.ContextPhrase{
				Text:  phrase,
				Boost: boostValues[i],
			}
		}
	} else {
		for i, phrase := range phrases {
			phraseList[i] = &cubicpb.ContextPhrase{
				Text:  phrase,
				Boost: 0,
			}
		}
	}
	return c.cubic.CompileContext(ctx, &cubicpb.CompileContextRequest{
		ModelId: modelID,
		Token:   token,
		Phrases: phraseList,
	})
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

	return c.cubic.Recognize(ctx, &cubicpb.RecognizeRequest{
		Config: cfg,
		Audio:  &cubicpb.RecognitionAudio{Data: b},
	})
}

const defaultStreamingBufsize uint32 = 8192
const defaultConnectTimeout = 2 * time.Second

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
// Data is read from the given audio reader into a buffer and streamed to cubic
// server.  The default buffer size may be overridden using Options when
// creating the Client.
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
	handlerFunc RecognitionResponseHandler) error {

	stream, err := c.cubic.StreamingRecognize(ctx)
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
		if err := sendaudio(stream, cfg, audio, c.streamingBufSize); err != nil && err != io.EOF {
			// if sendaudio encountered io.EOF, it's only a
			// notification that the stream has closed.  The actual
			// status will be obtained in a subsequent Recv call, in
			// the other goroutine below.  We therefore only forward
			// non-EOF errors.
			errch <- err
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
				// if we couldn't Send, the stream has
				// encountered an error and we don't need to
				// CloseSend.
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
