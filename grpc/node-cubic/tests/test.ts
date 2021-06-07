import { CompileContextRequest, ContextInfo, ContextPhrase } from './../src/grpc/cubic_pb.d';


import {RecognitionAudio, RecognitionConfig, StreamingRecognizeRequest,ListModelsRequest, RecognizeRequest } from '../src/grpc/cubic_pb';


import { Empty } from 'google-protobuf/google/protobuf/empty_pb'
import {grpc} from "@improbable-eng/grpc-web"
const myTransport = grpc.CrossBrowserHttpTransport({ withCredentials: true, });
grpc.setDefaultTransport(myTransport);
/**
 transport?: TransportFactory;
    debug?: boolean;
 */



// Specify the default transport before any requests are made. 
import { SpeechRecorder } from "speech-recorder";
import * as fs from 'fs';
import {RecognitionAudio, RecognitionConfig, StreamingRecognizeRequest,ListModelsRequest, RecognizeRequest } from '../src/grpc/cubic_pb';
import CubicClient from '../src/index'
const serverAddr = "127.0.0.1:2727"
let client =new CubicClient(serverAddr)



const recorder = new SpeechRecorder();
const writeStream = fs.createWriteStream("audio.raw");


let stream = client.streamingRecognize()
stream.on('data',(response)=>{
    let results = response.getResultsList()
    for (let result of results){
        let alternatives = result.getAlternativesList()
        if (alternatives.length>0){
            let words = alternatives[0].getWordsList()
            for (let word of words){
                console.log(word.getWord())
            }
        }
    }
})

let config = new RecognitionConfig()
config.addAudioChannels(1)
config.setModelId('1');
let req = new StreamingRecognizeRequest()
req.setConfig(config)
stream.write(req)


recorder.start({
    onAudio: (audio) => {
        req = new StreamingRecognizeRequest()
        let reqAudio =new RecognitionAudio()
        reqAudio.setData(audio)
        req.setAudio(reqAudio)
        req.setAudio()
        stream.write(req)
    }
});




