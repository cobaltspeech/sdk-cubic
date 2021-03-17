---
title: "Installing the SDK"
weight: 21
---

Instructions for installing the SDK are language specific.

<!--more-->

### Go
The Go SDK supports Go modules and requires Go 1.12 or later. To use the SDK,
import this package into your application:

``` go
import "github.com/cobaltspeech/sdk-cubic/grpc/go-cubic"
```

### Python
The Python SDK depends on Python >= 3.5. You may use pip to perform a system-wide install, or use virtualenv for a local install.

``` bash
pip install --upgrade pip
pip install "git+https://github.com/cobaltspeech/sdk-cubic#egg=cobalt-cubic&subdirectory=grpc/py-cubic"
```

### C# 

The C# SDK utilizes the [NuGet package manager](https://www.nuget.org).  The package is called `Cubic-SDK`, under the owners name of `CobaltSpeech`.

NuGet allows 4 different ways to install.  Further instructions can be found on the [nuget webpage](https://www.nuget.org/packages/Cubic-SDK/).  Installing via the `dotnet` cli through the command:

``` bash
dotnet add package Cubic-SDK
```
### Java
#### Maven 
Add jitpack to repository list:
``` bash
<repositories>
    <repository>
      <id>jitpack.io</id>
      <url>https://jitpack.io</url>
    </repository> 
    ...
  </repositories>
```
Add dependency:
``` bash
<dependencies>
    ...
    <dependency>
        <groupId>com.github.cobaltspeech</groupId>
        <artifactId>sdk-cubic</artifactId>
        <version>v1.6.0-java</version>
    </dependency>
</dependencies>
```
#### Android
##### From Souce code
Building for Android requires more steps, so it is described on the [Android Integrations](../android/) page.

### iOS

#### Swift Package Manager

The [Swift Package Manager](https://swift.org/package-manager/) is a tool for automating the distribution of Swift code and is integrated into the `swift` compiler.

Once you have your Swift package set up, adding swift-cubic as a dependency is as easy as adding it to the `dependencies` value of your `Package.swift`.

```swift
dependencies: [
    .package(url: "git@github.com:cobaltspeech/sdk-cubic.git", .upToNextMajor(from: "1.6.1"))
]
```
