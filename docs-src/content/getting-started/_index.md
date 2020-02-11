+++
title = "Server Setup"
description = ""
weight = 1
+++

This section is meant to get you started using Cubic Server via a Docker image.

It is not necessary to go through through these steps to call the demo server for evaluation purposes, only to run Cubic Server on premises.  The demo server can be found at https://demo-cubic.cobaltspeech.com.

{{% panel theme="default" header="Note" %}}
Commercial use of the demo service is not permitted. This server is for testing and demonstration purposes only and is not guaranteed to support high availability or high volume. Data uploaded to the server may be stored for internal purposes.
{{% /panel %}}

## Installing the Cubic Server Image

The SDK communicates with a Cubic Server instance using [gRPC](https://grpc.io).  Cobalt distributes a docker image that contains the `cubicsvr` binary and model files.

<!--more-->

1. Contact Cobalt to get a link to the image file in AWS S3.  This link will expire in two weeks, so be sure to download the file to your own server.

2. Download with the AWS CLI if you have it, or with curl:

    ```bash
    URL="the url sent by Cobalt"
    IMAGE_NAME="name you want to give the file (should end with the same extension as the url, usually bz2)"
    curl $URL -L -o $IMAGE_NAME
    ```

3. Load the docker image

    ```bash
    docker load < $IMAGE_NAME
    ```

    This will output the name of the image (e.g. cubicsvr-demo-en_us-16).

4. Start the cubic service

    ```bash
    docker run -p 2727:2727 -p 8080:8080 --name cobalt -v usage:/usage cubicsvr-demo-en_us-16
    ```

    That will start listening for grpc commands on port 2727 and http requests on 8080, and will stream the debug log to stdout.  (You can replace `--name cobalt` with whatever name you want.  That just provides a way to refer back to the currently running container.) 

    The command also names the [volume](https://docs.docker.com/storage/volumes/) to which usage data is being written so you can track the amount of audio Cubic Server has processed, and submit the usage log to Cobalt if you have a volume-based license. That volume will persist even when the container is restarted; run `docker volume inspect usage` to see its location on the host machine.

5. Verify the service is running by calling

    ```bash
    curl http://localhost:8080/api/version
    ```

6. If you want to explore the package to see the model files etc, call the following to open the bash terminal on the previously run image.  Model files are located in the /model directory.

    ```bash
    docker exec -it cobalt bash
    ```

## Contents of the docker image

- **Base docker image** : debian-stretch-slim
- **Additional dependencies**
  - sox

### Cobalt-specific files

- **cubicsvr** - binary for performing Automatic Speech Recognition
- **model.config** - top-level config
- **am/nnet3_online/final.mdl** - this is the acoustic model
- **am/nnet3_online/conf/online_cmvn.conf** - feature extraction parameters for the features fed into the GMM model used for i-vector statistics accumulation.
- **am/nnet3_online/conf/splice.conf** - GMM feature context when accumulating statistics for i-vector accumulation. 
- **am/nnet3_online/ivector_extractor/*** - Kaldi configuration files related to ivectors  
- **graph/HCLG.fst** - the decoding graph: the combination of the AMs transition graph, the lexicon, and the language model
- **graph/words.txt** - an integer to word mapping, needed because the output of the HCLG graph contains only integer symbol IDs
- **graph/phones/word_boundary.int** - this is needed only when confusion network output is requested, it tells the decoder which phones are at word boundaries
