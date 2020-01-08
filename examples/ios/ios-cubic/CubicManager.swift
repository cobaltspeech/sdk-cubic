//
//  CubicManager.swift
//  RecordAudio
//
//  Created by Alex Mankov on 07.01.2020.
//  Copyright © 2020 Alex Mankov. All rights reserved.
//

import Foundation
import AVFoundation
import SwiftProtobuf
import SwiftGRPC
protocol  CubicManagerDelegate {
    func successRecognize(_ res:Cobaltspeech_Cubic_RecognitionResponse)
    
}
class CoboltClient:Cobaltspeech_Cubic_CubicServiceClient{}
class CubicManager:NSObject,AVAudioRecorderDelegate {
    private let client:CoboltClient
    private var whistleRecorder: AVAudioRecorder!
    private var selectedModelId:String = "\(1)"
    var delegate:CubicManagerDelegate?
    var selectedModel:Cobaltspeech_Cubic_Model? {
        didSet{
            if let c = selectedModel {
                selectedModelId = c.id
            }
        }
    }
    required init(client:CoboltClient) {
        self.client = client
    }
    init(url:String) {
        self.client =  CoboltClient(channel: Channel(address: url))
    }
    func log(_ text:String){
        print("CF: \(text)")
    }
    public func listModels(callback:@escaping(_ models:[Cobaltspeech_Cubic_Model]?,_ error:Error?)->()) {
        do {
           let listModels = Cobaltspeech_Cubic_ListModelsRequest()
           try  client.listModels(listModels, completion: { (respose, result) in
               if let res = respose {
                DispatchQueue.main.async {
                   callback(res.models,nil)
                }
               }
               
           })
       } catch let e {
           print("\(e)")
       }
    }
    public func isAuthorized() -> Bool {
        return AVCaptureDevice.authorizationStatus(for:  AVMediaType.audio) == .authorized
    }
    public func requestAccess(completionHandler: @escaping( (_ granted:Bool)->() )) {
        AVCaptureDevice.requestAccess(for:  AVMediaType.audio, completionHandler: completionHandler)
    }
   private class func getDocumentsDirectory() -> URL {
       let paths = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)
       let documentsDirectory = paths[0]
       return documentsDirectory
   }


   
    private class func getWavURL(_ name:String = "") -> URL {
        return CubicManager.getDocumentsDirectory().appendingPathComponent("whistle\(name).wav")
   }
    public func record() -> Bool {
        //kAudioFormatMPEG4AAC default
        if !isAuthorized() {
            return false
        }
        let settings:[String : Any] = [
            AVFormatIDKey: Int(kAudioFormatLinearPCM),
            AVSampleRateKey: 16000.0,
            AVNumberOfChannelsKey: 1,
            AVLinearPCMBitDepthKey: 16,
            AVLinearPCMIsFloatKey:false,
            AVLinearPCMIsBigEndianKey:false,
            AVEncoderAudioQualityKey: AVAudioQuality.high.rawValue
        ]
        
        do {
            let audioURL = CubicManager.getWavURL()
            whistleRecorder = try AVAudioRecorder(url: audioURL, settings: settings)
            whistleRecorder.delegate = self
            return whistleRecorder.record()
        } catch let e {
            finishRecording(success: false)
        }
        return false
    }
    func finishRecording(success: Bool) {
        whistleRecorder?.stop()
        whistleRecorder = nil
    }
    func stop() {
        finishRecording(success: true)
        uploadRecord()
    }
    func uploadRecord(){
            do {
                var req = Cobaltspeech_Cubic_RecognizeRequest()
                req.config = Cobaltspeech_Cubic_RecognitionConfig()
                req.config.modelID = selectedModelId
                req.config.idleTimeout = Google_Protobuf_Duration()
                req.config.idleTimeout.seconds = 5
                req.config.audioEncoding = .rawLinear16
                
                req.audio = Cobaltspeech_Cubic_RecognitionAudio()
                let audioUrl = CubicManager.getWavURL()
                req.audio.data = try Data(contentsOf: audioUrl)
                
                  try client.recognize(req) { (respose, result) in
                       if let res = respose {
                            do {
                                self.log("Res from  \(String(describing: try res.jsonString()))")
                                DispatchQueue.main.async {
                                    self.delegate?.successRecognize(res)
                                }
                            } catch let e {
                                print("\(e)")
                            }
                        }
                      
                  }
                /*let array:[Cobaltspeech_Cubic_RecognitionConfig.Encoding] = [.,.wav,.mp3,.flac,.vox8000,.ulaw8000]
                for code in array {
                  
                }*/
            } catch let error {
                
            }
        }
    
}
