#!/bin/bash
# Build the plain Java version
mvn install

# Build the Android gradle version
cp ./grpc/cubic.proto ./grpc/android-cubic/src/main/proto/cubic.proto
cd ./grpc/android-cubic && chmod +x ./gradlew && ./gradlew clean install -xtest && ./gradlew build publishToMavenLocal
