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

import os
import sys
import grpc

from google.protobuf.empty_pb2 import Empty

sys.path.append(os.path.join(os.path.dirname(os.path.realpath(__file__))))
from cubic_pb2 import ListModelsRequest, RecognizeRequest, StreamingRecognizeRequest, CompileContextRequest
from cubic_pb2 import RecognitionConfig, RecognitionAudio, RecognitionContext, ContextPhrase, CompiledContext
from cubic_pb2_grpc import CubicStub


class Client(object):
    """ A client for interacting with Cobalt's Cubic GRPC API."""

    def __init__(self, serverAddress, insecure=False,
                 serverCertificate=None,
                 clientCertificate=None,
                 clientKey=None,
                 bufferSize=8192):
        """  Creates a new cubic Client object.

        Args:
            serviceAddress: host:port of where cubic server is running

            insecure: If set to true, an insecure grpc channel is used.
                      Otherwise, a channel with transport security is used.

            serverCertificate:  PEM certificate as byte string which is used as a  
                                root certificate that can validate the certificate 
                                presented by the server we are connecting to. Use this 
                                when connecting to an instance of cubic server that is 
                                using a self-signed certificate.

            clientCertificate:  PEM certificate as bytes string presented by this Client when
                                connecting to a server. Use this when setting up mutually 
                                authenticated TLS. The clientKey must also be provided.

            clientKey:  PEM key as byte string presented by this Client when
                        connecting to a server. Use this when setting up mutually 
                        authenticated TLS. The clientCertificate must also be provided.

            bufferSize: Bytes of audio sent to cubic server in each chunk if using streaming
                        recognition. Default is 8192 bytes.
        """

        self.serverAddress = serverAddress
        self.insecure = insecure
        self.bufferSize = bufferSize

        if self.bufferSize <= 0:
            raise ValueError('buffer size must be greater than 0')

        if insecure:
            # no transport layer security (TLS)
            self._channel = grpc.insecure_channel(serverAddress)
        else:
            # using a TLS endpoint with optional certificates for mutual authentication
            if clientCertificate is not None and clientKey is None:
                raise ValueError("client key must also be provided")
            if clientKey is not None and clientCertificate is None:
                raise ValueError("client certificate must also be provided")
            self._creds = grpc.ssl_channel_credentials(
                root_certificates=serverCertificate,
                private_key=clientKey,
                certificate_chain=clientCertificate)
            self._channel = grpc.secure_channel(serverAddress, self._creds)

        self._client = CubicStub(self._channel)

    def __del__(self):
        """ Closes and cleans up after Client. """
        try:
            self._channel.close()
        except AttributeError:
            # client wasn't fully instantiated, no channel to close
            pass

    def Version(self):
        """ Queries the server for its version. """
        return self._client.Version(Empty())

    def ListModels(self):
        """ Retrieves a list of available speech recognition models. """
        return self._client.ListModels(ListModelsRequest())

    def CompileContext(self, modelID, token, phrases, boostValues=None):
        """ Compiles the given list of phrases or words into a compact, fast to 
        access form for a cubic model, which may later be provided in a `Recognize` or
        `StreamingRecognize` call to aid speech recognition.

        Args: 
            modelID: unique identifier of the model to compile the context information for.

            token:  A string allowed by the model being used, such as "names" or 
                    "airports", that is used to determine the position in the 
                    recognition output where the provided list of phrases or words
                    may appear. The allowed tokens for a given model can be found in
                    its ModelAttributes obtained via the `ListModels` method.

            phrases: The list of phrases or words to compile.

            boostValues: A list of positive floating numbers, one for each phrase or word in
                         the phrases list. The likelihood of the corresponding entry in the
                         phrarses list appearing in the output can be increased by setting a
                         higher value for it. If given boostValues, the new probability of the
                         corresponding phrase entry becomes (boost + 1.0) * old probability. By
                         default, all provded phrases or words are given an equal probability of
                         1/N, where N = total number of phrases or words. The new probabilities
                         are normalized after boosting so that they sum to one. This means that
                         if all phrases are given the same boost value, they will still have the
                         same default likelihood. This also means that the boost value can be any
                         positive value, but for most cases, values between 0 to 10 work well.
                         Negative values may be provided but they will be treated as 0 (no boost).
        """

        if boostValues is not None:
            if len(boostValues) != len(phrases):
                raise ValueError(
                    "len(boostValues) must be the same as len(phrases)")
            contextPhrases = [ ContextPhrase(text=txt, boost=val)
                                for (txt, val) in zip(phrases, boostValues) ]
        else:
            contextPhrases = [ContextPhrase(text=txt) for txt in phrases]

        return self._client.CompileContext(CompileContextRequest(
            model_id=modelID, token=token, phrases=contextPhrases,
        ))

    def Recognize(self, cfg, audio):
        """ Performs synchronous speech recognition: receive results after all audio
        has been sent and processed. It is expected that this request be typically
        used for short audio content: less than a minute long.  For longer content,
        the `StreamingRecognize` method should be preferred.

        Args:
            cfg: RecognitionConfig object containing the model ID, timeout, audio encoding etc.

            audio: a binary stream of data to send to cubic. 
        """

        rcgAudio = RecognitionAudio(data=audio.read())
        return self._client.Recognize(RecognizeRequest(config=cfg, audio=rcgAudio))

    def StreamingRecognize(self, cfg, audio):
        """ Performs bidirectional streaming speech recognition. Receive results while
            sending audio.

        Args:
            cfg: RecognitionConfig object containing the model ID, timeout, audio encoding etc.

            audio: a binary stream of data to send to cubic. The object passed in should have a 
                    read(nBytes) method that returns nBytes from the binary stream. An example is
                    the object created using open('filepath', 'rb'); For streaming from a microphone,
                    the object could be a PyAudio() stream. Byte chunks are read from this stream
                    and sent to cubic server sequentially. The size of each chunk is equal to the 
                    buffer size set for the client.
        """

        stream = _audioStreamer(cfg, audio, self.bufferSize)
        for resp in self._client.StreamingRecognize(stream):
            yield resp


def _audioStreamer(cfg, audio, bufferSize):
    """ A generator that streams audio data packaged for streaming recognition. 
    The first yield is a config message, and the following are all audio messages. """
    yield StreamingRecognizeRequest(config=cfg)
    while True:
        data = audio.read(bufferSize)
        if len(data) == 0:
            break
        rcgAudio = RecognitionAudio(data=data)
        yield StreamingRecognizeRequest(audio=rcgAudio)
