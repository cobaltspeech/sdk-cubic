#!/bin/bash
#tag=$(git tag --contains) && branch=$(git branch | sed -n -e 's/^\* \(.*\)/\1/p' | sed 's#/#~#g') && restag=${tag:=$branch}
for restag in $(git tag) 
do
    if [[ "$restag" == *java ]]
    then
        mvn install
    else
        cp grpc/cubic.proto grpc/android-cubic/src/main/proto/cubic.proto
        ./grpc/android-cubic && chmod +x gradlew && ./gradlew clean install -xtest
    fi
done
