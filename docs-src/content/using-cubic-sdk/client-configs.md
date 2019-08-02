---
title: "Recognition Configurations"
weight: 25
---

An in-depth explanation of the various fields of the complete SDK can be found [here](../../protobuf/autogen-doc-cubic-proto/).  The sub-section [RecognitionConfig](../../protobuf/autogen-doc-cubic-proto/#message-recognitionconfig) is particularly important here.

This page here discusses the more common combinations sent to the server.

## Fields

Here is a quick overview of the fields.

| Field | Required | Default | Description |
| ----- | -------- | ------- | ----------- |
| model_id | Yes | | Unique ID of the model to use. |
| audio_encoding | Yes | | Encoding format of the audio, such as RAW_LINEAR_16, WAV, MP3, etc. |
| idle_timeout | No | `0s` (Unlimited) | Maximum time allowed between each gRPC message. The server may place further restrictions depending on its configuration. |
| enable_word_time_offsets | No | `false` | Toggles the calculation of word-level timestamps.  The specified model must also support word-level timestamps for this field to be populated. |
| enable_word_confidence | No | `false` | Toggles the calculation of word-level confidence scores. The specified model must also support word-level confidence for this field to be populated.|
| enable_raw_transcript | No | `false` | If `true`, the raw transcript will be included in the results. |
| enable_confusion_network | No | `false` | Toggles the inclusion of a confusion network, consisting of multiple alternative transcriptions.  The specified model must also support confusion networks for this field to be populated. |
| audio_channels | No | `[0]` (mono) | Specifies which channels of a multi-channel audio file to be transcribed, each as their own individual audio stream. |

## Use cases

The most basic use case is getting a formatted transcript.  This would simply need a config such as:

```json
{
    "model_id": "1",
    "audio_encoding": "raw"
}
```

and your resulting transcript would be found at `results.alternatives[*].transcript`.

------

Sometimes, only the raw (unformatted) transcript is desired.  In this case, there are two options.  

1. Disable server-side formatting and use the above config.  Retrieve the raw transcript from the `results.alternatives[*].transcript` field.
2. Specify the `enable_raw_transcript = true` flag, and access the field `results.alternatives[*].raw_transcript`.

> Note: Prior to `cubicsvr` v2.9.0 and SDK-Cubic v1.3.0, the field `results.alternatives[*].transcript` was populated with either the raw or the formatted transcription depending on the `enable_raw_transcript` config.  After these changes, raw transcripts have been pushed to a new field `results.alternatives[*].raw_transcript`, and only populated when `enable_raw_transcript` is set to true.

------

Another use case would be getting both the formatted and raw transcript.  This can be done using this config.

```json
{
    "model_id": "1",
    "audio_encoding": "raw",
    "enable_raw_transcript": "true"
}
```

The formatted transcript would be found at `results.alternatives[*].transcript`, and the raw transcript would be found at `results.alternatives[*].raw_transcript`.

------

If you need to know the timestamp for each word, to align subtitles with a video, for example, then you can use this config to enable those word-level timestamps.  Please note the inclusion of `enable_raw_transcript`; the word-level information corresponds to the raw transcript, since the formatter may combine multiple words into one symbol in the formatted transcript (e.g. "twenty one" to "21")

```json
{
    "model_id": "1",
    "audio_encoding": "raw",
    "enable_raw_transcript": true,
    "enable_word_time_offsets": true
}
```

Timestamps will be found at `results.alternatives[*].words[*].start_time` and `results.alternatives[*].words[*].duration`.

------

Word-level confidences, i.e. for displaying a lighter color for less confident words, can be included much like the word-level timestamps.  Please note the inclusion of `enable_raw_transcript`.

```json
{
    "model_id": "1",
    "audio_encoding": "raw",
    "enable_raw_transcript": true,
    "enable_word_time_offsets": true
}
```

Word-level confidence scores will be found at `results.alternatives[*].words[*].confidence`.
 
------

For applications that need more than the one-best transcription, the most comprehensive and detailed Cubic results are found in the confusion network.  Please refer to the [in depth confusion network documentation](../../protobuf/autogen-doc-cubic-proto/#message-recognitionconfusionnetwork) to see what is included.

To enable the confusion network, the config will look similar to this:

```json
{
    "model_id": "1",
    "audio_encoding": "raw",
    "enable_raw_transcript": true,
    "enable_confusion_network": true
}
```

The confusion network can be accessed at `result.cnet`.
