#!/bin/bash
if [[ "$VERSION" == *java ]]
then
    mvn install
else
    cp ./grpc/cubic.proto ./grpc/android-cubic/src/main/proto/cubic.proto
    cd ./grpc/android-cubic && chmod +x ./gradlew && ./gradlew clean install -xtest
fi
