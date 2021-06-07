"use strict";
exports.__esModule = true;
var cubic_1 = require("./../grpc/cubic");
var cl = new cubic_1.GrpcWebImpl('https://demo.cobaltspeech.com:2727/cubic/', {});
var l = new cubic_1.CubicClientImpl(cl);
l.ListModels({}, null).then(function (a) {
    console.log(a);
});
