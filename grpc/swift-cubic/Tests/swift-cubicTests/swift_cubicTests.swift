import XCTest
@testable import swift_cubic

final class swift_cubicTests: XCTestCase {
    func testExample() {
        // This is an example of a functional test case.
        // Use XCTAssert and related functions to verify your tests produce the correct
        // results.
        XCTAssertEqual(swift_cubic().text, "Hello, World!")
    }

    static var allTests = [
        ("testExample", testExample),
    ]
}
