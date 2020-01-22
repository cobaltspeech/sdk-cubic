//
//  CubicManager.swift
//  ios-cubic
//
//

import Foundation
import AVFoundation
import SwiftProtobuf
import SwiftGRPC
import sdk_cubic
public protocol CubicManagerDelegate {
    
    func managerDidRecognizeWithResponse(_ res: Cobaltspeech_Cubic_RecognitionResponse)
    
}



public class CubicManager: NSObject, AVAudioRecorderDelegate {
    
    private let client: Cobaltspeech_Cubic_CubicServiceClient
    private var whistleRecorder: AVAudioRecorder!
    private var selectedModelId: String = "\(1)"
    public var delegate: CubicManagerDelegate?
    var isRecord = false
    public var selectedModel: Cobaltspeech_Cubic_Model? {
        didSet {
            if let c = selectedModel {
                selectedModelId = c.id
            }
        }
    }
    
    public required init(client: Cobaltspeech_Cubic_CubicServiceClient) {
        self.client = client
    }
    
    public init(url: String) {
        self.client = Cobaltspeech_Cubic_CubicServiceClient(channel: Channel(address: url))
    }
    
    func log(_ text:String){
        print("CF: \(text)")
    }
    
    public func listModels(callback: @escaping(_ models:[Cobaltspeech_Cubic_Model]?, _ error:Error?) -> ()) {
        do {
           let listModels = Cobaltspeech_Cubic_ListModelsRequest()
           try client.listModels(listModels, completion: { (respose, result) in
                if let res = respose {
                    DispatchQueue.main.async {
                        callback(res.models, nil)
                    }
                }
           })
       } catch let e {
           print("\(e)")
       }
    }
    
    public func isAuthorized() -> Bool {
        return AVCaptureDevice.authorizationStatus(for: AVMediaType.audio) == .authorized
    }
    
    public func requestAccess(completionHandler: @escaping((_ granted:Bool) -> ())) {
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
    
    @discardableResult
    public func record() -> Bool {
        //kAudioFormatMPEG4AAC default
        if !isAuthorized() {
            return false
        }
        
        let settings: [String : Any] = [
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
            isRecord = whistleRecorder.record()
            return isRecord
        } catch {
            finishRecording(success: false)
        }
        
        return false
    }
    
    func finishRecording(success: Bool) {
        whistleRecorder?.stop()
        whistleRecorder = nil
    }
    
    public func stop() {
        if (isRecord){
            finishRecording(success: true)
            uploadRecord()
        }
    }
    
    func uploadRecord() {
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
                        self.log("Res from \(String(describing: try res.jsonString()))")
                        DispatchQueue.main.async {
                            self.delegate?.managerDidRecognizeWithResponse(res)
                        }
                    } catch let e {
                        print("\(e)")
                    }
               }
              
            }
        } catch let e {
            print("\(e)")
        }
    }
    
}
