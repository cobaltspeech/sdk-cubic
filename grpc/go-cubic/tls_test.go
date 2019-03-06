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
	"context"
	"crypto/tls"
	"crypto/x509"
	"errors"
	"fmt"
	"net"
	"testing"

	"github.com/cobaltspeech/sdk-cubic/grpc/go-cubic"
	"github.com/cobaltspeech/sdk-cubic/grpc/go-cubic/cubicpb"
	"google.golang.org/grpc"
	"google.golang.org/grpc/credentials"
)

// The TLS certificates below were generated using:
//
// openssl req -new -x509 -newkey rsa:512 -keyout key.pem -out cert.pem -nodes
//
// and are used for testing only. Do not use in production.

var certPem = []byte(`-----BEGIN CERTIFICATE-----
MIIBjjCCATigAwIBAgIJAKafD1cpn4lbMA0GCSqGSIb3DQEBCwUAMCExCzAJBgNV
BAYTAlVTMRIwEAYDVQQDDAlsb2NhbGhvc3QwHhcNMTkwMzA3MDk1NzU3WhcNMTkw
NDA2MDk1NzU3WjAhMQswCQYDVQQGEwJVUzESMBAGA1UEAwwJbG9jYWxob3N0MFww
DQYJKoZIhvcNAQEBBQADSwAwSAJBAMNZXv5BGx4JNz2BPs3T1iolaQ/IEwYMESkA
5JhLA0/sn5ZnTY4NKwGHHbOE731hUbjRf4sJCrinZzbgIp4YUwcCAwEAAaNTMFEw
HQYDVR0OBBYEFC/i040wrY8xt6zYEL9ENs0A+/tqMB8GA1UdIwQYMBaAFC/i040w
rY8xt6zYEL9ENs0A+/tqMA8GA1UdEwEB/wQFMAMBAf8wDQYJKoZIhvcNAQELBQAD
QQC0Om9U+8skETcjfGRSF02TYCVpyBO3ADQnS8BzZ9ucNXc0h9InFi3inM+9POIB
idER5thVQ2XZHVp9XSTwlyvA
-----END CERTIFICATE-----`)

var keyPem = []byte(`-----BEGIN PRIVATE KEY-----
MIIBUwIBADANBgkqhkiG9w0BAQEFAASCAT0wggE5AgEAAkEAw1le/kEbHgk3PYE+
zdPWKiVpD8gTBgwRKQDkmEsDT+yflmdNjg0rAYcds4TvfWFRuNF/iwkKuKdnNuAi
nhhTBwIDAQABAkAs4Y6ambiyOOnRHq2gOmg8/KVbs9rzC7ixk/vxJWLYbJ0OHUzu
06EWqi92+WjApPeq8pN38+43d7g99oTaHN3BAiEA6bqI2mNNqqxvQDQz8HOsjGnN
86lohEAO5RZ0XGfEF6ECIQDV9p/Hi8lHZ42Ea7tvC+vRbrxM+1PPIZAzASQqLHBJ
pwIgas4u030ldKSvgetZALwbUV7YGkyD0ktjgxQrwD46wYECIBl37tL436+hMuY1
UK1KNPrL/JwJOpOoQhrgIubmPUN3AiA+VhvtjyaKbvkuDrWrmd9w3Qs6en3tZPk9
Dx9VwuUOUQ==
-----END PRIVATE KEY-----`)

func setupGRPCServerWithTLS(mutual bool) (*grpc.Server, int, error) {
	cert, err := tls.X509KeyPair(certPem, keyPem)
	if err != nil {
		return nil, 0, err
	}

	lis, err := net.Listen("tcp", ":0")
	if err != nil {
		return nil, 0, err
	}

	tc := &tls.Config{
		Certificates: []tls.Certificate{cert},
	}

	if mutual {
		certPool := x509.NewCertPool()
		if ok := certPool.AppendCertsFromPEM(certPem); !ok {
			return nil, 0, fmt.Errorf("could not set up mutual-tls server: %v", err)
		}
		tc.ClientCAs = certPool
		tc.ClientAuth = tls.RequireAndVerifyClientCert
	}

	s := grpc.NewServer(grpc.Creds(credentials.NewTLS(tc)))
	cubicpb.RegisterCubicServer(s, &MockCubicServer{})
	go s.Serve(lis)
	return s, lis.Addr().(*net.TCPAddr).Port, nil
}

var errConstructorFailed = errors.New("client creation failed")

// we define a few convenience functions to easily test client connections.
//
// testClientConnection takes in the return value of a NewClient() call,
// verifies that the client was created correctly and then returns the error
// returned by the Version method.  The client creation should always succeed.
// The TLS credentials are verified in the actual RPC call, and hence we call
// Version and return the error that ocurred in the call.
//
// shouldFail and shouldSucceed take the error returned from the
// testClientConnection function, verify if there should or should not have been
// an error, and call t.Errorf as necessary.

func testClientConnection(client *cubic.Client, clientErr error) error {
	if clientErr != nil {
		return errConstructorFailed
	}
	defer client.Close()
	_, err := client.Version(context.Background())
	return err
}

func shouldFail(t *testing.T, errorPrefix string, testErr error) {
	if testErr == errConstructorFailed {
		t.Errorf("%s failed: %v", errorPrefix, testErr)
	}

	if testErr == nil {
		t.Errorf("%s should fail, but succeeded", errorPrefix)
	}
}

