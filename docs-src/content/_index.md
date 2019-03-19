---
title: "Cubic SDK Documentation"
---

# Cubic SDK

Cubic is Cobalt's speech recognition engine.  It can be bundled into your
application as a library, or deployed on-prem and accessed over the network.
This documentation refers to accessing the network-based Cubic server.

Cobalt will provide you with a package of Cubic that contains the engine,
appropriate speech recognition models and a server application.  This server
exports Cubic's functionality over the gRPC protocol.  The
https://github.com/cobaltspeech/sdk-cubic repository contains the SDK that you
can use in your application to communicate with the Cubic server. This SDK is
currently available for the Go language; and we would be happy to talk to you if
you need support for other languages. Most of the core SDK is generated
automatically using the gRPC tools, and Cobalt provides a top level package for
more convenient API calls.
