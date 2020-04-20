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

import Foundation
import GRPC
import NIOSSL

public typealias CubicFailureCallback = (_ error: Error) -> ()

public enum CubicError: Error {
    case countError(String)
}

public typealias Client = Cobaltspeech_Cubic_CubicClient

extension Cobaltspeech_Cubic_CubicClient {

    public convenience init(host: String,
                            port: Int,
                            useTLS: Bool = false) {
        let target = ConnectionTarget.hostAndPort(host, port)
        let eventLoopGroup = PlatformSupport.makeEventLoopGroup(loopCount: 1,
                                                                networkPreference: .best)
        
        let tls = useTLS ? ClientConnection.Configuration.TLS() : nil
        
        let configuration = ClientConnection.Configuration(target: target,
                                                           eventLoopGroup: eventLoopGroup,
                                                           errorDelegate: nil,
                                                           connectivityStateDelegate: nil,
                                                           tls: tls,
                                                           connectionBackoff: nil)
        let connection = ClientConnection(configuration: configuration)
        
        self.init(channel: connection)
    }
    
    public convenience init(host: String,
                            port: Int,
                            tlsCertificateFileName: String,
                            tlsCertificateFormat: NIOSSLSerializationFormats) {
        let target = ConnectionTarget.hostAndPort(host, port)
        let eventLoopGroup = PlatformSupport.makeEventLoopGroup(loopCount: 1,
                                                                networkPreference: .best)
        
        var configuration = ClientConnection.Configuration(target: target, eventLoopGroup: eventLoopGroup)
        
        if let cert = try? NIOSSLCertificate(file: tlsCertificateFileName, format: tlsCertificateFormat) {
            let source = NIOSSLCertificateSource.certificate(cert)
            var tls = ClientConnection.Configuration.TLS()
            tls.certificateChain.append(source)
            configuration.tls = tls
        }
        
        let connection = ClientConnection(configuration: configuration)
        self.init(channel: connection)
    }
    
    public func listModels(success: @escaping (_ models: [Cobaltspeech_Cubic_Model]?) -> (), failure: CubicFailureCallback?) {
        let request = Cobaltspeech_Cubic_ListModelsRequest()
    
        listModels(request).response.whenComplete({ (result) in
            do {
                let response = try result.get()
                success(response.models)
            } catch let error {
                failure?(error)
            }
        })
    }

    public func compileContext(modelID: String,
                               token: String,
                               phrases: [String],
                               boostValues: [Float],
                               success: @escaping (_ compiledCtx: Cobaltspeech_Cubic_CompiledContext?) -> (),
                               failure: CubicFailureCallback?) {

        var request = Cobaltspeech_Cubic_CompileContextRequest()
        request.modelID = modelID
        request.token = token
        if (boostValues.count() > 0) {
            if (boostValues.count() != phrases.count()) {
                failure?(CubicError.countError("number of boost values not the same as number of phrases"))
            })
            for (phrase, boost) in zip(phrases, boostValues) {
                var ctxPhrase = Cobalt_Cubic_ContextPhrase()
                ctxPhrase.text = phrase
                ctxPhrase.boost = boost
                request.phrases.append(ctxPhrase)
            }
        } else {
            for phrase in phrases {
                var ctxPhrase = Cobalt_Cubic_ContextPhrase()
                ctxPhrase.text = phrase
                ctxPhrase.boost = 0
                request.phrases.append(ctxPhrase)
            }
        }
        compileContext(request).response.whenComplete({ (result) in
            do {
                let response = try result.get()
                success(response.context)
            } catch let error {
                failure?(error)
            }
        })
    }

    public func streamingRecognize(audioData: Data,
                                   chunkSize: Int,
                                   config: Cobaltspeech_Cubic_RecognitionConfig,
                                   success: @escaping (_ response: Cobaltspeech_Cubic_RecognitionResponse) -> (),
                                   failure: CubicFailureCallback?) {
        let dispatchGroup = DispatchGroup()
        
        let call = streamingRecognize(handler: { (result) in
            success(result)
        })
        
        var request = Cobaltspeech_Cubic_StreamingRecognizeRequest()
        request.config = config

        dispatchGroup.enter()
         
        call.sendMessage(request).whenComplete({ (result) in
            switch result {
            case .failure(let error):
                failure?(error)
            default:
                break
            }
             
            dispatchGroup.leave()
        })
        
        dispatchGroup.wait()
        
        let dataSize = audioData.count
        let fullChunks = dataSize / chunkSize
        let totalChunks = fullChunks + (dataSize % chunkSize == 0 ? 0 : 1)

        for chunkCounter in 0..<totalChunks {
            var chunk: Data
            let chunkBase = chunkCounter * chunkSize
            var diff = chunkSize
             
            if (chunkCounter == totalChunks - 1) {
                diff = dataSize - chunkBase
            }

            chunk = audioData.subdata(in: chunkBase..<(chunkBase + diff))
            
            dispatchGroup.enter()
             
            request.audio.data = chunk
             
            call.sendMessage(request).whenComplete({ (result) in
                switch result {
                case .failure(let error):
                    failure?(error)
                default:
                    break
                }
                 dispatchGroup.leave()
            })
             
            dispatchGroup.wait()
         }

        dispatchGroup.enter()
                 
        call.sendEnd().whenComplete { (result) in
            switch result {
            case .failure(let error):
                failure?(error)
            default:
                break
            }
            
            dispatchGroup.leave()
        }
                 
        dispatchGroup.wait()
    }
    
    public func recognize(audioURL: URL,
                          config: Cobaltspeech_Cubic_RecognitionConfig,
                          success: @escaping (_ response: Cobaltspeech_Cubic_RecognitionResponse) -> (),
                          failure: CubicFailureCallback?) {
        do {
            var request = Cobaltspeech_Cubic_RecognizeRequest()
            request.config = config

            var audio = Cobaltspeech_Cubic_RecognitionAudio()
            audio.data = try Data(contentsOf: audioURL)
            request.audio = audio
            
            recognize(request).response.whenComplete({ (result) in
                switch result {
                case .success(let recognitionResponse):
                    success(recognitionResponse)
                case .failure(let error):
                    failure?(error)
                }
            })
        } catch let error {
            failure?(error)
        }
    }
    
}
