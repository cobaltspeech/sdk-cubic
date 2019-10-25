---
title: "Android Integrations"
weight: 45
---

## Adding the `protobuf-gradle-plugin` to your Android project

In your root `build.gradle` file, add a new protobuf-gradle-plugin dependency

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

This will allow the app's gradle build script to generate the protobuf code.  

## Generating code from protobuf files

Next, you will have to add the code to actually generate the files.
To generate the gRPC code, modify your `app/build.gradle` file with the following:

``` groovy
apply plugin: 'com.android.application' // Should already exist
apply plugin: 'com.google.protobuf' // Add this line

android { /*...*/}

// This section adds a step to generate the gRPC code from `app/src/main/proto` proto files.
// This section adds a step to generate the gRPC code from `app/src/main/proto` proto files
// and make it available to your java/kotlin code.
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
                    option 'lite' // the gRPC documentation suggests using lite in android applications
                }
            }
        }
    }
}

// Runtime dependencies
dependencies {
    // Existing dependencies ...

    // gRPC Libraries
    implementation 'io.grpc:grpc-okhttp:1.24.0'
    implementation 'io.grpc:grpc-protobuf-lite:1.24.0' // the gRPC documentation suggests using lite in android applications
    implementation 'io.grpc:grpc-stub:1.24.0'
    implementation 'io.grpc:grpc-auth:1.24.0'
    implementation 'javax.annotation:javax.annotation-api:1.2'
}

```

By default,  generateProtoTasks assumes all protofiles are availabe at `app/src/main/proto`.
To include proto files somewhere else, add lines such as this:

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
| google/api/annotations.proto | https://github.com/googleapis/googleapis/blob/6ae2d42/google/api/annotations.proto |
| google/api/http.proto | https://github.com/googleapis/googleapis/blob/6ae2d42/google/api/http.proto |
| google/protobuf/descriptor.proto | https://github.com/protocolbuffers/protobuf/blob/044c766/src/google/protobuf/descriptor.proto |
| google/protobuf/duration.proto | https://github.com/protocolbuffers/protobuf/blob/044c766/src/google/protobuf/duration.proto |

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

We know it takes work to get environments set up.  If you have any problems, don't hesitate to contact us.
