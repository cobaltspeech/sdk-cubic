#!/bin/bash
if [[ "$VERSION" == *java ]]
then
    # Build the plain Java version
    mvn install
else
  # Build the Android gradle version
    cp ./grpc/cubic.proto ./grpc/android-cubic/src/main/proto/cubic.proto
    cd ./grpc/android-cubic && chmod +x ./gradlew && ./gradlew clean install -xtest
fi
