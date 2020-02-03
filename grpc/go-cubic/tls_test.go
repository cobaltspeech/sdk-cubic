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
// openssl req -new -x509 -newkey rsa:2048 -days 36500 -keyout key.pem -out cert.pem -nodes
//
// and are used for testing only. Do not use in production.

var certPem = []byte(`-----BEGIN CERTIFICATE-----
MIIDlTCCAn2gAwIBAgIUISO9AdBzEIxv366ruyRniPVrA8AwDQYJKoZIhvcNAQEL
BQAwWTELMAkGA1UEBhMCQVUxEzARBgNVBAgMClNvbWUtU3RhdGUxITAfBgNVBAoM
GEludGVybmV0IFdpZGdpdHMgUHR5IEx0ZDESMBAGA1UEAwwJbG9jYWxob3N0MCAX
DTIwMDExNDE4NTY0OVoYDzIxMTkxMjIxMTg1NjQ5WjBZMQswCQYDVQQGEwJBVTET
MBEGA1UECAwKU29tZS1TdGF0ZTEhMB8GA1UECgwYSW50ZXJuZXQgV2lkZ2l0cyBQ
dHkgTHRkMRIwEAYDVQQDDAlsb2NhbGhvc3QwggEiMA0GCSqGSIb3DQEBAQUAA4IB
DwAwggEKAoIBAQDdF183r5B4ZeM677SYQrf7JWc26eBN60mqOSC18aeRxkeQsgO4
X3Y7wpQRcVdsm/i291otNIOWm4mJLLJFVE87Yq65gH4O4MHxQNlhZ0Bf1J8WsbsF
RHk3LF2rhUBll6cG+Z1OX7mCtmM33znXDFxTf3/DZ5XgeleNG98umeUMg8rHgj2y
UB4nwoMbeJIjk7e5tBQKCCNOYM1Mda1wzrvxo3blXsIFzpxLqQ+tVnYuql9CYjX1
69Nwq+Dsgv6zNWzWMlPTPAKdbOVVvXV2hfQ3LmnuzCv9t/TUdwkdyUMDUkbF+T8v
eD5bMP3k8lYuaNu0YQmbgbKvklK7voaEFte9AgMBAAGjUzBRMB0GA1UdDgQWBBTO
3ItCZrqn4cPtvGiVT1BQ+gzZhDAfBgNVHSMEGDAWgBTO3ItCZrqn4cPtvGiVT1BQ
+gzZhDAPBgNVHRMBAf8EBTADAQH/MA0GCSqGSIb3DQEBCwUAA4IBAQCGSvX5vN2F
siW23KnlurUtNiSoPzwSOwRGzaPcQYc1rdTXfN0F3Hj4qRnVJ+9jl3z6/xQnrgzg
iQ+4bZcnJeebmPI0jMZZXDXdDnp/Ze4klELpG53DzVzGZ7FvENmfNFIEIx6hrT2K
TDrWfZomjYu4Tn5rGAA4TflA9u8AWHYcZDLtjiwuHNFJLY6PdZSJU+OaPJXztRDV
jg/KVCsPH5LLxZy1U175YbWN7nIDvG2/H7o2vQdBs9A8lJ8CEGr2/jXTl8GDyOYN
6uHPipr8Y2rvh/jY8HalYo3x5gXM7AJk4OBh6x/Fcw6L0BmUHrUYvaU8UbwjraKF
PISX1spqbI8a
-----END CERTIFICATE-----`)

