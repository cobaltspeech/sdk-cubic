# -*- coding: utf-8 -*-
#
# Copyright(2019) Cobalt Speech and Language Inc.
#
# Licensed under the Apache License, Version 2.0 (the "License")
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http: // www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

import unittest
import grpc
from concurrent import futures

from client import Client
import cubic_pb2
import cubic_pb2_grpc
expectedResponses = {}

expectedResponses['Version'] = cubic_pb2.VersionResponse(cubic='2727', server='v.27.0')

def setupGRPCServerWithTLS(certPem, keyPem, mutual=False):
    # create a gRPC server and adding the defined cubic servicer class to it
    server = grpc.server(futures.ThreadPoolExecutor(max_workers=1))
    cubic_pb2_grpc.add_CubicServicer_to_server(CubicServicer(), server)
    # adding SSL credentials
    if mutual:
        creds = grpc.ssl_server_credentials([(keyPem, certPem)], root_certificates=certPem, require_client_auth=True)
    else:
        creds = grpc.ssl_server_credentials([(keyPem, certPem)])
    serverAddress = 'localhost:2727'
    server.add_secure_port(serverAddress, creds)
    server.start()
    return server, serverAddress

# Mock server service implementations
class CubicServicer(cubic_pb2_grpc.CubicServicer):
    
    def Version(self, request, context):
        return expectedResponses['Version']


class TestTLS(unittest.TestCase):
    

    def test_ServerTLS(self):
        # TestServerTLS starts a server with TLS(not mutual) and makes sure only the
        # appropriate clients can connect to it. Since the server uses a self-signed
        # certificate, a default client should not be able to call methods, as the 
        # certificate can not be validated.
        server, serverAddress = setupGRPCServerWithTLS(certPem, keyPem, mutual=False)
        
        # default client with self-signed server cert; should fail
        with self.assertRaises(grpc.RpcError) as context:
            client = Client(serverAddress, insecure=False)
            response = client.Version()
        self.assertEqual(context.exception.details(), "failed to connect to all addresses")
        
        # client provides incorrect server certificate; should fail
        with self.assertRaises(grpc.RpcError) as context:
            client = Client(serverAddress, insecure=False, serverCertificate=fakeCertPem)
            response = client.Version()
        self.assertEqual(context.exception.details(), "Empty update")

        # client tries to connect with insecure channel; should fail
        with self.assertRaises(grpc.RpcError) as context:
            client = Client(serverAddress, insecure=True, serverCertificate=certPem)
            response = client.Version()
        self.assertEqual(context.exception.details(), "Socket closed")
        
        # client provides correct server certificate; should succeed
        client = Client(serverAddress, insecure=False, serverCertificate=certPem)
        response = client.Version()
        self.assertEqual(response, expectedResponses['Version'])
        
        server.stop(0)

    def test_MutualTLS(self):
        # TestMutualTLS starts a server with mutual TLS enabled and makes 
        # sure only the appropriate clients can connect to it.
        server, serverAddress = setupGRPCServerWithTLS(certPem, keyPem, mutual=True)
        
        # mutual tls with correct cert but wrong CA; should fail (client can not validate the server)
        with self.assertRaises(grpc.RpcError) as context:
            client = Client(serverAddress, insecure=False, serverCertificate=fakeCertPem, clientCertificate=certPem, clientKey=keyPem)
            response = client.Version()
        self.assertEqual(context.exception.details(), "Empty update")
        
        # mutual tls with wrong cert but correct CA; should fail (server can not validate the client)
        with self.assertRaises(grpc.RpcError) as context:
            client = Client(serverAddress, insecure=False, serverCertificate=certPem, clientCertificate=fakeCertPem, clientKey=fakeKeyPem)
            response = client.Version()
        self.assertEqual(context.exception.details(), "Empty update")

        # mutual tls with correct cert and CA but no key provided; should fail (client raises exception)
        with self.assertRaises(ValueError):
            client = Client(serverAddress, insecure=False, serverCertificate=certPem, clientCertificate=certPem)
            response = client.Version()

        # client tries to connect with insecure channel; should fail
        with self.assertRaises(grpc.RpcError) as context:
            client = Client(serverAddress, insecure=True, serverCertificate=certPem, clientCertificate=certPem, clientKey=keyPem)
            response = client.Version()
        self.assertEqual(context.exception.details(), "Socket closed")

        # client presented a bad client cert/key; should fail
        with self.assertRaises(grpc.RpcError) as context:
            client = Client(serverAddress, insecure=False, serverCertificate=certPem, clientCertificate=certPem[:3], clientKey=keyPem)
            response = client.Version()
        self.assertEqual(context.exception.details(), "Empty update")

        # client provides appropriate certificates; should succeed
        client = Client(serverAddress, insecure=False, serverCertificate=certPem, clientCertificate=certPem, clientKey=keyPem)
        response = client.Version()
        self.assertEqual(response, expectedResponses['Version'])
        
        server.stop(0)

