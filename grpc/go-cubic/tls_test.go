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
// openssl req -new -x509 -newkey rsa:512 -days 36500 -keyout key.pem -out cert.pem -nodes
//
// and are used for testing only. Do not use in production.

var certPem = []byte(`-----BEGIN CERTIFICATE-----
MIICADCCAaqgAwIBAgIJANt3rsozAyPsMA0GCSqGSIb3DQEBCwUAMFkxCzAJBgNV
BAYTAlVTMRMwEQYDVQQIDApTb21lLVN0YXRlMSEwHwYDVQQKDBhJbnRlcm5ldCBX
aWRnaXRzIFB0eSBMdGQxEjAQBgNVBAMMCWxvY2FsaG9zdDAgFw0xOTA0MTExNjU1
MTlaGA8yMTE5MDMxODE2NTUxOVowWTELMAkGA1UEBhMCVVMxEzARBgNVBAgMClNv
bWUtU3RhdGUxITAfBgNVBAoMGEludGVybmV0IFdpZGdpdHMgUHR5IEx0ZDESMBAG
A1UEAwwJbG9jYWxob3N0MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBALE0oXoXFm8X
nIGziJ/ZAT6CCYEkq2mr+maMX0GWx1q4FW8BaqgN65CcM5weQYiVoMKSigqQX/Ni
RXTVr3TpvlUCAwEAAaNTMFEwHQYDVR0OBBYEFFrEW7mW9llN8jxEpumUQ963Zq+h
MB8GA1UdIwQYMBaAFFrEW7mW9llN8jxEpumUQ963Zq+hMA8GA1UdEwEB/wQFMAMB
Af8wDQYJKoZIhvcNAQELBQADQQBbwHWAG6ibqVXfJguWz5LrrfGQ46/wi3KdavTl
ySEkLFaMK7tfwJg5tnZbjxuDB2J17CRQs6gL4b/yJpD+amVz
-----END CERTIFICATE-----`)

var keyPem = []byte(`-----BEGIN PRIVATE KEY-----
MIIBVQIBADANBgkqhkiG9w0BAQEFAASCAT8wggE7AgEAAkEAsTShehcWbxecgbOI
n9kBPoIJgSSraav6ZoxfQZbHWrgVbwFqqA3rkJwznB5BiJWgwpKKCpBf82JFdNWv
dOm+VQIDAQABAkAiS9lk08sXvS4hPCoZJdrDyk8km4BBd3ODHW/iNdhnih6Mpgtb
Ib+Dkux53ryY+2cu7/ceayWBTUhQUUsQ1F+RAiEA5diJ1yvCSLC6nFgYebT9sLda
3aWmjN6VXL4a2IlquJcCIQDFXqtn3eCxs9VRlAsjyRkviEm3Pq59AyUJDgyNaUmR
8wIgWEnDOOnQKUfphqC4VhfV0wm7V6SHw1jEmulTOpYebmUCIQC3JWgM0/lDLMsQ
Dj5gEKXMU72DyyiDXL2rL1w6hK7+PQIhAJQF17cocDL8mK11fjdf6dg1UHyizTIn
qLSSUOdYRzk/
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