var keyPem = []byte(`-----BEGIN PRIVATE KEY-----
MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDdF183r5B4ZeM6
77SYQrf7JWc26eBN60mqOSC18aeRxkeQsgO4X3Y7wpQRcVdsm/i291otNIOWm4mJ
LLJFVE87Yq65gH4O4MHxQNlhZ0Bf1J8WsbsFRHk3LF2rhUBll6cG+Z1OX7mCtmM3
3znXDFxTf3/DZ5XgeleNG98umeUMg8rHgj2yUB4nwoMbeJIjk7e5tBQKCCNOYM1M
da1wzrvxo3blXsIFzpxLqQ+tVnYuql9CYjX169Nwq+Dsgv6zNWzWMlPTPAKdbOVV
vXV2hfQ3LmnuzCv9t/TUdwkdyUMDUkbF+T8veD5bMP3k8lYuaNu0YQmbgbKvklK7
voaEFte9AgMBAAECggEBAK18w4jMyQ7Q1KfQpOO9puT6Cq36g7pg4OMkBNkAkT9A
WbPfHDA3KG3oV4wAZluhYF8iZa6HQKKT1i6/1fu1Fp9A5l5Fx6UhFM6c1ncqMEeC
bnu+Z0TQ4FU9CRuoaknN4JEGmjt/vfAl8mFLVvW6i1AyAi1xQRhup/jgYBcPR76y
zcmgdyaDNX/Z4rPAIzPTJbEjPhV8L3J8SjM6CML9k03QA6GALyVIJwUOUy/qJtYL
9UrUFwE+jjbifWjIpNjdKPFi6ltgSk9cOQZXcKWw+0CBJ5q3cZ6m6faqnWyDMHqP
garQd8bHzxG5yrVqrYiP8Mv2vD+cham8Nl2frJsK4cECgYEA/yR5rTpaFkZ9fV12
/r/KLapClg9R6COwBWk5nPvGde7rLMPVg2sQgyzfj7fRvZiQDx9zjmn96LJX+uwr
do88rWFUCmq5S/G0p1UD3DEW1b/YcN5D/nnG0QjeP0PSmG4Aj/SxDUD8AX1zwCpn
/G7dJame4A45tW3oxlGDMUPIwHkCgYEA3dWZV2VIHk5Aa4PJ92iTCtIPQnVb7140
WL2cCmsvIWe/UkHvHJZYcgB78OOuNET3ijI83rx/Ry2iBapKnuHxbl8npOw+8btZ
GakR0Htbns/sOqfb1jruen6kw+HTZaYYYEckHw8DiARLc4PlC6WSsYhL8W2Jl+SD
mwBb4NJQKGUCgYBGcR2e9BNXPxL6f8mQwAbj4LQNliE5BFFezRR5ARJkERig/Vh/
thmS/dqjZU7lF6/+XOKcmSrfCg48WuQNEbLg85QuZBTQoOUNpe0w5+S0EwmA7/y5
z4lSwS4LLYCBUS2akSYo0J5DEw3YKl0XVsx7z37rwUGxk6zGxE6CVYKhkQKBgGGh
PyJqjcngsJNg5gM//+70MgkSs4pukGU51bH0KELwcRBXuk9/j59kvSdwXNveOn+U
yptQpEeEOtl5b+vrDqF/uWfpHW6wAG+9q/xwPgtwAMxz0dnAB/LbR9J50drbtcCx
rqEIr4ouMbK+KpDsptoBXUL87WBvDsip6MXSabrNAoGBAKKjpwZXPBUfjRMEWSrv
AQLrBifzWHi6NbC+yFxdbaidzOzOLfxMXUKW8MRWK9Wsqza1r/bubbVTakzzFuZa
sxjcfOmE0/ArjIxeMLVvaXbY1hCgwdplJf0JyksidT7njIkXL+vFwq6VD5R+JCC8
MqcOXU7V6srirJOObsIHyC/i
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
	go func() { _ = s.Serve(lis) }()
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
		testClientConnection(cubic.NewClient(addr)))

	// by adding the appropriate CA to the client config, we should be now able to call methods.
	shouldSucceed(t, "default client with root CA of server",
		testClientConnection(cubic.NewClient(addr, cubic.WithServerCert(certPem))))

	shouldFail(t, "insecure client with tls server",
		testClientConnection(cubic.NewClient(addr, cubic.WithInsecure())))

	_, err = cubic.NewClient(addr, cubic.WithServerCert(certPem[:3]))
	if err == nil {
		t.Errorf("tls with bad server cert, want failure, got success")
	}

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
		testClientConnection(cubic.NewClient(addr, cubic.WithClientCert(certPem, keyPem), cubic.WithServerCert(certPem))))

	// mutual tls with correct cert but wrong CA should fail (client can not validate the server)
	shouldFail(t, "mutual tls with wrong CA cert",
		testClientConnection(cubic.NewClient(addr, cubic.WithClientCert(certPem, keyPem), cubic.WithServerCert(fakeCertPem))))

	// mutual tls with wrong cert but correct CA should fail (server can not validate the client)
	shouldFail(t, "mutual tls with wrong client cert",
		testClientConnection(cubic.NewClient(addr, cubic.WithClientCert(fakeCertPem, fakeKeyPem), cubic.WithServerCert(certPem))))

	// client creation should fail when presented with a bad client cert/key.
	_, err = cubic.NewClient(addr, cubic.WithClientCert(fakeCertPem[:3], fakeKeyPem))
	if err == nil {
		t.Errorf("client creation with invalid client cert, want failure, got success")
	}
}
