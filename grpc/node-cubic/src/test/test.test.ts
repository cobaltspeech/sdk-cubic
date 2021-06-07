import { CubicClientImpl, GrpcWebImpl } from './../grpc/cubic';
var cl=new GrpcWebImpl('https://demo.cobaltspeech.com:2727/cubic/',{})
var l=new CubicClientImpl(cl)

l.ListModels({}, null as any).then(a => {
    console.log(a);
});
