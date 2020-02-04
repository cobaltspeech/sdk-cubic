// swift-tools-version:5.1
// The swift-tools-version declares the minimum version of Swift required to build this package.

import PackageDescription

let package = Package(
    name: "swift-cubic",
    products: [
        // Products define the executables and libraries produced by a package, and make them visible to other packages.
        .library(
            name: "swift-cubic",
            targets: ["swift-cubic"]),
    ],
    dependencies: [
          .package(url: "https://github.com/grpc/grpc-swift.git", from: "1.0.0-alpha.9")
        // Dependencies declare other packages that this package depends on.
        // .package(url: /* package url */, from: "1.0.0"),
    ],
    targets: [
        // Targets are the basic building blocks of a package. A target can define a module or a test suite.
        // Targets can depend on other targets in this package, and on products in packages which this package depends on.
        .target(
            name: "swift-cubic",
            dependencies: ["GRPC"]),
        .testTarget(
            name: "swift-cubicTests",
            dependencies: ["swift-cubic"]),
    ]
)
