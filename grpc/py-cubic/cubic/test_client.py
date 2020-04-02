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
import io
import grpc
from concurrent import futures

from client import Client, RecognitionAudio, RecognitionConfig, CompiledContext
import cubic_pb2
import cubic_pb2_grpc

expectedResponses = {}
expectedResponses['Version'] = cubic_pb2.VersionResponse(cubic='2727', server='v.27.0')
expectedResponses['ListModels'] = cubic_pb2.ListModelsResponse(models=[cubic_pb2.Model(id="1"), cubic_pb2.Model(id="2")])

expectedResponses['CompileContext'] = cubic_pb2.CompileContextResponse(
    context=CompiledContext(data=b'\x00\x01\x02\x03'))

expectedResponses['Recognize'] = cubic_pb2.RecognitionResponse(results=[cubic_pb2.RecognitionResult(
    alternatives=[cubic_pb2.RecognitionAlternative(transcript="This is a test")])])

expectedResponses['StreamingRecognize'] = cubic_pb2.RecognitionResponse(
    results=[expectedResponses['Recognize'].results[0], expectedResponses['Recognize'].results[0]])

def setupGRPCServer():
    # create a gRPC server and adding the defined cubic servicer class to it
    server = grpc.server(futures.ThreadPoolExecutor(max_workers=1))
    cubic_pb2_grpc.add_CubicServicer_to_server(CubicServicer(), server)
    serverAddress = 'localhost:2727'
    server.add_insecure_port(serverAddress)
    server.start()
    return server, serverAddress

# Mock server service implementations
class CubicServicer(cubic_pb2_grpc.CubicServicer):
    
    def Version(self, request, context):
        return expectedResponses['Version']

    def ListModels(self, request, context):
        return expectedResponses['ListModels']
    
    def CompileContext(self, request, context):
        return expectedResponses['CompileContext']

    def Recognize(self, request, context):
        if request.config.audio_encoding != cubic_pb2.RecognitionConfig.RAW_LINEAR16:
            context.set_code(grpc.StatusCode.INVALID_ARGUMENT)
            context.set_details('audio encoding not supported')
            return cubic_pb2.RecognitionResponse()

        return expectedResponses['Recognize']

    def StreamingRecognize(self, request_iterator, context):
        
        # first message must be config
        request = next(request_iterator)
        if request.config == cubic_pb2.RecognitionConfig(): # empty config message
            context.set_code(grpc.StatusCode.FAILED_PRECONDITION)
            context.set_details('streamingrecognize failed: first message should be a config message')
            return cubic_pb2.RecognitionResponse()
        # rest should be audio messages
        try:
            while True:
                request = next(request_iterator)
                if request.audio == cubic_pb2.RecognitionAudio():   # empty audio message
                    context.set_code(grpc.StatusCode.INVALID_ARGUMENT)
                    context.set_details('streamingrecognize failed: all messages after the first should be audio messages')
                    return cubic_pb2.RecognitionResponse()
                yield expectedResponses['StreamingRecognize']
        except StopIteration:
            pass
        except Exception:
            context.set_code(grpc.StatusCode.INTERNAL)
            context.set_details('streamingrecognize failed: unexpected exception arose')
            return cubic_pb2.RecognitionResponse()

class TestClient(unittest.TestCase):
    
    @classmethod
    def setUpClass(cls):
        cls.server, cls.serverAddress = setupGRPCServer()

    @classmethod
    def tearDownClass(cls):
        cls.server.stop(0)

    def test_Version(self):
        client = Client(self.serverAddress, insecure=True)
        response = client.Version()
        self.assertEqual(response, expectedResponses['Version'])

    def test_ListModels(self):
        client = Client(self.serverAddress, insecure=True)
        response = client.ListModels()
        self.assertEqual(response, expectedResponses['ListModels'])

    def test_CompileContext(self):
        client = Client(self.serverAddress, insecure=True)
        phrases = {"COVID":0.0, "COVFEFE":0.0, "NAMBIA":0.0}
        response = client.CompileContext("1", "oov", phrases)
        self.assertEqual(response, expectedResponses['CompileContext'])

    def test_Recognize(self):
        client = Client(self.serverAddress, insecure=True)
        audio = io.BytesIO(b"0"*8912)
        
        # checking with unsupported encoding type, should raise exception
        cfg = RecognitionConfig(
            model_id = "1",
            audio_encoding = "WAV"
        )
        with self.assertRaises(grpc.RpcError) as context:
            resp = client.Recognize(cfg, audio)
        self.assertEqual(context.exception.code(), grpc.StatusCode.INVALID_ARGUMENT)
        self.assertEqual(context.exception.details(), 'audio encoding not supported')
        
        # checking with a valid request
        cfg = RecognitionConfig(
            model_id="1",
            audio_encoding="RAW_LINEAR16"
        )
        response = client.Recognize(cfg, audio)
        self.assertEqual(response, expectedResponses['Recognize'])

    def test_StreamingRecognize(self):
        client = Client(self.serverAddress, insecure=True)
        audio = io.BytesIO(b"0"*8192*5)

        cfg = RecognitionConfig(
            model_id="1",
            audio_encoding="RAW_LINEAR16"
        )
        for response in client.StreamingRecognize(cfg, audio):
            self.assertEqual(response, expectedResponses['StreamingRecognize'])


if __name__ == "__main__":
    unittest.main()
