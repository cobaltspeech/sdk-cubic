// package: cobaltspeech.cubic
// file: cubic.proto

var cubic_pb = require("./cubic_pb");
var google_protobuf_empty_pb = require("google-protobuf/google/protobuf/empty_pb");
var grpc = require("@improbable-eng/grpc-web").grpc;

var Cubic = (function () {
  function Cubic() {}
  Cubic.serviceName = "cobaltspeech.cubic.Cubic";
  return Cubic;
}());

Cubic.Version = {
  methodName: "Version",
  service: Cubic,
  requestStream: false,
  responseStream: false,
  requestType: google_protobuf_empty_pb.Empty,
  responseType: cubic_pb.VersionResponse
};

Cubic.ListModels = {
  methodName: "ListModels",
  service: Cubic,
  requestStream: false,
  responseStream: false,
  requestType: cubic_pb.ListModelsRequest,
  responseType: cubic_pb.ListModelsResponse
};

Cubic.Recognize = {
  methodName: "Recognize",
  service: Cubic,
  requestStream: false,
  responseStream: false,
  requestType: cubic_pb.RecognizeRequest,
  responseType: cubic_pb.RecognitionResponse
};

Cubic.StreamingRecognize = {
  methodName: "StreamingRecognize",
  service: Cubic,
  requestStream: true,
  responseStream: true,
  requestType: cubic_pb.StreamingRecognizeRequest,
  responseType: cubic_pb.RecognitionResponse
};

Cubic.CompileContext = {
  methodName: "CompileContext",
  service: Cubic,
  requestStream: false,
  responseStream: false,
  requestType: cubic_pb.CompileContextRequest,
  responseType: cubic_pb.CompileContextResponse
};

exports.Cubic = Cubic;

function CubicClient(serviceHost, options) {
  this.serviceHost = serviceHost;
  this.options = options || {};
}

CubicClient.prototype.version = function version(requestMessage, metadata, callback) {
  if (arguments.length === 2) {
    callback = arguments[1];
  }
  var client = grpc.unary(Cubic.Version, {
    request: requestMessage,
    host: this.serviceHost,
    metadata: metadata,
    transport: this.options.transport,
    debug: this.options.debug,
    onEnd: function (response) {
      if (callback) {
        if (response.status !== grpc.Code.OK) {
          var err = new Error(response.statusMessage);
          err.code = response.status;
          err.metadata = response.trailers;
          callback(err, null);
        } else {
          callback(null, response.message);
        }
      }
    }
  });
  return {
    cancel: function () {
      callback = null;
      client.close();
    }
  };
};

CubicClient.prototype.listModels = function listModels(requestMessage, metadata, callback) {
  if (arguments.length === 2) {
    callback = arguments[1];
  }
  var client = grpc.unary(Cubic.ListModels, {
    request: requestMessage,
    host: this.serviceHost,
    metadata: metadata,
    transport: this.options.transport,
    debug: this.options.debug,
    onEnd: function (response) {
      if (callback) {
        if (response.status !== grpc.Code.OK) {
          var err = new Error(response.statusMessage);
          err.code = response.status;
          err.metadata = response.trailers;
          callback(err, null);
        } else {
          callback(null, response.message);
        }
      }
    }
  });
  return {
    cancel: function () {
      callback = null;
      client.close();
    }
  };
};

CubicClient.prototype.recognize = function recognize(requestMessage, metadata, callback) {
  if (arguments.length === 2) {
    callback = arguments[1];
  }
  var client = grpc.unary(Cubic.Recognize, {
    request: requestMessage,
    host: this.serviceHost,
    metadata: metadata,
    transport: this.options.transport,
    debug: this.options.debug,
    onEnd: function (response) {
      if (callback) {
        if (response.status !== grpc.Code.OK) {
          var err = new Error(response.statusMessage);
          err.code = response.status;
          err.metadata = response.trailers;
          callback(err, null);
        } else {
          callback(null, response.message);
        }
      }
    }
  });
  return {
    cancel: function () {
      callback = null;
      client.close();
    }
  };
};

CubicClient.prototype.streamingRecognize = function streamingRecognize(metadata) {
  var listeners = {
    data: [],
    end: [],
    status: []
  };
  var client = grpc.client(Cubic.StreamingRecognize, {
    host: this.serviceHost,
    metadata: metadata,
    transport: this.options.transport
  });
  client.onEnd(function (status, statusMessage, trailers) {
    listeners.status.forEach(function (handler) {
      handler({ code: status, details: statusMessage, metadata: trailers });
    });
    listeners.end.forEach(function (handler) {
      handler({ code: status, details: statusMessage, metadata: trailers });
    });
    listeners = null;
  });
  client.onMessage(function (message) {
    listeners.data.forEach(function (handler) {
      handler(message);
    })
  });
  client.start(metadata);
  return {
    on: function (type, handler) {
      listeners[type].push(handler);
      return this;
    },
    write: function (requestMessage) {
      client.send(requestMessage);
      return this;
    },
    end: function () {
      client.finishSend();
    },
    cancel: function () {
      listeners = null;
      client.close();
    }
  };
};

CubicClient.prototype.compileContext = function compileContext(requestMessage, metadata, callback) {
  if (arguments.length === 2) {
    callback = arguments[1];
  }
  var client = grpc.unary(Cubic.CompileContext, {
    request: requestMessage,
    host: this.serviceHost,
    metadata: metadata,
    transport: this.options.transport,
    debug: this.options.debug,
    onEnd: function (response) {
      if (callback) {
        if (response.status !== grpc.Code.OK) {
          var err = new Error(response.statusMessage);
          err.code = response.status;
          err.metadata = response.trailers;
          callback(err, null);
        } else {
          callback(null, response.message);
        }
      }
    }
  });
  return {
    cancel: function () {
      callback = null;
      client.close();
    }
  };
};

exports.CubicClient = CubicClient;

