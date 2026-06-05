import XCTest
import UIKit
@testable import PrivacyScreenPlugin

class PrivacyScreenPluginTests: XCTestCase {
    func testStartDoesNotEnableByDefault() {
        let implementation = PrivacyScreen()
        implementation.start {
            nil
        }

        XCTAssertFalse(implementation.isEnabled)
    }

    func testEnableDisableState() {
        let implementation = PrivacyScreen()
        implementation.setEnabled(true)
        XCTAssertTrue(implementation.isEnabled)

        implementation.setEnabled(false)
        XCTAssertFalse(implementation.isEnabled)
    }

    func testEnableDoesNotRequestWindowForLiveCapture() {
        let implementation = PrivacyScreen()
        var requestedWindow = false
        implementation.setApplicationStateProvider {
            .active
        }
        implementation.start {
            requestedWindow = true
            return nil
        }

        implementation.setEnabled(true)

        XCTAssertFalse(requestedWindow)
    }

    func testEnableWhileInactiveRequestsWindowForOverlay() {
        let implementation = PrivacyScreen()
        var requestedWindow = false
        implementation.setApplicationStateProvider {
            .inactive
        }
        implementation.start {
            requestedWindow = true
            return nil
        }

        implementation.setEnabled(true)

        XCTAssertTrue(requestedWindow)
    }
}
