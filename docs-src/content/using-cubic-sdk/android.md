---
title: "Android Integrations"
weight: 25
---

## Adding the `protobuf-gradle-plugin` to your Android project

In your root `build.gradle` file, you will need to add a new protobuf-gradle-plugin dependancy

``` groovy
buildscript {
    // ...
    dependencies {
        // ...
        classpath "com.google.protobuf:protobuf-gradle-plugin:0.8.10"
        // ...
    }
}
```

This will allow you to have the gradle build script generate the protobuf code.  

## Generating code from protobuf files

Next, you will have to add the code to actually generate the files.
You will need to modifiy your `app/build.gradle` file with the following.

``` groovy
apply plugin: 'com.android.application' // Should already exist
apply plugin: 'com.google.protobuf' // Add this line

android { /*...*/}

// This section adds a step to generate the gRPC code from `app/src/main/proto` proto files.
// The generated code should magically be available to your java/kotlin code.
protobuf {
    protoc {
        artifact = 'com.google.protobuf:protoc:3.10.0'
    }
    plugins {
        javalite {
            artifact = "com.google.protobuf:protoc-gen-javalite:3.0.0"
        }
        grpc {
            artifact = 'io.grpc:protoc-gen-grpc-java:1.24.0'
        }
    }
    generateProtoTasks {
        all().each { task ->
            task.plugins {
                javalite {}
                grpc {
                    // Options added to --grpc_out
                    option 'lite' //gRPC suggest we use lite in android applications
                }
            }
        }
    }
}

// Then we need to add a few runtime dependancies
dependencies {
    // Existing dependencies ...

    // Libraries needed for grpc stuff.
    // Note: changing the versions may require different libraries.
    implementation 'io.grpc:grpc-okhttp:1.24.0'
    implementation 'io.grpc:grpc-protobuf-lite:1.24.0' // gRPC suggests we use lite in android applications
    implementation 'io.grpc:grpc-stub:1.24.0'
    implementation 'io.grpc:grpc-auth:1.24.0'
    implementation 'javax.annotation:javax.annotation-api:1.2'
}

```

Please note that this assumes all protofiles are availabe at `app/src/main/proto`.
If you want to have the files somewhere else, you can add a line such as this.

``` groovy

dependencies {
    // ...
    protobuf files("lib/protos.tar.gz")
    protobuf files("/path/to/other/folder/to/include/")
}

```

## Downloading the latest cubic.proto file

You can find our proto file at `https://github.com/cobaltspeech/sdk-cubic/blob/master/grpc/cubic.proto`.
Copy this file to `app/src/main/proto/cubic.proto`.

cubic.proto also relies on a few other proto files:

| Name | URL |
| ---- | --- |
| cubic.proto | https://github.com/cobaltspeech/sdk-cubic/blob/master/grpc/cubic.proto |
| google/api/annotations.proto | https://github.com/googleapis/googleapis/blob/master/google/api/annotations.proto |
| google/api/http.proto | https://github.com/googleapis/googleapis/blob/master/google/api/http.proto |
| google/protobuf/descriptor.proto | https://github.com/protocolbuffers/protobuf/blob/master/src/google/protobuf/descriptor.proto |
| google/protobuf/duration.proto | https://github.com/protocolbuffers/protobuf/blob/master/src/google/protobuf/duration.proto |

Once you have all of these files downloaded, your file structure would look like this:

``` txt
app/src/main/proto/
├── cubic.proto
└── google
    ├── api
    │   ├── annotations.proto
    │   └── http.proto
    └── protobuf
        ├── descriptor.proto
        └── duration.proto
```

At this point, you should be able to do a `Build>Clean Build` and `Build>Rebuild Project`.

## Contact us

We know it takes work to get environments setup.  If you have any problems, don't hesitate to contact us.
