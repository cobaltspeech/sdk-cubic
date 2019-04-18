---
title: "Installation"
weight: 1
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
pip install git+https://github.com/cobaltspeech/sdk-cubic/grpc/py-cubic#egg=cobalt-cubic
```