# The TLS certificates below were generated using:
#
# openssl req -new -x509 -newkey rsa:512 -days 36500 -keyout key.pem -out cert.pem -nodes
#
# and are used for testing only. Do not use in production.
certPem = b"""-----BEGIN CERTIFICATE-----
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
-----END CERTIFICATE-----"""

keyPem = b"""-----BEGIN PRIVATE KEY-----
MIIBVQIBADANBgkqhkiG9w0BAQEFAASCAT8wggE7AgEAAkEAsTShehcWbxecgbOI
n9kBPoIJgSSraav6ZoxfQZbHWrgVbwFqqA3rkJwznB5BiJWgwpKKCpBf82JFdNWv
dOm+VQIDAQABAkAiS9lk08sXvS4hPCoZJdrDyk8km4BBd3ODHW/iNdhnih6Mpgtb
Ib+Dkux53ryY+2cu7/ceayWBTUhQUUsQ1F+RAiEA5diJ1yvCSLC6nFgYebT9sLda
3aWmjN6VXL4a2IlquJcCIQDFXqtn3eCxs9VRlAsjyRkviEm3Pq59AyUJDgyNaUmR
8wIgWEnDOOnQKUfphqC4VhfV0wm7V6SHw1jEmulTOpYebmUCIQC3JWgM0/lDLMsQ
Dj5gEKXMU72DyyiDXL2rL1w6hK7+PQIhAJQF17cocDL8mK11fjdf6dg1UHyizTIn
qLSSUOdYRzk/
-----END PRIVATE KEY-----"""
#
# define a new "fake" certificate and key that can be 
# used in testing credential validation
#
fakeCertPem = b""" -----BEGIN CERTIFICATE-----
MIIBZjCCARCgAwIBAgIJAMn7swGSO8/UMA0GCSqGSIb3DQEBCwUAMA0xCzAJBgNV
BAYTAlVTMB4XDTE5MDMwNzEyMjAxM1oXDTE5MDQwNjEyMjAxM1owDTELMAkGA1UE
BhMCVVMwXDANBgkqhkiG9w0BAQEFAANLADBIAkEA2fY7pvsLKU0SKjYEat8lbU8n
OFVicZSuqKVuE/n/0PW69MLAiA/8nNgX/RGads1udSNe1LptlYH8scE76qi4VwID
AQABo1MwUTAdBgNVHQ4EFgQUEN5OTssGhgt04gmfYEPGhBPRVLwwHwYDVR0jBBgw
FoAUEN5OTssGhgt04gmfYEPGhBPRVLwwDwYDVR0TAQH/BAUwAwEB/zANBgkqhkiG
9w0BAQsFAANBALF4l2Szu9agWyu5LtOxd4I7eyMfyPZhqa0Lff5j/VdyTjIVDrgo
XKJaldxh9WV0fSXLIqczEqmAQbmbj/CTvz8=
-----END CERTIFICATE-----"""

fakeKeyPem =  b"""-----BEGIN PRIVATE KEY-----
MIIBVQIBADANBgkqhkiG9w0BAQEFAASCAT8wggE7AgEAAkEA2fY7pvsLKU0SKjYE
at8lbU8nOFVicZSuqKVuE/n/0PW69MLAiA/8nNgX/RGads1udSNe1LptlYH8scE7
6qi4VwIDAQABAkBduVEbU3YQM3DtL78kiYHZiCDQS38CYjHcmQ5Fjsne+xBcDJxZ
8dtnCu4/OVNUevTGAObATIQBW5eBieUf8tZRAiEA7F2Bx/p+N8fzjcyCifMTUyOk
qpRXPgwc/wQ4KfdvwTkCIQDsEVztoPr1wnnmBRFhZsXzOvzGpSMczm2x3EmvIA2W
DwIgIvgym0OUKOyMPA5lwcMUuNgtJI+N2MAyCgi1xn+1KQECIQC/YXgwIgEy+n4u
r88eYt56SUkilkB4GxatSgTmmBrLmwIhAJ66U/pu4nL2HIhGcUdxawRC0DJRwRKc
B6KD9XmVFWXX
-----END PRIVATE KEY-----"""


if __name__ == "__main__":
    unittest.main()
