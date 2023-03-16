package webgrpc

import (
	"net/http"
	"strings"

	"github.com/improbable-eng/grpc-web/go/grpcweb"
	"google.golang.org/grpc"
)

func Handler(server *grpc.Server) http.HandlerFunc {
	grpcwebServer := grpcweb.WrapServer(server, grpcweb.WithWebsockets(true))
	return func(rw http.ResponseWriter, r *http.Request) {
		r.URL.Path = r.URL.Path[strings.Index(r.URL.Path, "cobaltspeech.cubic.Cubic"):]
		if grpcwebServer.IsGrpcWebRequest(r) {
			grpcwebServer.ServeHTTP(rw, r)
			return
		}
		if grpcwebServer.IsGrpcWebSocketRequest(r) {
			grpcwebServer.HandleGrpcWebsocketRequest(rw, r)
			return
		}
		rw.WriteHeader(http.StatusNotFound)
	}
}
