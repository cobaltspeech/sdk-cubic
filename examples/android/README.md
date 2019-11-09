For convenience, this example app directory includes a copy of the protobuf dependencies on which cubic relies. You can pull different versions of the files from github as needed:

| Name | Version included |
| ---- | ---------------- |
| google/api/annotations.proto | 1.3.1 |
| google/api/http.proto |  1.3.1 |
| google/protobuf/descriptor.proto | 3.10.1 |
| google/protobuf/duration.proto | 3.10.1 |


This example app also contains a symlink to cubic.proto.  If you build on Windows and the symlink does not resolve correctly, manually copy the file from <your_project_root>/grpc/cubic.proto.


