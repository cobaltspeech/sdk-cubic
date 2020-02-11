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

### Android
Building for Android requires more steps, so it is described on the [Android Integrations](https://cobaltspeech.github.io/sdk-cubic/using-cubic-sdk/android/) page.
