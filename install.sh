#!/bin/bash
#tag=$(git tag --contains) && branch=$(git branch | sed -n -e 's/^\* \(.*\)/\1/p' | sed 's#/#~#g') && restag=${tag:=$branch}
echo $VERSION
if [[ "$VERSION" == *java ]]
then
    echo "java"
    mvn install
else
    echo "droid"
    cp ./grpc/cubic.proto ./grpc/android-cubic/src/main/proto/cubic.proto
    cd ./grpc/android-cubic && chmod +x ./gradlew && ./gradlew clean install -xtest
fi