func shouldSucceed(t *testing.T, errorPrefix string, testErr error) {
	if testErr == errConstructorFailed {
		t.Errorf("%s failed: %v", errorPrefix, testErr)
	}

	if testErr != nil {
		t.Errorf("%s should not fail, but failed: %v", errorPrefix, testErr)
	}
}

// TestServerTLS starts a server with TLS (not mutual) and makes sure only the
// appropriate clients can connect to it.
func TestServerTLS(t *testing.T) {
	svr, port, err := setupGRPCServerWithTLS(false)
	if err != nil {
		t.Errorf("unable to start server: %v", err)
		return
	}
	defer svr.Stop()

	addr := fmt.Sprintf("localhost:%d", port)

	// since the server uses a self-signed certificate, a default client
	// should not be able to call methods, as the certificate can not be
	// validated.
	shouldFail(t, "default client with self-signed server cert",
		testClientConnection(cubic.NewClient(addr, &tls.Config{})))

	// by adding the appropriate CA to the client config, we should be now able to call methods.
	caCertPool := x509.NewCertPool()
	if ok := caCertPool.AppendCertsFromPEM(certPem); !ok {
		t.Errorf("could not add certificate to certpool")
		return
	}

	shouldSucceed(t, "default client with root CA of server",
		testClientConnection(cubic.NewClient(addr, &tls.Config{RootCAs: caCertPool})))

	shouldFail(t, "insecure client with tls server",
		testClientConnection(cubic.NewClientWithInsecure(addr)))
}

// TestMutualTLS starts a server with mutual TLS enabled and makes sure only the
// appropriate clients can connect to it.
func TestMutualTLS(t *testing.T) {
	svr, port, err := setupGRPCServerWithTLS(true)
	if err != nil {
		t.Errorf("unable to start server: %v", err)
		return
	}
	defer svr.Stop()

	addr := fmt.Sprintf("localhost:%d", port)

	// define a new "fake" certificate and key that can be used in testing
	// credential validation

	fakeCertPem := []byte(`-----BEGIN CERTIFICATE-----
MIIBZjCCARCgAwIBAgIJAMn7swGSO8/UMA0GCSqGSIb3DQEBCwUAMA0xCzAJBgNV
BAYTAlVTMB4XDTE5MDMwNzEyMjAxM1oXDTE5MDQwNjEyMjAxM1owDTELMAkGA1UE
BhMCVVMwXDANBgkqhkiG9w0BAQEFAANLADBIAkEA2fY7pvsLKU0SKjYEat8lbU8n
OFVicZSuqKVuE/n/0PW69MLAiA/8nNgX/RGads1udSNe1LptlYH8scE76qi4VwID
AQABo1MwUTAdBgNVHQ4EFgQUEN5OTssGhgt04gmfYEPGhBPRVLwwHwYDVR0jBBgw
FoAUEN5OTssGhgt04gmfYEPGhBPRVLwwDwYDVR0TAQH/BAUwAwEB/zANBgkqhkiG
9w0BAQsFAANBALF4l2Szu9agWyu5LtOxd4I7eyMfyPZhqa0Lff5j/VdyTjIVDrgo
XKJaldxh9WV0fSXLIqczEqmAQbmbj/CTvz8=
-----END CERTIFICATE-----`)

	fakeKeyPem := []byte(`-----BEGIN PRIVATE KEY-----
MIIBVQIBADANBgkqhkiG9w0BAQEFAASCAT8wggE7AgEAAkEA2fY7pvsLKU0SKjYE
at8lbU8nOFVicZSuqKVuE/n/0PW69MLAiA/8nNgX/RGads1udSNe1LptlYH8scE7
6qi4VwIDAQABAkBduVEbU3YQM3DtL78kiYHZiCDQS38CYjHcmQ5Fjsne+xBcDJxZ
8dtnCu4/OVNUevTGAObATIQBW5eBieUf8tZRAiEA7F2Bx/p+N8fzjcyCifMTUyOk
qpRXPgwc/wQ4KfdvwTkCIQDsEVztoPr1wnnmBRFhZsXzOvzGpSMczm2x3EmvIA2W
DwIgIvgym0OUKOyMPA5lwcMUuNgtJI+N2MAyCgi1xn+1KQECIQC/YXgwIgEy+n4u
r88eYt56SUkilkB4GxatSgTmmBrLmwIhAJ66U/pu4nL2HIhGcUdxawRC0DJRwRKc
B6KD9XmVFWXX
-----END PRIVATE KEY-----`)

	// mutual tls should work with appropriate certificates
	shouldSucceed(t, "mutual tls with correct keys",
		testClientConnection(cubic.NewClientWithMutualTLS(addr, certPem, keyPem, certPem)))

	// mutual tls with correct cert but wrong CA should fail (client can not validate the server)
	shouldFail(t, "mutual tls with wrong CA cert",
		testClientConnection(cubic.NewClientWithMutualTLS(addr, certPem, keyPem, fakeCertPem)))

	// mutual tls with wrong cert but correct CA should fail (server can not validate the client)
	shouldFail(t, "mutual tls with wrong client cert",
		testClientConnection(cubic.NewClientWithMutualTLS(addr, fakeCertPem, fakeKeyPem, certPem)))
}
