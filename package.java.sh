#!/bin/bash

if [[ "$VERSION" == *java ]]
then
  # Build the plain Java version
  echo java building
  (cd grpc/java-cubic && mvn install)
  
else
  if [[ "$VERSION" == *java-lite ]]
  then
    # Build the plain JavaLite version
    echo java-lite building
    (cd grpc/java-lite-cubic && mvn install)
    
  else
    # Build the Android gradle version
    echo android building
    cp ./grpc/cubic.proto ./grpc/android-cubic/src/main/proto/cubic.proto
    cd ./grpc/android-cubic && chmod +x ./gradlew && ./gradlew clean install -xtest
  fi
fi
